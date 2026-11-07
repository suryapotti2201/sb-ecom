package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.service.CartService;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable @Positive(message = "Product ID must be greater than zero") Long productId,
            @PathVariable @Positive(message = "Quantity must be at least 1") Integer quantity){
        log.info("CartController => addProductToCart : In");
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        log.info("CartController => addProductToCart : Out");
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CartDTO>> getCarts(){
        log.info("CartController => getCarts : In");
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        log.info("cartDTOS : {}", cartDTOS.toString());
        log.info("CartController => getCarts : Out");
        return new ResponseEntity<>(cartDTOS, HttpStatus.OK);
    }

    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCardById(){
        log.info("CartController => getCardById : In");
        CartDTO cartDTO = cartService.getCart();
        log.info("cartDTO : {}", cartDTO.toString());
        log.info("CartController => getCardById : Out");
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long productId){
        log.info("CartController => deleteProductFromCart : In");
        String status = cartService.deleteProductFromCart(productId);
        log.info("CartController => deleteProductFromCart : Out");
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable @Positive(message = "Product ID must be greater than zero") Long productId,
            @PathVariable @Positive(message = "Quantity must be at least 1") Integer quantity){
        log.info("CartController => updateCartProduct : In");
        CartDTO cartDTO = cartService.updateCartProduct(productId, quantity);
        log.info("CartController => updateCartProduct : Out");
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

}
