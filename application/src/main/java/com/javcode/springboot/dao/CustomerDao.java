package com.javcode.springboot.dao;


import static com.javcode.springboot.jooq.tables.Customer.CUSTOMER;

import java.util.List;
import java.util.Optional;

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
        customerRecord.changed(CUSTOMER.ID, false);
        customerRecord.from(customer);
        customerRecord.store();
        return customerRecord.into(new Customer());
    }

    public List<Customer> getAll() {
        return dslContext.selectFrom(CUSTOMER)
                .fetchInto(Customer.class);
    }

    public Optional<Customer> findById(Long customerId) {
        return Optional.ofNullable(dslContext.selectFrom(CUSTOMER)
.where(CUSTOMER.ID.eq(customerId))
                .fetchOneInto(Customer.class));
    }

    public List<Customer> findByName(String name) {
        return dslContext.selectFrom(CUSTOMER)
                .where(CUSTOMER.NAME.contains(name))
                .fetchInto(Customer.class);
    }

    public void update(final Customer customer) {
        CustomerRecord updatingCustomer = customer.into(dslContext.newRecord(CUSTOMER));
        dslContext.update(CUSTOMER)
                .set(updatingCustomer)
                .where(CUSTOMER.ID.eq(customer.getId()))
                .execute();
    }

    public int delete(final Long customerId) {
        return dslContext.deleteFrom(CUSTOMER)
            .where(CUSTOMER.ID.eq(customerId))
            .execute();
    }
}
