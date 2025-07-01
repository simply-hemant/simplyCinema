package com.simply.Cinema.core.user.repository;

import com.simply.Cinema.core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    //Using Optional<User> to handle null safety.
    // Using existsBy... to improve performance when you just need to check for duplicates.

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);


}
