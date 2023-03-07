package com.example.demo.controller;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping(value = "/{username}")
    public User findUserByUsername(@PathVariable String username){
        return mongoService.findUserByUsername(username).orElse(new User());
    }

    @GetMapping(value = "/{username}/friends")
    public List<User> findUserFriendsByUsername(@PathVariable String username){
        User user = mongoService.findUserByUsername(username).orElse(new User());
        return mongoService.findUserFriendsByUsername(user.getFriends()).orElse(new ArrayList<>());
    }

    @GetMapping(value = "/userCount")
    public List<User> userCountPerCity(){
        return mongoService.countUsersPerCityAggregation();
    }

    @GetMapping(value = "/user", params = {"email", "pwz"})
    public User login(@RequestParam(value = "email") String email, @RequestParam("pwz") String pwz) throws Exception {
        return userConfiguration.checkLogin(email, pwz);
    }

    @GetMapping(value = "/update/{id}")
    public User updateUserById(
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

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user) throws Exception {
        userConfiguration.validateSignUp(user);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }
}

