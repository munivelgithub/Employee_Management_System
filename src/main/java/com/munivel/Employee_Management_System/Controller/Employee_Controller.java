package com.munivel.Employee_Management_System.Controller;

import com.munivel.Employee_Management_System.Model.Employee;
import com.munivel.Employee_Management_System.Service.Service;
import com.munivel.Employee_Management_System.jwt.JwtUtils;
import com.munivel.Employee_Management_System.jwt.LoginRequest;
import com.munivel.Employee_Management_System.jwt.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
// @RequestMapping("/")
public class Employee_Controller {
  @Autowired private Service service;
  @Autowired private JwtUtils jwtUtils;
  @Autowired private AuthenticationManager authenticationManager;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    Authentication authentication;
    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername(), loginRequest.getPassword()));
    } catch (AuthenticationException exception) {
      Map<String, Object> map = new HashMap<>();
      map.put("message", "Bad credentials");
      map.put("status", false);
      return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

    return ResponseEntity.ok(response);
  }

  // One Role	hasRole('ADMIN')	User must have the ADMIN role.
  // Multiple Roles (OR)	hasAnyRole('ADMIN', 'USER')	User must have at least one of the listed
  // roles. This is the simplest way to allow multiple roles.
  // Multiple Roles (OR)	hasRole('ADMIN') or hasRole('USER')	Equivalent to hasAnyRole(). Useful when
  // combining with other conditions.
  // Multiple Roles (AND)	hasRole('ADMIN') and hasRole('MANAGER')	User must have ALL listed roles.
  // Authority	hasAuthority('DELETE_PRIVILEGE')	Checks for a specific permission/authority, not
  // necessarily a high-level role.

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/user")
  public String userEndPoint() {
    return "hello ! user";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public String adminEndPoint() {
    return "hello ! admin";
  }

  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @RequestMapping("/")
  public String homs() {
    return "hello";
  }

  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @GetMapping("/home")
  public String home() {
    return "Welcome";
  }

  @PreAuthorize("hasAnyRole('ADMIN')")
  @GetMapping("/All_Details")
  public ResponseEntity<List<Employee>> all_list() {
    List<Employee> ls = service.alldetails();
    return new ResponseEntity<>(ls, HttpStatus.OK);
  }

  // Difference between @Valid and @Validated
  //
  // @Valid → comes from Jakarta Bean Validation (JSR 380), commonly used for validating request
  // bodies.
  //
  // @Validated → comes from Spring Framework, supports group validation (useful when you want
  // different validation rules in different situations).
  //
  // For your case → ✅ just use @Valid.
  // Because of @Valid, Spring Boot says:
  //
  // "Before calling the controller method, let’s check all the validation rules on this object."
  //
  // It looks at your Employee class:
  //
  // @NotBlank
  // private String name;
  //
  // @Email
  // private String email;
  //
  // @Pattern(regexp = "^[0-9]{10}$")
  // private String phone;
  //
  // @DecimalMin(value = "0.0", inclusive = false)
  // private Double salary;
  // The validation is done by Spring’s Bean Validation framework, which under the hood uses Jakarta
  // Bean Validation (formerly javax.validation, now part of Jakarta EE). Let me break it down
  // clearly:
  // The @Valid tells Spring:
  //
  // “Before passing this Employee object to the controller method, validate it according to the
  // constraints defined in the class.”
  // 2️⃣ Who performs the actual checks?
  //
  // The Bean Validation provider (usually Hibernate Validator, which is included in Spring Boot by
  // default) looks at your annotations like:
  //
  // @NotBlank
  //
  // @Email
  //
  // @Pattern
  //
  // @DecimalMin
  //
  // and checks if the incoming values satisfy the rules.
  //
  // If everything is valid → the Employee object is passed to your controller.
  //
  // If any rule fails → MethodArgumentNotValidException is thrown before your controller method
  // executes.
  // But on their own, they don’t send any response to the client. They just throw a
  // MethodArgumentNotValidException if the input fails.
  // 2️⃣@RestControllerAdvice handles the exception
  //
  // Your GlobalExceptionHandler catches that exception:
  // Model (Employee) → defines validation rules.
  //
  // Controller → receives request with @Valid.
  //
  // GlobalExceptionHandler → converts validation exceptions into readable JSON messages.
  // If any constraint is violated, Spring does not call your controller method.

  // The <?> in Java is called a wildcard. It’s used in generics to mean “any type.” Let’s break it
  // down in the context of your code:
  // Here, ResponseEntity<?> means:

  // ResponseEntity is a generic class that can hold any type of body.
  // <?> says:
  // <?> instead of a specific typewe can use this flexible?
  // “The body of this ResponseEntity can be any type, I don’t want to specify it now.”
  // <?> = unknown type
  //
  // <T> = type parameter you define
  //
  // <T extends SomeClass> = type must extend a specific cla

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/Add")
  public ResponseEntity<Employee> Adding(@Valid @RequestBody Employee employee) {
    Employee employees = service.add_Employee(employee);
    if (employees != null) {
      return new ResponseEntity<>(employees, HttpStatus.ACCEPTED);
    }
    return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/Delete/{id}")
  public ResponseEntity<?> delete(@PathVariable int id) {
    try {
      service.deleting(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/update/{id}")
  public ResponseEntity<Employee> update(
      @PathVariable int id, @Valid @RequestBody Employee employee) {
    Employee e = service.update_employee(id, employee);
    return new ResponseEntity<>(e, HttpStatus.OK);
  }

  // getting data by name
  //  @GetMapping("/Byname/{name}")
  //  public ResponseEntity<Employee> getbyname(@PathVariable String name) {
  //    Optional<Employee> e = service.getbyname(name);
  //    if (e.isPresent()) {
  //      return new ResponseEntity<>(e.get(), HttpStatus.OK);
  //      // e.get() is an actual object of an employee
  //      // Optional.get()
  //
  //      // get() retrieves the value inside the Optional.
  //      //
  //      // If the Optional contains a value, it returns that value.
  //      //
  //      // If the Optional is empty, calling get() will throw NoSuchElementException.
  //    }
  //    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  //    //Returning new ResponseEntity<>(null, HttpStatus.NOT_FOUND) works, but in REST it’s better
  // to avoid returning null in the body for 404.
  //    //
  //    //Just return ResponseEntity.notFound().build()
  //  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/csrf")
  public CsrfToken token(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute("_csrf");
  }
}
