package com.gujjumarket.AgentManagmentSystem.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gujjumarket.AgentManagmentSystem.config.DateUtil;
import com.gujjumarket.AgentManagmentSystem.model.Admin;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.model.RoleAmount;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.model.WithdrawalRequest;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.repo.RoleAmountRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.service.AdminService;
import com.gujjumarket.AgentManagmentSystem.service.PTService;
import com.gujjumarket.AgentManagmentSystem.service.ProductService;
import com.gujjumarket.AgentManagmentSystem.service.PromotionService;
import com.gujjumarket.AgentManagmentSystem.service.RoleAmountService;
import com.gujjumarket.AgentManagmentSystem.service.SellService;
import com.gujjumarket.AgentManagmentSystem.service.UserService;
import com.gujjumarket.AgentManagmentSystem.utils.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("admin")
public class AdminController {

	@Autowired
	AdminService adService;
	
	@Autowired
	AdminRepo adRepo;
	@Autowired
	PTService ptService;
	@Autowired
	UserService uService;
	@Autowired
	Userrepo uRepo;
	@Autowired
	SellService sellService;
	@Autowired
	PromotionService proService;
	@Autowired
	ProductRepo pRepo;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	RoleAmountService roleAmountService;
	
	@Autowired
	RoleAmountRepo amountRepo;
	
	@Value("${upload-dir}")
	private String upload_dir;

	@Autowired
	withdrawalRequestRepo wRepo;

//	private final String FILE_STORAGE_PATH = "C:\\Users\\vhora\\OneDrive\\Desktop\\ProData\\";
//	
	@PostMapping("create")
	public ResponseEntity<?> createAdmin(@RequestParam String name, @RequestParam String password,
			@RequestParam MultipartFile photo) {
		try {
			adService.createAdmin(name, password, photo);
			return ResponseEntity.status(HttpStatus.CREATED).body("Admin Created Succesfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Admin.");
		}
	}
	
	@PostMapping("/update-amount")
    public RoleAmount updateAmount(@RequestBody RoleAmount roleAmount) {
		
		String role=roleAmount.getRole();
		double amount=roleAmount.getAmount();
        return roleAmountService.updateAmount(role, amount);
    }
	@GetMapping("/role-amounts")
    public List<RoleAmount> getAllRoleAmounts() {
        return amountRepo.findAll() ;
    }

	
	@PostMapping("loginAdmin")
	public ResponseEntity<?> loginAdmin(@RequestBody Admin admin, HttpSession session) {
		Admin authAdmin = adService.getLogin(admin.getPhoneNo(), admin.getPassword());
		if (authAdmin.getPhoneNo().equals(admin.getPhoneNo()) && admin.getPassword().equals(authAdmin.getPassword())) {

			String jwtToken = JWT.generateToken(authAdmin.getAdminid(), authAdmin.getRole());
			session.setAttribute("userID", authAdmin.getAdminid());
//			System.out.println("session is created " + session.getAttribute("userID"));
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("token", jwtToken);
			response.put("role", authAdmin.getRole());
			response.put("userID", authAdmin.getAdminid());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid credentials");
	}
	
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody, HttpSession session) {
		String currentPassword = requestBody.get("current_password"); // Correcting the field name
		String newPassword = requestBody.get("new_password");
		String confirmPassword = requestBody.get("confirm_password");

		Integer adminId = (Integer) session.getAttribute("userID");
		

		if (adminId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
		}

		Optional<Admin> userOptional = adRepo.findById(adminId);

		if (userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}

		Admin admin = userOptional.get();

		try {
			adService.changePassword(currentPassword, newPassword, confirmPassword, admin);
			return ResponseEntity.ok("Password updated successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping("newpassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String phoneNo = request.get("phoneNo");
        String newPassword = request.get("np");
        String confirmNewPassword = request.get("cnp");

        // Check if user exists
        Admin admin = adService.getByPhoneNo(phoneNo);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }

        // Save the new password
        adService.savePassword(admin, newPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully");
    }
	

	
	
	@PostMapping("/profile")
	public ResponseEntity<?> profile(HttpServletRequest request, @RequestHeader("Authorization") String tokenHeader) {
//		Integer userid1 = (Integer) request.getAttribute("userID");
		if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			String token = tokenHeader.substring(7);
			Jws<Claims> claims = Jwts.parser().verifyWith(JWT.getJwtsecret()).build().parseSignedClaims(token);

			Integer userId1 = claims.getPayload().get("userId", Integer.class);
		if (userId1 != null) {
			Optional<Admin> admin = adRepo.findById(userId1);
			if (admin.isPresent()) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(admin.get());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
			}
		}
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
	}

	@PutMapping("/updateprofile")
	public ResponseEntity<?> updateProfile(HttpSession session, @RequestParam String name,
			@RequestParam MultipartFile photo) {
		Integer adminId = (Integer) session.getAttribute("userID");
		if (adminId != null) {
			adService.updateAdmin(name, photo, adminId);
			return ResponseEntity.status(HttpStatus.OK).body("Profile updated successfully");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
	}

	@PostMapping("/uploadImage")
	public ResponseEntity<String> uploadImage(@RequestParam("adminId") int adminId,
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

			adService.saveImage(adminId, photo);
			return ResponseEntity.ok().body("Image uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
		}
	}

	@CrossOrigin
	@PutMapping("/updatepassword")
	public ResponseEntity<?> updatePassword(@RequestBody Map<String, Object> data, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		String password = (String) data.get("password");
		String np = (String) data.get("np");
		String cnp = (String) data.get("cnp");
		if (adminId != null) {
			Admin updatedAdmin = adService.updatePassword(password, np, cnp, adminId);
			return ResponseEntity.status(HttpStatus.OK).body(updatedAdmin);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
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

	@CrossOrigin
	@PostMapping("createproducttype")
	public ResponseEntity<?> uploadproducttype(@RequestBody ProductType pt, HttpServletRequest request) {
		System.out.println("herer");
		System.out.println(pt);
		Integer adminId = (Integer) request.getAttribute("userId");
		System.out.println(adminId);
		if (adminId != null) { // before user todo adminId !=null
			System.out.println(pt);
			adService.creatept(pt, adminId);
			return ResponseEntity.status(HttpStatus.CREATED).body("Product Type Created Succesfully");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Product type");
	}

	@CrossOrigin
	@PostMapping("/createprocate")
	public ResponseEntity<?> createprocate(@RequestBody Map<String, Object> pc, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		if (adminId != null) { // before user todo adminId !=null
			adService.createpc(pc, adminId);
			return ResponseEntity.status(HttpStatus.CREATED).body("Product Category Created Successfully");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Product category");
	}

	@PostMapping("createproduct")
	public ResponseEntity<?> createproduct(HttpServletRequest request, @RequestParam(required = false) Integer pcate,
//			@RequestPart(required = false) MultipartFile pphoto,
//		    @RequestPart(required = false) MultipartFile pfile,
			@RequestParam String pname, @RequestParam String pdesc, @RequestParam String pcode,
			@RequestParam Long pprice, @RequestParam double CHcomm, @RequestParam double STcomm,
			@RequestParam double DHcomm, @RequestParam double Acomm, @RequestParam double SAcomm,
			@RequestParam double cityhcomm, @RequestParam(required = false) boolean isrenewal) {
		Integer adminId = (Integer) request.getAttribute("userId");
		System.out.println(adminId);
		System.out.println(pcate);
		if (adminId != null && pcate != null) { // before user todo adminId !=null

			// pphoto and pfile add
			adService.createproduct(pdesc, pcate, adminId, pname,pcode, pprice, CHcomm, STcomm, DHcomm, Acomm, SAcomm,
					cityhcomm, isrenewal);
			return ResponseEntity.status(HttpStatus.CREATED).body("Product Created Succesfully");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Product");
	}

	@GetMapping("createuserdata")
	public ResponseEntity<?> getUserData(HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		if (adminId != null) {
			List<Map<String, Object>> Userrole = uService.getAllUserIdAndRole();
			if (Userrole != null && !Userrole.isEmpty()) {
				return ResponseEntity.status(HttpStatus.CREATED).body(Userrole);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not find users with specified attributes");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try again");
	}


	
	
	@PostMapping("/viewDocument")
	public ResponseEntity<?> viewDocument(@RequestBody Map<String, Object> request) throws IOException {
	    Integer userid = (Integer) request.get("userid");
	    String docType = (String) request.get("docType"); // Could be "aadhaar" or "pan"


	    if (userid == null  || docType == null ) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    User user = uService.getUserById(userid);

	    if (user == null) {
	        return new ResponseEntity<>("user not found",HttpStatus.NOT_FOUND);
	    }

	    String documentPath = null;
	    if ("aadhar".equalsIgnoreCase(docType)) {
	        documentPath = user.getAADHAR();
	    } else if ("pan".equalsIgnoreCase(docType)) {
	        documentPath = user.getPAN();
	    } else {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    if (documentPath == null ) {
	    	return new ResponseEntity<>("KYC is pending", HttpStatus.OK);
	    }

//	    
	    
	    
	    	File file = new File(upload_dir + documentPath);

		    if (!file.exists()) {
		        return new ResponseEntity<>("KYC is pending",HttpStatus.OK);
		    }
		    
		    Resource resource = new InputStreamResource(new FileInputStream(file));
		    HttpHeaders headers = new HttpHeaders();
		    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());

		    MediaType contentType = getContentType(file); // Determine the correct content type
		 
		    return ResponseEntity.ok()
		            .headers(headers)
		            .contentType(contentType)
		            .body(resource);
		
	}
	  


	


 // Utility method to get MediaType based on file extension
    private MediaType getContentType(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "jpeg":
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // Fallback to a generic type
        }
    }
	


 // User-Management
 	@PostMapping("createuser")
 	public ResponseEntity<?> createuser(HttpServletRequest request, @RequestBody User user) {
 		Integer adminId = (Integer) request.getAttribute("userId");
 		if (adminId != null && user != null) {
 			user.setRole(user.getRole().toUpperCase());
 			if (user.getRole().equals("COUNTRYHEAD")) {
 				String userpassword = adService.createCH(adminId, user);
 				return ResponseEntity.status(HttpStatus.CREATED)
 	                    .body("Countryhead Created Successfully. Login details have been sent to " + user.getUseremail());
 			} else {
 				String userpassword = adService.createUser(adminId, user);
 				return ResponseEntity.status(HttpStatus.CREATED)
 	                    .body(user.getRole()+" Created Successfully. Login details have been sent to " + user.getUseremail());
 			}
 		}
 		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create User");
 	}

 	@PostMapping("/users")
 	public ResponseEntity<List<Map<String, Object>>> getUserOverview(@RequestBody(required = false) User request) {
 		List<Map<String, Object>> filteredUsers = adService.getUserOverview(); // Get all users
// 		System.out.println(request.getUserid());
 		if (request != null && request.getUserid() != 0) { // Check for a specific userid
 			Integer requestedUserid = request.getUserid();
 			filteredUsers = filteredUsers.stream() // Filter for the requested userid
 					.filter(user -> requestedUserid.equals(user.get("userid"))).collect(Collectors.toList());
 		}

 		Collections.reverse(filteredUsers);
 		return ResponseEntity.status(HttpStatus.OK).body(filteredUsers);
 	}
 	
 	
 	
    @PostMapping("/updateKYCStatus")
    public ResponseEntity<?> updateKYCStatus(@RequestBody Map<String, Object> request) {
        try {
            Integer userid = (Integer) request.get("userid");
            String status = (String) request.get("KYCstatus");

            uService.updateKYCStatus(userid, status);

            return new ResponseEntity<>("Status " + status, HttpStatus.OK);
        } catch (ClassCastException e) {
            return new ResponseEntity<>("Invalid request data format", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//	@PostMapping("/aadhar")
//    public ResponseEntity<Resource> viewAadhaar(@RequestBody User request) throws IOException {
//        User user = uService.getUserById(request.getUserid());
//
//        if (user == null || user.getAADHAR() == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        File file = new File(FILE_STORAGE_PATH + user.getAADHAR());
//
//        if (!file.exists()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        Resource resource = new InputStreamResource(new FileInputStream(file));
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());
//
//        MediaType contentType = getContentType(file); // Determine the correct content type
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(contentType)
//                .body(resource);
//    }
//
//    @PostMapping("/pan")
//    public ResponseEntity<Resource> viewPAN(@RequestBody User request) throws IOException {
//        User user = uService.getUserById(request.getUserid());
//
//        if (user == null || user.getPAN() == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        File file = new File(FILE_STORAGE_PATH + user.getPAN());
//
//        if (!file.exists()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        Resource resource = new InputStreamResource(new FileInputStream(file));
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());
//
//        MediaType contentType = getContentType(file); // Determine the correct content type
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(contentType)
//                .body(resource);
//    }

	@PutMapping("/user-role-management")
	public ResponseEntity<?> manageUserRole(@RequestBody(required = false) User user) {
		User updatedUser = adService.manageUserRole(user);
		return ResponseEntity.status(HttpStatus.OK).body("role and managedby  updated");
	}

	@PutMapping("/users/disable")
	public ResponseEntity<User> disableUser(@RequestBody User request) {
		int userId = request.getUserid();
		boolean disable = request.isUsDisabled();
		User updatedUser = adService.disableUser(userId, disable);
		return ResponseEntity.ok(updatedUser);
	}

//--------------------------------------------------------------------------------------
	@PostMapping("pendingsells")
	public ResponseEntity<?> getpendingsells(HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		if (adminId != null) {
			List<Sell> allsell = sellService.getallpendingsells();
			List<Sell> pendingsells = new ArrayList<Sell>();
			for (Sell sell : allsell) {
				if (sell.getSalestatus().equals("Pending")) {
					pendingsells.add(sell);
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(pendingsells);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out OR failed to get Sell");
	}

	@PostMapping("/sellapproved")
	public ResponseEntity<?> sellApprovedOrUnapproved(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
	    Integer sellId = (Integer) requestBody.get("sellid");
	    String status = (String) requestBody.get("status");

	    if (sellId != null && status != null && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Unapproved"))) {
	        Integer adminId = (Integer) request.getAttribute("userId");
	        if (adminId != null) {
	            Sell updatedSell = adService.updateSellStatus(sellId, status);
	            return ResponseEntity.status(HttpStatus.OK).body("Sell status updated to: " + updatedSell.getSalestatus());
	        } else {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to Update Sell");
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body. Please provide sellid and status.");
	    }
	}

	// --f
	@PostMapping("pending-withdrawals")
	public ResponseEntity<List<Map<String, Object>>> getPendingWithdrawals() {
		List<WithdrawalRequest> pendingWithdrawals = wRepo.findByStatus("Pending");
		List<Map<String, Object>> filteredWithdrawals = new ArrayList<>();

		// Define the date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		for (WithdrawalRequest withdrawal : pendingWithdrawals) {
			Map<String, Object> filteredWithdrawal = new HashMap<>();
			filteredWithdrawal.put("withdrawalId", withdrawal.getWithdrawalId());
			filteredWithdrawal.put("remainingAmount", withdrawal.getRemainingAmount());
			filteredWithdrawal.put("amount", withdrawal.getAmount());
			filteredWithdrawal.put("approved", withdrawal.isApproved());
			filteredWithdrawal.put("status", withdrawal.getStatus());
			filteredWithdrawal.put("processed", withdrawal.isProcessed());
			filteredWithdrawal.put("request_Date", dateFormat.format(withdrawal.getRequestDate())); // Manual formatting
			filteredWithdrawal.put("role", withdrawal.getRole());
			filteredWithdrawals.add(filteredWithdrawal);
		}

		return ResponseEntity.ok(filteredWithdrawals);
	}



	
	@PostMapping("/withdraw/approve")
	public ResponseEntity<String> approveWithdrawal(@RequestBody WithdrawalRequest request) {
	    Long withdrawalId = request.getWithdrawalId();
	    WithdrawalRequest withdrawalRequest = wRepo.findById(withdrawalId).orElse(null);

	    if (withdrawalRequest != null) {
	        String status = request.getStatus();

	        if ("Approved".equalsIgnoreCase(status)) {
	            withdrawalRequest.setApproved(true);
	            withdrawalRequest.setStatus("Approved");
	        } else if ("UnApproved".equalsIgnoreCase(status)) {
	            withdrawalRequest.setApproved(false);
	            withdrawalRequest.setStatus("UnApproved");
	        } else {
	            return ResponseEntity.badRequest().body("Invalid status specified.");
	        }

	        withdrawalRequest.setProcessed(true);
	        withdrawalRequest.setWitdhrawalDate(new java.util.Date());
	        wRepo.save(withdrawalRequest);

	        return ResponseEntity.ok("Withdrawal request " + status.toLowerCase() + " successfully.");
	    } else {
	        return ResponseEntity.badRequest().body("Withdrawal request not found.");
	    }
	}

	@GetMapping("/withdrawalList")
	public ResponseEntity<List<Map<String, Object>>> getWithdrawalRequests() {
	    List<Object[]> withdrawalRequests = wRepo.findByStatusInPendingOrApprovedOrUnApproved();
	 // Define the date format
	 		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    List<Map<String, Object>> response = withdrawalRequests.stream().map(obj -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("withdrawalId", obj[0]);
	        map.put("requestDate", dateFormat.format((Date) obj[1]));
	        map.put("Username", obj[2]);
	        map.put("role", obj[3]);
	        map.put("status", obj[4]);
	        double amount = (double) obj[5];  // Convert to double if necessary
	        map.put("amount", formatDouble(amount));
	        map.put("email", obj[6]);
	        map.put("Usermobile", obj[7]);

	        Date withdrawalDate = (Date) obj[8];
	        if (withdrawalDate != null) {
	            map.put("WitdhrawalDate", dateFormat.format(withdrawalDate));
	        }

	        return map;
	    }).collect(Collectors.toList());

	    return ResponseEntity.ok(response);
	}
	public static String formatDouble(double value) {
        return String.format("%.2f", value);
    }

	// --faisal
	@PostMapping("/agentSalesCount")
	public ResponseEntity<Map<String, Integer>> getNumberOfSalesByAgent() {
		Map<String, Integer> salesCounts = sellService.getNumberOfSalesByAgent();
		return ResponseEntity.ok(salesCounts);
	}

//	Update User Details
	@PutMapping("/updateUsers")
	public ResponseEntity<Map<String, Object>> updateUser(
	        @RequestParam int Userid,
	        @RequestParam(value = "cityorvillage", required = false) String cityorvillage,
	        @RequestParam(value = "district", required = false) String district,
	        @RequestParam(value = "gender", required = false) String gender,
	        @RequestParam(value = "state", required = false) String state,
	        @RequestParam(value = "dob", required = false) String dobStr,
	        @RequestParam(value = "Useraddress", required = false) String Useraddress,
	        @RequestPart(value = "UserProfilePhoto", required = false) MultipartFile UserProfilePhoto) throws ParseException {

	    User existingUser = uService.getUserById(Userid);
	    if (existingUser == null) {
	        return ResponseEntity.notFound().build();
	    }

	    Map<String, Object> updateUserFields = new HashMap<>();
	    updateUserFields.put("Userid", Userid);
	    if (cityorvillage != null) updateUserFields.put("cityorvillage", cityorvillage);
	    if (district != null) updateUserFields.put("district", district);
	    if (gender != null) updateUserFields.put("gender", gender);
	    if (state != null) updateUserFields.put("state", state);
	    if (dobStr != null) {
	        try {
	            java.sql.Date dob = new java.sql.Date(DateUtil.stringToDate(dobStr, "yyyy-MM-dd").getTime());
	            updateUserFields.put("dob", dob);
	        } catch (ParseException e) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format. Use yyyy-MM-dd."));
	        }
	    }
	    if (Useraddress != null) updateUserFields.put("Useraddress", Useraddress);

	    User updatedUser = uService.updateUserFields(existingUser, updateUserFields, UserProfilePhoto);

	    Map<String, Object> response = new HashMap<>();
	    response.put("Userid", updatedUser.getUserid());
	    response.put("cityorvillage", updatedUser.getCityorvillage());
	    response.put("district", updatedUser.getDistrict());
	    response.put("gender", updatedUser.getGender());
	    response.put("state", updatedUser.getState());
	    response.put("dob", DateUtil.dateToString(updatedUser.getDob(), "yyyy-MM-dd"));
	    response.put("Useraddress", updatedUser.getUseraddress());

	    return ResponseEntity.ok(response);
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

	
	@GetMapping("/kyc-pending")
	public ResponseEntity<List<Map<String, Object>>> getKYCPendingUsers() {
		List<Map<String, Object>> kycPendingUsers = adService.getKYCPendingUsers();
		return ResponseEntity.ok(kycPendingUsers);
	}
// ---------------------------------------------------
	@PostMapping("/commission-all-sale")
	public ResponseEntity<?> getAllCommissionData() {
		try {
			List<Sell> allSales = uService.getAllSalesData();

			if (allSales.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(Collections.singletonMap("message", "No commission data found."));
			}

			List<Map<String, Object>> response = new ArrayList<>();
			for (Sell sell : allSales) {
				response.add(adService.populateCommissionDetails(sell));
			}

			Collections.reverse(response);
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error",
					"An unexpected error occurred while fetching all commission data."));
		}
	}

	@PostMapping("myteam")
	public ResponseEntity<?> getMyTeam(@RequestBody User user, HttpServletRequest request) {
		Integer userid = user.getUserid();
		Integer userid1 = (Integer) request.getAttribute("userId");

		if (userid1 != null && userid1.equals(userid)) {
			List<Map<String, Object>> teamInfo = adService.getMyTeam(userid1);
			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}


	@PostMapping("/list_head")
	public ResponseEntity<?> getMyTeam(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
		return adService.getListHead(requestBody, request);
	}

	@PostMapping("/team-by-role")
	public ResponseEntity<?> getUsersManagedByRole(@RequestBody Map<String, Object> request) {
		try {
			String role = (String) request.get("role");

			if (role == null || role.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("error", "Role parameter is required."));
			}

			List<User> managedUsers = adService.getUsersManagedByRole(role);

			if (managedUsers.isEmpty()) {
				return ResponseEntity.status(204).body(Map.of("message", "No users found managed by the given role."));
			}

			return ResponseEntity.ok(managedUsers);

		} catch (Exception ex) {
			return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred."));
		}
	}

	@PostMapping("/myteamUsername")
	public ResponseEntity<?> getMyTeamWithUsername(@RequestBody User user, HttpSession session) {
		try {
			Integer userId = user.getUserid();
//			String username = user.getUsername();

			// Retrieve the ID of the logged-in admin from the session
			Integer adminId = (Integer) session.getAttribute("userID");

			// Check if loggedInAdminId is not null
			if (adminId != null) {
				// Check if the logged-in user is an admin
				boolean isAdmin = adService.isAdmin(adminId);

				// If the logged-in user is an admin, they can access any user's team
				if (isAdmin) {
					List<Map<String, Object>> teamInfo = adService.getMyTeam(userId);
					return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
				}
			} else {
				// Handle the case where loggedInAdminId is null
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not logged in");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}
	 // Endpoint to fetch usernames by role
    @PostMapping("/usernames")
    public ResponseEntity<List<Map<String, Object>>> getUsernamesByRole(@RequestBody Map<String, String> request) {
        String role = request.get("role");
        try {
            List<Map<String, Object>> users = adService.getUsernamesByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(Collections.singletonMap("error", e.getMessage())));
        }
    }

    // Endpoint to set target for a user
    @PostMapping("/set-target")
    public ResponseEntity<String> setUserTarget(@RequestBody Map<String, Object> request) {
        try {
            adService.setUserTarget(request);
            return ResponseEntity.ok("Target set successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to set target: " + e.getMessage());
        }
    }
    @PostMapping("/get-target")
    public ResponseEntity<?> getSalesTargetForUser(@RequestBody(required = false) Map<String, Integer> request) {
        if (request == null || !request.containsKey("userid")) {
            try {
                List<Map<String, Object>> response = adService.getAllSalesTargets();
               Collections.reverse(response);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to fetch all user targets: " + e.getMessage());
            }
        }

        Integer userId = request.get("userid");
        try {
            Map<String, Object> response = adService.getSalesTargetForUser(userId);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User target not found.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch user target: " + e.getMessage());
        }
    }


    // Endpoint to edit sales target for a user
    @PutMapping("/edit-target")
    public ResponseEntity<?> editUserTarget(@RequestBody Map<String, Object> request) {
        try {
            adService.editUserTarget(request);
            return ResponseEntity.ok("User target updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating user target.");
        }
    }

    // Endpoint to delete sales target for a user
    @PostMapping("/delete-target")
    public ResponseEntity<?> deleteUserTarget(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userid");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is required.");
        }

        try {
        	boolean isDeleted = adService.deleteUserTarget(userId);
            if (isDeleted) {
                return ResponseEntity.ok("User target deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User target not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the target: " + e.getMessage());
        }
    }
    
    @PostMapping("/setAmountAchieved")
    public ResponseEntity<?> setAmountAchieved( @RequestBody Map<String, Object> requestBody) {
        try {
            Boolean isAmountAchieved = (Boolean) requestBody.get("isAmountAchieved");
            System.out.println(isAmountAchieved);
            Integer userId =  (Integer) requestBody.get("userid");
            System.out.println(userId);
            if (isAmountAchieved == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body.");
            }
            User user = uRepo.findByUserid(userId);
            user.setIsAmountachived(isAmountAchieved);
            uRepo.save(user);
            System.out.println(user);
//            System.out.println( user.setIsAmountachived(isAmountAchieved));
            return ResponseEntity.ok("isAmountAchieved set "+ isAmountAchieved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
    
    
    @GetMapping("/managedUsers")
    public ResponseEntity<List<Map<String, Object>>> getUsersManagedByUser(@RequestBody Map<String, Object> user) {
        try {
            // Check if userId is provided and valid
            if (user.containsKey("userId") && user.get("userId") instanceof Integer) {
                int userId = (int) user.get("userId");
                System.out.println("User ID: " + userId);

                List<Map<String, Object>> managedUsers = adService.getMyTeam(userId);

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

    
	
//-------------------------------------
	
//	reports and analysis.
	@PostMapping("/generate_report")
	public ResponseEntity<String> generateReport() throws Exception {
		List<Map<String, Object>> reportData = adService.generateReport();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		String response = mapper.writeValueAsString(reportData);

		return ResponseEntity.ok(response);
	}
//----
//	Client-Monitoring
	@PostMapping("/client-monitoring")
	public ResponseEntity<List<Map<String, Object>>> getClientDataForAdmin(@RequestBody Map<String, String> request) throws ParseException {
	    String role = request.get("role");
	    if (role == null || !"admin".equalsIgnoreCase(role)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	    }

	    int page = request.containsKey("page") ? Integer.parseInt(request.get("page")) : 0;
	    int size = 10; // Default size

	    try {
	        List<Map<String, Object>> customerData = adService.getAllClientMonitoringData(page, size);
	        return ResponseEntity.ok(customerData);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
//	--------------
	@PostMapping("/role")
	public ResponseEntity<?> getSalesDataByRole(@RequestBody Map<String, Object> request) {
		try {
			// Validate and parse user role
			String userRole = (String) request.get("role");
			if (userRole == null || userRole.isEmpty()) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User role is required."));
			}

			List<Map<String, Object>> response = new ArrayList<>();

			if (isHigherLevelRole(userRole)) {
				// Get users with this role
				List<User> usersWithRole = uRepo.findAllByRole(userRole);

				// Fetch sales data for users in this role
				for (User user : usersWithRole) {
					List<Sell> salesData = adService.getSalesDataForUserHierarchy(user.getUserid());
					for (Sell sell : salesData) {
						response.add(adService.populateCommissionDetails(sell, user));
					}
				}

			} else {
				// If the role is not among the higher-level roles, fetch individual sales
				List<Sell> salesData = adService.getSalesDataByRole(userRole);

				if (salesData.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT)
							.body(Collections.singletonMap("message", "No sales data found for the given user role."));
				}

				for (Sell sell : salesData) {
					response.add(adService.populateCommissionDetails(sell));
				}
			}

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error",
					"An unexpected error occurred while fetching sales data by role."));
		}
	}

	private boolean isHigherLevelRole(String role) {
		Set<String> higherRoles = Set.of("COUNTRYHEAD", "STATEHEAD", "DISTRICTHEAD", "CITYHEAD");
		return higherRoles.contains(role);
	}
	@GetMapping("/agents-and-subagents")
    public List<Map<String, Object>> getAgentsAndSubagents() {
        return uService.getAgentsAndSubagents().stream()
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userid", user.getUserid());
                    userInfo.put("UserName", user.getUsername());
                    userInfo.put("Role", user.getRole());
                    return userInfo;
                })
                .collect(Collectors.toList());
    }
	
	@GetMapping("/counts")
    public Map<String, Object> getAllCounts() {
        Map<String, Object> response = new HashMap<>();
        
        response.putAll(adService.getUserRoleCounts());
        response.putAll(adService.getEntityCounts());
         response.putAll(adService.getTotalCommissionsByRoles());
         response.putAll(productService.getProductMonthlySells());
//        response.putAll(adService.groupAndCalculateSales());
        
        return response;
    }
	
	@PostMapping("/target")
	public ResponseEntity<Map<String, Object>> updateUserYearlyTarget(@RequestBody User request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        String updatedYearlyTarget = uService.updateUserYearlyTarget(request.getUserid(), request.getYearlyTarget());
	        response.put("message", "Yearly target updated successfully");
	        response.put("yearlyTarget", updatedYearlyTarget);
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    } catch (Exception e) {
	        response.put("message", "An error occurred while updating the yearly target");
	        response.put("yearlyTarget", 0);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


	@PostMapping("getteamtarget")
	public ResponseEntity<?> getMyTeamtarget( HttpServletRequest request) {
		
		Integer userid1 = (Integer) request.getAttribute("userId");

		if (userid1 != null ) {
			List<Map<String, Object>> teamInfo = uService.getMyTeamtargetforadmin(userid1);
			Collections.reverse(teamInfo);
			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}
	
	@PostMapping("countryheads")
	public ResponseEntity<?> getMyTeamcountryhead( HttpServletRequest request) {
		
		Integer userid1 = (Integer) request.getAttribute("userId");

		if (userid1 != null ) {
			List<Map<String, Object>> teamInfo = uService.getcountryheads();
			return ResponseEntity.status(HttpStatus.OK).body(teamInfo);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
	}
	
}
