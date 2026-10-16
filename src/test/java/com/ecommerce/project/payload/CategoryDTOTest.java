package com.ecommerce.project.payload;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void noArgsConstructorShouldInitializeDefaultValues() {
        CategoryDTO category = new CategoryDTO();

        assertNull(category.getCategoryName());
        assertEquals('\0', category.getStatus());
    }

    @Test
    void allArgsConstructorShouldInitializeAllFields() {
        CategoryDTO category = new CategoryDTO("Electronics", 'A');

        assertEquals("Electronics", category.getCategoryName());
        assertEquals('A', category.getStatus());
    }

    @Test
    void settersAndGettersShouldUpdateAndReturnFieldValues() {
        CategoryDTO category = new CategoryDTO();

        category.setCategoryName("Books");
        category.setStatus('I');

        assertEquals("Books", category.getCategoryName());
        assertEquals('I', category.getStatus());
    }

    @Test
    void equalCategoriesShouldBeEqualAndHaveSameHashCode() {
        CategoryDTO first = new CategoryDTO("Fashion", 'A');
        CategoryDTO second = new CategoryDTO("Fashion", 'A');

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void categoriesWithDifferentValuesShouldNotBeEqual() {
        CategoryDTO category = new CategoryDTO("Fashion", 'A');

        assertNotEquals(category, new CategoryDTO("Fashion", 'I'));
        assertNotEquals(category, new CategoryDTO("Home", 'A'));
        assertNotEquals(category, null);
        assertNotEquals(category, "Fashion");
    }

    @Test
    void toStringShouldContainCategoryFieldValues() {
        CategoryDTO category = new CategoryDTO("Toys", 'A');

        String representation = category.toString();

        assertTrue(representation.contains("Toys"));
        assertTrue(representation.contains("A"));
    }

    @Test
    void validCategoryNameAtMinimumAndMaximumLengthShouldPassValidation() {
        CategoryDTO minimumLengthCategory = new CategoryDTO("AB", 'A');
        CategoryDTO maximumLengthCategory =
                new CategoryDTO("A".repeat(100), 'A');

        assertTrue(validator.validate(minimumLengthCategory).isEmpty());
        assertTrue(validator.validate(maximumLengthCategory).isEmpty());
    }

    @Test
    void nullBlankAndTooShortCategoryNamesShouldFailValidation() {
        assertCategoryNameIsInvalid(null);
        assertCategoryNameIsInvalid("");
        assertCategoryNameIsInvalid("   ");
        assertCategoryNameIsInvalid("A");
    }

    @Test
    void categoryNameLongerThanMaximumShouldFailValidation() {
        CategoryDTO category = new CategoryDTO("A".repeat(101), 'A');

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(category);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath()
                        .toString()
                        .equals("categoryName")));
    }

    @Test
    void statusShouldNotBeValidated() {
        CategoryDTO category = new CategoryDTO("Valid category", '\0');

        assertTrue(validator.validate(category).isEmpty());
    }

    private void assertCategoryNameIsInvalid(String categoryName) {
        CategoryDTO category = new CategoryDTO(categoryName, 'A');

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(category);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath()
                        .toString()
                        .equals("categoryName")));
    }
}
