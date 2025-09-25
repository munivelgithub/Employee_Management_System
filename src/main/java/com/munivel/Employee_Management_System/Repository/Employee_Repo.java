package com.munivel.Employee_Management_System.Repository;

import com.munivel.Employee_Management_System.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Employee_Repo extends JpaRepository<Employee, Integer> {}
