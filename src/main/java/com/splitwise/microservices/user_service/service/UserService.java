package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.entity.User;

public interface UserService {

    public User saveUser(User user);

    public String getUserPassword(String emailId);
}
