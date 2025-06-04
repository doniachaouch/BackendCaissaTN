package edu.polytech.caissatn.image.mapper;


import edu.polytech.caissatn.image.dto.ImageDTO;
import edu.polytech.caissatn.image.entity.Image;
import edu.polytech.caissatn.image.repository.ImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageMapper {
    private final ImageRepository imageRepository;

    public ImageMapper(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image toEntity(ImageDTO imageDTO) {
        if (imageDTO == null) {
            return null;
        }

        return this.imageRepository.findById(imageDTO.getId()).get();
    }

/*    public List<Category> toEntities(List<CategoryDTO> categoryDTOs) {
        if (categoryDTOs.isEmpty()) {
            return null;
        }
        return categoryDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }*/

    public ImageDTO toDTO(Image image) {
        if (image == null) {
            return null;
        }

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(image.getId());

        return imageDTO;
    }
/*
    public List<CategoryDTO> toDTOs(List<Category> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }*/

}

