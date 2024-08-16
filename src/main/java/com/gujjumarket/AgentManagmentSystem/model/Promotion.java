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
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "sell_id")
    private Sell sell;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;
    
    private String duration; // e.g., 'month', 'quarter', 'year'
    private int units;       // Number of units for the promotion
    private long saleAmount;
    
    // Constructors, getters, and setters
}
