package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

    @Query("select g.groupName from Group g where g.groupId =:groupId ")
    String getGroupNameById(Long groupId);
    Optional<Group> findById(Long groupId);
    List<Group> findByGroupIdIn(List<Long> groupIds);
}
