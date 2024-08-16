package com.gujjumarket.AgentManagmentSystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.Admin;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductCatRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductTypeRepo;

@Service
public class ProductcatService {
	@Autowired
	ProductCatRepo pcRepo;
	@Autowired
	ProductTypeRepo ptRepo;
	@Autowired
	AdminRepo adRepo;

	public Optional<ProductCategory> getpcbyid(Integer pcid) {

		return pcRepo.findById(pcid);
	}
	
	
	

	public List<ProductCategory> allpt() {
		return pcRepo.findAll();
	}

	public List<ProductCategory> getpcbyptid(ProductType productType) {
		return pcRepo.findbyproducttype(productType);
	}

//	i have to run this after the teting.
	public ProductCategory updatepcbyid(ProductCategory pC, Integer adminId) {
		Admin admin = adRepo.getReferenceById(adminId);
		System.out.println(admin);
		Integer pc = pC.getPcid();
		System.out.println(pc);
		String pcatagory = pC.getPcatagory();
		System.out.println(pcatagory);
		String pcdescription = pC.getPcdescription();
		System.out.println(pcdescription);
		ProductCategory pcate = pcRepo.getReferenceById(pc);
		pcate.setPcatagory(pcatagory.isEmpty() ? pcate.getPcatagory() : pcatagory);
		pcate.setPcdescription(pcdescription.isEmpty() ? pcate.getPcdescription() : pcdescription);
		pcate.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
		pcate.setUpdatedby(admin);
		pcate.setProducttype(Objects.equals(pC.getProducttype(), null) ? pcate.getProducttype() : pC.getProducttype());
		return pcRepo.save(pcate);
	}

//	public ProductCategory updateProductCategoryById(ProductCategory pC) {
//		Integer pcid = pC.getPcid();
//		ProductCategory pcate = pcRepo.getReferenceById(pcid);
//
//		if (pcate == null) {
//			throw new IllegalArgumentException("Product Category with ID " + pcid + " not found.");
//		}
//
//		// Update ProductCategory fields based on the incoming request
//		pcate.setPcatagory(
//				pC.getPcatagory() != null && !pC.getPcatagory().isEmpty() ? pC.getPcatagory() : pcate.getPcatagory());
//		pcate.setPcdescription(pC.getPcdescription() != null && !pC.getPcdescription().isEmpty() ? pC.getPcdescription()
//				: pcate.getPcdescription());
//		pcate.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//		// If there's no admin, don't set updatedby, or set to a default admin/username
//		// if needed
//		if (pC.getUpdatedby() != null) {
//			pcate.setUpdatedby(pC.getUpdatedby()); // Set updatedby if provided
//		}
//
//		// Update ProductType if given
//		ProductType newProductType = pC.getProducttype();
//		if (newProductType != null) {
//			pcate.setProducttype(newProductType);
//		}
//
//		return pcRepo.save(pcate);
//	}

	public void deletepcbyid(Integer pcid) {
		pcRepo.deleteById(pcid);
	}




	  public List<ProductCategory> getCategoriesByProductType(int ptid) {
	        return pcRepo.findByProducttypePtid(ptid);
	    }

}
