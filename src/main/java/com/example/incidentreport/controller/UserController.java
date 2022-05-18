package com.example.incidentreport.controller;

import com.example.incidentreport.contract.ResponseResult;
import com.example.incidentreport.contract.User;
import com.example.incidentreport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService=userService;
    }

    @PostMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Missing mandatory fields," +
                    "username already exist")})
    public ResponseEntity<ResponseResult> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("User Created"));
    }

    @PutMapping(value = "/user/{userName}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "User does not exist")})
    public ResponseEntity<ResponseResult> updateUser(@PathVariable String userName, @RequestBody User user) {
        userService.updateUser(userName, user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("User Updated"));
    }

    @DeleteMapping(value = "/user/{userName}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "User does not exist")})
    public ResponseEntity<ResponseResult> deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("User Deleted"));
    }


    @GetMapping(value = "/user/{userName}")
    @Operation(summary = "Get a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User list populated"),
    @ApiResponse(responseCode = "400", description = "User does not exist")})
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        User user = userService.getUserByUserName(userName);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(value = "/users")
    @Operation(summary = "List all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User list populated")})
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
