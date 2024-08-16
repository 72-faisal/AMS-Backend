package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.RoleAmount;
import com.gujjumarket.AgentManagmentSystem.repo.RoleAmountRepo;

@Service
public class RoleAmountService {

	@Autowired
    private RoleAmountRepo repository;
    
    public RoleAmount updateAmount(String role, double amount) {
        RoleAmount roleAmount = repository.findByRole(role);
        if (roleAmount != null) {
            roleAmount.setAmount(amount);
            return repository.save(roleAmount);
        }
        return null;
    }
}
