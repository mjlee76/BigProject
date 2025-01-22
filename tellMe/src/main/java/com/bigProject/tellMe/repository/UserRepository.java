package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);

    User findByPhone(String phoneNum);

    User findByEmail(String eMail);
}
