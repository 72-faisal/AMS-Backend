package com.gujjumarket.AgentManagmentSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class SecurityQuestions {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int Qid;
	private String Question;
	
}
