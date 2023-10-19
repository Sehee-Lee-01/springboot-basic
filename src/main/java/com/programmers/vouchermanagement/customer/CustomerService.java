package com.programmers.vouchermanagement.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> readBlacklist() {
        List<Customer> blacklist = customerRepository.findBlackCustomers();
        //TODO: logger to inform no black customers exists
        if (blacklist.isEmpty()) {
            throw new NoSuchElementException("no blacklist");
        }

        return blacklist;
    }
}
