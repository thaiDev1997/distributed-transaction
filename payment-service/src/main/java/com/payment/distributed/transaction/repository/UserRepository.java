package com.payment.distributed.transaction.repository;

import com.payment.distributed.transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Iterable<User> findByName(String name);
}
