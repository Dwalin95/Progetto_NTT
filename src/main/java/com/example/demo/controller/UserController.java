package com.example.demo.controller;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Test pagina iniziale
    @RequestMapping("/")
    public String home() {
        return "La Home";
    }

    @GetMapping(value = "/ciao")
    public String hello() {
        return "Hello World!";
    }

    // Visualizzazione contenuto
    @GetMapping(value = "/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username){
        Optional<User> user = mongoService.findUserByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{username}/friends")
    public ResponseEntity<List<User>> findUserFriendsByUsername(@PathVariable String username){
        Optional<User> user = mongoService.findUserByUsername(username);
        if (user.isPresent()) {
            List<User> friends = mongoService.findUserFriendsByUsername(user.get().getFriends()).orElse(new ArrayList<>());
            return ResponseEntity.ok(friends);
        } else {
            return ResponseEntity.notFound().build(); //Nel caso in cui {username} fosse inesistente
        }
    }

    @GetMapping(value = "/list")
    public Optional<List<User>> findAllUsers() {
        return mongoService.findAllUsers();
    }
    @GetMapping(value = "/userCount")
    public List<User> userCountPerCity(){
        return mongoService.countUsersPerCityAggregation();
    }

    @GetMapping(value = "/{username}/friendsPerCity")
    public List<User> friendsCountPerCity(@PathVariable String username){
        List<String> friends = mongoService.findUserByUsername(username).orElse(new User()).getFriends();
        return mongoService.countFriendsPerCityAggregation(friends);
    }

    @GetMapping(value = "/user", params = {"email", "pwz"})
    public User login(@RequestParam(value = "email") String email, @RequestParam("pwz") String pwz) throws Exception {
        return userConfiguration.checkLogin(email, pwz);
    }

    // base perinviare e ricevere richieste di amicizia
    @GetMapping(value = "/{username}/receivedFriendRequests")
    public ResponseEntity<List<User>> findUserFriendRequestsByUsername(@PathVariable String username){
        Optional<User> user = mongoService.findUserByUsername(username);
        if(user.isPresent()){
            List<String> friendRequestsList = user.get().getReceivedFriendRequests();
            List<User> friends = mongoService.findUserFriendsByUsername(friendRequestsList).orElse(new ArrayList<>());
            return ResponseEntity.ok(friends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{username}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String username, @RequestParam String usernameFriend){
        User user = mongoService.findUserByUsername(username).orElse(null);
        User friendToAdd = mongoService.findUserByUsername(usernameFriend).orElse(null);

        user.getSentFriendRequests().add(usernameFriend);
        friendToAdd.getReceivedFriendRequests().add(username);

        mongoService.saveUser(user);
        mongoService.saveUser(friendToAdd);
    }


//Update relativo ai campi K,V

    @PutMapping (value = "/update/{id}")
    public User updateUserById(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    ){

        User user = mongoService.findUserById(id).orElse(null);

        user.setUsername(username.orElse(user.getUsername()));
        user.setFirstName(firstName.orElse(user.getFirstName()));
        user.setLastName(lastName.orElse(user.getLastName()));
        user.setEmail(email.orElse(user.getEmail()));
        user.setGender(gender.orElse(user.getGender()));

        mongoService.saveUser(user);
        return user;
    }

    /*
    Con la Response Entity

     @PutMapping(value = "/update/{id}")
     public ResponseEntity<User> updateUserById(
     @PathVariable String id,
     @RequestParam Optional<String> username,
     @RequestParam Optional<String> firstName,
     @RequestParam Optional<String> lastName,
     @RequestParam Optional<String> email,
     @RequestParam Optional<String> gender
     ) {

     Optional<User> user = mongoService.findUserById(id);

     if (user.isPresent()) {
     user.get().setUsername(username.orElse(user.get().getUsername()));
     user.get().setFirstName(firstName.orElse(user.get().getFirstName()));
     user.get().setLastName(lastName.orElse(user.get().getLastName()));
     user.get().setEmail(email.orElse(user.get().getEmail()));
     user.get().setGender(gender.orElse(user.get().getGender()));

     mongoService.saveUser(user.get());

     return ResponseEntity.ok(user.get());
     } else {
     return ResponseEntity.notFound().build();
     }
     }
     */


    //Creazione ed Eliminazione

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user) throws Exception {
        userConfiguration.validateSignUp(user);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }
}

