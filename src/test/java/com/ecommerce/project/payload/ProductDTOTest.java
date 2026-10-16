package com.ecommerce.project.payload;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductDTOTest {

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
    void noArgsConstructorShouldInitializeFieldsToDefaults() {
        ProductDTO product = new ProductDTO();

        assertAll(
                () -> assertNull(product.getProductName()),
                () -> assertNull(product.getImage()),
                () -> assertNull(product.getPrice()),
                () -> assertNull(product.getDiscount()),
                () -> assertNull(product.getSpecialPrice()),
                () -> assertNull(product.getQuantity()),
                () -> assertEquals('\0', product.getStatus())
        );
    }

    @Test
    void allArgsConstructorShouldInitializeAllFields() {
        ProductDTO product = validProduct();

        assertAll(
                () -> assertEquals("Laptop", product.getProductName()),
                () -> assertEquals("laptop.png", product.getImage()),
                () -> assertEquals(new BigDecimal("999.99"), product.getPrice()),
                () -> assertEquals(new BigDecimal("10.00"), product.getDiscount()),
                () -> assertEquals(new BigDecimal("899.99"), product.getSpecialPrice()),
                () -> assertEquals(5L, product.getQuantity()),
                () -> assertEquals('A', product.getStatus())
        );
    }

    @Test
    void settersAndGettersShouldRoundTripValues() {
        ProductDTO product = new ProductDTO();
        BigDecimal price = new BigDecimal("125.50");
        BigDecimal discount = new BigDecimal("15.25");
        BigDecimal specialPrice = new BigDecimal("105.00");

        product.setProductName("Tablet");
        product.setImage("tablet.jpg");
        product.setPrice(price);
        product.setDiscount(discount);
        product.setSpecialPrice(specialPrice);
        product.setQuantity(12L);
        product.setStatus('I');

        assertAll(
                () -> assertEquals("Tablet", product.getProductName()),
                () -> assertEquals("tablet.jpg", product.getImage()),
                () -> assertSame(price, product.getPrice()),
                () -> assertSame(discount, product.getDiscount()),
                () -> assertSame(specialPrice, product.getSpecialPrice()),
                () -> assertEquals(12L, product.getQuantity()),
                () -> assertEquals('I', product.getStatus())
        );
    }

    @Test
    void equalProductsShouldBeEqualAndHaveSameHashCode() {
        ProductDTO first = validProduct();
        ProductDTO second = validProduct();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void productsWithDifferentValuesShouldNotBeEqual() {
        ProductDTO first = validProduct();
        ProductDTO second = validProduct();
        second.setQuantity(6L);

        assertNotEquals(first, second);
    }

    @Test
    void toStringShouldContainProductState() {
        ProductDTO product = validProduct();

        String result = product.toString();

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.contains("Laptop")),
                () -> assertTrue(result.contains("laptop.png")),
                () -> assertTrue(result.contains("999.99")),
                () -> assertTrue(result.contains("10.00")),
                () -> assertTrue(result.contains("899.99")),
                () -> assertTrue(result.contains("5")),
                () -> assertTrue(result.contains("A"))
        );
    }

    @Test
    void validProductShouldHaveNoConstraintViolations() {
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(validProduct());

        assertTrue(violations.isEmpty());
    }

    @Test
    void productNameShouldBeRequiredAndHaveAtLeastTwoCharacters() {
        ProductDTO product = validProduct();
        product.setProductName(" ");

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertHasViolation(violations, "productName",
                "ProductName should not be null or blank");

        product.setProductName("A");
        violations = validator.validate(product);

        assertHasViolation(violations, "productName",
                "Product name must be between 2 and 150 characters");
    }

    @Test
    void productNameShouldNotExceedMaximumLength() {
        ProductDTO product = validProduct();
        product.setProductName("A".repeat(151));

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertHasViolation(violations, "productName",
                "Product name must be between 2 and 150 characters");
    }

    @Test
    void imageShouldNotExceedMaximumLength() {
        ProductDTO product = validProduct();
        product.setImage("i".repeat(256));

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertHasViolation(violations, "image",
                "Image filename must not exceed 255 characters");
    }

    @Test
    void priceShouldBeRequiredAndGreaterThanZero() {
        ProductDTO product = validProduct();
        product.setPrice(null);

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);
        assertHasViolation(violations, "price", "Price is required");

        product.setPrice(BigDecimal.ZERO);
        violations = validator.validate(product);

        assertHasViolation(violations, "price", "Price must be greater than zero");
    }

    @Test
    void priceShouldRespectDigitsConstraint() {
        ProductDTO product = validProduct();
        product.setPrice(new BigDecimal("12345678901.00"));

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertHasViolation(violations, "price",
                "Price must have at most 10 integer digits and 2 decimal places");

        product.setPrice(new BigDecimal("10.123"));
        violations = validator.validate(product);

        assertHasViolation(violations, "price",
                "Price must have at most 10 integer digits and 2 decimal places");
    }

    @Test
    void discountShouldBeRequiredAndWithinAllowedRange() {
        ProductDTO product = validProduct();
        product.setDiscount(null);

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);
        assertHasViolation(violations, "discount", "Discount is required");

        product.setDiscount(new BigDecimal("-0.01"));
        violations = validator.validate(product);
        assertHasViolation(violations, "discount", "Discount must not be negative");

        product.setDiscount(new BigDecimal("100.01"));
        violations = validator.validate(product);
        assertHasViolation(violations, "discount",
                "Discount must not exceed 100 percent");
    }

    @Test
    void discountShouldRespectDigitsConstraint() {
        ProductDTO product = validProduct();
        product.setDiscount(new BigDecimal("1000.00"));

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertHasViolation(violations, "discount",
                "Discount must have at most 3 integer digits and 2 decimal places");

        product.setDiscount(new BigDecimal("10.123"));
        violations = validator.validate(product);

        assertHasViolation(violations, "discount",
                "Discount must have at most 3 integer digits and 2 decimal places");
    }

    @Test
    void quantityShouldBeRequiredAndNonNegative() {
        ProductDTO product = validProduct();
        product.setQuantity(null);

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);
        assertHasViolation(violations, "quantity", "Stock quantity is required");

        product.setQuantity(-1L);
        violations = validator.validate(product);

        assertHasViolation(violations, "quantity",
                "Stock quantity must be zero or greater");
    }

    @Test
    void zeroQuantityAndBoundaryValuesShouldBeValid() {
        ProductDTO product = validProduct();
        product.setQuantity(0L);
        product.setPrice(new BigDecimal("0.01"));
        product.setDiscount(new BigDecimal("100.00"));

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        assertTrue(violations.isEmpty());
    }

    private static ProductDTO validProduct() {
        return new ProductDTO(
                "Laptop",
                "laptop.png",
                new BigDecimal("999.99"),
                new BigDecimal("10.00"),
                new BigDecimal("899.99"),
                5L,
                'A'
        );
    }

    private static void assertHasViolation(
            Set<ConstraintViolation<ProductDTO>> violations,
            String property,
            String message) {

        assertTrue(
                violations.stream().anyMatch(violation ->
                        violation.getPropertyPath().toString().equals(property)
                                && violation.getMessage().equals(message)),
                () -> "Expected violation for property '" + property
                        + "' with message '" + message + "', but got " + violations
        );
    }
}
