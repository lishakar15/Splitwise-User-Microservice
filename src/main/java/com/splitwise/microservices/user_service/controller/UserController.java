package com.splitwise.microservices.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register-user")
    public void registerUser()
    {

    }
    @PostMapping("/login-user")
    public ResponseEntity<String> loginUser()
    {
        System.out.println("Login Successful!!");
        //Write code for Authentication
        return new ResponseEntity<>("Login Successful", HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-user/{userId}")
    public void getUserDetails()
    {

    }

    @PutMapping("/update-user/")
    public void updateUserDetails()
    {

    }

    @GetMapping("/listUsers")
    public void getAllUsers()
    {
        //Return all users
    }

}
