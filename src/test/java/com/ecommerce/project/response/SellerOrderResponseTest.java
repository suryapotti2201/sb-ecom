package com.ecommerce.project.response;

import com.ecommerce.project.payload.SellerOrderDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SellerOrderResponseTest {

    @Test
    void noArgsConstructorShouldInitializeFieldsToDefaults() {
        SellerOrderResponse response = new SellerOrderResponse();

        assertAll(
                () -> assertNull(response.getContent()),
                () -> assertNull(response.getPageNumber()),
                () -> assertNull(response.getPageSize()),
                () -> assertNull(response.getTotalElements()),
                () -> assertNull(response.getTotalPages()),
                () -> assertFalse(response.isLastPage())
        );
    }

    @Test
    void allArgsConstructorShouldInitializeAllFields() {
        SellerOrderDTO order = mock(SellerOrderDTO.class);
        List<SellerOrderDTO> content = List.of(order);

        SellerOrderResponse response = new SellerOrderResponse(
                content,
                1,
                10,
                25L,
                3,
                true
        );

        assertAll(
                () -> assertSame(content, response.getContent()),
                () -> assertEquals(1, response.getPageNumber()),
                () -> assertEquals(10, response.getPageSize()),
                () -> assertEquals(25L, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldUpdateAllFields() {
        SellerOrderDTO order = mock(SellerOrderDTO.class);
        List<SellerOrderDTO> content = List.of(order);
        SellerOrderResponse response = new SellerOrderResponse();

        response.setContent(content);
        response.setPageNumber(2);
        response.setPageSize(20);
        response.setTotalElements(41L);
        response.setTotalPages(3);
        response.setLastPage(true);

        assertAll(
                () -> assertSame(content, response.getContent()),
                () -> assertEquals(2, response.getPageNumber()),
                () -> assertEquals(20, response.getPageSize()),
                () -> assertEquals(41L, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldAllowNullValuesAndFalseLastPage() {
        SellerOrderResponse response = new SellerOrderResponse(
                List.of(mock(SellerOrderDTO.class)),
                1,
                10,
                5L,
                1,
                true
        );

        response.setContent(null);
        response.setPageNumber(null);
        response.setPageSize(null);
        response.setTotalElements(null);
        response.setTotalPages(null);
        response.setLastPage(false);

        assertAll(
                () -> assertNull(response.getContent()),
                () -> assertNull(response.getPageNumber()),
                () -> assertNull(response.getPageSize()),
                () -> assertNull(response.getTotalElements()),
                () -> assertNull(response.getTotalPages()),
                () -> assertFalse(response.isLastPage())
        );
    }

    @Test
    void equalResponsesShouldBeEqualAndHaveSameHashCode() {
        SellerOrderDTO order = mock(SellerOrderDTO.class);
        List<SellerOrderDTO> content = List.of(order);

        SellerOrderResponse first = new SellerOrderResponse(
                content, 0, 10, 1L, 1, true
        );
        SellerOrderResponse second = new SellerOrderResponse(
                content, 0, 10, 1L, 1, true
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void responsesWithDifferentValuesShouldNotBeEqual() {
        SellerOrderResponse first = new SellerOrderResponse(
                List.of(mock(SellerOrderDTO.class)), 0, 10, 1L, 1, true
        );
        SellerOrderResponse second = new SellerOrderResponse(
                List.of(mock(SellerOrderDTO.class)), 0, 10, 1L, 1, false
        );

        assertNotEquals(first, second);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a response");
    }

    @Test
    void toStringShouldContainResponseState() {
        SellerOrderResponse response = new SellerOrderResponse(
                null, 2, 25, 50L, 2, true
        );

        String representation = response.toString();

        assertAll(
                () -> assertTrue(representation.contains("SellerOrderResponse")),
                () -> assertTrue(representation.contains("pageNumber=2")),
                () -> assertTrue(representation.contains("pageSize=25")),
                () -> assertTrue(representation.contains("totalElements=50")),
                () -> assertTrue(representation.contains("totalPages=2")),
                () -> assertTrue(representation.contains("lastPage=true"))
        );
    }
}
