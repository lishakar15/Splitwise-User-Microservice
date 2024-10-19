package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    @Query("Select count (F.friendsId) from  Friends F where (F.userId1 =:userId1 and F.userId2 =:userId2) or  (F.userId1 =:userId2 and F.userId2 =:userId1)")
    int findCountByUserId1OrUserId2(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    @Query("Select F from Friends F where F.userId1 =:userId or F.userId2 =:userId")
    List<Friends> getFriendsByUserId(@Param("userId") Long userId);

}
