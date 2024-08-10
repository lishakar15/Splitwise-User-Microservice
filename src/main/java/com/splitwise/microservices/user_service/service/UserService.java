package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.constants.StringConstants;
import com.splitwise.microservices.user_service.entity.Users;
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

    public Users saveUser(Users users) {

       return userRepository.save(users);

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

    public Optional<Users> getUserById(Long userId) {

        return userRepository.findById(userId);
    }

    public List<Users> getUsersDetailById(List<Long> userIds) {
        List<Users> usersList = new ArrayList<>();
        for(Long userId : userIds)
        {
            Optional<Users> optional = userRepository.findById(userId);
            if(optional != null && optional.isPresent())
            {
                usersList.add(optional.get());
            }
        }
        return usersList;
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
    public Users getUserDetailsByEmailId(String emailId)
    {
        Users users = null;
        if(StringUtils.hasLength(emailId))
        {
            users = userRepository.findByEmailId(emailId);
        }
        return users;
    }

    private Map<Long, String> getUserNamesMap(List<Long> userIds) {
        Map<Long, String> userNameMap = new HashMap<>();
        if(userIds == null && userIds.isEmpty())
        {
            return userNameMap;
        }
        else
        {
            List<Users> usersList = getUsersDetailById(userIds);
            userNameMap = usersList.stream().collect(Collectors.toMap(user-> user.getUserId(),
                    user -> user.getFirstName() + " " + user.getLastName()));
        }
        return userNameMap;
    }
}
