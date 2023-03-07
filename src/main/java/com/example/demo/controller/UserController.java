package com.example.demo.controller;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class UserController {

    private final MongoService mongoService;
    @Autowired
    private UserConfiguration userConfiguration;

    @RequestMapping("/")
    public String home() {
        return "La Home";
    }

    @GetMapping(value = "/ciao")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping(value = "/list")
    public Optional<List<User>> findAllUsers() {
        return mongoService.findAllUsers();
    }

    @GetMapping(value = "/user", params = {"email", "pwz"})
    public User login(@RequestParam(value = "email") String email, @RequestParam("pwz") String pwz) throws Exception {
        return userConfiguration.checkLogin(email, pwz);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }

    @PostMapping(value = "/creazione")
    public void createUser(@RequestBody User user) throws Exception {
        userConfiguration.validateSignUp(user);
    }

    @GetMapping(value = "/update/{id}")
    public User updateUser(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    ){
        //TODO: cambiare orElse
        User user = mongoService.findUserById(id).orElse(null);

        user.setUsername(username.orElse(user.getUsername()));
        user.setFirstName(firstName.orElse(user.getFirstName()));
        user.setLastName(lastName.orElse(user.getLastName()));
        user.setEmail(email.orElse(user.getEmail()));

        user.setGender(gender.orElse(user.getEmail()));

        mongoService.saveUser(user);

        return user;
    }
}

