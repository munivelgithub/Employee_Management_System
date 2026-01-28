package com.munivel.Employee_Management_System.Config;

import javax.sql.DataSource;

import com.munivel.Employee_Management_System.jwt.AuthEntryPointJwt;
import com.munivel.Employee_Management_System.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Security_Config {
  // DataSource is a standard Java interface for managing database connections.
  // It provides the connection details (URL, username, password)
  // and is used by Spring to interact with the database.
  //
  @Autowired DataSource dataSource;
  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http.csrf(customizer -> customizer.disable());
//    http.authorizeHttpRequests(request -> request.anyRequest().authenticated());
//    http.httpBasic(Customizer.withDefaults());
//    // http.formLogin(Customizer.withDefaults());
//    http.sessionManagement(
//        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//    return http.build();
    //the above is old
    // This is a classic and subtle Spring Security issue. Your configuration is 99% correct, but a chain reaction is causing the misleading 401 error.
    //
    //Here is the step-by-step breakdown of what's happening and how to fix it.
    //
    //🕵️ The Problem: A Chain Reaction
    //The 401 error on /signin isn't because /signin itself is blocked. Your .requestMatchers("/signin").permitAll() rule is working.
    //
    //The problem happens after your request successfully reaches the controller:
    //
    //Request OK: You send a POST to /signin. Your SecurityFilterChain correctly sees /signin and permitAll() allows the request to pass to your controller.
    //
    //Controller Runs: Your Employee_Controller's authenticateUser method is executed.
    //
    //Authentication Fails: The line authenticationManager.authenticate(...) is called.
    //
    //Database Error: The AuthenticationManager uses your UserDetailsService bean. You have configured JdbcUserDetailsManager. This manager immediately tries to run a SELECT query against your database to find the user in the users and authorities tables.
    //
    //Exception Thrown: Since you likely haven't created these specific tables, the JdbcUserDetailsManager throws a DataAccessException (or a similar JdbcSQL...Exception) because the tables don't exist.
    //
    //Wrong catch Block: Your controller's try...catch block is only looking for AuthenticationException. A DataAccessException is a different type of exception, so it is not caught.
    //
    //Unhandled Exception: The DataAccessException is thrown out of your controller.
    //
    //Forward to /error: Spring Boot's default error handling catches this exception and forwards the request internally to the /error endpoint to generate a proper error response.
    //
    //Security Blocks /error: This new request for /error is checked against your security rules:
    //
    //.requestMatchers("/signin").permitAll()
    //
    //.anyRequest().authenticated()
    //
    //Final 401: The path /error is not /signin, so it matches .anyRequest().authenticated(). Since you are not authenticated, Spring Security blocks this request to the error page and triggers your AuthEntryPointJwt, which correctly returns the 401 "Unauthorized" error you are seeing.
    //
    //✅ How to Fix It
    //You have two main ways to fix this, plus one "good practice" improvement.
    //
    //Solution 1: (Easiest Fix) Use In-Memory Users for Testing
    //Since you already have the code for it, just switch your UserDetailsService bean to use InMemoryUserDetailsManager. This avoids the database entirely.
    //
    //In Security_Config.java:
    //
    //Java
    //
    //  @Bean
    //  public UserDetailsService userDetailsService() {
    //    UserDetails user =
    //        User.withUsername("Sathaiyan")
    //            .roles("USER")
    //            .password(passwordEncoder().encode("Sathaiyan@123"))
    //            .build();
    //    UserDetails admin =
    //        User.withUsername("admin")
    //            .roles("ADMIN")
    //            .password(passwordEncoder().encode("Munivel@9787"))
    //            .build();
    //    UserDetails user1 =
    //        User.withUsername("Munivel")
    //            .roles("USER")
    //            .password(passwordEncoder().encode("Munivel@9787"))
    //            .build();
    //
    //    // Use this line instead of JdbcUserDetailsManager
    //    return new InMemoryUserDetailsManager(user, admin, user1);
    //
    //    /*
    //    // This is causing the error because the tables 'users' and 'authorities' don't exist
    //    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    //    // userDetailsManager.createUser(user1);
    //    //    userDetailsManager.createUser(admin);
    //
    //    return userDetailsManager;
    //    */
    //  }
    //Solution 2: (Correct JDBC Fix) Create the Database Tables
    //If you want to use JdbcUserDetailsManager, you must create the default tables Spring Security expects.
    //
    //Run this SQL in your database:
    //
    //SQL
    //
    //CREATE TABLE users (
    //    username VARCHAR(50) NOT NULL PRIMARY KEY,
    //    password VARCHAR(500) NOT NULL,
    //    enabled BOOLEAN NOT NULL
    //);
    //
    //CREATE TABLE authorities (
    //    username VARCHAR(50) NOT NULL,
    //    authority VARCHAR(50) NOT NULL,
    //    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
    //);
    //CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
    //After creating the tables, you must also un-comment the createUser lines in your userDetailsService bean to add the users to the database one time.
    //
    //Java
    //
    //  @Bean
    //  public UserDetailsService userDetailsService() {
    //    // ... (user definitions) ...
    //
    //    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    //
    //    // Run this ONCE to add users to your new tables
    //    // userDetailsManager.createUser(user1);
    //    // userDetailsManager.createUser(admin);
    //    // userDetailsManager.createUser(user);
    //
    //    return userDetailsManager;
    //  }
    //Note: Run the app once with createUser un-commented, then comment them out again. If you leave them un-commented, the app will crash on the next startup when it tries to create users that already exist.
    //
    //💡 Recommended Improvement (Do this anyway)
    //You should always permit the /error endpoint so you can see the real error messages instead of a 401.
    http.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                    .requestMatchers("/signin","/error").permitAll()
                    .anyRequest().authenticated());
    http.sessionManagement(
            session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS)
    );
    http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
    //http.httpBasic(withDefaults());
    http.headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()
            )
    );
    http.csrf(csrf -> csrf.disable());
    http.addFilterBefore(authenticationJwtTokenFilter(),
            UsernamePasswordAuthenticationFilter.class);


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
    // userDetailsManager.createUser(user1);
    //    userDetailsManager.createUser(admin);

    return userDetailsManager;
    // A JdbcUserDetailsManager is created using the DataSource.
    // Two users (user1 and admin) are added to the database using createUser.

    // return new InMemoryUserDetailsManager(user1, admin);
  }
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
    return builder.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

/*
* @Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeHttpRequests()
        .requestMatchers("/api/public").permitAll()     // anyone can access
        .requestMatchers("/api/private").authenticated() // must be logged in
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(authEntryPointJwt);    // your class here
}
*/
