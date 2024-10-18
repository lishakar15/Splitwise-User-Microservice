package com.splitwise.microservices.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteLinkRequest {
    private Long inviterId;
    private Long groupId;
    private String baseUrl;
}
