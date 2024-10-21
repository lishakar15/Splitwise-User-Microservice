package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.Friends;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.exception.UserException;
import com.splitwise.microservices.user_service.jwt.JwtUtils;
import com.splitwise.microservices.user_service.model.*;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws UserException {
        if(user == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Boolean isUserRegistered = userService.registerNewUser(user);
        if(isUserRegistered)
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
        if(userCredentials == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return userService.authenticateUserCredential(userCredentials);
    }

    @PostMapping("accept-invite")
    public ResponseEntity<String> acceptUserInvite(@RequestBody InviteRequest inviteRequest){
        if(inviteRequest == null)
        {
            return new ResponseEntity<>("",HttpStatus.OK);
        }
        Boolean isInviteAccepted = userService.saveFriendsFromUserInvite(inviteRequest);
        if(isInviteAccepted)
        {
            String userName = userService.getUserNameById(inviteRequest.getCreatedBy());
            return new ResponseEntity<>("You are now friends with "+userName, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Invite accept failed", HttpStatus.OK);
        }
    }
    @PostMapping("/invite-link")
    public ResponseEntity<String> getInvitationLink(@RequestBody InviteLinkRequest inviteRequest){
        if(inviteRequest == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String link = userService.createInviteLink(inviteRequest);
        if(link == null)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(link,HttpStatus.OK);

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
    @GetMapping("/get-all-friends/{userId}")
    public ResponseEntity<List<UserModel>> getAllFriendsByUserId(@PathVariable("userId") Long userId){
        if(userId == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<UserModel>  friendsList = userService.getAllFriendsList(userId);
        return new ResponseEntity<>(friendsList,HttpStatus.OK);
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
