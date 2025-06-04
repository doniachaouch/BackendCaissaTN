package edu.polytech.caissatn.sale.validator;

import edu.polytech.caissatn.sale.dto.PaymentDTO;

import edu.polytech.caissatn.sale.dto.TransactionDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class BalanceAssertionValidator implements ConstraintValidator<BalanceAssertion, TransactionDTO> {

    @Override
    public void initialize(BalanceAssertion constraintAnnotation) {
    }


   @Override
   public boolean isValid(TransactionDTO value, ConstraintValidatorContext context) {
       //return true;/*
       if (value == null || value.getItems().isEmpty()) {
           return true;
       }

       // Supposant que item.getPrice() et item.getQty() renvoient déjà des BigDecimal
       BigDecimal expectedTotal = value.getItems().stream()
               .map(item -> item.getPrice().multiply(new BigDecimal(item.getQty()))) // Multiplier price * qty
               .reduce(BigDecimal.ZERO, BigDecimal::add); // Somme des résultats

       // Supposant que payment.getAmount() renvoie déjà un BigDecimal
       BigDecimal actualTotal = value.getPayments().stream()
               .map(PaymentDTO::getAmount) // Utiliser directement le montant
               .reduce(BigDecimal.ZERO, BigDecimal::add); // Somme des paiements

       BigDecimal epsilon = new BigDecimal("0.01"); // Tolérance d'erreur

       // Comparer la différence avec l'epsilon, utiliser .abs() pour valeur absolue
       return expectedTotal.subtract(actualTotal).abs().compareTo(epsilon) < 0;
   }

}
