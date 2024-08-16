package com.gujjumarket.AgentManagmentSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class PromotionCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    
    private String userRole;
    
    private String duration; // e.g., month, quarter, year
    
    @ManyToOne
    @JoinColumn(name = "pcid")
    private ProductCategory productCategory;
    
    private double saleAmount;
    
    private int unit; // Represented as an integer count
    
    // Constructors, getters, setters
}
