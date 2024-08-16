package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.Customer;
import com.gujjumarket.AgentManagmentSystem.repo.CustomerRepo;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepo cRepo;

	public Customer findbyname(String cN) {
		return cRepo.findbyname(cN);
	}

}
