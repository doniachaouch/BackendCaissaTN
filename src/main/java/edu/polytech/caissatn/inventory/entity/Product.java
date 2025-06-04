package edu.polytech.caissatn.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.polytech.caissatn.audit.entity.AuditableEntity;
import edu.polytech.caissatn.image.entity.Image;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    @JsonIgnore
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @NotNull
    @Column(nullable = false)
    private BigDecimal purchaseUnitPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal saleUnitPrice;

    @NotNull
    @Column(nullable = false)
    private int stockQty;

    private int stockMinLimit;

    private int stockMaxLimit;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Barcode> barcodes;

    @Column(length = 255)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public BigDecimal getPurchaseUnitPrice() {
        return purchaseUnitPrice;
    }

    public void setPurchaseUnitPrice(BigDecimal purchaseUnitPrice) {
        this.purchaseUnitPrice = purchaseUnitPrice;
    }

    public BigDecimal getSaleUnitPrice() {
        return saleUnitPrice;
    }

    public void setSaleUnitPrice(BigDecimal saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public int getStockMinLimit() {
        return stockMinLimit;
    }

    public void setStockMinLimit(int stockMinLimit) {
        this.stockMinLimit = stockMinLimit;
    }

    public int getStockMaxLimit() {
        return stockMaxLimit;
    }

    public void setStockMaxLimit(int stockMaxLimit) {
        this.stockMaxLimit = stockMaxLimit;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    // Adder and remover for categories
    public void addCategory(Category category) {
        categories.add(category);
        category.getProducts().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getProducts().remove(this);
    }
    // You can override @PrePersist and @PreUpdate to set auditing fields
    @PrePersist
    protected void onCreate() {
        this.setCreatedDate(LocalDateTime.now());
        this.setLastModifiedDate(LocalDateTime.now());
        // Set the createdBy from the auditAware implementation
    }

    @PreUpdate
    protected void onUpdate() {
        this.setLastModifiedDate(LocalDateTime.now());
        // Set the lastModifiedBy from the auditAware implementation
    }
}
