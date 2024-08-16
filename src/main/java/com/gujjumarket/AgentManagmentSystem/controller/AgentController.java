package com.gujjumarket.AgentManagmentSystem.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gujjumarket.AgentManagmentSystem.config.DateUtil;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.model.WithdrawalRequest;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.service.ContryheadService;
import com.gujjumarket.AgentManagmentSystem.service.ProductService;
import com.gujjumarket.AgentManagmentSystem.service.SellService;
import com.gujjumarket.AgentManagmentSystem.service.UserService;
import com.gujjumarket.AgentManagmentSystem.service.withdrawalService;
import com.gujjumarket.AgentManagmentSystem.utils.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("AGENT")
public class AgentController {

	@Autowired
	ContryheadService chService;
	@Autowired
	ProductService pService;
	@Autowired
	SellService sellService;
	@Autowired
	UserService uService;
//	f
//	@Autowired
//	Userrepo Repo;
	@Autowired
	Userrepo uRepo;
	@Autowired
	withdrawalRequestRepo wRepo;

	@Value("${upload-dir}")
	private String upload_dir;

	@Autowired
	withdrawalService wservice;

//	@PostMapping("profile")
//	public ResponseEntity<?> profile(HttpServletRequest request, @RequestHeader("Authorization") String tokenHeader) {
////		Integer userid1 = (Integer) request.getAttribute("userID");
//		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
//			String token = tokenHeader.substring(7);
//			Jws<Claims> claims = Jwts.parser().verifyWith(JWT.getJwtsecret()).build().parseSignedClaims(token);
//
//			Integer userId1 = claims.getPayload().get("userId", Integer.class);
//
//			if (userId1 != null) {
//				User u = uService.getprofile(userId1);
//				if (u != null) {
//					return ResponseEntity.status(HttpStatus.ACCEPTED).body(u);
//				} else {
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
//				}
//			}
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out OR invalid credentials");
//	}
	@PostMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestBody Map<String, Integer> requestBody) {
        Integer userId = requestBody.get("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is required");
        }

        Map<String, Object> profile = uService.getprofile(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
        }
    }

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenHeader) {
		System.out.println("agent logout");
		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			String token = tokenHeader.substring(7); // Extract the JWT from the header
			JWT.blacklistToken(token); // Blacklist the token
			return ResponseEntity.ok("Logged out successfully");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing token");
	}

//	@PostMapping("/createSubAgent")
//	public ResponseEntity<?> createSubAgent(HttpServletRequest request, )
//	{
//		Integer loggedInUserId = (Integer) request.getAttribute("userId");
//		if (loggedInUserId != null && subAgent != null) {
//			String userPassword = uService.createSubAgent(loggedInUserId, subAgent);
//			return ResponseEntity.status(HttpStatus.CREATED)
//					.body("Subagent Created Successfully. Password is " + userPassword);
//		} else {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Subagent");
//		}
//	}

	@PostMapping("/createSubAgent")
	public ResponseEntity<?> createSubAgent(HttpServletRequest request, @RequestBody User request1
	) {
		Integer loggedInUserId = (Integer) request.getAttribute("userId");
		String username = request1.getUsername();
		Long mobileno = request1.getUsermobile();
		String useremail = request1.getUseremail();
		if (loggedInUserId != null) {
			User subAgent = new User();
			subAgent.setUsername(username);
			subAgent.setUseremail(useremail);
//	        
			subAgent.setUsermobile(mobileno);
//	        subAgent.setIsKYCDone(true);

			// Set other fields as necessary

			String userPassword = uService.createSubAgent(loggedInUserId, subAgent);
			
			List<User> SubagentUsers = uRepo.findByManageBy(loggedInUserId);
	        
	        // Calculate the monthly target for the logged-in user
	        User loggedInUser = uService.getUserById(loggedInUserId);
	        double monthlyTarget = loggedInUser.getMonthlyTarget();
	        
	        // Calculate the monthly target per agent user
	        double targetPerAgent = monthlyTarget / SubagentUsers.size();
	        
	        // Update the monthly target for each agent user
	        for (User SubagentUser : SubagentUsers) {
	            SubagentUser.setMonthlyTarget(targetPerAgent);
	            SubagentUser.setQuarterlyTarget(monthlyTarget*4);
	            SubagentUser.setHalfYearlyTarget(monthlyTarget*6);
	            SubagentUser.setYearlyTarget(monthlyTarget*12);
	            // Update other target values if needed
	            
	            // Save the updated agent user
	            uService.updateUser(SubagentUser);
	        }
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Subagent Created Successfully. Login details have been sent to " + subAgent.getUseremail());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Subagent");
		}
	}

	@PostMapping("/subagents")
	public ResponseEntity<?> getSubagentsManagedByLoggedInUser(HttpServletRequest request,
			@RequestBody User userIdRequest) {
		Integer loggedInUserId = (Integer) request.getAttribute("userId");

		if (loggedInUserId != null && loggedInUserId.equals(userIdRequest.getUserid())) {
			List<Map<String, Object>> subagents = uService.getSubagentsByManager(userIdRequest.getUserid());
			Collections.reverse(subagents);
			return ResponseEntity.ok(subagents);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
	}

	@PostMapping("/withdraw")
	public ResponseEntity<String> withdraw(@RequestBody WithdrawalRequest withdrawalRequest) {
		// Validate the withdrawal request
		if (withdrawalRequest == null || withdrawalRequest.getUser() == null || withdrawalRequest.getAmount() <= 0) {
			return ResponseEntity.badRequest().body("Invalid withdrawal request.");
		}

		// Retrieve the user from the database
		User user = uRepo.findById(withdrawalRequest.getUser().getUserid()).orElse(null);
		if (user == null) {
			System.out.println("User not found");
			return ResponseEntity.badRequest().body("User not found");
		}

		// Check if the user can withdraw the requested amount
		double withdrawalAmount = withdrawalRequest.getAmount();
		double totalCommissionAmount = user.getTotalCommissionAmount();

		if (wservice.canWithdraw(user, withdrawalAmount)) {
			double remainingCommission = totalCommissionAmount - withdrawalAmount;

			// Allow withdrawal of entire amount if the request exceeds 300
			if (withdrawalAmount > 300) {
				withdrawalAmount = totalCommissionAmount;
				remainingCommission = 0;
			}

			// Ensure remaining commission is not less than 1000 unless full withdrawal
			if (remainingCommission < 300 && withdrawalAmount < totalCommissionAmount) {
				System.out.println("Withdrawal not allowed. Remaining amount is less than 300.");
				return ResponseEntity.badRequest().body("Withdrawal not allowed. Remaining amount is less than 300.");
			}

			// Update the user's commission amount
			user.setTotalCommissionAmount(remainingCommission);
			uRepo.save(user); // Save the updated user details

			// Create and save the withdrawal request
			WithdrawalRequest newWithdrawalRequest = new WithdrawalRequest();
			newWithdrawalRequest.setUser(user);
			newWithdrawalRequest.setAmount(withdrawalAmount);
			newWithdrawalRequest.setRemainingAmount(remainingCommission);
			newWithdrawalRequest.setStatus("pending");
			newWithdrawalRequest.setProcessed(false); // Assuming withdrawal is not processed yet
			newWithdrawalRequest.setRequestDate(new Date()); // Set the current date
			newWithdrawalRequest.setRole(user.getRole());
			newWithdrawalRequest.setUsername(user.getUsername());

			wRepo.save(newWithdrawalRequest); // Save the withdrawal request to the database

			System.out.println("Withdrawal successful!");
			return ResponseEntity.ok("Withdrawal successful!");
		} else {
			System.out.println("Withdrawal not allowed. Insufficient commission amount.");
			return ResponseEntity.badRequest().body("Withdrawal not allowed. Insufficient commission amount.");
		}
	}

	@PostMapping("/uploadImage")
	public ResponseEntity<String> uploadImage(@RequestParam("userId") int userId,
			@RequestParam("photo") MultipartFile[] photos) {
		try {
			if (photos == null || photos.length == 0) {
				return ResponseEntity.badRequest().body("Please upload an image file");
			}

			if (photos.length > 1) {
				return ResponseEntity.badRequest().body("Only one image file can be uploaded at a time");
			}

			MultipartFile photo = photos[0];
			if (photo.isEmpty()) {
				return ResponseEntity.badRequest().body("Please upload an image file");
			}

			uService.saveImage(userId, photo);
			return ResponseEntity.ok().body("Image uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
		}
	}

	@PutMapping("/updatepassword")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody) {
		// Extract user ID from the request body and validate
		String userIdStr = requestBody.get("userid");
		if (userIdStr == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is required.");
		}

		Integer userId;
		try {
			userId = Integer.parseInt(userIdStr);
		} catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user ID format.");
		}

		// Extract other fields from the request body
		String currentPassword = requestBody.get("password");
		String newPassword = requestBody.get("np");
		String confirmPassword = requestBody.get("cnp");

		// Check if the user exists in the repository
		Optional<User> userOptional = uRepo.findById(userId);
		if (userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}

		// Retrieve the user object from the Optional
		User user = userOptional.get();

		try {
			// Attempt to change the password
			uService.changePassword(currentPassword, newPassword, confirmPassword, user);
			return ResponseEntity.ok("Password updated successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

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

	@GetMapping("/managedUsers")
    public ResponseEntity<Map<String, Object>> getUsersManagedByUser(@RequestBody Map<String, Object> user) {
        try {
            if (user == null || user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Request body is empty"));
            }

            if (user.containsKey("userId")) {
                Object userIdObj = user.get("userId");
                if (userIdObj instanceof Integer) {
                    int userId = (Integer) userIdObj;
                    List<Map<String, Object>> managedUsers = uService.getMyTeam(userId);

                    if (managedUsers.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(Map.of("managedUsers", managedUsers));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid userId type"));
                }
            }

            if (user.containsKey("role")) {
                Object roleObj = user.get("role");
                if (roleObj instanceof String) {
                    String role = (String) roleObj;
                    List<Map<String, Object>> usersByRole = uService.getUsersByRole(role);

                    if (usersByRole.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(Map.of("usersByRole", usersByRole));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid role type"));
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Neither userId nor role provided"));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Type mismatch in request body"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

	private boolean isHigherLevelRole(User user) {
		Set<String> higherRoles = Set.of("COUNTRYHEAD", "STATEHEAD", "DISTRICTHEAD", "CITYHEAD");
		return higherRoles.contains(user.getRole());
	}

//	@GetMapping("Products")
//	public ResponseEntity<?> products(@RequestParam(required = false) Integer ptid,
//			@RequestParam(required = false) Integer pcid, @RequestParam(required = false) Integer pid,
//			HttpSession session) {
//		Integer userid1 = (Integer) session.getAttribute("userID");
//		if (ptid == null && pcid == null && pid == null && userid1 != null) {
//			List<Product> product = pService.findallproduct();
//			if (product != null && !product.isEmpty()) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
//			}
//		} else if (pid != null && userid1 != null) {
//			Product product = pService.findbyid(pid);
//			if (product != null) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
//			}
//		} else if (ptid != null && userid1 != null) {
//			ProductType PT = new ProductType();
//			PT.setPtid(ptid);
//			List<Product> product = pService.getpbyptid(PT);
//			if (product != null && !product.isEmpty()) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND)
//						.body("No products found for the given product type.");
//			}
//		} else if (pcid != null && userid1 != null) {
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
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters. Or user Logged Out");
//	}
	@PostMapping("Products")
	public ResponseEntity<?> getMethodName(@RequestBody(required = false) Map<String, Object> P, HttpSession session) {
		Integer adminId = (Integer) session.getAttribute("userID");

//		--changes are here ...
		if (P == null || P.isEmpty()) {
			// Fetch all products
			List<Product> products = pService.findallproduct();
			if (!products.isEmpty()) {

				// reverse list
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
//		if (ptid == null && pcid == null && pid == null && adminId != null && P==null||P.isEmpty()) {
//			List<Product> product = pService.findallproduct();
//			if (product != null && !product.isEmpty()) {
//				return ResponseEntity.ok(product);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
//			}
//		} 
		if (pid != null && adminId != null) {
			Product product = pService.findbyid(pid);
			System.out.println(pid);
			if (product != null) {

				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}
		} else if (ptid != null && adminId != null) {
			ProductType PT = new ProductType();
			PT.setPtid(ptid);
			List<Product> product = pService.getpbyptid(PT);
			if (product != null && !product.isEmpty()) {
				// reverse list
				Collections.reverse(product);
				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No products found for the given product type.");
			}
		} else if (pcid != null && adminId != null) {
			ProductCategory pc = new ProductCategory();
			pc.setPcid(pcid);
			List<Product> product = pService.getpbypcid(pc);
			if (product != null && !product.isEmpty()) {

				Collections.reverse(product);
				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No products found for the given product category.");
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters.");
	}

	@PostMapping("/registersell")
	public ResponseEntity<?> registerSell(HttpSession session, @RequestBody Map<String, Object> sell) {
	    Integer userId = (Integer) sell.get("userId");
	    if (userId != null) {
	        // Retrieve the user from the database
	        User user = uService.getUserById(userId);
	        if (user != null && !user.isUsDisabled()) {
	            // User is not disabled, proceed with registering the sell
	        	Map<String, Integer> result = sellService.sell(sell, userId);
	            // Create a response map
	            Map<String, Object> response = new HashMap<>();
	            response.put("message", "Sell created successfully");
	            response.put("cid", result.get("cid"));
	            response.put("sellid", result.get("sellid"));
	            
	            // Return the response map with HttpStatus.CREATED
	            return ResponseEntity.status(HttpStatus.CREATED).body(response);
	        } else {
	            // User is disabled, return error response
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is disabled, cannot register sell");
	        }
	    }
	    // If userId is null, return error response
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register sell");
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

	


	@PostMapping("/KYCpending")
	public ResponseEntity<?> KYCpending(HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userID");
		if (userId != null) {
			List<User> myTeam = uService.getmyteam(userId);
			List<Map<String, Object>> listOfPendingKYC = new ArrayList<>();
			for (User user : myTeam) {
				if (user.getPAN() != null && !user.getPAN().isEmpty() && user.getAADHAR() != null
						&& !user.getAADHAR().isEmpty()) {
					// If both PAN and Aadhar are not blank, set KYC status to done (1)
					user.setIsKYCDone(true);
					uService.updateUser(user); // Update user in the database
				} else {
					// If either PAN or Aadhar is blank, add to the list of pending KYC
					Map<String, Object> memberData = new HashMap<>();
					memberData.put("Pan", user.getPAN());
					memberData.put("Aadhar", user.getAADHAR());
					listOfPendingKYC.add(memberData);
				}
			}

			return ResponseEntity.status(HttpStatus.OK).body(listOfPendingKYC);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User logged out");
	}


	@PutMapping("/updateUsers")
	public ResponseEntity<Map<String, Object>> updateUser(@RequestParam int Userid,
	        @RequestParam(value = "cityorvillage", required = false) String cityorvillage,
	        @RequestParam(value = "district", required = false) String district,
	        @RequestParam(value = "gender", required = false) String gender,
	        @RequestParam(value = "state", required = false) String state,
	        @RequestParam(value = "dob", required = false) String dobStr,
	        @RequestParam(value = "Useraddress", required = false) String Useraddress,
	        @RequestParam(value = "Useremail", required = false) String Useremail,
	        @RequestPart(value = "UserProfilePhoto", required = false) MultipartFile UserProfilePhoto)
	        throws ParseException {

	    User existingUser = uService.getUserById(Userid);
	    if (existingUser == null) {
	        return ResponseEntity.notFound().build();
	    }

	    Map<String, Object> updateUserFields = new HashMap<>();
	    updateUserFields.put("Userid", Userid);
	    if (cityorvillage != null)
	        updateUserFields.put("cityorvillage", cityorvillage);
	    if (district != null)
	        updateUserFields.put("district", district);
	    if (gender != null)
	        updateUserFields.put("gender", gender);
	    if (state != null)
	        updateUserFields.put("state", state);
	    if (dobStr != null) {
	        try {
	            java.sql.Date dob = new java.sql.Date(DateUtil.stringToDate(dobStr, "yyyy-MM-dd").getTime());
	            updateUserFields.put("dob", dob);
	        } catch (ParseException e) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format. Use yyyy-MM-dd."));
	        }
	    }
	    if (Useraddress != null)
	        updateUserFields.put("Useraddress", Useraddress);
	    if (Useremail != null)
	        updateUserFields.put("Useremail", Useremail); // Added Useremail update

	    User updatedUser = uService.updateUserFields(existingUser, updateUserFields, UserProfilePhoto);

	    Map<String, Object> response = new HashMap<>();
	    response.put("Userid", updatedUser.getUserid());
	    response.put("cityorvillage", updatedUser.getCityorvillage());
	    response.put("district", updatedUser.getDistrict());
	    response.put("gender", updatedUser.getGender());
	    response.put("state", updatedUser.getState());
	    response.put("dob", DateUtil.dateToString(updatedUser.getDob(), "yyyy-MM-dd"));
	    response.put("Useraddress", updatedUser.getUseraddress());
	    response.put("Useremail", updatedUser.getUseremail()); // Added Useremail to response

	    return ResponseEntity.ok(response);
	}


	private String saveFile(MultipartFile file, String fileType) {
		try {
			String originalFilename = file.getOriginalFilename();
			String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
			String newFilename = fileType + fileExtension;
			Path path = Paths.get(upload_dir, newFilename); // Replace with your directory
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			return newFilename;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file", e);
		}
	}


	@PostMapping("/client-details")
	public ResponseEntity<List<Map<String, Object>>> getCustomerDataForAgent(@RequestBody Map<String, Integer> request)
			throws ParseException {
		Integer userId = request.get("userid");
//	    Integer page = request.get("page");
//	    Integer record = request.get("record");

		if (userId == null) {
			return ResponseEntity.badRequest().body(null);
		}

		try {
			List<Map<String, Object>> customerData = uService.getClientMonitoringData(userId);
			Collections.reverse(customerData);
			return ResponseEntity.ok(customerData);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}


	@PutMapping("/updateKYC")
	public ResponseEntity<Map<String, Object>> updateKYC(@RequestParam("panFile") MultipartFile panFile,
			@RequestParam("aadharFile") MultipartFile aadharFile, @RequestParam("userid") int userid) {
		User updateUser = uService.getUserById(userid);
		if (updateUser == null) {
			return ResponseEntity.notFound().build();
		}
		// Pass the UserService instance to the updateKYCWithFiles method
		Map<String, Object> responseMap = uService.updateKYCWithFiles(panFile, aadharFile, updateUser, uService);

		return ResponseEntity.ok(responseMap);
	}

	@PutMapping("/updateBankDetails")
	public ResponseEntity<User> updateBankDetails(@RequestBody User updateUser) {
		User existingUser = uService.getUserById(updateUser.getUserid());
		if (existingUser == null) {
			return ResponseEntity.notFound().build();
		}

		User updatedUser = uService.updateBankDetails(existingUser, updateUser);
		return ResponseEntity.ok(updatedUser);
	}

	@PostMapping("/overview")
	public ResponseEntity<List<User>> getUserOverview() {
		// Query users with roles City Head and District Head
		List<User> users = uService.getUsersByRoles("SUBAGENT");

		if (users.isEmpty()) {
			return ResponseEntity.noContent().build(); // No users found
		}

		return ResponseEntity.ok(users);
	}

	@PostMapping("/salesHistory")
	public ResponseEntity<Map<String, Object>> getSalesHistory() {
		Map<String, Object> salesHistory = new HashMap<>();

		// Get the last sale
		Sell lastSale = sellService.getLastSale();
		salesHistory.put("lastSale", lastSale);

		// Get all sales history
		List<Sell> allSalesHistory = sellService.getAllSalesHistory();
		salesHistory.put("allSalesHistory", allSalesHistory);

		return ResponseEntity.ok(salesHistory);
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


	@PostMapping("/counts")
	public Map<String, Double> getCountsForUser(@RequestBody Map<String, Integer> requestBody) {
		int userId = requestBody.get("userId");
		User user = uRepo.findByUserid(userId);

		if (userId == 0) {
			throw new IllegalArgumentException("userId cannot be null");
		}

		Map<String, Double> response = new HashMap<>();

		response.putAll(uService.groupAndCalculateSales(userId));

		double userCount = uService.getUserCountByManager(userId);
		response.put("userCount", userCount);

		double clientcount = sellService.getclient(userId);
		response.put("clients", clientcount);

		response.putAll(uService.getproductcount());

		response.put("TotalCommission", Double.parseDouble(formatDouble(user.getTotalCommissionAmount())));

		// provide list of product which is sell by user
		double soldProducts = uService.getProductsSoldByUser(userId);
		response.put("soldProducts", soldProducts);

		return response;
	}

	public static String formatDouble(double value) {
	        return String.format("%.2f", value);
	    }

	@PostMapping("/hierarchy")
	public ResponseEntity<?> getSalesData(@RequestBody Map<String, Object> request) {

		try {
			Object userIdObj = request.get("userid");

			if (userIdObj == null) {
				return ResponseEntity.badRequest()
						.body(Collections.singletonMap("error", "User ID is required in the request body."));
			}

			int userId = Integer.parseInt(userIdObj.toString());

			// Retrieve the user to determine their role
			User user = uRepo.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

			List<Map<String, Object>> response = new ArrayList<>();

			// Check if the user is a higher-level role
			if (isHigherLevelRole(user)) {
				List<Sell> salesData = uService.getSalesDataForUserHierarchy(userId);

				if (salesData.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
							Collections.singletonMap("message", "No sales data found for the given user hierarchy."));
				}

				for (Sell sell : salesData) {
					Map<String, Object> commissionDetails = uService.populateCommissionDetails(sell, user);
					response.add(commissionDetails);
				}

			} else {
				// If user is an agent or subagent, get their specific sales data
				List<Sell> salesData = uService.getSalesByUserId(userId);

				if (salesData.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT)
							.body(Collections.singletonMap("message", "No sales data found for the given user."));
				}

				for (Sell sell : salesData) {
					Map<String, Object> commissionDetails = uService.populateCommissionDetails(sell);
					response.add(commissionDetails);
				}
			}

			Collections.reverse(response);
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "An unexpected error occurred while fetching sales data."));
		}
	}


	
	@PostMapping("/target")
	public ResponseEntity<Map<String, Object>> assignYearlyTargetToAgents(HttpServletRequest request) {
	    // Get the logged-in user's ID from the request attributes
	    Integer loggedInUserId = (Integer) request.getAttribute("userId");

	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Call the service method to assign yearly target to agents and get the target per agent
	        double targetPerAgent = uService.assignYearlyTargetToAgents(loggedInUserId);
	        String responseMessage = "Monthly target assigned to Subagents successfully. Target per Subagent: " + targetPerAgent;

	        // Add the response data to the map
	        response.put("status", HttpStatus.OK.value());
	        response.put("message", responseMessage);
	        response.put("targetperSubagent", targetPerAgent);
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    } catch (RuntimeException e) {
	        // Add the error response data to the map
	        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@PostMapping("getteamtarget")
    public ResponseEntity<?> getMyTeamtarget(HttpServletRequest request) {
        Integer loggedInUserId = (Integer) request.getAttribute("userId");
        return uService.getMyTeamtarget(loggedInUserId);
    }

	
	@PostMapping("myteam")
	public ResponseEntity<?> getMyTeam(@RequestBody User user, HttpServletRequest request) {
		Integer userid = user.getUserid();
		Integer userid1 = (Integer) request.getAttribute("userId");

		if (userid1 != null && userid1.equals(userid)) {
			List<Map<String, Object>> teamInfo = uService.getMyTeam(userid1);
			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}
	
	@PostMapping("/usersforsettarget")
    public ResponseEntity<Map<String, Object>> getUsersManagedByUser1(@RequestBody Map<String, Object> user) {
        try {
            if (user == null || user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Request body is empty"));
            }

            if (user.containsKey("userId")) {
                Object userIdObj = user.get("userId");
                if (userIdObj instanceof Integer) {
                    int userId = (Integer) userIdObj;
                    Map<String, Object> managedUsers = uService.getMyTeamforsettarget(userId);

                    if (managedUsers.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(managedUsers);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid userId type"));
                }
            }

            if (user.containsKey("role")) {
                Object roleObj = user.get("role");
                if (roleObj instanceof String) {
                    String role = (String) roleObj;
                    List<Map<String, Object>> usersByRole = uService.getUsersByRole(role);

                    if (usersByRole.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(Map.of("usersByRole", usersByRole));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid role type"));
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Neither userId nor role provided"));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Type mismatch in request body"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
}
