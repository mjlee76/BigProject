package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);

    User findByPhone(String phoneNum);

    User findByEmail(String eMail);

    List<User> findByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.phone = :phone")
    List<User> findByUserNameAndPhone(@Param("userName") String userName, @Param("phone") String phone);
}
