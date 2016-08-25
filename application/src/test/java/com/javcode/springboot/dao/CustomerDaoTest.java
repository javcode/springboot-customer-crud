package com.javcode.springboot.dao;

import static com.javcode.springboot.jooq.tables.Customer.CUSTOMER;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableMap.Builder;
import com.javcode.springboot.jooq.tables.pojos.Customer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestPersistenceConfiguration.class)
public class CustomerDaoTest {

    @Inject
    private CustomerDao dao;

    @Inject
    private JdbcTemplate template;

    @Inject
    private DSLContext dslContext;

    @Before
    public void setup() {
        this.template.execute("delete from customer;");
    }

    @Test
    public void saveShouldStoreCustomerOnDB() {
        Customer customer = new Customer()
                .setAddress("queen street 12")
                .setName("Random")
                .setPhone("0221123123");
        Customer savedCustomer = dao.save(customer);

        assertNotNull(savedCustomer);

        List<Map<String, Object>> dbResult = this.template.queryForList(
                "SELECT ID, NAME, ADDRESS, PHONE FROM customer WHERE ID = ?", savedCustomer.getId());
        assertNotNull(dbResult);
        assertThat(dbResult, hasSize(1));
        assertThat(dbResult.get(0), hasEntry("ID", savedCustomer.getId()));
        assertThat(dbResult.get(0), hasEntry("NAME", "Random"));
    }

    @Test
    public void getByIdShouldReturnCustomerInDB() {
        Long savedId = insertCustomer("customer", "address", "12301923");

        Optional<Customer> result = dao.findById(savedId);
        assertThat(result.isPresent(), equalTo(true));
        Customer customer = result.get();
        assertThat(customer.getName(), is("customer"));
        assertThat(customer.getAddress(), is("address"));
        assertThat(customer.getPhone(), is("12301923"));
    }

    @Test
    public void getByNameShouldReturnCustomerMatchingByName() {
        insertCustomer("noname", "address1", "12301923");
        insertCustomer("javier", "queen st", "06546578");
        insertCustomer("analia", "fort st2", "44566789");

        List<Customer> result = dao.findByName("javier");
        assertNotNull(result);
        assertThat(result, hasSize(1));
        Customer customer = result.get(0);
        assertThat(customer.getName(), is("javier"));
        assertThat(customer.getAddress(), is("queen st"));
        assertThat(customer.getPhone(), is("06546578"));
    }

    @Test
    public void getByNameShouldReturnMultipleCustomersMatchingByName() {
        insertCustomer("noname", "address1", "12301923");
        insertCustomer("javier", "queen st", "06546578");
        insertCustomer("jacinto", "fort st2", "44566789");

        List<Customer> result = dao.findByName("ja");
        assertNotNull(result);
        assertThat(result, hasSize(2));

        Customer customer1 = result.get(0);
        assertThat(customer1.getName(), is("javier"));
        assertThat(customer1.getAddress(), is("queen st"));
        assertThat(customer1.getPhone(), is("06546578"));

        Customer customer2 = result.get(1);
        assertThat(customer2.getName(), is("jacinto"));
        assertThat(customer2.getAddress(), is("fort st2"));
        assertThat(customer2.getPhone(), is("44566789"));
    }

    @Test
    public void getByNameShouldReturnEmptyListWhenNoCustomerMatchesByName() {
        insertCustomer("noname", "address1", "12301923");
        insertCustomer("javier", "queen st", "06546578");
        insertCustomer("analia", "fort st2", "44566789");

        List<Customer> result = dao.findByName("no match");
        assertNotNull(result);
        assertThat(result, hasSize(0));
    }

    @Test
    public void updateShouldSetTheNewFieldsInTheDB() {
        Long savedId = insertCustomer("javier", "queen st", "06546578");

        Optional<Customer> result = dao.findById(savedId);
        assertThat(result.isPresent(), equalTo(true));
        Customer customer = result.get();
        customer.setName("javier durante");
        dao.update(customer);

        Customer updatedCustomer = this.template.queryForObject(
                "SELECT ID, NAME, ADDRESS, PHONE FROM customer WHERE ID = ?", new Object[] { savedId },
                (rs, rowNum) -> {
                    return new Customer()
                            .setId(rs.getLong("ID"))
                            .setName(rs.getString("NAME"))
                            .setAddress(rs.getString("ADDRESS"))
                            .setPhone(rs.getString("PHONE"));
                });
        assertNotNull(updatedCustomer);
    }

    @SuppressWarnings("rawtypes")
    private long insertCustomer(String name, String address, String phone) {
        final Builder<Field, Object> paramBuilder = QueryUtil.paramBuilder()
                .put(CUSTOMER.NAME, name)
                .put(CUSTOMER.ADDRESS, address)
                .put(CUSTOMER.PHONE, phone);
        return QueryUtil.insert(dslContext, CUSTOMER, paramBuilder.build());
    }
}
