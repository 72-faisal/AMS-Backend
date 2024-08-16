package com.gujjumarket.AgentManagmentSystem.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class WithdrawalRequest {
//--f
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "withdrawal_sequence")
	@SequenceGenerator(name = "withdrawal_sequence", sequenceName = "withdrawal_sequence", allocationSize = 1)
	private Long withdrawalId;
	@ManyToOne
	@JoinColumn(name = "Userid")
	private User user;


	
	private Double remainingAmount;
	
	private double amount;
	
	private boolean approved;
	private String status;
	private boolean processed;
	private long Usermobile;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date requestDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date WitdhrawalDate;
	
	private String role,Username,email;
	 public WithdrawalRequest(Long withdrawalId,Long Usermobile, Date requestDate,Date WitdhrawalDate, String username, String role,String email, String status, double amount) {
	        this.withdrawalId = withdrawalId;
	        this.WitdhrawalDate = WitdhrawalDate;
	        this.requestDate = requestDate;
	        this.Username = username;
	        this.Usermobile= Usermobile;
	        this.role = role;
	        this.status = status;
	        this.amount = amount;
	        this.email = email;
	    }

	    // Default constructor
	    public WithdrawalRequest() {}
	


}
