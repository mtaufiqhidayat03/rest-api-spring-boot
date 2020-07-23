package com.tutorial.springboot.restapi.controller;

import com.tutorial.springboot.restapi.exception.ResourceNotFoundException;
import com.tutorial.springboot.restapi.model.User;
import com.tutorial.springboot.restapi.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

@RestController
@RequestMapping("/api/v1")
@Api(value = "userController", description = "Operations of user controller")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @ApiOperation(value = "View a list of available users", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "Search user by id", response = User.class)
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RejectedExecutionException("User not found on :" + userId));
        return ResponseEntity.ok().body(user);
    }

    @ApiOperation(value = "Add new user")
    @PostMapping("/users")
    public User createUser(@Valid @ModelAttribute User user) {
        //System.out.println(user);
        return userRepository.save(user);
    }

    @ApiOperation(value = "Update existing user")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable(value = "id") Long userId, @Valid @ModelAttribute User userData) throws ResourceNotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on : " + userId));
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setUpdatedAt(new Date());
        user.setUpdatedBy(userData.getUpdatedBy());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @ApiOperation(value = "Delete existing user")
    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found on : " + userId));
        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
