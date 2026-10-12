package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        if(quantity == null || quantity < 1){
            throw new EcommerceException("Only positive quantities can be added to cart");
        }

        Cart cart = fetchCart();
        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId)
            );

        Optional<CartItem> cartItem = cartItemRepository.findByProductIdAndCardId(productId, cart.getCartId());
        if(cartItem.isPresent()){
            throw  new EcommerceException("Product " + product.getProductName() +
                    " already exists in the cart");
        }

        if(product.getQuantity() == 0){
            throw new EcommerceException(product.getProductName() + " is not available");
        }

        if(product.getQuantity() < quantity){
            throw new EcommerceException("Please make an order of the " + product.getProductName() +
                    " less than or equal to the quantity " + product.getQuantity());
        }


        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setCreatedBy(authUtil.loggedInUser().getUserName());
        newCartItem.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        newCartItem.setCreatedOn(LocalDateTime.now());
        newCartItem.setLastUpdatedOn(LocalDateTime.now());
        // Initialize the collection before persisting the new item.
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }
        cart.getCartItems().size();
        cartItemRepository.save(newCartItem);
        cart.getCartItems().add(newCartItem);

        recalculateTotal(cart);
        cart.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        cart.setLastUpdatedOn(LocalDateTime.now());
        cart = cartRepository.save(cart);

        return convertCartToCartDTO(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()){
            throw new EcommerceException("No Cart exist");
        }
        return carts.stream().map(this::convertCartToCartDTO).toList();
    }

    @Override
    public CartDTO getCart() {
        Cart userCart = cartRepository.findByEmailId(authUtil.loggedInEmailId());
        if(userCart == null){
            throw new EcommerceException("No Cart exist");
        }
        return  convertCartToCartDTO(userCart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long productId) {
        Cart userCart = lockCurrentCart();
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, userCart.getCartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("CartItem", "productId", productId));
        removeItem(userCart, cartItem);
        recalculateTotal(userCart);
        userCart.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        userCart.setLastUpdatedOn(LocalDateTime.now());
        cartRepository.save(userCart);
        return "Product (" + cartItem.getProduct().getProductName() +") removed from the cart!";
    }

    @Transactional
    @Override
    public CartDTO updateCartProduct(Long productId, int quantity) {
        if(!(quantity == 1 || quantity == -1)){
            throw new EcommerceException("Provide Valid quantity 1 or -1");
        }

        Cart userCart = lockCurrentCart();

        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId)
                );


        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, userCart.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        int updatedQuantity = cartItem.getQuantity() + quantity;
        if (updatedQuantity < 0) {
            throw new EcommerceException("Cart quantity cannot be negative");
        }
        if(quantity > 0 && product.getQuantity() < updatedQuantity){
            throw new EcommerceException("Please make an order of the " + product.getProductName() +
                    " less than or equal to the quantity " + product.getQuantity());
        }

        if(cartItem.getQuantity() + quantity == 0){
            removeItem(userCart, cartItem);
        }else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
            cartItem.setLastUpdatedOn(LocalDateTime.now());
            cartItemRepository.save(cartItem);
        }
        recalculateTotal(userCart);
        userCart.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        userCart.setLastUpdatedOn(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(userCart);
        return convertCartToCartDTO(updatedCart);
    }

    private Cart fetchCart() {
        // A stable user row serializes first-cart creation even when no cart exists.
        // This helper runs inside addProductToCart's transaction.
        User user = authUtil.loggedInUser();
        Object userId = entityManager.getEntityManagerFactory()
                .getPersistenceUnitUtil().getIdentifier(user);
        User lockedUser = entityManager.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (lockedUser == null) {
            throw new EcommerceException("User not found");
        }
        Cart userCart = cartRepository.findByEmailForUpdate(authUtil.loggedInEmailId())
                .orElse(null);
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(BigDecimal.valueOf(0));
        cart.setUser(lockedUser);
        cart.setCreatedBy(authUtil.loggedInUser().getUserName());
        cart.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        cart.setCreatedOn(LocalDateTime.now());
        cart.setLastUpdatedOn(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public CartDTO convertCartToCartDTO(Cart cart){
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productList = cart.getCartItems().stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(Long.valueOf(item.getQuantity()));
            return productDTO;
        }).toList();
        cartDTO.setProducts(productList);
        return cartDTO;
    }

    @Override
    @Transactional
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findByIdForUpdate(cartId).orElseThrow( () ->
                new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, cartId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId));

        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        recalculateTotal(cart);
        cart.setLastUpdatedBy(SystemConstants.SYSTEM);
        cart.setLastUpdatedOn(LocalDateTime.now());
        cartRepository.save(cart);
        cartItem.setLastUpdatedBy(SystemConstants.SYSTEM);
        cartItem.setLastUpdatedOn(LocalDateTime.now());
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findByIdForUpdate(cartId).orElseThrow( () ->
                new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, cart.getCartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("CartItem", "productId", productId));
        removeItem(cart, cartItem);
        recalculateTotal(cart);
        cart.setLastUpdatedBy(SystemConstants.SYSTEM);
        cart.setLastUpdatedOn(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private Cart lockCurrentCart() {
        return cartRepository.findByEmailForUpdate(authUtil.loggedInEmailId())
                .orElseThrow(() -> new EcommerceException("No Cart exist"));
    }

    private void removeItem(Cart cart, CartItem item) {
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
    }

    private void recalculateTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getProductPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }
}
