package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserTest {

    @Test
    void noArgsConstructorInitializesDefaultCollectionsAndLeavesOtherFieldsUnset() {
        User user = new User();

        assertNull(user.getUserId());
        assertNull(user.getUserName());
        assertNull(user.getEmailId());
        assertNull(user.getPassword());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
        assertNotNull(user.getAddresses());
        assertTrue(user.getAddresses().isEmpty());
        assertNull(user.getProducts());
        assertNull(user.getCart());
        assertNull(user.getCreatedBy());
        assertNull(user.getCreatedOn());
        assertNull(user.getLastUpdatedBy());
        assertNull(user.getLastUpdatedOn());
    }

    @Test
    void convenienceConstructorSetsCredentials() {
        User user = new User("jane.doe", "jane@example.com", "secret");

        assertNull(user.getUserId());
        assertEquals("jane.doe", user.getUserName());
        assertEquals("jane@example.com", user.getEmailId());
        assertEquals("secret", user.getPassword());
        assertNotNull(user.getRoles());
        assertNotNull(user.getAddresses());
    }

    @Test
    void allArgsConstructorSetsEveryField() {
        Long userId = 42L;
        String userName = "jane.doe";
        String emailId = "jane@example.com";
        String password = "secret";
        Role role = mock(Role.class);
        Address address = mock(Address.class);
        Product product = mock(Product.class);
        Cart cart = mock(Cart.class);
        Set<Role> roles = new HashSet<>(Set.of(role));
        List<Address> addresses = new ArrayList<>(List.of(address));
        Set<Product> products = new HashSet<>(Set.of(product));
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 2, 3, 4);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 3, 4, 5);

        User user = new User(
                userId,
                userName,
                emailId,
                password,
                roles,
                addresses,
                products,
                cart,
                "admin",
                createdOn,
                "editor",
                lastUpdatedOn
        );

        assertEquals(userId, user.getUserId());
        assertEquals(userName, user.getUserName());
        assertEquals(emailId, user.getEmailId());
        assertEquals(password, user.getPassword());
        assertSame(roles, user.getRoles());
        assertSame(addresses, user.getAddresses());
        assertSame(products, user.getProducts());
        assertSame(cart, user.getCart());
        assertEquals("admin", user.getCreatedBy());
        assertEquals(createdOn, user.getCreatedOn());
        assertEquals("editor", user.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, user.getLastUpdatedOn());
    }

    @Test
    void settersAndGettersRoundTripAllProperties() {
        User user = new User();
        Long userId = 7L;
        Role role = mock(Role.class);
        Address address = mock(Address.class);
        Product product = mock(Product.class);
        Cart cart = mock(Cart.class);
        Set<Role> roles = new HashSet<>(Set.of(role));
        List<Address> addresses = new ArrayList<>(List.of(address));
        Set<Product> products = new HashSet<>(Set.of(product));
        LocalDateTime createdOn = LocalDateTime.of(2024, 6, 7, 8, 9);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2024, 7, 8, 9, 10);

        user.setUserId(userId);
        user.setUserName("user");
        user.setEmailId("user@example.com");
        user.setPassword("password");
        user.setRoles(roles);
        user.setAddresses(addresses);
        user.setProducts(products);
        user.setCart(cart);
        user.setCreatedBy("creator");
        user.setCreatedOn(createdOn);
        user.setLastUpdatedBy("updater");
        user.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(userId, user.getUserId());
        assertEquals("user", user.getUserName());
        assertEquals("user@example.com", user.getEmailId());
        assertEquals("password", user.getPassword());
        assertSame(roles, user.getRoles());
        assertSame(addresses, user.getAddresses());
        assertSame(products, user.getProducts());
        assertSame(cart, user.getCart());
        assertEquals("creator", user.getCreatedBy());
        assertEquals(createdOn, user.getCreatedOn());
        assertEquals("updater", user.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, user.getLastUpdatedOn());
    }

    @Test
    void equalsAndHashCodeAreValueBased() {
        User first = new User("jane.doe", "jane@example.com", "secret");
        User second = new User("jane.doe", "jane@example.com", "secret");

        first.setUserId(1L);
        second.setUserId(1L);
        first.setCreatedBy("admin");
        second.setCreatedBy("admin");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, first);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a user");
    }

    @Test
    void equalsReturnsFalseWhenAPropertyDiffers() {
        User first = new User("jane.doe", "jane@example.com", "secret");
        User second = new User("john.doe", "jane@example.com", "secret");

        assertNotEquals(first, second);
    }

    @Test
    void toStringIncludesRegularFieldsAndExcludesProductAndCartFields() {
        User user = new User("jane.doe", "jane@example.com", "secret");
        Product product = mock(Product.class);
        Cart cart = mock(Cart.class);
        user.setProducts(Set.of(product));
        user.setCart(cart);

        String value = user.toString();

        assertTrue(value.contains("jane.doe"));
        assertTrue(value.contains("jane@example.com"));
        assertTrue(value.contains("secret"));
        assertFalse(value.contains("products="));
        assertFalse(value.contains("cart="));
    }
}
