package com.gujjumarket.AgentManagmentSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.PromotionCriteria;
import com.gujjumarket.AgentManagmentSystem.model.PromotionCriteriaRequest;
import com.gujjumarket.AgentManagmentSystem.repo.ProductCatRepo;
import com.gujjumarket.AgentManagmentSystem.repo.PromotionCriteriaRepository;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;

@Service
public class PromotionService {
	@Autowired
	private PromotionCriteriaRepository criteriaRepo;
	@Autowired
	private Userrepo userRepo;
	@Autowired
	private ProductCatRepo pcRepo;
	
	public void setPromotionCriteria(PromotionCriteriaRequest request) {
	    PromotionCriteria promotionCriteria = new PromotionCriteria();
	    promotionCriteria.setUserRole(request.getUserRole());
	    promotionCriteria.setDuration(request.getDuration());
	    promotionCriteria.setSaleAmount(request.getSaleAmount());
	    promotionCriteria.setUnit(request.getUnit());
	    
	    // Assuming you have a method to find ProductCategory by id in the repository
	    ProductCategory productCategory = pcRepo.findByPcid(request.getPcid());
	    
	    if (productCategory == null) {
	        throw new RuntimeException("Product category not found");
	    }
	    
	    promotionCriteria.setProductCategory(productCategory);
	    
	    // Save promotion criteria
	    criteriaRepo.save(promotionCriteria);
	}
//	public void setPromotionCriteria(PromotionCriteriaRequest request) {
//	    String userRole = request.getUserRole();
//	    String duration = request.getDuration();
//	    double saleAmount = request.getSaleAmount();
//	    
//	    // Process each category in the list
//	    for (ProductCategory categoryInfo : request.getCategories()) {
//	        int pcid = categoryInfo.getPcid();
//	        int unit = categoryInfo.getUnit();
//	        
//	        // Create a PromotionCriteria object
//	        PromotionCriteria promotionCriteria = new PromotionCriteria();
//	        promotionCriteria.setUserRole(userRole);
//	        promotionCriteria.setDuration(duration);
//	        promotionCriteria.setSaleAmount(saleAmount);
//	        promotionCriteria.setUnit(unit);
//	        
//	        // Find ProductCategory by pcid
//	        ProductCategory productCategory = pcRepo.findByPcid(pcid);
//	        
//	        // Check if ProductCategory exists
//	        if (productCategory == null) {
//	            throw new RuntimeException("Product category not found for pcid: " + pcid);
//	        }
//	        
//	        // Set ProductCategory in PromotionCriteria
//	        promotionCriteria.setProductCategory(productCategory);
//	        
//	        // Save promotion criteria
//	        criteriaRepo.save(promotionCriteria);
//	    }
//	}


//	@Transactional
//	public void setPromotionCriteria(PromotionCriteriaRequest request) {
//        for (ProductCategoryRequest categoryRequest : request.getProductCategories()) {
//            ProductCategory productCategory = productCategoryRepo.findByCategoryName(categoryRequest.getName());
//            if (productCategory != null) {
//                PromotionCriteria criteria = new PromotionCriteria();
//                criteria.setUserRole(request.getUserRole());
//                criteria.setDuration(request.getDuration());
//                criteria.setProductCategory(productCategory);
//                criteria.setSaleAmount(request.getSaleAmount());
//                criteria.setUnits(request.getUnits());
//                criteriaRepo.save(criteria);
//            } else {
//                // Handle case where product category does not exist
//            }
//        }
//    }
//
//	@Transactional
//	public void applyPromotionCriteria() {
//		List<PromotionCriteria> allCriteria = criteriaRepo.findAll();
//		for (PromotionCriteria criteria : allCriteria) {
//			List<User> users = userRepo.findByRole(criteria.getUserRole());
//			for (User user : users) {
//				// Logic to filter sales data and check if user meets promotion criteria
//				// If user meets criteria, promote the user (update user role)
//				if (userMeetsPromotionCriteria(user, criteria)) {
//					promoteUser(user);
//				}
//			}
//		}
//	}
//
//	private boolean userMeetsPromotionCriteria(User user, PromotionCriteria criteria) {
//	    // Retrieve the sales data for the user
//	    List<Sell> userSales = user.getSell();
//
//	    // Filter sales data based on the duration criteria
//	    LocalDate startDate = LocalDate.now();
//	    switch (criteria.getDuration()) {
//	        case "MONTH":
//	            startDate = startDate.minusMonths(1);
//	            break;
//	        case "QUARTAR":
//	            startDate = startDate.minusMonths(3);
//	            break;
//	        case "YEAR":
//	            startDate = startDate.minusYears(1);
//	            break;
//	        default:
//	            // Handle unknown duration
//	            break;
//	    }
//
//	    // Initialize variables to track total sale amount and units sold
//	    double totalSaleAmount = 0;
//	    int totalUnitsSold = 0;
//
//	    // Iterate over user's sales data and calculate total sale amount and units sold
//	    for (Sell sale : userSales) {
//	        // Check if sale meets duration criteria
//	        if (sale.getRegisterDate().toLocalDate().isAfter(startDate)) {
//	            // Increment total sale amount
//	            totalSaleAmount += sale.getSaleamount();
//	            // Increment total units sold
//	            totalUnitsSold += sale.getUnits();
//	        }
//	    }
//
//	    // Check if user meets sale amount and units criteria
//	    return totalSaleAmount >= criteria.getSaleAmount() && totalUnitsSold >= criteria.getUnits();
//	}
//
//
//	private void promoteUser(User user) {
//	    // Get the current user role
//	    String currentRole = user.getRole();
//
//	    // Determine the new role based on the current role
//	    String newRole = null;
//	    switch (currentRole) {
//	        case "SUBAGENT":
//	            newRole = "AGENT";
//	            break;
//	        case "AGENT":
//	            newRole = "CITYHEAD";
//	            break;
//	        case "CITYHEAD":
//	            newRole = "DISTRICTHEAD";
//	            break;
//	        case "DISTRICTHEAD":
//	            newRole = "STATEHEAD";
//	            break;
//	        // Add cases for other roles as needed
//	        default:
//	            // Handle unknown or unsupported roles
//	            break;
//	    }
//
//	    // Update the user's role if a new role was determined
//	    if (newRole != null) {
//	        user.setRole(newRole);
//	        userRepo.save(user);
//	    } else {
//	        // Log a message or handle the case where no new role was determined
//	    }
//	}
//	public void setPromotionCriteria(PromotionCriteriaRequest request) {
//	    String userRole = request.getUserRole();
//	    String duration = request.getDuration();
//	    Double SaleAmount= request.getSaleAmount();
//
//	    List<ProductCategory> categoryList = request.getPcategory();
//	    for (ProductCategory category : categoryList) {
//	        PromotionCriteria criteria = new PromotionCriteria();
//	        Integer units= criteria.getUnits();
//	        criteria.setUserRole(userRole);
//	        criteria.setDuration(duration);
//	        criteria.setCategory(category.getPcatagory());
//	        criteria.setSaleAmount(SaleAmount);
//	        criteria.setUnits(units);
//	        criteriaRepo.save(criteria);
//	    }
//	}
//
//
//	    public void applyPromotionCriteria() {
//	        // Logic to apply promotion criteria remains the same
//	    }

}