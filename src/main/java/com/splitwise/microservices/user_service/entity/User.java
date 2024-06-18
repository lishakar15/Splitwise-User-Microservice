package com.splitwise.microservices.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name="id")
    private Long userId;
    @Column(name="user_name")
    private String userName;
    @Column(name="email")
    private String emailId;
    @Column(name="password")
    private String password;
    @Column(name="phone")
    private String phoneNumber;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="last_update_date")
    private Date lastUpdateDate;
}