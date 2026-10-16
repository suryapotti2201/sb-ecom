package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void noArgsConstructorShouldInitializeFieldsToNull() {
        Role role = new Role();

        assertNull(role.getRoleId());
        assertNull(role.getRoleName());
    }

    @Test
    void roleNameConstructorShouldSetRoleName() {
        AppRole roleName = AppRole.values()[0];

        Role role = new Role(roleName);

        assertNull(role.getRoleId());
        assertEquals(roleName, role.getRoleName());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        Long roleId = 42L;
        AppRole roleName = AppRole.values()[0];

        Role role = new Role(roleId, roleName);

        assertEquals(roleId, role.getRoleId());
        assertEquals(roleName, role.getRoleName());
    }

    @Test
    void settersShouldUpdateFields() {
        Role role = new Role();
        Long roleId = 7L;
        AppRole roleName = AppRole.values()[0];

        role.setRoleId(roleId);
        role.setRoleName(roleName);

        assertEquals(roleId, role.getRoleId());
        assertEquals(roleName, role.getRoleName());
    }

    @Test
    void equalsAndHashCodeShouldDependOnRoleFields() {
        Long roleId = 1L;
        AppRole roleName = AppRole.values()[0];

        Role first = new Role(roleId, roleName);
        Role second = new Role(roleId, roleName);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setRoleId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void equalsShouldHandleNullAndDifferentTypes() {
        Role role = new Role(1L, AppRole.values()[0]);

        assertNotEquals(role, null);
        assertNotEquals(role, new Object());
        assertEquals(role, role);
    }

    @Test
    void toStringShouldExcludeRoleName() {
        AppRole roleName = AppRole.values()[0];
        Role role = new Role(10L, roleName);

        String representation = role.toString();

        assertTrue(representation.contains("Role"));
        assertTrue(representation.contains("10"));
        assertFalse(representation.contains(roleName.name()));
    }
}
