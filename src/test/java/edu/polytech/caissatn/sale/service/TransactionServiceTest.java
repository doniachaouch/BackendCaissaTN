package edu.polytech.caissatn.sale.service;

import edu.polytech.caissatn.exception.BusinessException;
import edu.polytech.caissatn.inventory.entity.Product;
import edu.polytech.caissatn.inventory.repository.ProductRepository;
import edu.polytech.caissatn.sale.dto.TransactionDTO;
import edu.polytech.caissatn.sale.entity.Customer;
import edu.polytech.caissatn.sale.entity.Payment;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.entity.TransactionItem;
import edu.polytech.caissatn.sale.mapper.TransactionMapper;
import edu.polytech.caissatn.sale.repository.CustomerRepository;
import edu.polytech.caissatn.sale.repository.PaymentRepository;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import edu.polytech.caissatn.sale.repository.TransactionItemRepository;
import edu.polytech.caissatn.sale.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionItemRepository transactionItemRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RegisterRepository registerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionDTO transactionDTO;
    private Transaction transaction;
    private Register openRegister;
    private Product product;
    private Customer customer;
    private Payment payment;
    private TransactionItem transactionItem;

    @BeforeEach
    void setUp() {
        // Setup open register
        openRegister = new Register();
        openRegister.setId(UUID.randomUUID());
        openRegister.setClosingCash(null);
        openRegister.setEndDate(null);

        // Setup product
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setStockQty(10);

        // Setup customer
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setCredit(BigDecimal.valueOf(100));

        // Setup transaction item
        transactionItem = new TransactionItem();
        transactionItem.setId(UUID.randomUUID());
        transactionItem.setProduct(product);
        transactionItem.setQty(2);

        // Setup payment
        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setType("cash");
        payment.setAmount(BigDecimal.valueOf(50));

        // Setup transaction DTO
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(UUID.randomUUID());
        transactionDTO.setRegister(openRegister.getId());
        transactionDTO.setTotal(BigDecimal.valueOf(50));

        // Setup transaction entity
        transaction = new Transaction();
        transaction.setId(transactionDTO.getId());
        transaction.setRegister(openRegister);
        transaction.setTotal(BigDecimal.valueOf(50));
        transaction.setItems(Arrays.asList(transactionItem));
        transaction.setPayments(Arrays.asList(payment));
        transaction.setCustomer(customer);
    }

    @Test
    void save_ShouldSaveTransaction_WhenValidTransactionProvided() {
        // Given
        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.save(transactionDTO);

        // Then
        assertNotNull(result);
        assertEquals(transaction.getId(), result.getId());
        assertNotNull(transactionDTO.getDate());

        verify(transactionRepository).save(transaction);
        verify(transactionItemRepository).saveAll(transaction.getItems());
        verify(paymentRepository).saveAll(transaction.getPayments());
        verify(productRepository).save(product);

        // Verify stock was decremented
        assertEquals(8, product.getStockQty());
    }

    @Test
    void save_ShouldThrowException_WhenNoOpenRegister() {
        // Given
        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Collections.emptyList());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.save(transactionDTO));

        assertNotNull(exception);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenRegisterMismatch() {
        // Given
        Register differentRegister = new Register();
        differentRegister.setId(UUID.randomUUID());
        transaction.setRegister(differentRegister);

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.save(transactionDTO));

        assertNotNull(exception);
    }

    @Test
    void save_ShouldThrowException_WhenTransactionRegisterIsNull() {
        // Given
        transaction.setRegister(null);

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> transactionService.save(transactionDTO));

        assertNotNull(exception);
    }

    @Test
    void save_ShouldIncrementStock_WhenRefundTransaction() {
        // Given
        Transaction refundedTransaction = new Transaction();
        refundedTransaction.setId(UUID.randomUUID());
        transaction.setRefund(refundedTransaction);

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.save(transactionDTO);

        // Then
        // Stock should be incremented for refund
        assertEquals(12, product.getStockQty());
        verify(productRepository).save(product);
    }

    @Test
    void save_ShouldHandleCreditPayment() {
        // Given
        Payment creditPayment = new Payment();
        creditPayment.setType("credit");
        creditPayment.setAmount(BigDecimal.valueOf(25));
        transaction.setPayments(Arrays.asList(creditPayment));

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.save(transactionDTO);

        // Then
        assertEquals(BigDecimal.valueOf(125), customer.getCredit());
        verify(customerRepository).save(customer);
    }

    @Test
    void save_ShouldHandleRefundCreditPayment() {
        // Given
        Payment refundCreditPayment = new Payment();
        refundCreditPayment.setType("refund_credit");
        refundCreditPayment.setAmount(BigDecimal.valueOf(30));
        transaction.setPayments(Arrays.asList(refundCreditPayment));

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.save(transactionDTO);

        // Then
        assertEquals(BigDecimal.valueOf(70), customer.getCredit());
        verify(customerRepository).save(customer);
    }

    @Test
    void save_ShouldHandleTransactionWithoutItems() {
        // Given
        transaction.setItems(null);

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.save(transactionDTO);

        // Then
        assertNotNull(result);
        verify(transactionItemRepository, never()).saveAll(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void save_ShouldHandleTransactionWithoutPayments() {
        // Given
        transaction.setPayments(null);

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.save(transactionDTO);

        // Then
        assertNotNull(result);
        verify(paymentRepository, never()).saveAll(any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void save_ShouldHandleItemWithoutProduct() {
        // Given
        TransactionItem itemWithoutProduct = new TransactionItem();
        itemWithoutProduct.setProduct(null);
        transaction.setItems(Arrays.asList(itemWithoutProduct));

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.save(transactionDTO);

        // Then
        assertNotNull(result);
        verify(productRepository, never()).save(any());
    }

    @Test
    void save_ShouldHandleTransactionWithoutCustomer() {
        // Given
        transaction.setCustomer(null);
        Payment creditPayment = new Payment();
        creditPayment.setType("credit");
        creditPayment.setAmount(BigDecimal.valueOf(25));
        transaction.setPayments(Arrays.asList(creditPayment));

        when(registerRepository.findByClosingCashAndEndDate(null, null))
                .thenReturn(Arrays.asList(openRegister));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.save(transactionDTO);

        // Then
        assertNotNull(result);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteTransaction() {
        // Given
        String transactionId = UUID.randomUUID().toString();

        // When
        transactionService.delete(transactionId);

        // Then
        verify(transactionRepository).deleteById(UUID.fromString(transactionId));
    }

    @Test
    void getByRegister_ShouldReturnPagedTransactions() {
        // Given
        String registerId = openRegister.getId().toString();
        int page = 0;
        int size = 10;

        List<Transaction> transactions = Arrays.asList(transaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(page, size), 1);

        when(registerRepository.findById(UUID.fromString(registerId)))
                .thenReturn(Optional.of(openRegister));
        when(transactionRepository.findAllByRegister(eq(openRegister), any(Pageable.class)))
                .thenReturn(transactionPage);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // When
        Page<TransactionDTO> result = transactionService.getByRegister(registerId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(transactionDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getByRegister_ShouldHandleNonExistentRegister() {
        // Given
        String registerId = UUID.randomUUID().toString();
        int page = 0;
        int size = 10;

        when(registerRepository.findById(UUID.fromString(registerId)))
                .thenReturn(Optional.empty());
        when(transactionRepository.findAllByRegister(eq(null), any(Pageable.class)))
                .thenReturn(Page.empty());

        // When
        Page<TransactionDTO> result = transactionService.getByRegister(registerId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}