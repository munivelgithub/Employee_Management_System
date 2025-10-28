package com.munivel.Employee_Management_System.Config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Security_Config {
  // DataSource is a standard Java interface for managing database connections.
  // It provides the connection details (URL, username, password)
  // and is used by Spring to interact with the database.
  @Autowired DataSource dataSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(customizer -> customizer.disable());
    http.authorizeHttpRequests(request -> request.anyRequest().authenticated());
    http.httpBasic(Customizer.withDefaults());
    // http.formLogin(Customizer.withDefaults());
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    //    UserDetails user1 =
    //        User.withUsername("user").roles("USER").password("{noop}Munivel@123").build();
    UserDetails user =
        User.withUsername("Sathaiyan")
            .roles("USER")
            .password(passwordEncoder().encode("Sathaiyan@123"))
            .build();
    UserDetails admin =
        User.withUsername("admin")
            .roles("ADMIN")
            .password(passwordEncoder().encode("Munivel@9787"))
            .build();
    UserDetails user1 =
        User.withUsername("Munivel")
            .roles("USER")
            .password(passwordEncoder().encode("Munivel@9787"))
            .build();
    // inmeory is used to create an user in the inmemory
    // bu the jdbc user details manager is used to create an user in the database

    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    //userDetailsManager.createUser(user1);
    //    userDetailsManager.createUser(admin);

    return userDetailsManager;
    // A JdbcUserDetailsManager is created using the DataSource.
    // Two users (user1 and admin) are added to the database using createUser.

    // return new InMemoryUserDetailsManager(user1, admin);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
