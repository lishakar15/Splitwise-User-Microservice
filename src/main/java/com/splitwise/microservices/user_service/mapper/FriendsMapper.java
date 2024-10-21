package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.model.FriendsModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FriendsMapper {

    public List<FriendsModel> getFriendsModelListFromUsers(List<User> users)
    {
        List<FriendsModel> friendsModelList = new ArrayList<>();
        for(User user : users)
        {
            FriendsModel friendsModel = FriendsModel.builder()
                    .userId(user.getUserId())
                    .userName(user.getFirstName()+" "+user.getLastName())
                    .build();
            friendsModelList.add(friendsModel);
        }
        return friendsModelList;
    }
}
