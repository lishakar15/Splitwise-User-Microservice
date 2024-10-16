package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupMapper {

    public List<GroupMemberDetails> getGroupMemberDetailsFromGroup(Long groupId, List<GroupMemberDetails> groupMemberDetails)
    {
        List<GroupMemberDetails> groupMemberDetailsList = new ArrayList<>();
        for(GroupMemberDetails memberDetails : groupMemberDetails){
            memberDetails.setGroupId(groupId);
            groupMemberDetailsList.add(memberDetails);
        }
        return groupMemberDetailsList;
    }
}
