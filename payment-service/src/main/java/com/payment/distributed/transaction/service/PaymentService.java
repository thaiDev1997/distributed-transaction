package com.payment.distributed.transaction.service;

import com.payment.distributed.transaction.entity.User;
import com.payment.distributed.transaction.entity.UserBalance;
import com.payment.distributed.transaction.repository.PaymentRepository;
import com.payment.distributed.transaction.repository.UserBalanceRepository;
import com.payment.distributed.transaction.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PaymentService {
    UserRepository userRepository;
    UserBalanceRepository userBalanceRepository;
    PaymentRepository paymentRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i < 3; i++) {
            Iterator<User> iterator = userRepository.findByName("User " + i).iterator();
            if (!iterator.hasNext()) {
                User user = User.of("User " + i, "user " + i + "@gmail.com", "0123456789" + i,
                        "Location " + i, null);
                user = userRepository.save(user);

                UserBalance userBalance = new UserBalance(i * 1000.0, null);
                user.setBalance(userBalance);
                userBalance.setUser(user);
                userBalanceRepository.save(userBalance);
            } else {
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    UserBalance userBalance = user.getBalance();
                    userBalance.setAmount((i * 1000.0));
                    userBalanceRepository.save(userBalance);
                }
            }
        }
    }
}
