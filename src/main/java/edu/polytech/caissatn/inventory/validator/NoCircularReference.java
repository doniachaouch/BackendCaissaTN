package edu.polytech.caissatn.inventory.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoCircularReferenceValidator.class)
public @interface NoCircularReference {

    String message() default "category must not form a circular reference with its parent";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
