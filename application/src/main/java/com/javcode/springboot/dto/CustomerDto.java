package com.javcode.springboot.dto;

import com.javcode.springboot.jooq.tables.interfaces.ICustomer;

public class CustomerDto implements ICustomer {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;
    private String phone;

    @Override
    public CustomerDto setId(Long value) {
        id = value;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CustomerDto setName(String value) {
        this.name = value;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CustomerDto setAddress(String value) {
        this.address = value;
        return this;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public CustomerDto setPhone(String value) {
        this.phone = value;
        return this;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void from(ICustomer from) {
        this.id = from.getId();
        this.name = from.getName();
        this.address = from.getAddress();
        this.phone = from.getPhone();
    }

    @Override
    public <E extends ICustomer> E into(E into) {
        into.from(this);
        return into;
    }

}
