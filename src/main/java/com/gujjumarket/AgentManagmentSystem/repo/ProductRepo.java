package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.producttype = :productType")
	List<Product> findbyproducttype(ProductType productType);

	@Query("SELECT p FROM Product p WHERE p.productcategory = :productCategory")
	List<Product> findbypc(ProductCategory productCategory);

	
	Object findByPname(String pname);

	Product findProductByPname(String pname);

	double getProductAmountByPid(int pid);
	
	@Query("SELECT p FROM Product p WHERE p.pname IN :productName")
	List<Product> findByPname(@Param("productName") List<String> productName);

	@Query("SELECT p FROM Product p WHERE p.productcategory.pcid = :pcid AND p.isdisable = false")
    List<Product> findByProductcategoryPcid(@Param("pcid") int pcid);

	@Query("SELECT p.id FROM Product p WHERE p.pname = :productName")
    int findPidByPname(@Param("productName") String productName);


	
//	Product getProductByName(String productname);

//	List<Product> findByPname(List<String> productName);

}
