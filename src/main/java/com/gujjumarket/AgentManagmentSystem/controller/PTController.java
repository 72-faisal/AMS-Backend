package com.gujjumarket.AgentManagmentSystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.service.PTService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("producttype")
public class PTController {

	@Autowired
	PTService ptService;

//	@GetMapping("/products")
//	public ResponseEntity<?> getProducts(@RequestBody(required = false) Map<String, Object> ptid) {
//		Integer pt=(Integer) ptid.get("ptid");
//
//		if (pt != null) {
//			Optional<ProductType> pt1 = ptService.getptbyid(pt);
//			return ResponseEntity.ok(pt1);
//		} else {
//			List<ProductType> protype = ptService.allpt();
//			return ResponseEntity.ok(protype);
//		}
//	}
//	@GetMapping("/products")
//	public ResponseEntity<?> getProducts(@RequestBody(required = false) Map<String, Object> ptid) {
//		if (ptid != null) {
//			Integer pt = (Integer) ptid.get("ptid");
//			if (pt != null) {
//				Optional<ProductType> pt1 = ptService.getptbyid(pt);
//				return ResponseEntity.ok(pt1);
//			}
//		}
//
//		// If ptid is null or pt is null, return all product types
//		List<ProductType> protype = ptService.allpt();
//		return ResponseEntity.ok(protype);
//	}
//	--changes here
	@CrossOrigin
	@PostMapping("/products")
	public ResponseEntity<?> getProducts(@RequestBody(required = false) Map<String, Object> ptid, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
//		System.out.println(adminId);
		if (ptid != null) {
			Integer pt = (Integer) ptid.get("ptid");
			if (pt != null) {
				Optional<ProductType> pt1 = ptService.getptbyid(pt);
				if (pt1.isPresent()) {
					return ResponseEntity.ok(pt1.get());
				} else {
					return ResponseEntity.notFound().build();
				}
			}
		}

		// If ptid is null or pt is null, return all product types
		List<ProductType> productTypes = ptService.allpt();
		Collections.reverse(productTypes);
		return ResponseEntity.ok(productTypes);
	}

	
	
	
	 @DeleteMapping("deleteproduct")
	  public ResponseEntity<?> deleteproduct(HttpServletRequest request, @RequestBody Map<String, Integer> ptid) 
	  {
  		 Integer pt = ptid.get("ptid"); 
  		Integer adminId = (Integer) request.getAttribute("userId");
	   	
	   	 if (pt != null && adminId != null) { 
	   	// Check if the product exists in the database 
	   	if (ptService.productExists(pt)) {
	  				
	  				ptService.deleteproduct(pt); 
	  				return ResponseEntity.ok("ProductType deleted"); 
	  }
	   else {
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ProductType not found");
	     }
	    }
	    
	  		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete productType");
	   }
	 
	
	

// i have to use this api after testing.
	@PutMapping("/updateptype")
	public ResponseEntity<?> updateProductType(@RequestBody Map<String, Object> PT, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		System.out.println(PT);
		if (adminId != null) {// before this todo adminId != null
			ptService.updatePT(PT, adminId);
			return ResponseEntity.ok("ProductType updated successfully");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Product");
	}

//	 @PutMapping("/updateptype")
//	    public ResponseEntity<?> updateProductType(@RequestBody Map<String, Object> PT) {
//	        try {
//	            ProductType updatedPT = ptService.updateProductType(PT);
//	            return ResponseEntity.ok("Product Type updated successfully.");
//	        } catch (NoSuchElementException e) {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Type not found.");
//	        } catch (Exception e) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Product Type.");
//	        }
//	    }

}
