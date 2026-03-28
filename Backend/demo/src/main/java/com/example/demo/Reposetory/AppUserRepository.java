package com.example.demo.Reposetory;

import com.example.demo.AppUser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE AppUser u SET u.enabled = true WHERE u.email = ?1")
    void enableAppUser(String email);

    @Query("SELECT u FROM AppUser u JOIN u.role r WHERE r.name = :roleName")
    Optional<AppUser> findUserByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM AppUser u JOIN u.role r WHERE r.id = :roleId")
    Optional<AppUser> findUserByRoleName(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query("UPDATE AppUser u SET u.firstName = :firstName, u.lastName = :lastName, " +
            "u.phoneNumber = :phoneNumber WHERE u.id = :userId")
    int updateProfile(@Param("userId") Long userId,
                      @Param("firstName") String firstName,
                      @Param("lastName") String lastName,
                      @Param("phoneNumber") String phoneNumber);

    Optional<AppUser>findByPhoneNumber(String phonNumber);
}
