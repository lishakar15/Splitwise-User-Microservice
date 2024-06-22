package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.repository.GroupMemberDetailsRepository;
import com.splitwise.microservices.user_service.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService{
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupMemberDetailsRepository groupMemberDetailsRepository;

    public Group saveGroupDetails(Group group) {
        return groupRepository.save(group);
    }

    public GroupMemberDetails addGroupMember(GroupMemberDetails groupMemberDetails) {

        return groupMemberDetailsRepository.save(groupMemberDetails);
    }

    public List<Long> getGroupMemberIds(Long groupId) {
       return groupMemberDetailsRepository.getUserIdByGroupId(groupId);
    }

    public List<String> getGroupNamesById(List<Long> groupIds) {
        List<String> groupNameList = new ArrayList<>();
        for(Long groupId : groupIds)
        {
            String groupName = groupRepository.getGroupNameById(groupId);
            groupNameList.add(groupName);
        }
        return groupNameList;
    }

    public void addGroupMembers(Group savedGroup) {


    }
}
