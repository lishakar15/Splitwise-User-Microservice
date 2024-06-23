package com.splitwise.microservices.user_service.controller;

import com.splitwise.microservices.user_service.entity.Group;
import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.mapper.GroupMapper;
import com.splitwise.microservices.user_service.mapper.UserMapper;
import com.splitwise.microservices.user_service.model.UserModel;
import com.splitwise.microservices.user_service.service.GroupService;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getGroupMembers(@PathVariable("groupId") Long groupId)
    {
        if(groupId == null)
        {
            return new ResponseEntity<>("Group Id cannot be null",HttpStatus.BAD_REQUEST);
        }
        //Get Member Ids
        List<Long> memberIds = groupService.getAllUserIdByGroupId(groupId);
        if(memberIds == null || memberIds.isEmpty())
        {
            return new ResponseEntity<>("Group doesn't exist",HttpStatus.OK);
        }
        //Get User Details with Member Ids
        List<User> users = userService.getUsersDetailById(memberIds);
        if(users == null || users.isEmpty())
        {
            return new ResponseEntity<>("No Group Members found",HttpStatus.OK);
        }
        List<UserModel> memberDetailsList = userMapper.getUserModel(users);
        return new ResponseEntity<>(memberDetailsList.toString(),HttpStatus.OK); //Need to convert to json and return
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

    @DeleteMapping("/delete-group/{groupId}")
    public void deleteGroup(@PathVariable("groupId") Long groupId)
    {
        groupService.deleteGroupAndMembers(groupId);
    }

}
