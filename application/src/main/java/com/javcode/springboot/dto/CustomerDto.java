package com.javcode.springboot.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.javcode.springboot.jooq.tables.interfaces.ICustomer;

public class CustomerDto implements ICustomer {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 2, max = 255, message = "Name length must be between 2 and 255 characters")
    private String name;

    @NotNull
    @Size(min = 2, max = 255, message = "Address length must be between 2 and 255 characters")
    private String address;

    @NotNull
    @Pattern(regexp = "^([0-9\\(\\)\\/\\+ \\-]*)$", message = "Only numbers, + and - signs are allowed")
    @Size(min = 6, max = 30, message = "Phone number length must be between 6 and 30 characters")
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
