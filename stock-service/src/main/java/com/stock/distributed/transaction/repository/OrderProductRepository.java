package com.stock.distributed.transaction.repository;

import com.stock.distributed.transaction.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    OrderProduct findByOrderId(long orderId);
}
