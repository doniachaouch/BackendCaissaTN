package edu.polytech.caissatn.sale.service;


import edu.polytech.caissatn.sale.dto.CustomerDTO;
import edu.polytech.caissatn.sale.entity.Customer;
import edu.polytech.caissatn.sale.mapper.CustomerMapper;
import edu.polytech.caissatn.sale.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public Customer save(CustomerDTO customerDTO) {
        logger.info("Attempting to save customer: {}", customerDTO);
        try {
            Customer customer = customerMapper.toEntity(customerDTO);
            Customer savedCustomer = customerRepository.save(customer);
            logger.debug("Customer saved successfully: {}", savedCustomer);
            return savedCustomer;
        } catch (Exception e) {
            logger.error("Error occurred while saving customer: {}", customerDTO, e);
            return null;
        }
    }


    public void delete(String id) {
        logger.info("Attempting to delete customer with id: {}", id);
        try {
            customerRepository.deleteById(UUID.fromString(id));
            logger.debug("Customer deleted successfully with id: {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while deleting customer with id: {}", id, e);
        }
    }

    public List<CustomerDTO> getAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toDTOs(customers);
    }
}
