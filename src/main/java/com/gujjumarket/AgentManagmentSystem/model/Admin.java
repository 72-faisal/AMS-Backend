package com.gujjumarket.AgentManagmentSystem.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "Adminid")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Admin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Adminid;
	private String name, password, photo,role;
	private String phoneNo;
	
//	@ToString.Exclude
//	@OneToMany
//	private List<ProductType> productType;
//	@ToString.Exclude
//	@OneToMany
//	private List<ProductCategory> productcategory;
//	@ToString.Exclude
//	@OneToMany
//	private List<Product> product;
//	@ToString.Exclude
//	@OneToMany
//	private List<User> user;
//
////	private boolean withdrawalApproved;
//	public Admin() {
//	}

}
