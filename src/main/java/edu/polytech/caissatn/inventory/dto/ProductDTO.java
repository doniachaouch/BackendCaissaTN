package edu.polytech.caissatn.inventory.dto;

import edu.polytech.caissatn.inventory.validator.ValidProduct;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ValidProduct
public class ProductDTO{
    private UUID id;

    @Size(max = 48)
    @Size(min = 3)
    @NotEmpty
    private String name;

    @NotEmpty
    private List<UUID> categories;

    private UUID brand;

    private UUID image;

    @Min(0)
    private BigDecimal purchaseUnitPrice;

    @Min(0)
    private BigDecimal saleUnitPrice;

    @Min(0)
    private int stockQty;

    @Min(0)
    private int stockMinLimit;

    @Min(0)
    private int stockMaxLimit;

    private List<String> barcodes;

    @Size(max = 255)
    private String description;

    public @Size(max = 255) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 255) String description) {
        this.description = description;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
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

    public List<UUID> getCategories() {
        return categories;
    }

    public void setCategories(List<UUID> categories) {
        this.categories = categories;
    }

    public UUID getBrand() {
        return brand;
    }

    public void setBrand(UUID brand) {
        this.brand = brand;
    }

    public UUID getImage() {
        return image;
    }

    public void setImage(UUID image) {
        this.image = image;
    }
}
