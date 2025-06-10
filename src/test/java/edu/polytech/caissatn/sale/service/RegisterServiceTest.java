package edu.polytech.caissatn.sale.service;

import edu.polytech.caissatn.exception.BusinessException;
import edu.polytech.caissatn.exception.BusinessExceptionReason;
import edu.polytech.caissatn.exception.NotFoundException;
import edu.polytech.caissatn.sale.dto.RegisterDTO;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.entity.Payment;
import edu.polytech.caissatn.sale.mapper.RegisterMapper;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import edu.polytech.caissatn.sale.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    @Mock private RegisterRepository registerRepository;
    @Mock private RegisterMapper registerMapper;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private RegisterService registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOpenRegister_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setOpeningCash(BigDecimal.valueOf(100));
        Register entity = new Register();
        Register savedEntity = new Register();
        savedEntity.setId(UUID.randomUUID());

        when(registerRepository.findByClosingCashAndEndDate(null, null)).thenReturn(Collections.emptyList());
        when(registerMapper.toEntity(any())).thenReturn(entity);
        when(registerRepository.save(entity)).thenReturn(savedEntity);

        Register result = registerService.open(dto);

        assertNotNull(result);
        verify(registerRepository).save(entity);
    }

    @Test
    void testOpenRegister_AlreadyOpened_ThrowsException() {
        when(registerRepository.findByClosingCashAndEndDate(null, null)).thenReturn(List.of(new Register()));

        RegisterDTO dto = new RegisterDTO();
        dto.setOpeningCash(BigDecimal.TEN);

        BusinessException ex = assertThrows(BusinessException.class, () -> registerService.open(dto));
        assertEquals(BusinessExceptionReason.REGISTER_ALREADY_OPENED, ex.getReason());
    }

    @Test
    void testGetCurrentRegister_Success() {
        Register register = new Register();
        RegisterDTO dto = new RegisterDTO();

        when(registerRepository.findByClosingCashAndEndDate(null, null)).thenReturn(List.of(register));
        when(registerMapper.toDTO(register)).thenReturn(dto);

        RegisterDTO result = registerService.getCurrentRegister();
        assertNotNull(result);
    }

    @Test
    void testGetCurrentRegister_NoRegisterOpened() {
        when(registerRepository.findByClosingCashAndEndDate(null, null)).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class, () -> registerService.getCurrentRegister());
        assertEquals(BusinessExceptionReason.NO_REGISTER_IS_OPENED, ex.getReason());
    }

    @Test
    void testCloseRegister_SuccessWithMultiplePayments() {
        UUID id = UUID.randomUUID();
        RegisterDTO dto = new RegisterDTO();
        dto.setId(id);
        dto.setClosingCash(BigDecimal.valueOf(170));

        Register register = new Register();
        register.setId(id);
        register.setOpeningCash(BigDecimal.valueOf(100));

        List<Payment> payments = new ArrayList<>();
        Payment cashPayment = new Payment();
        cashPayment.setType("cash");
        cashPayment.setAmount(BigDecimal.valueOf(100));
        payments.add(cashPayment);

        Payment refundPayment = new Payment();
        refundPayment.setType("refund_cash");
        refundPayment.setAmount(BigDecimal.valueOf(30));
        payments.add(refundPayment);

        Payment cardPayment = new Payment();
        cardPayment.setType("card");
        cardPayment.setAmount(BigDecimal.valueOf(50));
        payments.add(cardPayment);

        Transaction transaction = new Transaction();
        transaction.setPayments(payments);

        when(registerRepository.findById(id)).thenReturn(Optional.of(register));
        when(transactionRepository.findAllByRegister(register)).thenReturn(List.of(transaction));
        when(registerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Register result = registerService.close(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(170), result.getClosingCash());
        assertEquals(BigDecimal.valueOf(170), result.getExpectedCash()); // 100 + 100 - 30
    }

    @Test
    void testCloseRegister_NotFound() {
        UUID id = UUID.randomUUID();
        RegisterDTO dto = new RegisterDTO();
        dto.setId(id);

        when(registerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> registerService.close(dto));
    }

    @Test
    void testDeleteRegister_Success() {
        UUID id = UUID.randomUUID();

        when(registerRepository.existsById(id)).thenReturn(true);

        registerService.delete(id.toString());

        verify(registerRepository).deleteById(id);
    }

    @Test
    void testDeleteRegister_NotFound() {
        UUID id = UUID.randomUUID();

        when(registerRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> registerService.delete(id.toString()));
    }

    @Test
    void testDeleteRegister_InvalidUUID() {
        String invalidId = "abc123";

        assertThrows(IllegalArgumentException.class, () -> registerService.delete(invalidId));
    }

    @Test
    void testGetAllRegisters_Success() {
        List<Register> registers = List.of(new Register());
        List<RegisterDTO> dtos = List.of(new RegisterDTO());

        Page<Register> registerPage = new PageImpl<>(registers);
        when(registerRepository.findAll(PageRequest.of(0, 10))).thenReturn(registerPage);
        when(registerMapper.toDTO(any())).thenReturn(dtos.get(0));

        Page<RegisterDTO> result = registerService.getAll(0, 10);

        assertEquals(1, result.getContent().size());
    }
}
