package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.model.GroupMember;
import com.splitwise.microservices.user_service.model.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GroupMapper {

    public List<GroupMemberDetails> getGroupMemberDetailsWithGroupId(Long groupId, List<GroupMemberDetails> groupMemberDetails)
    {
        List<GroupMemberDetails> groupMemberDetailsList = new ArrayList<>();
        for(GroupMemberDetails memberDetails : groupMemberDetails){
            memberDetails.setGroupId(groupId);
            groupMemberDetailsList.add(memberDetails);
        }
        return groupMemberDetailsList;
    }

    public List<GroupMemberDetails> getGroupMemberDetailsWithUserName(Map<Long, String> userNameMap, List<GroupMemberDetails> groupMemberDetails)
    {
        for(GroupMemberDetails member : groupMemberDetails){
            Long userId = member.getUserId();
            member.setUserName(userNameMap.get(userId));
        }
        return groupMemberDetails;
    }

    public List<GroupMember> getGroupMembersFromUsers(List<User> users){

        List<GroupMember> groupMembers = new ArrayList<>();
        for(User user : users){
            GroupMember groupMember = GroupMember.builder()
                    .userId(user.getUserId())
                    .userName(user.getFirstName()+ " "+user.getLastName())
                    .build();
            groupMembers.add(groupMember);
        }
        return groupMembers;
    }
}
