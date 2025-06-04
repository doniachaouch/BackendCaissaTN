package edu.polytech.caissatn.inventory.mapper;


import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.dto.CategoryDTO;
import edu.polytech.caissatn.inventory.entity.Category;
import edu.polytech.caissatn.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    public CategoryMapper(CategoryRepository categoryRepository, ImageRepository imageRepository) {
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
    }

    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = categoryDTO.getId() != null ? this.categoryRepository.findById(categoryDTO.getId()).orElse(new Category()) : new Category();
        category.setName(categoryDTO.getName());
        category.setParent(categoryDTO.getParent() != null ? categoryRepository.findById(categoryDTO.getParent()).orElse(null) : null);
        category.setImage(categoryDTO.getImage() != null ? imageRepository.findById(categoryDTO.getImage()).orElse(null) : null);

        return category;
    }

    public List<Category> toEntities(List<CategoryDTO> categoryDTOs) {
        if (categoryDTOs.isEmpty()) {
            return null;
        }
        return categoryDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setParent(category.getParent() != null ? category.getParent().getId() : null);
        categoryDTO.setImage(category.getImage() !=  null ? category.getImage().getId() : null);

        return categoryDTO;
    }

    public List<CategoryDTO> toDTOs(List<Category> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}

