package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.configuration.CustomUserDetailService;
import com.splitwise.microservices.user_service.constants.StringConstants;
import com.splitwise.microservices.user_service.entity.Friends;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.jwt.JwtUtils;
import com.splitwise.microservices.user_service.mapper.UserMapper;
import com.splitwise.microservices.user_service.model.InviteRequest;
import com.splitwise.microservices.user_service.model.LoginResponse;
import com.splitwise.microservices.user_service.model.UserCredentials;
import com.splitwise.microservices.user_service.model.UserModel;
import com.splitwise.microservices.user_service.repository.FriendsRepository;
import com.splitwise.microservices.user_service.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupService groupService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    FriendsRepository friendsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public User saveUser(User user) {

       return userRepository.save(user);

    }

    public String getUserPassword(String loginParameter) {
        String password = userRepository.getUserPasswordByEmailId(loginParameter);
        if(password == null || StringConstants.EMPTY_STRING.equals(password))
        {
            //Search user by phone number
           password =  userRepository.getUserPasswordByPhone(loginParameter);
        }
        return password;
    }

    public Optional<User> getUserById(Long userId) {

        return userRepository.findById(userId);
    }

    public List<User> getUsersDetailById(List<Long> userIds) {

        return userRepository.findByUserIdIn(userIds);
    }

    public String getUserNameById(Long userId) {

        return userRepository.getUserNameById(userId);
    }

    public Map<Long, String> getUserNameMapByUserIds(List<Long> userIds) {
        Map<Long,String> userNameMap = new HashMap<>();
        for(Long userId: userIds)
        {
            String userName = getUserNameById(userId);
            userNameMap.putIfAbsent(userId,userName);
        }
        return userNameMap;
    }

    public Map<Long, String> getUserNameMapByGroupId(Long groupId) {
        List<Long> userIds = groupService.getAllUserIdByGroupId(groupId);
        if(userIds != null && !userIds.isEmpty())
        {
            return getUserNamesMap(userIds);
        }
        else
        {
            return new HashMap<>();
        }
    }
    public User getUserDetailsByEmailId(String emailId)
    {
        User user = null;
        if(StringUtils.hasLength(emailId))
        {
            user = userRepository.findByEmailId(emailId);
        }
        return user;
    }

    public Map<Long, String> getUserNamesMap(List<Long> userIds) {
        Map<Long, String> userNameMap = new HashMap<>();
        if (userIds != null && !userIds.isEmpty()) {
            List<User> userList = getUsersDetailById(userIds);
            userNameMap = userList.stream().collect(Collectors.toMap(user -> user.getUserId(),
                    user -> user.getFirstName()));
        }
        return userNameMap;
    }

    public Map<Long, String> getUserEmailMap(List<Long> userIds) {
        Map<Long, String> userEmailMap = new HashMap<>();
        if (userIds != null && !userIds.isEmpty()) {
            List<User> userList = getUsersDetailById(userIds);
            userEmailMap = userList.stream().collect(Collectors.toMap(user->user.getUserId(),user -> user.getEmailId()));
        }
        return userEmailMap;
    }

    public Map<Long, String> getAllFriendsUserNameMapByUserId(Long userId) {
        Map<Long, String> userNameMap = new HashMap<>();
        if(userId == null)
        {
            throw new RuntimeException("UserId cannot be null");
        }
        //Get the List of groups user belongs to
        List<Long> groupIds = groupService.getAllGroupIdsOfUser(userId);
        //Get userIds of the group members by group ids\
        List<Long> membersUserIds = groupService.getGroupMembersUserIdByGroupIds(groupIds);
        //Get userNameMap by userIds
        List<User> usersList = userRepository.findByUserIdIn(membersUserIds);
        userNameMap = getUserNamesMap(membersUserIds);
        return userNameMap;
    }

    public ResponseEntity<?> authenticateUserCredential(UserCredentials userCredentials) {
        Authentication authentication;
        LoginResponse loginResponse;
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getUserName(),
                    userCredentials.getPassword()));
            if(authentication.isAuthenticated())
            {
                //Authentication Successfully Completed Let's generate the JWT Token
                CustomUserDetailService userDetails = (CustomUserDetailService) authentication.getPrincipal(); //Principal -> Authenticated User
                String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
                loginResponse = LoginResponse.builder()
                        .jwtToken(jwtToken)
                        .userName(userDetails.getUser().getFirstName())
                        .userId(userDetails.getUserId())
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
            LOGGER.error("Unable to login with the given credentials "+ex);
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loginResponse,HttpStatus.OK);
    }

    public Boolean registerNewUser(User user) {
        Boolean isUserRegistered = false;
        String encryptedPassword = encoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        User savedUser = saveUser(user);
        if(savedUser != null){
            isUserRegistered = true;
        }

        return isUserRegistered;

    }

    public List<UserModel> getUserInfoMapFromUsers(List<User> users) {
        return userMapper.getUserModel(users);
    }

    public Boolean saveFriendsFromUserInvite(InviteRequest inviteRequest) {
        Boolean inviteAccepted = false;
        if(inviteRequest != null)
        {
            Friends friends = Friends.builder()
                    .userId1(inviteRequest.getUserId1())
                    .userId2(inviteRequest.getUserId2())
                    .createdBy(inviteRequest.getCreatedBy())
                    .createdAt(new Date())
                    .build();
            friendsRepository.save(friends);
            inviteAccepted = true;
        }
        return inviteAccepted;
    }
}
