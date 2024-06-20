package com.splitwise.microservices.user_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {

    @PostMapping("/create-group")
    public void createGroup()
    {

    }
    @PutMapping("/update-group")
    public void updateGroup()
    {

    }
    @GetMapping("/get-members/{groupId}")
    public void getGroupMembers(Long groupId)
    {

    }
    @GetMapping("/get-groups/{userId}")
    public void getAllTheGroupOfUser(Long userId)
    {

    }


}
