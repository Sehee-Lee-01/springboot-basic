package com.programmers.vouchermanagement.customer.service;

import com.programmers.vouchermanagement.customer.domain.Customer;
import com.programmers.vouchermanagement.customer.dto.CreateCustomerRequest;
import com.programmers.vouchermanagement.customer.dto.CustomerResponse;
import com.programmers.vouchermanagement.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void create(CreateCustomerRequest createCustomerRequest) {
        Customer customer = new Customer(UUID.randomUUID(), createCustomerRequest.name(), createCustomerRequest.isBlack());
        customerRepository.insert(customer);
    }

    public List<CustomerResponse> readAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            return Collections.emptyList();
        }
        return customers.stream().map(CustomerResponse::from).toList();
    }

    public List<CustomerResponse> readAllBlackCustomer() {
        List<Customer> blacklist = customerRepository.findAllBlackCustomer();
        if (blacklist.isEmpty()) {
            return Collections.emptyList();
        }
        return blacklist.stream().map(CustomerResponse::from).toList();
    }
}
