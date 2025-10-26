package com.munivel.Employee_Management_System.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
@Data
public class Employee {


  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotBlank(message = "Name is required and cannot be empty")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be in a valid format (e.g., user@example.com)")
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
  @Column(nullable = false, unique = true)
  private String phone;

  @NotBlank(message = "Gender is required")
  @Column(nullable = false)
  private String gender;

  @NotNull(message = "Salary is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
  @Column(nullable = false)
  private Double salary;

  @NotBlank(message = "Role is required")
  @Column(nullable = false)
  private String role;
}
