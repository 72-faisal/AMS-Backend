package com.gujjumarket.AgentManagmentSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.ProductType;

@Repository
public interface ProductTypeRepo extends JpaRepository<ProductType, Integer> {

//	Object findAllById(ProductType pt);

}
