package com.splitwise.microservices.user_service.repository;

import com.splitwise.microservices.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query
    public User getUserByEmailId(String emailId);
    //Get user password by emailId
    @Query(value = "select u.password from user_details u where u.email_Id = :emailId LIMIT 1",nativeQuery =true)
    public String getUserPasswordByEmailId(@Param("emailId") String emailId);
    //Get user password by phone number
    @Query(value ="select u.password from user_details u where u.phone =:phoneNumber LIMIT 1", nativeQuery = true)
    public String getUserPasswordByPhone(@Param("phoneNumber") String loginParameter);
    @Query(value = "select u.firstName from User u where userId =:userId")
    public String getUserNameById(@Param("userId") Long userId);

    public User findByEmailId(String emailId);
}
