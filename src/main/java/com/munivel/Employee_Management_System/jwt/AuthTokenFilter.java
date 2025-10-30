package com.munivel.Employee_Management_System.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired private JwtUtils jwtUtils;

  @Autowired private UserDetailsService userDetailsService; // inbuild class

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  // this method will intercept an request and  verifying the token
  // it is an custom filter chain that intercept with the filter chain flow
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        UserDetails userDetails =
            userDetailsService.loadUserByUsername(
                username); // fetching an data fron the data base and return the datas like the
        // username,roles and etc

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }
    filterChain.doFilter(
        request,
        response); // this is injected at the middle at some place then it need to be continue not
    // the code will breaak
  }

  private String parseJwt(HttpServletRequest request) {
    String jwt = jwtUtils.getJwtFromHeader(request);
    logger.debug("AuthTokenFilter.java: {}", jwt);
    return jwt;
  }
}
/*@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
How Spring Security Works — Locker & ID Card Example

1️⃣ The user sends a login or JWT request.
2️⃣ Spring Security validates the user (checks token, username, etc.).
3️⃣ Once verified, Spring says — “This user is real.”
It then creates an ID card (the Authentication object) containing:

✅ Username

✅ Roles/Authorities

✅ Extra details (IP, session, etc.)
4️⃣ Spring stores this ID card safely inside a locker — the SecurityContextHolder.
5️⃣ When any new request comes in, Spring opens the locker, retrieves the ID card, and knows exactly who the user is and what access they have.

✅ In simple words:
After validation, the user’s details are kept safely in the locker (SecurityContextHolder), and Spring uses that stored ID card to identify and authorize the user for every reques
*/
