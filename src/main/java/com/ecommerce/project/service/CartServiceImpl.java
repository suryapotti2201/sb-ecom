package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

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
    public CartDTO addProductToCart(Long productId, Integer quantity) {
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
        cartItemRepository.save(newCartItem);

        cart.setTotalPrice( cart.getTotalPrice() + (quantity * product.getSpecialPrice()));
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
        Cart userCart = cartRepository.findByEmailId(authUtil.loggedInEmailId());
        if(userCart == null){
            throw new EcommerceException("No Cart exist");
        }
        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId)
                );
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, userCart.getCartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("CartItem", "productId", productId));
        userCart.setTotalPrice(userCart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(userCart.getCartId(), productId);
        cartRepository.save(userCart);
        return "Product (" + cartItem.getProduct().getProductName() +") removed from the cart!";
    }

    @Transactional
    @Override
    public CartDTO updateCartProduct(Long productId, int quantity) {
        if(!(quantity == 1 || quantity == -1)){
            throw new EcommerceException("Provide Valid quantity 1 or -1");
        }

        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId)
                );

        Cart userCart = cartRepository.findByEmailId(authUtil.loggedInEmailId());
        if(userCart == null){
            throw new EcommerceException("No Cart exist");
        }

        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, userCart.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity() < cartItem.getQuantity() + quantity){
            throw new EcommerceException("Please make an order of the " + product.getProductName() +
                    " less than or equal to the quantity " + product.getQuantity());
        }

        if(cartItem.getQuantity() + quantity == 0){
            cartItemRepository.deleteCartItemByProductIdAndCartId(userCart.getCartId(), productId);
        }else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        }
        userCart.setTotalPrice(userCart.getTotalPrice() + (quantity * cartItem.getProductPrice()));
        Cart updatedCart = cartRepository.save(userCart);
        return convertCartToCartDTO(updatedCart);
    }

    private Cart fetchCart() {
        Cart userCart = cartRepository.findByEmailId(authUtil.loggedInEmailId());
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
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
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow( () ->
                new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, cartId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId));

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()) +
                (product.getSpecialPrice() * cartItem.getQuantity()));
        cartItem.setProductPrice(product.getSpecialPrice());
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow( () ->
                new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, cart.getCartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("CartItem", "productId", productId));
        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cart.getCartId(), productId);
        cartRepository.save(cart);
    }
}