package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.utilities.StringConstants;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

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
}
