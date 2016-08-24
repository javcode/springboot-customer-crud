package com.javcode.springboot.dao;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javcode.springboot.jooq.tables.pojos.Customer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestPersistenceConfiguration.class)
public class CustomerDaoTest {

    @Inject
    private CustomerDao dao;

    @Inject
    private JdbcTemplate template;

    @Before
    public void setup() {
        this.template.execute("delete from customer;");
    }

    @Test
    public void saveShouldStoreCustomerOnDB() {
        Customer customer = new Customer()
                .setId("id")
                .setAddress("queen street 12")
                .setName("Random")
                .setPhone("0221123123");
        Customer savedCustomer = dao.save(customer);

        assertNotNull(savedCustomer);
        assertThat(savedCustomer.getId(), is("id"));

        List<Map<String, Object>> dbResult = this.template.queryForList(
                "SELECT ID, NAME, ADDRESS, PHONE FROM customer WHERE ID = ?", savedCustomer.getId());
        assertNotNull(dbResult);
        assertThat(dbResult, hasSize(1));
        assertThat(dbResult.get(0), hasEntry("ID", customer.getId()));
    }
}
