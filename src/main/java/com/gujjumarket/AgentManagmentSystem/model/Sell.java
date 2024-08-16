	package com.gujjumarket.AgentManagmentSystem.model;
	
	import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
	
	@Data
	@Entity
	public class Sell {
	
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		private int sellid;
		private Date registerDate,approvedDate;
		private String Username, Userrole, Productname, salestatus;
		private double TotalCommissionAmount, CHcomm, STcomm, DHcomm,Cityhcomm, Acomm, SAcomm;
		private long  Saleamount;
		
		
	//	by Faisal
	//	private Double monthlyTarget;
	//    private Double quarterlyTarget;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	//    private Double yearlyTarget;
	//    private String timePeriod,teamLevel;
//		private double TotalSales;
		private String promotionDuration; // e.g., 'month', 'quarter', 'year'
	    private String promotionTarget;   // e.g., 'Statehead', 'districthead', 'cityhead', 'agent', 'subagent'
	    private int promotionUnits;       // Number of units for the promotion
	    private long promotionSaleAmount;
	//   ----- 
		private int pid;
		@ManyToOne
		private ProductCategory productcategory;
		@ManyToOne
		@JoinColumn(name = "soldby")
		@JsonIgnore
		private User soldby;
		
		@ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "customer")
		private Customer customer;
	
		@ManyToOne
	    @JoinColumn(name = "product_id")
	    private Product product;
		
		@OneToMany(mappedBy = "sell")
	    private List<Payment> payments; // This establishes a relationship with `Payment`
		
		@ManyToOne
	    @JoinColumn(name = "id")
	    private ComissionDetails comissionDetails;
		
		private Date renewaldate;
		
		private String renewalStatus;

		@Override
		public String toString() {
			return "Sell [sellid=" + sellid + ", registerDate=" + registerDate + ", approvedDate=" + approvedDate
					+ ", Username=" + Username + ", Userrole=" + Userrole + ", Productname=" + Productname
					+ ", salestatus=" + salestatus + ", TotalCommissionAmount=" + TotalCommissionAmount + ", CHcomm="
					+ CHcomm + ", STcomm=" + STcomm + ", DHcomm=" + DHcomm + ", Cityhcomm=" + Cityhcomm + ", Acomm="
					+ Acomm + ", SAcomm=" + SAcomm + ", Saleamount=" + Saleamount + ", promotionDuration="
					+ promotionDuration + ", promotionTarget=" + promotionTarget + ", promotionUnits=" + promotionUnits
					+ ", promotionSaleAmount=" + promotionSaleAmount + ", pid=" + pid + ", productcategory="
					+ productcategory + ", soldby=" + soldby + ", customer=" + customer + ", product=" + product
					+ ", payments=" + payments + ", comissionDetails=" + comissionDetails + ", renewaldate="
					+ renewaldate + ", renewalStatus=" + renewalStatus + "]";
		}
		
		
	
	}
