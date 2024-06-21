package com.splitwise.microservices.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name ="group_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long groupId;
    @Column(name="grp_name")
    private String groupName;
    @Column(name="created_by")
    private Integer createdBy;
    @Column(name="create_date")
    private Date createdAt;
    @Column(name="last_update_date")
    private Date lastUpdated;

}
