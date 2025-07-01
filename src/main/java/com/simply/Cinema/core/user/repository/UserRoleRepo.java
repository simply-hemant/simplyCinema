package com.simply.Cinema.core.user.repository;

import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long> {

    // ✅ Fetch the role from database
    Optional<UserRole> findByRole(UserRoleEnum role);

    List<UserRole> findByUserId(Long userId);

}
