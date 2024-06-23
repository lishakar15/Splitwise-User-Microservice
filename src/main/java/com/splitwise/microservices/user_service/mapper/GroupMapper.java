package com.splitwise.microservices.user_service.mapper;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupMemberDetails getGroupMemberDetailsFromGroup(Group group)
    {
        GroupMemberDetails groupMemberDetails = null;
        if(group != null)
        {
            groupMemberDetails = new GroupMemberDetails();
            groupMemberDetails.setGroupId(group.getGroupId());
            groupMemberDetails.setUserId(group.getCreatedBy());
            groupMemberDetails.setJoinedAt(group.getCreatedAt());
        }

        return groupMemberDetails;
    }
}
