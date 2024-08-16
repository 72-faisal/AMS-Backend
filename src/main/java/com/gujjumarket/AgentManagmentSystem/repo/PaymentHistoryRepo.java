//package com.gujjumarket.AgentManagmentSystem.repo;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import com.gujjumarket.AgentManagmentSystem.model.Customer;
//import com.gujjumarket.AgentManagmentSystem.model.PaymentHistory;
//
//import jakarta.transaction.Transactional;
//
//@Repository
//public interface PaymentHistoryRepo extends JpaRepository<PaymentHistory, Long> {
//
////	List<PaymentHistory> findBypayid(Long payid);
////
////	void managePaymentHistory(PaymentHistory paymentHistory);
//
//	@Modifying
//	@Transactional
//	default void managePaymentHistory(PaymentHistory paymentHistory) {
//		save(paymentHistory);
//	}
//
////	@Query(	"SELECT COUNT(*) FROM PaymentHistory WHERE cid = :cid AND Status IN (:statuses)")
//	boolean existsByCustomerAndStatusIn(Customer customer, List<String> asList);
//}
