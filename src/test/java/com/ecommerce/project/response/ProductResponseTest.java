package com.ecommerce.project.response;

import com.ecommerce.project.payload.ProductDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void noArgsConstructorShouldInitializeDefaultValues() {
        ProductResponse response = new ProductResponse();

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
        List<ProductDTO> content = new ArrayList<>();
        ProductResponse response = new ProductResponse(
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
        List<ProductDTO> content = new ArrayList<>();
        ProductResponse response = new ProductResponse();

        response.setContent(content);
        response.setPageNumber(2);
        response.setPageSize(20);
        response.setTotalElements(42L);
        response.setTotalPages(3);
        response.setLastPage(true);

        assertAll(
                () -> assertSame(content, response.getContent()),
                () -> assertEquals(2, response.getPageNumber()),
                () -> assertEquals(20, response.getPageSize()),
                () -> assertEquals(42L, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldAllowNullableReferenceFields() {
        ProductResponse response = new ProductResponse(
                List.of(),
                1,
                10,
                1L,
                1,
                true
        );

        response.setContent(null);
        response.setPageNumber(null);
        response.setPageSize(null);
        response.setTotalElements(null);
        response.setTotalPages(null);

        assertAll(
                () -> assertNull(response.getContent()),
                () -> assertNull(response.getPageNumber()),
                () -> assertNull(response.getPageSize()),
                () -> assertNull(response.getTotalElements()),
                () -> assertNull(response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void equalResponsesShouldBeEqualAndHaveSameHashCode() {
        List<ProductDTO> content = new ArrayList<>();

        ProductResponse first = new ProductResponse(content, 0, 10, 0L, 0, false);
        ProductResponse second = new ProductResponse(content, 0, 10, 0L, 0, false);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void responsesWithDifferentValuesShouldNotBeEqual() {
        ProductResponse first = new ProductResponse(List.of(), 0, 10, 0L, 0, false);
        ProductResponse differentPage = new ProductResponse(List.of(), 1, 10, 0L, 0, false);
        ProductResponse differentLastPage = new ProductResponse(List.of(), 0, 10, 0L, 0, true);

        assertNotEquals(first, differentPage);
        assertNotEquals(first, differentLastPage);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a ProductResponse");
    }

    @Test
    void toStringShouldContainResponseFieldValues() {
        ProductResponse response = new ProductResponse(List.of(), 1, 10, 25L, 3, true);

        String representation = response.toString();

        assertAll(
                () -> assertNotNull(representation),
                () -> assertTrue(representation.contains("pageNumber=1")),
                () -> assertTrue(representation.contains("pageSize=10")),
                () -> assertTrue(representation.contains("totalElements=25")),
                () -> assertTrue(representation.contains("totalPages=3")),
                () -> assertTrue(representation.contains("lastPage=true"))
        );
    }
}
