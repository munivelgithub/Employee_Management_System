 package com.munivel.Employee_Management_System.Config;

 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.Customizer;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.core.userdetails.User;
 import org.springframework.security.core.userdetails.UserDetails;
 import org.springframework.security.core.userdetails.UserDetailsService;
 import org.springframework.security.provisioning.InMemoryUserDetailsManager;
 import org.springframework.security.web.SecurityFilterChain;

 @Configuration
 @EnableWebSecurity
 public class Security_Config {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(customizer -> customizer.disable());
    http.authorizeHttpRequests(request -> request.anyRequest().authenticated());
    http.httpBasic(Customizer.withDefaults());
    //http.formLogin(Customizer.withDefaults());
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }


  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user1 =
    User.withUsername("Munivel").roles("HR").password("{noop}Munivel@123").build();
    return new InMemoryUserDetailsManager(user1);
  }

 }
