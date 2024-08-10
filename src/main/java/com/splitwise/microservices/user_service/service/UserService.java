package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.constants.StringConstants;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<User> userList = new ArrayList<>();
        for(Long userId : userIds)
        {
            Optional<User> optional = userRepository.findById(userId);
            if(optional != null && optional.isPresent())
            {
                userList.add(optional.get());
            }
        }
        return userList;
    }

    public String getUserNameById(Long userId) {

        return userRepository.getUserNameById(userId);
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

    private Map<Long, String> getUserNamesMap(List<Long> userIds) {
        Map<Long, String> userNameMap = new HashMap<>();
        if(userIds == null && userIds.isEmpty())
        {
            return userNameMap;
        }
        else
        {
            List<User> userList = getUsersDetailById(userIds);
            userNameMap = userList.stream().collect(Collectors.toMap(user-> user.getUserId(),
                    user -> user.getFirstName() + " " + user.getLastName()));
        }
        return userNameMap;
    }
}
