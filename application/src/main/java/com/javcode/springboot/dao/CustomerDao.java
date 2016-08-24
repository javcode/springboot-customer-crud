package com.javcode.springboot.dao;


import static com.javcode.springboot.jooq.tables.Customer.CUSTOMER;

import java.util.List;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.javcode.springboot.jooq.tables.pojos.Customer;
import com.javcode.springboot.jooq.tables.records.CustomerRecord;


@Repository
public class CustomerDao {

    private final DSLContext dslContext;

    @Inject
    public CustomerDao(final DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Customer save(Customer customer) {
        final CustomerRecord customerRecord = dslContext.newRecord(CUSTOMER);
        customerRecord.from(customer);
        customerRecord.store();
        return customerRecord.into(new Customer());
    }

    public List<Customer> findById(String customerId) {
        return dslContext.selectFrom(CUSTOMER)
                .where(CUSTOMER.ID.equal(customerId))
                .fetchInto(Customer.class);
    }

    public Customer insertOrUpdate(final Customer customer) {
        if (customer.getId() == null) {
            return insert(customer);
        } else {
            return update(customer);
        }
    }

    private Customer insert(final Customer customer) {
        CustomerRecord newCustomer = customer.into(dslContext.newRecord(CUSTOMER));
        newCustomer.changed(CUSTOMER.ID, false);
        return dslContext.insertInto(CUSTOMER)
                .set(newCustomer)
                .returning()
                .fetchOne()
                .into(new Customer());
    }

    private Customer update(final Customer customer) {
        CustomerRecord updatingCustomer = customer.into(dslContext.newRecord(CUSTOMER));
        return dslContext.update(CUSTOMER)
                .set(updatingCustomer)
                .where(CUSTOMER.ID.eq(customer.getId()))
                .returning()
                .fetchOne()
                .into(new Customer());
    }

}
