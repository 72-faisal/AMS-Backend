package com.gujjumarket.AgentManagmentSystem.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

//import jakarta.persistence.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class ProductCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int pcid;
	private String pcatagory,pcdescription;
	private Date createddate , updateddate;
	
	private int unit;
	@ManyToOne
	private ProductType producttype;
	
	@OneToMany
	private List<Product> product;
	
	@ManyToOne
    @JoinColumn(name = "createdby")
	@JsonIgnore
    private Admin createdby;

    @ManyToOne
    @JoinColumn(name = "updatedby")
    @JsonIgnore
    private Admin updatedby;
}
