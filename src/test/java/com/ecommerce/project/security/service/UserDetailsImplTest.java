package com.ecommerce.project.security.service;

import com.ecommerce.project.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class UserDetailsImplTest {

    @Test
    void build_mapsUserPropertiesAndRolesToUserDetails() throws Exception {
        User user = mock(User.class);

        Long id = 42L;
        String username = "customer";
        String email = "customer@example.com";
        String password = "encoded-password";

        doReturn(id).when(user).getUserId();
        doReturn(username).when(user).getUserName();
        doReturn(email).when(user).getEmailId();
        doReturn(password).when(user).getPassword();

        Method getRolesMethod = User.class.getMethod("getRoles");
        Class<?> roleClass = resolveCollectionElementType(getRolesMethod);
        Method getRoleNameMethod = roleClass.getMethod("getRoleName");
        Object roleName = getRoleNameMethod.getReturnType().getEnumConstants()[0];

        Object role = mock(roleClass, invocation -> {
            if (invocation.getMethod().equals(getRoleNameMethod)) {
                return roleName;
            }
            return Answers.RETURNS_DEFAULTS.answer(invocation);
        });

        Collection<Object> roles = createCollection(getRolesMethod.getReturnType());
        roles.add(role);
        doReturn(roles).when(user).getRoles();

        UserDetailsImpl result = UserDetailsImpl.build(user);

        assertEquals(id, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmailId());
        assertEquals(password, result.getPassword());
        assertNotNull(result.getAuthorities());
        assertEquals(1, result.getAuthorities().size());

        GrantedAuthority authority = result.getAuthorities().iterator().next();
        assertEquals(((Enum<?>) roleName).name(), authority.getAuthority());
    }

    @Test
    void build_withEmptyRoles_returnsUserDetailsWithNoAuthorities() {
        User user = mock(User.class);

        doReturn(7L).when(user).getUserId();
        doReturn("user").when(user).getUserName();
        doReturn("user@example.com").when(user).getEmailId();
        doReturn("password").when(user).getPassword();
        doReturn(new LinkedHashSet<>()).when(user).getRoles();

        UserDetailsImpl result = UserDetailsImpl.build(user);

        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
        assertEquals(7L, result.getId());
        assertEquals("user", result.getUsername());
    }

    @Test
    void build_withNullUser_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> UserDetailsImpl.build(null));
    }

    @Test
    void build_withNullRoles_throwsNullPointerException() {
        User user = mock(User.class);
        doReturn(null).when(user).getRoles();

        assertThrows(NullPointerException.class, () -> UserDetailsImpl.build(user));
    }

    @Test
    void constructorAndAccessors_returnConfiguredValues() {
        List<GrantedAuthority> authorities =
                List.of(() -> "ROLE_CUSTOMER");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                10L,
                "username",
                "email@example.com",
                "password",
                authorities
        );

        assertEquals(10L, userDetails.getId());
        assertEquals("username", userDetails.getUsername());
        assertEquals("email@example.com", userDetails.getEmailId());
        assertEquals("password", userDetails.getPassword());
        assertSame(authorities, userDetails.getAuthorities());
    }

    @Test
    void accountStatusMethods_areAlwaysTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl();

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_returnsTrueForSameInstance() {
        UserDetailsImpl userDetails = new UserDetailsImpl();

        assertEquals(userDetails, userDetails);
    }

    @Test
    void equals_comparesUsersById() {
        UserDetailsImpl first = new UserDetailsImpl(
                1L, "first", "first@example.com", "password", List.of());
        UserDetailsImpl sameId = new UserDetailsImpl(
                1L, "different", "different@example.com", "other", List.of());
        UserDetailsImpl differentId = new UserDetailsImpl(
                2L, "first", "first@example.com", "password", List.of());

        assertEquals(first, sameId);
        assertNotEquals(first, differentId);
    }

    @Test
    void equals_treatsTwoNullIdsAsEqual() {
        UserDetailsImpl first = new UserDetailsImpl(
                null, "first", null, null, null);
        UserDetailsImpl second = new UserDetailsImpl(
                null, "second", null, null, null);

        assertEquals(first, second);
    }

    @Test
    void equals_returnsFalseForNullAndDifferentTypes() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", List.of());

        assertNotEquals(userDetails, null);
        assertNotEquals(userDetails, "not a user details instance");
    }

    private static Class<?> resolveCollectionElementType(Method getRolesMethod) {
        Type genericReturnType = getRolesMethod.getGenericReturnType();
        if (!(genericReturnType instanceof ParameterizedType parameterizedType)) {
            throw new IllegalStateException("User.getRoles() must have a parameterized return type");
        }

        Type elementType = parameterizedType.getActualTypeArguments()[0];
        if (elementType instanceof java.lang.reflect.WildcardType wildcardType) {
            elementType = wildcardType.getUpperBounds()[0];
        }
        if (elementType instanceof ParameterizedType parameterizedElementType) {
            elementType = parameterizedElementType.getRawType();
        }
        if (!(elementType instanceof Class<?> elementClass)) {
            throw new IllegalStateException("Unable to resolve the role type");
        }
        return elementClass;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollection(Class<?> collectionType) {
        if (Set.class.isAssignableFrom(collectionType)) {
            return new LinkedHashSet<>();
        }
        if (List.class.isAssignableFrom(collectionType)) {
            return new ArrayList<>();
        }
        if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet<>();
        }
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>();
        }
        throw new IllegalStateException("Unsupported roles collection type: " + collectionType);
    }
}
