package edu.polytech.caissatn.sale.controller;


import edu.polytech.caissatn.sale.dto.CustomerDTO;
import edu.polytech.caissatn.sale.entity.Customer;
import edu.polytech.caissatn.sale.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CustomerDTO> open(@Valid @RequestBody CustomerDTO customerDTO) {
        logger.info("Received request to save customer: {}", customerDTO);
        try {
            Customer customer = customerService.save(customerDTO);
            customerDTO.setId(customer.getId());
            logger.info("Customer saved successfully with id: {}", customerDTO.getId());
            return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
        } catch (Exception e) {
            logger.error("Failed to save customer: {}", customerDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Handle errors gracefully without app failure
        }
    }


    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void>  delete(@PathVariable String id) {
        logger.info("Received request to delete customer with id: {}", id);
        try {
            customerService.delete(id);
            logger.info("Customer deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete customer with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CustomerDTO>> getAll() {
        return ResponseEntity.ok(this.customerService.getAll());
    }
}