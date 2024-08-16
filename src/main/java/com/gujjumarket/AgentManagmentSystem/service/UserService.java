package com.gujjumarket.AgentManagmentSystem.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gujjumarket.AgentManagmentSystem.config.DateUtil;
import com.gujjumarket.AgentManagmentSystem.model.Customer;
import com.gujjumarket.AgentManagmentSystem.model.Payment;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.RoleAmount;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.PaymentRepository;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.repo.RoleAmountRepo;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.utils.JWT;
import com.gujjumarket.AgentManagmentSystem.utils.PhotoUpload;
import com.gujjumarket.AgentManagmentSystem.utils.passwordHasher;
import com.razorpay.Order;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	Userrepo uRepo;
	
	@Autowired
	ProductRepo pRepo;
	
	@Autowired
	ProductService pService;
	
	@Autowired
	PaymentRepository paymentRepo;
	
	@Autowired
	RoleAmountRepo amountRepo;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	SellRepo sRepo;
	@Value("${upload-dir}")
	private String upload_dir;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private withdrawalRequestRepo wRepo;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDouble(double value) {
        return String.format("%.2f", value);
    }
	
	
//	public User getprofile(Integer userid1) {
//	    // Fetch the user based on the provided user ID
//	    User u = uRepo.findById(userid1).orElse(null);
//
//	    // Check if the user is not null and has a manager
//	    if (u != null && u.getManageBy() != 0) {
//	        // Fetch the manager user based on the manageBy field of the user
//	        User manageByUser = uRepo.findById(u.getManageBy()).orElse(null);
//
//	        // If the manager user is found, set the manager's username and usermobile to the current user
//	        if (manageByUser != null) {
//	            u.setManageByUsername(manageByUser.getUsername());
//	            u.setManageByUsermobile(manageByUser.getUsermobile());
//	        } else {
//	            System.out.println("Manager user not found for user ID: " + u.getManageBy());
//	        }
//	    }
//	    
//	    // Return the updated user object
//	    return u;
//	}
	public Map<String, Object> getprofile(Integer userid1) {
	    Map<String, Object> response = new HashMap<>();
	    User user = uRepo.findById(userid1).orElse(null);
	    if (user != null && user.getManageBy() != 0) {
	        User manageByUser = uRepo.findById(user.getManageBy()).orElse(null);
	        if (manageByUser != null) {
	            response.put("username", user.getUsername());
	            response.put("managedByName", manageByUser.getUsername());
	            response.put("phoneNo", String.valueOf(user.getUsermobile()));
	            response.put("gender", user.getGender());
	            response.put("dob", user.getDob().toString());
	            response.put("email", user.getUseremail());
	            response.put("address", user.getUseraddress());
	            response.put("state", user.getState());
	            response.put("district", user.getDistrict());
	            response.put("city", user.getCityorvillage());
	            response.put("managedBYnumber", manageByUser.getUsermobile());
	            return response;
	        } else {
	            System.out.println("Manager user not found for user ID: " + user.getManageBy());
	        }
	    }
	    return null;
	}
	
	
//	public List<User> getUsersByRole(String role) {
//        return uRepo.findByRole(role);
//    }

//	public List<User> getUsersUnderHierarchy(int Userid) {
//	    // Find users managed by the specified userId
//		
//	    List<User> users = uRepo.findByManageBy(Userid);
//	    System.out.println(users.size());
//	    
//	    
////	    if (users.isEmpty()) {
////	        // No subordinates found
////	        return List.of();
////	    }
////
////	    User user = users.get(0); // Assuming userId uniquely identifies a user
////	    String role = user.getRole();
////
////	    switch (role) {
////	        case "STATEHEAD":
////	        		return uRepo.findByManageBy(userId);
////	        case "DISTRICTHEAD":
////	        	return uRepo.findByManageBy(userId);
////	        case "CITYHEAD":
////	        	return uRepo.findByManageBy(userId);
////	        case "AGENT":
////	            // Fetch subordinates based on the user's manager ID
////	            return uRepo.findByManageBy(userId);
////	        case "SUBAGENT":
////	            // Subagents do not have further subordinates
////	            return List.of();
////	        default:
////	            // Handle other roles as needed
////	            return List.of();
////	    }
//		return users;
//	    
//	    
//	    
//	    
//	}
	public List<Map<String, Object>> getcountryheads() {
	    try {
	        List<User> myTeam = uRepo.findAllByRole("COUNTRYHEAD");
	        List<Map<String, Object>> listOfMember = new ArrayList<>();

	        // Number format for Indian Rupees
	        Locale indiaLocale = new Locale("en", "IN");
	        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);

	        for (User user : myTeam) {
	            Map<String, Object> memberAndSaleData = new HashMap<>();

	            // Add member data to the combined map
	            memberAndSaleData.put("userId", user.getUserid());
	            memberAndSaleData.put("username", user.getUsername());
	            memberAndSaleData.put("mobile", user.getUsermobile());
	            memberAndSaleData.put("totalEarning", currencyFormat.format(user.getTotalCommissionAmount()));
	            memberAndSaleData.put("role", user.getRole());
	            memberAndSaleData.put("yearlytargetAmount", formatDouble(user.getYearlyTarget()));
	            memberAndSaleData.put("Assignpercentage", user.getAssignpercentage());
	            memberAndSaleData.put("monthlytarget",formatDouble(user.getMonthlyTarget()));
	            
	            

	            List<Sell> sellList = user.getSell();

	            // Group sales by month, quarter, and year
	            Map<String, String> monthlySales = new LinkedHashMap<>();
	            Map<String, String> quarterlySales = new LinkedHashMap<>();
	            Map<String, String> yearlySales = new LinkedHashMap<>();

	            for (Sell sell : sellList) {
	                Date approvedDate = sell.getApprovedDate();
	                if (approvedDate != null) {
	                    Calendar calendar = Calendar.getInstance();
	                    calendar.setTime(approvedDate);

	                    int year = calendar.get(Calendar.YEAR);
	                    int month = calendar.get(Calendar.MONTH) + 1;
	                    int quarter = (month - 1) / 3 + 1;

	                    String monthKey = year + "-" + month;
	                    String quarterKey = year + "-Q" + quarter;
	                    String yearKey = String.valueOf(year);

	                    // Format sale amount as currency
	                    String formattedSaleAmount = currencyFormat.format(formatDouble(sell.getSaleamount()));

	                    // Update monthly sales
	                    monthlySales.put(monthKey, formattedSaleAmount);

	                    // Update quarterly sales
	                    quarterlySales.put(quarterKey, formattedSaleAmount);

	                    // Update yearly sales
	                    yearlySales.put(yearKey, formattedSaleAmount);
	                } else {
	                    // Handle the null approvedDate scenario here if necessary
	                    System.out.println("Warning: sell record has null approvedDate");
	                }
	            }

	            // Add sales data to the combined map
	            memberAndSaleData.put("monthlySales", monthlySales);
	            memberAndSaleData.put("quarterlySales", quarterlySales);
	            memberAndSaleData.put("yearlySales", yearlySales);

	            // Add the combined data for the user to the list of members
	            listOfMember.add(memberAndSaleData);
	        }

	        return listOfMember;
	    } catch (Exception e) {
	        // Log the exception for further analysis
	        e.printStackTrace();
	        // Throw a custom exception or return an empty list based on your requirement
	        return new ArrayList<>();
	    }
	}
	
	public List<Map<String, Object>> getMyTeam(int userId) {
	    try {
	        List<User> myTeam = uRepo.findByManageBy(userId);
	        List<Map<String, Object>> listOfMember = new ArrayList<>();
	        
	       

	        // Number format for Indian Rupees
	        Locale indiaLocale = new Locale("en", "IN");
	        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);

	        for (User user : myTeam) {
	            Map<String, Object> memberAndSaleData = new HashMap<>();

	            // Add member data to the combined map
	            memberAndSaleData.put("userId", user.getUserid());
	            memberAndSaleData.put("username", user.getUsername());
	            memberAndSaleData.put("mobile", user.getUsermobile());
	            memberAndSaleData.put("totalEarning", currencyFormat.format(Double.parseDouble(formatDouble(user.getTotalCommissionAmount()))));
	            memberAndSaleData.put("role", user.getRole());
	            memberAndSaleData.put("monthlytargetAmount", user.getMonthlyTarget());
	            memberAndSaleData.put("Assignpercentage", user.getAssignpercentage());
	            memberAndSaleData.put("userEmail",user.getUseremail());
	            memberAndSaleData.put("city",user.getCityorvillage());
	            memberAndSaleData.put("District",user.getDistrict());
	            memberAndSaleData.put("State",user.getState());
	            String formattedDate;
				try {
					formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
					
				} catch (ParseException e) {
					formattedDate = null; // Handle error appropriately
				}
	            memberAndSaleData.put("RgisterDate",formattedDate);
	            
	            

	            List<Sell> sellList = user.getSell();

	            // Group sales by month, quarter, and year
	            Map<String, String> monthlySales = new LinkedHashMap<>();
	            Map<String, String> quarterlySales = new LinkedHashMap<>();
	            Map<String, String> yearlySales = new LinkedHashMap<>();

	            for (Sell sell : sellList) {
	                Date approvedDate = sell.getApprovedDate();
	                if (approvedDate != null) {
	                    Calendar calendar = Calendar.getInstance();
	                    calendar.setTime(approvedDate);

	                    int year = calendar.get(Calendar.YEAR);
	                    int month = calendar.get(Calendar.MONTH) + 1;
	                    int quarter = (month - 1) / 3 + 1;

	                    String monthKey = year + "-" + month;
	                    String quarterKey = year + "-Q" + quarter;
	                    String yearKey = String.valueOf(year);

	                    // Format sale amount as currency
	                    String formattedSaleAmount = currencyFormat.format(Double.parseDouble(formatDouble(sell.getSaleamount())));

	                    // Update monthly sales
	                    monthlySales.put(monthKey, formattedSaleAmount);

	                    // Update quarterly sales
	                    quarterlySales.put(quarterKey, formattedSaleAmount);

	                    // Update yearly sales
	                    yearlySales.put(yearKey, formattedSaleAmount);
	                } else {
	                    // Handle the null approvedDate scenario here if necessary
	                    System.out.println("Warning: sell record has null approvedDate");
	                }
	            }

	            // Add sales data to the combined map
	            memberAndSaleData.put("monthlySales", monthlySales);
	            memberAndSaleData.put("quarterlySales", quarterlySales);
	            memberAndSaleData.put("yearlySales", yearlySales);

	            // Add the combined data for the user to the list of members
	            listOfMember.add(memberAndSaleData);
	            
	            
	        }
	        return listOfMember;
	    } catch (Exception e) {
	        // Log the exception for further analysis
	        e.printStackTrace();
	        // Throw a custom exception or return an empty list based on your requirement
	        return new ArrayList<>();
	    }
	}

	public List<User> getUsersManagedByUser(Integer managerUserId) {
        System.out.println(managerUserId);
		return uRepo.findByManageBy(managerUserId);
    }
		
	 public List<User> getUsersByRoleAndParent(String role, Integer userId) {
	        List<User> users = Collections.emptyList();

	        if (role.equals(User.ROLE_STATE_HEAD)) {
	            users = uRepo.findByRoleAndManageBy(User.ROLE_STATE_HEAD, userId);
	        } else if (role.equals(User.ROLE_DISTRICT_HEAD) || role.equals(User.ROLE_CITY_HEAD) || role.equals(User.ROLE_AGENT) || role.equals(User.ROLE_SUB_AGENT)) {
	            users = uRepo.findByManageBy(userId);
	        }

	        return users;
	    }

	    public List<User> getManagedUsers(int managerId) {
	        return uRepo.findByManageBy(managerId);
	    }

//	    public List<User> getUsersByRole(String role) {
//	        return uRepo.findByRole(role);
//	    }

	    public List<Sell> getSellsByUser(int userId) {
	        User user = uRepo.findById(userId).orElse(null);
	        if (user != null) {
	            return user.getSell();
	        }
	        return Collections.emptyList();
	    }

    
	
//	//new added
//	public void deleteUserById(Integer userid) {
//        try {
//            if (uRepo.existsById(userid)) {
//                System.out.println("User with userid " + userid + " exists. Proceeding to delete.");
//                uRepo.deleteById(userid);
//                System.out.println("User with userid " + userid + " has been deleted.");
//            } else {
//                System.out.println("User with userid " + userid + " does not exist.");
//                throw new NoSuchElementException("User with userid " + userid + " does not exist");
//            }
//        } catch (Exception e) {
//            System.err.println("Error deleting user with userid " + userid + ": " + e.getMessage());
//            throw e;
//        }
//    }
	    
//	public ResponseEntity<?> loginUser(Long userMobile, String userPassword, HttpSession session) {
//		User user = uRepo.findByusermobile(userMobile);
//
//		if (user != null && !user.isUsDisabled()) {
//			// Check if the provided password matches either plaintext or hashed password
//			if (userPassword.equals(user.getUserpassword())
//					|| passwordHasher.verifyPassword(userPassword, user.getUserpassword())) {
//				// Successful login
////				String mobile = String.valueOf(userMobile);
//				String jwtToken = JWT.generateToken(user.getUserid(), user.getRole());
//				session.setAttribute("userID", user.getUserid());
//				Map<String, Object> response = new HashMap<String, Object>();
//				response.put("token", jwtToken);
//				response.put("role", user.getRole());
//				response.put("userid", user.getUserid());
//				response.put("isFirstLogin", user.isFirstTimeLogin());
//				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
//			}
//		}
//
//		// User not found, disabled, or incorrect credentials
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid User credentials Or User Disabled");
//	}
	    
	    public ResponseEntity<?> loginUser(Long userMobile, String userPassword, HttpSession session) {
			User user = uRepo.findByusermobile(userMobile);

			
			if (user != null && !user.isUsDisabled()) {
				
				// Check if the provided password matches either plaintext or hashed password
				if (userPassword.equals(user.getUserpassword())
						|| passwordHasher.verifyPassword(userPassword, user.getUserpassword())) {
					// Successful login
//					String mobile = String.valueOf(userMobile);
					String jwtToken = JWT.generateToken(user.getUserid(), user.getRole());
					session.setAttribute("userID", user.getUserid());
					Map<String, Object> response = new HashMap<String, Object>();
					user.setFirstTimeLogin(false);
					response.put("token", jwtToken);
					response.put("role", user.getRole());
					response.put("userid", user.getUserid());
					response.put("isFirstLogin", user.isFirstTimeLogin());
					response.put("isPaid", user.isPaid());
					
					if (!user.isPaid()) {
	                    RoleAmount roleAmount = amountRepo.findByRole(user.getRole());
	                    if (roleAmount != null) {
	                        response.put("amount", roleAmount.getAmount());
	                    }
	                }
					
					return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
				}
	            
			}

			// User not found, disabled, or incorrect credentials
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid User credentials Or User Disabled");
		}
	    
	    

	public void saveImage(int userId, MultipartFile photo) throws IOException {
		// Retrieve the existing Admin entity from the database
		Optional<User> UserOptinal = uRepo.findById(userId);

		// Check if the Admin entity exists
		if (UserOptinal.isPresent()) {
			User user = UserOptinal.get();

			// Set the photo field
			String filename = photo.getOriginalFilename();
			String storeFilename = PhotoUpload.saveFile(upload_dir, filename, photo);
			user.setUserprofile(storeFilename);

			// Save the updated user entity
			uRepo.save(user);
		} else {
			throw new EntityNotFoundException("Admin with ID " + userId + " not found");
		}
	}

	public User getlogin(long usermobile, String userpassword) {
		User u = uRepo.findByusermobile(usermobile);
		return u;
	}

//	 public String createSubAgent(Integer loggedInUserId, User subAgent) {
//	        User loggedInUser = uRepo.findById(loggedInUserId).orElse(null);
//	        if (loggedInUser != null) {
//	            // Assuming "Subagent" is a specific role for subagents
//	            subAgent.setRole("SUBAGENT");
//	            subAgent.setUserpassword(UUID.randomUUID().toString());
//	            subAgent.setCreatedby(loggedInUser.getCreatedby());
//	            subAgent.setUpdatedby(loggedInUser.getUpdatedby());
//	            subAgent.setManageBy(loggedInUserId);
//	            subAgent.setCreateddate(Date.valueOf(LocalDate.now()));
//	            subAgent.setUpdateddate(Date.valueOf(LocalDate.now()));
//	            subAgent.setIsSubUser(true);
//	            subAgent.setFirstTimeLogin(true);
//	            subAgent.setUsDisabled(false);
//	            subAgent.setState(subAgent.getState());
//	            subAgent.setDistrict(subAgent.getDistrict());
//	            subAgent.setCityorvillage(subAgent.getCityorvillage());
//	            subAgent.setBankname(subAgent.getBankname());
//	            subAgent.setBranchname(subAgent.getBranchname());
//	            subAgent.setIFSCcode(subAgent.getIFSCcode());
//	            subAgent.setAccounttype(subAgent.getAccounttype());
//	            subAgent.setAccountholdername(subAgent.getAccountholdername());
//	            subAgent.setAccountNo(subAgent.getAccountNo());
////	            if (aadharPhoto != null && !aadharPhoto.isEmpty()) {
////	                String aadharPhotoName = SaveFile( aadharPhoto);
////	                subAgent.setAADHAR(aadharPhotoName);
////	            }
////
////	            // Save PAN photo
////	            if (panPhoto != null && !panPhoto.isEmpty()) {
////	                String panPhotoName = SaveFile( panPhoto);
////	                subAgent.setAADHAR(panPhotoName);
////	            }
//
//	            
//	            uRepo.save(subAgent);
//	            return subAgent.getUserpassword();
//	        }
//	        throw new IllegalArgumentException("Unable to Create Subagent");
//	    }
	
	public String createSubAgent(Integer loggedInUserId, User subAgent) {
	    User loggedInUser = uRepo.findById(loggedInUserId).orElse(null);
	    if (loggedInUser != null) {
	        subAgent.setRole("SUBAGENT");
	        subAgent.setUserpassword(UUID.randomUUID().toString());
	        subAgent.setCreatedby(loggedInUser.getCreatedby());
	        subAgent.setUpdatedby(loggedInUser.getUpdatedby());
	        subAgent.setManageBy(loggedInUserId);
	        subAgent.setCreateddate(Date.valueOf(LocalDate.now()));
	        subAgent.setUpdateddate(Date.valueOf(LocalDate.now()));
	        subAgent.setIsSubUser(true);
	        subAgent.setFirstTimeLogin(true);
	        subAgent.setUsDisabled(false);
//	        subAgent.setState(subAgent.getState());
//            subAgent.setDistrict(subAgent.getDistrict());
//            subAgent.setCityorvillage(subAgent.getCityorvillage());
//            subAgent.setBankname(subAgent.getBankname());
//            subAgent.setBranchname(subAgent.getBranchname());
//            subAgent.setIFSCcode(subAgent.getIFSCcode());
//            subAgent.setAccounttype(subAgent.getAccounttype());
//            subAgent.setAccountholdername(subAgent.getAccountholdername());
//            subAgent.setAccountNo(subAgent.getAccountNo());
            subAgent.setUsername(subAgent.getUsername());
	        subAgent.setUseremail(subAgent.getUseremail());
	        subAgent.setUsermobile(subAgent.getUsermobile());
	        subAgent.setKYCstatus("Unapproved");
	        // Set additional fields as necessary

//	        // Save PAN photo
//	        if (panPhoto != null && !panPhoto.isEmpty()) {
//	            String panPhotoName = saveFile(panPhoto,"PAN");
//	            subAgent.setPAN(panPhotoName); // Assuming setPAN is the method to set PAN photo filename
//	        }
//
//	        // Save AADHAR photo
//	        if (aadharPhoto != null && !aadharPhoto.isEmpty()) {
//	            String aadharPhotoName = saveFile(aadharPhoto,"AADHAR");
//	            subAgent.setAADHAR(aadharPhotoName); // Assuming setAADHAR is the method to set AADHAR photo filename
//	        }

	        uRepo.save(subAgent);
	        sendAgentemail(subAgent);
	        return subAgent.getUserpassword();
	    }
	    throw new IllegalArgumentException("Unable to Create Subagent");
	}
	
	private void sendCityheadmail(User subAgent) {
		String subject = "Agent Account Created";
		String text = "Agent Created Successfully.\n" + "Username: " + subAgent.getUsername() + "\n" + "Email: "
				+ subAgent.getUseremail() + "\n" + "Password: " + subAgent.getUserpassword();

		sendEmail(subAgent.getUseremail(), subject, text);
	}

	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}
	private void sendAgentemail(User Agent) {
		String subject = "Subagent Account Created";
		String text = "Subagent Created Successfully.\n" + "Username: " + Agent.getUsername() + "\n" + "Email: "
				+ Agent.getUseremail() + "\n" + "Password: " + Agent.getUserpassword();

		sendEmail(Agent.getUseremail(), subject, text);
	}
	
	public String createAgent(Integer loggedInUserId, User Agent) {
	    User loggedInUser = uRepo.findById(loggedInUserId).orElse(null);
	    if (loggedInUser != null) {
	        Agent.setRole("AGENT");
	        Agent.setUserpassword(UUID.randomUUID().toString());
	        Agent.setCreatedby(loggedInUser.getCreatedby());
	        Agent.setUpdatedby(loggedInUser.getUpdatedby());
	        Agent.setManageBy(loggedInUserId);
	        Agent.setCreateddate(Date.valueOf(LocalDate.now()));
	        Agent.setUpdateddate(Date.valueOf(LocalDate.now()));
	        Agent.setIsSubUser(true);
	        Agent.setFirstTimeLogin(true);
	        Agent.setUsDisabled(false);
            Agent.setUsername(Agent.getUsername());
	        Agent.setUseremail(Agent.getUseremail());
	        Agent.setUsermobile(Agent.getUsermobile());
	        Agent.setKYCstatus("Unapproved");
	        

	        uRepo.save(Agent);
	        sendCityheadmail(Agent);
	        return Agent.getUserpassword();
	    }
	    throw new IllegalArgumentException("Unable to Create Agent");
	}

	private String saveFile(MultipartFile file,String fileType) {
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

	public List<User> getalluser() {
		// TODO Auto-generated method stub
		List<User> u = uRepo.findAll();
		return u;
	}

	public User getUserMobile(Long usermobile) {
		User u = uRepo.findByusermobile(usermobile);
		return u;
	}
	
	public User savepassword(User u, String np) {
		// TODO Auto-generated method stub
		String hashPassword = passwordHasher.hashPassword(np);
		u.setUserpassword(hashPassword);
		return uRepo.save(u);
	}

	public List<User> getmyteam(Integer userid1) {
		List<User> u = uRepo.getmyteam(userid1);
		return u;
	}

	public User getUserById(int Userid) {
		return uRepo.findById(Userid).orElse(null);
	}

	public User updateUser(User user) {
		return uRepo.save(user);
	}
	
	public User getManager(Integer managerId) {
        return uRepo.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }
	
//	// Update user details (username, useremail, role, dob, usermobile)
//	public User updateUserDetails(int userId, String username, String useremail, String role, java.sql.Date dob,
//			Long usermobile) {
//		User existingUser = getUserById(userId);
//		if (existingUser == null) {
//			return null;
//		}
//		if (username != null) {
//			existingUser.setUsername(username);
//		}
//		if (useremail != null) {
//			existingUser.setUseremail(useremail);
//		}
//		if (role != null) {
//			existingUser.setRole(role);
//		}
//		if (dob != null) {
//			existingUser.setDob(dob);
//		}
//		if (usermobile != null) {
//			existingUser.setUsermobile(usermobile);
//		}
//		return updateUser(existingUser);
//	}
//
//	public User updateKYCDetails(int userId, String pan, String aadhar) {
//		User existingUser = getUserById(userId);
//		if (existingUser == null) {
//			return null;
//		}
//		if (pan != null) {
//			existingUser.setPAN(pan);
//		}
//		if (aadhar != null) {
//			existingUser.setAADHAR(aadhar);
//		}
//		return updateUser(existingUser);
//	}
//
//	public User updateBankDetails(int userId, String bankname, String branchname, String accounttype,
//			String accountholdername, String ifsccode) {
//		User existingUser = getUserById(userId);
//		if (existingUser == null) {
//			return null;
//		}
//		if (bankname != null) {
//			existingUser.setBankname(bankname);
//		}
//		if (branchname != null) {
//			existingUser.setBranchname(branchname);
//		}
//		if (accounttype != null) {
//			existingUser.setAccounttype(accounttype);
//		}
//		if (accountholdername != null) {
//			existingUser.setAccountholdername(accountholdername);
//		}
//		if (ifsccode != null) {
//			existingUser.setIFSCcode(ifsccode);
//		}
//		return updateUser(existingUser);
//	}

//	--by gulam bhai and i add the withdrwal information.
//	public List<Map<String, Object>> getMyTeam(int userId) {
//		List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
//		List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//		for (User user : myTeam) {
//			Map<String, Object> memberAndWithdrawalData = new HashMap<>();
//
//			// Add member data to the combined map
//			memberAndWithdrawalData.put("name", user.getUsername());
//			memberAndWithdrawalData.put("mobile", user.getUsermobile());
//			memberAndWithdrawalData.put("totalEarning", user.getTotalCommissionAmount());
//
//			List<WithdrawalRequest> reqList = wRepo.findAllByUser(user);
//
//			// Create a list to hold withdrawal data for the user
//			List<Map<String, Object>> withdrawalDataList = new ArrayList<>();
//
//			for (WithdrawalRequest req : reqList) {
//				// Create a new map for each withdrawal request
//				Map<String, Object> withdrawalData = new HashMap<>();
//				withdrawalData.put("remainingAmount", req.getRemainingAmount());
//				withdrawalData.put("withdrawalAmount", req.getAmount());
//
//				// Add withdrawalData to the list of withdrawal data
//				withdrawalDataList.add(withdrawalData);
//			}
//
//			// Add the list of withdrawal data to the combined map
//			memberAndWithdrawalData.put("withdrawalData", withdrawalDataList);
//
//			// Add the combined data for the user to the list of members
//			listOfMember.add(memberAndWithdrawalData);
//		}
//
//		return listOfMember;
//	}

//	public User updateUserFields(User existingUser, Map<String, Object> updateUserFields, MultipartFile userProfilePhoto) {
//	    if (updateUserFields.containsKey("cityorvillage")) {
//	        existingUser.setCityorvillage((String) updateUserFields.get("cityorvillage"));
//	    }
//	    if (updateUserFields.containsKey("district")) {
//	        existingUser.setDistrict((String) updateUserFields.get("district"));
//	    }
//	    if (updateUserFields.containsKey("gender")) {
//	        existingUser.setGender((String) updateUserFields.get("gender"));
//	    }
//	    if (updateUserFields.containsKey("state")) {
//	        existingUser.setState((String) updateUserFields.get("state"));
//	    }
//	    if (updateUserFields.containsKey("dob")) {
//	        existingUser.setDob((java.sql.Date) updateUserFields.get("dob"));
//	    }
//	    if (updateUserFields.containsKey("Useraddress")) {
//	        existingUser.setUseraddress((String) updateUserFields.get("Useraddress"));
//	    }
//
//	    if (userProfilePhoto != null && !userProfilePhoto.isEmpty()) {
//	        String profilePhotoPath = saveUserProfilePhoto(userProfilePhoto);
//	        existingUser.setUserprofile(profilePhotoPath);
//	    }
//	    
//
//	    return uRepo.save(existingUser);
//	}
	public User updateUserFields(User existingUser, Map<String, Object> updateUserFields, MultipartFile userProfilePhoto) {
	    if (updateUserFields.containsKey("cityorvillage")) {
	        existingUser.setCityorvillage((String) updateUserFields.get("cityorvillage"));
	    }
	    if (updateUserFields.containsKey("district")) {
	        existingUser.setDistrict((String) updateUserFields.get("district"));
	    }
	    if (updateUserFields.containsKey("gender")) {
	        existingUser.setGender((String) updateUserFields.get("gender"));
	    }
	    if (updateUserFields.containsKey("state")) {
	        existingUser.setState((String) updateUserFields.get("state"));
	    }
	    if (updateUserFields.containsKey("dob")) {
	        existingUser.setDob((java.sql.Date) updateUserFields.get("dob"));
	    }
	    if (updateUserFields.containsKey("Useraddress")) {
	        existingUser.setUseraddress((String) updateUserFields.get("Useraddress"));
	    }
	    if (updateUserFields.containsKey("Useremail")) { // Update Useremail if present
	        existingUser.setUseremail((String) updateUserFields.get("Useremail"));
	    }

	    if (userProfilePhoto != null && !userProfilePhoto.isEmpty()) {
	        String profilePhotoPath = saveUserProfilePhoto(userProfilePhoto);
	        existingUser.setUserprofile(profilePhotoPath);
	    }

	    return uRepo.save(existingUser);
	}



	private String saveUserProfilePhoto(MultipartFile userProfilePhoto) {
	    File directory = new File(upload_dir);
	    if (!directory.exists()) {
	        directory.mkdirs();
	    }
	    String fileName = userProfilePhoto.getOriginalFilename();
	    String filePath = upload_dir + File.separator + fileName;
	    File file = new File(filePath);
	    try {
	        userProfilePhoto.transferTo(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return fileName;
	}

//	public User updateKYCFields(User existingUser, User updateUser) {
//		// Update KYC fields if provided
//		existingUser.setPAN(updateUser.getPAN() != null ? updateUser.getPAN() : existingUser.getPAN());
//		existingUser.setAADHAR(updateUser.getAADHAR() != null ? updateUser.getAADHAR() : existingUser.getAADHAR());
//
//		existingUser.setIsKYCDone(existingUser.getPAN() != null && !existingUser.getPAN().isEmpty()
//				&& existingUser.getAADHAR() != null && !existingUser.getAADHAR().isEmpty());
//
//		// Save the updated user
//		return uRepo.save(existingUser);
//	}

	public Map<String, Object> updateKYCWithFiles(MultipartFile panFile, MultipartFile aadharFile, User updateUser,
			UserService userService) {
		// Process PAN file
		if (!panFile.isEmpty()) {
			String panFileName = "pan_" + updateUser.getUserid() + "_" + panFile.getOriginalFilename();
			String storeFilename = PhotoUpload.saveFile(upload_dir, panFileName, panFile);
			updateUser.setPAN(panFileName); // Set PAN file name to the user
			updateUser.setIsKYCDone(true);
		}

		// Process Aadhar file
		if (!aadharFile.isEmpty()) {
			String aadharFileName = "aadhar_" + updateUser.getUserid() + "_" + aadharFile.getOriginalFilename();
			PhotoUpload.saveFile(upload_dir, aadharFileName, aadharFile);
			updateUser.setAADHAR(aadharFileName); // Set AADHAR file name to the user
		}

		// Save the user to persist the changes
		userService.saveOrUpdate(updateUser); // Use the service to update the user in the database

		// Update KYC fields in the user object for the response
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("username", updateUser.getUsername());
		responseMap.put("usermobile", updateUser.getUsermobile());
		responseMap.put("useremail", updateUser.getUseremail());
		responseMap.put("role", updateUser.getRole());
		responseMap.put("createddate", updateUser.getCreateddate());
		responseMap.put("PAN", updateUser.getPAN());
		responseMap.put("AADHAR", updateUser.getAADHAR());
		responseMap.put("KYCdone",updateUser.isIsKYCDone());

		return responseMap;
	}

	public void saveOrUpdate(User user) {
		uRepo.save(user); // Persists the updated user object in the database
	}

	public User updateBankDetails(User existingUser, User updateUser) {
		// Update bank details fields if provided
		existingUser
				.setBankname(updateUser.getBankname() != null ? updateUser.getBankname() : existingUser.getBankname());
		// Continue updating other bank details fields...

		existingUser.setBranchname(
				updateUser.getBranchname() != null ? updateUser.getBranchname() : existingUser.getBranchname());
		existingUser.setAccounttype(
				updateUser.getAccounttype() != null ? updateUser.getAccounttype() : existingUser.getAccounttype());
		existingUser.setAccountholdername(updateUser.getAccountholdername() != null ? updateUser.getAccountholdername()
				: existingUser.getAccountholdername());
		existingUser
				.setIFSCcode(updateUser.getIFSCcode() != null ? updateUser.getIFSCcode() : existingUser.getIFSCcode());

		existingUser.setAccountNo(updateUser.getAccountNo() != null ? updateUser.getAccountNo() : existingUser.getAccountNo());
		
		// Save the updated user
		return uRepo.save(existingUser);
	}

	public List<User> getUsersByRoles(String... roles) {
		// Query users by roles
		return uRepo.findByRoleIn(Arrays.asList(roles));
	}

	

	public User changepasswordFirsttime(User u, String np, String cnp, String question1, String question2,
			String securityanswer1, String securityanswer2, String password) {

		if (u.getUserpassword().equals(password) && np.equals(cnp)) {
			System.out.println("inside service if block");
//			u.setSecurityanswer1(securityanswer1);
//			u.setSecurityanswer2(securityanswer2);
//			u.setQuestion1(password);
			u.setFirstTimeLogin(false);
			String hashPassword = passwordHasher.hashPassword(password);
			u.setUserpassword(hashPassword);
//			u.setQuestion1(question1);
//			u.setQuestion2(question2);
			System.out.println("password hashed");
			return uRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public User changepassword(String password, String np, String cnp, User u) {
		if (passwordHasher.verifyPassword(password, u.getUserpassword()) && np.equals(cnp)) {
			String hashPassword = passwordHasher.hashPassword(cnp);
			u.setUserpassword(hashPassword);
			return uRepo.save(u);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch here");
	}

//	public List<Map<String, Object>> getMyTeam(int userId, String username) {
//	    List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
//	    List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//	    for (User user : myTeam) {
//	        Map<String, Object> memberAndWithdrawalData = new HashMap<>();
//
//	        // Add member data to the combined map
//	        memberAndWithdrawalData.put("userId", user.getUserid()); // Include userId
//	        memberAndWithdrawalData.put("username", user.getUsername()); // Include username
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

	public List<Map<String, Object>> getMyTeam(int userId, String username) {
		try {
			List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
			List<Map<String, Object>> listOfMember = new ArrayList<>();

			for (User user : myTeam) {
				Map<String, Object> memberAndSaleData = new HashMap<>();

				// Add member data to the combined map
				memberAndSaleData.put("userId", user.getUserid());
				memberAndSaleData.put("username", user.getUsername());
				memberAndSaleData.put("mobile", user.getUsermobile());
				memberAndSaleData.put("totalEarning", user.getTotalCommissionAmount());

				List<Sell> sellList = user.getSell();

				// Group sales by month, quarter, and year
				Map<String, Double> monthlySales = new HashMap<>();
				Map<String, Double> quarterlySales = new HashMap<>();
				Map<String, Double> yearlySales = new HashMap<>();

				for (Sell sell : sellList) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(sell.getApprovedDate());

					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH) + 1;
					int quarter = (month - 1) / 3 + 1;

					String monthKey = year + "-" + month;
					String quarterKey = year + "-Q" + quarter;
					String yearKey = String.valueOf(year);

					// Update monthly sales
					monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
					// Update quarterly sales
					quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
					// Update yearly sales
					yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
				}

				// Add sales data to the combined map
				memberAndSaleData.put("monthlySales", monthlySales);
				memberAndSaleData.put("quarterlySales", quarterlySales);
				memberAndSaleData.put("yearlySales", yearlySales);

				// Add the combined data for the user to the list of members
				listOfMember.add(memberAndSaleData);
			}

			return listOfMember;
		} catch (Exception e) {
			// Log the exception for further analysis
			e.printStackTrace();
			// Throw a custom exception or return an empty list based on your requirement
			return new ArrayList<>();
		}
	}

	public boolean isAdmin(Integer adminId) {
		// Fetch the user from the database based on the user ID
		Optional<User> userOptional = uRepo.findById(adminId);

		// Check if the user exists and if the user's role indicates admin privileges
		return userOptional.isPresent() && userOptional.get().getRole().equalsIgnoreCase("admin");
	}
	
//	public List<Map<String, Object>> getMyTeamtarget(int userId) {
//		try {
//			List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
//			List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//			for (User user : myTeam) {
//				Map<String, Object> memberAndSaleData = new HashMap<>();
//
//				// Add member data to the combined map
//				memberAndSaleData.put("userId", user.getUserid());
//				memberAndSaleData.put("username", user.getUsername());
//				memberAndSaleData.put("mobile", user.getUsermobile());
//				memberAndSaleData.put("MonthlyTarget", user.getMonthlyTarget());
//				memberAndSaleData.put("QuarterlyTarget", user.getQuarterlyTarget());
//				memberAndSaleData.put("HalfyearTarget", user.getHalfYearlyTarget());
//				memberAndSaleData.put("YearlyTarget", user.getYearlyTarget());
//				List<Sell> sellList = user.getSell();
//
//				// Group sales by month, quarter, and year
//				Map<String, Double> monthlySales = new HashMap<>();
//				Map<String, Double> quarterlySales = new HashMap<>();
//				Map<String, Double> yearlySales = new HashMap<>();
//
//				for (Sell sell : sellList) {
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(sell.getApprovedDate());
//
//					int year = calendar.get(Calendar.YEAR);
//					int month = calendar.get(Calendar.MONTH) + 1;
//					int quarter = (month - 1) / 3 + 1;
//
//					String monthKey = year + "-" + month;
//					String quarterKey = year + "-Q" + quarter;
//					String yearKey = String.valueOf(year);
//
//					// Update monthly sales
//					monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//					// Update quarterly sales
//					quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//					// Update yearly sales
//					yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//				}
//
//				// Add sales data to the combined map
//				memberAndSaleData.put("AchivedAmount", monthlySales);
////				memberAndSaleData.put("quarterlySales", quarterlySales);
////				memberAndSaleData.put("yearlySales", yearlySales);
//
//				// Add the combined data for the user to the list of members
//				listOfMember.add(memberAndSaleData);
//			}
//
//			return listOfMember;
//		} catch (Exception e) {
//			// Log the exception for further analysis
//			e.printStackTrace();
//			// Throw a custom exception or return an empty list based on your requirement
//			return new ArrayList<>();
//		}
//	}
//	
//	public List<Map<String, Object>> getMyTeamtarget() {
//		try {
//			List<User> myTeam = uRepo.findAll();
//			List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//			for (User user : myTeam) {
//				Map<String, Object> memberAndSaleData = new HashMap<>();
//
//				// Add member data to the combined map
//				memberAndSaleData.put("userId", user.getUserid());
//				memberAndSaleData.put("username", user.getUsername());
//				memberAndSaleData.put("mobile", user.getUsermobile());
//				memberAndSaleData.put("MonthlyTarget", user.getMonthlyTarget());
//				memberAndSaleData.put("QuarterlyTarget", user.getQuarterlyTarget());
//				memberAndSaleData.put("HalfyearTarget", user.getHalfYearlyTarget());
//				memberAndSaleData.put("YearlyTarget", user.getYearlyTarget());
//				List<Sell> sellList = user.getSell();
//
//				// Group sales by month, quarter, and year
//				Map<String, Double> monthlySales = new HashMap<>();
//				Map<String, Double> quarterlySales = new HashMap<>();
//				Map<String, Double> yearlySales = new HashMap<>();
//
//				for (Sell sell : sellList) {
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(sell.getApprovedDate());
//
//					int year = calendar.get(Calendar.YEAR);
//					int month = calendar.get(Calendar.MONTH) + 1;
//					int quarter = (month - 1) / 3 + 1;
//
//					String monthKey = year + "-" + month;
//					String quarterKey = year + "-Q" + quarter;
//					String yearKey = String.valueOf(year);
//
//					// Update monthly sales
//					monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//					// Update quarterly sales
//					quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//					// Update yearly sales
//					yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//				}
//
//				// Add sales data to the combined map
//				memberAndSaleData.put("AchivedAmount", monthlySales);
////				memberAndSaleData.put("quarterlySales", quarterlySales);
////				memberAndSaleData.put("yearlySales", yearlySales);
//
//				// Add the combined data for the user to the list of members
//				listOfMember.add(memberAndSaleData);
//			}
//
//			return listOfMember;
//		} catch (Exception e) {
//			// Log the exception for further analysis
//			e.printStackTrace();
//			// Throw a custom exception or return an empty list based on your requirement
//			return new ArrayList<>();
//		}
//	}

	public void changePassword(String currentPassword, String newPassword, String confirmPassword, User user) {
		// Verify if the provided current password matches the stored plain text
		// password
		
		if (!passwordHasher.verifyPassword(currentPassword, user.getUserpassword())) {
			
			throw new IllegalArgumentException("Incorrect current password.");
		}

		// Check if the new password matches the confirmed password
		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("New password and confirm password do not match.");
		}

		// Hash the new password using BCrypt
		String hashedNewPassword = passwordHasher.hashPassword(newPassword);

		// Set the new hashed password for the user
		user.setUserpassword(hashedNewPassword);

		// Save the updated user in the repository
		uRepo.save(user);
	}

//	 public List<Sell> getSalesDataForUserHierarchy(int userId) {
//	        List<Sell> salesDataList = new ArrayList<>();
//
//	        // Retrieve the root user
//	        User rootUser = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//	        // Ensure visitedUserIds is not null
//	        Set<Integer> visitedUserIds = new HashSet<>();
//
//	        // Get all users in the hierarchy
//	        List<User> hierarchicalUsers = getHierarchicalUsers(rootUser, visitedUserIds);
//
//	        // Retrieve sales data for all users in the hierarchy
//	        for (User user : hierarchicalUsers) {
//	            List<Sell> userSales = sRepo.findBySoldbyUserId(user.getUserid());
//	            salesDataList.addAll(userSales);
//	        }
//
//	        return salesDataList;
//	    }
//	public List<Sell> getSalesDataForUserHierarchy(int userId) {
//		List<Sell> salesDataList = new ArrayList<>();
//
//		// Retrieve the root user
//		User rootUser = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//		// Ensure visitedUserIds is not null
//		Set<Integer> visitedUserIds = new HashSet<>();
//
//		// Get all users in the hierarchy
//		List<User> hierarchicalUsers = getHierarchicalUsers(rootUser, visitedUserIds);
//
//		// Retrieve sales data for all users in the hierarchy
//		for (User user : hierarchicalUsers) {
//
//			List<Sell> userSales = sRepo.findBySoldbyUserId(user.getUserid());
//			salesDataList.addAll(userSales);
//		}
//
//		// Retrieve the direct subagents for this user (lower-level employees not
//		// captured by hierarchy)
//		List<User> subagents = uRepo.findSubagentByUserid(userId);
//		for (User subagent : subagents) {
//			List<Sell> subagentSales = sRepo.findBySoldbyUserId(subagent.getUserid());
//			System.out.println(subagent.getUserid());
//			salesDataList.addAll(subagentSales);
//		}
//
//		return salesDataList;
//	}
	
	
	public List<Sell> getSalesDataForUserHierarchy(int userId) {
        List<Sell> salesDataList = new ArrayList<>();

        // Retrieve the root user
        User rootUser = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // Get users in the hierarchy and their sales
        List<User> hierarchicalUsers = getHierarchicalUsers(rootUser, new HashSet<>());

        for (User user : hierarchicalUsers) {
            List<Sell> userSales = sRepo.findBySoldbyUserId(user.getUserid());
            salesDataList.addAll(userSales);
        }

        // Retrieve sales for direct subagents managed by this user
        List<User> subagents = uRepo.findSubagentByUserid(userId);
        for (User subagent : subagents) {
            List<Sell> subagentSales = sRepo.findBySoldbyUserId(subagent.getUserid());
            salesDataList.addAll(subagentSales);
        }

        return salesDataList;
    }

    private List<User> getHierarchicalUsers(User rootUser, Set<Integer> visitedUserIds) {
        List<User> hierarchicalUsers = new ArrayList<>();

        // If this user has already been visited, we have a cycle, so return to avoid recursion
        if (visitedUserIds.contains(rootUser.getUserid())) {
            return hierarchicalUsers;
        }

        // Mark this user as visited
        visitedUserIds.add(rootUser.getUserid());

        // Get direct subordinates
        List<User> directSubordinates = uRepo.findByManageBy(rootUser.getUserid());

        for (User subordinate : directSubordinates) {
            hierarchicalUsers.add(subordinate);
            // Recursive call, passing the updated set of visited user IDs
            hierarchicalUsers.addAll(getHierarchicalUsers(subordinate, visitedUserIds));
        }

        return hierarchicalUsers;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "Unknown";
        }
        return DATE_FORMAT.format(date);
    }

    public Map<String, Object> populateCommissionDetails(Sell sell, User user) {
        if (sell == null) {
            throw new IllegalArgumentException("Sell object cannot be null");
        }

        Map<String, Object> commissionDetails = new HashMap<>();
        commissionDetails.put("salestatus", safeGet(sell.getSalestatus(), "Unknown"));
        commissionDetails.put("username", safeGet(sell.getUsername(), "Unknown"));
        commissionDetails.put("userrole", safeGet(sell.getUserrole(), "Unknown"));
        commissionDetails.put("date", formatDate(sell.getRegisterDate()));
        commissionDetails.put("transactionId", safeGet(sell.getSellid(), -1));
        commissionDetails.put("productname", safeGet(sell.getProductname(), "Unknown"));
        commissionDetails.put("payment_received", safeGet(sell.getSaleamount(), 0.0));

        // Commission amount and rate based on the role hierarchy
//        double commissionAmount = getCommissionAmount(sell, user);
        
        double commissionRate = getCommissionRate(sell, user);
        double commissionAmount = (sell.getSaleamount()*getCommissionRate(sell, user))/100;
//        System.out.println(commissionAmount);

        commissionDetails.put("commissionAmount", formatDouble(commissionAmount));
        commissionDetails.put("commissionRate", commissionRate);

        return commissionDetails;
    }

    private double getCommissionAmount(Sell sell, User user) {
        if (user == null) {
            return 0.0;
        }

        String userRole1 = safeGet(user.getRole(), "");
        switch (userRole1) {
            case "COUNTRYHEAD":
                return safeGet(sell.getCHcomm(), 0.0);
            case "STATEHEAD":
                return safeGet(sell.getSTcomm(), 0.0);
            case "DISTRICTHEAD":
                return safeGet(sell.getDHcomm(), 0.0);
            case "CITYHEAD":
                return safeGet(sell.getCityhcomm(), 0.0);
            case "AGENT":
                return safeGet(sell.getAcomm(), 0.0);
            case "SUBAGENT":
                return safeGet(sell.getSAcomm(), 0.0);
            default:
                return 0.0;
        }
    }

    private double getCommissionRate(Sell sell, User user) {
        try {
            Product product = pRepo.findProductByPname(safeGet(sell.getProductname(), ""));

            if (product == null) {
                return 0.0;
            }

            switch (user.getRole()) {
                case "AGENT":
                    return safeGet(product.getAcomm(), 0.0);
                case "SUBAGENT":
                    return safeGet(product.getSAcomm(), 0.0);
                case "CITYHEAD":
                    return safeGet(product.getCityhcomm(), 0.0);
                case "DISTRICTHEAD":
                    return safeGet(product.getDHcomm(), 0.0);
                case "STATEHEAD":
                    return safeGet(product.getSTcomm(), 0.0);
                case "COUNTRYHEAD":
                    return safeGet(product.getCHcomm(), 0.0);
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private <T> T safeGet(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    public Map<String, Object> populateCommissionDetails(Sell row) {
        Map<String, Object> commissionDetails = new HashMap<>();
        commissionDetails.put("salestatus", safeGet(row.getSalestatus(), "Unknown"));
        commissionDetails.put("username", safeGet(row.getUsername(), "Unknown"));
        commissionDetails.put("userrole", safeGet(row.getUserrole(), "Unknown"));
        commissionDetails.put("date", formatDate(row.getRegisterDate()));
        commissionDetails.put("transactionId", safeGet(row.getSellid(), -1));
        commissionDetails.put("productname", safeGet(row.getProductname(), "Unknown"));
        commissionDetails.put("payment_received", safeGet(row.getSaleamount(), 0.0));

        double commissionAmount = (row.getSaleamount()*getCommissionRate(row))/100;
        commissionDetails.put("commissionAmount", commissionAmount);
        double commissionRate = getCommissionRate(row);
        commissionDetails.put("commissionRate", commissionRate);

        return commissionDetails;
    }

    private double getCommissionAmount(Sell row) {
        switch (row.getUserrole()) {
            case "AGENT":
                return safeGet(row.getAcomm(), 0.0);
            case "SUBAGENT":
                return safeGet(row.getSAcomm(), 0.0);
            case "CITYHEAD":
                return safeGet(row.getCityhcomm(), 0.0);
            case "DISTRICTHEAD":
                return safeGet(row.getDHcomm(), 0.0);
            case "STATEHEAD":
                return safeGet(row.getSTcomm(), 0.0);
            case "COUNTRYHEAD":
                return safeGet(row.getCHcomm(), 0.0);
            default:
                return 0.0;
        }
    }

    private double getCommissionRate(Sell row) {
        try {
            Product product = pRepo.findProductByPname(safeGet(row.getProductname(), ""));

            if (product == null) {
                return 0.0;
            }

            switch (row.getUserrole()) {
                case "AGENT":
                    return safeGet(product.getAcomm(), 0.0);
                case "SUBAGENT":
                    return safeGet(product.getSAcomm(), 0.0);
                case "CITYHEAD":
                    return safeGet(product.getCityhcomm(), 0.0);
                case "DISTRICTHEAD":
                    return safeGet(product.getDHcomm(), 0.0);
                case "STATEHEAD":
                    return safeGet(product.getSTcomm(), 0.0);
                case "COUNTRYHEAD":
                    return safeGet(product.getCHcomm(), 0.0);
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

	public List<Map<String, Object>> getAllUserIdAndRole() {
		List<User> users = uRepo.findAll();

		// Explicitly declare the lambda expression's expected output
		Function<User, Map<String, Object>> userToMap = user -> {
			Map<String, Object> map = new HashMap<>();
			map.put("Userid", user.getUserid());
			map.put("Userrole", user.getRole());
			map.put("Username", user.getUsername());
			return map;
		};

		return users.stream().map(userToMap) // Explicitly using the Function
				.collect(Collectors.toList());
	}

	public List<Sell> getSalesByUserId(int userId) {
		return sRepo.findBySoldbyUserId(userId);
	}

	public List<Sell> getAllSalesData() {
		return sRepo.findAll();
	}

	public Optional<User> findUserById(int userId) {
		return uRepo.findById(userId);
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}


	
//	public List<Map<String, Object>> getClientMonitoringData(int userId) throws ParseException {
//	    // Fetch user by ID
//	    Optional<User> userOptional = uRepo.findById(userId);
//	    if (!userOptional.isPresent()) {
//	        throw new IllegalArgumentException("User not found with ID: " + userId);
//	    }
//	    User user = userOptional.get();
//
//	    // Check if the user is an AGENT or SUBAGENT
//	    if (!"AGENT".equalsIgnoreCase(user.getRole()) && !"SUBAGENT".equalsIgnoreCase(user.getRole())) {
//	        throw new IllegalArgumentException("User is not an agent or subagent");
//	    }
//
//	    List<Map<String, Object>> monitoringData = new ArrayList<>();
//
//	    Map<String, Object> userMap = new HashMap<>();
//	    userMap.put("userid", user.getUserid());
//	    userMap.put("username", user.getUsername());
//	    userMap.put("email", user.getUseremail());
//	    userMap.put("usermobile", user.getUsermobile());
////	    userMap.put("state", user.getState());
////	    userMap.put("district", user.getDistrict());
////	    userMap.put("cityorvillage", user.getCityorvillage());
//
//	    // Product information
//	    if (user.getProducts() != null && !user.getProducts().isEmpty()) {
//	        List<Map<String, Object>> productDetails = new ArrayList<>();
//	        for (Product product : user.getProducts()) {
//	            Map<String, Object> productMap = new HashMap<>();
//	            productMap.put("pname", product.getPname());
//	            productMap.put("pdesc", product.getPdesc());
//	            String formattedDate = product.getSubRenewalDate() != null
//	                    ? DateUtil.dateToString(product.getSubRenewalDate(), "yyyy-MM-dd")
//	                    : null;
//	            productMap.put("subRenewalDate", formattedDate);
//	            productMap.put("subRenewalStatus", product.getSubRenewalStatus());
//	            productDetails.add(productMap);
//	        }
//	        userMap.put("products", productDetails);
//	    }
//
//	    // Customer information
//	 // Customer and transaction details
//	    List<Map<String, Object>> customerDetails = new ArrayList<>();
//	    List<Map<String, Object>> transactionDetails = new ArrayList<>();
//	    for (Sell sell : user.getSell()) {
//	        if (sell.getSoldby() != null && sell.getSoldby().getUserid() == userId) {
//	            // Customer information
//	            Customer customer = sell.getCustomer();
//	            if (customer != null) {
//	                Map<String, Object> customerMap = new HashMap<>();
//	                customerMap.put("customername", customer.getCustomername());
//	                customerMap.put("customeremail", customer.getCustomeremail());
//	                customerMap.put("customermobile", customer.getCustomermobile());
//	                customerMap.put("customerstate", customer.getCustomerstate());
//	                customerMap.put("customerdistrict", customer.getCustomerdistrict());
//	                customerMap.put("cutomercityorvillage", customer.getCustomercityorvillage());
//	                customerMap.put("customercommission", sell.getAcomm());
//	                
//	                if ("AGENT".equalsIgnoreCase(user.getRole())) {
//	                    customerMap.put("customercommission", sell.getAcomm());
//	                } else if ("SUBAGENT".equalsIgnoreCase(user.getRole())) {
//	                    customerMap.put("customercommission", sell.getSAcomm());
//	                }
//	                
//	                
//	                int pid = sell.getPid();
//	              
//	                Product product = pService.getProductByPid(pid);
////	                Map<String, Object> customerMap = new HashMap<>();
//	                if (product != null) {
//	                    
//	                    customerMap.put("productname", product.getPname());
//	                    customerMap.put("productdesc", product.getPdesc());
//	                } else {
//	                    System.out.println("Product is null for sell ID: " + sell.getSellid());
//	                }
//	            
//
//	                customerDetails.add(customerMap);
//	            } else {
//	                System.out.println("Customer is null for sell ID: " + sell.getSellid());
//	            }
//
//	            // Transaction details
//	            if (sell.getPayments() != null && !sell.getPayments().isEmpty()) {
//	                for (Payment payment : sell.getPayments()) {
//	                    Map<String, Object> transactionMap = new HashMap<>();
//	                    transactionMap.put("transactionId", payment.getTransactionId());
//	                    transactionMap.put("transactionType", payment.getTransactionType());
//	                    transactionMap.put("transactionDateTime", payment.getTransactionDateTime());
//	                    transactionMap.put("transactionAmount", payment.getTransactionAmount());
//	                    transactionDetails.add(transactionMap);
//	                }
//	            }
//	        }
//	    }
//	    userMap.put("customers", customerDetails);
//	    userMap.put("transactions", transactionDetails);
//	   
//
//	    monitoringData.add(userMap);
//
//	    return monitoringData;
//	}
	
	public List<Map<String, Object>> getClientMonitoringData(int userId) throws ParseException {

	    // Fetch user by ID
	    Optional<User> userOptional = uRepo.findById(userId);
	    if (!userOptional.isPresent()) {
	        throw new IllegalArgumentException("User not found with ID: " + userId);
	    }
	    User user = userOptional.get();

	    // Check if the user is an AGENT or SUBAGENT
	    if (!"AGENT".equalsIgnoreCase(user.getRole()) && !"SUBAGENT".equalsIgnoreCase(user.getRole())) {
	        throw new IllegalArgumentException("User is not an agent or subagent");
	    }

	    List<Map<String, Object>> monitoringData = new ArrayList<>();

	    Map<String, Object> userMap = new HashMap<>();
	    userMap.put("userid", user.getUserid());
	    userMap.put("username", user.getUsername());
	    userMap.put("role", user.getRole());
	    userMap.put("usermobile", user.getUsermobile());
	    // Add other user information as needed

	    // Customer and transaction details
	    List<Map<String, Object>> customerDetails = new ArrayList<>();
	    List<Map<String, Object>> transactionDetails = new ArrayList<>();
	    for (Sell sell : user.getSell()) {
	        if (sell.getSoldby() != null && sell.getSoldby().getUserid() == userId) {
	            // Customer information
	            Customer customer = sell.getCustomer();
	            if (customer != null) {
	                Map<String, Object> customerMap = new HashMap<>();
	                customerMap.put("customername", customer.getCustomername());
	                customerMap.put("cid", customer.getCid());
	                customerMap.put("customeremail", customer.getCustomeremail());
	                customerMap.put("customermobile", customer.getCustomermobile());
	                customerMap.put("customerstate", customer.getCustomerstate());
	                customerMap.put("customerdistrict", customer.getCustomerdistrict());
	                customerMap.put("cutomercityorvillage", customer.getCustomercityorvillage());
	                customerMap.put("customercommission", formatDouble(sell.getAcomm()));

	                if ("AGENT".equalsIgnoreCase(user.getRole())) {
	                    customerMap.put("customercommission", formatDouble(sell.getAcomm()));
	                } else if ("SUBAGENT".equalsIgnoreCase(user.getRole())) {
	                    customerMap.put("customercommission", formatDouble(sell.getSAcomm()));
	                }

	                int pid = sell.getPid();
	                Product product = pService.getProductByPid(pid);
	                
	                if (product != null) {
	                    customerMap.put("productname", product.getPname());
	                    customerMap.put("productdesc", product.getPdesc());
	                    String formattedDate = sell.getRenewaldate() != null
	                            ? DateUtil.dateToString(sell.getRenewaldate(), "yyyy-MM-dd")
	                            : null;
	                    customerMap.put("RenewalDate", formattedDate);
	                    customerMap.put("subRenewalStatus", sell.getRenewalStatus());
	                }
	                else {
	                    System.out.println("Product is null for sell ID: " + sell.getSellid());
	                }

	                customerDetails.add(customerMap);
	            } else {
	                System.out.println("Customer is null for sell ID: " + sell.getSellid());
	            }

//	            System.out.println(";;;");
//	            int transactionId=payment.get
	            
	            List<Payment> payments = paymentRepo.findByCustomer_Cid(customer.getCid());
                for (Payment p : payments) {
                    Map<String, Object> transactionMap = new HashMap<>();
                    transactionMap.put("transactionId", p.getTransactionId());
                    transactionMap.put("transactionstatus", p.getTransactionStatus());
                    String formattedDate = p.getTransactionDate() != null
                            ? DateUtil.dateToString(p.getTransactionDate(), "yyyy-MM-dd")
                            : null;
                    transactionMap.put("transactiondate", formattedDate);
              
                    transactionMap.put("transactionAmount", p.getAmount());
                    transactionMap.put("customerid",p.getCustomer().getCid());
                    transactionDetails.add(transactionMap);
                }
	                }
	            }
		                
	        
	    

	    // Sort customer details by customer name in descending order
	    customerDetails.sort(Comparator.comparing((Map<String, Object> m) -> (Integer) m.get("cid")).reversed());

//	    // Pagination logic
//	    int start = page * record;
//	    int end = Math.min(start + record, customerDetails.size());
//	    List<Map<String, Object>> pagedCustomerDetails = customerDetails.subList(start, end);

	    userMap.put("customers", customerDetails);
	    userMap.put("payments", transactionDetails);

	    monitoringData.add(userMap);

	    return monitoringData;
	}







	
	
//	public List<Map<String, Object>> getCustomersByUserId(int userId) throws ParseException {
//	    // Fetch the user by userId
//		User user = uRepo.findById(userId).orElse(null);
//        if (user == null || (!"AGENT".equalsIgnoreCase(user.getRole()) && !"SUBAGENT".equalsIgnoreCase(user.getRole()))) {
//            // Return an empty list if the user is not found or not an agent/subagent
//            return new ArrayList<>();
//        }
//	    
//	    List<Map<String, Object>> monitoringData = new ArrayList<>();
//
//	    Map<String, Object> userMap = new HashMap<>();
//	    userMap.put("username", user.getUsername());
//	    userMap.put("email", user.getUseremail());
//	    userMap.put("usermobile", user.getUsermobile());
//	    userMap.put("state", user.getState());
//	    userMap.put("district", user.getDistrict());
//	    userMap.put("cityorvillage", user.getCityorvillage());
//
//	    // Product information
//	    if (user.getProducts() != null && !user.getProducts().isEmpty()) {
//	        List<Map<String, Object>> productDetails = new ArrayList<>();
//	        for (Product product : user.getProducts()) {
//	            Map<String, Object> productMap = new HashMap<>();
//	            productMap.put("pname", product.getPname());
//	            productMap.put("pdesc", product.getPdesc());
//	            String formattedDate = product.getSubRenewalDate() != null
//	                    ? DateUtil.dateToString(product.getSubRenewalDate(), "yyyy-MM-dd")
//	                    : null;
//	            productMap.put("subRenewalDate", formattedDate);
//	            productMap.put("subRenewalStatus", product.getSubRenewalStatus());
//	            productDetails.add(productMap);
//	        }
//	        userMap.put("products", productDetails);
//	    }
//
//	    
//	    List<Map<String, Object>> customerDetails = new ArrayList<>();
//
//        // Customer information
//        if (user.getSell() != null && !user.getSell().isEmpty()) {
//            for (Sell sell : user.getSell()) {
//                if (sell.getCustomer() != null) {
//                    Map<String, Object> customerMap = new HashMap<>();
//                    Customer customer = sell.getCustomer();
//                    customerMap.put("customername", customer.getCustomername());
//                    customerMap.put("customeremail", customer.getCustomeremail());
//                    customerMap.put("customermobile", customer.getCustomermobile());
//                    customerMap.put("customeraddress", customer.getCustomeraddress());
//                    customerMap.put("customercityorvillage", customer.getCustomercityorvillage());
//                    customerMap.put("customerstate", customer.getCustomerstate());
//                    customerMap.put("customerdistrict", customer.getCustomerdistrict());
//                    customerMap.put("totalspend", customer.getTotalspend());
//                    customerDetails.add(customerMap);
//                }
//            }
//	    }
////	    if (user.getSell() != null && !user.getSell().isEmpty()) {
////	        List<Map<String, Object>> transactionDetails = new ArrayList<>();
////	        double totalCommission = 0.0; // Initialize total commission
////
////	        for (Sell sell : user.getSell()) {
////	            if (sell.getCustomer() != null) {
////	                Map<String, Object> customerMap = new HashMap<>();
////	                Customer customer = sell.getCustomer();
////	                customerMap.put("customername", customer.getCustomername());
////	                customerMap.put("customeremail", customer.getCustomeremail());
////	                customerMap.put("customermobile", customer.getCustomermobile());
////	                transactionDetails.add(customerMap);
////	            }
////
////	            // Calculate total commission for each sell and accumulate
////	            totalCommission += sell.getAcomm();
////
////	            // Other transaction details...
////	        }
////
////	        // Add total commission to user map
////	        userMap.put("totalCommission", totalCommission);
////
////	        // Add other transaction details...
////	        userMap.put("transactions", transactionDetails);
////	    }
//
//	    monitoringData.add(userMap);
//
//	    return monitoringData;
//	}
	
	

	

	    
	



	
	



	//new added
//	public List<User> getUsersByRole(String role) {
//        return uRepo.findByRole(role);
//    }
	public List<Map<String, Object>> getUsersByRole(String role) {
	    try {
	    	System.out.println("hi"+role);
	        List<User> usersByRole = uRepo.findAllByRole(role);
	        System.out.println(usersByRole);
	        return processUsersList(usersByRole);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}

	private List<Map<String, Object>> processUsersList(List<User> users) {
	    List<Map<String, Object>> listOfMember = new ArrayList<>();

	    for (User user : users) {
	        Map<String, Object> memberAndSaleData = new HashMap<>();

	        // Add member data to the combined map
	        memberAndSaleData.put("userId", user.getUserid());
	        memberAndSaleData.put("username", user.getUsername());
	        memberAndSaleData.put("mobile", user.getUsermobile());
	        memberAndSaleData.put("totalEarning", user.getTotalCommissionAmount());
	        memberAndSaleData.put("role", user.getRole());

	        List<Sell> sellList = user.getSell();

	        // Group sales by month, quarter, and year
	        Map<String, Double> monthlySales = new HashMap<>();
	        Map<String, Double> quarterlySales = new HashMap<>();
	        Map<String, Double> yearlySales = new HashMap<>();

	        for (Sell sell : sellList) {
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(sell.getApprovedDate());

	            int year = calendar.get(Calendar.YEAR);
	            int month = calendar.get(Calendar.MONTH) + 1;
	            int quarter = (month - 1) / 3 + 1;

	            String monthKey = year + "-" + month;
	            String quarterKey = year + "-Q" + quarter;
	            String yearKey = String.valueOf(year);

	            // Update monthly sales
	            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
	            // Update quarterly sales
	            quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
	            // Update yearly sales
	            yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
	        }

	        // Add sales data to the combined map
	        memberAndSaleData.put("monthlySales", monthlySales);
	        memberAndSaleData.put("quarterlySales", quarterlySales);
	        memberAndSaleData.put("yearlySales", yearlySales);

	        // Add the combined data for the user to the list of members
	        listOfMember.add(memberAndSaleData);
	    }

	    return listOfMember;
	}


//	public List<User> getSubagentsByManager(Integer managerId) {
//    return uRepo.findByManageBy(managerId);
//}
	
	 public List<Map<String, Object>> getSubagentsByManager(Integer managerId) {
	        List<User> subagents = uRepo.findByManageBy(managerId);
	        return subagents.stream().map(this::mapUserToLimitedFields).collect(Collectors.toList());
	    }

	  private Map<String, Object> mapUserToLimitedFields(User user) {
	        Map<String, Object> limitedUserMap = new HashMap<>();
	        limitedUserMap.put("userid", user.getUserid());
	        limitedUserMap.put("username", user.getUsername());
	        limitedUserMap.put("usermobile", user.getUsermobile());
	        limitedUserMap.put("role", user.getRole());
	        limitedUserMap.put("useremailId", user.getUseremail());
	        
	        String formattedDate;
			try {
				formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
				formattedDate = DateUtil.dateToString(user.getUpdateddate(), "yyyy-MM-dd");
			} catch (ParseException e) {
				formattedDate = null; // Handle error appropriately
			}
//			filteredUser.put("registerDate", formattedDate);
	        limitedUserMap.put("createddate", formattedDate);
	        limitedUserMap.put("updateddate", formattedDate);
	        limitedUserMap.put("isSubUser", user.isIsSubUser());
	        limitedUserMap.put("firstTimeLogin", user.isFirstTimeLogin());
	        limitedUserMap.put("usDisabled", user.isUsDisabled());
	        limitedUserMap.put("manageBy", user.getManageBy());
	        limitedUserMap.put("totalCommissionAmount", user.getTotalCommissionAmount());
	        limitedUserMap.put("active", user.getActive());
	        return limitedUserMap;
	    }


	public Map<String, Double> getUserRoleCounts() {
        Map<String, Double> roleCounts = new HashMap<>();
        roleCounts.put("STATEHEAD", uRepo.countByRole("STATEHEAD"));
        roleCounts.put("DISTRICTHEAD", uRepo.countByRole("DISTRICTHEAD"));
        roleCounts.put("CITYHEAD", uRepo.countByRole("CITYHEAD"));
        roleCounts.put("AGENT", uRepo.countByRole("AGENT"));
        roleCounts.put("SUBAGENT", uRepo.countByRole("SUBAGENT"));
        roleCounts.put("COUNTRYHEAD", uRepo.countByRole("COUNTRYHEAD"));
        // Add more roles if needed
        return roleCounts;
    }


	public Map<String, Double> getEntityCounts() {
        Map<String, Double> entityCounts = new HashMap<>();
        entityCounts.put("Product", (double) pRepo.count());
        entityCounts.put("ProductCategory", (double) pRepo.count());
        entityCounts.put("ProductType", (double) pRepo.count());
        return entityCounts;
    }
	
//	public Map< String, Double> groupAndCalculateSales() {
//        List<Sell> sellList =sRepo.findAll();
//
//        Map<String, Double> monthlySales = new HashMap<>();
//        Map<String, Double> quarterlySales = new HashMap<>();
//        Map<String, Double> yearlySales = new HashMap<>();
//
//        for (Sell sell : sellList) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(sell.getApprovedDate());
//
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int quarter = (month - 1) / 3 + 1;
//
//            String monthKey = year + "-" + month;
//            String quarterKey = year + "-Q" + quarter;
//            String yearKey = String.valueOf(year);
//
//            // Update monthly sales
//            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//            // Update quarterly sales
//            quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//            // Update yearly sales
//            yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//        }
//
//        // Calculate the total sales for each category
//        double totalMonthlySales = 0.0;
//        for (double amount : monthlySales.values()) {
//            totalMonthlySales += amount;
//        }
//
//        double totalQuarterlySales = 0.0;
//        for (double amount : quarterlySales.values()) {
//            totalQuarterlySales += amount;
//        }
//
//        double totalYearlySales = 0.0;
//        for (double amount : yearlySales.values()) {
//            totalYearlySales += amount;
//        }
//
//        // Create a map of totals to return
//        Map<String, Double> totalSalesMap = new HashMap<>();
//        totalSalesMap.put("totalMonthlySales", totalMonthlySales);
//        totalSalesMap.put("totalQuarterlySales", totalQuarterlySales);
//        totalSalesMap.put("totalYearlySales", totalYearlySales);
//
//        return totalSalesMap;
//    }
	
	public Map<String, Double> groupAndCalculateSales() {
	    List<Sell> sellList = sRepo.findAll();

	    Map<String, Double> monthlySales = new HashMap<>();
	    Map<String, Double> quarterlySales = new HashMap<>();
	    Map<String, Double> yearlySales = new HashMap<>();

	    for (Sell sell : sellList) {
	        if (sell.getApprovedDate() != null) { // Add a null check
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(sell.getApprovedDate());

	            int year = calendar.get(Calendar.YEAR);
	            int month = calendar.get(Calendar.MONTH) + 1;
	            int quarter = (month - 1) / 3 + 1;

	            String monthKey = year + "-" + month;
	            String quarterKey = year + "-Q" + quarter;
	            String yearKey = String.valueOf(year);

	            // Update monthly sales
	            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
	            // Update quarterly sales
	            quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
	            // Update yearly sales
	            yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
	        }
	    }

	    // Calculate the total sales for each category
	    double totalMonthlySales = monthlySales.values().stream().mapToDouble(Double::doubleValue).sum();
	    double totalQuarterlySales = quarterlySales.values().stream().mapToDouble(Double::doubleValue).sum();
	    double totalYearlySales = yearlySales.values().stream().mapToDouble(Double::doubleValue).sum();

	    // Create a map of totals to return
	    Map<String, Double> totalSalesMap = new HashMap<>();
	    totalSalesMap.put("totalMonthlySales", totalMonthlySales);
	    totalSalesMap.put("totalQuarterlySales", totalQuarterlySales);
	    totalSalesMap.put("totalYearlySales", totalYearlySales);

	    return totalSalesMap;
	}
	
	
//	public Map<String, Double> groupAndCalculateSales(int userId) {
//	    List<Sell> sellList = sRepo.findBySoldbyUserId(userId); // Assuming a method findByUserId exists in your repository
//
//	    Map<String, Double> monthlySales = new HashMap<>();
//	    Map<String, Double> quarterlySales = new HashMap<>();
//	    Map<String, Double> yearlySales = new HashMap<>();
//
//	    for (Sell sell : sellList) {
//	        if (sell.getApprovedDate() != null) { // Add a null check
//	            Calendar calendar = Calendar.getInstance();
//	            calendar.setTime(sell.getApprovedDate());
//
//	            int year = calendar.get(Calendar.YEAR);
//	            int month = calendar.get(Calendar.MONTH) + 1;
//	            int quarter = (month - 1) / 3 + 1;
//
//	            String monthKey = year + "-" + month;
//	            String quarterKey = year + "-Q" + quarter;
//	            String yearKey = String.valueOf(year);
//
//	            // Update monthly sales
//	            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//	            // Update quarterly sales
//	            quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//	            // Update yearly sales
//	            yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//	        }
//	    }
//
//	    // Calculate the total sales for each category
//	    double totalMonthlySales = monthlySales.values().stream().mapToDouble(Double::doubleValue).sum();
//	    double totalQuarterlySales = quarterlySales.values().stream().mapToDouble(Double::doubleValue).sum();
//	    double totalYearlySales = yearlySales.values().stream().mapToDouble(Double::doubleValue).sum();
//
//	    // Create a map of totals to return
//	    Map<String, Double> totalSalesMap = new HashMap<>();
//	    totalSalesMap.put("totalMonthlySales", totalMonthlySales);
//	    totalSalesMap.put("totalQuarterlySales", totalQuarterlySales);
//	    totalSalesMap.put("totalYearlySales", totalYearlySales);
//
//	    return totalSalesMap;
//	}
	public Map<String, Double> getSalesTotalsForUserHierarchy(int userId) {
        // Initialize the total sales map
        Map<String, Double> totalSalesMap = new HashMap<>();
        totalSalesMap.put("totalMonthlySales", 0.0);
        totalSalesMap.put("totalQuarterlySales", 0.0);
        totalSalesMap.put("totalYearlySales", 0.0);

        // Start traversal from the given userId (country head or any head)
        traverseUserHierarchy(userId, totalSalesMap);

        return totalSalesMap;
    }

    private void traverseUserHierarchy(int userId, Map<String, Double> totalSalesMap) {
        // Get the user role
        User user = uRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        String role = user.getRole();

        if (role.equals(User.ROLE_AGENT) || role.equals(User.ROLE_SUB_AGENT)) {
            // Aggregate sales for agents and sub-agents only
            Map<String, Double> userSales = groupAndCalculateSalesForUserAndSubordinates(userId);
            totalSalesMap.put("totalMonthlySales", totalSalesMap.get("totalMonthlySales") + userSales.get("totalMonthlySales"));
            totalSalesMap.put("totalQuarterlySales", totalSalesMap.get("totalQuarterlySales") + userSales.get("totalQuarterlySales"));
            totalSalesMap.put("totalYearlySales", totalSalesMap.get("totalYearlySales") + userSales.get("totalYearlySales"));
        } else {
            // Recursively traverse the user hierarchy
            List<User> managedUsers = uRepo.findByManageBy(userId);
            for (User managedUser : managedUsers) {
                traverseUserHierarchy(managedUser.getUserid(), totalSalesMap);
            }
        }
    }

    private Map<String, Double> groupAndCalculateSalesForUserAndSubordinates(int userId) {
        // Aggregate sales for the user
        Map<String, Double> totalSalesMap = groupAndCalculateSales(userId);
        
     // Number format for Indian Rupees
        Locale indiaLocale = new Locale("en", "IN");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);
    

        // Find the sub-agents managed by the agent
        List<User> subAgents = uRepo.findByRoleAndManageBy( User.ROLE_SUB_AGENT,userId);
        for (User subAgent : subAgents) {
            // Aggregate sales for the sub-agent
            Map<String, Double> subAgentSales = groupAndCalculateSales(subAgent.getUserid());
            
            totalSalesMap.put("totalMonthlySales", totalSalesMap.get("totalMonthlySales") + (subAgentSales.get("totalMonthlySales")));
            totalSalesMap.put("totalQuarterlySales", totalSalesMap.get("totalQuarterlySales") + subAgentSales.get("totalQuarterlySales"));
            totalSalesMap.put("totalYearlySales", totalSalesMap.get("totalYearlySales") + subAgentSales.get("totalYearlySales"));
        }

        return totalSalesMap;
    }

    public Map<String, Double> groupAndCalculateSales(int userId) {
        List<Sell> sellList = sRepo.findBySoldbyUserId(userId);

        Map<String, Double> monthlySales = new HashMap<>();
        Map<String, Double> quarterlySales = new HashMap<>();
        Map<String, Double> yearlySales = new HashMap<>();

        for (Sell sell : sellList) {
            if (sell.getApprovedDate() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sell.getApprovedDate());

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int quarter = (month - 1) / 3 + 1;

                String monthKey = year + "-" + month;
                String quarterKey = year + "-Q" + quarter;
                String yearKey = String.valueOf(year);

                monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
                quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
                yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
            }
        }

        double totalMonthlySales = monthlySales.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalQuarterlySales = quarterlySales.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalYearlySales = yearlySales.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Double> totalSalesMap = new HashMap<>();
        totalSalesMap.put("totalMonthlySales", Double.parseDouble(formatDouble(totalMonthlySales)));
        totalSalesMap.put("totalQuarterlySales", Double.parseDouble(formatDouble(totalQuarterlySales)));
        totalSalesMap.put("totalYearlySales", Double.parseDouble(formatDouble(totalYearlySales)));

        return totalSalesMap;
    }


	public double getUserCountByManager(int managerId) {
        
        List<User> users = uRepo.findByManageBy(managerId);
        return users.size();
    }


	public Map<String, Double> getproductcount() {
		
	        Map<String, Double> entityCounts = new HashMap<>();
	        entityCounts.put("Product", (double) pRepo.count());
//	        entityCounts.put("ProductCategory", (double) pRepo.count());
//	        entityCounts.put("ProductType", (double) pRepo.count());
	        return entityCounts;
	    
	}
	
	public double getProductsSoldByUser(int userId) {
        List<Sell> sells = sRepo.findBySoldbyUserId(userId);
        List<Product> products = new ArrayList<>();
        for (Sell sell : sells) {
            products.add(sell.getProduct());
        }
        return products.size();
    }


	public List<User> getAgentsAndSubagents() {
        List<String> agentRoles = Arrays.asList("AGENT", "SUBAGENT");
        return uRepo.findByRoleIn(agentRoles);
    }


	public void updateKYCStatus(Integer userid, String status) throws IllegalArgumentException {
        if (userid == null || status == null) {
            throw new IllegalArgumentException("User ID or KYC status is missing");
        }

        User user = uRepo.findById(userid).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!"Approved".equalsIgnoreCase(status) && !"UnApproved".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }

        user.setKYCstatus(status);
        uRepo.save(user);
    }

//	public void updateUserYearlyTarget(int userId, double targetAmount) {
//       
//		User user = uRepo.findById(userId).orElse(null);
//		
//        
//        if (user != null) {
//            user.setYearlyTarget(targetAmount);
//            user.setRemainigPercentage(100);
//            user.setMonthlyTarget(targetAmount/12);
//            user.setQuarterlyTarget(targetAmount/4);
//            user.setHalfYearlyTarget(targetAmount/2);
//            uRepo.save(user);
//        } else {
//            // Handle case where user with given ID is not found
//            throw new RuntimeException("User not found with ID: " + userId);
//        }
//    }
	
	public String updateUserYearlyTarget(int userId, double targetAmount) {
	    // Retrieve the user by ID
	    User user = uRepo.findById(userId).orElse(null);

	    if (user != null) {
	        // Update the target amounts for the user
	        user.setYearlyTarget(targetAmount);
	        user.setRemainigPercentage(100);
	        user.setMonthlyTarget(targetAmount / 12);
	        user.setQuarterlyTarget(targetAmount / 4);
	        user.setHalfYearlyTarget(targetAmount / 2);
	        uRepo.save(user);

	        // Retrieve all users managed by this user
	        List<User> managedUsers = uRepo.findByManageBy(userId);
	        
	        // Set assignTarget to 0 for all managed users
	        for (User managedUser : managedUsers) {
	            managedUser.setAssignpercentage(0);
	        }
	        
	        // Save the updates to managed users
	        uRepo.saveAll(managedUsers);

	        // Return the updated yearly target amount
	        return formatDouble(user.getYearlyTarget());
	    } else {
	        // Handle case where user with given ID is not found
	        throw new RuntimeException("User not found with ID: " + userId);
	    }
	}


//	public static Map<String, Double> groupAndCalculateSales(List<Sell> sellList, Map<String, Object> memberAndSaleData) {
//        Map<String, Double> monthlySales = new HashMap<>();
//        Map<String, Double> quarterlySales = new HashMap<>();
//        Map<String, Double> yearlySales = new HashMap<>();
//
//        for (Sell sell : sellList) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(sell.getApprovedDate());
//
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int quarter = (month - 1) / 3 + 1;
//
//            String monthKey = year + "-" + month;
//            String quarterKey = year + "-Q" + quarter;
//            String yearKey = String.valueOf(year);
//
//            // Update monthly sales
//            monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//            // Update quarterly sales
//            quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//            // Update yearly sales
//            yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//        }
//
//        // Calculate the total sales for each category
//        double totalMonthlySales = 0.0;
//        for (double amount : monthlySales.values()) {
//            totalMonthlySales += amount;
//        }
//
//        double totalQuarterlySales = 0.0;
//        for (double amount : quarterlySales.values()) {
//            totalQuarterlySales += amount;
//        }
//
//        double totalYearlySales = 0.0;
//        for (double amount : yearlySales.values()) {
//            totalYearlySales += amount;
//        }
//
//        
//
//        
//
//        // Create a map of totals to return
//        Map<String, Double> totalSalesMap = new HashMap<>();
//        totalSalesMap.put("totalMonthlySales", totalMonthlySales);
//        totalSalesMap.put("totalQuarterlySales", totalQuarterlySales);
//        totalSalesMap.put("totalYearlySales", totalYeasrlySales);
//
//        return totalSalesMap;
//    }


	


//	public String updateUserYearlyTargetForUser(int userId, double percentage, Integer loggedInUserId) {
//	    User loggedUser = uRepo.findByUserid(loggedInUserId);
//	    if (loggedUser == null) {
//	        throw new RuntimeException("Logged in user not found with ID: " + loggedInUserId);
//	    }
//
//	    User user = uRepo.findByUserid(userId);
//	    if (user == null) {
//	        throw new RuntimeException("User not found with ID: " + userId);
//	    }
//
//	    if (percentage <= loggedUser.getRemainigPercentage()) {
//	        double amount = (percentage * loggedUser.getMonthlyTarget()) / 100;
//	        loggedUser.setRemainigPercentage(loggedUser.getRemainigPercentage()-percentage);
//	        
//	        user.setAssignpercentage(percentage);
//	        user.setYearlyTarget(amount*12);
//	        user.setRemainigPercentage(100);
//	        user.setMonthlyTarget(amount);
//	        user.setHalfYearlyTarget(amount / 2);
//	        user.setQuarterlyTarget(amount / 4);
//
//	        uRepo.save(loggedUser);
//	        uRepo.save(user);
//	        return "success"; // Success
//	    } else {
//	        return "percentage must be less than " + loggedUser.getRemainigPercentage(); // Failure
//	    }
//	}
	
//	public Map<String, Object> updateUserYearlyTargetForUser(int userId, double percentage, Integer loggedInUserId) {
//	    User loggedUser = uRepo.findByUserid(loggedInUserId);
//	    if (loggedUser == null) {
//	        throw new RuntimeException("Logged in user not found with ID: " + loggedInUserId);
//	    }
//
//	    User user = uRepo.findByUserid(userId);
//	    if (user == null) {
//	        throw new RuntimeException("User not found with ID: " + userId);
//	    }
//
//	    Map<String, Object> response = new HashMap<>();
//	    if (percentage <= loggedUser.getRemainigPercentage()) {
//	        double amount = (percentage * loggedUser.getMonthlyTarget()) / 100;
//	        loggedUser.setRemainigPercentage(loggedUser.getRemainigPercentage() - percentage);
//
//	        user.setAssignpercentage(percentage);
//	        user.setYearlyTarget(amount * 12);
//	        user.setRemainigPercentage(100);
//	        user.setMonthlyTarget(amount);
//	        user.setHalfYearlyTarget(amount / 2);
//	        user.setQuarterlyTarget(amount / 4);
//
//	        uRepo.save(loggedUser);
//	        uRepo.save(user);
//
//	        response.put("status", "success");
//	        response.put("message", "monthly target updated successfully");
//	        response.put("amount", amount);
//	    } else {
//	        response.put("status", "failure");
//	        response.put("message", "Percentage must be less than " + loggedUser.getRemainigPercentage());
//	        response.put("amount", 0);
//	    }
//
//	    return response;
//	}
	
	public Map<String, Object> updateUserYearlyTargetForUser(int userId, double percentage, Integer loggedInUserId) {
	    User loggedUser = uRepo.findByUserid(loggedInUserId);
	    if (loggedUser == null) {
	        throw new RuntimeException("Logged in user not found with ID: " + loggedInUserId);
	    }

	    User user = uRepo.findByUserid(userId);
	    if (user == null) {
	        throw new RuntimeException("User not found with ID: " + userId);
	    }

	    Map<String, Object> response = new HashMap<>();
	    if (percentage <= loggedUser.getRemainigPercentage()) {
	        double amount = (percentage * loggedUser.getMonthlyTarget()) / 100;
	        loggedUser.setRemainigPercentage(loggedUser.getRemainigPercentage() - percentage);

	        user.setAssignpercentage(loggedUser.getRemainigPercentage());
	        user.setYearlyTarget(amount * 12);
	        user.setRemainigPercentage(100);
	        user.setMonthlyTarget(amount);
	        user.setHalfYearlyTarget(amount * 6); // Half-yearly is six months, so multiply by 6
	        user.setQuarterlyTarget(amount * 4);  // Quarterly is three months, so multiply by 3

	        uRepo.save(loggedUser);
	        uRepo.save(user);

	        // Retrieve all users managed by this user and set their assignTarget to 0
	        List<User> managedUsers = uRepo.findByManageBy(userId);
	        for (User managedUser : managedUsers) {
	            managedUser.setAssignpercentage(0);
	        }
	        uRepo.saveAll(managedUsers);

	        response.put("status", "success");
	        response.put("message", "monthly target updated successfully");
	        response.put("amount", formatDouble(amount));
	    } else {
	        response.put("status", "failure");
	        response.put("message", "Percentage must be less than Or equal to  " + loggedUser.getRemainigPercentage());
	        response.put("amount", 0);
	    }

	    return response;
	}




//	public double assignYearlyTargetToAgents(int loggedInUserId) {
//	    // Retrieve all agents managed by the logged-in user
//	    List<User> agentsOrSubagents = uRepo.findByManageBy(loggedInUserId);
//
//	    // Retrieve the logged-in user's yearly target
//	    User loggedInUser = uRepo.findByUserid(loggedInUserId);
//	    double yearlyTarget = loggedInUser.getMonthlyTarget();
//
//	    // Calculate the yearly target to be assigned to each agent
//	    double targetPerAgent = yearlyTarget / agentsOrSubagents.size();
//	    System.out.println(targetPerAgent);
//
//	    // Update the yearly target for each agent
//	    for (User agent : agentsOrSubagents) {
//	        agent.setYearlyTarget(targetPerAgent*12);
//	        agent.setHalfYearlyTarget(targetPerAgent / 2);
//	        agent.setQuarterlyTarget(targetPerAgent / 4);
//	        agent.setMonthlyTarget(targetPerAgent);
//	        uRepo.save(agent);
//	    }
//
//	    // Return the calculated target per agent
//	    return targetPerAgent;
//	}
	public double assignYearlyTargetToAgents(int loggedInUserId) {
	    // Retrieve all agents managed by the logged-in user
	    List<User> agentsOrSubagents = uRepo.findByManageBy(loggedInUserId);

	    // Retrieve the logged-in user's yearly target
	    User loggedInUser = uRepo.findByUserid(loggedInUserId);
	    double yearlyTarget = loggedInUser.getMonthlyTarget(); // Assuming this is the correct yearly target

	    // Calculate the yearly target to be assigned to each agent
	    double targetPerAgent = yearlyTarget / agentsOrSubagents.size();
	    System.out.println(targetPerAgent);

	    // Update the yearly target for each agent
	    for (User agent : agentsOrSubagents) {
	        agent.setYearlyTarget(targetPerAgent*12);
	        agent.setHalfYearlyTarget(targetPerAgent*6);
	        agent.setQuarterlyTarget(targetPerAgent*4);
	        agent.setMonthlyTarget(targetPerAgent);
	        agent.setAssignpercentage(0); // Assuming this is to be set for agents too
	        uRepo.save(agent);

	        // Retrieve all users managed by this agent and set their assignTarget to 0
	        List<User> managedUsers = uRepo.findByManageBy(agent.getUserid());
	        for (User managedUser : managedUsers) {
	            managedUser.setAssignpercentage(0);
	        }
	        uRepo.saveAll(managedUsers);
	    }

	    // Return the calculated target per agent
	    return targetPerAgent;
	}




//	public List<Map<String, Object>> getMyTeamAgentSubagent(int userId) {
//	    try {
//	        List<User> users = uRepo.findByRoleIn(Arrays.asList("AGENT", "SUBAGENT"));
//	        List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//	        for (User user : users) {
//	            Map<String, Object> memberAndSaleData = new HashMap<>();
//
//	            // Add member data to the combined map
//	            memberAndSaleData.put("userId", user.getUserid());
//	            memberAndSaleData.put("username", user.getUsername());
//	            memberAndSaleData.put("mobile", user.getUsermobile());
//	            memberAndSaleData.put("MonthlyTarget", user.getMonthlyTarget());
//	            memberAndSaleData.put("QuarterlyTarget", user.getQuarterlyTarget());
//	            memberAndSaleData.put("HalfyearTarget", user.getHalfYearlyTarget());
//	            memberAndSaleData.put("YearlyTarget", user.getYearlyTarget());
//	            List<Sell> sellList = user.getSell();
//
//	            // Group sales by month, quarter, and year
//	            Map<String, Double> monthlySales = new HashMap<>();
//	            Map<String, Double> quarterlySales = new HashMap<>();
//	            Map<String, Double> yearlySales = new HashMap<>();
////	            List<Map<String, Object>> nullDateSales = new ArrayList<>();
//
//	            for (Sell sell : sellList) {
//	                if (sell.getApprovedDate() != null) {  // Check if approvedDate is not null
//	                    Calendar calendar = Calendar.getInstance();
//	                    calendar.setTime(sell.getApprovedDate());
//
//	                    int year = calendar.get(Calendar.YEAR);
//	                    int month = calendar.get(Calendar.MONTH) + 1;
//	                    int quarter = (month - 1) / 3 + 1;
//
//	                    String monthKey = year + "-" + month;
//	                    String quarterKey = year + "-Q" + quarter;
//	                    String yearKey = String.valueOf(year);
//
//	                    // Update monthly sales
//	                    monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//	                    // Update quarterly sales
//	                    quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//	                    // Update yearly sales
//	                    yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//	                } else {
//	                    // Add sales with null dates to a separate list
//	                    Map<String, Object> saleWithNullDate = new HashMap<>();
//	                    saleWithNullDate.put("saleAmount", sell.getSaleamount());
//	                    saleWithNullDate.put("approvedDate", null);
////	                    nullDateSales.add(saleWithNullDate);
//	                }
//	            }
//
//	            // Add sales data to the combined map
//	            memberAndSaleData.put("AchievedAmount", monthlySales);
////	            memberAndSaleData.put("NullDateSales", nullDateSales);
//	            // memberAndSaleData.put("quarterlySales", quarterlySales);
//	            // memberAndSaleData.put("yearlySales", yearlySales);
//
//	            // Add the combined data for the user to the list of members
//	            listOfMember.add(memberAndSaleData);
//	        }
//
//	        return listOfMember;
//	    } catch (Exception e) {
//	        // Log the exception for further analysis
//	        e.printStackTrace();
//	        // Throw a custom exception or return an empty list based on your requirement
//	        return new ArrayList<>();
//	    }
//	}


	
	
	

	

	
	public List<Map<String, Object>> getMyTeamtargetforadmin(int userId) {
		try {
			List<User> myTeam = uRepo.findAll();
			List<Map<String, Object>> listOfMember = new ArrayList<>();

			for (User user : myTeam) {
				Map<String, Object> memberAndSaleData = new HashMap<>();

				// Add member data to the combined map
				memberAndSaleData.put("userId", user.getUserid());
				memberAndSaleData.put("username", user.getUsername());
				memberAndSaleData.put("mobile", user.getUsermobile());
				memberAndSaleData.put("MonthlyTarget", formatDouble(user.getMonthlyTarget()));
				memberAndSaleData.put("QuarterlyTarget", formatDouble(user.getQuarterlyTarget()));
				memberAndSaleData.put("HalfyearTarget", formatDouble(user.getHalfYearlyTarget()));
				memberAndSaleData.put("YearlyTarget", formatDouble(user.getYearlyTarget()));
				memberAndSaleData.put("Role",user.getRole());
				memberAndSaleData.put("IsAmountAchieved",user.isIsAmountachived());
				List<Sell> sellList = user.getSell();

	            // Group sales by month, quarter, and year
	            Map<String, Double> monthlySales = new HashMap<>();
	            Map<String, Double> quarterlySales = new HashMap<>();
	            Map<String, Double> yearlySales = new HashMap<>();
//	            List<Map<String, Object>> nullDateSales = new ArrayList<>();

	            for (Sell sell : sellList) {
	                if (sell.getApprovedDate() != null) {  // Check if approvedDate is not null
	                    Calendar calendar = Calendar.getInstance();
	                    calendar.setTime(sell.getApprovedDate());

	                    int year = calendar.get(Calendar.YEAR);
	                    int month = calendar.get(Calendar.MONTH) + 1;
	                    int quarter = (month - 1) / 3 + 1;

	                    String monthKey = year + "-" + month;
	                    String quarterKey = year + "-Q" + quarter;
	                    String yearKey = String.valueOf(year);

	                    // Update monthly sales
	                    monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
	                    // Update quarterly sales
	                    quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
	                    // Update yearly sales
	                    yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
	                } else {
	                    // Add sales with null dates to a separate list
	                    Map<String, Object> saleWithNullDate = new HashMap<>();
	                    saleWithNullDate.put("saleAmount", formatDouble(sell.getSaleamount()));
	                    saleWithNullDate.put("approvedDate", null);
//	                    nullDateSales.add(saleWithNullDate);
	                }
	            }

	            // Add sales data to the combined map
	            memberAndSaleData.put("AchievedAmount", Double.parseDouble(formatDouble(user.getTargetAchivedAmount())));
//	            memberAndSaleData.put("NullDateSales", nullDateSales);
	            // memberAndSaleData.put("quarterlySales", quarterlySales);
	            // memberAndSaleData.put("yearlySales", yearlySales);

	            // Add the combined data for the user to the list of members
	            listOfMember.add(memberAndSaleData);
	        }

	        return listOfMember;
	    } catch (Exception e) {
	        // Log the exception for further analysis
	        e.printStackTrace();
	        // Throw a custom exception or return an empty list based on your requirement
	        return new ArrayList<>();
	    }
	}
	
	

	




//	public List<Sell> getAllSalesForUserAndSubordinates(User user) {
//	    List<User> allSubordinates = getSubordinates(user);  // Get all users in the hierarchy
//	    allSubordinates.add(user);  // Include the current user
//
//	    List<Sell> allSales = new ArrayList<>();
//	    for (User u : allSubordinates) {
//	        List<Sell> sales = sRepo.findBysoldby(u);  // Fetch sales for this user
//	        allSales.addAll(sales);
//	    }
//
//	    return allSales;
//	}
//	public List<User> getSubordinates(User user) {
//	    List<User> directSubordinates = uRepo.findByManageBy(user);
//	    List<User> allSubordinates = new ArrayList<>(directSubordinates);
//
//	    for (User subordinate : directSubordinates) {
//	        allSubordinates.addAll(getSubordinates(subordinate));  // Recursive call to get deeper subordinates
//	    }
//
//	    return allSubordinates;
//	}
//
//    public Map<String, Object> populateCommissionDetails(Sell row) {
//        Map<String, Object> commissionDetails = new HashMap<>();
//        commissionDetails.put("salestatus", row.getSalestatus());
//        commissionDetails.put("username", row.getUsername());
//        commissionDetails.put("userrole", row.getUserrole());
//        commissionDetails.put("date", row.getRegisterDate());
//        commissionDetails.put("transactionId", row.getSellid());
//        commissionDetails.put("productname", row.getProductname());
//        commissionDetails.put("payment_received", row.getSaleamount());
//        double commissionAmount = getCommissionAmount(row);
//        commissionDetails.put("commissionAmount", commissionAmount);
//        double commissionRate = getCommissionRate(row);
//        commissionDetails.put("commissionRate", commissionRate);
//        return commissionDetails;
//    }
//
//    // Helper method to get commission amount based on user role
//    private double getCommissionAmount(Sell row) {
//        switch (row.getUserrole()) {
//            case "AGENT":
//                return row.getAcomm();
//            case "SUBAGENT":
//                return row.getSAcomm();
//            case "CITYHEAD":
//                return row.getCityhcomm();
//            case "DISTRICTHEAD":
//                return row.getDHcomm();
//            case "STATEHEAD":
//                return row.getSTcomm();
//            case "COUNTRYHEAD":
//                return row.getCHcomm();
//            default:
//                return 0.0;
//        }
//    }
//
//    // Helper method to get commission rate based on user role
//    private double getCommissionRate(Sell row) {
//        Product product = pRepo.findProductByPname(row.getProductname());
//        if (product == null) {
//            return 0.0;
//        }
//        switch (row.getUserrole()) {
//            case "AGENT":
//                return product.getAcomm();
//            case "SUBAGENT":
//                return product.getSAcomm();
//            case "CITYHEAD":
//                return product.getCityhcomm();
//            case "DISTRICTHEAD":
//                return product.getDHcomm();
//            case "STATEHEAD":
//                return product.getSTcomm();
//            case "COUNTRYHEAD":
//                return product.getCHcomm();
//            default:
//                return 0.0;
//        }
//    }

//		public Map<String, Object> getMyTeamDetails(Integer userId, String username) {
//		    Map<String, Object> userDetails = new HashMap<>();
//		    
//		    // Fetch users based on user ID and username
//		    List<User> users = uRepo.findByUseridAndUsername(userId, username);
//		    
//		    if (!users.isEmpty()) {
//		        List<Map<String, Object>> userList = new ArrayList<>();
//		        for (User user : users) {
//		            Map<String, Object> userMap = new HashMap<>();
//		            userMap.put("username", user.getUsername());
//		            userMap.put("usermobile", user.getUsermobile());
//		            userMap.put("totalCommissionAmount", user.getTotalCommissionAmount());
//		            
//		            // Fetch all withdrawal requests for the user
//		            List<WithdrawalRequest> withdrawalRequests = wRepo.findAllByUser(user);
//		            List<Map<String, Object>> withdrawalRequestList = new ArrayList<>();
//		            for (WithdrawalRequest withdrawalRequest : withdrawalRequests) {
//		                Map<String, Object> withdrawalInfo = new HashMap<>();
//		                withdrawalInfo.put("remainingAmount", withdrawalRequest.getRemainingAmount());
//		                withdrawalInfo.put("amount", withdrawalRequest.getAmount());
//		                withdrawalRequestList.add(withdrawalInfo);
//		            }
//		            userMap.put("withdrawalRequests", withdrawalRequestList);
//		            
//		            userList.add(userMap);
//		        }
//		        userDetails.put("users", userList);
//		    }
//		    
//		    return userDetails;
//		}

//	implementation done by Faisal Vhora.
//	public double getTotalSalesByTeam(Integer countryHeadId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public void monitorTeam(User user) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void manageUser(User user) {
//		// TODO Auto-generated method stub
//		
//	}
	
	public Map<String, Object> getMyTeamforsettarget(int userId) {
	    try {
	        List<User> myTeam = uRepo.findByManageBy(userId);
	        List<Map<String, Object>> listOfMember = new ArrayList<>();

	        User loggedUser = uRepo.findByUserid(userId);
	        
	        if (loggedUser == null) {
	            throw new Exception("Logged user not found for userId: " + userId);
	        }

	        // Number format for Indian Rupees
	        Locale indiaLocale = new Locale("en", "IN");
	        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);

	        for (User user : myTeam) {
	            Map<String, Object> memberAndSaleData = new HashMap<>();

	            // Add member data to the combined map
	            memberAndSaleData.put("userId", user.getUserid());
	            memberAndSaleData.put("username", user.getUsername());
//	            memberAndSaleData.put("mobile", user.getUsermobile());
//	            memberAndSaleData.put("totalEarning", currencyFormat.format(user.getTotalCommissionAmount()));
	            memberAndSaleData.put("role", user.getRole());
	            memberAndSaleData.put("monthlytargetAmount", formatDouble(user.getMonthlyTarget()));
//	            memberAndSaleData.put("Assignpercentage", user.getAssignpercentage());
//	            memberAndSaleData.put("userEmail", user.getUseremail());
	            memberAndSaleData.put("city", user.getCityorvillage());
	            memberAndSaleData.put("District", user.getDistrict());
	            memberAndSaleData.put("State", user.getState());

	            String formattedDate;
	            try {
	                formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
	            } catch (ParseException e) {
	                formattedDate = null; // Handle error appropriately
	            }
	            memberAndSaleData.put("RgisterDate", formattedDate);

	            List<Sell> sellList = user.getSell();

	            // Group sales by month, quarter, and year
	            Map<String, String> monthlySales = new LinkedHashMap<>();
	            Map<String, String> quarterlySales = new LinkedHashMap<>();
	            Map<String, String> yearlySales = new LinkedHashMap<>();

	            for (Sell sell : sellList) {
	                Date approvedDate = sell.getApprovedDate();
	                if (approvedDate != null) {
	                    Calendar calendar = Calendar.getInstance();
	                    calendar.setTime(approvedDate);

	                    int year = calendar.get(Calendar.YEAR);
	                    int month = calendar.get(Calendar.MONTH) + 1;
	                    int quarter = (month - 1) / 3 + 1;

	                    String monthKey = year + "-" + month;
	                    String quarterKey = year + "-Q" + quarter;
	                    String yearKey = String.valueOf(year);

	                    // Format sale amount as currency
	                    String formattedSaleAmount = currencyFormat.format(sell.getSaleamount());

	                    // Update monthly sales
	                    monthlySales.put(monthKey, formattedSaleAmount);

	                    // Update quarterly sales
	                    quarterlySales.put(quarterKey, formattedSaleAmount);

	                    // Update yearly sales
	                    yearlySales.put(yearKey, formattedSaleAmount);
	                } else {
	                    // Handle the null approvedDate scenario here if necessary
	                    System.out.println("Warning: sell record has null approvedDate");
	                }
	            }

	            // Add sales data to the combined map
//	            memberAndSaleData.put("monthlySales", monthlySales);
//	            memberAndSaleData.put("quarterlySales", quarterlySales);
//	            memberAndSaleData.put("yearlySales", yearlySales);

	            // Add the combined data for the user to the list of members
	            listOfMember.add(memberAndSaleData);
	        }

	        // Wrap the list in a map before returning
	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("managedUsers", listOfMember);
	        responseMap.put("loggeduser target", formatDouble(loggedUser.getMonthlyTarget()));

	        return responseMap;
	    } catch (Exception e) {
	        // Log the exception for further analysis
	        e.printStackTrace();
	        // Throw a custom exception or return an empty list based on your requirement
	        return Collections.emptyMap();
	    }
	}
	
	public Map<String, Object> getMyTeamforsettargetforcountryhead(int userId) {
	    try {
	        List<User> myTeam = uRepo.findByManageBy(userId);
	        List<Map<String, Object>> listOfMember = new ArrayList<>();

	        User loggedUser = uRepo.findByUserid(userId);
	        
	        if (loggedUser == null) {
	            throw new Exception("Logged user not found for userId: " + userId);
	        }

	        // Number format for Indian Rupees
	        Locale indiaLocale = new Locale("en", "IN");
	        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(indiaLocale);

	        for (User user : myTeam) {
	            // Skip users with the role "COUNTRYHEAD"
	            if ("COUNTRYHEAD".equals(user.getRole())) {
	                continue;
	            }

	            Map<String, Object> memberAndSaleData = new HashMap<>();

	            // Add member data to the combined map
	            memberAndSaleData.put("userId", user.getUserid());
	            memberAndSaleData.put("username", user.getUsername());
//	            memberAndSaleData.put("mobile", user.getUsermobile());
//	            memberAndSaleData.put("totalEarning", currencyFormat.format(user.getTotalCommissionAmount()));
	            memberAndSaleData.put("role", user.getRole());
	            memberAndSaleData.put("monthlytargetAmount", formatDouble(user.getMonthlyTarget()));
//	            memberAndSaleData.put("Assignpercentage", user.getAssignpercentage());
//	            memberAndSaleData.put("userEmail", user.getUseremail());
	            memberAndSaleData.put("city", user.getCityorvillage());
	            memberAndSaleData.put("District", user.getDistrict());
	            memberAndSaleData.put("State", user.getState());

	            String formattedDate;
	            try {
	                formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
	            } catch (ParseException e) {
	                formattedDate = null; // Handle error appropriately
	            }
	            memberAndSaleData.put("RgisterDate", formattedDate);

	            List<Sell> sellList = user.getSell();

	            // Group sales by month, quarter, and year
	            Map<String, String> monthlySales = new LinkedHashMap<>();
	            Map<String, String> quarterlySales = new LinkedHashMap<>();
	            Map<String, String> yearlySales = new LinkedHashMap<>();

	            for (Sell sell : sellList) {
	                Date approvedDate = sell.getApprovedDate();
	                if (approvedDate != null) {
	                    Calendar calendar = Calendar.getInstance();
	                    calendar.setTime(approvedDate);

	                    int year = calendar.get(Calendar.YEAR);
	                    int month = calendar.get(Calendar.MONTH) + 1;
	                    int quarter = (month - 1) / 3 + 1;

	                    String monthKey = year + "-" + month;
	                    String quarterKey = year + "-Q" + quarter;
	                    String yearKey = String.valueOf(year);

	                    // Format sale amount as currency
	                    String formattedSaleAmount = currencyFormat.format(sell.getSaleamount());

	                    // Update monthly sales
	                    monthlySales.put(monthKey, formattedSaleAmount);

	                    // Update quarterly sales
	                    quarterlySales.put(quarterKey, formattedSaleAmount);

	                    // Update yearly sales
	                    yearlySales.put(yearKey, formattedSaleAmount);
	                } else {
	                    // Handle the null approvedDate scenario here if necessary
	                    System.out.println("Warning: sell record has null approvedDate");
	                }
	            }

	            // Add sales data to the combined map
//	            memberAndSaleData.put("monthlySales", monthlySales);
//	            memberAndSaleData.put("quarterlySales", quarterlySales);
//	            memberAndSaleData.put("yearlySales", yearlySales);

	            // Add the combined data for the user to the list of members
	            listOfMember.add(memberAndSaleData);
	        }

	        // Wrap the list in a map before returning
	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("managedUsers", listOfMember);
	        responseMap.put("loggeduser target", formatDouble(loggedUser.getMonthlyTarget()));

	        System.out.println("Logged user target added: " + loggedUser.getMonthlyTarget()); // Debugging line

	        return responseMap;
	    } catch (Exception e) {
	        // Log the exception for further analysis
	        e.printStackTrace();
	        // Throw a custom exception or return an empty list based on your requirement
	        return Collections.emptyMap();
	    }
	}

//	public void checkAndUpdateTargetAchieved() {
//        List<User> users = uRepo.findAll();
//
//        for (User user : users) {
//            if (user.getMonthlyTarget() != 0 && user.getTargetAchivedAmount() != null) {
//                if (user.getMonthlyTarget()<=(user.getTargetAchivedAmount())) {
//                    user.setIsAmountachived(true);
//                    uRepo.save(user);
//                }
//            }
//        }
//    }


	public ResponseEntity<?> getMyTeamtarget(Integer loggedInUserId) {
        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Logged out");
        }

        List<User> managedUsers = uRepo.findByManageBy(loggedInUserId);

        if (managedUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No managed users found");
        }

        List<Map<String, Object>> teamInfoList = new ArrayList<>();

        for (User user : managedUsers) {
            Map<String, Object> teamInfo = new HashMap<>();
            teamInfo.put("userId", user.getUserid());
            teamInfo.put("Role", user.getRole());
            teamInfo.put("userName", user.getUsername());
            teamInfo.put("monthlyTargetAmount", Double.parseDouble(formatDouble(user.getMonthlyTarget())));
            teamInfo.put("achievedTargetAmount", Double.parseDouble(formatDouble(user.getTargetAchivedAmount())));
            teamInfo.put("isTargetAchieved", user.isIsAmountachived());
            // Add more target information as needed

            
            teamInfoList.add(teamInfo);
        }

        List<Map<String, Object>> filteredTeamInfo = teamInfoList.stream()
                .filter(member -> !"COUNTRYHEAD".equals(member.get("Role")))
                .collect(Collectors.toList());

        
        return ResponseEntity.status(HttpStatus.OK).body(filteredTeamInfo);
    }


}
