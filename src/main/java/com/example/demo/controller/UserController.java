package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MongoService;
import com.example.demo.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {



    private final MongoService mongoService;


    @RequestMapping("/")
    public String home(){
        return "La home";
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
    public void createStudent(@RequestBody User user) {
        mongoService.saveUser(user);
    }
}


