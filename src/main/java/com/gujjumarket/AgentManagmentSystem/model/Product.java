package com.gujjumarket.AgentManagmentSystem.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int pid;

	private String pname, pdesc, pphoto, pfile,pcode;
// by faisal.
//	private String brand;
//
	
	private Long  pprice;
	private double CHcomm, STcomm, DHcomm, Cityhcomm, Acomm, SAcomm;
	
	private boolean isdisable,isrenewal;
	

	@ManyToOne
	@JoinColumn(name = "createdby")
	@JsonIgnore
	private Admin createdby;

	@ManyToOne
	@JoinColumn(name = "updatedby")
	@JsonIgnore
	private Admin updatedby;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createddate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date updateddate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date subRenewalDate;
//	
	private String subRenewalStatus;

	@ManyToOne
	private ProductType producttype;

	@ManyToOne
//	@JoinColumn(name = "pcid") 
	private ProductCategory productcategory;

}
