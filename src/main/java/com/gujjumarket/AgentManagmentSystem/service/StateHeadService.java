package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.StateHeadRepo;
import com.gujjumarket.AgentManagmentSystem.utils.passwordHasher;

@Service
public class StateHeadService {

	@Autowired
	StateHeadRepo shRepo;

	public User getprofile(Integer userid1) {
		User u = shRepo.getReferenceById(userid1);
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
			return shRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public User changepassword(String password, String np, String cnp, User u) {
		if (passwordHasher.verifyPassword(password, u.getUserpassword()) && np.equals(cnp)) {
			String hashPassword = passwordHasher.hashPassword(cnp);
			u.setUserpassword(hashPassword);
			return shRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch here");
	}
	
}
