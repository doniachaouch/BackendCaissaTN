package edu.polytech.caissatn.sale.dto;


import edu.polytech.caissatn.sale.validator.BalanceAssertion;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@BalanceAssertion
public class TransactionDTO {
    private UUID id;

    @NotNull
    private UUID register;

    private List<TransactionItemDTO> items;

    @NotEmpty
    private List<PaymentDTO> payments;

    private UUID customer;

    private Instant date;

    @Positive
    private BigDecimal total;

    private UUID refund;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRegister() {
        return register;
    }

    public void setRegister(UUID register) {
        this.register = register;
    }

    public List<TransactionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }

    public UUID getCustomer() {
        return customer;
    }

    public void setCustomer(UUID customer) {
        this.customer = customer;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public UUID getRefund() {
        return refund;
    }

    public void setRefund(UUID refund) {
        this.refund = refund;
    }
}
