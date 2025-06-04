package edu.polytech.caissatn.inventory.validator;


import edu.polytech.caissatn.inventory.dto.ProductDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ProductDTOValidator implements ConstraintValidator<ValidProduct, ProductDTO> {

    @Override
    public void initialize(ValidProduct constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProductDTO productDTO, ConstraintValidatorContext context) {
        if (productDTO == null) {
            return true; // Let @NotNull handle this
        }

        // Assurez-vous que saleUnitPrice et purchaseUnitPrice sont des BigDecimal
        BigDecimal saleUnitPrice = productDTO.getSaleUnitPrice(); // supposé déjà BigDecimal
        BigDecimal purchaseUnitPrice = productDTO.getPurchaseUnitPrice(); // supposé déjà BigDecimal

        // Check if saleUnitPrice is higher than purchaseUnitPrice
        if (saleUnitPrice.compareTo(purchaseUnitPrice) <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Sale unit price must be higher than purchase unit price")
                    .addPropertyNode("saleUnitPrice")
                    .addConstraintViolation();
            return false;
        }

        // Assurez-vous que stockMaxLimit et stockMinLimit sont des BigDecimal
        BigDecimal stockMaxLimit = BigDecimal.valueOf(productDTO.getStockMaxLimit()); // conversion d'un int ou double
        BigDecimal stockMinLimit = BigDecimal.valueOf(productDTO.getStockMinLimit()); // conversion d'un int ou double

        // Check if stockMaxLimit is higher or equal to stockMinLimit
        if (stockMaxLimit.compareTo(stockMinLimit) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Max stock limit must be higher or equal to min stock limit")
                    .addPropertyNode("stockMaxLimit")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
