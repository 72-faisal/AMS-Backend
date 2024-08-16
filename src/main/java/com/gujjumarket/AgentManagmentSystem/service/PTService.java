	package com.gujjumarket.AgentManagmentSystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.Admin;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductCatRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductTypeRepo;

@Service
public class PTService {

	@Autowired
	ProductTypeRepo ptRepo;
	@Autowired
	AdminRepo adRepo;

	public Optional<ProductType> getptbyid(Integer ptid) {
		return ptRepo.findById(ptid);
	}

	public List<ProductType> allpt() {
		return ptRepo.findAll();
	}

	public boolean deleteproduct(Integer ptid) {
		ptRepo.deleteById(ptid);
		return true;
	}
	
	
	  public boolean productExists(Integer ptid) { 
	  
	  return ptRepo.existsById(ptid);
	  }
	 
	
	
// i have to use this after testing.
	public ProductType updatePT(Map<String, Object> pT, Integer adminId) {
		Optional<Admin> adminById = adRepo.findById(adminId);

		String ptype = (String) pT.get("ptype");
		Integer ptid = (Integer) pT.get("ptid");
		String ptdescription = (String) pT.get("ptdescription");

		try {
			if (adminById != null) {
				Optional<ProductType> pt1 = ptRepo.findById(ptid);
				ProductType proT = pt1.get();
				proT.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
				proT.setUpdatedby(adminById.get());
				proT.setPtype(ptype.isEmpty() ? proT.getPtype() : ptype);
				proT.setPtdescription(ptdescription.isEmpty() ? proT.getPtdescription() : ptdescription);
				return ptRepo.save(proT);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return (ProductType) pT;

	}

//	public ProductType updateProductType(Map<String, Object> pT) {
//        Integer ptid = (Integer) pT.get("ptid");
//
//        // Ensure the product type exists
//        Optional<ProductType> ptOptional = ptRepo.findById(ptid);
//        if (!ptOptional.isPresent()) {
//            throw new NoSuchElementException("Product Type with ID " + ptid + " not found.");
//        }
//
//        ProductType proT = ptOptional.get();
//
//        // Update the product type's properties
//        String ptype = (String) pT.get("ptype");
//        String ptdescription = (String) pT.get("ptdescription");
//
//        proT.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//        if (ptype != null && !ptype.isEmpty()) {
//            proT.setPtype(ptype);
//        }
//
//        if (ptdescription != null && !ptdescription.isEmpty()) {
//            proT.setPtdescription(ptdescription);
//        }
//
//        return ptRepo.save(proT);
//    }
}
