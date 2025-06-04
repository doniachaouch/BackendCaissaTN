package edu.polytech.caissatn.inventory.validator;


import edu.polytech.caissatn.inventory.dto.CategoryDTO;
import edu.polytech.caissatn.inventory.mapper.CategoryMapper;
import edu.polytech.caissatn.inventory.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoCircularReferenceValidator implements ConstraintValidator<NoCircularReference, CategoryDTO> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public NoCircularReferenceValidator(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void initialize(NoCircularReference constraintAnnotation) {
    }

    @Override
    public boolean isValid(CategoryDTO value, ConstraintValidatorContext context) {
        if (value == null || value.getParent() == null) {
            return true; // Null values are handled by @NotEmpty
        }

        Set<UUID> visitedIds = new HashSet<>();
        CategoryDTO current = value;
        UUID currentId = current.getId();

        while (currentId != null) {
            if (visitedIds.contains(currentId)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("category must not form a circular reference with its ancestors")
                        .addPropertyNode("parent")
                        .addConstraintViolation();
                return false;
            }
            visitedIds.add(currentId);
            if (current.getParent() == null) {
                break;
            }
            current = categoryMapper.toDTO(categoryRepository.findById(current.getParent()).get());
            currentId = current.getId();
        }

        return true;
    }
}
