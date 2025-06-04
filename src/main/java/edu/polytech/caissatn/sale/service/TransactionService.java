package edu.polytech.caissatn.sale.service;

import edu.polytech.caissatn.exception.BusinessException;
import edu.polytech.caissatn.inventory.entity.Product;
import edu.polytech.caissatn.inventory.repository.ProductRepository;
import edu.polytech.caissatn.sale.dto.TransactionDTO;
import edu.polytech.caissatn.sale.entity.Payment;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.entity.TransactionItem;
import edu.polytech.caissatn.sale.mapper.TransactionMapper;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import edu.polytech.caissatn.sale.repository.PaymentRepository;
import edu.polytech.caissatn.sale.repository.CustomerRepository;
import edu.polytech.caissatn.sale.repository.TransactionItemRepository;
import edu.polytech.caissatn.sale.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static edu.polytech.caissatn.exception.BusinessExceptionReason.NOT_OPENED_REGISTER;
import static edu.polytech.caissatn.exception.BusinessExceptionReason.NO_REGISTER_IS_OPENED;

@Service
@Transactional
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final TransactionMapper transactionMapper;
    private final RegisterRepository registerRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionItemRepository transactionItemRepository,
                              TransactionMapper transactionMapper,
                              RegisterRepository registerRepository,
                              PaymentRepository paymentRepository,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionMapper = transactionMapper;
        this.registerRepository = registerRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public Transaction save(TransactionDTO transactionDTO) {
        logger.info("Starting to save transaction with DTO: {}", transactionDTO);

        // Cherche un registre ouvert (null sur closingCash et endDate)
        List<Register> openRegisters = registerRepository.findByClosingCashAndEndDate(null, null);
        if (openRegisters.isEmpty()) {
            logger.warn("No open register found, transaction cannot be saved.");
            throw new BusinessException(NO_REGISTER_IS_OPENED);
        }
        Register openRegister = openRegisters.get(0);

        transactionDTO.setDate(Instant.now());

        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        // IMPORTANT : vérifier si register est non nul avant de comparer
        if (transaction.getRegister() == null || !transaction.getRegister().getId().equals(openRegister.getId())) {
            logger.warn("Register mismatch: transaction's register {} is not open register {}",
                    transaction.getRegister() != null ? transaction.getRegister().getId() : "null",
                    openRegister.getId());
            throw new BusinessException(NOT_OPENED_REGISTER);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.debug("Transaction saved successfully with ID: {}", savedTransaction.getId());

        // Traiter les items
        processTransactionItems(savedTransaction);

        // Traiter les paiements et crédits client
        processPaymentsAndCredit(savedTransaction);

        return savedTransaction;
    }

    private void processTransactionItems(Transaction savedTransaction) {
        List<TransactionItem> items = savedTransaction.getItems();
        if (items == null || items.isEmpty()) {
            logger.warn("No transaction items to process.");
            return;
        }
        items.forEach(item -> {
            item.setTransaction(savedTransaction);
            Product product = item.getProduct();
            if (product == null) {
                logger.warn("Transaction item has no product, skipping stock update.");
                return;
            }
            if (savedTransaction.getRefund() != null) {
                product.setStockQty(product.getStockQty() + item.getQty());
            } else {
                product.setStockQty(product.getStockQty() - item.getQty());
            }
            productRepository.save(product);
            logger.trace("Updated stock for product ID: {}", product.getId());
        });
        transactionItemRepository.saveAll(items);
        logger.trace("Transaction items saved.");
    }

    private void processPaymentsAndCredit(Transaction savedTransaction) {
        List<Payment> payments = savedTransaction.getPayments();
        if (payments == null || payments.isEmpty()) {
            logger.warn("No payments to process.");
            return;
        }
        payments.forEach(payment -> payment.setTransaction(savedTransaction));
        paymentRepository.saveAll(payments);

        BigDecimal credit = payments.stream()
                .filter(payment -> "credit".equals(payment.getType()))
                .map(payment -> new BigDecimal(payment.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (credit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentCredit = savedTransaction.getCustomer() != null ? savedTransaction.getCustomer().getCredit() : BigDecimal.ZERO;
            if (savedTransaction.getCustomer() != null) {
                savedTransaction.getCustomer().setCredit(currentCredit.add(credit));
                customerRepository.save(savedTransaction.getCustomer());
                logger.info("Credit updated for customer ID: {}", savedTransaction.getCustomer().getId());
            }
        }

        BigDecimal refundCredit = payments.stream()
                .filter(payment -> "refund_credit".equals(payment.getType()))
                .map(payment -> new BigDecimal(payment.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (refundCredit.compareTo(BigDecimal.ZERO) > 0 && savedTransaction.getCustomer() != null) {
            BigDecimal currentCredit = savedTransaction.getCustomer().getCredit();
            savedTransaction.getCustomer().setCredit(currentCredit.subtract(refundCredit));
            customerRepository.save(savedTransaction.getCustomer());
            logger.info("Refund credit updated for customer ID: {}", savedTransaction.getCustomer().getId());
        }
    }

    public void delete(String id) {
        transactionRepository.deleteById(UUID.fromString(id));
    }

    public Page<TransactionDTO> getByRegister(String id, int page, int size) {
        Register register = registerRepository.findById(UUID.fromString(id)).orElse(null);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionsPage = transactionRepository.findAllByRegister(register, pageable);
        return transactionsPage.map(transactionMapper::toDTO);
    }
}
