package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.GroupMemberDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberDetailsRepository extends JpaRepository<GroupMemberDetails,Long> {

    @Query("select gm.userId from GroupMemberDetails gm where gm.groupId =:groupId")
    public List<Long> getUserIdByGroupId(@Param("groupId") Long groupId);

    @Query("select gm.groupId from GroupMemberDetails gm where gm.userId =:userId")
    public List<Long> getAllGroupIdsOfUser(@Param("userId")Long userId);

}
