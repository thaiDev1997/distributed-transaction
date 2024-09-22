package com.payment.distributed.transaction.repository;

import com.payment.distributed.transaction.entity.UserBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserBalance> findByUserId(Long userId);
}
