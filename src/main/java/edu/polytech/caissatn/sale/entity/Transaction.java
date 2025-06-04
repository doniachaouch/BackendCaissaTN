package edu.polytech.caissatn.sale.entity;


import edu.polytech.caissatn.audit.entity.AuditableEntity;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "register_id")
    private Register register;

    @OneToMany(mappedBy = "transaction")
    private List<TransactionItem> items;

    @OneToMany(mappedBy = "transaction")
    private List<Payment> payments;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "refund_id")
    private Transaction refund;

    private Instant date;

    private BigDecimal total;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public void setItems(List<TransactionItem> items) {
        this.items = items;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
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

    public Transaction getRefund() {
        return refund;
    }

    public void setRefund(Transaction refund) {
        this.refund = refund;
    }
}
