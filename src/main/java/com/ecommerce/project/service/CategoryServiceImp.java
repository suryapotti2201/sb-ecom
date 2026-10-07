package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        if(categoryRepository.existsByCategoryName(category.getCategoryName())){
            throw new EcommerceException("Category with the name " + category.getCategoryName() + " already exists !!!");
        }
        category.setStatus(SystemConstants.ACTIVE);
        category.setCreatedBy(authUtil.loggedInUser().getUserName());
        category.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        category.setCreatedOn(LocalDateTime.now());
        category.setLastUpdatedOn(LocalDateTime.now());
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty()){
            throw new EcommerceException("Categories are not yet created!!!");
        }
        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        category.setStatus(SystemConstants.INACTIVE);
        category.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        category.setLastUpdatedOn(LocalDateTime.now());
        Category updateCategory = categoryRepository.save(category);
        List<Product> products = productRepository.findByCategory(category);
        products.forEach(product -> productService.deleteProduct(product.getProductId()));
        return modelMapper.map(updateCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Optional<Category> optionalCategory = categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE);
        if (optionalCategory.isPresent()){
            if(categoryRepository.existsByCategoryName(category.getCategoryName())){
                throw new EcommerceException("Category with the name " + category.getCategoryName() + " already exists !!!");
            }
            category.setCategoryId(categoryId);
            category.setStatus(SystemConstants.ACTIVE);
            category.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
            category.setLastUpdatedOn(LocalDateTime.now());
            Category updatedCategory = categoryRepository.save(category);
            return modelMapper.map(updatedCategory, CategoryDTO.class);
        }else {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()){
            return modelMapper.map(category.get(), CategoryDTO.class);
        }else {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }
    }

    @Override
    public CategoryResponse getActiveCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAllByStatus(SystemConstants.ACTIVE, pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty()){
            throw new EcommerceException("Categories are not yet created!!!");
        }
        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }
}