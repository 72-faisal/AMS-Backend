package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.CountryheadRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.utils.passwordHasher;

@Service
public class ContryheadService {
	@Autowired
	CountryheadRepo chRepo;
	
	@Autowired
	Userrepo userrepo;

//	public User getprofile(Integer userid1) {
//		User u = chRepo.getReferenceById(userid1);
//		return u;
//	}
	
	public User getprofile(Integer userid1) {
	    User u = userrepo.getReferenceById(userid1);
	    if (u.getManageBy() != 0) {
	        User manageByUser = userrepo.getReferenceById(u.getManageBy());
	        System.out.println("Manage By User: " + manageByUser); // Log to check if manageByUser is correctly fetched
	        u.setManageByUsername(manageByUser.getUsername());
	        u.setManageByUsermobile(manageByUser.getUsermobile());
	    }
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
			return chRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public User changepassword(String password, String np, String cnp, User u) {
		if (passwordHasher.verifyPassword(password, u.getUserpassword()) && np.equals(cnp)) {
			String hashPassword = passwordHasher.hashPassword(cnp);
			u.setUserpassword(hashPassword);
			return chRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch here");
	}
// --f
//	public void manageCommission(ComissionDetails commissionRate) {
//
//		if (commissionRate != null) {
//			// Perform validation logic here
//			if (isValidCommission(commissionRate)) {
//				commissionDetailsRepo.save(commissionRate);
//			} else {
//				// Handle validation failure
//				throw new IllegalArgumentException("Invalid commission details");
//			}
//		} else {
//			throw new IllegalArgumentException("Commission details cannot be null");
//		}
//	}
//
//	private boolean isValidCommission(ComissionDetails commissionRate) {
//		// Check if commission rate is within valid range
//		return commissionRate.getCommissionRate() >= 0 && commissionRate.getCommissionRate() <= 100;
//	}
//
//	public User monitorTeam(User user) {
//		if (user != null) {
//			// Additional logic can be added here if needed
//			return chRepo.save(user);
//		} else {
//			throw new IllegalArgumentException("User cannot be null");
//		}
//	}
//
//	public void manageUser(User user) {
//
//		if (user != null) {
//			// Perform validation logic here
//			if (isValidUser(user)) {
//				chRepo.save(user);
//			} else {
//				// Handle validation failure
//				throw new IllegalArgumentException("Invalid user details");
//			}
//		} else {
//			throw new IllegalArgumentException("User cannot be null");
//		}
//	}
//
//	private boolean isValidUser(User user) {
//
////	   Check if email is unique
//		return chRepo.findById(user.getUserid()) == null;
//	}
//
//	public void manageProduct(Product product) {
//
//		if (product != null) {
//			// Perform validation logic here
//			if (isValidProduct(product)) {
//				pRepo.save(product);
//			} else {
//				// Handle validation failure
//				throw new IllegalArgumentException("Invalid product details");
//			}
//		} else {
//			throw new IllegalArgumentException("Product cannot be null");
//		}
//	}
//
//	private boolean isValidProduct(Product product) {
//		// Perform validation logic here
//		// Check if product name is unique
//		return pRepo.findByPname(product.getPname()) == null;
//	}
//
//	public void managePaymentHistory(PaymentHistory paymentHistory) {
//
//		if (paymentHistory != null) {
//			// Perform validation logic here
//			if (isValidPaymentHistory(paymentHistory)) {
//				paymentHistoryRepo.save(paymentHistory);
//			} else {
//				// Handle validation failure
//				throw new IllegalArgumentException("Invalid payment history details");
//			}
//		} else {
//			throw new IllegalArgumentException("Payment history cannot be null");
//		}
//	}
//
//	private boolean isValidPaymentHistory(PaymentHistory paymentHistory) {
//
//		// Check if payment amount is non-negative
//		return paymentHistory.getTransactionAmount() >= 0;
//	}



}
//@Autowired
//private CommissionDetailsRepo commissionDetailsRepo; // Assuming you have a repository for commission details
////@Autowired
////private SellRepo sellRepo; // Assuming you have a repository for target sales
//@Autowired
//private ProductRepo pRepo; // Assuming you have a repository for products
//@Autowired
//private PaymentHistoryRepo paymentHistoryRepo;
