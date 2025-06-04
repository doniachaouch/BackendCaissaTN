package edu.polytech.caissatn.inventory.controller;

import edu.polytech.caissatn.inventory.dto.ProductDTO;
import edu.polytech.caissatn.inventory.entity.Product;
import edu.polytech.caissatn.inventory.service.ProductService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("Creating product: {}", productDTO.getName());
        try {
            Product product = productService.save(productDTO);
            productDTO.setId(product.getId());
            logger.info("Product created with ID: {}", product.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
        } catch (RuntimeException e) {
            logger.error("Error while creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        logger.info("Deleting product with ID: {}", id);
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error while deleting product: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String searchQuery,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId
    ) {
        logger.info("Fetching all products with pagination - page: {}, size: {}", page, size);
        Page<ProductDTO> productsPage = productService.getProducts(page, size, searchQuery, categoryId, brandId);
        return ResponseEntity.ok(productsPage);
    }

    @GetMapping("/barcode")
    public ResponseEntity<Product> getProductByBarcode(@RequestParam String barcode) {
        logger.info("Fetching product by barcode: {}", barcode);
        Product product = productService.findByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        logger.info("Fetching product with ID: {}", id);
        Optional<ProductDTO> productDTO = productService.getProductById(id);
        return productDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
