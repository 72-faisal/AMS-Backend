package com.gujjumarket.AgentManagmentSystem.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gujjumarket.AgentManagmentSystem.Exception.ProductNotFoundException;
import com.gujjumarket.AgentManagmentSystem.model.Admin;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductCatRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductTypeRepo;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.utils.PhotoUpload;

import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	ProductRepo pRepo;
	@Autowired
	AdminRepo adRepo;
	@Autowired
	ProductTypeRepo ptREpo;
	@Autowired
	ProductCatRepo pcRepo;
	@Value("${upload-dir}")
	private String upload_dir;
	
	@Autowired
	SellRepo sellRepo;

	public List<Product> findallproduct() {
		List<Product> p = pRepo.findAll();
		return p;
	}

	public Product findbyid(Integer pid) {
		Product p = pRepo.getReferenceById(pid);
		return p;
	}

	public List<Product> getpbyptid(ProductType productType) {
		try {
			return pRepo.findbyproducttype(productType);
		} catch (Exception e) {
			// Log the exception message, you can use any logging framework
			System.err.println("An error occurred while fetching products: " + e.getMessage());
			// Return an empty list upon encountering an exception
			return Collections.emptyList();
		}
	}

	public List<Product> getpbypcid(ProductCategory productCategory) {

		try {
			return pRepo.findbypc(productCategory);
		} catch (Exception e) {
			// Log the exception message, you can use any logging framework
			System.err.println("An error occurred while fetching products: " + e.getMessage());
			// Return an empty list upon encountering an exception
			return Collections.emptyList();
		}
	}

	public void deleteById(Integer pid) {
		pRepo.deleteById(pid);
		// TODO Auto-generated method stub

	}
	
	
//	public void disableProduct(int productId) throws ProductAlreadyDisabledException, Exception {
//        Optional<Product> optionalProduct = pRepo.findById(productId);
//        if (optionalProduct.isPresent()) {
//            Product product = optionalProduct.get();
//            if (!product.isIsdisable()) {
//                product.setIsdisable(true);  // Assuming the field is named 'isDisable'
//                pRepo.save(product);
//            } else {
//                throw new ProductAlreadyDisabledException("Product is already disabled");
//            }
//        } else {
//            throw new Exception("Product not found with id: " + productId);
//        }
//    }
	
	public Product disableProduct(int productId, boolean disable) {
        Optional<Product> optionalProduct = pRepo.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setIsdisable(disable);
            
            return pRepo.save(product);
        }else {
			// Handle the case where the user does not exist
			throw new RuntimeException("User not found with ID: " + productId);
		}
    }
//	i have to use this after testing .
//	public Product updateproduct(Product product, Integer pid, Integer pcid, Integer ptid, MultipartFile pphoto,
//			MultipartFile pfile, String pname, String pdesc, Long pcode, Long pprice, Double cHcomm, Double sTcomm,
//			Double dHcomm, Double acomm, Double sAcomm, Integer adminId) {
//		
//		try {
//			System.out.println(pphoto.getName());
//			Admin a = adRepo.getReferenceById(adminId);
//			ProductType pt = ptREpo
//					.getReferenceById(Objects.equals(ptid, null) ? product.getProducttype().getPtid() : ptid);
//			ProductCategory pc = pcRepo
//					.getReferenceById(Objects.equals(pcid, null) ? product.getProductcategory().getPcid() : pcid);
//
//			product.setPname(Objects.equals(pname, null) ? product.getPname() : pname); 
//			product.setPdesc(Objects.equals(pdesc, null) ? product.getPdesc() : pdesc);
//			product.setPcode(Objects.equals(pcode, null) ? product.getPcode() : pcode);
//			product.setPprice(Objects.equals(pprice, null) ? product.getPprice() : pprice);
////			product.setPcode(Objects.equals(pcode, null) ? product.getPcode() : pcode);
////			product.setPprice(Objects.equals(pprice, null) ? product.getPprice() : pprice);
//			product.setCHcomm(Objects.equals(cHcomm, null) ? product.getCHcomm() : cHcomm);
//			product.setSTcomm(Objects.equals(sTcomm, null) ? product.getSTcomm() : sTcomm);
//			product.setDHcomm(Objects.equals(dHcomm, null) ? product.getDHcomm() : dHcomm);
//			product.setAcomm(Objects.equals(acomm, null) ? product.getAcomm() : acomm);
//			product.setSAcomm(Objects.equals(sAcomm, null) ? product.getSAcomm() : sAcomm);
//			product.setCreatedby(a);
//			product.setProducttype(pt);
//			product.setProductcategory(pc);
//			product.setUpdatedby(a);
//			product.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//			if (pphoto != null && !pphoto.isEmpty()) {
//				String pphotoname = pphoto.getOriginalFilename();
//				System.out.println(pphotoname);
//				String storepphoto = PhotoUpload.saveFile(upload_dir, pphotoname, pphoto);
//				product.setPphoto(storepphoto);
//			}
//
//			if (pfile != null && !pfile.isEmpty()) {
//
//				String pfilename = pfile.getOriginalFilename();
//				System.out.println(pfilename);
//				String storepfile = PhotoUpload.saveFile(upload_dir, pfilename, pfile);
//				product.setPfile(storepfile);
//			}
//
//			return pRepo.save(product);
//		}  catch (Exception e) {
//	        System.err.println("An error occurred while updating product: " + e.getMessage());
//	        throw new IllegalArgumentException("Product update failed: " + e.getMessage(), e);
//	    }
//
//	}
	@Transactional
	public Product updateProduct(Product product, Integer pid, Integer pcid, Integer ptid, String pname, String pdesc, String pcode, Long pprice, MultipartFile pphoto, MultipartFile pfile, Double CHcomm, Double STcomm, Double DHcomm, Double Cityhcomm, Double Acomm, Double SAcomm, Integer adminId) {
		Admin a = adRepo.getReferenceById(adminId);
	   try {
	      // Update product fields
	      if (pname != null) {
	         product.setPname(pname);
	      }
	      if (pdesc != null) {
	         product.setPdesc(pdesc);
	      }
	      if (pcode != null) {
	         product.setPcode(pcode);
	      }
	      if (pprice != null) {
	         product.setPprice(pprice);
	      }

	      // Set commission fields
	      product.setCHcomm(CHcomm != null ? CHcomm : product.getCHcomm());
	      product.setSTcomm(STcomm != null ? STcomm : product.getSTcomm());
	      product.setDHcomm(DHcomm != null ? DHcomm : product.getDHcomm());
	      product.setCityhcomm(Cityhcomm != null ? Cityhcomm : product.getCityhcomm());
	      product.setAcomm(Acomm != null ? Acomm : product.getAcomm());
	      product.setSAcomm(SAcomm != null ? SAcomm : product.getSAcomm());

	      // Update other fields
	      // Ensure correct admin and type/category references
//	      Admin a = adRepo.getOne(adminId);
//	      product.setAdmin(a);

	      if (pphoto != null && !pphoto.isEmpty()) {
	         String pphotoname = pphoto.getOriginalFilename();
	         String storepphoto = PhotoUpload.saveFile(upload_dir, pphotoname, pphoto);
	         product.setPphoto(storepphoto);
	      }

	      if (pfile != null && !pfile.isEmpty()) {
	         String pfilename = pfile.getOriginalFilename();
	         String storepfile = PhotoUpload.saveFile(upload_dir, pfilename, pfile);
	         product.setPfile(storepfile);
	      }

	      product.setUpdatedby(a);
	      product.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));

	      return pRepo.save(product); // Save the updated product
	   } catch (Exception e) {
	      throw new IllegalArgumentException("Product update failed: " + e.getMessage(), e);
	   }
	}

	public List<Product> getProductsByCategory(int pcid) {
	    return pRepo.findByProductcategoryPcid(pcid).stream()
	            .filter(product -> !product.isIsdisable())
	            .collect(Collectors.toList());
	}

	public Product getProductByPid(int pid) {
        Optional<Product> product = pRepo.findById(pid);
        return product.orElse(null);
    }

	public boolean checkProductRenewal(Integer productid) throws ProductNotFoundException {
        Optional<Product> productOptional = pRepo.findById(productid);
        if (productOptional.isPresent()) {
            return productOptional.get().isIsrenewal();
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productid);
        }
    }

	public Map<String, Integer> getProductMonthlySells() {
	    // Get the start and end dates of the current month
	    Date now = new Date(System.currentTimeMillis());
	    Date startOfMonth = getStartOfMonth(now);
	    Date endOfMonth = getEndOfMonth(now);

	    // Fetch all product names from the product table
	    List<String> productNames = getAllProductNames();
	    
	    // Initialize a map to store product names and their respective counts
	    Map<String, Integer> productMonthlySells = new HashMap<>();

	    // For each product name, query the sell table to count the occurrences within the given month
	    for (String productName : productNames) {
	    	
	        int sellCount = sellRepo.countByProductNameAndApprovedDateBetween(productName, startOfMonth, endOfMonth);
	       
	        productMonthlySells.put(productName, sellCount);
	    }

	    return productMonthlySells;
	}

	public List<String> getAllProductNames() {
    // Assuming you have a product repository injected or accessible
    List<Product> products = pRepo.findAll();
    
    // Extracting product names from the list of products
    return products.stream()
                   .map(Product::getPname)
                   .collect(Collectors.toList());
}

	// Method to get the start of the month for a given date
	private Date getStartOfMonth(Date date) {
	    LocalDate localDate = date.toLocalDate().withMonth(5).withDayOfMonth(1);
	    System.out.println(localDate);
	    return Date.valueOf(localDate);
	}

	private Date getEndOfMonth(Date date) {
	    LocalDate localDate = date.toLocalDate().withMonth(5).withDayOfMonth(date.toLocalDate().lengthOfMonth());
	    System.out.println(localDate);
	    return Date.valueOf(localDate);
	}


	

}
