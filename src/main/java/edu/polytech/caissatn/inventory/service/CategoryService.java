package edu.polytech.caissatn.inventory.service;



import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.dto.CategoryDTO;
import edu.polytech.caissatn.inventory.entity.Category;
import edu.polytech.caissatn.inventory.mapper.CategoryMapper;
import edu.polytech.caissatn.inventory.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(ImageRepository imageRepository, CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public Category save(CategoryDTO categoryDTO) {
        try {
            logger.debug("Saving category with name: {}", categoryDTO.getName());
            Category category = categoryMapper.toEntity(categoryDTO);
            Category savedCategory = categoryRepository.save(category);
            logger.info("Category saved successfully with ID: {}", savedCategory.getId());
            return savedCategory;
        } catch (Exception e) {
            logger.error("Error saving category: {}", categoryDTO.getName(), e);
            throw new RuntimeException("Failed to save category", e);
        }
    }

    public void delete(String id) {
        try {
            UUID categoryId = UUID.fromString(id);
            logger.debug("Attempting to delete category with ID: {}", categoryId);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Delete the related image if it exists
            if (category.getImage() != null) {
                try {
                    logger.info("Deleting related image for category with ID: {}", categoryId);
                    imageRepository.delete(category.getImage());
                    logger.info("Image deleted successfully for category with ID: {}", categoryId);
                } catch (Exception e) {
                    logger.warn("Error deleting image for category with ID: {}", categoryId, e);
                }
            }

            categoryRepository.deleteById(categoryId);
            logger.info("Category with ID: {} deleted successfully", categoryId);
        } catch (Exception e) {
            logger.error("Error deleting category with ID: {}", id, e);
            throw new RuntimeException("Failed to delete category", e);
        }
    }

    public List<CategoryDTO> getAll() {
        try {
            logger.debug("Fetching all categories");
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDTO> categoryDTOs = categoryMapper.toDTOs(categories);
            logger.info("Successfully fetched {} categories", categoryDTOs.size());
            return categoryDTOs;
        } catch (Exception e) {
            logger.error("Error fetching categories", e);
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }
}
