package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.Users;
import com.splitwise.microservices.user_service.model.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public List<UserModel> getUserModel(List<Users> users) {
        List<UserModel> memberDetailsList = new ArrayList<>();
        for(Users user : users)
        {
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
