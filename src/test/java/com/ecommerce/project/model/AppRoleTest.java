package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppRoleTest {

    @Test
    void valuesShouldContainAllRolesInDeclarationOrder() {
        assertArrayEquals(
                new AppRole[]{
                        AppRole.ROLE_USER,
                        AppRole.ROLE_ADMIN,
                        AppRole.ROLE_SELLER
                },
                AppRole.values()
        );
    }

    @Test
    void valueOfShouldReturnTheMatchingRole() {
        assertEquals(AppRole.ROLE_USER, AppRole.valueOf("ROLE_USER"));
        assertEquals(AppRole.ROLE_ADMIN, AppRole.valueOf("ROLE_ADMIN"));
        assertEquals(AppRole.ROLE_SELLER, AppRole.valueOf("ROLE_SELLER"));
    }

    @Test
    void valueOfShouldRejectUnknownRoleNames() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AppRole.valueOf("ROLE_UNKNOWN")
        );
    }

    @Test
    void eachRoleShouldExposeItsDeclaredNameAndOrdinal() {
        assertEquals("ROLE_USER", AppRole.ROLE_USER.name());
        assertEquals(0, AppRole.ROLE_USER.ordinal());

        assertEquals("ROLE_ADMIN", AppRole.ROLE_ADMIN.name());
        assertEquals(1, AppRole.ROLE_ADMIN.ordinal());

        assertEquals("ROLE_SELLER", AppRole.ROLE_SELLER.name());
        assertEquals(2, AppRole.ROLE_SELLER.ordinal());
    }
}
