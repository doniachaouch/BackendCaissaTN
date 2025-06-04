package edu.polytech.caissatn.sale.mapper;

import edu.polytech.caissatn.sale.dto.TransactionDTO;
import edu.polytech.caissatn.sale.entity.Payment;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.entity.TransactionItem;
import edu.polytech.caissatn.sale.repository.CustomerRepository;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import edu.polytech.caissatn.sale.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    private final TransactionRepository transactionRepository;
    private final TransactionItemMapper transactionItemMapper;
    private final PaymentMapper paymentMapper;
    private final CustomerRepository customerRepository;
    private final RegisterRepository registerRepository;

    public TransactionMapper(TransactionRepository transactionRepository,
                             TransactionItemMapper transactionItemMapper,
                             PaymentMapper paymentMapper,
                             CustomerRepository customerRepository,
                             RegisterRepository registerRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemMapper = transactionItemMapper;
        this.paymentMapper = paymentMapper;
        this.customerRepository = customerRepository;
        this.registerRepository = registerRepository;
    }

    public Transaction toEntity(TransactionDTO dto) {
        if (dto == null) return null;

        Transaction entity = dto.getId() != null
                ? transactionRepository.findById(dto.getId()).orElse(new Transaction())
                : new Transaction();

        entity.setCustomer(dto.getCustomer() != null
                ? customerRepository.findById(dto.getCustomer()).orElse(null)
                : null);

        entity.setRegister(dto.getRegister() != null
                ? registerRepository.findById(dto.getRegister()).orElse(null)
                : null);

        List<TransactionItem> items = transactionItemMapper.toEntities(dto.getItems());
        items.forEach(item -> item.setTransaction(entity));  // Lien bi-directionnel important
        entity.setItems(items);

        List<Payment> payments = paymentMapper.toEntities(dto.getPayments());
        payments.forEach(payment -> payment.setTransaction(entity));  // idem
        entity.setPayments(payments);

        entity.setDate(dto.getDate());
        entity.setTotal(dto.getTotal());

        entity.setRefund(dto.getRefund() != null
                ? transactionRepository.findById(dto.getRefund()).orElse(null)
                : null);

        return entity;
    }

    public List<Transaction> toEntities(List<TransactionDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public TransactionDTO toDTO(Transaction entity) {
        if (entity == null) return null;

        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setCustomer(entity.getCustomer() != null ? entity.getCustomer().getId() : null);
        dto.setRegister(entity.getRegister() != null ? entity.getRegister().getId() : null);
        dto.setItems(transactionItemMapper.toDTOs(entity.getItems()));
        dto.setPayments(paymentMapper.toDTOs(entity.getPayments()));
        dto.setDate(entity.getDate());
        dto.setTotal(entity.getTotal());
        dto.setRefund(entity.getRefund() != null ? entity.getRefund().getId() : null);

        return dto;
    }

    public List<TransactionDTO> toDTOs(List<Transaction> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
