package edu.polytech.caissatn.sale.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BalanceAssertionValidator.class)
public @interface BalanceAssertion {

    String message() default "Transaction payments sum does not match the total price of transaction items";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
