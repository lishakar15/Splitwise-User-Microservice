package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.repository.UserRepository;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {
        //Todo Check if email or phone number already exists if yes then redirect to login
        System.out.println(user.toString());
        if(user == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else
        {
            //Todo Need to encrypt the password and save it
            User savedUser = userService.saveUser(user);
        }
        return new ResponseEntity<>("User Saved successfully",HttpStatus.OK);
    }
    @PostMapping("/login-user")
    public ResponseEntity<String> loginUser(@RequestParam("emailId") String emailId,@RequestParam("password") String password)
    {
        //Write code for Authentication
    
        //Need to write code to decrypt password
        String userPassword = userService.getUserPassword(emailId);
        System.out.println("userPassword = " +userPassword);
        if(userPassword != null && userPassword.equals(password))
        {
            System.out.println("User logged in successfully");
            return new ResponseEntity<>("Login Successful", HttpStatus.ACCEPTED);
        }
        else
        {
            return new ResponseEntity<>("Login Request failed! Invalid email or password",HttpStatus.BAD_REQUEST);
        }
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
