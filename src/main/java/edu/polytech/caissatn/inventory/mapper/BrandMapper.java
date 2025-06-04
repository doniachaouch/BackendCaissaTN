package edu.polytech.caissatn.inventory.mapper;


import edu.polytech.caissatn.image.repository.ImageRepository;
import edu.polytech.caissatn.inventory.dto.BrandDTO;
import edu.polytech.caissatn.inventory.entity.Brand;
import edu.polytech.caissatn.inventory.repository.BrandRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandMapper {
    private final BrandRepository brandRepository;
    private final ImageRepository imageRepository;

    public BrandMapper(BrandRepository brandRepository, ImageRepository imageRepository) {
        this.brandRepository = brandRepository;
        this.imageRepository = imageRepository;
    }

    public Brand toEntity(BrandDTO brandDTO) {
        if (brandDTO == null) {
            return null;
        }

        Brand brand = brandDTO.getId() != null ? this.brandRepository.findById(brandDTO.getId()).orElse(new Brand()) : new Brand();
        brand.setName(brandDTO.getName());
        brand.setImage(brandDTO.getImage() != null ? imageRepository.findById(brandDTO.getImage()).orElse(null) : null);

        return brand;
    }

    public List<Brand> toEntities(List<BrandDTO> brandDTOs) {
        if (brandDTOs.isEmpty()) {
            return null;
        }
        return brandDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public BrandDTO toDTO(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brand.getId());
        brandDTO.setName(brand.getName());
        brandDTO.setImage(brand.getImage() !=  null ? brand.getImage().getId() : null);

        return brandDTO;
    }

    public List<BrandDTO> toDTOs(List<Brand> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}

