package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        //Todo Need to encrypt the password and save it
        User savedUser = userService.saveUser(user);
        if(null != savedUser)
        {
           return new ResponseEntity<>("User Saved successfully",HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Error occurred while saving user details",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login-user")
    public ResponseEntity<String> loginUser(@RequestParam("loginParam") String loginParameter,
                                            @RequestParam("password") String password)
    {
        //Write code for Authentication
    
        //Need to write code to decrypt password
        String userPassword = userService.getUserPassword(loginParameter);
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
    public ResponseEntity<User> getUserDetails(@PathVariable("userId")Long userId)
    {
        if(userId == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<User> optional = userService.getUserById(userId);
        if(!optional.isPresent())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
        {
            return new ResponseEntity<>(optional.get(),HttpStatus.OK);
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<User> updateUserDetails(@RequestBody User user)
    {
        //Todo Need to check if the id already exists and then update
        if(user == null || user.getUserId() == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User updatedUser = userService.saveUser(user);
        System.out.println("User updated successfully");
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);

    }

    @GetMapping("/listUsers")
    public void getAllUsers()
    {
        //Return all users
    }

}
