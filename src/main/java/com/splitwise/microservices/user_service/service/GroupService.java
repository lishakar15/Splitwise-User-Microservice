package com.splitwise.microservices.user_service.service;

import com.splitwise.microservices.user_service.clients.ActivityClient;
import com.splitwise.microservices.user_service.constants.StringConstants;
import com.splitwise.microservices.user_service.entity.Friends;
import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.enums.ActivityType;
import com.splitwise.microservices.user_service.external.Activity;
import com.splitwise.microservices.user_service.external.ActivityRequest;
import com.splitwise.microservices.user_service.mapper.GroupMapper;
import com.splitwise.microservices.user_service.model.GroupDetailsModel;
import com.splitwise.microservices.user_service.model.GroupDataResponse;
import com.splitwise.microservices.user_service.model.GroupMember;
import com.splitwise.microservices.user_service.model.UserModel;
import com.splitwise.microservices.user_service.repository.FriendsRepository;
import com.splitwise.microservices.user_service.repository.GroupMemberDetailsRepository;
import com.splitwise.microservices.user_service.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupMemberDetailsRepository groupMemberDetailsRepository;
    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    ActivityClient activityClient;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    FriendsRepository friendsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);
    @Transactional
    public Boolean saveGroupDetailsFromRequest(GroupDetailsModel groupRequest) {
        if(groupRequest != null)
        {
            Group savedGroup = groupRepository.save(groupRequest.getGroup());
            if(savedGroup == null)
            {
               return false;
            }
            List<GroupMemberDetails> groupMemberDetailsList = groupMapper.getGroupMemberDetailsWithGroupId(savedGroup.getGroupId(), groupRequest.getGroupMemberDetails());
            return saveGroupMembers(groupMemberDetailsList);
        }
        return false;
    }

    public Boolean saveGroupMembers(List<GroupMemberDetails> groupMemberDetails) {
        Boolean isGroupMembersSaved = false;
        try {
            groupMemberDetailsRepository.saveAll(groupMemberDetails);
            for (GroupMemberDetails memberDetails : groupMemberDetails) {
                createGroupMemberActivity(ActivityType.USER_ADDED, memberDetails);
            }
            isGroupMembersSaved = true;
        } catch (Exception ex) {
            LOGGER.error("Error occurred while saving Group Members data");
        }
        return isGroupMembersSaved;
    }
    public Boolean deleteGroupMembers(List<GroupMemberDetails> groupMemberDetails) {
        Boolean isMembersDeleted = false;
        try {
            groupMemberDetailsRepository.deleteAll(groupMemberDetails);
            for (GroupMemberDetails memberDetails : groupMemberDetails) {
                createGroupMemberActivity(ActivityType.USER_REMOVED, memberDetails);
            }
            isMembersDeleted = true;
        } catch (Exception ex) {
            LOGGER.error("Error occurred while deleting Group Members");
        }
        return isMembersDeleted;
    }

    private void createGroupMemberActivity(ActivityType activityType, GroupMemberDetails groupMemberDetails) {
        if (groupMemberDetails != null) {
            try {
                Activity activity = Activity.builder()
                        .activityType(activityType)
                        .createDate(new Date())
                        .groupId(groupMemberDetails.getGroupId())
                        .build();

                StringBuilder sb = new StringBuilder();
                String groupName = getGroupNameById(groupMemberDetails.getGroupId());
                String userName = userService.getUserNameById(groupMemberDetails.getUserId());
                if (ActivityType.USER_ADDED.equals(activityType)) {
                    sb.append(userName);
                    sb.append(StringConstants.USER_JOINED_GROUP);
                } else if (ActivityType.USER_REMOVED.equals(activityType)) {
                    sb.append(userName);
                    sb.append(StringConstants.USER_LEFT_GROUP);
                }
                sb.append(groupName);
                activity.setMessage(sb.toString());
                List<Long> groupMemberIds = getAllUserIdByGroupId(groupMemberDetails.getGroupId());
                ActivityRequest activityRequest = ActivityRequest.builder()
                        .activity(activity)
                        .userIds(groupMemberIds)
                        .build();

                //Send to Orchestrate
                activityClient.sendActivityRequest(activityRequest);
            } catch (Exception ex) {
                LOGGER.error("Error occurred in createGroupMemberActivity()", ex);
            }
        }
    }

    public List<Long> getAllUserIdByGroupId(Long groupId) {
        return groupMemberDetailsRepository.getAllUserIdByGroupId(groupId);
    }

    public List<String> getGroupNamesById(List<Long> groupIds) {
        List<String> groupNameList = new ArrayList<>();
        for (Long groupId : groupIds) {
            String groupName = groupRepository.getGroupNameById(groupId);
            groupNameList.add(groupName);
        }
        return groupNameList;
    }

    public List<GroupDataResponse> getUserGroupDataList(List<Long> groupIds) {
        if (groupIds == null) {
            return null;
        }
        List<GroupDataResponse> groupDataResponseList = new ArrayList<>();
        for (Long groupId : groupIds) {
            List<GroupMember> groupMembers = new ArrayList<>();
            GroupDataResponse groupDataResponse = new GroupDataResponse();
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

        int rowsAffected = groupMemberDetailsRepository.deleteGroupMember(userId, groupId);
        if (rowsAffected > 0) {
            GroupMemberDetails groupMemberDetails = new GroupMemberDetails();
            groupMemberDetails.setUserId(userId);
            groupMemberDetails.setGroupId(groupId);
            createGroupMemberActivity(ActivityType.USER_REMOVED, groupMemberDetails);
        }
        return rowsAffected > 0 ? true : false;
    }

    public String getGroupNameById(Long groupId) {
        return groupRepository.getGroupNameById(groupId);
    }

    public GroupDataResponse getUserDataByGroupId(Long groupId, Long userId) {
        GroupDataResponse groupDataResponse = null;
        GroupMemberDetails groupMemberDetails = groupMemberDetailsRepository.findByGroupIdAndUserId(groupId, userId);
        if (groupMemberDetails != null && groupMemberDetails.getGroupMemberId() > 0) {
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
        Map<Long, String> groupNameMap = new HashMap<>();
        List<Long> groupIds = getAllGroupIdsOfUser(userId);
        List<Group> groupList = groupRepository.findByGroupIdIn(groupIds);
        groupNameMap = groupList.stream().collect(Collectors.toMap(g -> g.getGroupId(), g -> g.getGroupName()));
        return groupNameMap;
    }

    public List<Long> getGroupMembersUserIdByGroupIds(List<Long> groupIds) {
        List<Long> membersUserId = groupMemberDetailsRepository.getUserIdsByGroupIdIn(groupIds);
        return membersUserId;
    }

    public List<UserModel> getGroupMembersByUserId(Long userId) {
        Set<Long> uniqueUserIds = new HashSet<>();
        List<Long> groupIds = getAllGroupIdsOfUser(userId);
        List<Long> userIds = groupMemberDetailsRepository.getAllUserIdInGroupId(groupIds);
        uniqueUserIds.addAll(userIds);
        List<User> users = userService.getUsersDetailById(new ArrayList<>(uniqueUserIds));
        return userService.getUserInfoMapFromUsers(users);
    }

    public List<UserModel> getGroupMembersByGroupId(Long groupId) {
        Set<Long> uniqueUserIds = new HashSet<>();
        List<Long> userIds = groupMemberDetailsRepository.getAllUserIdByGroupId(groupId);
        uniqueUserIds.addAll(userIds);
        List<User> users = userService.getUsersDetailById(new ArrayList<>(uniqueUserIds));
        return userService.getUserInfoMapFromUsers(users);
    }
    @Transactional
    public Boolean joinAsGroupMember(GroupMemberDetails groupMemberDetails) {
        Boolean isJoined = false;
        if(groupMemberDetails != null)
        {
            GroupMemberDetails existinGroupMemberDetails = groupMemberDetailsRepository.findByGroupIdAndUserId(groupMemberDetails.getGroupId(), groupMemberDetails.getUserId());
            if(existinGroupMemberDetails == null || existinGroupMemberDetails.getGroupMemberId() == null )
            {
                groupMemberDetails.setJoinedAt(new Date());
                groupMemberDetailsRepository.save(groupMemberDetails);
                saveFriendsFromInvite(groupMemberDetails);
                createGroupMemberActivity(ActivityType.USER_ADDED, groupMemberDetails);
                isJoined = true;
            }
        }
        return isJoined;
    }

    private void saveFriendsFromInvite(GroupMemberDetails groupMemberDetails) {
        if(groupMemberDetails != null){
            List<Friends> friendsList = new ArrayList<>();
            Long currentUserId = groupMemberDetails.getUserId();
            List<Long> groupMemberIdList = getAllUserIdByGroupId(groupMemberDetails.getGroupId());
            for(Long memberId : groupMemberIdList)
            {
                if(memberId != currentUserId)
                {
                    //Check if the friend relation already exists
                    Boolean isFriendExists = userService.checkForExistingRelationship(memberId, currentUserId);
                    if(!isFriendExists)
                    {
                        Friends friend = Friends.builder()
                                .userId1(currentUserId)
                                .userId2(memberId)
                                .relationShip(StringConstants.GROUP_TYPE)
                                .createdAt(new Date())
                                .build();
                        friendsList.add(friend);
                    }
                }
            }
            friendsRepository.saveAll(friendsList);
        }
    }

    public GroupDetailsModel getGroupDetailsByGroupId(Long groupId) {
        GroupDetailsModel groupDetailsModel = new GroupDetailsModel();
        Optional<Group> optional = groupRepository.findById(groupId);
        if(optional.isPresent()){
            groupDetailsModel.setGroup(optional.get());
            List<GroupMemberDetails> memberDetails = groupMemberDetailsRepository.findByGroupId(groupId);
            List<Long> userIds = memberDetails.stream().map(u -> u.getUserId()).collect(Collectors.toList());
            Map<Long, String> userNameMap = userService.getFullUserNamesMap(userIds);
            List<GroupMemberDetails> memberDetailsList = groupMapper.getGroupMemberDetailsWithUserName(userNameMap, memberDetails);
            groupDetailsModel.setGroupMemberDetails(memberDetailsList);
        }
        return groupDetailsModel;
    }

    public List<GroupMember> getGroupMembersListByUserId(Long userId){
        Set<Long> uniqueUserIds = new HashSet<>();
        List<Long> groupIds = getAllGroupIdsOfUser(userId);
        List<Long> userIds = groupMemberDetailsRepository.getAllUserIdInGroupId(groupIds);
        uniqueUserIds.addAll(userIds);
        List<User> users = userService.getUsersDetailById(new ArrayList<>(uniqueUserIds));
        return groupMapper.getGroupMembersFromUsers(users);
    }

    @Transactional
    public Boolean processGroupUpdate(GroupDetailsModel groupRequest) {
        if (groupRequest == null || groupRequest.getGroup() == null) {
            return false;
        }

        Group newGroup = groupRequest.getGroup();
        Long groupId = newGroup.getGroupId();

        // Check if group exists
        Optional<Group> existingGroupOptional = groupRepository.findById(groupId);
        if (!existingGroupOptional.isPresent()) {
            return false;
        }
        Group existingGroup = existingGroupOptional.get();

        // Update group name if changed
        if (!newGroup.getGroupName().equals(existingGroup.getGroupName())) {
            existingGroup.setGroupName(newGroup.getGroupName());
            groupRepository.save(existingGroup);
        }

        List<GroupMemberDetails> membersFromRequest = groupRequest.getGroupMemberDetails();
        List<Long> existingMemberIds = groupMemberDetailsRepository.getAllUserIdByGroupId(groupId);
        List<GroupMemberDetails> existingMembers = groupMemberDetailsRepository.findByGroupId(groupId);

        List<GroupMemberDetails> newMembers = new ArrayList<>();
        List<GroupMemberDetails> deletedMembers = new ArrayList<>();

        for (GroupMemberDetails member : membersFromRequest) {
            if (!existingMemberIds.contains(member.getUserId())) {
                newMembers.add(member);
            } else {
                existingMemberIds.remove(member.getUserId());
            }
        }

        for (Long deletedMemberId : existingMemberIds) {
            existingMembers.stream()
                    .filter(m -> m.getUserId().equals(deletedMemberId))
                    .findFirst()
                    .ifPresent(deletedMembers::add);
        }

        if (!newMembers.isEmpty()) {
            saveGroupMembers(newMembers);
        }

        if (!deletedMembers.isEmpty()) {
            deleteGroupMembers(deletedMembers);
        }
        return true;
    }

    public List<Group> getAllGroupsOfUser(Long userId) {
        List<Group> groupList = null;
        List<Long> groupIds = getAllGroupIdsOfUser(userId);
        if(groupIds != null && !groupIds.isEmpty())
        {
            groupList = groupRepository.findByGroupIdIn(groupIds);
        }
        return groupList;
    }
}
