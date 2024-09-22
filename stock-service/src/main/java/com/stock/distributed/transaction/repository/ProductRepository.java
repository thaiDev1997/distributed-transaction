package com.stock.distributed.transaction.repository;

import com.stock.distributed.transaction.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Iterable<Product> findByName(String name);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Product> findById(Long id);
}
