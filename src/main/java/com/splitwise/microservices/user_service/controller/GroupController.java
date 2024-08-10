package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.entity.Users;
import com.splitwise.microservices.user_service.mapper.GroupMapper;
import com.splitwise.microservices.user_service.mapper.UserMapper;
import com.splitwise.microservices.user_service.model.UserModel;
import com.splitwise.microservices.user_service.service.GroupService;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    GroupService groupService;
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    GroupMapper groupMapper;

    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestBody Group group)
    {
        if(group == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //Save group details
        Group savedGroup = groupService.saveGroupDetails(group);
        if(savedGroup == null)
        {
            return new ResponseEntity<>("Error occurred while saving Group Details",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Add the creator as a Group Member
        GroupMemberDetails groupMemberDetails = groupMapper.getGroupMemberDetailsFromGroup(savedGroup);
        groupService.addGroupMember(groupMemberDetails);
        return new ResponseEntity<>("Group Details Saved Successfully",HttpStatus.OK);
    }

    @PostMapping("/add-members")
    public ResponseEntity<String> addGroupMembers(@RequestBody GroupMemberDetails groupMemberDetails)
    {
        if(groupMemberDetails == null)
        {
            return new ResponseEntity<>("Invalid input to add members",HttpStatus.NOT_FOUND);
        }
        GroupMemberDetails groupMemberDetails1 = groupService.addGroupMember(groupMemberDetails);
        return new ResponseEntity<>("Member added successfully to the group",HttpStatus.NOT_FOUND);

    }
    @PutMapping("/update-group")
    public ResponseEntity<String> updateGroup(@RequestBody Group group)
    {
        if(group == null || group.getGroupId()==null)
        {
            return new ResponseEntity<>("Error occurred while updating Group",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Group updatedGroup = groupService.saveGroupDetails(group);
        return new ResponseEntity<>("Group updated successfully",HttpStatus.OK);
    }
    @GetMapping("/get-members/{groupId}")
    public ResponseEntity<List<UserModel>> getGroupMembers(@PathVariable("groupId") Long groupId)
    {
        if(groupId == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //Get Member Ids
        List<Long> memberIds = groupService.getAllUserIdByGroupId(groupId);
        if(memberIds == null || memberIds.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //Get Users Details with Member Ids
        List<Users> users = userService.getUsersDetailById(memberIds);
        if(users == null || users.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<UserModel> memberDetailsList = userMapper.getUserModel(users);
        return new ResponseEntity<>(memberDetailsList,HttpStatus.OK); //Need to convert to json and return
    }

    /**
     * This method return list group name a user participating
     * @param userId
     */
    @GetMapping("/get-groups/{userId}")
    public ResponseEntity<List<String>> getAllTheGroupOfUser(@PathVariable("userId") Long userId)
    {
        List<Long> groupIds = groupService.getAllGroupIdsOfUser(userId);
        if(groupIds == null || groupIds.isEmpty())
        {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
        List<String> groupNames = groupService.getGroupNamesById(groupIds);
        if(groupNames == null || groupNames.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(groupNames, HttpStatus.OK);
    }
    @GetMapping("/get-group-name/{groupId}")
    public ResponseEntity<String> getGroupNameById(@PathVariable("groupId") Long groupId)
    {
        if(groupId == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String groupName = groupService.getGroupNameById(groupId);
        if(!StringUtils.hasLength(groupName))
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(groupName,HttpStatus.OK);
    }

    /**
     * Method to delete the group (Only Admin can perform this)
     * @param groupId
     */
    @DeleteMapping("/delete-group/{groupId}")
    public void deleteGroup(@PathVariable("groupId") Long groupId)
    {
        //Todo add check for Admin user of the group
        groupService.deleteGroupAndMembers(groupId);
    }

    @DeleteMapping("/{userId}/leave-group/{groupId}")
    public ResponseEntity<String> leaveGroup(@PathVariable("userId") Long userId,@PathVariable Long groupId)
    {
        //Todo Do check for any pending out standings
        boolean isDeleted = groupService.deleteGroupMember(userId,groupId);
        if(isDeleted)
        {
            return new ResponseEntity<>("You Left the group successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error occurred while performing leave group",HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
