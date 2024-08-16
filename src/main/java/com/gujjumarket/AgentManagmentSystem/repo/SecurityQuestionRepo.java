package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gujjumarket.AgentManagmentSystem.model.SecurityQuestions;

public interface SecurityQuestionRepo extends JpaRepository<SecurityQuestions, Integer>{

	List<SecurityQuestions> findAll();

}
