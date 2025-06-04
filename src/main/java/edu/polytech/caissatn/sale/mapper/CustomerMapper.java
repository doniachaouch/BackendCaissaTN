package edu.polytech.caissatn.sale.mapper;



import edu.polytech.caissatn.sale.dto.CustomerDTO;
import edu.polytech.caissatn.sale.entity.Customer;
import edu.polytech.caissatn.sale.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    private final CustomerRepository customerRepository;

    public CustomerMapper(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        Customer customer = customerDTO.getId() != null ? this.customerRepository.findById(customerDTO.getId()).get() : new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPhone(customerDTO.getPhone());
        customer.setCredit(customerDTO.getCredit());

        return customer;
    }

    public List<Customer> toEntities(List<CustomerDTO> customerDTOs) {
        if (customerDTOs.isEmpty()) {
            return null;
        }
        return customerDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setCredit(customer.getCredit());

        return customerDTO;
    }

    public List<CustomerDTO> toDTOs(List<Customer> customers) {
        if (customers.isEmpty()) {
            return null;
        }
        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}

