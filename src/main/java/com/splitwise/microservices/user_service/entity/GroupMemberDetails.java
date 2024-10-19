package com.splitwise.microservices.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name ="group_member_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="grp_member_id")
    private Long groupMemberId;
    @Column(name="group_id")
    private Long groupId;
    @Column(name="user_id")
    private Long userId;
    @Transient
    private String userName;
    @Column(name="join_date")
    private Date joinedAt;
}
