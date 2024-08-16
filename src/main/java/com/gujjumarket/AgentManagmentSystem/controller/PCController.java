package com.gujjumarket.AgentManagmentSystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.service.PTService;
import com.gujjumarket.AgentManagmentSystem.service.ProductcatService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("procate")
public class PCController {
	@Autowired
	ProductcatService pcService;
	@Autowired
	PTService ptService;

//	@GetMapping("getprocate")
//	public ResponseEntity<?> getprocate(@RequestBody Map<String, Object> pcid) {
//		Integer prc = (Integer) pcid.get("pcid");
//
//		if (prc != null) {
//			Optional<ProductCategory> pc = pcService.getpcbyid(prc);
//			return ResponseEntity.ok(pc);
//		} else {
//			List<ProductCategory> pc = pcService.allpt();
//			return ResponseEntity.ok(pc);
//		}
//	}
	@PostMapping("/getprocate")
	public ResponseEntity<?> getprocate(@RequestBody(required = false) Map<String, Integer> procateid) {
		// Check if request body is not null and contains the "pcid" key
		if (procateid != null && procateid.containsKey("pcid")) {
			Integer pcid = procateid.get("pcid");
			if (pcid != null) {
				// If pcid is provided, retrieve the specific product category
				Optional<ProductCategory> pc = pcService.getpcbyid(pcid);
				if (pc.isPresent()) {
					return ResponseEntity.ok(pc.get());
				} else {
					// Return 404 Not Found if the product category with the provided ID does not
					// exist
					return ResponseEntity.notFound().build();
				}
			}
		}

		// If pcid is not provided or is null, return all product categories
		List<ProductCategory> allProductCategories = pcService.allpt();
		Collections.reverse(allProductCategories);
		return ResponseEntity.ok(allProductCategories);
	}

	@PostMapping("getprocatebyPT")
	public ResponseEntity<?> getprocatebyPT(@RequestBody Map<String, Integer> ptid) {
		Integer ptidpc = ptid.get("ptid");
		if (ptidpc != null) {
			ProductType PT = new ProductType();
			PT.setPtid(ptidpc);
			List<ProductCategory> pc = pcService.getpcbyptid(PT);
			if (pc != null && !pc.isEmpty()) {
				return ResponseEntity.ok(pc);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get Product type");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get Product type");
		}
	}

// i have to use this after testing the api.
	@PutMapping("updateprocate")
	public ResponseEntity<?> updateprocate(@RequestBody ProductCategory PC, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
//		System.out.println(adminId);
		if (adminId != null) {// before user todo adminId !=null
			ProductCategory pc = pcService.updatepcbyid(PC, adminId);
			return ResponseEntity.ok(pc);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch Product type");
	}
//	@PutMapping("updateprocate")
//	public ResponseEntity<?> updateProductCategory(@RequestBody ProductCategory PC, HttpSession session) {
//		try {
//			// Update the ProductCategory without requiring adminId
//			ProductCategory updatedPC = pcService.updateProductCategoryById(PC);
//			return ResponseEntity.ok(updatedPC);
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Product Category.");
//		}
//	}

	@DeleteMapping("deleteprocate")
	public ResponseEntity<?> deleteprocate(@RequestBody Map<String, Integer> pcid) {
		Integer pccid = pcid.get("pcid");
		if (pccid != null) {
			pcService.deletepcbyid(pccid);
			return ResponseEntity.ok("Product catagory deleted successfully");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to DELETE Product type");
	}

}
