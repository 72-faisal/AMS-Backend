package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;

@Service
public class withdrawalService {


//	 @Autowired
//	    private Userrepo uRepo;
//
	    @Autowired
	    private withdrawalRequestRepo wrepo;
//	    @Autowired
//	    private SellRepo sRepo;

	    public boolean canWithdraw(User user, Double withdrawalAmount) {
	        if (user == null) {
	            System.out.println("User Not Found");
	            return false; // User not found
	        }

	        // Check if the user is allowed to withdraw based on their role
	        switch (user.getRole()) {
	        
	            case "SUBAGENT":
	            	System.out.println();
	            case "AGENT":
	            	System.out.println("agent");
	            case "CITYHEAD":
	            case "DISTRICTHEAD":
	            case "STATEHEAD":
	            case "COUNTRYHEAD":
	                return canWithdrawforRole(user, withdrawalAmount);
	            default:
	                return false; // Role not recognized
	        }
	    }

	    private boolean canWithdrawforRole(User user, Double withdrawalAmount) {
	    	double commissionAmount = user.getTotalCommissionAmount();
	        System.out.println(user.getTotalCommissionAmount());
	        return commissionAmount >= withdrawalAmount && commissionAmount >= 1000;
	   
		}

		

//		public static WithdrawalRequest getByUserId(int Userid) {
//			// TODO Auto-generated method stub
//			return withdrawalRequestRepo.findByUserId(Userid);
//		}

	}
