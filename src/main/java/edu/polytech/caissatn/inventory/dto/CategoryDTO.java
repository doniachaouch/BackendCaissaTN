package edu.polytech.caissatn.inventory.dto;


import edu.polytech.caissatn.inventory.validator.NoCircularReference;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@NoCircularReference
public class CategoryDTO {
    private UUID id;

    @Size(max = 48)
    @Size(min = 3)
    @NotEmpty
    private String name;

    private UUID parent;

    private UUID image;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public UUID getImage() {
        return image;
    }

    public void setImage(UUID image) {
        this.image = image;
    }
}
