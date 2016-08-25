package com.javcode.springboot.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javcode.springboot.dao.CustomerDao;
import com.javcode.springboot.dto.CustomerDto;
import com.javcode.springboot.exception.NotFoundException;
import com.javcode.springboot.jooq.tables.pojos.Customer;

@RestController
@RequestMapping(value = "/customer", produces = APPLICATION_JSON_VALUE)
public class CustomerController {

    private final CustomerDao customerDao;

    @Inject
    public CustomerController(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @RequestMapping(method = POST)
    public CustomerDto saveCustomer(final @RequestBody CustomerDto customerDto) {
        Customer savedCustomer = customerDao.save(customerDto.into(new Customer()));
        return savedCustomer.into(new CustomerDto());
    }

    @RequestMapping(value = "/{id}", method = GET)
    public CustomerDto getCustomerById(final @PathVariable Long id) {
        return customerDao.findById(id)
                    .map(c -> c.into(new CustomerDto()))
                    .orElseThrow(() -> new NotFoundException());
    }
}
