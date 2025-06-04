package edu.polytech.caissatn.sale.service;

import edu.polytech.caissatn.exception.BusinessException;
import edu.polytech.caissatn.exception.BusinessExceptionReason;
import edu.polytech.caissatn.exception.NotFoundException;
import edu.polytech.caissatn.sale.dto.RegisterDTO;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.mapper.RegisterMapper;
import edu.polytech.caissatn.sale.repository.RegisterRepository;
import edu.polytech.caissatn.sale.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@Transactional
public class RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final RegisterRepository registerRepository;
    private final RegisterMapper registerMapper;
    private final TransactionRepository transactionRepository;

    public RegisterService(RegisterRepository registerRepository, RegisterMapper registerMapper, TransactionRepository transactionRepository) {
        this.registerRepository = registerRepository;
        this.registerMapper = registerMapper;
        this.transactionRepository = transactionRepository;
    }

    public Register open(RegisterDTO registerDTO) {
        logger.info("Attempting to open a new register with data: {}", registerDTO);
        List<Register> openRegisters = registerRepository.findByClosingCashAndEndDate(null, null);
        if (!openRegisters.isEmpty()) {
            logger.warn("Attempted to open a register when one is already open.");
            throw new BusinessException(BusinessExceptionReason.REGISTER_ALREADY_OPENED);
        }

        registerDTO.setStartDate(Instant.now());

        // Si openingCash est null, on le met à zéro
        if (registerDTO.getOpeningCash() == null) {
            registerDTO.setOpeningCash(BigDecimal.ZERO);
        }

        Register register = registerMapper.toEntity(registerDTO);
        Register savedRegister = registerRepository.save(register);

        logger.info("Register opened successfully with ID: {}", savedRegister.getId());
        return savedRegister;
    }

    public RegisterDTO getCurrentRegister() {
        logger.debug("Fetching the current open register.");
        List<Register> openRegisters = registerRepository.findByClosingCashAndEndDate(null, null);
        if (openRegisters.isEmpty()) {
            logger.warn("No open register found.");
            throw new BusinessException(BusinessExceptionReason.NO_REGISTER_IS_OPENED);
        }
        RegisterDTO currentRegister = registerMapper.toDTO(openRegisters.get(0));
        logger.debug("Current open register found: {}", currentRegister);
        return currentRegister;
    }


    public Register close(RegisterDTO registerDTO) {
        logger.info("Attempting to close register with data: {}", registerDTO);

        // 1. Récupérer le registre en base par son ID (évite de créer une nouvelle entité détachée)
        UUID registerId = registerDTO.getId();
        Register register = registerRepository.findById(registerId)
                .orElseThrow(() -> new NotFoundException("Register not found with ID: " + registerId));

        // 2. Mettre à jour la date de fin et le cash de fermeture
        register.setEndDate(Instant.now());
        register.setClosingCash(registerDTO.getClosingCash());

        // 3. Récupérer les transactions liées à ce registre
        List<Transaction> transactions = transactionRepository.findAllByRegister(register);

        // 4. Calculer la somme des paiements en cash
        BigDecimal totalCashPayments = transactions.stream()
                .flatMap(transaction -> transaction.getPayments().stream())
                .filter(payment -> "cash".equals(payment.getType()))
                .map(payment -> new BigDecimal(payment.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Calculer la somme des remboursements en cash
        BigDecimal totalRefundCashPayments = transactions.stream()
                .flatMap(transaction -> transaction.getPayments().stream())
                .filter(payment -> "refund_cash".equals(payment.getType()))
                .map(payment -> new BigDecimal(payment.getAmount().toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Calculer le cash attendu
        BigDecimal expectedCash = register.getOpeningCash()
                .add(totalCashPayments)
                .subtract(totalRefundCashPayments);

        // 7. Mettre à jour le cash attendu dans le registre
        register.setExpectedCash(expectedCash);

        // 8. Sauvegarder le registre mis à jour
        Register closedRegister = registerRepository.save(register);

        logger.info("Register closed successfully with ID: {}", closedRegister.getId());
        return closedRegister;
    }


    public void delete(String id) {
        logger.info("Attempting to delete register with ID: {}", id);
        if (!registerRepository.existsById(UUID.fromString(id))) {
            logger.warn("Register with ID {} not found.", id);
            throw new NotFoundException("Register with ID " + id + " not found.");
        }
        registerRepository.deleteById(UUID.fromString(id));
        logger.info("Register deleted successfully with ID: {}", id);
    }

    public Page<RegisterDTO> getAll(int page, int size) {
        logger.debug("Fetching all registers with page: {} and size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Register> registerPage = registerRepository.findAll(pageable);

        Page<RegisterDTO> result = registerPage.map(registerMapper::toDTO);
        logger.debug("Successfully fetched {} registers.", result.getContent().size());
        return result;
    }
}