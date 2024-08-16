package com.gujjumarket.AgentManagmentSystem.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gujjumarket.AgentManagmentSystem.model.Customer;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
import com.gujjumarket.AgentManagmentSystem.repo.CountryheadRepo;
import com.gujjumarket.AgentManagmentSystem.repo.CustomerRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;

@Service
public class SellService {

	@Autowired
	SellRepo sellRepo;
	@Autowired
	CountryheadRepo chRepo;
	@Autowired
	ProductRepo pRepo;
	@Autowired
	AdminRepo adRepo;
	@Autowired
	Userrepo uRepo;
	@Autowired
	CustomerRepo cRepo;

//	public void sell(Map<String, Object> sell, Integer userid1) {
//		User u = chRepo.getReferenceById(userid1);
//
//		Integer pid = (Integer) sell.get("pid");
//		Product p = pRepo.getReferenceById(pid);
//
//		String CN = (String) sell.get("customername");
//		String CM = (String) sell.get("customermobile");
//		String CA = (String) sell.get("customeraddress");
//		String CC = (String) sell.get("customercityorvillage");
//		String CS = (String) sell.get("customerstate");
//		String CE = (String) sell.get("customeremail");
//		String CD=(String) sell.get("customerdistrict");
//		String subDuration=(String) sell.get("subscriptionDuration");
//		
//
//		Sell s = new Sell();
//		s.setRegisterDate(java.sql.Date.valueOf(LocalDate.now()));
//		s.setUsername(u.getUsername());
//		s.setUserrole(u.getRole());
//		s.setProductname(p.getPname());
//		s.setSaleamount(p.getPprice());
//		s.setSalestatus("Pending");
//		s.setSAcomm(p.getSAcomm() * p.getPprice());
//		s.setAcomm(p.getAcomm() * p.getPprice());
//		s.setCityhcomm(p.getCityhcomm() * p.getPprice());
//		s.setDHcomm(p.getDHcomm() * p.getPprice());
//		s.setSTcomm(p.getSTcomm() * p.getPprice());
//		s.setCHcomm(p.getCHcomm() * p.getPprice());
//		s.setTotalCommissionAmount((p.getSAcomm() * p.getPprice()) + (p.getAcomm() * p.getPprice())
//				+ (p.getCityhcomm() * p.getPprice()) + (p.getDHcomm() * p.getPprice()) + (p.getSTcomm() * p.getPprice())
//				+ (p.getCHcomm() * p.getPprice()));
//
//		s.setSoldby(u);
//		s.setPid(pid);
//		
//		LocalDate renewalDate = LocalDate.now();
//	    if ("month".equals(subDuration)) {
//	        renewalDate = renewalDate.plusMonths(1);
//	    } else if ("6 months".equals(subDuration)) {
//	        renewalDate = renewalDate.plusMonths(6);
//	    } else if ("1 year".equals(subDuration)) {
//	        renewalDate = renewalDate.plusYears(1);
//	    }else if("quater".equals(subDuration)) {
//	    	renewalDate=renewalDate.plusMonths(4);
//	    }
//	    
//	    s.setRenewaldate(java.sql.Date.valueOf(renewalDate));
//	    s.setRenewalStatus("Ongoing");
//		
//		Customer getcustomer = cRepo.findbyname(CN);
//		System.out.println(getcustomer);
//
//		if (getcustomer == null) {
//			Customer c = new Customer();
//			c.setCustomername(CN);
//			c.setCustomermobile(CM);
//			c.setCustomeraddress(CA);
//			c.setCustomercityorvillage(CC);
//			c.setCustomerstate(CS);
//			c.setCustomeremail(CE);
//			c.setTotalspend(p.getPprice());
//			c.setCustomerdistrict(CD);
//
//			cRepo.save(c);
//			s.setCustomer(c);
//		} else {
//			getcustomer.setTotalspend(getcustomer.getTotalspend() + p.getPprice());
//			cRepo.save(getcustomer);
//			s.setCustomer(getcustomer);
//		}
//
//		sellRepo.save(s);
//	}
	
	public Map<String, Integer> sell(Map<String, Object> sell, Integer userid1) {
	    User u = chRepo.getReferenceById(userid1);

	    Integer pid = (Integer) sell.get("pid");
	    Product p = pRepo.getReferenceById(pid);

	    String CN = (String) sell.get("customername");
	    String CM = (String) sell.get("customermobile");
	    String CA = (String) sell.get("customeraddress");
	    String CC = (String) sell.get("customercityorvillage");
	    String CS = (String) sell.get("customerstate");
	    String CE = (String) sell.get("customeremail");
	    String CD = (String) sell.get("customerdistrict");
	    String subDuration = (String) sell.get("selectedDuration");

	    Sell s = new Sell();
	    s.setRegisterDate(java.sql.Date.valueOf(LocalDate.now()));
	    s.setUsername(u.getUsername());
	    s.setUserrole(u.getRole());
	    s.setProductname(p.getPname());
	    s.setSaleamount(p.getPprice());
	    s.setSalestatus("Pending");
	    s.setSAcomm(p.getSAcomm() * p.getPprice());
	    s.setAcomm(p.getAcomm() * p.getPprice());
	    s.setCityhcomm(p.getCityhcomm() * p.getPprice());
	    s.setDHcomm(p.getDHcomm() * p.getPprice());
	    s.setSTcomm(p.getSTcomm() * p.getPprice());
	    s.setCHcomm(p.getCHcomm() * p.getPprice());
	    s.setTotalCommissionAmount((p.getSAcomm() * p.getPprice()) + (p.getAcomm() * p.getPprice())
	            + (p.getCityhcomm() * p.getPprice()) + (p.getDHcomm() * p.getPprice()) + (p.getSTcomm() * p.getPprice())
	            + (p.getCHcomm() * p.getPprice()));

	    s.setSoldby(u);
	    s.setPid(pid);

	    if (subDuration != null) {
	        LocalDate renewalDate = LocalDate.now();
	        if ("month".equals(subDuration)) {
	            renewalDate = renewalDate.plusMonths(1);
	            s.setRenewaldate(java.sql.Date.valueOf(renewalDate));
		        s.setRenewalStatus("Ongoing");
	        } else if ("6 months".equals(subDuration)) {
	            renewalDate = renewalDate.plusMonths(6);
	            s.setRenewaldate(java.sql.Date.valueOf(renewalDate));
		        s.setRenewalStatus("Ongoing");
	        } else if ("1 year".equals(subDuration)) {
	            renewalDate = renewalDate.plusYears(1);
	            s.setRenewaldate(java.sql.Date.valueOf(renewalDate));
		        s.setRenewalStatus("Ongoing");
	        } else if ("quater".equals(subDuration)) {
	            renewalDate = renewalDate.plusMonths(4);
	            s.setRenewaldate(java.sql.Date.valueOf(renewalDate));
		        s.setRenewalStatus("Ongoing");
	        }else if("".equals(subDuration)) {
	        	s.setRenewalStatus("No Subscription Needed");
	        }
	       
	    } 

	    Customer getcustomer = null ;
	    System.out.println(getcustomer);

	    Customer c = new Customer();
	    if (getcustomer == null) {
	        
	        c.setCustomername(CN);
	        c.setCustomermobile(CM);
	        c.setCustomeraddress(CA);
	        c.setCustomercityorvillage(CC);
	        c.setCustomerstate(CS);
	        c.setCustomeremail(CE);
	        c.setTotalspend(p.getPprice());
	        c.setCustomerdistrict(CD);

	        cRepo.save(c);
	        s.setCustomer(c);}
//	    } else {
//	        getcustomer.setTotalspend(getcustomer.getTotalspend() + p.getPprice());
//	        cRepo.save(getcustomer);
//	        s.setCustomer(getcustomer);
//	    }

	    sellRepo.save(s);
	    Map<String, Integer> response = new HashMap<>();
	    response.put("cid", c.getCid());
	    response.put("sellid", s.getSellid());
	    return response;
	    
	}


	public List<Sell> getallpendingsells() {
		List<Sell> listofpendingsell = sellRepo.findAll();
		return listofpendingsell;
	}

//	--faisal
	public Product getBestSellingProduct() {
		List<Sell> allSales = sellRepo.findAll();
		Map<String, Integer> productSalesCount = new HashMap<>();

		// Count the number of times each product is sold
		for (Sell sale : allSales) {
			String productName = sale.getProductname();
			int count = productSalesCount.getOrDefault(productName, 0);
			productSalesCount.put(productName, count + 1);
		}

		// Find the product with the highest number of sales
		String bestSellingProductName = null;
		int maxSalesCount = 0;
		for (Entry<String, Integer> entry : productSalesCount.entrySet()) {
			if (entry.getValue() > maxSalesCount) {
				maxSalesCount = entry.getValue();
				bestSellingProductName = entry.getKey();
			}
		}
		Product bestSellingProduct = pRepo.findProductByPname(bestSellingProductName);

		return bestSellingProduct;
	}

//--faisal
	public Map<String, Integer> getNumberOfSalesByAgent() {
		// Find all users with role "agent" or "subagent"
		List<User> agents = uRepo.findByRoleIn(List.of("AGENT", "SUBAGENT"));

		// Initialize counters for agent sales and subagent sales
		int agentSalesCount = 0;
		int subagentSalesCount = 0;

		// Iterate through each agent and count their sales
		for (User agent : agents) {
			// Assuming you have a method in your repository to find sales by user ID
			int salesCount = sellRepo.countBySoldbyId(agent.getUserid());
			if (agent.getRole().equals("AGENT")) {
				agentSalesCount += salesCount;
			} else if (agent.getRole().equals("SUBAGENT")) {
				subagentSalesCount += salesCount;
			}
		}

		// Create a map to hold the counts of sales made by agents and subagents
		Map<String, Integer> salesCounts = new HashMap<>();
		salesCounts.put("agentSalesCount", agentSalesCount);
		salesCounts.put("subagentSalesCount", subagentSalesCount);

		// Return the map containing the counts of sales made by agents and subagents
		return salesCounts;
	}

//--faisal
	public Map<String, Double> getTotalSalesAmountByAgent(Integer userId) {
		List<Sell> sells;
		String agentName = null;

		if (userId != null) {
			User agent = uRepo.findById(userId).orElse(null);
			if (agent != null) {
				agentName = agent.getUsername();
			}
			// If userId is provided, filter sales by that user ID
			sells = sellRepo.findBySoldbyUserId(userId);
		} else {
			// If userId is not provided, get all sales
			sells = sellRepo.findAll();
		}

		// Calculate total sales amount
		double totalSalesAmount = 0;
		for (Sell sell : sells) {
			totalSalesAmount += sell.getSaleamount();
		}

		// Create a map to hold the agent's name (or ID) and total sales amount
		Map<String, Double> result = new HashMap<>();
		if (agentName != null) {
			result.put(agentName, totalSalesAmount);
		} else {
			result.put("Total Sales Amount", totalSalesAmount);
		}
		return result;
	}

//    faisal
	public Sell getLastSale() {
		return sellRepo.findLastByOrderByApprovedDateDescLimit1().stream().findFirst().orElse(null);
	}

//  faisal
	public List<Sell> getAllSalesHistory() {
		return sellRepo.findAllByOrderByApprovedDateDesc();
	}

// faisal

//	--this method is used to set the target for particular or single subordinates...

//	public String setSalesTarget(String role, int userId, double monthlyTarget, double quarterlyTarget, double halfYearlyTarget, double yearlyTarget) {
//	    Optional<User> optionalUser = uRepo.findById(userId);
//
//	    if (optionalUser.isPresent()) {
//	        User targetUser = optionalUser.get();
//	        switch (role) {
//	            case "COUNTRYHEAD":
//	                if ("STATEHEAD".equals(targetUser.getRole())) {
//	                    setSalesTargets(userId, monthlyTarget, quarterlyTarget, halfYearlyTarget, yearlyTarget);
//	                    return "Sales targets set successfully for State Head";
//	                } else {
//	                    return "Sales target can only be set for State Head";
//	                }
//	            case "STATEHEAD":
//	                if ("DISTRICTHEAD".equals(targetUser.getRole())) {
//	                    setSalesTargets(userId, monthlyTarget, quarterlyTarget, halfYearlyTarget, yearlyTarget);
//	                    return "Sales targets set successfully for District Head";
//	                } else {
//	                    return "Sales target can only be set for District Head";
//	                }
//	            case "DISTRICTHEAD":
//	                if ("CITYHEAD".equals(targetUser.getRole())) {
//	                    setSalesTargets(userId, monthlyTarget, quarterlyTarget, halfYearlyTarget, yearlyTarget);
//	                    return "Sales targets set successfully for City Head";
//	                } else {
//	                    return "Sales target can only be set for City Head";
//	                }
//	            // Add cases for other roles
//	            default:
//	                return "Invalid role for setting sales target.";
//	        }
//	    } else {
//	        return "User not found";
//	    }
//	}
//
//	private void setSalesTargets(int userId, double monthlyTarget, double quarterlyTarget, double halfYearlyTarget, double yearlyTarget) {
//	    Optional<User> optionalUser = uRepo.findById(userId);
//	    if (optionalUser.isPresent()) {
//	        User user = optionalUser.get();
//	        user.setMonthlyTarget(monthlyTarget);
//	        user.setQuarterlyTarget(quarterlyTarget);
//	        user.setHalfYearlyTarget(halfYearlyTarget);
//	        user.setYearlyTarget(yearlyTarget);
//	        uRepo.save(user);
//	    } else {
//	        // Handle the case where the user is not found
//	    }
//	}
// the upper method working ..

//	--this method is using to set the target for the all subordinates which manageby described userid in postman...
	public String setSalesTargets(int userId, double monthlyTarget, double quarterlyTarget, double halfYearlyTarget,
			double yearlyTarget) {
		Optional<User> optionalUser = uRepo.findById(userId);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			// Check if the user is the appropriate role to set sales targets
			if (!isValidRoleForSalesTargets(user)) {
				return "User with ID " + userId + " cannot set sales targets for subordinates.";
			}

			// Find subordinates managed by the user
			List<User> subordinates = uRepo.findByManageBy(userId);

			// Set sales targets for each subordinate
			for (User subordinate : subordinates) {
				setSalesTargets(subordinate, monthlyTarget, quarterlyTarget, halfYearlyTarget, yearlyTarget);
			}

			return "Sales targets set successfully for subordinates managed by user with ID " + userId + ".";
		} else {
			return "User with ID " + userId + " not found.";
		}
	}

	private boolean isValidRoleForSalesTargets(User user) {
		String role = user.getRole();
		return role.equals("COUNTRYHEAD") || role.equals("STATEHEAD") || role.equals("DISTRICTHEAD")
				|| role.equals("CITYHEAD");
	}

	private void setSalesTargets(User user, double monthlyTarget, double quarterlyTarget, double halfYearlyTarget,
			double yearlyTarget) {
		// Set sales targets for the user
//		user.setMonthlyTarget(monthlyTarget);
//		user.setQuarterlyTarget(quarterlyTarget);
//		user.setHalfYearlyTarget(halfYearlyTarget);
//		user.setYearlyTarget(yearlyTarget);
		uRepo.save(user);
	}

	public User getSalesTarget(Integer Userid) {
		Optional<User> optionalUser = uRepo.findById(Userid);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			User response = new User();
//			response.setUserid(user.getUserid());
//			response.setMonthlyTarget(user.getMonthlyTarget());
//			response.setQuarterlyTarget(user.getQuarterlyTarget());
//			response.setHalfYearlyTarget(user.getHalfYearlyTarget());
//			response.setYearlyTarget(user.getYearlyTarget());
			return response;
		} else {
			return null;
		}
	}

//	public Map<String, Double> getTotalSalesAmountForTeam(Integer userId) {
//	    // Initialize total sales amount
//	    double totalSalesAmount = 0.0;
//
//	    // Get the agent and their subagents
//	    User agent = uRepo.findById(userId).orElse(null);
//	    List<User> teamMembers = uRepo.findByManageBy(userId);
//
//	    // Include sales amount of the agent
//	    List<Sell> agentSells = sellRepo.findBySoldbyUserId(userId);
//	    for (Sell sell : agentSells) {
//	        totalSalesAmount += sell.getSaleamount();
//	    }
//
//	    // Include sales amount of the subagents
//	    for (User subagent : teamMembers) {
//	        List<Sell> subagentSells = sellRepo.findBySoldbyUserId(subagent.getUserid());
//	        for (Sell sell : subagentSells) {
//	            totalSalesAmount += sell.getSaleamount();
//	        }
//	    }

	// Create a map to hold the total sales amount
//	    Map<String, Double> result = new HashMap<>();
//	    result.put("Total Sales Amount", totalSalesAmount);
//	    return result;
//	}
//	public List<Map<String, Object>> getSalesDetailsByUserId(Integer userId) {
//		List<Object[]> results = sellRepo.getSalesDetailsByUserId(userId);
//		return mapResultsToSalesDetails(results);
//	}
//
//	public List<Map<String, Object>> getSalesDetailsForUserAndTeam(Integer userId) {
//		List<Object[]> results = sellRepo.getSalesDetailsForUserAndTeam(userId);
//		return mapResultsToSalesDetails(results);
//	}
	
//	-- this two for individual.
	public List<Map<String, Object>> getSalesDetailsByUserId(Integer userId) {
		List<Sell> sells = sellRepo.findBySoldbyUserId(userId);
		return mapSellsToSalesDetails(sells);
	}

	private List<Map<String, Object>> mapSellsToSalesDetails(List<Sell> sells) {
		List<Map<String, Object>> salesDetails = new ArrayList<>();
		for (Sell sell : sells) {
			Map<String, Object> saleDetail = new HashMap<>();
			saleDetail.put("productName", sell.getProductname());
			saleDetail.put("sellId", sell.getSellid());
			saleDetail.put("saleAmount", sell.getSaleamount());
			saleDetail.put("saleStatus", sell.getSalestatus());
			saleDetail.put("userRole", sell.getSoldby().getRole()); // Assuming User entity has a 'userRole' field
			salesDetails.add(saleDetail);
		}
		return salesDetails;
	}

//	-for all team member...
	
	public List<Map<String, Object>> getSalesDetailsForUserAndTeam(Integer userId) {
		// Fetch all team members including the user
		List<User> teamMembers = uRepo.findTeamMembersByUserid(userId);
		List<Map<String, Object>> salesDetails = new ArrayList<>();

		for (User teamMember : teamMembers) {
			// Get sales details for each team member
			List<Sell> sells = sellRepo.findBySoldbyUserId(teamMember.getUserid());

			// Map sales details to desired format
			for (Sell sell : sells) {
				Map<String, Object> saleDetail = new HashMap<>();
				saleDetail.put("productName", sell.getProductname());
				saleDetail.put("sellId", sell.getSellid());
				saleDetail.put("saleAmount", sell.getSaleamount());
				saleDetail.put("saleStatus", sell.getSalestatus());
				saleDetail.put("userRole", teamMember.getRole()); // Assuming user role is needed
				saleDetail.put("Userid", teamMember.getUserid()); // Assuming user role is needed

				salesDetails.add(saleDetail);
			}
		}

		return salesDetails;
	}

//	private List<Map<String, Object>> mapResultsToSalesDetails(List<Object[]> results) {
//		List<Map<String, Object>> salesDetails = new ArrayList<>();
//		for (Object[] row : results) {
//			Map<String, Object> saleDetail = new HashMap<>();
//			saleDetail.put("productName", row[0]);
//			saleDetail.put("sellId", row[1]);
//			saleDetail.put("saleAmount", row[2]);
//			saleDetail.put("saleStatus", row[3]);
//			saleDetail.put("userRole", row[4]);
//			salesDetails.add(saleDetail);
//		}
//		return salesDetails;
//	}

	public double getTotalCommissionForTeam(int userId) {
		// Query the database to find all team members managed by the given user ID
		List<User> teamMembers = uRepo.findByManageBy(userId);

		// Initialize a variable to hold the total commission
		double totalCommission = 0.0;

		// Iterate over the team members and sum up their total commission amounts
		for (User teamMember : teamMembers) {
			totalCommission += teamMember.getTotalCommissionAmount();
		}

		// Return the total commission for the team
		return totalCommission;
	}

	public double getTotalCommissionForTeamIncludeLeader(int userId) {
		// Query the database to find all team members managed by the given user ID
		List<User> teamMembers = uRepo.findByManageBy(userId);

		// Initialize a variable to hold the total commission
		double totalCommission = 0.0;

		// Fetch the commission amount for the agent
		User agent = uRepo.findById(userId).orElse(null);
		if (agent != null) {
			totalCommission += agent.getTotalCommissionAmount(); // Add agent's commission to the total
		}

		// Iterate over the team members and sum up their total commission amounts
		for (User teamMember : teamMembers) {
			totalCommission += teamMember.getTotalCommissionAmount();
		}

		// Return the total commission for the team
		return totalCommission;
	}

	 // Get the last sale for a specific user
//    public Sell getLastSaleByUserId(int userId) {
//        return sellRepo.findFirstByOrderByApprovedDateDescForUser(userId);
//    }
//
//    // Get all sales history for a specific user
//    public List<Sell> getAllSalesHistoryByUserId(int userId) {
//        return sellRepo.findAllByUserAndOrderByApprovedDateDesc(userId);
//    }
	public Optional<Sell> getLastSaleByUserId(int userId) {
	    return sellRepo.findTop1ByUserIdOrderByApprovedDateDesc(userId).stream().findFirst();
	}



	public List<Sell> getAllSalesHistoryByUserId(int userId) {
	    return sellRepo.findBySoldbyUserIdOrderByApprovedDateDesc(userId);
	}

	public double getclient(int userId) {
		// TODO Auto-generated method stub
//		sellRepo.countBySoldbyId(userId);
		return sellRepo.countBySoldbyId(userId);
	}


	  public List<Map<String, Object>> getBestSellingProducts() {
	        List<Sell> allSales = sellRepo.findAll();
	        Map<String, Integer> productSalesCount = new HashMap<>();

	        // Count the number of times each product is sold
	        for (Sell sale : allSales) {
	            String productName = sale.getProductname();
	            int count = productSalesCount.getOrDefault(productName, 0);
	            productSalesCount.put(productName, count + 1);
	        }

	        // Sort the products by sales count in descending order and get the top 10
	        List<Map<String, Object>> top10ProductSales = productSalesCount.entrySet().stream()
	            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
	            .limit(10)
	            .map(entry -> {
	                Map<String, Object> productInfo = new HashMap<>();
	                productInfo.put("productName", entry.getKey());
	                productInfo.put("salesCount", entry.getValue());
	                return productInfo;
	            })
	            .collect(Collectors.toList());

	        return top10ProductSales;
	    }


	

}
