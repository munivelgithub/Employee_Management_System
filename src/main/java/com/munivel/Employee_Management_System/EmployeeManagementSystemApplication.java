package com.munivel.Employee_Management_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.munivel.Employee_Management_System")
@ComponentScan(
    basePackages = {
      "com.munivel.Employee_Management_System",
      "com.munivel.Employee_Management_System.exception",
      "com.munivel.Employee_Management_System.Config" // package of GlobalExceptionHandler
    })
public class EmployeeManagementSystemApplication {
  public static void main(String[] args) {
    SpringApplication.run(EmployeeManagementSystemApplication.class, args);
  }
}
