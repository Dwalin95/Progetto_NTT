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

    //TODO: LDB - vedere come fare per fare l'upload delle immagini dalla galleria
    public boolean isImage(String imageUrl){
        //    The URL must start with either https and
        //    then followed by :// and
        //    then it must contain www. and
        //    then followed by subdomain of length (2, 256) and
        //    last part contains top level domain like .com, .org etc.
        //    must end with jpg, jpeg, png, webp, svg
        String regex = "((https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)(?i)(jpg\\Z|jpeg\\Z|png\\Z|webp\\Z|svg\\Z)";

        if(imageUrl.matches(regex)){
            return true;
        } else {
            throw new PreconditionFailedException(ErrorMsg.URL_IS_NOT_VALID.getMsg());
        }
    }

    public void handleUpdateException(boolean check, RuntimeException exception) {
        if (check) {
            throw exception;
        }
    }

    public boolean emailExists(String email) {
        if(mongoService.findUserByEmail(email).isPresent()){
            throw new PreconditionFailedException(String.format(ErrorMsg.EMAIL_ALREADY_IN_USE.getMsg(), email));
        } else {
            return true;
        }
    }

    public boolean usernameExists(String username) {
        if(mongoService.findUserByUsername(username).isPresent()){
            throw new PreconditionFailedException(String.format(ErrorMsg.USERNAME_ALREADY_IN_USE.getMsg(), username));
        } else {
            return true;
        }
    }

    public void validateSignUp(User user) {
        Optional.of(user)
                .map(this::validatePasswordAndEmail)
                .map(mongoService::saveUser);
    }

    //TODO: sistemare sono invertite le eccezioni di emailExists e usernameExists
    private User validatePasswordAndEmail(User user) {
        return Optional.of(user)
                .filter(u -> this.emailExists(u.getEmail()))
                .filter(u -> this.usernameExists(u.getUsername()))
                .filter(u -> this.isImage(u.getProfilePicUrl()))
                .filter(u -> this.validatePassword(u.getPassword()) && this.validateEmail(u.getEmail()))
                .map(this::encodeNewPassword)
                .orElseThrow(() -> new UnauthorizedException(ErrorMsg.ACCESS_DENIED.getMsg()));
    }

    private User encodeNewPassword(User u) {
        u.setPassword(passwordEncoder().encode(u.getPassword()));
        return u;
    }


    private boolean validateEmail(String email) {
        if(email.contains("@")){
            return true;
        } else {
            throw new PreconditionFailedException(ErrorMsg.EMAIL_MUST_CONTAIN_AT.getMsg());
        }
    }

    private boolean validatePassword(String password) {
        //regex: Must be from 8 to 32 characters long, at least 1 special character (only [!, #, %, @]), at least 1 upper-case letter, at least 1 lower-case letter, at least 1 number
        if(password.matches("^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[^!#%@]*[!#%@])[A-Za-z0-9!#%@]{8,32}$")){
            return true;
        } else {
            throw new PreconditionFailedException(ErrorMsg.PASSWORD_MUST_RESPECT_RULES.getMsg());
        }
    }
}
