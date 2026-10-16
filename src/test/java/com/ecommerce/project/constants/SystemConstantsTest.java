package com.ecommerce.project.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemConstantsTest {

    @Test
    void shouldExposeStatusConstants() {
        assertAll(
                () -> assertEquals('A', SystemConstants.ACTIVE),
                () -> assertEquals('I', SystemConstants.INACTIVE)
        );
    }

    @Test
    void shouldExposePaginationAndSortingConstants() {
        assertAll(
                () -> assertEquals("0", SystemConstants.PAGE_NUMBER),
                () -> assertEquals("10", SystemConstants.PAGE_SIZE),
                () -> assertEquals("productId", SystemConstants.SORT_PRODUCTS_BY),
                () -> assertEquals("asc", SystemConstants.SORT_DIR),
                () -> assertEquals("asc", SystemConstants.ASC),
                () -> assertEquals("categoryId", SystemConstants.SORT_CATEGORY_BY),
                () -> assertEquals("SYSTEM", SystemConstants.SYSTEM),
                () -> assertEquals("createdOn", SystemConstants.SORT_SELLER_ORDER_BY)
        );
    }
}
