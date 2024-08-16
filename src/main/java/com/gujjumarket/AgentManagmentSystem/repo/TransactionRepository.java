package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.Payment;

@Repository
public interface TransactionRepository extends JpaRepository<Payment, Long> {

	@Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
	Optional<Payment> findByTransactionId(Long transactionId);

}
