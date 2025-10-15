package com.munivel.Employee_Management_System.Repository_Testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.munivel.Employee_Management_System.Model.Employee;
import com.munivel.Employee_Management_System.Repository.Employee_Repo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Repository_Testing {

  // assert j
  @Autowired private Employee_Repo repo;

  private Employee e;

  @Test
  public void test1() {
    e = e = repo.findById(1).orElse(null);
    assertThat(e).isNotNull();
  }

  @Test
  public void test2() {
    e = e = repo.findById(1).orElse(null);
    assertThat(e.getName(), startsWith("M"));
    assertThat(e.getRole(), endsWith("r"));
  }
}
