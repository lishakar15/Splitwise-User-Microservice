package com.splitwise.microservices.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendsId;
    @Column(name = "user_id_1")
    private Long userId1;
    @Column(name = "user_id_2")
    private Long userId2;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "relationship_type")
    private String relationShip;
    @Column(name = "created_at")
    private Date createdAt;
}
