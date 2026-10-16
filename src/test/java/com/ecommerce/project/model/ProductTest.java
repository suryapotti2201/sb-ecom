package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void noArgsConstructorInitializesProductWithDefaultValues() {
        Product product = new Product();

        assertAll(
                () -> assertNull(product.getProductId()),
                () -> assertNull(product.getProductName()),
                () -> assertNull(product.getImage()),
                () -> assertNull(product.getDescription()),
                () -> assertNull(product.getPrice()),
                () -> assertNull(product.getSpecialPrice()),
                () -> assertNull(product.getDiscount()),
                () -> assertNull(product.getQuantity()),
                () -> assertEquals('\0', product.getStatus()),
                () -> assertNull(product.getCategory()),
                () -> assertNull(product.getSeller()),
                () -> assertNotNull(product.getCartItems()),
                () -> assertTrue(product.getCartItems().isEmpty()),
                () -> assertNull(product.getCreatedBy()),
                () -> assertNull(product.getCreatedOn()),
                () -> assertNull(product.getLastUpdatedBy()),
                () -> assertNull(product.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorStoresAllSuppliedValues() {
        Long productId = 10L;
        String productName = "Laptop";
        String image = "laptop.png";
        String description = "Business laptop";
        BigDecimal price = new BigDecimal("1200.00");
        BigDecimal specialPrice = new BigDecimal("1099.99");
        BigDecimal discount = new BigDecimal("100.01");
        Long quantity = 25L;
        char status = 'A';
        Category category = new Category();
        User seller = new User();
        List<CartItem> cartItems = new ArrayList<>();
        String createdBy = "admin";
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 1, 10, 15);
        String lastUpdatedBy = "manager";
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 2, 11, 30);

        Product product = new Product(
                productId,
                productName,
                image,
                description,
                price,
                specialPrice,
                discount,
                quantity,
                status,
                category,
                seller,
                cartItems,
                createdBy,
                createdOn,
                lastUpdatedBy,
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(productId, product.getProductId()),
                () -> assertEquals(productName, product.getProductName()),
                () -> assertEquals(image, product.getImage()),
                () -> assertEquals(description, product.getDescription()),
                () -> assertEquals(price, product.getPrice()),
                () -> assertEquals(specialPrice, product.getSpecialPrice()),
                () -> assertEquals(discount, product.getDiscount()),
                () -> assertEquals(quantity, product.getQuantity()),
                () -> assertEquals(status, product.getStatus()),
                () -> assertSame(category, product.getCategory()),
                () -> assertSame(seller, product.getSeller()),
                () -> assertSame(cartItems, product.getCartItems()),
                () -> assertEquals(createdBy, product.getCreatedBy()),
                () -> assertEquals(createdOn, product.getCreatedOn()),
                () -> assertEquals(lastUpdatedBy, product.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, product.getLastUpdatedOn())
        );
    }

    @Test
    void settersAndGettersUpdateAndReturnAllProperties() {
        Product product = new Product();
        Category category = new Category();
        User seller = new User();
        List<CartItem> cartItems = new ArrayList<>();
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 9, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 10, 30);

        product.setProductId(20L);
        product.setProductName("Phone");
        product.setImage("phone.png");
        product.setDescription("Smart phone");
        product.setPrice(new BigDecimal("799.99"));
        product.setSpecialPrice(new BigDecimal("699.99"));
        product.setDiscount(new BigDecimal("100.00"));
        product.setQuantity(50L);
        product.setStatus('I');
        product.setCategory(category);
        product.setSeller(seller);
        product.setCartItems(cartItems);
        product.setCreatedBy("creator");
        product.setCreatedOn(createdOn);
        product.setLastUpdatedBy("updater");
        product.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(20L, product.getProductId()),
                () -> assertEquals("Phone", product.getProductName()),
                () -> assertEquals("phone.png", product.getImage()),
                () -> assertEquals("Smart phone", product.getDescription()),
                () -> assertEquals(new BigDecimal("799.99"), product.getPrice()),
                () -> assertEquals(new BigDecimal("699.99"), product.getSpecialPrice()),
                () -> assertEquals(new BigDecimal("100.00"), product.getDiscount()),
                () -> assertEquals(50L, product.getQuantity()),
                () -> assertEquals('I', product.getStatus()),
                () -> assertSame(category, product.getCategory()),
                () -> assertSame(seller, product.getSeller()),
                () -> assertSame(cartItems, product.getCartItems()),
                () -> assertEquals("creator", product.getCreatedBy()),
                () -> assertEquals(createdOn, product.getCreatedOn()),
                () -> assertEquals("updater", product.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, product.getLastUpdatedOn())
        );
    }

    @Test
    void settersAcceptNullValuesForNullableProperties() {
        Product product = new Product();

        product.setProductName(null);
        product.setImage(null);
        product.setDescription(null);
        product.setPrice(null);
        product.setSpecialPrice(null);
        product.setDiscount(null);
        product.setQuantity(null);
        product.setCategory(null);
        product.setSeller(null);
        product.setCartItems(null);
        product.setCreatedBy(null);
        product.setCreatedOn(null);
        product.setLastUpdatedBy(null);
        product.setLastUpdatedOn(null);

        assertAll(
                () -> assertNull(product.getProductName()),
                () -> assertNull(product.getImage()),
                () -> assertNull(product.getDescription()),
                () -> assertNull(product.getPrice()),
                () -> assertNull(product.getSpecialPrice()),
                () -> assertNull(product.getDiscount()),
                () -> assertNull(product.getQuantity()),
                () -> assertNull(product.getCategory()),
                () -> assertNull(product.getSeller()),
                () -> assertNull(product.getCartItems()),
                () -> assertNull(product.getCreatedBy()),
                () -> assertNull(product.getCreatedOn()),
                () -> assertNull(product.getLastUpdatedBy()),
                () -> assertNull(product.getLastUpdatedOn())
        );
    }

    @Test
    void equalProductsAreEqualAndHaveTheSameHashCode() {
        Product first = productWithCommonValues();
        Product second = productWithCommonValues();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void productIsNotEqualToNullDifferentTypeOrProductWithDifferentState() {
        Product product = productWithCommonValues();
        Product differentProduct = productWithCommonValues();
        differentProduct.setProductName("Different");

        assertAll(
                () -> assertNotEquals(product, null),
                () -> assertNotEquals(product, "product"),
                () -> assertNotEquals(product, differentProduct),
                () -> assertEquals(product, product)
        );
    }

    @Test
    void toStringContainsProductState() {
        Product product = productWithCommonValues();

        String representation = product.toString();

        assertAll(
                () -> assertTrue(representation.contains("productId=1")),
                () -> assertTrue(representation.contains("productName=Keyboard")),
                () -> assertTrue(representation.contains("status=A")),
                () -> assertTrue(representation.contains("createdBy=tester"))
        );
    }

    private Product productWithCommonValues() {
        return new Product(
                1L,
                "Keyboard",
                "keyboard.png",
                "Mechanical keyboard",
                new BigDecimal("100.00"),
                new BigDecimal("80.00"),
                new BigDecimal("20.00"),
                10L,
                'A',
                null,
                null,
                new ArrayList<>(),
                "tester",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "tester",
                LocalDateTime.of(2025, 1, 2, 10, 0)
        );
    }
}
