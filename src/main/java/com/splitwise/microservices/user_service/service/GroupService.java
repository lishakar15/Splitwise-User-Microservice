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

    public List<Long> getAllUserIdByGroupId(Long groupId) {
       return groupMemberDetailsRepository.getAllUserIdByGroupId(groupId);
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

    public List<Long> getAllGroupIdsOfUser(Long userId) {
        return groupMemberDetailsRepository.getAllGroupIdsOfUser(userId);
    }

    public void deleteGroupAndMembers(Long groupId) {
        groupRepository.deleteById(groupId);
        groupMemberDetailsRepository.deleteByGroupId(groupId);
    }

    public boolean deleteGroupMember(Long userId, Long groupId) {
        int rowsAffected = groupMemberDetailsRepository.deleteGroupMember(userId,groupId);
        return rowsAffected > 0 ? true : false;
    }
}
