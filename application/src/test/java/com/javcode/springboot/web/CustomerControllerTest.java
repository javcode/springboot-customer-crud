package com.javcode.springboot.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javcode.springboot.dao.CustomerDao;
import com.javcode.springboot.dto.CustomerDto;
import com.javcode.springboot.jooq.tables.pojos.Customer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CustomerControllerTest.Config.class)
@WebAppConfiguration
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CustomerControllerTest {

    @Configuration
    @Import(ControllerTestConfig.class)
    public static class Config {

        @Mock
        private CustomerDao customerDao;

        public Config() {
            MockitoAnnotations.initMocks(this);
        }

        @Bean
        public CustomerDao customerDao() {
            return customerDao;
        }
    }

    @Inject
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Inject
    private CustomerDao customerDao;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void saveShouldStoreCustomer() throws Exception {
        CustomerDto customerDto = new CustomerDto()
            .setName("name")
            .setAddress("address")
            .setPhone("02121");
        Long customerId = 10L;

        String jsonContent = new ObjectMapper().writeValueAsString(customerDto);

        when(customerDao.save(Matchers.any())).thenAnswer(invocation -> {
            Customer customer = invocation.getArgumentAt(0, Customer.class);
            customer.setId(customerId);
            return customer;
        });

        mockMvc.perform(post("/customers")
                    .contentType(APPLICATION_JSON)
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(customerId.intValue())))
                .andExpect(jsonPath("name", is("name")))
                .andExpect(jsonPath("address", is("address")))
                .andExpect(jsonPath("phone", is("02121")));
    }

    @Test
    public void getCustomerShouldRetrieveAllCustomersFromDB() throws Exception {
        CustomerDto customerDto1 = new CustomerDto()
            .setId(10L)
            .setName("name")
            .setAddress("address")
            .setPhone("02121");
        CustomerDto customerDto2 = new CustomerDto()
            .setId(22L)
            .setName("name 2")
            .setAddress("address 2")
            .setPhone("021212");

        when(customerDao.getAll()).thenReturn(Arrays.asList(customerDto1, customerDto2).stream()
                .map(c -> c.into(new Customer()))
                .collect(Collectors.toList()));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(customerDto1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(customerDto1.getName())))
                .andExpect(jsonPath("$[0].address", is(customerDto1.getAddress())))
                .andExpect(jsonPath("$[0].phone", is(customerDto1.getPhone())))

                .andExpect(jsonPath("$[1].id", is(customerDto2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(customerDto2.getName())))
                .andExpect(jsonPath("$[1].address", is(customerDto2.getAddress())))
                .andExpect(jsonPath("$[1].phone", is(customerDto2.getPhone())));
    }

    @Test
    public void getCustomeByIdShouldReturnSingleCustomer() throws Exception {
        Long customerId = 10L;
        Customer customer = new Customer()
            .setId(customerId)
            .setName("name")
            .setAddress("address")
            .setPhone("02121");

        when(customerDao.findById(customerId)).thenReturn(Optional.of(customer));

        mockMvc.perform(get(String.format("/customers/%s", customerId)))
                .andExpect(status().isOk());
    }

    @Test
    public void getCustomeByIdShouldReturn404WhenCustomerDoesNotExists() throws Exception {
        Long customerId = 10L;
        when(customerDao.findById(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/customers/%s", customerId)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCustomerShouldReturn404WhenCustomerDoesNotExists() throws Exception {
        Long customerId = 10L;
        CustomerDto customerDto = new CustomerDto()
            .setId(customerId)
            .setName("name")
            .setAddress("address")
            .setPhone("02121");
    
        String jsonContent = new ObjectMapper().writeValueAsString(customerDto);
        when(customerDao.findById(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/customers/%s", customerId))
                    .contentType(APPLICATION_JSON)
                    .content(jsonContent))
                .andExpect(status().isNotFound());

        verify(customerDao, times(0)).update(any());
    }

    @Test
    public void updateCustomerShouldUpdateItInTheDB() throws Exception {
        Long customerId = 10L;
        CustomerDto customerDto = new CustomerDto()
            .setId(customerId)
            .setName("updated name")
            .setAddress("address")
            .setPhone("02121");

        String jsonContent = new ObjectMapper().writeValueAsString(customerDto);

        when(customerDao.findById(customerId)).thenReturn(
                Optional.of(customerDto.into(new Customer())));

        mockMvc.perform(put(String.format("/customers/%s", customerId))
                .contentType(APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());

        verify(customerDao).update(any(Customer.class));
    }

    @Test
    public void deleteCustomerShouldReturn404WhenCustomerDoesNotExists() throws Exception {
        Long customerId = 10L;

        when(customerDao.findById(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(delete(String.format("/customers/%s", customerId)))
                .andExpect(status().isNotFound());

        verify(customerDao, times(0)).delete(customerId);
    }

    @Test
    public void deleteCustomerShouldDeleteItFromTheDB() throws Exception {
        Long customerId = 10L;
        Customer customer = new Customer()
            .setId(customerId)
            .setName("deleted name")
            .setAddress("address")
            .setPhone("02121");

        when(customerDao.findById(customerId)).thenReturn(Optional.of(customer));

        mockMvc.perform(delete(String.format("/customers/%s", customerId)))
                .andExpect(status().isOk());

        verify(customerDao).delete(customerId);
    }
}
