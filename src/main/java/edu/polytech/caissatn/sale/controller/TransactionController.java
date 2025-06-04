package edu.polytech.caissatn.sale.controller;

import edu.polytech.caissatn.sale.dto.TransactionDTO;
import edu.polytech.caissatn.sale.entity.Transaction;
import edu.polytech.caissatn.sale.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionDTO> save(@Valid @RequestBody TransactionDTO transactionDTO) {
        logger.info("Received request to save transaction: {}", transactionDTO);
        try {
            Transaction transaction = transactionService.save(transactionDTO);
            transactionDTO.setId(transaction.getId());
            logger.info("Transaction saved successfully with id: {}", transactionDTO.getId());
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        } catch (Exception e) {
            logger.error("Failed to save transaction: {}", transactionDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
