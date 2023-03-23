package com.example.ntt.configuration;

import com.example.ntt.dto.UserAuthDTO;
import com.example.ntt.enums.ErrorMsg;
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

import javax.swing.*;
import java.awt.*;
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
    public User checkLogin(UserAuthDTO credentials) {
        if (emailExists(credentials.getEmail())) {
            User user = mongoService.findUserByEmail(credentials.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No users found with this email: %s", credentials.getEmail())));

            if (passwordEncoder().matches(credentials.getPassword(), user.getPassword())) {
                return user;
            } else {
                throw new UnauthorizedException("Password not valid");
            }
        } else {
            throw new ResourceNotFoundException(String.format("Email: %s not found", credentials.getEmail()));
        }
    }

    public boolean isImage(String imageUrl){
        Image image = new ImageIcon(imageUrl).getImage();
        return image.getWidth(null) != -1;
    }

    //TODO: metodo alternativo per fare il check dell'url decidere quale usare - LDB
   /* public boolean isImage(String imageUrl) throws IOException {
        Image image = ImageIO.read(new URL(imageUrl));
        return image != null;
    }*/

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

    //TODO: LDB - trovare il modo di togliere gli if lo so che è brutto
    private User validatePasswordAndEmail(User user) {
        if(emailExists(user.getEmail())){
            if(usernameExists(user.getUsername())){
                if(isImage(user.getProfilePicUrl())){
                    if(validatePassword(user.getPassword()) && validateEmail(user.getEmail())){
                        user.setPassword(passwordEncoder().encode(user.getPassword()));
                        return user;
                    } else {
                        throw new UnauthorizedException(ErrorMsg.ACCESS_DENIED.getMsg());
                    }
                } else {
                    throw new PreconditionFailedException(ErrorMsg.URL_IS_NOT_IMG.getMsg());
                }
            } else {
                throw new PreconditionFailedException(String.format(ErrorMsg.USERNAME_ALREADY_IN_USE.getMsg(), user.getUsername()));
            }
        } else {
            throw new PreconditionFailedException(String.format(ErrorMsg.EMAIL_ALREADY_IN_USE.getMsg(), user.getEmail()));
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
