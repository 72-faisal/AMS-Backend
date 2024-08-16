package com.gujjumarket.AgentManagmentSystem.model;

import lombok.Data;

//@Entity
@Data
public class PromotionCriteriaRequest {
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private int id;
	
	    private String userRole;
	    private String duration;
	    private double saleAmount;
	    private int unit;
//	    private List<ProductCategory> categories;
	    private int pcid;

	    // Constructors, getters, setters
	

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private int id;
//	private String userRole;
//    private String duration;
//    private List<ProductCategory> pcategory;
////    private String productCategory;
//    private double saleAmount;
////    private int units;
	
}