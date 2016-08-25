package com.javcode.springboot.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.javcode.springboot.exception.GlobalExceptionHandler;

@Import(GlobalExceptionHandler.class)
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = { CustomerController.class })
public class ControllerTestConfig {

}
