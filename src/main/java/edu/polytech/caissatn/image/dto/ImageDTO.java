package edu.polytech.caissatn.image.dto;


import edu.polytech.caissatn.inventory.validator.NoCircularReference;
import jakarta.persistence.Id;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@NoCircularReference
public class ImageDTO {
    @Id
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
