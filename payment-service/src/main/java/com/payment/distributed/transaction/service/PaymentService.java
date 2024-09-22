package com.payment.distributed.transaction.service;

import com.common.distributed.transaction.constant.status.PaymentStatus;
import com.common.distributed.transaction.dto.RequestOrder;
import com.common.distributed.transaction.dto.event.OrderEvent;
import com.common.distributed.transaction.dto.event.PaymentEvent;
import com.common.distributed.transaction.dto.event.StockEvent;
import com.payment.distributed.transaction.entity.Payment;
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
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PaymentEvent stockReserved(StockEvent stockEvent) {
        RequestOrder requestOrder = stockEvent.getOrder();
        double orderAmount = requestOrder.getAmount();
        return userBalanceRepository.findByUserId(requestOrder.getUserId())
                .filter(balance -> balance.getAmount() > 0 && balance.getAmount() >= orderAmount)
                .map(balance -> {
                    Payment payment = Payment.of(requestOrder.getUserId(), requestOrder.getOrderId(), orderAmount,
                            requestOrder.getPaymentMode(), PaymentStatus.PROCESSED);
                    paymentRepository.save(payment);
                    balance.setAmount(balance.getAmount() - orderAmount);

                    // assign user's address - phone -> delivery address - phone
                    User user = balance.getUser();
                    requestOrder.setAddress(user.getAddress());
                    requestOrder.setPhone(user.getPhone());
                    return PaymentEvent.builder().order(requestOrder).status(PaymentStatus.PROCESSED).build();
                })
                .orElse(PaymentEvent.builder().order(requestOrder).status(PaymentStatus.FAILED).build());
    }

    @Transactional
    public boolean refundPayment(OrderEvent orderEvent) {
        RequestOrder order = orderEvent.getOrder();
        return paymentRepository
                .findByOrderId(order.getOrderId())
                .filter(payment -> PaymentStatus.PROCESSED.equals(payment.getStatus()))
                .map(payment -> {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    return userBalanceRepository
                            .findByUserId(payment.getUserId())
                            .map(balance -> {
                                balance.setAmount(balance.getAmount() + payment.getAmount());
                                userBalanceRepository.save(balance);
                                return true;
                            }).orElse(false);

                }).orElse(false);
    }
}
