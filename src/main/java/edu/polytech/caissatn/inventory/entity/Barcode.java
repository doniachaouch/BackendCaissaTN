package edu.polytech.caissatn.inventory.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.polytech.caissatn.audit.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name = "barcodes"
)
public class Barcode extends AuditableEntity implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID id;
    @Column(
            nullable = false,
            unique = true
    )
    private @NotNull String code;
    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    @JsonIgnore
    private Product product;

    public Barcode() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
