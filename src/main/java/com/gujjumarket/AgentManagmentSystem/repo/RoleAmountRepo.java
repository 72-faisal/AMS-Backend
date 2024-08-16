package com.gujjumarket.AgentManagmentSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.RoleAmount;

@Repository
public interface RoleAmountRepo extends JpaRepository<RoleAmount, Long> {

	RoleAmount findByRole(String role);

	
}
