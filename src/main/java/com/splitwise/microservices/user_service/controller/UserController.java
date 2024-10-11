package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.jwt.JwtUtils;
import com.splitwise.microservices.user_service.model.LoginResponse;
import com.splitwise.microservices.user_service.model.UserCredentials;
import com.splitwise.microservices.user_service.configuration.CustomUserDetailService;
import com.splitwise.microservices.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder encoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {
        //Todo Check if email or phone number already exists if yes then redirect to login
        if(user == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String encryptedPassword = encoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
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
    public ResponseEntity<?> loginUser(@RequestBody UserCredentials userCredentials)
    {
        Authentication authentication;
        LoginResponse loginResponse;
        if(userCredentials == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //Authentication the user credentials
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getUserName(),
                            userCredentials.getPassword()));
            if(authentication.isAuthenticated())
            {
                //Authentication Successfully Completed Let's generate the JWT Token
                UserDetails userDetails = (UserDetails) authentication.getPrincipal(); //Principal -> Authenticated User
                String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
                Long userId = ((CustomUserDetailService) userDetails).getUserId();
                loginResponse = LoginResponse.builder()
                        .jwtToken(jwtToken)
                        .userName(userDetails.getUsername())
                        .userId(userId)
                        .build();
                //Authentication Completed, Set the Authentication object into the Spring Context for future requests
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        catch(AuthenticationException ex)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loginResponse,HttpStatus.OK);
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
    @PostMapping("/get-user-name-map/")
    public ResponseEntity<Map<Long,String>> getUserNameMapByUserIds(@RequestBody List<Long> userIds){

        if(userIds == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<Long, String> userNameMap = userService.getUserNameMapByUserIds(userIds);
        if(userNameMap == null && userNameMap.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userNameMap, HttpStatus.OK);
    }
    @PostMapping("/get-user-email-map")
    public ResponseEntity<Map<Long,String>> getUserEmailMap(@RequestBody List<Long> userIds){
        if(userIds == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<Long,String> userEmailMap = userService.getUserEmailMap(userIds);
        if(userEmailMap == null && userEmailMap.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userEmailMap, HttpStatus.OK);
    }

    @GetMapping("/get-user-name-map/{groupId}")
    public ResponseEntity<Map<Long,String>> getUserNameMap(@PathVariable("groupId") Long groupId)
    {
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

    @GetMapping("/get-friends-name-map/{userId}")
    public ResponseEntity<Map<Long,String>> getAllFriendsUserNameMap(@PathVariable("userId") Long userId) {
        if(userId == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<Long, String> userNameMap = userService.getAllFriendsUserNameMapByUserId(userId);
        if(userNameMap == null || userNameMap.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userNameMap, HttpStatus.OK);
    }

}
