//package com.gujjumarket.AgentManagmentSystem.repo;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.gujjumarket.AgentManagmentSystem.model.ComissionDetails;
//
//@Repository
//public interface CommissionDetailsRepo extends JpaRepository<ComissionDetails, Integer> {
//
//	List<ComissionDetails> findByUserRole(String userRole);
//
////	List<ComissionDetails> findByUserId(Integer userId);
////
////	  @Query("SELECT cd FROM ComissionDetails cd WHERE cd.userId = ?1 AND cd.transactionId = ?2")
////	    ComissionDetails findByUserIdAndTransactionId(Integer userId, Integer transactionId);
////
////	    // Another example of custom query
////	    @Query("SELECT cd FROM ComissionDetails cd WHERE cd.userId = ?1 AND cd.saleStatus = ?2")
////	    List<ComissionDetails> findByUserIdAndSaleStatus(Integer userId, String saleStatus);
//
////	void manageCommission(ComissionDetails commissionRate);
//
////	void manageCommission(ComissionDetails commissionRate);
//
//}
