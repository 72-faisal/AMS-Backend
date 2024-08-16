package com.gujjumarket.AgentManagmentSystem.model;


import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ProductType {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int ptid;
	private String ptype,ptdescription;
	private Date createddate , updateddate;
	
	@OneToMany
	private List<ProductCategory> productcategory;
	
	@ManyToOne
    @JoinColumn(name = "createdby")
	@JsonIgnore
    private Admin createdby;

    @ManyToOne
    @JoinColumn(name = "updatedby")
    @JsonIgnore
    private Admin updatedby;
}
