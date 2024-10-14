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
        for (User user : users) {
            UserModel userModel = UserModel.builder().firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .emailId(user.getEmailId())
                    .phone(user.getPhoneNumber())
                    .build();
            memberDetailsList.add(userModel);
        }
        return memberDetailsList;
    }
}
