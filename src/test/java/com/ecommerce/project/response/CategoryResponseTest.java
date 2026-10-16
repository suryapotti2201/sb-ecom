package com.ecommerce.project.response;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryResponseTest {

    @Test
    void noArgsConstructorShouldInitializeDefaultValues() {
        CategoryResponse response = new CategoryResponse();

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
        List<com.ecommerce.project.payload.CategoryDTO> content =
                Collections.singletonList(null);

        CategoryResponse response = new CategoryResponse(
                content,
                2,
                10,
                25L,
                3,
                true
        );

        assertAll(
                () -> assertEquals(content, response.getContent()),
                () -> assertEquals(2, response.getPageNumber()),
                () -> assertEquals(10, response.getPageSize()),
                () -> assertEquals(25L, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldUpdateAllFields() {
        List<com.ecommerce.project.payload.CategoryDTO> content =
                Collections.emptyList();
        CategoryResponse response = new CategoryResponse();

        response.setContent(content);
        response.setPageNumber(1);
        response.setPageSize(20);
        response.setTotalElements(100L);
        response.setTotalPages(5);
        response.setLastPage(true);

        assertAll(
                () -> assertEquals(content, response.getContent()),
                () -> assertEquals(1, response.getPageNumber()),
                () -> assertEquals(20, response.getPageSize()),
                () -> assertEquals(100L, response.getTotalElements()),
                () -> assertEquals(5, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldSupportNullValuesAndFalseLastPage() {
        CategoryResponse response = new CategoryResponse(
                Collections.emptyList(),
                1,
                10,
                10L,
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
        List<com.ecommerce.project.payload.CategoryDTO> content =
                Collections.singletonList(null);

        CategoryResponse first = new CategoryResponse(
                content, 0, 10, 1L, 1, false
        );
        CategoryResponse second = new CategoryResponse(
                content, 0, 10, 1L, 1, false
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void responsesWithDifferentValuesShouldNotBeEqual() {
        CategoryResponse first = new CategoryResponse(
                Collections.emptyList(), 0, 10, 1L, 1, false
        );
        CategoryResponse second = new CategoryResponse(
                Collections.emptyList(), 0, 10, 1L, 1, true
        );

        assertNotEquals(first, second);
    }

    @Test
    void toStringShouldContainFieldValues() {
        CategoryResponse response = new CategoryResponse(
                Collections.emptyList(), 1, 10, 20L, 2, true
        );

        String result = response.toString();

        assertAll(
                () -> assertTrue(result.contains("pageNumber=1")),
                () -> assertTrue(result.contains("pageSize=10")),
                () -> assertTrue(result.contains("totalElements=20")),
                () -> assertTrue(result.contains("totalPages=2")),
                () -> assertTrue(result.contains("lastPage=true"))
        );
    }
}
