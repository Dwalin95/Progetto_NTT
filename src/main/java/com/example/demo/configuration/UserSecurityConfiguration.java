package com.example.demo.configuration;

import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSecurityConfiguration {

    @Autowired
    private MongoService mongoService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //TODO: sono due modi di criptare la password, devo capire se uno è meglio dell'altro o è uguale
    public String passwordEncoder(String psw){
        return passwordEncoder().encode(psw);
        // BCrypt.hashpw(psw, BCrypt.gensalt());
    }

    //TODO: anche qua invece del system.out dobbiamo creare una eccezione
    public boolean checkLogin(User user){
        String email = user.getEmail();
        String psw = user.getPwz();

        if(emailExists(email)){
            User foundUser = mongoService.findUserByEmail(email).get();

            if(BCrypt.checkpw(foundUser.getPwz(), passwordEncoder(psw))){
                System.out.println("Login effettuato con successo");
                return true;
            } else {
                System.out.println("La password è errata");
                return false;
            }
        } else {
            System.out.println("L'email non è presente nel database");
            return false;
        }
    }

    private boolean emailExists(String email){
        return mongoService.findUserByEmail(email).isPresent();
    }


    public boolean validateEmail(String email){
        return email.contains("@");
    }

    public boolean validatePassword(String psw){
        //regex: Must be from 8 to 32 characters long, at least 1 special character (only [!, #, %, @]), at least 1 upper-case letter, at least 1 lower-case letter, at least 1 number
        return psw.matches("^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[^!#%@]*[!#%@])[A-Za-z0-9!#%@]{8,32}$");
    }
}
