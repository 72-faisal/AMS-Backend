package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.DistrictheadRepo;
import com.gujjumarket.AgentManagmentSystem.utils.passwordHasher;

@Service
public class DistrictHeadService {

	@Autowired
	DistrictheadRepo dhRepo;

	public User getprofile(Integer userid1) {
		User u = dhRepo.getReferenceById(userid1);
		return u;
	}

	public User changepasswordFirsttime(User u, String np, String cnp, String question1,String question2,
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
			return dhRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public User changepassword(String password, String np, String cnp, User u) {
		if (passwordHasher.verifyPassword(password, u.getUserpassword()) && np.equals(cnp)) {
			String hashPassword = passwordHasher.hashPassword(cnp);
			u.setUserpassword(hashPassword);
			return dhRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch here");
	}
	
//    public List<Object> getProductWiseTargetTeamSaleMonthly(Long districtHeadId) {
//        // Implement logic to retrieve product-wise target team sales for the month
//        // You may return a list of objects containing product details and sales data
//    }
//
//    public List<Object> getProductWiseTargetTeamSaleQuarterly(Long districtHeadId) {
//        // Implement logic to retrieve product-wise target team sales for the quarter
//        // You may return a list of objects containing product details and sales data
//    }
//
//    public List<Object> getProductWiseTargetTeamSaleYearly(Long districtHeadId) {
//        // Implement logic to retrieve product-wise target team sales for the year
//        // You may return a list of objects containing product details and sales data
//    }
//
//    public List<Object> getCommissionDetails(Long districtHeadId) {
//        // Implement logic to retrieve commission details for the District Head's team
//        // You may return a list of objects containing commission details
//    }
//
//    public boolean canWithdraw(Long districtHeadId) {
//        // Implement logic to check if the District Head can withdraw commission
//        // You may calculate the total commission earned by the team and check against a threshold
//    }
//
//    public List<Object> getPaymentHistory(Long districtHeadId) {
//        // Implement logic to retrieve payment history for the District Head's team
//        // You may return a list of objects containing payment history details
//    }

}
