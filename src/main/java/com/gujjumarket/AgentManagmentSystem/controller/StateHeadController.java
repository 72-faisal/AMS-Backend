package com.gujjumarket.AgentManagmentSystem.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.gujjumarket.AgentManagmentSystem.model.SecurityQuestions;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.model.WithdrawalRequest;
import com.gujjumarket.AgentManagmentSystem.repo.SecurityQuestionRepo;
import com.gujjumarket.AgentManagmentSystem.repo.StateHeadRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.service.SellService;
import com.gujjumarket.AgentManagmentSystem.service.StateHeadService;
import com.gujjumarket.AgentManagmentSystem.service.UserService;
import com.gujjumarket.AgentManagmentSystem.service.withdrawalService;
import com.gujjumarket.AgentManagmentSystem.utils.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("STATEHEAD")
public class StateHeadController {

	@Autowired
	StateHeadService shService;
	@Autowired
	SecurityQuestionRepo sqRepo;
	@Autowired
	UserService uService;
	@Autowired
	StateHeadRepo shRepo;
	@Autowired
	Userrepo uRepo;
	@Autowired
	withdrawalRequestRepo wRepo;
	@Autowired
	SellService sellService;
	@Autowired
	withdrawalService wservice;
	
	@Value("${upload-dir}")
	private String upload_dir;

//	@GetMapping("profile/{userid}")
//	public ResponseEntity<?> profile(HttpSession session, @PathVariable Integer userid) {
//		Integer userid1 = (Integer) session.getAttribute("userID");
//		System.out.println(userid1);
//		if (userid1 != null && userid1 == userid) {
//			User u = shService.getprofile(userid1);
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(u);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out OR invalid credentials");
//	}

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
//                User u = uService.getprofile(userId1);
//                if (u != null) {
//                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(u);
//                } else {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
//                }
//            }
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
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7); // Extract the JWT from the header
            JWT.blacklistToken(token); // Blacklist the token
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing token");
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
//	@GetMapping("changepassword/{userid}")
//	public ResponseEntity<?> passwordpage(HttpSession session, @PathVariable Integer userid) {
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		User u = shService.getprofile(userid2);
//		if (u.isFirstTimeLogin() == true) {
//			List<SecurityQuestions> Questions = sqRepo.findAll();
//			return ResponseEntity.status(HttpStatus.OK).body(Questions);
//		}
//		return ResponseEntity.status(HttpStatus.OK).body("fetch the page");
//	}
//	@PostMapping("changepassword")
//	public ResponseEntity<?> passwordpage(HttpSession session, @RequestParam Integer userid) {
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		User u = shService.getprofile(userid2);
//		if (u.isFirstTimeLogin() == true) {
//			List<SecurityQuestions> Questions = sqRepo.findAll();
//			return ResponseEntity.status(HttpStatus.OK).body(Questions);
//		}
//		return ResponseEntity.status(HttpStatus.OK).body("fetch the page");
//	}
	@PostMapping("changepassword")
	public ResponseEntity<?> passwordPage(HttpSession session, @RequestBody User user) {
	    Integer userId = (Integer) session.getAttribute("userID");
	    User loggedInUser = shService.getprofile(userId);
	    
	    if (loggedInUser != null && loggedInUser.getUserid() != 0) {
	        if (loggedInUser.isFirstTimeLogin()) {
	            List<SecurityQuestions> questions = sqRepo.findAll();
	            return ResponseEntity.status(HttpStatus.OK).body(questions);
	        }
	        return ResponseEntity.status(HttpStatus.OK).body("Fetch the page");
	    }
	    
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User logged out or invalid credentials.");
	}
//	@PutMapping("changepassword/{userid}")
//	public ResponseEntity<?> changepassword(HttpSession session, @PathVariable Integer userid,
//			@RequestBody Map<String, Object> changepassword) {
//
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		User u = shService.getprofile(userid2);
//
//		String password = (String) changepassword.get("password");
//		String np = (String) changepassword.get("np");
//		String cnp = (String) changepassword.get("cnp");
//		String securityanswer1 = (String) changepassword.get("securityanswer1");
//		String securityanswer2 = (String) changepassword.get("securityanswer2");
//		String question1 = (String) changepassword.get("question1");
//		String question2 = (String) changepassword.get("question2");
//
//		if (u != null && u.isFirstTimeLogin() == true) {
//			System.out.println("in controller if block");
//			User u1 = shService.changepasswordFirsttime(u, np, cnp, question1, question2, securityanswer1,
//					securityanswer2, password);
//			System.out.println("after service method");
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(u1);
//		} else if (u.isFirstTimeLogin() == false) {
//			User u1 = shService.changepassword(password, np, cnp, u);
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(u1);
//		}
//		return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Update Password.");
//	}
//
//	@PutMapping("changepassword")
//	public ResponseEntity<?> changepassword(HttpSession session, @RequestBody Map<String, Object> changepassword) {
//
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		User u = shService.getprofile(userid2);
//
//		String password = (String) changepassword.get("password");
//		String np = (String) changepassword.get("np");
//		String cnp = (String) changepassword.get("cnp");
//		String securityanswer1 = (String) changepassword.get("securityanswer1");
//		String securityanswer2 = (String) changepassword.get("securityanswer2");
//		String question1 = (String) changepassword.get("question1");
//		String question2 = (String) changepassword.get("question2");
//
//		if (u != null && u.isFirstTimeLogin() == true) {
//			System.out.println("in controller if block");
//			User u1 = shService.changepasswordFirsttime(u, np, cnp, question1, question2, securityanswer1,
//					securityanswer2, password);
//			System.out.println("after service method");
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(u1);
//		} else if (u.isFirstTimeLogin() == false) {
//			User u1 = shService.changepassword(password, np, cnp, u);
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body(u1);
//		}
//		return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Update Password.");
//	}
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

//	@PostMapping("/hierarchy")
//	public ResponseEntity<?> getSalesData(@RequestBody Map<String, Object> request) {
//	    try {
//	        Object userIdObj = request.get("Userid");
//	        if (userIdObj == null) {
//	            return ResponseEntity.badRequest()
//	                    .body(Collections.singletonMap("error", "User ID is required in the request body."));
//	        }
//
//	        int userId = Integer.parseInt(userIdObj.toString());
//
//	        // Retrieve the user to determine their role
//	        User user = uRepo.findById(userId)
//	                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
//
//	        List<Map<String, Object>> response = new ArrayList<>();
//
//	        // Check if the user is a higher-level role
//	        if (isHigherLevelRole(user)) {
//	            List<Sell> salesData = uService.getSalesDataForUserHierarchy(userId);
//
//	            if (salesData.isEmpty()) {
//	                return ResponseEntity.status(HttpStatus.NO_CONTENT)
//	                        .body(Collections.singletonMap("message", "No sales data found for the given user hierarchy."));
//	            }
//
//	            for (Sell sell : salesData) {
//	                response.add(uService.populateCommissionDetails(sell, user));
//	            }
//
//	        } else {
//	            // If user is an agent or subagent, get their specific sales data
//	            List<Sell> salesData = uService.getSalesByUserId(userId);
//
//	            if (salesData.isEmpty()) {
//	                return ResponseEntity.status(HttpStatus.NO_CONTENT)
//	                        .body(Collections.singletonMap("message", "No sales data found for the given user."));
//	            }
//
//	            for (Sell sell : salesData) {
//	                response.add(uService.populateCommissionDetails(sell));
//	            }
//	        }
//
//	        return ResponseEntity.ok(response);
//
//	    } catch (IllegalArgumentException ex) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//	                .body(Collections.singletonMap("error", ex.getMessage()));
//	    } catch (Exception ex) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.singletonMap("error", "An unexpected error occurred while fetching sales data."));
//	    }
//	}
	
	 @PostMapping("/hierarchy")
	    public ResponseEntity<?> getSalesData(@RequestBody Map<String, Object> request) {
	     
		 int i=1;
		 
		 try {
	            Object userIdObj = request.get("userid");
	            if (userIdObj == null) {
	                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User ID is required in the request body."));
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
	                   
	                	System.out.println(i);
	                	i++;
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

	    private boolean isHigherLevelRole(User user) {
	        Set<String> higherRoles = Set.of("COUNTRYHEAD", "STATEHEAD", "DISTRICTHEAD", "CITYHEAD");
	        return higherRoles.contains(user.getRole());
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

//	@GetMapping("myteam/{userid}")
//	public ResponseEntity<?> getMyTeam(@PathVariable Integer userid,HttpSession session) {
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		if (userid2 != null && userid2 == userid) {
//			List<User> myTeam = uService.getmyteam(userid2);
//			List<Map<String, Object>> listOfMember = new ArrayList<>();
//			for (User user : myTeam) {
//				Map<String, Object> memberData = new HashMap<String, Object>();
//				memberData.put("name", user.getUsername());
//				memberData.put("mobile", user.getUsermobile());
//				memberData.put("total earning", user.getTotalCommissionAmount());
//				listOfMember.add(memberData);
//			}
//			
//			return ResponseEntity.status(HttpStatus.OK).body(listOfMember);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
//	}
//	@GetMapping("myteam")
//	public ResponseEntity<?> getMyTeam(@RequestParam Integer userid,HttpSession session) {
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		if (userid2 != null && userid2 == userid) {
//			List<User> myTeam = uService.getmyteam(userid2);
//			List<Map<String, Object>> listOfMember = new ArrayList<>();
//			for (User user : myTeam) {
//				Map<String, Object> memberData = new HashMap<String, Object>();
//				memberData.put("name", user.getUsername());
//				memberData.put("mobile", user.getUsermobile());
//				memberData.put("total earning", user.getTotalCommissionAmount());
//				listOfMember.add(memberData);
//			}
//			
//			return ResponseEntity.status(HttpStatus.OK).body(listOfMember);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
//	}
//	public List<Map<String, Object>> getMyTeam(int userId) {
//	    List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
//	    List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//	    for (User user : myTeam) {
//	        Map<String, Object> memberAndWithdrawalData = new HashMap<>();
//
//	        // Add member data to the combined map
//	        memberAndWithdrawalData.put("name", user.getUsername());
//	        memberAndWithdrawalData.put("mobile", user.getUsermobile());
//	        memberAndWithdrawalData.put("totalEarning", user.getTotalCommissionAmount());
//
//	        List<WithdrawalRequest> reqList = wRepo.findAllByUser(user);
//
//	        // Create a list to hold withdrawal data for the user
//	        List<Map<String, Object>> withdrawalDataList = new ArrayList<>();
//
//	        for (WithdrawalRequest req : reqList) {
//	            // Create a new map for each withdrawal request
//	            Map<String, Object> withdrawalData = new HashMap<>();
//	            withdrawalData.put("remainingAmount", req.getRemainingAmount());
//	            withdrawalData.put("withdrawalAmount", req.getAmount());
//
//	            // Add withdrawalData to the list of withdrawal data
//	            withdrawalDataList.add(withdrawalData);
//	        }
//
//	        // Add the list of withdrawal data to the combined map
//	        memberAndWithdrawalData.put("withdrawalData", withdrawalDataList);
//
//	        // Add the combined data for the user to the list of members
//	        listOfMember.add(memberAndWithdrawalData);
//	    }
//
//	    return listOfMember;
//	}

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
	
//	@PostMapping("myteam")
//	public ResponseEntity<?> getMyTeam(@RequestBody User user, HttpSession session) {
//		Integer userId = user.getUserid();
//		Integer loggedInUserId = (Integer) session.getAttribute("userID");
//		String username = user.getUsername();
//
//		if (loggedInUserId != null && loggedInUserId.equals(userId)) {
//			List<Map<String, Object>> teamInfo = uService.getMyTeam(userId, username);
//			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
//	}

//	@GetMapping("KYCpending/{userid}")
//	public ResponseEntity<?> KYCpending (@PathVariable Integer userid,HttpSession session){
//		Integer userid2 = (Integer) session.getAttribute("userID");
//		if (userid2 != null && userid2 == userid) {
//			List<User> myTeam = uService.getmyteam(userid2);
//			List<Map<String, Object>> listOfMember = new ArrayList<>();
//			for (User user : myTeam) {
//				if (user.isKYCdone() == false) {
//					Map<String, Object> memberData = new HashMap<String, Object>();
//					memberData.put("Pan", user.getPAN());
//					memberData.put("Aadhar", user.getAADHAR());
//					listOfMember.add(memberData);
//				}
//				
//			}
//			return ResponseEntity.status(HttpStatus.OK).body(listOfMember);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
//	}
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

//	@PutMapping("/updateUsers")
//	public ResponseEntity<User> updateUser(@RequestBody User updateUser) {
//		User existingUser = uService.getUserById(updateUser.getUserid());
//		if (existingUser == null) {
//			return ResponseEntity.notFound().build();
//		}
//
//		User updatedUser = uService.updateUserFields(existingUser, updateUser);
//		return ResponseEntity.ok(updatedUser);
//	}
	
//	@PutMapping("/updateUsers")
//    public ResponseEntity<User> updateUser(
//            @RequestParam int Userid,
//            @RequestParam(value = "cityorvillage", required = false) String cityorvillage,
//            @RequestParam(value = "district", required = false) String district,
//            
//            @RequestParam(value = "gender", required = false) String gender,
//            @RequestParam(value = "state", required = false) String state,
//            
//            @RequestParam(value = "dob", required = false) java.sql.Date dob,
//            @RequestParam(value = "Useraddress", required = false) String Useraddress,
////            @RequestParam(value = "ManageBy", required = false) Integer ManageBy,
//            @RequestPart(value = "UserProFilePhoto", required = false) MultipartFile UserProfilePhoto) {
//
//		User existingUser = uService.getUserById(Userid);
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        User updateUser = new User();
//        updateUser.setUserid(Userid);
//        updateUser.setCityorvillage(cityorvillage);
//        updateUser.setDistrict(district);
//        updateUser.setState(state);
////        updateUser.setRole(role);
////        updateUser.setUsermobile(Usermobile);
//        updateUser.setDob(dob);
//        updateUser.setUseraddress(Useraddress);
//        updateUser.setGender(gender);
////        updateUser.setManageBy(ManageBy);
//
//        User updatedUser = uService.updateUserFields(existingUser, updateUser, UserProfilePhoto);
//        return ResponseEntity.ok(updatedUser);
//    }
	
//	@PutMapping("/updateUsers")
//    public ResponseEntity<User> updateUser(
//            @RequestBody User user,
//            @RequestPart(value = "UserProfilePhoto", required = false) MultipartFile UserProfilePhoto) {
//
//		System.out.println(user.getUserid());
//        User existingUser = uService.getUserById(user.getUserid());
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        if (user.getCityorvillage() != null) {
//            existingUser.setCityorvillage(user.getCityorvillage());
//        }
//        if (user.getDistrict() != null) {
//            existingUser.setDistrict(user.getDistrict());
//        }
//        if (user.getState() != null) {
//            existingUser.setState(user.getState());
//        }
//        if (user.getDob() != null) {
//            existingUser.setDob(user.getDob());
//        }
//        if (user.getUseraddress() != null) {
//            existingUser.setUseraddress(user.getUseraddress());
//        }
//        if (user.getGender() != null) {
//            existingUser.setGender(user.getGender());
//        }
//        if(user.getUseremail()!= null) {
//        	existingUser.setUseremail(user.getUseremail());
//        }
//        if (UserProfilePhoto != null && !UserProfilePhoto.isEmpty()) {
//            // Handle the file upload here, e.g., save it to a directory or a database
//            // existingUser.setUserProfilePhoto(UserProfilePhoto.getBytes()); // If you want to save the photo bytes directly
//        }
//
//        User updatedUser = uService.updateUser(existingUser);
//        return ResponseEntity.ok(updatedUser);
//    }
	
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

//	@PutMapping("/updateKYC")
//	public ResponseEntity<User> updateKYC(@RequestBody User updateUser) {
//		User existingUser = uService.getUserById(updateUser.getUserid());
//		if (existingUser == null) {
//			return ResponseEntity.notFound().build();
//		}
//
//		User updatedUser = uService.updateKYCFields(existingUser, updateUser);
//		return ResponseEntity.ok(updatedUser);
//	}
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
		List<User> users = uService.getUsersByRoles("CITYHEAD", "DISTRICTHEAD");

		if (users.isEmpty()) {
			return ResponseEntity.noContent().build(); // No users found
		}

		return ResponseEntity.ok(users);
	}

//	@PostMapping("/withdraw")
//	public ResponseEntity<String> withdraw(@RequestBody WithdrawalRequest withdrawalRequest) {
//		// Retrieve the user from the database based on the provided user ID
//		if (withdrawalRequest == null || withdrawalRequest.getUser() == null || withdrawalRequest.getAmount() <= 0) {
//			return ResponseEntity.badRequest().body("Invalid withdrawal request.");
//		}
//		User user = shRepo.findById(withdrawalRequest.getUser().getUserid()).orElse(null);
//
//		if (user == null) {
//			System.out.println("User not found");
//			return ResponseEntity.badRequest().body("User not found");
//		} else if (wservice.canWithdraw(user, withdrawalRequest.getAmount())) {
//
//			double withdrawalAmount = withdrawalRequest.getAmount();
//			System.out.println(user.getTotalCommissionAmount());
//			double remainingCommission = user.getTotalCommissionAmount() - withdrawalAmount;
//			if (remainingCommission < 1000) {
//				System.out.println("Withdrawal not allowed. Remaining amount is less than 1000.");
//				return ResponseEntity.badRequest().body("Withdrawal not allowed. Remaining amount is less than 1000.");
//			}
//			user.setTotalCommissionAmount(remainingCommission);
//			uRepo.save(user); // Save the updated user details
//			WithdrawalRequest newWithdrawalRequest = new WithdrawalRequest();
//			newWithdrawalRequest.setUser(user);
//			newWithdrawalRequest.setAmount(withdrawalRequest.getAmount());
//			newWithdrawalRequest.setRemainingAmount(remainingCommission);
//			newWithdrawalRequest.setStatus("pending");
////			newWithdrawalRequest.setTotalAmount(user.getTotalCommissionAmount());			
//			newWithdrawalRequest.setProcessed(false); // Assuming withdrawal is not processed yet
//  			newWithdrawalRequest.setRequestDate(new Date()); // Set the current date
//			newWithdrawalRequest.setRole(user.getRole());// Set the current date
//
//			wRepo.save(newWithdrawalRequest); // Save the withdrawal request to the database
//
//			System.out.println("Withdrawal successful!");
//			return ResponseEntity.ok("Withdrawal successful!");
//		} else {
//			System.out.println("Withdrawal not allowed. Insufficient commission amount.");
//			return ResponseEntity.badRequest().body("Withdrawal not allowed. Insufficient commission amount.");
//		}
//	}

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
//	@PostMapping("/{userid}/logout")
//	public ResponseEntity<String> logout(@PathVariable int userid, HttpSession session) {
//		session.invalidate();
//		return ResponseEntity.ok("Logout Successful");
//	}

//	@PostMapping("/counts")
//	public Map<String, Double> getCountsForUser(@RequestBody Map<String, Integer> requestBody) {
//	    int userId = requestBody.get("userId");
//	    User user=uRepo.findByUserid(userId);
//
//	    if (userId == 0) {
//	        throw new IllegalArgumentException("userId cannot be null");
//	    }
//
//	    Map<String, Double> response = new HashMap<>();
//	   
////	    response.putAll(uService.groupAndCalculateSales(userId));
//	    response.put("TotalCommission",user.getTotalCommissionAmount());
//	   
//	    double userCount = uService.getUserCountByManager(userId);
//        response.put("userCount", userCount);
//        
//        Map<String, Double> salesTotals = uService.getSalesTotalsForUserHierarchy(userId);
//        response.putAll(salesTotals);
//        
//      //count of product
//        response.putAll(uService.getproductcount());
//	    
//	    return response;
//	}
	
	@PostMapping("/counts")
	public Map<String, Object> getCountsForUser(@RequestBody Map<String, Integer> requestBody) {
	    int userId = requestBody.get("userId");
	    User user = uRepo.findByUserid(userId);

	    if (userId == 0) {
	        throw new IllegalArgumentException("userId cannot be null");
	    }

	    Map<String, Object> response = new HashMap<>();

	    // Number format for Indian Rupees
	    Locale indiaLocale = new Locale("en", "IN");
	    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);

	    // Add Total Commission to response
	    String totalCommission = currencyFormat.format(Double.parseDouble(formatDouble(user.getTotalCommissionAmount())));
	    response.put("TotalCommission", totalCommission);

	    // Get user count and add to response
	    double userCount = uService.getUserCountByManager(userId);
	    response.put("userCount", userCount);

	    // Get sales totals and add to response
	    Map<String, Double> salesTotals = uService.getSalesTotalsForUserHierarchy(userId);
	    salesTotals.forEach((key, value) -> response.put(key, currencyFormat.format(value)));

	    // Get product count and add to response
	    Map<String, Double> productCounts = uService.getproductcount();
	    productCounts.forEach(response::put);

	    return response;
	}
	

	public static String formatDouble(double value) {
	        return String.format("%.2f", value);
	    }

	@PostMapping("/target")
	public ResponseEntity<Map<String, Object>> updateUserYearlyTarget(@RequestBody Map<String, Object> user,
	                                                                  HttpServletRequest request) {
	    Integer loggedInUserId = (Integer) request.getAttribute("userId");

	    int userId = (int) user.get("userId");
	    double percentage = ((Number) user.get("percentage")).doubleValue();

	    Map<String, Object> result = uService.updateUserYearlyTargetForUser(userId, percentage, loggedInUserId);
	    if ("success".equals(result.get("status"))) {
	        return ResponseEntity.status(HttpStatus.OK).body(result);
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
	    }
	}

	
	@PutMapping("/remainingPercentage")
    public ResponseEntity<String> updateRemainingPercentage(HttpServletRequest request,@RequestBody User req) {
        // Extract loggedUserId from the HttpServletRequest
        Integer loggedUserId = (Integer) request.getAttribute("userId");
        User loggeduser=uRepo.findByUserid(loggedUserId);
        //changes
        Integer userid=req.getUserid();
        System.out.println(userid);

        // Retrieve the user from the repository based on loggedUserId
        User user = uRepo.findByUserid(userid);
        System.out.println(user);
        if (user == null) {
        	//changes
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userid);
        }

        // Update the remainingPercentage field
        System.out.println(user.getAssignpercentage());
        System.out.println(loggeduser.getRemainigPercentage());
        loggeduser.setRemainigPercentage(user.getAssignpercentage()+loggeduser.getRemainigPercentage());
        
        
        // Save the updated user
        uRepo.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("percentage updated successfully");
    }
	
//	@PostMapping("getteamtarget")
//	public ResponseEntity<?> getMyTeamtarget( HttpServletRequest request) {
//		
//		Integer userid1 = (Integer) request.getAttribute("userId");
//
//		if (userid1 != null) {
//			List<Map<String, Object>> teamInfo = uService.getMyTeamtarget(userid1);
//			Collections.reverse(teamInfo);
//			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
//		}
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
//	}
	
	@PostMapping("getteamtarget")
    public ResponseEntity<?> getMyTeamtarget(HttpServletRequest request) {
        Integer loggedInUserId = (Integer) request.getAttribute("userId");
        return uService.getMyTeamtarget(loggedInUserId);
    }

	
	@PostMapping("/usersforsettarget")
    public ResponseEntity<Map<String, Object>> getUsersManagedByUser(@RequestBody Map<String, Object> user) {
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
