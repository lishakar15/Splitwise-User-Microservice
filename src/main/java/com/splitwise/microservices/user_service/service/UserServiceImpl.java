package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User saveUser(User user) {

       return userRepository.save(user);

    }

    @Override
    public String getUserPassword(String emailId) {
        return userRepository.getUserPasswordByEmailId(emailId);
    }

    }
