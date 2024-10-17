package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

}
