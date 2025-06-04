package edu.polytech.caissatn.sale.controller;


import edu.polytech.caissatn.exception.BusinessException;
import edu.polytech.caissatn.sale.dto.RegisterDTO;
import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.service.RegisterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }


    @PostMapping("/open")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RegisterDTO> open(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("Received request to open a register: {}", registerDTO);
        Register register = registerService.open(registerDTO);
        registerDTO.setId(register.getId());
        return ResponseEntity.status(HttpStatus.OK).body(registerDTO);
    }

    @PostMapping("/close")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RegisterDTO> close(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("Received request to close a register: {}", registerDTO);
        Register register = registerService.close(registerDTO);
        registerDTO.setId(register.getId());
        registerDTO.setExpectedCash(register.getExpectedCash());
        return ResponseEntity.status(HttpStatus.OK).body(registerDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        logger.info("Received request to delete register with ID: {}", id);
        registerService.delete(id);
    }

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<RegisterDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all registers with page: {} and size: {}", page, size);
        Page<RegisterDTO> result = registerService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/current")
    public ResponseEntity<RegisterDTO> getCurrentRegister() {
        try {
            RegisterDTO currentRegister = registerService.getCurrentRegister();
            return ResponseEntity.ok(currentRegister);
        } catch (BusinessException e) {
            return ResponseEntity.notFound().build(); // Renvoie 404 si pas de caisse ouverte
        }
    }
}