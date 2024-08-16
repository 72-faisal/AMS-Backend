package com.gujjumarket.AgentManagmentSystem.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Payment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String transactionId;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.sql.Date transactionDate;
    private int amount;
    private String transactionStatus;

    @ManyToOne
    @JoinColumn(name = "cid", nullable = false)
    @JsonBackReference
    private Customer customer;

	@ManyToOne
  @JoinColumn(name = "sellid") // Establishing the relationship with `Sell`
  private Sell sell;
	
	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;
	
	 private String paymentLink;
	 
    public Payment() {
         // Default status
    }

    @JsonProperty("cid")
    public Integer getCustomerId() {
        return customer != null ? customer.getCid() : null;
    }


	

    // Additional constructors, getters, and setters can be added here
}
