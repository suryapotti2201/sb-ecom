package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void noArgsConstructorCreatesCategoryWithDefaultValues() {
        Category category = new Category();

        assertAll(
                () -> assertNull(category.getCategoryId()),
                () -> assertNull(category.getCategoryName()),
                () -> assertEquals('\0', category.getStatus()),
                () -> assertNull(category.getCreatedBy()),
                () -> assertNull(category.getCreatedOn()),
                () -> assertNull(category.getLastUpdatedBy()),
                () -> assertNull(category.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorInitializesAllFields() {
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 9, 30);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 14, 45);

        Category category = new Category(
                42L,
                "Electronics",
                'A',
                "admin",
                createdOn,
                "editor",
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(42L, category.getCategoryId()),
                () -> assertEquals("Electronics", category.getCategoryName()),
                () -> assertEquals('A', category.getStatus()),
                () -> assertEquals("admin", category.getCreatedBy()),
                () -> assertEquals(createdOn, category.getCreatedOn()),
                () -> assertEquals("editor", category.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, category.getLastUpdatedOn())
        );
    }

    @Test
    void settersAndGettersUpdateAndReturnAllFields() {
        LocalDateTime createdOn = LocalDateTime.of(2024, 6, 1, 8, 15);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2024, 6, 2, 16, 20);
        Category category = new Category();

        category.setCategoryId(7L);
        category.setCategoryName("Books");
        category.setStatus('I');
        category.setCreatedBy("system");
        category.setCreatedOn(createdOn);
        category.setLastUpdatedBy("operator");
        category.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(7L, category.getCategoryId()),
                () -> assertEquals("Books", category.getCategoryName()),
                () -> assertEquals('I', category.getStatus()),
                () -> assertEquals("system", category.getCreatedBy()),
                () -> assertEquals(createdOn, category.getCreatedOn()),
                () -> assertEquals("operator", category.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, category.getLastUpdatedOn())
        );
    }

    @Test
    void categoriesWithSameFieldValuesAreEqualAndHaveSameHashCode() {
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 11, 0);

        Category first = new Category(
                1L, "Home", 'A', "admin", createdOn, "admin", lastUpdatedOn
        );
        Category second = new Category(
                1L, "Home", 'A', "admin", createdOn, "admin", lastUpdatedOn
        );
        Category different = new Category(
                2L, "Home", 'A', "admin", createdOn, "admin", lastUpdatedOn
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }

    @Test
    void toStringIncludesCategoryState() {
        Category category = new Category(
                3L, "Garden", 'A', "admin",
                LocalDateTime.of(2025, 3, 1, 12, 0),
                "editor",
                LocalDateTime.of(2025, 3, 2, 13, 0)
        );

        String representation = category.toString();

        assertNotNull(representation);
        assertTrue(representation.contains("categoryId=3"));
        assertTrue(representation.contains("categoryName=Garden"));
        assertTrue(representation.contains("status=A"));
    }
}
