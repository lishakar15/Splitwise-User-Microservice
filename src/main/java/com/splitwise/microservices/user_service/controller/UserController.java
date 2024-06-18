package com.splitwise.microservices.user_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/register-user")
    public void registerUser()
    {

    }
    @PostMapping("/login-user")
    public void loginUser()
    {
        //Write code for Authentication
    }

    @GetMapping("/get-user/{userId}")
    public void getUserDetails()
    {

    }

    @PutMapping("/update-user/")
    public void updateUserDetails()
    {

    }

}
