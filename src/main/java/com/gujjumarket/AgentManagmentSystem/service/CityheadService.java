package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.CityheadRepo;
import com.gujjumarket.AgentManagmentSystem.utils.passwordHasher;

@Service
public class CityheadService {

	@Autowired
	CityheadRepo cihRepo;
//	@Autowired
//	private CommissionDetailsRepo commissionDetailsRepo; // Assuming you have a repository for commission details
//	@Autowired
//	private SellRepo sellRepo; // Assuming you have a repository for target sales
//	@Autowired
//	private ProductRepo pRepo; // Assuming you have a repository for products
//	@Autowired
//	private PaymentHistoryRepo paymentHistoryRepo;

	public User getprofile(Integer userid1) {
		User u = cihRepo.getReferenceById(userid1);
		return u;
	}

	public User changepasswordFirsttime(User u, String np, String cnp, String question1, String question2,
			String securityanswer1, String securityanswer2, String password) {

		if (u.getUserpassword().equals(password) && np.equals(cnp)) {
			System.out.println("inside service if block");
//			u.setSecurityanswer1(securityanswer1);
//			u.setSecurityanswer2(securityanswer2);
//			u.setQuestion1(password);
			u.setFirstTimeLogin(false);
			String hashPassword = passwordHasher.hashPassword(password);
			u.setUserpassword(hashPassword);
//			u.setQuestion1(question1);
//			u.setQuestion2(question2);
			System.out.println("password hashed");
			return cihRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public User changepassword(String password, String np, String cnp, User u) {
		if (passwordHasher.verifyPassword(password, u.getUserpassword()) && np.equals(cnp)) {
			String hashPassword = passwordHasher.hashPassword(cnp);
			u.setUserpassword(hashPassword);
			return cihRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch here");
	}

//	by faisal.
//	public List<ComissionDetails> getCommissionDetails(Integer userId) {
//		try {
//			return commissionDetailsRepo.findByUserId(userId);
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to retrieve commission details.", e);
//		}
//	}
//
//	public ComissionDetails approveCommission(Integer userId, Integer transactionId) {
//		try {
//			ComissionDetails commission = commissionDetailsRepo.findByUserIdAndTransactionId(userId, transactionId);
//			if (commission != null) {
//				commission.setSaleStatus("Approved");
//				commissionDetailsRepo.save(commission);
//				return commission;
//			}
//			return null;
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to approve commission.", e);
//		}
//	}
//
//	public boolean canWithdraw(Integer userId) {
//		try {
//			List<ComissionDetails> commissions = commissionDetailsRepo.findByUserIdAndSaleStatus(userId, "Approved");
//			double totalCommission = commissions.stream().mapToDouble(ComissionDetails::getCommissionEarned).sum();
//			return totalCommission >= 100;
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to check withdrawal eligibility.", e);
//		}
//	}
//
//	public List<Sell> getProductWiseTargetSales(Integer sellid, String timePeriod) {
//		try {
//			return sRepo.findBysellidAndTimePeriod(sellid, timePeriod);
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to retrieve product-wise target sales.", e);
//		}
//	}
//
//	public List<Product> getAllProducts() {
//		try {
//			return pRepo.findAll();
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to retrieve all products.", e);
//		}
//	}
//
//	public List<PaymentHistory> getPaymentHistory(Long payid) {
//		try {
//			return paymentHistoryRepo.findBypayid(payid);
//		} catch (Exception e) {
//			// Log the exception
//			throw new RuntimeException("Failed to retrieve payment history.", e);
//		}
//	}
}
