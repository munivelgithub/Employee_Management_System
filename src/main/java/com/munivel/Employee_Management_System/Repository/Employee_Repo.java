package com.munivel.Employee_Management_System.Repository;

import com.munivel.Employee_Management_System.Model.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Employee_Repo extends JpaRepository<Employee, Integer> {
  Optional<Employee> findByName(String name);
  // it will genertate an query atutomatically select * from employee where name=?"
}
