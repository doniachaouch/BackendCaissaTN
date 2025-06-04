package edu.polytech.caissatn.inventory.mapper;


import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.dto.ProductDTO;
import edu.polytech.caissatn.inventory.entity.Product;
import edu.polytech.caissatn.inventory.repository.BarcodeRepository;
import edu.polytech.caissatn.inventory.repository.BrandRepository;
import edu.polytech.caissatn.inventory.repository.CategoryRepository;
import edu.polytech.caissatn.inventory.repository.ProductRepository;
import org.springframework.stereotype.Component;
import edu.polytech.caissatn.inventory.entity.Category;
import edu.polytech.caissatn.inventory.entity.Barcode;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ImageRepository imageRepository;
    private final BarcodeRepository barcodeRepository;

    public ProductMapper(ProductRepository productRepository, CategoryRepository categoryRepository, BrandRepository brandRepository, ImageRepository imageRepository, BarcodeRepository barcodeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.imageRepository = imageRepository;
        this.barcodeRepository=barcodeRepository;
    }

    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        Product product = productDTO.getId() != null ? this.productRepository.findById(productDTO.getId()).orElse(new Product()) : new Product();
        product.setName(productDTO.getName());
        product.setImage(productDTO.getImage() != null ? imageRepository.findById(productDTO.getImage()).orElse(null) : null);
        product.setBrand(brandRepository.findById(productDTO.getBrand()).orElse(null));
        product.setPurchaseUnitPrice(productDTO.getPurchaseUnitPrice());
        product.setSaleUnitPrice(productDTO.getSaleUnitPrice());
        product.setStockQty(productDTO.getStockQty());
        product.setStockMinLimit(productDTO.getStockMinLimit());
        product.setStockMaxLimit(productDTO.getStockMaxLimit());
        product.setDescription(productDTO.getDescription());
        product.setCategories(categoryRepository.findAllById(productDTO.getCategories()));
        product.setBarcodes(this.barcodeRepository.findAllByProductId(productDTO.getId()));
        return product;
    }

    public List<Product> toEntities(List<ProductDTO> productDTOs) {
        if (productDTOs.isEmpty()) {
            return null;
        }
        return productDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setImage(product.getImage() !=  null ? product.getImage().getId() : null);
        productDTO.setBrand(product.getBrand() !=  null ? product.getBrand().getId() : null);
        productDTO.setPurchaseUnitPrice(product.getPurchaseUnitPrice());
        productDTO.setSaleUnitPrice(product.getSaleUnitPrice());
        productDTO.setStockQty(product.getStockQty());
        productDTO.setStockMinLimit(product.getStockMinLimit());
        productDTO.setStockMaxLimit(product.getStockMaxLimit());
        productDTO.setCategories(product.getCategories().stream().map(Category::getId).toList());
        // Map barcodes
        productDTO.setBarcodes(product.getBarcodes().stream().map(Barcode::getCode).collect(Collectors.toList()));
        productDTO.setDescription(product.getDescription());
        return productDTO;
    }

  public List<ProductDTO> toDTOs(List<Product> products) {
      return products.stream().map(this::toDTO).collect(Collectors.toList());
  }
    public void updateEntityFromDTO(ProductDTO productDTO, Product product) {
        if (productDTO == null || product == null) {
            return;
        }

        // Update basic product fields
        product.setName(productDTO.getName());
        product.setImage(productDTO.getImage() != null ? imageRepository.findById(productDTO.getImage()).orElse(null) : null);
        product.setBrand(brandRepository.findById(productDTO.getBrand()).orElse(null));
        product.setPurchaseUnitPrice(productDTO.getPurchaseUnitPrice());
        product.setSaleUnitPrice(productDTO.getSaleUnitPrice());
        product.setStockQty(productDTO.getStockQty());
        product.setStockMinLimit(productDTO.getStockMinLimit());
        product.setStockMaxLimit(productDTO.getStockMaxLimit());
        product.setCategories(categoryRepository.findAllById(productDTO.getCategories()));
        product.setDescription(productDTO.getDescription());
        // Update barcodes (handled separately in the service)
        // This ensures that only the current barcodes are retained and updated
    }


}

