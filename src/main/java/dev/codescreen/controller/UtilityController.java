package dev.codescreen.controller;

import dev.codescreen.entity.AuthorizationResponse;
import dev.codescreen.entity.User;
import dev.codescreen.repository.AuthorizationResponseRepository;
import dev.codescreen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UtilityController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorizationResponseRepository authorizationResponseRepository;

    /**
     * Handles a POST request to "/user".
     * Expects a User object in the request body containing user information.
     * Sets the user's creation date to the current timestamp before saving it to the database using the UserRepository.
     * Responds with HTTP status code CREATED (201) upon successful user creation.
     *
     * Created for development process
     *
     * @param user The User object containing user details.
     * @return ResponseEntity containing the saved User object and HttpStatus.CREATED
     */
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody User user){
        user.setCreatedAt(LocalDateTime.now());
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    /**
     * Handles a GET request to "/user".
     * Retrieves all users from the database using the UserRepository.
     * Responds with HTTP status code OK (200) and a list of User objects.
     *
     * Created for development process
     *
     * @return ResponseEntity containing a list of User objects and HttpStatus.OK
     */
    @GetMapping("/user")
    public ResponseEntity<List<User>> findAllUsers(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }


    /**
     * Handles a GET request to "/responses".
     * Retrieves all failed authorization responses from the database using the AuthorizationResponseRepository.
     * Responds with HTTP status code OK (200) and a list of AuthorizationResponse objects.
     *
     * @return ResponseEntity containing a list of AuthorizationResponse objects and HttpStatus.OK
     */
    @GetMapping("/responses")
    public ResponseEntity<List<AuthorizationResponse>> findAllAuthorizationResponses(){
        return new ResponseEntity<>(authorizationResponseRepository.findAll(), HttpStatus.OK);
    }

}
