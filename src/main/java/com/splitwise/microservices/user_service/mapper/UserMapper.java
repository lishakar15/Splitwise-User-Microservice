package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.model.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public List<UserModel> getUserModel(List<User> users) {
        List<UserModel> memberDetailsList = new ArrayList<>();
        for(User user : users)
        {
            UserModel userModel = new UserModel();
            userModel.setFirstName(user.getFirstName());
            userModel.setLastName(user.getLastName());
            userModel.setEmailId(user.getEmailId());
            userModel.setPhone(user.getPhoneNumber());
            memberDetailsList.add(userModel);
        }
        return memberDetailsList;
    }
}
