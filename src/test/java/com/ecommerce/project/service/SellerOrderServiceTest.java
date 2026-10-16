package com.ecommerce.project.service;

import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.SellerOrder;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.SellerOrderDTO;
import com.ecommerce.project.repository.SellerOrderRepository;
import com.ecommerce.project.response.SellerOrderResponse;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerOrderServiceImplTest {

    @Mock
    private SellerOrderRepository sellerOrderRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SellerOrderServiceImpl sellerOrderService;

    @Captor
    private ArgumentCaptor<Iterable<SellerOrder>> sellerOrdersCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    void createSellerOrdersForCheckoutCreatesOneSellerOrderPerDistinctSeller() {
        Order order = new Order();

        User firstSeller = new User();
        firstSeller.setUserId(10L);
        firstSeller.setUserName("seller-one");

        User secondSeller = new User();
        secondSeller.setUserId(20L);
        secondSeller.setUserName("seller-two");

        OrderItem firstItem = orderItemForSeller(firstSeller);
        OrderItem secondItem = orderItemForSeller(secondSeller);
        OrderItem duplicateSellerItem = orderItemForSeller(firstSeller);

        sellerOrderService.createSellerOrdersForCheckout(
                order,
                List.of(firstItem, secondItem, duplicateSellerItem)
        );

        verify(sellerOrderRepository).saveAll(sellerOrdersCaptor.capture());

        List<SellerOrder> createdSellerOrders =
                toList(sellerOrdersCaptor.getValue());

        assertEquals(2, createdSellerOrders.size());

        assertEquals(
                1L,
                createdSellerOrders.stream()
                        .filter(item -> item.getSeller() == firstSeller)
                        .count()
        );

        assertEquals(
                1L,
                createdSellerOrders.stream()
                        .filter(item -> item.getSeller() == secondSeller)
                        .count()
        );

        for (SellerOrder sellerOrder : createdSellerOrders) {
            assertSame(order, sellerOrder.getOrder());
            assertEquals(OrderStatus.PLACED, sellerOrder.getOrderStatus());
            assertNotNull(sellerOrder.getCreatedOn());
            assertNotNull(sellerOrder.getLastUpdatedOn());
        }
    }

    @Test
    void createSellerOrdersForCheckoutSavesNoSellerOrdersForEmptyItems() {
        sellerOrderService.createSellerOrdersForCheckout(new Order(), List.of());

        verify(sellerOrderRepository).saveAll(sellerOrdersCaptor.capture());
        assertTrue(toList(sellerOrdersCaptor.getValue()).isEmpty());
    }

    @Test
    void updateOrderStatusUpdatesWhenTransitionIsValid() {
        Long sellerOrderId = 15L;
        Long sellerId = 27L;

        SellerOrderDTO request = mock(SellerOrderDTO.class);
        SellerOrder existingOrder = new SellerOrder();
        existingOrder.setOrderStatus(OrderStatus.PLACED);

        SellerOrder savedOrder = new SellerOrder();
        savedOrder.setOrderStatus(OrderStatus.PACKED);

        SellerOrderDTO mappedResponse = new SellerOrderDTO();
        User loggedInUser =  mock(User.class);

        when(request.getOrderStatus()).thenReturn(" PACKED ");
        when(request.getRemark()).thenReturn("Packed for dispatch");
        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(authUtil.loggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUserName()).thenReturn("seller");
        when(sellerOrderRepository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(Optional.of(existingOrder));
        when(sellerOrderRepository.save(existingOrder)).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, SellerOrderDTO.class)).thenReturn(mappedResponse);
        SellerOrderDTO result =
                sellerOrderService.updateOrderStatus(request, sellerOrderId);

        assertSame(mappedResponse, result);
        assertEquals(OrderStatus.PACKED, existingOrder.getOrderStatus());
        assertEquals("Packed for dispatch", existingOrder.getRemark());
        verify(sellerOrderRepository).save(existingOrder);
        verify(modelMapper).map(savedOrder, SellerOrderDTO.class);
    }

    @Test
    void updateOrderStatusReturnsMappedOrderWhenRequestedStatusIsAlreadyCurrent() {
        Long sellerOrderId = 15L;
        Long sellerId = 27L;

        SellerOrderDTO request = mock(SellerOrderDTO.class);
        SellerOrder existingOrder = new SellerOrder();
        existingOrder.setOrderStatus(OrderStatus.PACKED);

        SellerOrderDTO mappedResponse = new SellerOrderDTO();

        when(request.getOrderStatus()).thenReturn("PACKED");
        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(Optional.of(existingOrder));
        when(modelMapper.map(existingOrder, SellerOrderDTO.class)).thenReturn(mappedResponse);

        SellerOrderDTO result =
                sellerOrderService.updateOrderStatus(request, sellerOrderId);

        assertSame(mappedResponse, result);
        verify(modelMapper).map(existingOrder, SellerOrderDTO.class);
        verify(sellerOrderRepository, never()).save(any(SellerOrder.class));
    }

    @Test
    void updateOrderStatusRejectsUnsupportedRequestedStatus() {
        SellerOrderDTO request = mock(SellerOrderDTO.class);
        when(request.getOrderStatus()).thenReturn("CANCELLED");

        assertThrows(
                EcommerceException.class,
                () -> sellerOrderService.updateOrderStatus(request, 15L)
        );

        verify(sellerOrderRepository, never())
                .findBySellerOrderIdAndSellerId(any(Long.class), any(Long.class));
    }

    @Test
    void updateOrderStatusRejectsInvalidStatusTransition() {
        Long sellerOrderId = 15L;
        Long sellerId = 27L;

        SellerOrderDTO request = mock(SellerOrderDTO.class);
        SellerOrder existingOrder = new SellerOrder();
        existingOrder.setOrderStatus(OrderStatus.PLACED);

        when(request.getOrderStatus()).thenReturn("DELIVERED");
        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(Optional.of(existingOrder));

        assertThrows(
                EcommerceException.class,
                () -> sellerOrderService.updateOrderStatus(request, sellerOrderId)
        );

        assertEquals(OrderStatus.PLACED, existingOrder.getOrderStatus());
        verify(sellerOrderRepository, never()).save(any(SellerOrder.class));
    }

    @Test
    void updateOrderStatusThrowsWhenSellerOrderDoesNotExistForLoggedInSeller() {
        Long sellerOrderId = 15L;
        Long sellerId = 27L;

        SellerOrderDTO request = mock(SellerOrderDTO.class);
        when(request.getOrderStatus()).thenReturn("PACKED");
        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerOrderService.updateOrderStatus(request, sellerOrderId)
        );
    }

    @Test
    void getSellerOrdersReturnsMappedPaginatedResponseUsingAscendingSort() {
        Long sellerId = 27L;
        SellerOrder sellerOrder = new SellerOrder();
        SellerOrderDTO dto = new SellerOrderDTO();

        Pageable repositoryPageable = org.springframework.data.domain.PageRequest.of(
                1,
                5,
                Sort.by("createdOn").ascending()
        );
        Page<SellerOrder> page =
                new PageImpl<>(List.of(sellerOrder), repositoryPageable, 11);

        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findAllByUserId(eq(sellerId), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(sellerOrder, SellerOrderDTO.class)).thenReturn(dto);

        SellerOrderResponse response =
                sellerOrderService.getSellerOrders(1, 5, "createdOn", "ASC");

        verify(sellerOrderRepository).findAllByUserId(eq(sellerId), pageableCaptor.capture());

        Pageable requestedPageable = pageableCaptor.getValue();
        assertEquals(1, requestedPageable.getPageNumber());
        assertEquals(5, requestedPageable.getPageSize());
        assertEquals(
                Sort.Direction.ASC,
                requestedPageable.getSort().getOrderFor("createdOn").getDirection()
        );

        assertEquals(List.of(dto), response.getContent());
        assertEquals(1, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(11, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertFalse(response.isLastPage());
    }

    @Test
    void getSellerOrdersUsesDescendingSortWhenSortOrderIsNotAscending() {
        Long sellerId = 27L;
        Pageable pageRequest = org.springframework.data.domain.PageRequest.of(
                0,
                10,
                Sort.by("sellerOrderId").descending()
        );
        Page<SellerOrder> page = new PageImpl<>(List.of(), pageRequest, 0);

        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findAllByUserId(eq(sellerId), any(Pageable.class)))
                .thenReturn(page);

        sellerOrderService.getSellerOrders(0, 10, "sellerOrderId", "DESC");

        verify(sellerOrderRepository).findAllByUserId(eq(sellerId), pageableCaptor.capture());
        assertEquals(
                Sort.Direction.DESC,
                pageableCaptor.getValue()
                        .getSort()
                        .getOrderFor("sellerOrderId")
                        .getDirection()
        );
    }

    @Test
    void getSellerOrdersByStatusReturnsMappedResponseAndPassesStatusToRepository() {
        Long sellerId = 27L;
        SellerOrder sellerOrder = new SellerOrder();
        SellerOrderDTO dto = new SellerOrderDTO();

        Pageable pageRequest = org.springframework.data.domain.PageRequest.of(
                0,
                10,
                Sort.by("createdOn").descending()
        );
        Page<SellerOrder> page =
                new PageImpl<>(List.of(sellerOrder), pageRequest, 1);

        when(authUtil.loggedInUserId()).thenReturn(sellerId);
        when(sellerOrderRepository.findAllByUserIdAndStatus(
                eq(sellerId),
                eq(OrderStatus.PACKED),
                any(Pageable.class)
        )).thenReturn(page);
        when(modelMapper.map(sellerOrder, SellerOrderDTO.class)).thenReturn(dto);

        SellerOrderResponse response =
                sellerOrderService.getSellerOrdersByStatus(
                        "PACKED",
                        0,
                        10,
                        "createdOn",
                        "DESC"
                );

        verify(sellerOrderRepository).findAllByUserIdAndStatus(
                eq(sellerId),
                eq(OrderStatus.PACKED),
                pageableCaptor.capture()
        );

        assertEquals(
                Sort.Direction.DESC,
                pageableCaptor.getValue()
                        .getSort()
                        .getOrderFor("createdOn")
                        .getDirection()
        );
        assertEquals(List.of(dto), response.getContent());
        assertEquals(1, response.getTotalElements());
        assertTrue(response.isLastPage());
    }

    @Test
    void getSellerOrdersByStatusRejectsUnsupportedStatus() {
        assertThrows(
                EcommerceException.class,
                () -> sellerOrderService.getSellerOrdersByStatus(
                        "CANCELLED",
                        0,
                        10,
                        "createdOn",
                        "ASC"
                )
        );

        verify(sellerOrderRepository, never())
                .findAllByUserIdAndStatus(
                        any(Long.class),
                        any(OrderStatus.class),
                        any(Pageable.class)
                );
    }

    private OrderItem orderItemForSeller(User seller) {
        Product product = new Product();
        product.setSeller(seller);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        return orderItem;
    }

    private List<SellerOrder> toList(Iterable<SellerOrder> sellerOrders) {
        java.util.ArrayList<SellerOrder> result = new java.util.ArrayList<>();
        sellerOrders.forEach(result::add);
        return result;
    }
}
