package com.splitwise.microservices.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDataResponse {
    private Long groupId;
    private String groupName;
    private List<GroupMember> groupMembers;
}
