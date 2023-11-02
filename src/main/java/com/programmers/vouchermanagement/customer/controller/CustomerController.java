package com.programmers.vouchermanagement.customer.controller;

import com.programmers.vouchermanagement.customer.dto.CustomerDto;
import com.programmers.vouchermanagement.customer.service.CustomerService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<CustomerDto> readAllBlackCustomer() {
        return customerService.readAllBlackCustomer();
    }
}
