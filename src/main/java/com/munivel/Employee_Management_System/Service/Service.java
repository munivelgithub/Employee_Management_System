package com.munivel.Employee_Management_System.Service;

import com.munivel.Employee_Management_System.Model.Employee;
import com.munivel.Employee_Management_System.Repository.Employee_Repo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class Service {

  @Autowired private Employee_Repo repo;

  public List<Employee> alldetails() {
    return repo.findAll();
  }

  public Employee add_Employee(Employee employee) {
    Employee employee1 = repo.save(employee);
    return employee1;
  }

  // 2️⃣ What is Optional<Employee>?
  //
  // Optional is a container object introduced in Java 8 that may or may not contain a value.
  //
  // If an Employee with the given id exists → Optional contains the Employee.
  //
  // If not → Optional is empty.
  //
  // This helps avoid NullPointerException. how it is works
  // What is Optional?
  //
  // Think of Optional<T> as a box that may or may not hold a value of type T.
  //
  // In your case: Optional<Employee> is a box that may contain an Employee object.
  //
  // Internally, Optional has two states:
  //
  // Non-empty → contains a value.
  //
  // Empty → contains nothing (null internally, but safely wrapped).
  // What is Optional?
  //
  // Think of Optional<T> as a box that may or may not hold a value of type T.
  //
  // In your case: Optional<Employee> is a box that may contain an Employee object.
  //
  // Internally, Optional has two states:
  //
  // Non-empty → contains a value.
  //
  // Empty → contains nothing (null internally, but safely wrapped).
  // Method	What it does
  // isPresent()	Returns true if value exists
  // get()	Returns the value (throws exception if empty)
  // orElse(T other)	Returns value if exists, otherwise returns other
  // orElseThrow()	Returns value if exists, otherwise throws exception
  // ifPresent(Consumer)	Executes a block only if value exists
  public void deleting(int id) {
    if (repo.existsById(id)) {
      repo.deleteById(id);
    } else {
      throw new RuntimeException("Student is not found" + id);
    }
  }

  public Employee update_employee(int id, Employee employee) {
    Optional<Employee> e = repo.findById(id);
    if (e.isPresent()) {
      Employee employee1 = e.get();
      employee1.setId(employee.getId());
      employee1.setName(employee.getName());
      employee1.setRole(employee.getRole());
      employee1.setEmail(employee.getEmail());
      employee1.setPhone(employee.getPhone());
      employee1.setSalary(employee.getSalary());
      return repo.save(employee1);
    } else {
      throw new RuntimeException("Cannot able to update" + id);
    }
  }

  //  public Optional<Employee> getbyname(String name) {
  //    Optional<Employee> e = repo.findByName(name);
  //    return e;
  //  }
}
