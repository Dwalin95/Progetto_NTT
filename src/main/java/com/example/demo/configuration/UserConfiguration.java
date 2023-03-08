package com.example.demo.configuration;

import com.example.demo.exceptionHandler.ResourceNotFoundException;
import com.example.demo.exceptionHandler.UnauthorizedException;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfiguration {

    @Autowired
    private MongoService mongoService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ResponseEntity<User> checkLogin(String email, String psw) throws Exception {
        if (emailExists(email)) {
            User user = mongoService.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format("not users found with this email: %s", email)));

            if (passwordEncoder().matches(psw, user.getPwz())) {
                return ResponseEntity.ok(user);
            } else {
                throw new UnauthorizedException("password not valid");
            }
        } else {
            throw new ResourceNotFoundException(String.format("email: %s not found", email));
        }
    }

    private boolean emailExists(String email) {
        return mongoService.findUserByEmail(email).isPresent();
    }

    public void validateSignUp(User user) {
        String psw = user.getPwz();
        String email = user.getEmail();

        try {
            if (validatePassword(psw) && validateEmail(email)) {
                user.setPwz(passwordEncoder().encode(psw));
                mongoService.saveUser(user);
            }
        } catch (Exception e) {
            throw new UnauthorizedException("");
        }
    }

    private boolean validateEmail(String email) {
        return email.contains("@");
    }

    private boolean validatePassword(String psw) {
        //regex: Must be from 8 to 32 characters long, at least 1 special character (only [!, #, %, @]), at least 1 upper-case letter, at least 1 lower-case letter, at least 1 number
        return psw.matches("^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[^!#%@]*[!#%@])[A-Za-z0-9!#%@]{8,32}$");
    }
}
