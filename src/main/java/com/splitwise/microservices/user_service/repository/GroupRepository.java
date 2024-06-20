package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

}
