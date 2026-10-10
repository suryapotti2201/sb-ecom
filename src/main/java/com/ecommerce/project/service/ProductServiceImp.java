package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDto, Long categoryId) {
        Product product = modelMapper.map(productDto, Product.class);
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        product.setCategory(category);
        if(productRepository.existsByProductName(product.getProductName())){
            throw new EcommerceException("Product with the name " + product.getProductName() + " already exists !!!");
        }
        product.setSpecialPrice(product.getPrice().subtract(
                product.getDiscount().multiply(BigDecimal.valueOf(0.01)).multiply(
                        product.getPrice())));
        product.setImage("default.png");
        product.setStatus(SystemConstants.ACTIVE);
        product.setCreatedBy(authUtil.loggedInUser().getUserName());
        product.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        product.setCreatedOn(LocalDateTime.now());
        product.setLastUpdatedOn(LocalDateTime.now());
        Product createdProduct = productRepository.save(product);
        return modelMapper.map(createdProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new EcommerceException("Products are not yet created!!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    @Transactional
    public ProductDTO deleteProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setStatus(SystemConstants.INACTIVE);
            product.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
            product.setLastUpdatedOn(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            List<Cart> carts = cartRepository.findByProductId(updatedProduct.getProductId());
            carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
            return modelMapper.map(updatedProduct, ProductDTO.class);
        }else {
          throw new ResourceNotFoundException("Product", "productId", productId);
        }
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDto, Long productId) {
        Product product = modelMapper.map(productDto, Product.class);
        Product productFromDb = productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getPrice().subtract(
                product.getDiscount().multiply(BigDecimal.valueOf(0.01)).multiply(
                        product.getPrice())));
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        productFromDb.setLastUpdatedOn(LocalDateTime.now());
        Product updatedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findByProductId(updatedProduct.getProductId());
        carts.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            return  modelMapper.map(product.get(), ProductDTO.class);
        }else {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }
    }

    @Override
    public ProductResponse getAllActiveProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByStatus(SystemConstants.ACTIVE, pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new EcommerceException("Products are not yet created!!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByCategory(category, pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new EcommerceException("Products are not yet created!!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new EcommerceException("Products are not yet created!!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }
}