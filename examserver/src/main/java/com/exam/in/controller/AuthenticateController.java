package com.exam.in.controller;
import com.exam.in.config.JwtHelper;
import com.exam.in.entity.JwtRequest;
import com.exam.in.entity.JwtResponse;
import com.exam.in.service.impl.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ama")
public class AuthenticateController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> createToken(@RequestBody JwtRequest request) {
        try {
            authenticate(request.getUserName(), request.getPassword());
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
            String token = jwtHelper.generateToken(userDetails);
            JwtResponse response = new JwtResponse();
            response.setToken(token);
            logger.info("Token generated successfully for user: {}", request.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error generating token for user: {}", request.getUserName(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void authenticate(String username, String password) throws Exception {
        logger.info("Authenticating user: {}", username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
            logger.info("User authenticated successfully: {}", username);
        } catch (BadCredentialsException c) {
            logger.error("Invalid credentials for user: {}", username, c);
            throw new Exception("Invalid username and password: " + c.getMessage());
        }
    }
}