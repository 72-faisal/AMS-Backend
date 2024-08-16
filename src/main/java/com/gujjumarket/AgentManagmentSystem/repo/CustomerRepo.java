package com.gujjumarket.AgentManagmentSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gujjumarket.AgentManagmentSystem.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
	@Query("SELECT c FROM Customer c WHERE c.customername = :cN")
	Customer findbyname(String cN);

	
}
