package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.response.ProductResponse;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private ProductServiceImp productService;

    @Test
    void createProduct_createsProductWithCalculatedAndAuditFields() {
        ProductDTO input = mock(ProductDTO.class);
        ProductDTO result = mock(ProductDTO.class);
        Product product = mock(Product.class);
        Product savedProduct = mock(Product.class);
        Category category = mock(Category.class);
        User seller = mock(User.class);

        when(modelMapper.map(input, Product.class)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(product.getProductName()).thenReturn("Phone");
        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        when(product.getDiscount()).thenReturn(new BigDecimal("10"));
        when(productRepository.existsByProductName("Phone")).thenReturn(false);
        when(authUtil.loggedInUser()).thenReturn(seller);
        when(seller.getUserName()).thenReturn("seller");
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(modelMapper.map(savedProduct, ProductDTO.class)).thenReturn(result);

        ProductDTO actual = productService.createProduct(input, 10L);

        assertSame(result, actual);
        verify(product).setCategory(category);
        verify(product).setSpecialPrice(new BigDecimal("90.0000"));
        verify(product).setImage("default.png");
        verify(product).setStatus(SystemConstants.ACTIVE);
        verify(product).setSeller(seller);
        verify(product).setCreatedBy("seller");
        verify(product).setLastUpdatedBy("seller");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_throwsWhenCategoryDoesNotExist() {
        ProductDTO input = mock(ProductDTO.class);
        Product product = mock(Product.class);

        when(modelMapper.map(input, Product.class)).thenReturn(product);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.createProduct(input, 99L));

        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_throwsWhenProductNameAlreadyExists() {
        ProductDTO input = mock(ProductDTO.class);
        Product product = mock(Product.class);
        Category category = mock(Category.class);

        when(modelMapper.map(input, Product.class)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(product.getProductName()).thenReturn("Phone");
        when(productRepository.existsByProductName("Phone")).thenReturn(true);

        assertThrows(EcommerceException.class,
                () -> productService.createProduct(input, 1L));

        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts_returnsMappedPageResponse() {
        Product product = mock(Product.class);
        ProductDTO dto = mock(ProductDTO.class);
        Page<Product> page = new PageImpl<>(List.of(product), Pageable.ofSize(5), 1);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);

        ProductResponse response =
                productService.getAllProducts(0, 5, "productName", "asc");

        assertEquals(0, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());
        assertEquals(List.of(dto), response.getContent());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue()
                .getSort()
                .getOrderFor("productName");

        assertNotNull(order);
        assertTrue(order.isAscending());
    }

    @Test
    void getAllProducts_throwsWhenPageIsEmpty() {
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(EcommerceException.class,
                () -> productService.getAllProducts(0, 5, "productName", "desc"));
    }

    @Test
    void deleteProduct_marksProductInactiveAndRemovesItFromCarts() {
        Long productId = 7L;
        Product product = mock(Product.class);
        Product savedProduct = mock(Product.class);
        ProductDTO result = mock(ProductDTO.class);
        User owner = mock(User.class);
        User loggedInUser = mock(User.class);
        Cart cart = mock(Cart.class);

        when(productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(product.getSeller()).thenReturn(owner);
        when(owner.getUserId()).thenReturn(3L);
        when(loggedInUser.getUserId()).thenReturn(3L);
        when(loggedInUser.getUserName()).thenReturn("seller");
        when(authUtil.loggedInUser()).thenReturn(loggedInUser);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(savedProduct.getProductId()).thenReturn(productId);
        when(cartRepository.findByProductId(productId)).thenReturn(List.of(cart));
        when(cart.getCartId()).thenReturn(12L);
        when(modelMapper.map(savedProduct, ProductDTO.class)).thenReturn(result);

        ProductDTO actual = productService.deleteProduct(productId);

        assertSame(result, actual);
        verify(product).setStatus(SystemConstants.INACTIVE);
        verify(product).setLastUpdatedBy("seller");
        verify(cartService).deleteProductFromCart(12L, productId);
    }

    @Test
    void deleteProduct_throwsWhenProductDoesNotExist() {
        when(productRepository.findByProductIdAndStatus(1L, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(1L));
    }

    @Test
    void deleteProduct_throwsWhenLoggedInUserIsNotSeller() {
        Product product = mock(Product.class);
        User owner = mock(User.class);
        User otherUser = mock(User.class);

        when(productRepository.findByProductIdAndStatus(1L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(product.getSeller()).thenReturn(owner);
        when(owner.getUserId()).thenReturn(1L);
        when(otherUser.getUserId()).thenReturn(2L);
        when(authUtil.loggedInUser()).thenReturn(otherUser);

        assertThrows(EcommerceException.class,
                () -> productService.deleteProduct(1L));

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_updatesOwnedProductAndCartItems() {
        Long productId = 5L;
        ProductDTO input = mock(ProductDTO.class);
        ProductDTO result = mock(ProductDTO.class);
        Product changes = mock(Product.class);
        Product existing = mock(Product.class);
        Product saved = mock(Product.class);
        User seller = mock(User.class);
        Cart cart = mock(Cart.class);

        when(modelMapper.map(input, Product.class)).thenReturn(changes);
        when(productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(existing.getSeller()).thenReturn(seller);
        when(seller.getUserId()).thenReturn(4L);
        when(authUtil.loggedInUser()).thenReturn(seller);
        when(seller.getUserId()).thenReturn(4L);
        when(seller.getUserName()).thenReturn("seller");

        when(changes.getProductName()).thenReturn("Updated");
        when(changes.getDescription()).thenReturn("Description");
        when(changes.getPrice()).thenReturn(new BigDecimal("200.00"));
        when(changes.getDiscount()).thenReturn(new BigDecimal("5"));
        when(changes.getQuantity()).thenReturn(8L);

        when(productRepository.save(existing)).thenReturn(saved);
        when(saved.getProductId()).thenReturn(productId);
        when(cartRepository.findByProductId(productId)).thenReturn(List.of(cart));
        when(cart.getCartId()).thenReturn(20L);
        when(modelMapper.map(saved, ProductDTO.class)).thenReturn(result);

        ProductDTO actual = productService.updateProduct(input, productId);

        assertSame(result, actual);
        verify(existing).setProductName("Updated");
        verify(existing).setDescription("Description");
        verify(existing).setPrice(new BigDecimal("200.00"));
        verify(existing).setDiscount(new BigDecimal("5"));
        verify(existing).setSpecialPrice(new BigDecimal("190.0000"));
        verify(existing).setQuantity(8L);
        verify(cartService).updateProductInCarts(20L, productId);
    }

    @Test
    void updateProduct_throwsWhenProductDoesNotExist() {
        ProductDTO input = mock(ProductDTO.class);
        when(modelMapper.map(input, Product.class)).thenReturn(mock(Product.class));
        when(productRepository.findByProductIdAndStatus(1L, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(input, 1L));
    }

    @Test
    void updateProduct_throwsWhenUserIsNotSeller() {
        ProductDTO input = mock(ProductDTO.class);
        Product changes = mock(Product.class);
        Product existing = mock(Product.class);
        User owner = mock(User.class);
        User other = mock(User.class);

        when(modelMapper.map(input, Product.class)).thenReturn(changes);
        when(productRepository.findByProductIdAndStatus(1L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(existing.getSeller()).thenReturn(owner);
        when(owner.getUserId()).thenReturn(1L);
        when(other.getUserId()).thenReturn(2L);
        when(authUtil.loggedInUser()).thenReturn(other);

        assertThrows(EcommerceException.class,
                () -> productService.updateProduct(input, 1L));
    }

    @Test
    void getProductById_returnsMappedProduct() {
        Product product = mock(Product.class);
        ProductDTO dto = mock(ProductDTO.class);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);

        assertSame(dto, productService.getProductById(1L));
    }

    @Test
    void getProductById_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    void getAllActiveProducts_returnsResponse() {
        Product product = mock(Product.class);
        ProductDTO dto = mock(ProductDTO.class);
        Page<Product> page = new PageImpl<>(List.of(product), Pageable.ofSize(10), 1);

        when(productRepository.findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);

        ProductResponse response =
                productService.getAllActiveProducts(0, 10, "price", "DESC");

        assertEquals(List.of(dto), response.getContent());
        verify(productRepository).findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class));
    }

    @Test
    void getAllActiveProducts_throwsWhenNoProductsExist() {
        when(productRepository.findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(EcommerceException.class,
                () -> productService.getAllActiveProducts(0, 10, "price", "asc"));
    }

    @Test
    void getAllProductsByCategory_returnsResponse() {
        Category category = mock(Category.class);
        Product product = mock(Product.class);
        ProductDTO dto = mock(ProductDTO.class);
        Page<Product> page = new PageImpl<>(List.of(product), Pageable.ofSize(10), 1);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productRepository.findAllByCategory(eq(category), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);

        ProductResponse response =
                productService.getAllProductsByCategory(2L, 0, 10, "price", "asc");

        assertEquals(List.of(dto), response.getContent());
        verify(productRepository).findAllByCategory(eq(category), any(Pageable.class));
    }

    @Test
    void getAllProductsByCategory_throwsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getAllProductsByCategory(2L, 0, 10, "price", "asc"));
    }

    @Test
    void searchProductByKeyword_returnsResponseWithWildcardKeyword() {
        Product product = mock(Product.class);
        ProductDTO dto = mock(ProductDTO.class);
        Page<Product> page = new PageImpl<>(List.of(product), Pageable.ofSize(10), 1);

        when(productRepository.findAllByProductNameLikeIgnoreCase(eq("%phone%"), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(dto);

        ProductResponse response =
                productService.searchProductByKeyword("phone", 0, 10, "productName", "asc");

        assertEquals(List.of(dto), response.getContent());
        verify(productRepository).findAllByProductNameLikeIgnoreCase(
                eq("%phone%"), any(Pageable.class));
    }

    @Test
    void searchProductByKeyword_throwsWhenNoProductsMatch() {
        when(productRepository.findAllByProductNameLikeIgnoreCase(
                eq("%unknown%"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(EcommerceException.class,
                () -> productService.searchProductByKeyword(
                        "unknown", 0, 10, "productName", "desc"));
    }
}
