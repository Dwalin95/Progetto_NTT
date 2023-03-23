package com.example.ntt.configuration;

import com.example.ntt.exceptionHandler.PreconditionFailedException;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.User;
import com.example.ntt.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class UserConfiguration {

    @Autowired
    private MongoService mongoService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //TODO: LDB - trovare il modo di togliere gli if
    public User checkLogin(String email, String psw) {
        if (emailExists(email)) {
            User user = mongoService.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format("Not users found with this email: %s", email)));
            if (passwordEncoder().matches(psw, user.getPassword())) {
                return user;
            } else {
                throw new UnauthorizedException("Password not valid");
            }
        } else {
            throw new ResourceNotFoundException(String.format("Email: %s not found", email));
        }
    }

    public boolean emailExists(String email) {
        return mongoService.findUserByEmail(email).isPresent();
    }

    public boolean usernameExists(String username) {
        return mongoService.findUserByUsername(username).isPresent();
    }

    public void validateSignUp(User user) {
        Optional.of(user)
                .map(this::validatePasswordAndEmail)
                .map(mongoService::saveUser);
    }

    //TODO: LDB - trovare il modo di togliere gli if
    private User validatePasswordAndEmail(User user) {
        if(emailExists(user.getEmail())){
            if(usernameExists(user.getUsername())){
                if(validatePassword(user.getPassword()) && validateEmail(user.getEmail())){
                    user.setPassword(passwordEncoder().encode(user.getPassword()));
                    return user;
                } else {
                    throw new UnauthorizedException("Access denied");
                }
            } else {
                throw new PreconditionFailedException(String.format("The username %s is already present in use", user.getUsername()));
            }
        } else {
            throw new PreconditionFailedException(String.format("The email %s is already present in use", user.getEmail()));
        }
    }

    private boolean validateEmail(String email) {
        return email.contains("@");
    }

    private boolean validatePassword(String password) {
        //regex: Must be from 8 to 32 characters long, at least 1 special character (only [!, #, %, @]), at least 1 upper-case letter, at least 1 lower-case letter, at least 1 number
        return password.matches("^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[^!#%@]*[!#%@])[A-Za-z0-9!#%@]{8,32}$");
    }
}
