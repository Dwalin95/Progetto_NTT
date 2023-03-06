package com.example.demo.controller;

import com.example.demo.configuration.UserSecurityConfiguration;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MongoService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {


    private final MongoService mongoService;
    @Autowired
    UserSecurityConfiguration userSecurityConfiguration;

    @RequestMapping("/")
    public String home() {
        return "La Home";
    }


    @GetMapping(value = "/ciao")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping(value = "/list")
    public Optional<List<User>> findAll() {
        return mongoService.findAllUsers();
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }

    @PostMapping(value = "/creazione")
    public void createUser(@RequestBody User user) {

        String password = user.getPwz();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        user.setPwz(hashedPassword);
        mongoService.saveUser(user);

    }


    @GetMapping(value = "/revelio")
    public Boolean revelio() {

        User user = new User();

        String pwzInseritaUtente = "Minimo8!ettere#Chioccol@";
        String pwzDataBase = "$2a$10$rD0xnzlMmChvSoJuViBYueFFHs6UqslyAcJDYMXFtyKTFV9c0Yhr8";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(pwzInseritaUtente, pwzDataBase)) {
            return true;
        } else {
            return false;
        }
    }
    /** @PostMapping(value = "/creazione")
    public void createUser(@RequestBody User user){
    String psw = user.getPwz();
    String email = user.getEmail();

    if (userSecurityConfiguration.validatePassword(psw) && userSecurityConfiguration.validateEmail(email)) {
    user.setPwz(userSecurityConfiguration.passwordEncoder(psw));
    mongoService.saveUser(user);
    }

    }
     */
}

