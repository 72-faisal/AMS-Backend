package com.gujjumarket.AgentManagmentSystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gujjumarket.AgentManagmentSystem.Exception.ProductNotFoundException;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.service.PTService;
import com.gujjumarket.AgentManagmentSystem.service.ProductService;
import com.gujjumarket.AgentManagmentSystem.service.ProductcatService;
import com.gujjumarket.AgentManagmentSystem.service.SellService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("product")
public class ProductController {
	@Autowired
	ProductService pService;
	@Autowired
	ProductRepo pRepo;
	@Autowired
	SellService sellService;
	
	@Autowired
	PTService ptService;
	@Autowired
	ProductcatService pcService;

	
	
	
	
	
//	made some changes..
//	@CrossOrigin
//	@PostMapping("getproduct")
//	public ResponseEntity<?> getMethodName(@RequestBody(required = false) Map<String, Object> P,
//			HttpServletRequest request) {
//		Integer adminId = (Integer) request.getAttribute("userId");
//
////		--changes are here ...
//		if (P == null || P.isEmpty()) {
//			// Fetch all products
//			List<Product> products = pService.findallproduct();
//			if (!products.isEmpty()) {
//				return ResponseEntity.ok(products);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
//			}
//		}
//		Integer ptid = (Integer) P.get("ptid");
//		Integer pcid = (Integer) P.get("pcid");
//		Integer pid = (Integer) P.get("pid");
//		System.out.println(ptid);
//		System.out.println(pid);
//		System.out.println(pcid);
////		if (ptid == null && pcid == null && pid == null && adminId != null && P==null||P.isEmpty()) {
////			List<Product> product = pService.findallproduct();
////			if (product != null && !product.isEmpty()) {
////				return ResponseEntity.ok(product);
////			} else {
////				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
////			}
////		} 
//		if (pid != null && adminId != null) { // before user todo adminId !=null
//			Product product = pService.findbyid(pid);
//			System.out.println(pid);
//			if (product != null) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
//			}
//		} else if (ptid != null && adminId != null) {// before user todo adminId !=null
//			ProductType PT = new ProductType();
//			PT.setPtid(ptid);
//			List<Product> product = pService.getpbyptid(PT);
//			if (product != null && !product.isEmpty()) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND)
//						.body("No products found for the given product type.");
//			}
//		} else if (pcid != null && adminId != null) {// before user todo adminId !=null
//			ProductCategory pc = new ProductCategory();
//			pc.setPcid(pcid);
//			List<Product> product = pService.getpbypcid(pc);
//			if (product != null && !product.isEmpty()) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND)
//						.body("No products found for the given product category.");
//			}
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters.");
//	}

	@PostMapping("/check-renewal")
    public ResponseEntity<?> checkProductRenewal(@RequestBody Map<String, Integer> request) {
        Integer productId = request.get("productid");
        if (productId == null) {
            return ResponseEntity.badRequest().body("Product ID is required");
        }

        try {
            boolean isRenewal = pService.checkProductRenewal(productId);
            return ResponseEntity.ok(isRenewal);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
	
	@CrossOrigin
	@PostMapping("getproduct")
	public ResponseEntity<?> getMethodName(@RequestBody(required = false) Map<String, Object> P,
	        HttpServletRequest request) {
	    Integer adminId = (Integer) request.getAttribute("userId");

	    if (P == null || P.isEmpty()) {
	        // Fetch all products and exclude disabled ones
	        List<Product> products = pService.findallproduct().stream()
	                .filter(product -> !product.isIsdisable())
	                .collect(Collectors.toList());
	        if (!products.isEmpty()) {
	            Collections.reverse(products);
	        	return ResponseEntity.ok(products);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
	        }
	    }

	    Integer ptid = (Integer) P.get("ptid");
	    Integer pcid = (Integer) P.get("pcid");
	    Integer pid = (Integer) P.get("pid");
	    System.out.println(ptid);
	    System.out.println(pid);
	    System.out.println(pcid);

	    if (pid != null && adminId != null) { // before user todo adminId !=null
	        Product product = pService.findbyid(pid);
	        if (product != null && !product.isIsdisable()) {
	            return ResponseEntity.ok(product);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found or is disabled.");
	        }
	    } else if (ptid != null && adminId != null) {// before user todo adminId !=null
	        ProductType PT = new ProductType();
	        PT.setPtid(ptid);
	        List<Product> products = pService.getpbyptid(PT).stream()
	                .filter(product -> !product.isIsdisable())
	                .collect(Collectors.toList());
	        if (!products.isEmpty()) {
	            return ResponseEntity.ok(products);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No products found for the given product type.");
	        }
	    } else if (pcid != null && adminId != null) {// before user todo adminId !=null
	        ProductCategory pc = new ProductCategory();
	        pc.setPcid(pcid);
	        List<Product> products = pService.getpbypcid(pc).stream()
	                .filter(product -> !product.isIsdisable())
	                .collect(Collectors.toList());
	        if (!products.isEmpty()) {
	            return ResponseEntity.ok(products);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No products found for the given product category.");
	        }
	    }
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters.");
	}

	
	
	//product hierarchy in register sell
	@PostMapping("/fetch")
    public ResponseEntity<?> fetchProductData(@RequestBody(required = false) Map<String, Integer> request) {
        if (request == null || request.isEmpty()) {
            // Fetch all product types if the request body is empty
            List<ProductType> productTypes = ptService.allpt();
            return ResponseEntity.ok(productTypes);
        }

        Integer ptid = request.get("ptid");
        Integer pcid = request.get("pcid");
        Integer pid = request.get("pid");

        // Fetch product categories by product type ID
        if (ptid != null) {
            List<ProductCategory> categories = pcService.getCategoriesByProductType(ptid);
            return ResponseEntity.ok(categories);
        }

        // Fetch products by product category ID
        if (pcid != null) {
            List<Product> products = pService.getProductsByCategory(pcid);
            return ResponseEntity.ok(products);
        }

        // If pid is provided, it can be used to fetch a specific product, but this is not handled in your current logic
        // If you need to handle fetching by pid, add that logic here

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters.");
    }

	
	
	@PutMapping("/disable")
    public ResponseEntity<Product> disableProduct(@RequestBody Product request) {
        int productId = request.getPid();
        boolean disable = request.isIsdisable();
        Product updatedProduct = pService.disableProduct(productId, disable);
        return ResponseEntity.ok(updatedProduct);
    }

//	@PutMapping("updateproduct")
//	public ResponseEntity<?> createproduct(HttpSession session, @RequestParam Integer pid,
//			@RequestParam(required = false) Integer pcid, @RequestParam(required = false) Integer ptid,
//			@RequestPart(value = "pphoto", required = false) MultipartFile pphoto,
//			@RequestPart(value = "pfile", required = false) MultipartFile pfile,
//			@RequestParam(required = false) String pname, @RequestParam(required = false) String pdesc,
//			@RequestParam(required = false) Long pcode, @RequestParam(required = false) Long pprice,
//			@RequestParam(required = false) Double CHcomm, @RequestParam(required = false) Double STcomm,
//			@RequestParam(required = false) Double DHcomm, @RequestParam(required = false) Double Acomm,
//			@RequestParam(required = false) Double SAcomm) {
//		Integer adminId = (Integer) session.getAttribute("userID");
//		Product product = pService.findbyid(pid);
//		if (adminId != null) {
//			pService.updateproduct(product, pid, pcid, ptid, pphoto, pfile, pname, pdesc, pcode, pprice, CHcomm, STcomm,
//					DHcomm, Acomm, SAcomm, adminId);
//			return ResponseEntity.status(HttpStatus.OK).body("Product Update Succesfully");
//		}
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Update Product");
//	}

//	i have to use this controller method after testing.
//	@CrossOrigin
//	@PutMapping("/updateproduct")
//	public ResponseEntity<?> updateProduct(HttpServletRequest request, // Session to get adminId
//			@RequestParam Integer pid, // Product ID to update
//			@RequestParam(required = false) Integer pcid, // Optional Product Category ID
//			@RequestParam(required = false) Integer ptid, // Optional Product Type ID
//			@RequestParam(required = false) String pname, // Optional Product Name
//			@RequestParam(required = false) String pdesc, // Optional Product Description
//			@RequestParam(required = false) Long pcode, // Optional Product Code
//			@RequestParam(required = false) Long pprice, // Optional Product Price
//			@RequestParam(required = false) Double CHcomm, // Optional CHcomm
//			@RequestParam(required = false) Double STcomm, // Optional STcomm
//			@RequestParam(required = false) Double DHcomm, // Optional DHcomm
//			@RequestParam(required = false) Double Cityhcomm, // Optional Cityhcomm
//			@RequestParam(required = false) Double Acomm, // Optional Acomm
//			@RequestParam(required = false) Double SAcomm, // Optional SAcomm
//			@RequestPart(value = "pphoto", required = false) MultipartFile pphoto, // Optional Product Photo
//			@RequestPart(value = "pfile", required = false) MultipartFile pfile // Optional Product File
//	) {
//		// Retrieve admin ID from the session
//		Integer adminId = (Integer) request.getAttribute("userId");
//		System.out.println("pname: " + pname + ",CHcomm: " + CHcomm + ", STcomm: " + STcomm + ", DHcomm: " + DHcomm + "Cityhcomm: "+Cityhcomm
//				+ ", Acomm: " + Acomm + ", SAcomm: " + SAcomm);
//	        if (adminId == null) {  // Check if admin is authenticated
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not authenticated.");
//	        }
//
//		// Find product by ID
//		Product product = pService.findbyid(pid);
//
//		if (product == null) { // Check if the product exists
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
//		}
//		try {
//			// Update the product
//			pService.updateproduct(product, pid, pcid, ptid, pphoto, pfile, pname, pdesc, pcode, pprice, CHcomm, STcomm,
//					DHcomm, Cityhcomm, Acomm, SAcomm, adminId);
//			return ResponseEntity.ok("Product updated successfully.");
////			System.out.println("Saving product with CHcomm: " + product.getCHcomm() + ", STcomm: " + product.getSTcomm()
////			+ ", etc.");
//		} catch (IllegalArgumentException e) { // Handle update errors
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		}
//	}
	
	@CrossOrigin
	 @PutMapping("/updateproduct")
	 public ResponseEntity<?> updateProduct(HttpServletRequest request, // Session to get adminId
	       @RequestParam Integer pid, // Product ID to update
	       @RequestParam(required = false) Integer pcid, // Optional Product Category ID
	       @RequestParam(required = false) Integer ptid, // Optional Product Type ID
	       @RequestParam(required = false) String pname, // Optional Product Name
	       @RequestParam(required = false) String pdesc, // Optional Product Description
	       @RequestParam(required = false) String pcode, // Optional Product Code
	       @RequestParam(required = false) Long pprice, // Optional Product Price
	       @RequestPart(required = false) MultipartFile pphoto, // Optional Product Photo
	       @RequestPart(required = false) MultipartFile pfile, // Optional Product File
	       @RequestParam(required = false) Double CHcomm, // Optional CHcomm
	       @RequestParam(required = false) Double STcomm, // Optional STcomm
	       @RequestParam(required = false) Double DHcomm, // Optional DHcomm
	       @RequestParam(required = false) Double Cityhcomm, // Optional Cityhcomm
	       @RequestParam(required = false) Double Acomm, // Optional Acomm
	       @RequestParam(required = false) Double SAcomm // Optional SAcomm
	 ) {
	    // Retrieve admin ID from the session
	    Integer adminId = (Integer) request.getAttribute("userId");
	    if (adminId == null) { // Check if admin is authenticated
	       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not authenticated.");
	    }

	    // Find product by ID
	    Product product = pService.findbyid(pid);

	    if (product == null) { // Check if the product exists
	       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
	    }

	    try {
	       // Update the product
	       pService.updateProduct(product, pid, pcid, ptid, pname, pdesc, pcode, pprice, pphoto, pfile, CHcomm, STcomm, DHcomm, Cityhcomm, Acomm, SAcomm, adminId);
	       return ResponseEntity.ok("Product updated successfully.");
	    } catch (IllegalArgumentException e) { // Handle update errors
	       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	 }


//	@PutMapping("/updateproduct")
//	public ResponseEntity<?> updateProduct(HttpSession session, @RequestParam Integer pid,
//			@RequestParam(required = false) Integer pcid, @RequestParam(required = false) Integer ptid,
//			@RequestParam(required = false) String pname, @RequestParam(required = false) String pdesc,
//			@RequestParam(required = false) Long pcode, @RequestParam(required = false) Long pprice,
//			@RequestParam(required = false) Double CHcomm, @RequestParam(required = false) Double STcomm,
//			@RequestParam(required = false) Double DHcomm, @RequestParam(required = false) Double Acomm,
//			@RequestParam(required = false) Double SAcomm,
//			@RequestPart(value = "pphoto", required = false) MultipartFile pphoto,
//			@RequestPart(value = "pfile", required = false) MultipartFile pfile) {
//		System.out.println("start");
//		try {
//			// Retrieve the product by ID
//			System.out.println("in try");
//			Product product = pService.findbyid(pid);
////			System.out.println(product.getPid());
//			System.out.println(pid);
//			if (product == null) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
//			}
//
//			// Update the product with the provided data
//			pService.updateproduct(product, pid, pcid, ptid, pphoto, pfile, pname, pdesc, pcode, pprice, CHcomm, STcomm,
//					DHcomm, Acomm, SAcomm, null // Pass null for adminId
//			);
//
//			return ResponseEntity.ok("Product updated successfully.");
//
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An error occurred while updating the product.");
//		}
//	}

//	--faisal

	 @GetMapping("/best-selling-products")
	    public ResponseEntity<List<Map<String, Object>>> getBestSellingProducts() {
	        List<Map<String, Object>> bestSellingProducts = sellService.getBestSellingProducts();
	        if (!bestSellingProducts.isEmpty()) {
	            return ResponseEntity.ok(bestSellingProducts);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
//	--faisal
//	@PostMapping("/{userid}/logout")
//	public ResponseEntity<String> logout(@PathVariable int userid, HttpSession session) {
//		session.invalidate();
//		return ResponseEntity.ok("Logout Successful");
//	}
}
