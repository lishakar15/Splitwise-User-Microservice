package com.splitwise.microservices.user_service.service;

import com.google.gson.Gson;
import com.splitwise.microservices.user_service.clients.ActivityClient;
import com.splitwise.microservices.user_service.constants.StringConstants;
import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.enums.ActivityType;
import com.splitwise.microservices.user_service.external.ActivityRequest;
import com.splitwise.microservices.user_service.model.GroupDataResponse;
import com.splitwise.microservices.user_service.model.GroupMember;
import com.splitwise.microservices.user_service.repository.GroupMemberDetailsRepository;
import com.splitwise.microservices.user_service.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroupService{
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupMemberDetailsRepository groupMemberDetailsRepository;
    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    ActivityClient activityClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    public Group saveGroupDetails(Group group) {
        return groupRepository.save(group);
    }

    public GroupMemberDetails addGroupMember(GroupMemberDetails groupMemberDetails) {
        GroupMemberDetails savedGroupMemberDetails = groupMemberDetailsRepository.save(groupMemberDetails);
        createGroupMemberActivity(ActivityType.USER_ADDED,groupMemberDetails);
        return savedGroupMemberDetails;
    }

    private void createGroupMemberActivity(ActivityType activityType, GroupMemberDetails groupMemberDetails) {
        if(groupMemberDetails != null)
        {
            ActivityRequest activityRequest = new ActivityRequest();
            activityRequest.setActivityType(activityType);
            activityRequest.setGroupId(groupMemberDetails.getGroupId());
            activityRequest.setCreateDate(groupMemberDetails.getJoinedAt());
            StringBuilder sb = new StringBuilder();
            String groupName = getGroupNameById(groupMemberDetails.getGroupId());
            if(ActivityType.USER_ADDED.equals(activityType))
            {
                sb.append(StringConstants.USER_ID_PREFIX);
                sb.append(groupMemberDetails.getUserId());
                sb.append(StringConstants.USER_ID_SUFFIX);
                sb.append(StringConstants.USER_JOINED_GROUP);
            }
            else if(ActivityType.USER_REMOVED.equals(activityType))
            {
                sb.append(StringConstants.USER_ID_PREFIX);
                sb.append(groupMemberDetails.getUserId());
                sb.append(StringConstants.USER_ID_SUFFIX);
                sb.append(StringConstants.USER_LEFT_GROUP);;
            }
            sb.append(groupName);
            activityRequest.setMessage(sb.toString());
            try
            {
                //Send to Orchestrate
            }
            catch(Exception ex)
            {
                LOGGER.error("Error occurred in createGroupMemberActivity()",ex);
            }
        }
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
    public List<GroupDataResponse> getUserGroupDataList(List<Long> groupIds)
    {
        if(groupIds == null )
        {
            return null;
        }
        List<GroupDataResponse> groupDataResponseList = new ArrayList<>();
        for(Long groupId : groupIds)
        {
            List<GroupMember> groupMembers = new ArrayList<>();
            GroupDataResponse groupDataResponse = new GroupDataResponse();
            Map<Long, String> groupMembersMap = userService.getUserNameMapByGroupId(groupId);
            String groupName = groupRepository.getGroupNameById(groupId);
            for(Map.Entry<Long,String> memberMapEntry : groupMembersMap.entrySet())
            {
                GroupMember groupMember = GroupMember.builder()
                        .userId(memberMapEntry.getKey())
                        .userName(memberMapEntry.getValue())
                        .build();
                groupMembers.add(groupMember);
            }
            groupDataResponse.setGroupName(groupName);
            groupDataResponse.setGroupId(groupId);
            groupDataResponse.setGroupMembers(groupMembers);

            groupDataResponseList.add(groupDataResponse);
        }
        return groupDataResponseList;
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
        if(rowsAffected>0)
        {
            GroupMemberDetails groupMemberDetails= new GroupMemberDetails();
            groupMemberDetails.setUserId(userId);
            groupMemberDetails.setGroupId(groupId);
            createGroupMemberActivity(ActivityType.USER_REMOVED,groupMemberDetails);
        }
        return rowsAffected > 0 ? true : false;
    }

    public String getGroupNameById(Long groupId) {
        return  groupRepository.getGroupNameById(groupId);
    }

    public GroupDataResponse getUserDataByGroupId(Long groupId, Long userId) {
        GroupDataResponse groupDataResponse = null;
        GroupMemberDetails groupMemberDetails = groupMemberDetailsRepository.findByGroupIdAndUserId(groupId, userId);
        if (groupMemberDetails != null && groupMemberDetails.getGroupMemberId()>0) {
            groupDataResponse = new GroupDataResponse();
            List<GroupMember> groupMembers = new ArrayList<>();
            Map<Long, String> groupMembersMap = userService.getUserNameMapByGroupId(groupId);
            String groupName = groupRepository.getGroupNameById(groupId);
            for (Map.Entry<Long, String> memberMapEntry : groupMembersMap.entrySet()) {
                GroupMember groupMember = GroupMember.builder()
                        .userId(memberMapEntry.getKey())
                        .userName(memberMapEntry.getValue())
                        .build();
                groupMembers.add(groupMember);
            }
            groupDataResponse.setGroupName(groupName);
            groupDataResponse.setGroupId(groupId);
            groupDataResponse.setGroupMembers(groupMembers);
        }
        return groupDataResponse;
    }

    public Map<Long, String> getGroupNameMapByUserId(Long userId) {
        Map<Long,String> groupNameMap = new HashMap<>();
        List<Long> groupIds = getAllGroupIdsOfUser(userId);
        List<Group> groupList = groupRepository.findByGroupIdIn(groupIds);
        groupNameMap = groupList.stream().collect(Collectors.toMap(g -> g.getGroupId(), g-> g.getGroupName()));
        return groupNameMap;
    }
    public List<Long> getGroupMembersUserIdByGroupIds(List<Long> groupIds)
    {
        List<Long> membersUserId = groupMemberDetailsRepository.getUserIdsByGroupIdIn(groupIds);
        return membersUserId;
    }
}
