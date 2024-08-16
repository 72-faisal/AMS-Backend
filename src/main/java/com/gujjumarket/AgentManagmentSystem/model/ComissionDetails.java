package com.gujjumarket.AgentManagmentSystem.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ComissionDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private Date date;
	private Integer transactionId;
	private String userName;
	private String userRole;
	private String productName;
	private Double payment_received;
	private Double commissionRate;
	private Double commissionEarned;
	private String saleStatus;
	// private String manageCommission;

	@OneToMany(mappedBy = "comissionDetails")
    private List<Sell> sell;
//	
	@OneToMany
	private List<User> user;
	
	@OneToMany
	private List<Product> products;

}
