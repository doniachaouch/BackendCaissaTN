package edu.polytech.caissatn.inventory.service;



import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.dto.BrandDTO;
import edu.polytech.caissatn.inventory.entity.Brand;
import edu.polytech.caissatn.inventory.mapper.BrandMapper;
import edu.polytech.caissatn.inventory.repository.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BrandService {
    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ImageRepository imageRepository;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper, ImageRepository imageRepository) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.imageRepository = imageRepository;
    }

    public Brand save(BrandDTO brandDTO) {
        logger.debug("Saving brand: {}", brandDTO.getName());
        try {
            Brand brand = brandMapper.toEntity(brandDTO);
            Brand savedBrand = brandRepository.save(brand);
            logger.info("Brand saved successfully with ID: {}", savedBrand.getId());
            return savedBrand;
        } catch (Exception e) {
            logger.error("Error saving brand: {}", brandDTO.getName(), e);
            throw new RuntimeException("Error saving brand", e); // Re-throw to be handled by controller
        }
    }

    public void delete(String id) {
        logger.debug("Deleting brand with ID: {}", id);
        try {
            Brand brand = brandRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new RuntimeException("Brand not found"));

            if (brand.getImage() != null) {
                imageRepository.delete(brand.getImage());
                logger.info("Brand image deleted.");
            }

            brandRepository.deleteById(UUID.fromString(id));
            logger.info("Brand with ID: {} deleted successfully", id);
        } catch (RuntimeException e) {
            logger.warn("Attempted to delete a brand with invalid ID: {}", id);
            throw e; // Let the controller handle the response
        } catch (Exception e) {
            logger.error("Error deleting brand with ID: {}", id, e);
            throw new RuntimeException("Error deleting brand", e);
        }
    }

    public List<BrandDTO> getAll() {
        logger.debug("Fetching all brands");
        try {
            List<Brand> brands = brandRepository.findAll();
            logger.info("Fetched {} brands", brands.size());
            return brandMapper.toDTOs(brands);
        } catch (Exception e) {
            logger.error("Error fetching brands", e);
            throw new RuntimeException("Error fetching brands", e);
        }
    }
}
