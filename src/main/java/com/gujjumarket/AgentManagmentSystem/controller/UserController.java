package com.gujjumarket.AgentManagmentSystem.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.PaymentRepository;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.service.SellService;
import com.gujjumarket.AgentManagmentSystem.service.UserService;
import com.gujjumarket.AgentManagmentSystem.service.withdrawalService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserController {
	@Autowired
	UserService uService;
//--faisal	
	@Autowired
	withdrawalService wservice;
	@Autowired
	withdrawalRequestRepo wRepo;
	@Autowired
	Userrepo uRepo;
	@Autowired
	SellService sellService;
	@Autowired
	private PaymentRepository paymentRepo;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam Long Usermobile, @RequestParam String Userpassword,
			HttpSession session) {
		return uService.loginUser(Usermobile, Userpassword, session);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("Logout Successful");
	}

		@PutMapping("/change-password")
		public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody, HttpSession session) {
			String currentPassword = requestBody.get("current_password"); // Correcting the field name
			String newPassword = requestBody.get("new_password");
			String confirmPassword = requestBody.get("confirm_password");
	
			Integer userId = (Integer) session.getAttribute("userID");
	
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
			}
	
			Optional<User> userOptional = uRepo.findById(userId);
	
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
			}
	
			User user = userOptional.get();
	
			try {
				uService.changePassword(currentPassword, newPassword, confirmPassword, user);
				return ResponseEntity.ok("Password updated successfully.");
			} catch (IllegalArgumentException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		}
	
	@GetMapping("/managedUsers")
    public ResponseEntity<List<Map<String, Object>>> getUsersManagedByUser(@RequestBody Map<String, Object> user) {
        try {
            // Check if userId is provided and valid
            if (user.containsKey("userId") && user.get("userId") instanceof Integer) {
                int userId = (int) user.get("userId");
                System.out.println("User ID: " + userId);

                List<Map<String, Object>> managedUsers = uService.getMyTeam(userId);

                if (managedUsers.isEmpty()) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok(managedUsers);
                }
            }

            // Check if role is provided and valid
            if (user.containsKey("role") && user.get("role") instanceof String) {
                String role = (String) user.get("role");
                System.out.println("Role: " + role);

                List<Map<String, Object>> usersByRole = uService.getUsersByRole(role);

                if (usersByRole.isEmpty()) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok(usersByRole);
                }
            }

            // If neither userId nor role is provided, return bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Bad request due to type issues
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // General error handling
        }
    }

//	@CrossOrigin
//	@PostMapping("forgetpassword")
//	public ResponseEntity<?> forgetpasswordpage(@RequestBody User user, HttpSession session) {
//		Long Usermobile = user.getUsermobile();
////		System.out.println(Usermobile);
//		User u = uService.getUserMobile(Usermobile);
//		if (u != null) {
////			String q1 = u.getQuestion1();
////			String q2 = u.getQuestion2();
//			session.setAttribute("Usermobile", Usermobile);
////			Map<String, String> response = new HashMap<String, String>();
////			response.put("Security Question 1", q1);
////			response.put("Security Question 2", q2);
//			return ResponseEntity.status(HttpStatus.OK).body("success");
//		}
//		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not Found");
//	}

//	@CrossOrigin
//	@PostMapping("newpassword")
//	public ResponseEntity<?> newpassword(@RequestBody Map<String, String> password, HttpSession session) {
//		Long Usermobile = (Long) session.getAttribute("Usermobile");
//		User u = uService.getUserMobile(Usermobile);
//		String np = password.get("np");
//		String cnp = password.get("cnp");
//		if (u != null && np.equals(cnp)) {
//
//			uService.savepassword(u, cnp);
//			return ResponseEntity.status(HttpStatus.OK).body("Password set Successfully & Forwarding to Login page");
//		}
//		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN)
//				.body("User not Found or Input password Mismatch");
//	}
	@PostMapping("newpassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        Long userMobile = Long.valueOf(request.get("Usermobile"));
        String newPassword = request.get("np");
        String confirmNewPassword = request.get("cnp");

        // Check if user exists
        User user = uService.getUserMobile(userMobile);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }

        // Save the new password
        uService.savepassword(user, newPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully");
    }

	@PostMapping("/individualSalesDetails")
	public ResponseEntity<?> getIndividualSalesDetails(@RequestBody User user, HttpSession session) {
		Integer userId = user.getUserid();
		Integer userIdFromSession = (Integer) session.getAttribute("userID");

		if (userIdFromSession != null && userIdFromSession.equals(userId)) {
			List<Map<String, Object>> salesDetails = sellService.getSalesDetailsByUserId(userId); // Call the correct
																									// service method
			return ResponseEntity.status(HttpStatus.OK).body(salesDetails);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}

	@PostMapping("/teamSalesDetails")
	public ResponseEntity<?> getTeamSalesDetails(HttpSession session) {
		Integer userIdFromSession = (Integer) session.getAttribute("userID");

		if (userIdFromSession != null) {
			List<Map<String, Object>> salesDetails = sellService.getSalesDetailsForUserAndTeam(userIdFromSession);
			return ResponseEntity.status(HttpStatus.OK).body(salesDetails);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}

	@PostMapping("/teamCommission")
	public ResponseEntity<Double> getTeamCommission(@RequestBody User request) {
		int userId = request.getUserid();
		double totalCommission = sellService.getTotalCommissionForTeam(userId);
		return ResponseEntity.ok(totalCommission);
	}

	@PostMapping("/teamCommissionIncludeLeader")
	public ResponseEntity<Double> getTeamCommissionIncludeLeader(@RequestBody User request) {
		int userId = request.getUserid();
		double totalCommission = sellService.getTotalCommissionForTeamIncludeLeader(userId);
		return ResponseEntity.ok(totalCommission);
	}

//	--faisal 
//	@PostMapping("/salesHistory")
//	public ResponseEntity<Map<String, Object>> getSalesHistory() {
//		Map<String, Object> salesHistory = new HashMap<>();
//
//		// Get the last sale
//		Sell lastSale = sellService.getLastSale();
//		salesHistory.put("lastSale", lastSale);
//
//		// Get all sales history
//		List<Sell> allSalesHistory = sellService.getAllSalesHistory();
//		salesHistory.put("allSalesHistory", allSalesHistory);
//
//		return ResponseEntity.ok(salesHistory);
//	}
//	 @PostMapping("/salesHistory")
//	    public ResponseEntity<Map<String, Object>> getSalesHistory(@RequestBody Map<String, Integer> request) {
//	        int userId = request.get("Userid");
//
//	        Map<String, Object> salesHistory = new HashMap<>();
//
//	        // Get the last sale for the given user
//	        Sell lastSale = sellService.getLastSaleByUserId(userId);
//	        salesHistory.put("lastSale", lastSale);
//
//	        // Get all sales history for the given user
//	        List<Sell> allSalesHistory = sellService.getAllSalesHistoryByUserId(userId);
//	        salesHistory.put("allSalesHistory", allSalesHistory);
//
//	        return ResponseEntity.ok(salesHistory);
//	    }
	@PostMapping("/salesHistory")
	public ResponseEntity<Map<String, Object>> getSalesData(@RequestBody Map<String, Integer> request) {
	    int userId = request.get("Userid");

	    Map<String, Object> salesData = new HashMap<>();

	    // Get the most recent sale for the user
	    Optional<Sell> lastSale = sellService.getLastSaleByUserId(userId);
	    salesData.put("lastSale", lastSale.orElse(null)); // If no sale, add null

	    // Get all sales history for the user
	    List<Sell> allSalesHistory = sellService.getAllSalesHistoryByUserId(userId);
	    salesData.put("allSalesHistory", allSalesHistory);

	    return ResponseEntity.ok(salesData);
	}


//	@PostMapping("/set-sales-target")
//	public ResponseEntity<String> setSalesTarget(@RequestBody User request) {
//		String message = sellService.setSalesTargets(request.getUserid(), request.getMonthlyTarget(),
//				request.getQuarterlyTarget(), request.getHalfYearlyTarget(), request.getYearlyTarget());
//		return ResponseEntity.ok(message);
//	}

	@PostMapping("/get-sales-target")
	public ResponseEntity<User> getSalesTarget(@RequestBody User request) {
		Integer userId = request.getUserid();
		User response = sellService.getSalesTarget(userId);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//	@PostMapping("/create_order")
//	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException, ParseException {
//		int amt = Integer.parseInt(data.get("amount").toString());
//		String transactionType = (String) data.get("transactionType");
//		String paymentMethod = (String) data.get("paymentMethod");
//
//		RazorpayClient client = new RazorpayClient("rzp_test_fCrALWaeONsvFb", "e8kwIQAebsGbktpIhUhD73I2");
//		JSONObject ob = new JSONObject();
//		ob.put("amount", amt * 100);
//		ob.put("currency", "INR");
//		ob.put("receipt", "txn_854758");
//
//		Order order = client.orders.create(ob);
//
//		Payment payment = new Payment();
//		payment.setTransactionAmount(amt + "");
//		payment.setTransactionId(order.get("id"));
//		payment.setPaymentMethod(paymentMethod);
//		payment.setTransactionType(transactionType);
//		payment.setPaymentId(null);
////		payment.setStatus(order.get("status"));
//		payment.setStatus("Initiated");
//
//		LocalDateTime currentDateTime = LocalDateTime.now();
//		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//		payment.setTransactionDateTime(currentDate);
//		payment.setTotalAmount(amt); // Set total amount for partial payment
//		payment.setRemainingAmount(amt); // Initially, remaining amount is equal to total amount
//
//		paymentRepo.save(payment);
//		return order.toString();
//	}
//
//	@PostMapping("/update_order")
//	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
//		String orderId = data.get("order_id").toString();
//		Payment myPayment = paymentRepo.findByTransactionId(orderId);
//		if (myPayment == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
//		}
//
//		String paymentId = generatePaymentId(orderId);
//		myPayment.setPaymentId(paymentId);
//
//		// Get the remaining amount and paid amount
//		int remainingAmount = myPayment.getRemainingAmount();
//		int paidAmount = Integer.parseInt(data.get("amount").toString());
//
//		// Check if the paid amount exceeds the remaining amount
//		if (paidAmount > remainingAmount) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Paid amount exceeds the required amount");
//		}
//
//		// Update the remaining amount
//		remainingAmount -= paidAmount;
//		myPayment.setRemainingAmount(remainingAmount);
//
//		// Set status based on remaining amount
//		if (remainingAmount <= 0) {
//			myPayment.setStatus("paid");
//
//		} else {
//			myPayment.setStatus("partial");
//		}
//
//		paymentRepo.save(myPayment);
//
//		// Construct the response message
//		String responseMessage;
//		if (remainingAmount > 0) {
//			responseMessage = "Partial payment successful! Remaining amount is: " + remainingAmount;
//		} else {
//			responseMessage = "payment completed no due remaining!";
//		}
//
//		return ResponseEntity.ok(responseMessage);
//	}
//
//	// Method to generate a unique payment ID based on the order ID
//	private String generatePaymentId(String orderId) {
//		String uniqueIdentifier = Long.toString(System.currentTimeMillis());
//		return "PAY-" + orderId.replace("order_", "") + "-" + uniqueIdentifier;
//	}

	@PostMapping("/hierarchy")
	public ResponseEntity<List<Map<String, Object>>> getSalesDataForUserHierarchy(
			@RequestBody Map<String, Integer> request) {
		int userId = request.get("userid");
		List<Sell> salesData = uService.getSalesDataForUserHierarchy(userId);

		List<Map<String, Object>> response = new ArrayList<>();
		for (Sell sell : salesData) {
			Map<String, Object> data = uService.populateCommissionDetails(sell);
			response.add(data);
		}

		return ResponseEntity.ok(response);
	}

	// -----------------------
//		@PostMapping("login")
//		public ResponseEntity<?> login(@RequestParam Long Usermobile, @RequestParam String Userpassword,
//				HttpSession session) {
//			User u = uService.getlogin(Usermobile, Userpassword);
//			System.out.println(Userpassword + u.getUserpassword());
	//
//			if (u != null && Userpassword.equals(u.getUserpassword()) && Usermobile.equals(u.getUsermobile())) {
//				String mobile = String.valueOf(Usermobile);
//				String jwtToken = JWT.generateToken(mobile, u.getRole());
//				session.setAttribute("userID", u.getUserid());
//				Map<String, Object> response = new HashMap<String, Object>();
//				response.put("token", jwtToken);
//				response.put("role", u.getRole());
//				response.put("userid", u.getUserid());
//				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
//			} else if (u != null && Usermobile.equals(u.getUsermobile())
//					&& passwordHasher.verifyPassword(Userpassword, u.getUserpassword())) {
//				String mobile = String.valueOf(Usermobile);
//				String jwtToken = JWT.generateToken(mobile, u.getRole());
//				session.setAttribute("userID", u.getUserid());
//				Map<String, Object> response = new HashMap<String, Object>();
//				response.put("token", jwtToken);
//				response.put("role", u.getRole());
//				response.put("userid", u.getUserid());
//				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
//			}
//			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid User credentials");
//		}
//		@PostMapping("/login")
//		public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
//		    // Extract userMobile and userPassword from the User object
//		    Long Usermobile = user.getUsermobile();
//		    String Userpassword = user.getUserpassword();
//		    return uService.loginUser(Usermobile, Userpassword, session);
//		}
//		@PostMapping("forgetpassword")
//		public ResponseEntity<?> forgetpassword(@RequestBody Map<String, String> Answers, HttpSession session) {
//			Long Usermobile = (Long) session.getAttribute("Usermobile");
//			User u = uService.getSecQuestion(Usermobile);
//			String Answer1 = Answers.get("Answer1");
//			String Answer2 = Answers.get("Answer2");
//			if (u != null && u.getSecurityanswer1().equals(Answer1) && u.getSecurityanswer2().equals(Answer2)) {
//				return ResponseEntity.status(HttpStatus.OK).body("Forwarding to generate new password");
//			}
//			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN)
//					.body("User not Found or Answer Mismatch");
//		}

//		--faisal
//		@PostMapping("/withdraw")
//		public ResponseEntity<String> withdraw(@RequestBody WithdrawalRequest withdrawalRequest) {
//			// Retrieve the user from the database based on the provided user ID
//			if (withdrawalRequest == null || withdrawalRequest.getUser() == null || withdrawalRequest.getAmount() <= 0) {
//				return ResponseEntity.badRequest().body("Invalid withdrawal request.");
//			}
//			User user = uRepo.findById(withdrawalRequest.getUser().getUserid()).orElse(null);
	//
//			if (user == null) {
//				System.out.println("User not found");
//				return ResponseEntity.badRequest().body("User not found");
//			} else if (wservice.canWithdraw(user, withdrawalRequest.getAmount())) {
	//
//				double withdrawalAmount = withdrawalRequest.getAmount();
//				System.out.println(user.getTotalCommissionAmount());
//				double remainingCommission = user.getTotalCommissionAmount() - withdrawalAmount;
//				if (remainingCommission < 1000) {
//					System.out.println("Withdrawal not allowed. Remaining amount is less than 1000.");
//					return ResponseEntity.badRequest().body("Withdrawal not allowed. Remaining amount is less than 1000.");
//				}
//				user.setTotalCommissionAmount(remainingCommission);
//				uRepo.save(user); // Save the updated user details
//				WithdrawalRequest newWithdrawalRequest = new WithdrawalRequest();
//				newWithdrawalRequest.setUser(user);
//				newWithdrawalRequest.setAmount(withdrawalRequest.getAmount());
//				newWithdrawalRequest.setRemainingAmount(remainingCommission);
//				newWithdrawalRequest.setStatus("pending");
////				newWithdrawalRequest.setTotalAmount(user.getTotalCommissionAmount());			
//				newWithdrawalRequest.setProcessed(false); // Assuming withdrawal is not processed yet
//				newWithdrawalRequest.setRequestDate(new Date()); // Set the current date
//				newWithdrawalRequest.setRole(user.getRole());// Set the current date
	//
//				wRepo.save(newWithdrawalRequest); // Save the withdrawal request to the database
	//
//				System.out.println("Withdrawal successful!");
//				return ResponseEntity.ok("Withdrawal successful!");
//			} else {
//				System.out.println("Withdrawal not allowed. Insufficient commission amount.");
//				return ResponseEntity.badRequest().body("Withdrawal not allowed. Insufficient commission amount.");
//			}
//		}

//		--faisal
//		@GetMapping("/totalAmount")
//		public ResponseEntity<Map<String, Double>> getTotalSalesAmount(@RequestParam(required = false) Integer Userid) {
//			Map<String, Double> salesAmount;
//			if (Userid != null) {
//				salesAmount = sellService.getTotalSalesAmountByAgent(Userid);
//			} else {
//				salesAmount = sellService.getTotalSalesAmountByAgent(null);
//			}
//			return ResponseEntity.ok(salesAmount);
//		}
//		@GetMapping("/individualSalesAmount")
//		public ResponseEntity<Map<String, Double>> getIndividualSalesAmount(@RequestParam Integer userId) {
//		    Map<String, Double> salesAmount = sellService.getTotalSalesAmountByAgent(userId);
//		    return ResponseEntity.ok(salesAmount);
//		}
//		@GetMapping("/teamSalesAmount")
//		public ResponseEntity<Map<String, Double>> getTeamSalesAmount(@RequestParam Integer userId) {
//		    Map<String, Double> salesAmount = sellService.getTotalSalesAmountForTeam(userId);
//		    return ResponseEntity.ok(salesAmount);
//		}
//		@GetMapping("/individualSalesAmount")
//		public ResponseEntity<Map<String, Double>> getIndividualSalesAmount(@RequestBody User requestBody) {
//			Integer userId = requestBody.getUserid();
//			Map<String, Double> salesAmount = sellService.getTotalSalesAmountByAgent(userId);
//			return ResponseEntity.ok(salesAmount);
//		}
	//
//		@GetMapping("/teamSalesAmount")
//		public ResponseEntity<Map<String, Double>> getTeamSalesAmount(@RequestBody User requestBody) {
//			Integer userId = requestBody.getUserid();
//			Map<String, Double> salesAmount = sellService.getTotalSalesAmountForTeam(userId);
//			return ResponseEntity.ok(salesAmount);
//		}
//		@GetMapping("/individualSalesDetails")
//		public ResponseEntity<List<Map<String, Object>>> getIndividualSalesDetails(@RequestBody User requestBody) {
//		    Integer userId = requestBody.getUserid();
//		    List<Map<String, Object>> salesDetails = sellService.getSalesDetailsByUserId(userId);
//		    return ResponseEntity.ok(salesDetails);
//		}
	//
//		@GetMapping("/teamSalesDetails")
//		public ResponseEntity<List<Map<String, Object>>> getTeamSalesDetails(@RequestBody User requestBody) {
//		    Integer userId = requestBody.getUserid();
//		    List<Map<String, Object>> salesDetails = sellService.getSalesDetailsForUserAndTeam(userId);
//		    return ResponseEntity.ok(salesDetails);
//		}
//		@GetMapping("/teamCommission")
//		public ResponseEntity<Double> getTeamCommission(@RequestBody User request) {
//		    int userId = request.getUserid();
//		    double totalCommission = sellService.getTotalCommissionForTeam(userId);
//		    return ResponseEntity.ok(totalCommission);
//		}
//		@PostMapping("/set")
//		public ResponseEntity<String> setSalesTarget(@RequestBody User request) {
//			String message = sellService.setSalesTarget(request.getRole(),request.getUserid(), request.getMonthlyTarget(),
//					request.getQuarterlyTarget(), request.getHalfYearlyTarget(), request.getYearlyTarget());
//			System.out.println(message);
//			return ResponseEntity.ok(message);
//		}
//		@PostMapping("/hierarchy")
//		public ResponseEntity<List<Sell>> getSalesDataForUserHierarchy(@RequestBody User request) {
//			int userid=request.getUserid();
//			List<Sell> salesData = uService.getSalesDataForUserHierarchy(userid);
//			return ResponseEntity.ok(salesData);
//		}
//	@PostMapping("/commissionHierarchy")
//	public ResponseEntity<List<Map<String, Object>>> getCommissionDetailsByHierarchy(@RequestBody Map<String, Integer> requestBody) {
//	    Integer userId = requestBody.get("userId");
//	    if (userId == null) {
//	        // Return a List<Map<String, Object>> with a single error message
//	        List<Map<String, Object>> errorResponse = Collections.singletonList(
//	            Collections.singletonMap("error", "User ID is required.")
//	        );
//	        return ResponseEntity.badRequest().body(errorResponse);
//	    }
//
//	    Optional<User> userOptional = uRepo.findById(userId);
//	    if (!userOptional.isPresent()) {
//	        // Return a List<Map<String, Object>> with a single error message
//	        List<Map<String, Object>> errorResponse = Collections.singletonList(
//	            Collections.singletonMap("error", "User not found.")
//	        );
//	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//	    }
//
//	    User user = userOptional.get();
//	    List<Sell> allSales = uService.getAllSalesForUserAndSubordinates(user);
//
//	    List<Map<String, Object>> response = new ArrayList<>();
//	    for (Sell sell : allSales) {
//	        Map<String, Object> commissionDetails = uService.populateCommissionDetails(sell);  // Your existing logic
//	        response.add(commissionDetails);
//	    }
//
//	    return ResponseEntity.ok(response);
//	}
	//
//		@PostMapping("/create_order")
//		public String createOrder(@RequestBody Map<String, Object> data,Principal principal)
//				throws RazorpayException, ParseException {
//			System.out.println("hello function executed?");
////			String username = (String) session.getAttribute("Username");
////			Integer Userid= (Integer) session.getAttribute("Userid");
////			Integer Userid=(Integer) data.get("Userid");
////			System.out.println(Userid);
//			int amt = Integer.parseInt(data.get("amount").toString());
//			String transactionType = (String) data.get("transactionType");
//			String paymentMethod = (String) data.get("paymentMethod");
//			RazorpayClient client = new RazorpayClient("rzp_test_fCrALWaeONsvFb", "e8kwIQAebsGbktpIhUhD73I2");
//			JSONObject ob = new JSONObject();
//			ob.put("amount", amt * 100);
//			ob.put("currency", "INR");
//			ob.put("receipt", "txn_854758");
////			creating the new order.
//			Order order = client.orders.create(ob);
//			System.out.println(order);
	//
//			Payment payment = new Payment();
//			payment.setTransactionAmount(order.get("amount") + "");
//			payment.setTransactionId(order.get("id"));
//			payment.setPaymentMethod(paymentMethod);
//			payment.setTransactionType(transactionType);
//			payment.setPaymentId(null);
//			payment.setStatus(order.get("status"));
//			// Parse and set transaction date time
//			// Get the current date and time
//			LocalDateTime currentDateTime = LocalDateTime.now();
//			// Convert LocalDateTime to Date
//			Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//			// Set the current date and time to the payment object
//			payment.setTransactionDateTime(currentDate);
	//
////			User user = uRepo.getUserByUsername(username);
////			User user = uRepo.findByUserid(Userid);
////		    User user = (User) session.getAttribute("user");
	//
//		    // Set the User object in the Payment entity
////			
////		    payment.setUser(this.uRepo.findByUserid(principal.getName()));
	//
//			// Set the User object in the Payment entity
////			payment.setUser();
////			this.paymentRepo.save(payment);
	//
//			return order.toString();
//		}

//		@PostMapping("/create_order")
//		public String createOrder(@RequestBody Map<String, Object> data)
//				throws RazorpayException, ParseException {
//			System.out.println("hello function executed?");
////			String username = (String) session.getAttribute("Username");
////			Integer Userid = (Integer) data.get("Userid");
////			System.out.println(Userid);
//			int amt = Integer.parseInt(data.get("amount").toString());
//			String transactionType = (String) data.get("transactionType");
//			String paymentMethod = (String) data.get("paymentMethod");
//			RazorpayClient client = new RazorpayClient("rzp_test_fCrALWaeONsvFb", "e8kwIQAebsGbktpIhUhD73I2");
//			JSONObject ob = new JSONObject();
//			ob.put("amount", amt*100);
//			ob.put("currency", "INR");
//			ob.put("receipt", "txn_854758");
////			creating the new order.
//			Order order = client.orders.create(ob);
//			System.out.println(order);
	//
//			Payment payment = new Payment();
//			payment.setTransactionAmount(amt+"");
//			payment.setTransactionId(order.get("id"));
//			payment.setPaymentMethod(paymentMethod);
//			payment.setTransactionType(transactionType);
//			payment.setPaymentId(null);
//			payment.setStatus(order.get("status"));
//			// Parse and set transaction date time
//			// Get the current date and time
//			LocalDateTime currentDateTime = LocalDateTime.now();
//			// Convert LocalDateTime to Date
//			Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//			// Set the current date and time to the payment object
//			payment.setTransactionDateTime(currentDate);
//			paymentRepo.save(payment);
//			return order.toString();
//		}

//		@PostMapping("/update_order")
//		public ResponseEntity<?>updateOrder(@RequestBody Map<String,Object> data){
//			
//			Payment mypay=paymentRepo.findByTransactionId(data.get("order_id").toString());
//			mypay.setPaymentId(data.get("payment_id").toString());
//			mypay.setStatus(data.get("status").toString());
//			
//			paymentRepo.save(mypay);
//			
//			System.out.println(data);
//			
//			return ResponseEntity.ok("");
//		}

//		@PostMapping("/update_order")
//		public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
//		    // Retrieve order ID from the request data
//		    String orderId = data.get("order_id").toString();
//		    
//		    // Find the payment associated with the order ID
//		    Payment myPayment = paymentRepo.findByTransactionId(orderId);
//		    
//		    // Generate a new payment ID based on the order ID
//		    String paymentId = generatePaymentId(orderId); // You need to implement this method
//		    
//		    // Set the payment ID and status from the request data
////		    --check this two line when frontend of this given that if this data come from database else
////		    myPayment.setPaymentId(data.get("payment_id").toString());
////		    myPayment.setStatus(data.get("status").toString());
//		    
////		    Use This
//		    myPayment.setPaymentId(paymentId);
//		    myPayment.setStatus("paid");
//		    
//		    // Save the updated payment information
//		    paymentRepo.save(myPayment);
//		    
//		    // Log the request data
//		    System.out.println(data);
//		    
//		    return ResponseEntity.ok("");
//		}
	//
////		 Method to generate a payment ID based on the order ID
//		private String generatePaymentId(String orderId) {
//		    // Concatenate a prefix or suffix to the order ID to create the payment ID
//		    return "PAY-" + orderId; // Example: Prefix "PAY-" followed by the order ID
//		}

//		@PostMapping("/update_order")
//		public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
//		    try {
//		        String orderId = (String) data.get("order_id");
//		        String paymentId = (String) data.get("payment_id");
//		        String status =  (String) data.get("status");
//		        
//		        System.out.println(orderId);
//		        System.out.println(paymentId);
//		        System.out.println(status);
//		        // Check if any of the required fields are null
//		        if (orderId == null || paymentId == null || status == null) {
//		            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more required fields are missing");
//		        }
	//
//		        Payment mypay = paymentRepo.findByTransactionId(orderId);
//		        if (mypay == null) {
//		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found for the provided order ID");
//		        }
	//
//		        mypay.setPaymentId(paymentId);
//		        mypay.setStatus(status);
	//
//		        paymentRepo.save(mypay);
	//
//		        System.out.println(data);
	//
//		        return ResponseEntity.ok("");
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal server error occurred");
//		    }
//		}
//	--faisal
//	@PostMapping("/{userid}/logout")
//	public ResponseEntity<String> logout(@PathVariable int userid, HttpSession session) {
//		session.invalidate();
//		return ResponseEntity.ok("Logout Successful");
//	}
}
