package org.banshi.Repository;

import org.banshi.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByPhone(String phone);
}
