package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

//	public Payment findBytransactionId(String string);

	public Payment findByTransactionId(String transactionId);


	List<Payment> findByCustomer_Cid(int cid);
}
