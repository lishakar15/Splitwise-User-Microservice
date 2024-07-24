package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {
        //Todo Check if email or phone number already exists if yes then redirect to login
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
        if(userPassword != null && userPassword.equals(password))
        {
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
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);

    }
    @GetMapping("/get-user-name/{userId}")
    public ResponseEntity<String> getUsernameById(@PathVariable("userId") Long userId)
    {
        if(userId == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String userName = userService.getUserNameById(userId);
        if(!StringUtils.hasLength(userName))
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userName,HttpStatus.OK);
    }
    @GetMapping("/get-user-name-map/{groupId}")
    public ResponseEntity<Map<Long,String>>   getUserNameMap(@PathVariable("groupId") Long groupId)
    {
        LOGGER.info("User Microservice called bro");
        if(groupId == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<Long, String> userNameMap = userService.getUserNameMapByGroupId(groupId);
        if(userNameMap == null && userNameMap.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userNameMap, HttpStatus.OK);
    }
    @GetMapping("/listUsers")
    public void getAllUsersOfA()
    {
        //Return all users
    }

}
