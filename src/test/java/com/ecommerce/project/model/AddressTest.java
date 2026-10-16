package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void noArgsConstructorShouldInitializeFieldsToNull() {
        Address address = new Address();

        assertAll(
                () -> assertNull(address.getAddressId()),
                () -> assertNull(address.getStreet()),
                () -> assertNull(address.getBuildingName()),
                () -> assertNull(address.getCity()),
                () -> assertNull(address.getCountry()),
                () -> assertNull(address.getPincode()),
                () -> assertNull(address.getUser()),
                () -> assertNull(address.getCreatedBy()),
                () -> assertNull(address.getCreatedOn()),
                () -> assertNull(address.getLastUpdatedBy()),
                () -> assertNull(address.getLastUpdatedOn())
        );
    }

    @Test
    void convenienceConstructorShouldSetAddressDetails() {
        Address address = new Address(
                "Main Street",
                "Building A",
                "Springfield",
                "USA",
                "12345"
        );

        assertAll(
                () -> assertNull(address.getAddressId()),
                () -> assertEquals("Main Street", address.getStreet()),
                () -> assertEquals("Building A", address.getBuildingName()),
                () -> assertEquals("Springfield", address.getCity()),
                () -> assertEquals("USA", address.getCountry()),
                () -> assertEquals("12345", address.getPincode()),
                () -> assertNull(address.getUser()),
                () -> assertNull(address.getCreatedBy()),
                () -> assertNull(address.getCreatedOn()),
                () -> assertNull(address.getLastUpdatedBy()),
                () -> assertNull(address.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorShouldSetEveryField() {
        User user = new User();
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 12, 30);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 15, 45);

        Address address = new Address(
                7L,
                "Main Street",
                "Building A",
                "Springfield",
                "USA",
                "12345",
                user,
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(7L, address.getAddressId()),
                () -> assertEquals("Main Street", address.getStreet()),
                () -> assertEquals("Building A", address.getBuildingName()),
                () -> assertEquals("Springfield", address.getCity()),
                () -> assertEquals("USA", address.getCountry()),
                () -> assertEquals("12345", address.getPincode()),
                () -> assertSame(user, address.getUser()),
                () -> assertEquals("creator", address.getCreatedBy()),
                () -> assertEquals(createdOn, address.getCreatedOn()),
                () -> assertEquals("updater", address.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, address.getLastUpdatedOn())
        );
    }

    @Test
    void settersShouldUpdateEveryField() {
        Address address = new Address();
        User user = new User();
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 11, 0);

        address.setAddressId(10L);
        address.setStreet("Oak Avenue");
        address.setBuildingName("Building B");
        address.setCity("Chicago");
        address.setCountry("Canada");
        address.setPincode("A1B2C3");
        address.setUser(user);
        address.setCreatedBy("admin");
        address.setCreatedOn(createdOn);
        address.setLastUpdatedBy("editor");
        address.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(10L, address.getAddressId()),
                () -> assertEquals("Oak Avenue", address.getStreet()),
                () -> assertEquals("Building B", address.getBuildingName()),
                () -> assertEquals("Chicago", address.getCity()),
                () -> assertEquals("Canada", address.getCountry()),
                () -> assertEquals("A1B2C3", address.getPincode()),
                () -> assertSame(user, address.getUser()),
                () -> assertEquals("admin", address.getCreatedBy()),
                () -> assertEquals(createdOn, address.getCreatedOn()),
                () -> assertEquals("editor", address.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, address.getLastUpdatedOn())
        );
    }

    @Test
    void settersShouldAllowFieldsToBeCleared() {
        Address address = new Address(
                "Street",
                "Building",
                "City",
                "Country",
                "Pincode"
        );

        address.setAddressId(null);
        address.setStreet(null);
        address.setBuildingName(null);
        address.setCity(null);
        address.setCountry(null);
        address.setPincode(null);
        address.setUser(null);
        address.setCreatedBy(null);
        address.setCreatedOn(null);
        address.setLastUpdatedBy(null);
        address.setLastUpdatedOn(null);

        assertAll(
                () -> assertNull(address.getAddressId()),
                () -> assertNull(address.getStreet()),
                () -> assertNull(address.getBuildingName()),
                () -> assertNull(address.getCity()),
                () -> assertNull(address.getCountry()),
                () -> assertNull(address.getPincode()),
                () -> assertNull(address.getUser()),
                () -> assertNull(address.getCreatedBy()),
                () -> assertNull(address.getCreatedOn()),
                () -> assertNull(address.getLastUpdatedBy()),
                () -> assertNull(address.getLastUpdatedOn())
        );
    }

    @Test
    void equalAddressesShouldBeEqualAndHaveSameHashCode() {
        Address first = new Address(
                1L,
                "Street",
                "Building",
                "City",
                "Country",
                "12345",
                null,
                "creator",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "updater",
                LocalDateTime.of(2025, 1, 2, 10, 0)
        );
        Address second = new Address(
                1L,
                "Street",
                "Building",
                "City",
                "Country",
                "12345",
                null,
                "creator",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "updater",
                LocalDateTime.of(2025, 1, 2, 10, 0)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void addressesWithDifferentFieldValuesShouldNotBeEqual() {
        Address first = new Address("Street", "Building", "City", "Country", "12345");
        Address second = new Address("Different Street", "Building", "City", "Country", "12345");

        assertNotEquals(first, second);
    }

    @Test
    void addressShouldNotEqualNullOrAnotherType() {
        Address address = new Address();

        assertNotEquals(address, null);
        assertNotEquals(address, "address");
    }

    @Test
    void toStringShouldRepresentAddressWithoutIncludingUserField() {
        Address address = new Address(
                5L,
                "Main Street",
                "Building A",
                "Springfield",
                "USA",
                "12345",
                new User(),
                "creator",
                null,
                "updater",
                null
        );

        String representation = address.toString();

        assertAll(
                () -> assertTrue(representation.contains("addressId=5")),
                () -> assertTrue(representation.contains("street=Main Street")),
                () -> assertTrue(representation.contains("city=Springfield")),
                () -> assertTrue(representation.contains("country=USA")),
                () -> assertFalse(representation.contains("user="))
        );
    }
}
