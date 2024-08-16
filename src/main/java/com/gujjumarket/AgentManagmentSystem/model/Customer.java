package com.gujjumarket.AgentManagmentSystem.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cid;
	private String customername, customermobile, customeraddress, customercityorvillage, customerstate, customeremail,customerdistrict;
	private long totalspend;

	@OneToMany
	private List<Sell> sell;

}
