package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;

public interface ProductCatRepo extends JpaRepository<ProductCategory, Integer> {

//	@Query("SELECT p FROM ProductCategory p WHERE p.producttype = :ptid")
//	List<ProductCategory> getbyptid(@Param("ptid") Integer ptid);

	@Query("SELECT p FROM ProductCategory p WHERE p.producttype = :productType")
	List<ProductCategory> findbyproducttype(@Param("productType") ProductType productType);

	ProductCategory findByPcid(int pcid);

//	Optional<ProductCategory> findByCategoryName(String productCategory);

//	@Query("SELECT p FROM ProductCategory p	WHERE p.pcatagory=:name")
//	ProductCategory findByPcatagory(String name);

//	@Query("SELECT p FROM ProductCategory p	WHERE p.pcatagory=:name")
//	ProductCategory findByPcatagory(String productCategory);
//
//	int calculateTotalUnits();

//	Optional<ProductCategory> findByCategoryName(String categoryName);

	@Query("SELECT p FROM ProductCategory p WHERE p.pcatagory = :categoryName")
	Optional<ProductCategory> findByPCatagory(String categoryName);

	@Query("SELECT pc FROM ProductCategory pc WHERE pc.producttype.ptid = :ptid")
	List<ProductCategory> findByProducttypePtid(@Param("ptid") int ptid);


	

}
