package edu.polytech.caissatn.sale.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class CustomerDTO {
    @Id
    private UUID id;

    @Size(max = 50, message = "this field must be no more than 50 characters long")
    @Size(min = 3, message = "this field must be more than 2 character")
    @NotEmpty(message = "this field must not be empty or null")
    private String firstName;

    @Size(max = 50, message = "this field must be no more than 50 characters long")
    @Size(min = 3, message = "this field must be more than 2 character")
    @NotEmpty(message = "this field must not be empty or null")
    private String lastName;

    @NotEmpty(message = "this field must not be empty or null")
    @Size(min = 8, max = 8, message = "Phone number must be exactly 8 digits long")
    @Pattern(regexp = "\\d{8}", message = "Phone number must only contain digits")
    private String phone;

    private BigDecimal credit;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }
}
