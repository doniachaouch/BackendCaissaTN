package edu.polytech.caissatn.inventory.service;

import edu.polytech.caissatn.inventory.dto.ProductDTO;
import edu.polytech.caissatn.inventory.entity.Barcode;
import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.entity.Product;
import edu.polytech.caissatn.inventory.mapper.ProductMapper;
import edu.polytech.caissatn.inventory.repository.BarcodeRepository;
import edu.polytech.caissatn.inventory.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BarcodeRepository barcodeRepository;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          BarcodeRepository barcodeRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.barcodeRepository = barcodeRepository;
        this.imageRepository = imageRepository;
    }

    public Product save(ProductDTO productDTO) {
        Product product;

        if (productDTO.getId() != null && productRepository.existsById(productDTO.getId())) {
            // Mise à jour d’un produit existant
            product = productRepository.findById(productDTO.getId()).get();
            logger.info("Mise à jour du produit avec ID : {}", productDTO.getId());
            productMapper.updateEntityFromDTO(productDTO, product);
        } else {
            // Création d’un nouveau produit (même si un ID est déjà défini côté frontend)
            product = productMapper.toEntity(productDTO);
            logger.info("Création d’un nouveau produit : {}", productDTO.getName());
        }

        Product savedProduct = productRepository.save(product);
        logger.debug("Produit enregistré avec ID : {}", savedProduct.getId());

        // Gestion des codes-barres
        List<Barcode> existingBarcodes = barcodeRepository.findByProduct(savedProduct);
        Set<String> newBarcodeCodes = new HashSet<>(productDTO.getBarcodes());

        List<Barcode> barcodesToSave = new ArrayList<>();

        for (String code : newBarcodeCodes) {
            Optional<Barcode> existingBarcodeOpt = existingBarcodes.stream()
                    .filter(b -> b.getCode().equals(code))
                    .findFirst();

            if (existingBarcodeOpt.isPresent()) {
                Barcode existingBarcode = existingBarcodeOpt.get();
                existingBarcode.setProduct(savedProduct);
                barcodesToSave.add(existingBarcode);
                logger.debug("Code-barres existant mis à jour : {}", code);
            } else {
                Barcode barcode = new Barcode();
                barcode.setCode(code);
                barcode.setProduct(savedProduct);
                barcodesToSave.add(barcode);
                logger.debug("Nouveau code-barres ajouté : {}", code);
            }
        }

        // Suppression des anciens codes-barres supprimés du DTO
        existingBarcodes.stream()
                .filter(b -> !newBarcodeCodes.contains(b.getCode()))
                .forEach(b -> {
                    barcodeRepository.delete(b);
                    logger.debug("Code-barres supprimé : {}", b.getCode());
                });

        barcodeRepository.saveAll(barcodesToSave);
        logger.info("Tous les codes-barres ont été traités pour le produit : {}", savedProduct.getId());

        return savedProduct;
    }

    public void delete(String id) {
        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        logger.info("Suppression du produit avec ID : {}", id);

        if (product.getImage() != null) {
            imageRepository.delete(product.getImage());
            logger.info("Image supprimée pour le produit : {}", id);
        }

        productRepository.deleteById(UUID.fromString(id));
        logger.info("Produit supprimé avec succès : {}", id);
    }

    public List<ProductDTO> getAll() {
        List<Product> products = productRepository.findAll();
        logger.info("Récupération de tous les produits");
        return productMapper.toDTOs(products);
    }

    public Page<ProductDTO> getProducts(int page, int size, String searchQuery, UUID categoryId, UUID brandId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products;

        logger.info("Recherche produits : query='{}', categoryId={}, brandId={}", searchQuery, categoryId, brandId);

        if (categoryId != null && brandId != null) {
            products = productRepository.findByNameContainingAndCategories_IdEqualsAndBrand_IdEquals(searchQuery, categoryId, brandId, pageable);
        } else if (brandId != null) {
            products = productRepository.findByNameContainingAndBrand_IdEquals(searchQuery, brandId, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByNameContainingAndCategories_IdEquals(searchQuery, categoryId, pageable);
        } else {
            products = productRepository.findByNameOrBarcode(searchQuery, pageable);
        }

        return products.map(productMapper::toDTO);
    }

    public Optional<ProductDTO> getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO);
    }

    public Product findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec le code-barres : " + barcode));
    }
}
