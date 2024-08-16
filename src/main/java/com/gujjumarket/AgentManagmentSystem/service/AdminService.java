package com.gujjumarket.AgentManagmentSystem.service;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gujjumarket.AgentManagmentSystem.config.DateUtil;
import com.gujjumarket.AgentManagmentSystem.model.Admin;
import com.gujjumarket.AgentManagmentSystem.model.Customer;
import com.gujjumarket.AgentManagmentSystem.model.Payment;
import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.ProductCategory;
import com.gujjumarket.AgentManagmentSystem.model.ProductType;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.model.WithdrawalRequest;
import com.gujjumarket.AgentManagmentSystem.repo.AdminRepo;
//import com.gujjumarket.AgentManagmentSystem.repo.CommissionDetailsRepo;
import com.gujjumarket.AgentManagmentSystem.repo.CountryheadRepo;
import com.gujjumarket.AgentManagmentSystem.repo.PaymentRepository;
import com.gujjumarket.AgentManagmentSystem.repo.ProductCatRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductRepo;
import com.gujjumarket.AgentManagmentSystem.repo.ProductTypeRepo;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
//import com.gujjumarket.AgentManagmentSystem.repo.promotionRepo;
import com.gujjumarket.AgentManagmentSystem.repo.withdrawalRequestRepo;
import com.gujjumarket.AgentManagmentSystem.utils.PhotoUpload;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AdminService {

	@Autowired
	AdminRepo adRepo;
	@Autowired
	SellRepo sellRepo;
	@Autowired
	Userrepo uRepo;
	@Autowired
	ProductTypeRepo ptRepo;
	@Autowired
	ProductCatRepo pcRepo;
	@Autowired
	ProductRepo pRepo;
	@Autowired
	CountryheadRepo chRepo;
	@Autowired
	ProductService pService;
	@Autowired
	private withdrawalRequestRepo wRepo;
//	@Autowired
//	private CommissionDetailsRepo commissionRepo;
//	@Autowired
//	private promotionRepo proRepo;
	@Autowired
	PaymentRepository paymentRepo;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Value("${upload-dir}")
	private String upload_dir;

	@Autowired
	private JavaMailSender mailSender;

	public Admin getLogin(String phoneNo, String password) {
		return adRepo.findByPhoneNoAndPassword(phoneNo, password);
	}

	public Admin createAdmin(String name, String password, MultipartFile photo) throws IOException {

		String Filename = photo.getOriginalFilename();
		String storeFilename = PhotoUpload.saveFile(upload_dir, Filename, photo);
		Admin admin = new Admin();
		admin.setName(name);
		admin.setPassword(password);
		admin.setPhoto(storeFilename);
		return adRepo.save(admin);
	}

	public static String formatDouble(double value) {
        return String.format("%.2f", value);
    }
	
	public void changePassword(String currentPassword, String newPassword, String confirmPassword, Admin admin) {
		// Verify if the provided current password matches the stored plain text
		// password
		if (!currentPassword.equals(admin.getPassword())) {
			throw new IllegalArgumentException("Incorrect current password.");
		}

		// Check if the new password matches the confirmed password
		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("New password and confirm password do not match.");
		}

		// Hash the new password using BCrypt
		// String hashedNewPassword = passwordHasher.hashPassword(newPassword);

		// Set the new hashed password for the user
		admin.setPassword(newPassword);

		// Save the updated user in the repository
		adRepo.save(admin);
	}

	public Admin updateAdmin(String name, MultipartFile photo, int adminId) {

		Optional<Admin> adminbyId = adRepo.findById(adminId);
		Admin adminOG = adminbyId.get();

		String Filename = photo.getOriginalFilename();
		String storeFilename = PhotoUpload.saveFile(upload_dir, Filename, photo);
		adminOG.setName(name);
		adminOG.setPhoto(storeFilename);
		return adRepo.save(adminOG);
	}

	public Admin updatePassword(String password, String np, String cnp, int adminId) {

		Optional<Admin> adminbyId = adRepo.findById(adminId);
		Admin adminOG = adminbyId.get();

		if (adminOG.getPassword().equals(password) && np.equals(cnp)) {
			adminOG.setPassword(cnp);
			return adRepo.save(adminOG);
		}
		throw new IllegalArgumentException("Invalid password or password mismatch");
	}

	public void creatept(ProductType pt, Integer adminId) {
		Optional<Admin> adminbyId = adRepo.findById(adminId);
		if (adminbyId.isPresent()) {
			Admin admin = adminbyId.get();
			ProductType pt1 = new ProductType();
			pt1.setPtype(pt.getPtype());
			pt1.setPtdescription(pt.getPtdescription());
			pt1.setCreatedby(admin);
			pt1.setUpdatedby(admin);
			pt1.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
			pt1.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
			ptRepo.save(pt1);
		}
	}

//	replace this method to upper method after testing.
//	public void creatept(ProductType pt, Integer adminId) {
//		ProductType pt1 = new ProductType();
//		pt1.setPtype(pt.getPtype());
//		pt1.setPtdescription(pt.getPtdescription());
//
//		// Default to a generic "system" admin or similar, if no admin ID is provided
//		if (adminId != null) {
//			Optional<Admin> adminById = adRepo.findById(adminId);
//			if (adminById.isPresent()) {
//				Admin admin = adminById.get();
//				pt1.setCreatedby(admin);
//				pt1.setUpdatedby(admin);
//			}
//		}
//
//		// Set the creation and update dates
//		pt1.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
//		pt1.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//		// Save the new product type to the repository
//		ptRepo.save(pt1);
//	}

	public void createpc(Map<String, Object> pc, Integer adminId) {
		Integer ptid = (Integer) pc.get("ptid");
		System.out.println(ptid);
		String pcategory = (String) pc.get("pcatagory");
		String pcdescription = (String) pc.get("pcdescription");
		Optional<Admin> adminbyId = adRepo.findById(adminId);

		ProductType PT = ptRepo.getReferenceById(ptid);
		if (adminbyId.isPresent()) {
			Admin admin = adminbyId.get();
			ProductCategory newPC = new ProductCategory();
			newPC.setPcatagory(pcategory);
			newPC.setPcdescription(pcdescription);
			newPC.setProducttype(PT); // Setting ProductType directly
			newPC.setCreatedby(admin);
			newPC.setUpdatedby(admin);
			newPC.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
			newPC.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));

			pcRepo.save(newPC);
		}
	}

	// add these fields MultipartFile pfile, MultipartFile pphoto,
	public void createproduct(String pdesc, Integer pcate, Integer adminId, String pname, String pcode, Long pprice,
			double cHcomm, double sTcomm, double dHcomm, double acomm, double sAcomm, double cityhcomm,
			boolean isrenewal) {
		Admin admin = adRepo.getReferenceById(adminId);
		int pcid = pcate;
		ProductCategory pcate2 = pcRepo.getReferenceById(pcid);
		ProductType ptype = pcate2.getProducttype();
		if (admin != null && pcate2 != null) {
//			String pphotoname = pphoto.getOriginalFilename();
//			String storepphoto = PhotoUpload.saveFile(upload_dir, pphotoname, pphoto);
//			String pfilename = pfile.getOriginalFilename();
//			String storepfile = PhotoUpload.saveFile(upload_dir, pfilename, pfile);
			Product p = new Product();
			p.setPname(pname);
			p.setPdesc(pdesc);
			p.setCreatedby(admin);
			p.setUpdatedby(admin);
			p.setPprice(pprice);
			p.setPcode(pcode);
			p.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
			p.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
			p.setCHcomm(cHcomm);
			p.setSTcomm(sTcomm);
			p.setDHcomm(dHcomm);
			p.setAcomm(acomm);
			p.setSAcomm(sAcomm);
//			p.setPphoto(storepphoto);
//			p.setPfile(storepfile);
			p.setProductcategory(pcate2);
			p.setProducttype(ptype);
			p.setCityhcomm(cityhcomm);
			p.setIsdisable(false);
			p.setIsrenewal(isrenewal);
			pRepo.save(p);
		}
	}
// I have to change this method to upper one after testing the api.
//	public void createpc(Map<String, Object> pc, Integer adminId) {
//		Integer ptid = (Integer) pc.get("ptid");
//		String pcategory = (String) pc.get("pcatagory");
//		String pcdescription = (String) pc.get("pcdescription");
//
//		ProductCategory newPC = new ProductCategory();
//		newPC.setPcatagory(pcategory);
//		newPC.setPcdescription(pcdescription);
//
//		// Setting createdby and updatedby to null or some default value, if needed
//		newPC.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
//		newPC.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//		// ProductType setting based on the given product type ID
//		ProductType PT = ptRepo.getReferenceById(ptid);
//		newPC.setProducttype(PT);
//
//		pcRepo.save(newPC);
//	}
//	// I have to change this method to upper one after testing the api.
//
//	public void createproduct(String pdesc, Integer pcate, Integer adminId, MultipartFile pfile, MultipartFile pphoto,
//			String pname, Long pcode, Long pprice, double CHcomm, double STcomm, double DHcomm, double Acomm,
//			double SAcomm, double cityhcomm) {
//		ProductCategory pcate2 = pcRepo.getReferenceById(pcate);
//
//		Product p = new Product();
//		p.setPname(pname);
//		p.setPdesc(pdesc);
//		p.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
//		p.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//
//		// Save uploaded files
//		String pphotoname = pphoto.getOriginalFilename();
//		String storepphoto = PhotoUpload.saveFile(upload_dir, pphotoname, pphoto);
//		String pfilename = pfile.getOriginalFilename();
//		String storepfile = PhotoUpload.saveFile(upload_dir, pfilename, pfile);
//
//		// Set values and relationships
//		p.setPprice(pprice);
//		p.setPcode(pcode);
//		p.setCHcomm(CHcomm);
//		p.setSTcomm(STcomm);
//		p.setDHcomm(DHcomm);
//		p.setAcomm(Acomm);
//		p.setSAcomm(SAcomm);
//		p.setPphoto(storepphoto);
//		p.setPfile(storepfile);
//		p.setProductcategory(pcate2);
//		p.setProducttype(pcate2.getProducttype());
//		p.setCityhcomm(cityhcomm);
//
//		pRepo.save(p);
//	}

	public String createCH(Integer adminId, User user) {
		Admin admin = adRepo.getReferenceById(adminId);
		if (admin != null) {
			System.out.println(user.getUsermobile() + "user mobile");
//			System.out.println(user.getManagedBy() + "manager");
			user.setUsermobile(user.getUsermobile());
			user.setUsername(user.getUsername());
			user.setUserpassword(UUID.randomUUID().toString());
			user.setRole(user.getRole());
			user.setCreatedby(admin);
			user.setUpdatedby(admin);
			user.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
			user.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
			user.setIsSubUser(true);
			user.setUseremail(user.getUseremail());
			user.setFirstTimeLogin(true);
			user.setManageBy(adminId);
			user.setUsDisabled(false);
			chRepo.save(user);
			sendUserEmail(user);
			return user.getUserpassword();
		}
		throw new IllegalArgumentException("Unabale to Create User");
	}

	private void sendUserEmail(User subAgent) {
		String subject = subAgent.getRole() + " Account Created";
		String text = subAgent.getRole() + " Created Successfully.\n" + "Username: " + subAgent.getUsername() + "\n"
				+ "Email: " + subAgent.getUseremail() + "\n" + "Password: " + subAgent.getUserpassword();

		sendEmail(subAgent.getUseremail(), subject, text);
	}

	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	public String createUser(Integer adminId, User user) {
		Admin admin = adRepo.getReferenceById(adminId);
		if (admin != null) {
			user.setUsermobile(user.getUsermobile());
			user.setUsername(user.getUsername());
			user.setUserpassword(UUID.randomUUID().toString());
			user.setRole(user.getRole());
			user.setCreatedby(admin);
			user.setUpdatedby(admin);
			user.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
			user.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
			user.setIsSubUser(true);
			user.setUseremail(user.getUseremail());
			user.setFirstTimeLogin(true);
			user.setManageBy(user.getManageBy());
			user.setUsDisabled(false);
			user.setCityorvillage(user.getCityorvillage());
			user.setState(user.getState());
			user.setDistrict(user.getDistrict());
			uRepo.save(user);

			if (user.getRole().equals("SUBAGENT")) {
				// Find all users (agents) managed by the managed-by user
				List<User> agents = uRepo.findByManageBy(user.getManageBy());

				// Calculate the monthly target for the managed-by user
				User managedByUser = uRepo.findById(user.getManageBy()).orElse(null);
				if (managedByUser != null) {
					double monthlyTarget = managedByUser.getMonthlyTarget();

					// Calculate the monthly target per agent
					double targetPerAgent = monthlyTarget / agents.size();

					// Update the monthly target for each agent
					for (User agent : agents) {
						agent.setMonthlyTarget(targetPerAgent);
						agent.setQuarterlyTarget(monthlyTarget * 4);
						agent.setHalfYearlyTarget(monthlyTarget * 6);
						agent.setYearlyTarget(monthlyTarget * 12);
						// Update other target values if needed
						uRepo.save(agent);

					}
				}
			}

			if (user.getRole().equals("AGENT")) {
				// Find all users (agents) managed by the managed-by user
				List<User> agents = uRepo.findByManageBy(user.getManageBy());

				// Calculate the monthly target for the managed-by user
				User managedByUser = uRepo.findById(user.getManageBy()).orElse(null);
				if (managedByUser != null) {
					double monthlyTarget = managedByUser.getMonthlyTarget();

					// Calculate the monthly target per agent
					double targetPerAgent = monthlyTarget / agents.size();

					// Update the monthly target for each agent
					for (User agent : agents) {
						agent.setMonthlyTarget(targetPerAgent);
						agent.setQuarterlyTarget(monthlyTarget * 4);
						agent.setHalfYearlyTarget(monthlyTarget * 6);
						agent.setYearlyTarget(monthlyTarget * 12);
						// Update other target values if needed
						uRepo.save(agent);

						List<User> subAgents = uRepo.findByManageBy(agent.getUserid());

						// Calculate and update the targets for each sub-agent
						double subAgentMonthlyTarget = targetPerAgent / subAgents.size();
						for (User subAgent : subAgents) {
							subAgent.setMonthlyTarget(subAgentMonthlyTarget);
							// Update other target values if needed
							uRepo.save(subAgent);
						}

					}
				}
			}

			sendUserEmail(user);
			System.out.println(user);
			return user.getUserpassword();
		}
		throw new IllegalArgumentException("Unabale to Create User");
	}
//	public String createCH(Integer adminId, User user) {
//		Admin admin = adRepo.getReferenceById(adminId);
//		if (admin != null) {
//			System.out.println(user.getUsermobile() + "user mobile");
//			System.out.println(user.getManageBy() + "manager");
//			user.setUsermobile(user.getUsermobile());
//			user.setUserpassword(UUID.randomUUID().toString());
//			user.setRole(user.getRole());
//			user.setCreatedby(admin);
//			user.setUpdatedby(admin);
//			user.setCreateddate(java.sql.Date.valueOf(LocalDate.now()));
//			user.setUpdateddate(java.sql.Date.valueOf(LocalDate.now()));
//			user.setIsSubUser(true);
//			user.setFirstTimeLogin(true);
//			user.setManageBy(adminId);
//			user.setUsDisabled(false);
//			chRepo.save(user);
//			return user.getUserpassword();
//		}
//		throw new IllegalArgumentException("Unabale to Create User");
//	}

//	public Sell updatePendingSells(Integer sellid) {
//		Sell sell = sellRepo.getReferenceById(sellid);
//		User seller = chRepo.getReferenceById(sell.getSoldby().getUserid());
//		User CITYHEAD = chRepo.getReferenceById(seller.getManageBy());
//		User DISTRICTHEAD = chRepo.getReferenceById(CITYHEAD.getManageBy());
//		User STATEHEAD = chRepo.getReferenceById(DISTRICTHEAD.getManageBy());
//		User COUNTRYHEAD = chRepo.getReferenceById(STATEHEAD.getManageBy());
//
//		Integer pid = sell.getPid();
//		Product p = pRepo.getReferenceById(pid);
//
//		sell.setSalestatus("Approved");
//		sell.setApprovedDate(java.sql.Date.valueOf(LocalDate.now()));
//
//		CITYHEAD.setTotalCommissionAmount(CITYHEAD.getTotalCommissionAmount() + (p.getCityhcomm() * p.getPprice()));
//		DISTRICTHEAD
//				.setTotalCommissionAmount(DISTRICTHEAD.getTotalCommissionAmount() + (p.getDHcomm() * p.getPprice()));
//		STATEHEAD.setTotalCommissionAmount(STATEHEAD.getTotalCommissionAmount() + (p.getSTcomm() * p.getPprice()));
//		COUNTRYHEAD.setTotalCommissionAmount(CITYHEAD.getTotalCommissionAmount() + (p.getCHcomm() * p.getPprice()));
//		seller.setTotalCommissionAmount(seller.getTotalCommissionAmount() + (p.getAcomm() * p.getPprice()));
//
//		chRepo.save(COUNTRYHEAD);
//		chRepo.save(STATEHEAD);
//		chRepo.save(DISTRICTHEAD);
//		chRepo.save(CITYHEAD);
//		chRepo.save(seller);
//
//		return sellRepo.save(sell);
//
//	}

	public Sell updateSellStatus(Integer sellId, String status) {
		Sell sell = sellRepo.findById(sellId).orElse(null);
		User user = sell.getSoldby();

		if (sell != null) {
			User seller = chRepo.getReferenceById(sell.getSoldby().getUserid());
			User CITYHEAD = chRepo.getReferenceById(seller.getManageBy());
			User DISTRICTHEAD = chRepo.getReferenceById(CITYHEAD.getManageBy());
			User STATEHEAD = chRepo.getReferenceById(DISTRICTHEAD.getManageBy());
			User COUNTRYHEAD = chRepo.getReferenceById(STATEHEAD.getManageBy());

			Integer pid = sell.getPid();
			Product p = pRepo.getReferenceById(pid);

			if ("Approved".equalsIgnoreCase(status)) {
				sell.setSalestatus("Approved");
				sell.setApprovedDate(java.sql.Date.valueOf(LocalDate.now()));

				CITYHEAD.setTotalCommissionAmount(
						CITYHEAD.getTotalCommissionAmount() + ((p.getCityhcomm() / p.getPprice()) * 100));
				DISTRICTHEAD.setTotalCommissionAmount(
						DISTRICTHEAD.getTotalCommissionAmount() + (p.getDHcomm() * p.getPprice()));
				STATEHEAD.setTotalCommissionAmount(
						STATEHEAD.getTotalCommissionAmount() + (p.getSTcomm() * p.getPprice()));
				COUNTRYHEAD.setTotalCommissionAmount(
						COUNTRYHEAD.getTotalCommissionAmount() + (p.getCHcomm() * p.getPprice()));
				seller.setTotalCommissionAmount(
						seller.getTotalCommissionAmount() + ((p.getAcomm() / p.getPprice()) * 100));
				seller.setTotalCommissionAmount(
						seller.getTotalCommissionAmount() + ((p.getSAcomm() / p.getPprice()) * 100));

				Integer Aamount = (int) sell.getSaleamount();

				user.setTargetAchivedAmount(user.getTargetAchivedAmount() + Aamount);
				if(user.getTargetAchivedAmount()>=user.getMonthlyTarget()) {
					user.setIsAmountachived(true);
				}
				CITYHEAD.setTargetAchivedAmount(CITYHEAD.getTargetAchivedAmount() + Aamount);
				DISTRICTHEAD.setTargetAchivedAmount(DISTRICTHEAD.getTargetAchivedAmount() + Aamount);
				STATEHEAD.setTargetAchivedAmount(STATEHEAD.getTargetAchivedAmount() + Aamount);
				COUNTRYHEAD.setTargetAchivedAmount(COUNTRYHEAD.getTargetAchivedAmount() + Aamount);

			} else if ("Unapproved".equalsIgnoreCase(status)) {
				sell.setSalestatus("Unapproved");
				sell.setApprovedDate(null);

				CITYHEAD.setTotalCommissionAmount(
						CITYHEAD.getTotalCommissionAmount() - (p.getCityhcomm() * p.getPprice()));
				DISTRICTHEAD.setTotalCommissionAmount(
						DISTRICTHEAD.getTotalCommissionAmount() - (p.getDHcomm() * p.getPprice()));
				STATEHEAD.setTotalCommissionAmount(
						STATEHEAD.getTotalCommissionAmount() - (p.getSTcomm() * p.getPprice()));
				COUNTRYHEAD.setTotalCommissionAmount(
						COUNTRYHEAD.getTotalCommissionAmount() - (p.getCHcomm() * p.getPprice()));
				seller.setTotalCommissionAmount(seller.getTotalCommissionAmount() - (p.getAcomm() * p.getPprice()));
			}

			chRepo.save(COUNTRYHEAD);
			chRepo.save(STATEHEAD);
			chRepo.save(DISTRICTHEAD);
			chRepo.save(CITYHEAD);
			chRepo.save(seller);

			return sellRepo.save(sell);
		} else {
			throw new RuntimeException("Sell not found");
		}
	}

//	public List<User> getAllUsersWithDetails() {
//		return uRepo.findAll();
//	}
//	public List<User> getUserOverview() {
//	    return uRepo.findAll(); // Return the entire list of users
//	}

//	public List<Map<String, Object>> getUserOverview() {
//		List<User> users = uRepo.findAll();
//		List<Map<String, Object>> filteredUsers = new ArrayList<>();
//		for (User user : users) {
//			Map<String, Object> filteredUser = new HashMap<>();
//			filteredUser.put("userid", user.getUserid());
//			filteredUser.put("role", user.getRole());
//			filteredUser.put("UserPhoto", user.getUserPhoto());
////			filteredUser.put("monthlyTarget", user.getMonthlyTarget());
////			filteredUser.put("quarterlyTarget", user.getQuarterlyTarget());
////			filteredUser.put("halfYearlyTarget", user.getHalfYearlyTarget());
////			filteredUser.put("yearlyTarget", user.getYearlyTarget());
//			filteredUser.put("accountholdername", user.getAccountholdername());
//			filteredUser.put("totalCommissionAmount", user.getTotalCommissionAmount());
//			filteredUser.put("usermobile", user.getUsermobile());
//			filteredUser.put("username", user.getUsername());
//			filteredUser.put("useremail", user.getUseremail());
//			filteredUser.put("aadhar", user.getAADHAR());
//			
//			filteredUser.put("pan", user.getPAN());
//			filteredUser.put("accounttype", user.getAccounttype());
//			filteredUser.put("bankname", user.getBankname());
//			filteredUser.put("isKYCDone", user.isIsKYCDone());
//			filteredUser.put("branchname", user.getBranchname());
//			filteredUser.put("usDisabled", user.isUsDisabled());
//			String formattedDate;
//	        try {
//	            formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
//	        } catch (ParseException e) {
//	            formattedDate = null; // Handle error appropriately
//	        }
//
//	        filteredUser.put("registerDate", formattedDate);
////			filteredUser.put("firstTimeLogin", user.isFirstTimeLogin());
////			filteredUser.put("manageBy", user.getManageBy());
////	        User manager = user.getManageBy();
////            String managerName = (manager != null) ? manager.getUsername() : "N/A"; // Or other default value
////            filteredUser.put("manageBy", managerName);
//			filteredUser.put("isSubUser", user.isIsSubUser());
//			filteredUsers.add(filteredUser);
//			
//			User u1 = uRepo.getReferenceById(user.getManageBy());
//			String v = (u1.getUsername()+" "+ u1.getRole());
//			filteredUser.put("manageBy", v);
//			
//		}
//		return filteredUsers;
//
//	}

	public List<Map<String, Object>> getUserOverview() {
		List<User> users = uRepo.findAll();
		List<Map<String, Object>> filteredUsers = new ArrayList<>();
		for (User user : users) {
			Map<String, Object> filteredUser = new HashMap<>();
			filteredUser.put("userid", user.getUserid());
			filteredUser.put("role", user.getRole());
			filteredUser.put("Userprofile", user.getUserprofile());
			filteredUser.put("accountholdername", user.getAccountholdername());
			filteredUser.put("totalCommissionAmount", user.getTotalCommissionAmount());
			filteredUser.put("usermobile", user.getUsermobile());
			filteredUser.put("username", user.getUsername());
			filteredUser.put("useremail", user.getUseremail());
			filteredUser.put("aadhar", user.getAADHAR());
			filteredUser.put("pan", user.getPAN());
			filteredUser.put("accounttype", user.getAccounttype());
			filteredUser.put("bankname", user.getBankname());
			filteredUser.put("isKYCDone", user.isIsKYCDone());
			filteredUser.put("branchname", user.getBranchname());
			filteredUser.put("usDisabled", user.isUsDisabled());
			filteredUser.put("isSubUser", user.isIsSubUser());
			filteredUser.put("district", user.getDistrict());
			filteredUser.put("state", user.getState());
			filteredUser.put("cityorvillage", user.getCityorvillage());
			filteredUser.put("KYCstatus", user.getKYCstatus());

			// Handle the created date formatting safely
			String formattedDate;
			try {
				formattedDate = DateUtil.dateToString(user.getCreateddate(), "yyyy-MM-dd");
			} catch (ParseException e) {
				formattedDate = null; // Handle error appropriately
			}
			filteredUser.put("registerDate", formattedDate);

			// Fetch manageBy information using the manageBy id
			if (user.getManageBy() != 0) {
				try {
					User manageByUser = uRepo.getReferenceById(user.getManageBy());
					if (manageByUser != null) {
						filteredUser.put("manageBy", manageByUser.getUsername() + " " + manageByUser.getRole());
					} else {
						filteredUser.put("manageBy", 0);
					}
				} catch (EntityNotFoundException ex) {
					filteredUser.put("manageBy", 0);
				}
			} else {
				filteredUser.put("manageBy", 0);
			}

			filteredUsers.add(filteredUser);
		}
		return filteredUsers;
	}

//	public void deleteUserById(Integer userid) {
//        if (uRepo.existsById(userid)) {
//            uRepo.deleteById(userid);
//        } else {
//            throw new NoSuchElementException("User with userid " + userid + " does not exist");
//        }
//    }

//	public List<User> getUsersByRole(String userRole) {
//		return uRepo.findByRole(userRole);
//	}

	public List<User> getTeamByManagerId(Integer managerId) {
		return uRepo.findByManageBy(managerId);
	}

//	public ResponseEntity<?> getMyTeam(Map<String, Object> requestBody, HttpSession session) {
//		Integer loggedInUserId = (Integer) session.getAttribute("userID");
//		if (loggedInUserId == null) {
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
//		}
//
//		// Extract the userRole from the request body
//		String userRole = (String) requestBody.get("userRole");
//		if (userRole == null || userRole.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User role is required");
//		}
//
//		// Query the database to find all users with the specified role
//		List<User> usersWithRole = getUsersByRole(userRole);
//		if (usersWithRole.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found with the specified role");
//		}
//
//		List<Map<String, Object>> listOfMembers = new ArrayList<>();
//		for (User user : usersWithRole) {
//			List<User> team = getTeamByManagerId(user.getUserid());
//			for (User teamMember : team) {
//				Map<String, Object> memberData = new HashMap<>();
//				memberData.put("name", teamMember.getUsername());
//				memberData.put("mobile", teamMember.getUsermobile());
//				memberData.put("total earning", teamMember.getTotalCommissionAmount());
//				listOfMembers.add(memberData);
//			}
//		}
//
//		return ResponseEntity.status(HttpStatus.OK).body(listOfMembers);
//	} 
	public ResponseEntity<?> getListHead(Map<String, Object> requestBody, HttpServletRequest request) {
		Integer adminId = (Integer) request.getAttribute("userId");
		if (adminId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
		}

		// Extract the userRole from the request body
		String userRole = (String) requestBody.get("userRole");
		if (userRole == null || userRole.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User role is required");
		}

		// Query the database to find all users with the specified role
		List<User> headUsers = getUsersByRole(userRole);
		if (headUsers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found with the specified role");
		}

		List<Map<String, Object>> listOfHeads = new ArrayList<>();
		for (User head : headUsers) {
			Map<String, Object> headData = new HashMap<>();
			headData.put("name", head.getUsername());
			headData.put("mobile", head.getUsermobile());
			headData.put("total earning", head.getTotalCommissionAmount());
			listOfHeads.add(headData);
		}

		return ResponseEntity.status(HttpStatus.OK).body(listOfHeads);
	}

	public List<User> getUsersByRole(String userRole) {
		return uRepo.findByRole(userRole);
	}

//	public Map<String, Object> buildUserTargetResponse(User user) {
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("userId", user.getUserid());
//        userData.put("userRole", user.getRole());
//        userData.put("duration", user.getDuration());
//        userData.put("targetAmount", user.getTargetAmount());
//        userData.put("targetAchievedAmount", user.getTargetAchivedAmount());
//        userData.put("isAmountAchieved", user.isIsAmountachived());
//
//        List<String> productNames = user.getProducts().stream().map(Product::getPname).collect(Collectors.toList());
//        userData.put("products", productNames);
//
//        return userData;
//    }

//	public Map<String, Object> buildUserTargetResponse(User user) {
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("userId", user.getUserid());
//        userData.put("username", user.getUsername());
//        userData.put("userRole", user.getRole());
//        userData.put("duration", user.getDuration());
//        userData.put("targetAmount", user.getTargetAmount());
//        userData.put("targetAchievedAmount", user.getTargetAchivedAmount());
//        userData.put("isAmountAchieved", user.isIsAmountachived());
//
//        List<String> productNames = user.getProducts().stream().map(Product::getPname).collect(Collectors.toList());
//        userData.put("products", productNames);
//
//        return userData;
//    }

	public User manageUserRole(User user) {
		// Retrieve the existing user from the database
		User existingUser = uRepo.findById(user.getUserid()).orElse(null);

		if (existingUser != null) {
			// Update the user's role based on the new role provided
			existingUser.setRole(user.getRole());
			existingUser.setManageBy(user.getManageBy());
			existingUser.setUsername(user.getUsername());

			// Save the updated user
			return uRepo.save(existingUser);
		} else {
			// Handle the case where the user does not exist
			return null;
		}
	}

	// Method to disable/enable user accounts
	public User disableUser(int userId, boolean disable) {
		// Retrieve the user from the database
		Optional<User> optionalUser = uRepo.findById(userId);

		// Check if the user exists
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			// Update the disabled flag
			user.setUsDisabled(disable);

			// Save the changes to the database
			return uRepo.save(user);
		} else {
			// Handle the case where the user does not exist
			throw new RuntimeException("User not found with ID: " + userId);
		}
	}

//

	public List<Sell> getAllCommissions() {
		return sellRepo.findAll();
	}

//	public List<User> getTeamByRole(String role) {
//		return uRepo.findUsersManagedByRole(role);
//	}

//	public void upgradeDowngradeUserRole(int Userid, String newRole) {
//		// Find the user by userId
//		Optional<User> optionalUser = uRepo.findById(Userid);
//		if (optionalUser.isPresent()) {
//			User user = optionalUser.get();
//
//			// Update the user's role to newRole
//			user.setRole(newRole);
//
//			// Save the updated user
//			uRepo.save(user);
//
//			System.out.println("User role upgraded/downgraded successfully.");
//		} else {
//			System.out.println("User with ID " + Userid + " not found.");
//		}
//	}
//
////	public void createUser(User newUser) {
////
////		uRepo.save(newUser);
////		System.out.println("User created successfully.");
////	}
//
//	public void disableUser(int Userid) {
//	    // Logic to disable a user account
//	    // Find the user by userId
//	    Optional<User> optionalUser = uRepo.findById(Userid);
//	    if (optionalUser.isPresent()) {
//	        User user = optionalUser.get();
//	        // Set the user's status to disabled
//	        user.setStatus("disabled");
//	        // Save the updated user
//	        uRepo.save(user);
//	        System.out.println("User account disabled successfully.");
//	    } else {
//	        System.out.println("User with ID " + Userid + " not found.");
//	    }
//	}

	public void saveImage(int adminId, MultipartFile photo) throws IOException {
		// Retrieve the existing Admin entity from the database
		Optional<Admin> adminOptional = adRepo.findById(adminId);

		// Check if the Admin entity exists
		if (adminOptional.isPresent()) {
			Admin admin = adminOptional.get();

			// Set the photo field
			String filename = photo.getOriginalFilename();
			String storeFilename = PhotoUpload.saveFile(upload_dir, filename, photo);
			admin.setPhoto(storeFilename);

			// Save the updated Admin entity
			adRepo.save(admin);
		} else {
			throw new EntityNotFoundException("Admin with ID " + adminId + " not found");
		}
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

//	public List<Map<String, Object>> getMyTeam(int userid, String username) {
//	    try {
//	        List<User> myTeam = uRepo.findTeamMembersByUserid(userid);
//	        List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//	        for (User user : myTeam) {
//	            Map<String, Object> memberAndSaleData = new HashMap<>();
//
//	            // Add member data to the combined map
//	            memberAndSaleData.put("userId", user.getUserid()); // Include userId
//	            memberAndSaleData.put("username", user.getUsername()); // Include username
//	            memberAndSaleData.put("mobile", user.getUsermobile());
//	            memberAndSaleData.put("totalEarning", user.getTotalCommissionAmount());
//
//	            List<Sell> sellList = user.getSell(); // Assuming Sell is a model class representing sales
//
//	            // Group sales by month, quarter, and year
//	            Map<String, Double> monthlySales = new HashMap<>();
//	            Map<String, Double> quarterlySales = new HashMap<>();
//	            Map<String, Double> yearlySales = new HashMap<>();
//
//	            for (Sell sell : sellList) {
//	                Calendar calendar = Calendar.getInstance();
//	                calendar.setTime(sell.getApprovedDate()); // Assuming approvedDate is the date of sale approval
//
//	                int year = calendar.get(Calendar.YEAR);
//	                int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 since Calendar.MONTH is zero-based
//	                int quarter = (month - 1) / 3 + 1; // Calculate quarter
//
//	                String monthKey = year + "-" + month;
//	                String quarterKey = year + "-Q" + quarter;
//	                String yearKey = String.valueOf(year);
//
//	                // Update monthly sales
//	                monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + sell.getSaleamount());
//	                // Update quarterly sales
//	                quarterlySales.put(quarterKey, quarterlySales.getOrDefault(quarterKey, 0.0) + sell.getSaleamount());
//	                // Update yearly sales
//	                yearlySales.put(yearKey, yearlySales.getOrDefault(yearKey, 0.0) + sell.getSaleamount());
//	            }
//
//	            // Add sales data to the combined map
//	            memberAndSaleData.put("monthlySales", monthlySales);
//	            memberAndSaleData.put("quarterlySales", quarterlySales);
//	            memberAndSaleData.put("yearlySales", yearlySales);
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
//
//
//	 public boolean isAdmin(Integer adminId) {
//	        // Fetch the admin from the database
//	        Optional<Admin> adminOptional = adRepo.findById(adminId);
//
//	        // Check if the admin exists
//	        return adminOptional.isPresent();
//	    }

//	public List<Map<String, Object>> getMyTeam(int userId, String username) {
//        try {
//            List<User> myTeam = uRepo.findTeamMembersByUserid(userId);
//            List<Map<String, Object>> listOfMember = new ArrayList<>();
//
//            for (User user : myTeam) {
//                Map<String, Object> memberAndSaleData = new HashMap<>();
//
//                // Add member data to the combined map
//                memberAndSaleData.put("userId", user.getUserid()); // Include userId
//                memberAndSaleData.put("username", user.getUsername()); // Include username
//                memberAndSaleData.put("mobile", user.getUsermobile());
//                memberAndSaleData.put("totalEarning", user.getTotalCommissionAmount());
//
//                // Add additional fields you need for each user
//
//                listOfMember.add(memberAndSaleData);
//            }
//
//            return listOfMember;
//        } catch (Exception e) {
//            // Log the exception for further analysis
//            e.printStackTrace();
//            // Throw a custom exception or return an empty list based on your requirement
//            return new ArrayList<>();
//        }
//    }

	public List<Map<String, Object>> getMyTeam(int userId) {
		try {
			List<User> myTeam = uRepo.findAll();
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

	public boolean isAdmin(Integer userId) {
		// Fetch the admin from the database based on the admin ID
		Optional<Admin> admin = adRepo.findById(userId);

		// Check if the admin exists and if the admin's role indicates admin privileges
		return admin != null; // You might want to add additional checks here based on admin's role
	}

//	public void setUserTarget(Map<String, Object> request) {
//		Integer userId = (Integer) request.get("userId");
//		String userRole = (String) request.get("userRole");
//		ObjectMapper objectMapper = new ObjectMapper();
//		List<String> productNames = objectMapper.convertValue(request.get("products"),
//				new TypeReference<List<String>>() {
//				});
//		String duration = (String) request.get("duration");
//		Integer targetAmount = (Integer) request.get("targetAmount");
////		Integer targetAchivedAmount = (Integer) request.get("targetAchivedAmount");
////		boolean isAmountAchieved = (boolean) request.get("isAmountAchieved");
//
//		if (!isValidDuration(duration)) {
//			throw new IllegalArgumentException("Invalid duration. Must be 'month', 'quarter', or 'year'.");
//		}
//
//		if (userId == null || userRole == null || productNames == null || productNames.isEmpty() || duration == null
//				|| targetAmount == null) {
//			throw new IllegalArgumentException("Missing required fields.");
//		}
//
//		Optional<User> userOptional = uRepo.findById(userId);
//		if (!userOptional.isPresent()) {
//			throw new IllegalArgumentException("User not found.");
//		}
//
//		User user = userOptional.get();
//		List<Product> products = pRepo.findByPname(productNames);
//
//		user.setRole(userRole);
//		user.setProducts(products);
//		user.setDuration(duration);
//		user.setTargetAmount(targetAmount);
////		user.setTargetAchivedAmount(targetAchivedAmount);
////		user.setIsAmountachived(isAmountAchieved); // Set the initial state for achieving the target
//
//		uRepo.save(user); // Save the updated user entity
//	}

//	public boolean updateUserTarget(Map<String, Object> request) {
//		Integer userId = (Integer) request.get("userid");
//		if (userId == null) {
//			throw new IllegalArgumentException("User ID is required.");
//		}
//
//		Optional<User> userOptional = uRepo.findById(userId);
//
//		if (!userOptional.isPresent()) {
//			throw new IllegalArgumentException("User not found.");
//		}
//
//		User user = userOptional.get();
//
//		// Update target amount
//		if (request.containsKey("targetAmount")) {
//			Integer targetAmount = (Integer) request.get("targetAmount");
//			user.setTargetAmount(targetAmount);
//		}
//		if (request.containsKey("targetAchivedAmount")) {
//			Integer targetAchivedAmount = (Integer) request.get("targetAchivedAmount");
//			user.setTargetAchivedAmount(targetAchivedAmount);;
//		}
//
//		// Update user role
//		if (request.containsKey("userRole")) {
//			user.setRole((String) request.get("userRole"));
//		}
//
//		// Update duration
//		if (request.containsKey("duration")) {
//			String duration = (String) request.get("duration");
//			if (!isValidDuration(duration)) {
//				throw new IllegalArgumentException("Invalid duration.");
//			}
//			user.setDuration(duration);
//		}
//
//		// Update products
//		if (request.containsKey("products")) {
//			ObjectMapper objectMapper = new ObjectMapper();
//			List<String> productNames = objectMapper.convertValue(request.get("products"),
//					new TypeReference<List<String>>() {
//					});
//			List<Product> products = pRepo.findByPname(productNames);
//			user.setProducts(products); // Set the new product list
//		}
//
//		// Update isAmountAchieved
//		if (request.containsKey("isAmountAchieved")) {
//			Boolean isAmountAchieved = (Boolean) request.get("isAmountAchieved");
//			user.setIsAmountachived(isAmountAchieved);
//		}
//
//		uRepo.save(user); // Save the updated user
//		return true;
//	}
//
//	public boolean deleteUserTarget(Integer userId) {
//		Optional<User> userOptional = uRepo.findById(userId);
//
//		if (!userOptional.isPresent()) {
//			return false; // User not found
//		}
//
//		User user = userOptional.get();
//
//		// Reset or remove target-related information
//		user.setRole(null); // Remove role
//		user.setProducts(Collections.emptyList()); // Clear product list
//		user.setDuration(null); // Remove duration
//		user.setTargetAmount(null); // Reset target amount
//		user.setTargetAchivedAmount(null); // Reset target amount
//		user.setIsAmountachived(false); // Reset achievement status
//
//		uRepo.save(user); // Save the updated user entity to persist changes
//		return true;
//	}

	// Method to set target for a user
	public void setUserTarget(Map<String, Object> request) {
		Integer userId = (Integer) request.get("userId");
		String userRole = (String) request.get("userRole");
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> productNames = objectMapper.convertValue(request.get("products"),
				new TypeReference<List<String>>() {
				});
		String duration = (String) request.get("duration");
		Integer targetAmount = (Integer) request.get("targetAmount");

		if (!isValidDuration(duration)) {
			throw new IllegalArgumentException("Invalid duration. Must be 'month', 'quarter', or 'year'.");
		}

		if (userId == null || userRole == null || productNames == null || productNames.isEmpty() || duration == null
				|| targetAmount == null) {
			throw new IllegalArgumentException("Missing required fields.");
		}

		Optional<User> userOptional = uRepo.findById(userId);
		if (!userOptional.isPresent()) {
			throw new IllegalArgumentException("User not found.");
		}

		User user = userOptional.get();
		List<Product> products = pRepo.findByPname(productNames);

		user.setRole(userRole);
		user.setProducts(products);
		user.setDuration(duration);
		user.setTargetAmount(targetAmount);
		user.setActive(true);

		uRepo.save(user); // Save the updated user entity
	}

	// Method to get sales target for a user
//    public Map<String, Object> getSalesTargetForUser(Integer userId) {
//        Optional<User> userOptional = uRepo.findById(userId);
//        if (!userOptional.isPresent()) {
//            return null;
//        }
//
//        User user = userOptional.get();
//        return buildUserTargetResponse(user);
//    }
//
//    public List<Map<String, Object>> getAllSalesTargets() {
//	    List<User> allUsers = uRepo.findAll();
//	    List<Map<String, Object>> response = new ArrayList<>();
//
//	    for (User user : allUsers) {
//	        response.add(buildUserTargetResponse(user));
//	    }
//
//	    return response;
//	}

//    public List<Map<String, Object>> getAllSalesTargets() {
//        List<User> activeUsers = uRepo.findActiveUsers();
//        System.out.println(activeUsers);
//        List<Map<String, Object>> response = new ArrayList<>();
//
//        for (User user : activeUsers) {
//            response.add(buildUserTargetResponse(user));
//        }
//
//        return response;
//    }
//
//    public Map<String, Object> getSalesTargetForUser(Integer userId) {
//        Optional<User> userOptional = uRepo.findActiveUserById(userId);
//        if (!userOptional.isPresent()) {
//            return null;
//        }
//
//        User user = userOptional.get();
//        return buildUserTargetResponse(user);
//    }

	public List<Map<String, Object>> getAllSalesTargets() {
		List<User> activeUsers = uRepo.findActiveUsers(); // Fetch only active users
		List<Map<String, Object>> response = new ArrayList<>();

		for (User user : activeUsers) {
			response.add(buildUserTargetResponse(user));
		}

		return response;
	}

	public Map<String, Object> getSalesTargetForUser(Integer userId) {
		Optional<User> userOptional = uRepo.findActiveUserById(userId);
		if (!userOptional.isPresent()) {
			return null;
		}

		User user = userOptional.get();
		return buildUserTargetResponse(user);
	}

	// Method to edit sales target for a user
	public void editUserTarget(Map<String, Object> request) {
		Integer userId = (Integer) request.get("userId");

		if (userId == null) {
			throw new IllegalArgumentException("User ID is required.");
		}

		Optional<User> userOptional = uRepo.findById(userId);
		if (!userOptional.isPresent()) {
			throw new IllegalArgumentException("User not found.");
		}

		User user = userOptional.get();

		// Update target amount
		if (request.containsKey("targetAmount")) {
			Integer targetAmount = (Integer) request.get("targetAmount");
			user.setTargetAmount(targetAmount);
		}

		if (request.containsKey("products")) {
			ObjectMapper objectMapper = new ObjectMapper();
			List<String> productNames = objectMapper.convertValue(request.get("products"),
					new TypeReference<List<String>>() {
					});
			List<Product> products = pRepo.findByPname(productNames);
			user.setProducts(products);
			;
		}

		user.setActive(true);
		user.setDuration("Monthly");
		// Update target achieved amount
//        if (request.containsKey("targetAchivedAmount")) {
//            Integer targetAchivedAmount = (Integer) request.get("targetAchivedAmount");
//            user.setTargetAchivedAmount(targetAchivedAmount);
//        }

//        // Update user role
//        if (request.containsKey("userRole")) {
//            user.setRole((String) request.get("userRole"));
//        }

//        // Update duration
//        if (request.containsKey("duration")) {
//            String duration = (String) request.get("duration");
//            if (!isValidDuration(duration)) {
//                throw new IllegalArgumentException("Invalid duration.");
//            }
//            user.setDuration(duration);
//        }

//        // Update products
//        if (request.containsKey("products")) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<String> productNames = objectMapper.convertValue(request.get("products"),
//                    new TypeReference<List<String>>() {});
//            List<Product> products = pRepo.findByPname(productNames);
//            user.setProducts(products); // Set the new product list
//        }

		// Update isAmountAchieved
		if (request.containsKey("isAmountAchieved")) {
			Boolean isAmountAchieved = (Boolean) request.get("isAmountAchieved");
			user.setIsAmountachived(isAmountAchieved);
		}

		uRepo.save(user); // Save the updated user
	}

	// Method to delete sales target for a user
//    public boolean deleteUserTarget(Integer userId) {
//        Optional<User> userOptional = uRepo.findById(userId);
//        if (!userOptional.isPresent()) {
//            return false; // User not found
//        }
//
//        User user = userOptional.get();
//
//        // Reset or remove target-related information
//        user.setUsername(null);
//        user.setRole(null); // Remove role
//        user.setProducts(Collections.emptyList()); // Clear product list
//        user.setDuration(null); // Remove duration
//        user.setTargetAmount(null); // Reset target amount
//        user.setTargetAchivedAmount(null); // Reset target amount
//        user.setIsAmountachived(false); // Reset achievement status
//
//        uRepo.save(user); // Save the updated user entity to persist changes
//        return true;
//    }

	public boolean deleteUserTarget(Integer userId) {
		Optional<User> userOptional = uRepo.findById(userId);
		if (!userOptional.isPresent()) {
			return false; // User not found
		}

		User user = userOptional.get();
		user.setActive(false); // Set active field to false
		uRepo.save(user); // Save the updated user entity to persist changes
		return true;
	}

	// Method to fetch usernames by role
	public List<Map<String, Object>> getUsernamesByRole(String role) {
		List<User> users = uRepo.findByRole(role);
		List<Map<String, Object>> userResponses = new ArrayList<>();

		for (User user : users) {
			Map<String, Object> userData = new HashMap<>();
			userData.put("userId", user.getUserid());
			userData.put("username", user.getUsername());
			userData.put("role", user.getRole());
			userResponses.add(userData);
		}

		return userResponses;
	}

	// Method to fetch all user roles
//    public List<String> getAllRoles() {
//        return uRepo.findAllRoles();
//    }

	// Method to build user target response
	public Map<String, Object> buildUserTargetResponse(User user) {
		Map<String, Object> userData = new HashMap<>();
		userData.put("userId", user.getUserid());
		userData.put("username", user.getUsername());
		userData.put("userRole", user.getRole());
		userData.put("duration", user.getDuration());
		userData.put("targetAmount", user.getTargetAmount());
		userData.put("targetAchievedAmount", user.getTargetAchivedAmount());
		userData.put("isAmountAchieved", user.isIsAmountachived());

		List<String> productNames = user.getProducts().stream().map(Product::getPname).collect(Collectors.toList());
		userData.put("products", productNames);

		return userData;
	}

	// Method to fetch all user roles
//	    public List<String> getAllRoles() {
//	        return uRepo.findAllRoles();
//	    }

	// Method to build user target response
//	    public Map<String, Object> buildUserTargetResponse(User user) {
//	        Map<String, Object> userData = new HashMap<>();
//	        userData.put("userId", user.getUserid());
//	        userData.put("username", user.getUsername());
//	        userData.put("userRole", user.getRole());
//	        userData.put("duration", user.getDuration());
//	        userData.put("targetAmount", user.getTargetAmount());
//	        userData.put("targetAchievedAmount", user.getTargetAchivedAmount());
//	        userData.put("isAmountAchieved", user.isIsAmountachived());
//
//	        List<String> productNames = user.getProducts().stream().map(Product::getPname).collect(Collectors.toList());
//	        userData.put("products", productNames);
//
//	        return userData;
//	    }

//	 public void setUserTarget(Map<String, Object> request, Integer currentUserId) {
//	        Integer targetUserId = (Integer) request.get("userId");
//	        User user = uRepo.findById(targetUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//	        // Ensure only the manager can set the target
//	        if (user.getManageBy() != currentUserId) {
//	            throw new IllegalArgumentException("You do not have permission to set targets for this user.");
//	        }
//
//	        // Extract and validate required fields
//	        String duration = (String) request.get("duration");
//	        if (!isValidDuration(duration)) {
//	            throw new IllegalArgumentException("Invalid duration. Must be 'month', 'quarter', or 'year'.");
//	        }
//
//	        Integer targetAmount = (Integer) request.get("targetAmount");
//	        Integer targetAchivedAmount = (Integer) request.get("targetAchivedAmount");
//	        List<String> productNames = extractProductNames(request);
//
//	        List<Product> products = pRepo.findByPname(productNames);
//
//	        // Set the new target information
//	        user.setDuration(duration);
//	        user.setTargetAmount(targetAmount);
//	        user.setTargetAchivedAmount(targetAchivedAmount);
//	        user.setProducts(products);
//
//	        uRepo.save(user);
//	    }
//
//	    public void updateUserTarget(Map<String, Object> request, Integer currentUserId) {
//	        Integer targetUserId = (Integer) request.get("userid");
//	        User user = uRepo.findById(targetUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//	        // Ensure only the manager can edit the target
//	        if (user.getManageBy() != currentUserId) {
//	            throw new IllegalArgumentException("You do not have permission to edit targets for this user.");
//	        }
//
//	        // Update relevant fields
//	        if (request.containsKey("targetAmount")) {
//	            Integer targetAmount = (Integer) request.get("targetAmount");
//	            user.setTargetAmount(targetAmount);
//	        }
//
//	        if (request.containsKey("targetAchivedAmount")) {
//	            Integer targetAchivedAmount = (Integer) request.get("targetAchivedAmount");
//	            user.setTargetAchivedAmount(targetAchivedAmount);
//	        }
//
//	        if (request.containsKey("duration")) {
//	            String duration = (String) request.get("duration");
//	            if (!isValidDuration(duration)) {
//	                throw new IllegalArgumentException("Invalid duration.");
//	            }
//	            user.setDuration(duration);
//	        }
//
//	        if (request.containsKey("products")) {
//	            List<String> productNames = extractProductNames(request);
//	            List<Product> products = pRepo.findByPname(productNames);
//	            user.setProducts(products);
//	        }
//
//	        uRepo.save(user);
//	    }
//
//	    public boolean deleteUserTarget(Integer targetUserId, Integer currentUserId) {
//	        User user = uRepo.findById(targetUserId).orElse(null);
//
//	        if (user == null) {
//	            return false; // User not found
//	        }
//
//	        // Ensure only the manager can delete the target
//	        if (user.getManageBy() != currentUserId) {
//	            return false; // Not authorized
//	        }
//
//	        // Reset or remove target-related information
//	        user.setRole(null); 
//	        user.setProducts(Collections.emptyList()); 
//	        user.setDuration(null); 
//	        user.setTargetAmount(null); 
//	        user.setTargetAchivedAmount(null); 
//
//	        uRepo.save(user); // Save the updated user entity
//	        return true; // Target deleted
//	    }
//
//	    public Map<String, Object> buildUserTargetResponse(User user) {
//	        Map<String, Object> response = new HashMap<>();
//	        response.put("userid", user.getUserid());
//	        response.put("targetAmount", user.getTargetAmount());
//	        response.put("targetAchivedAmount", user.getTargetAchivedAmount());
//	        response.put("duration", user.getDuration());
//	        response.put("products", user.getProducts().stream().map(Product::getPname).collect(Collectors.toList()));
//
//	        return response;
//	    }
//
//	    public List<Map<String, Object>> buildAllUserTargetResponses(List<User> users) {
//	        return users.stream().map(this::buildUserTargetResponse).collect(Collectors.toList());
//	    }
//
////	    // Additional utility methods
////	    private boolean isValidDuration(String duration) {
////	        return Arrays.asList("month", "quarter", or "year").contains(duration.toLowerCase());
////	    }
//
//	    private List<String> extractProductNames(Map<String, Object> request) {
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        return objectMapper.convertValue(request.get("products"), new TypeReference<List<String>>() {});
//	    }
	private boolean isValidDuration(String duration) {
		return duration != null && (duration.equals("MONTH") || duration.equals("QUARTER") || duration.equals("YEAR"));
	}

	public List<Sell> getSalesDataForUserHierarchy(int userId) {
		User rootUser = uRepo.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

		List<Sell> salesDataList = new ArrayList<>();
		// Fetch all users in the hierarchy and their sales
		List<User> hierarchicalUsers = getHierarchicalUsers(rootUser, new HashSet<>());

		for (User user : hierarchicalUsers) {
			List<Sell> userSales = sellRepo.findBySoldbyUserId(user.getUserid());
			if (userSales != null) {
				salesDataList.addAll(userSales);
			}
		}

		return salesDataList;
	}

	private List<User> getHierarchicalUsers(User rootUser, Set<Integer> visitedUserIds) {
		if (visitedUserIds.contains(rootUser.getUserid())) {
			return Collections.emptyList(); // Avoid recursion
		}

		visitedUserIds.add(rootUser.getUserid());
		List<User> hierarchicalUsers = new ArrayList<>();
		List<User> directSubordinates = uRepo.findByManageBy(rootUser.getUserid());

		if (directSubordinates != null) {
			for (User subordinate : directSubordinates) {
				hierarchicalUsers.add(subordinate);
				hierarchicalUsers.addAll(getHierarchicalUsers(subordinate, visitedUserIds));
			}
		}

		return hierarchicalUsers;
	}

	public List<Sell> getCommissionDetailsByUserRoleAndSaleStatus(String role, String saleStatus) {
		return sellRepo.findByUserRoleAndSaleStatus(role, saleStatus);
	}

	public List<Sell> getSalesByUserId(int userId) {
		return sellRepo.findBySoldbyUserId(userId); // Fetch sales for a specific user
	}

//
	public Map<String, Object> populateCommissionDetails(Sell row) {
		Map<String, Object> commissionDetails = new HashMap<>();
		commissionDetails.put("salestatus", safeGet(row.getSalestatus(), "Unknown"));
		commissionDetails.put("username", safeGet(row.getUsername(), "Unknown"));
		commissionDetails.put("date", formatDate(row.getRegisterDate()));
		commissionDetails.put("userrole", safeGet(row.getUserrole(), "Unknown"));
		commissionDetails.put("sellid", safeGet(row.getSellid(), "Unknown"));
		commissionDetails.put("transactionId", safeGet(row.getSellid(), -1));
		commissionDetails.put("productname", safeGet(row.getProductname(), "Unknown"));
		commissionDetails.put("payment_received", safeGet(row.getSaleamount(), 0.0));

		double commissionAmount = getCommissionAmount(row);
		commissionDetails.put("commissionAmount", formatDouble(commissionAmount));
		double commissionRate = getCommissionRate(row);
		commissionDetails.put("commissionRate", commissionRate);

		return commissionDetails;
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "Unknown";
		}
		return DATE_FORMAT.format(date);
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

//		// Helper method to safely get a value with a default
	private <T> T safeGet(T value, T defaultValue) {
		return (value != null) ? value : defaultValue;
	}

	public List<Sell> getSalesDataByRole(String role) {
		if (role == null || role.isEmpty()) {
			throw new IllegalArgumentException("Role must not be null or empty.");
		}

		// Retrieve users with the specified role
		List<User> users = uRepo.findAllByRole(role);

		List<Sell> salesData = new ArrayList<>();
		for (User user : users) {
			List<Sell> userSales = sellRepo.findBySoldbyUserId(user.getUserid());
			salesData.addAll(userSales);
		}

		return salesData;
	}

	public List<Map<String, Object>> generateReport() {
		List<User> users = uRepo.findAll();
		List<Map<String, Object>> reportData = new ArrayList<>();

		for (User user : users) {
			List<WithdrawalRequest> withdrawalRequests = wRepo.findByUser(user);
			for (WithdrawalRequest withdrawalRequest : withdrawalRequests) {
				Map<String, Object> userData = new HashMap<>();
				userData.put("username", user.getUsername());
				userData.put("phone_number", user.getUsermobile());
				userData.put("email", user.getUseremail());
				userData.put("register_date", user.getCreateddate());
				userData.put("user_role", user.getRole());
				userData.put("withdrawal_date", withdrawalRequest.getWitdhrawalDate());
				userData.put("amount", formatDouble(withdrawalRequest.getAmount()));
				userData.put("total_commission_earned", formatDouble(user.getTotalCommissionAmount()));
				reportData.add(userData);
			}
		}

		return reportData;
	}

	public Map<String, Object> populateCommissionDetails(Sell sell, User user) {
		if (sell == null) {
			throw new IllegalArgumentException("Sell object cannot be null");
		}

		Map<String, Object> commissionDetails = new HashMap<>();
		commissionDetails.put("salestatus", safeGet(sell.getSalestatus(), "Unknown"));
		commissionDetails.put("username", safeGet(sell.getUsername(), "Unknown"));
		commissionDetails.put("userrole", safeGet(sell.getUserrole(), "Unknown"));
		commissionDetails.put("date", safeGet(sell.getRegisterDate(), "Unknown"));
		commissionDetails.put("transactionId", safeGet(sell.getSellid(), -1));
		commissionDetails.put("productname", safeGet(sell.getProductname(), "Unknown"));
		commissionDetails.put("payment_received", safeGet(sell.getSaleamount(), 0.0));

		// Commission amount and rate based on the role hierarchy
		double commissionAmount = getCommissionAmount(sell, user);
		double commissionRate = getCommissionRate(sell, user);

		commissionDetails.put("commissionAmount", formatDouble(commissionAmount));
		commissionDetails.put("commissionRate", commissionRate);

		return commissionDetails;
	}

	private double getCommissionAmount(Sell sell, User user) {
		// Calculate based on the hierarchy
		if (user == null) {
			return 0.0;
		}

		String userRole = safeGet(user.getRole(), "");
		switch (userRole) {
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
		if (user == null) {
			return 0.0;
		}

		Product product = pRepo.findProductByPname(safeGet(sell.getProductname(), ""));
		if (product == null) {
			return 0.0;
		}

		String userRole = safeGet(user.getRole(), "");
		switch (userRole) {
		case "COUNTRYHEAD":
			return safeGet(product.getCHcomm(), 0.0);
		case "STATEHEAD":
			return safeGet(product.getSTcomm(), 0.0);
		case "DISTRICTHEAD":
			return safeGet(product.getDHcomm(), 0.0);
		case "CITYHEAD":
			return safeGet(product.getCityhcomm(), 0.0);
		case "AGENT":
			return safeGet(product.getAcomm(), 0.0);
		case "SUBAGENT":
			return safeGet(product.getSAcomm(), 0.0);
		default:
			return 0.0;
		}
	}

	public List<User> getUsersManagedByRole(String role) {
		// TODO Auto-generated method stub
		return uRepo.findUsersManagedByRole(role);
	}

	public List<Map<String, Object>> getClientMonitoringData() throws ParseException {
		List<User> users = uRepo.findAll(); // Fetch all users

		List<Map<String, Object>> monitoringData = new ArrayList<>();

		for (User user : users) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("userid", user.getUserid());
			userMap.put("username", user.getUsername());
			userMap.put("userrole", user.getRole());
			userMap.put("email", user.getUseremail());
			userMap.put("phonenumber", user.getUsermobile());

			// Adding product information

			if (user.getProducts() != null && !user.getProducts().isEmpty()) {
				List<Map<String, Object>> productDetails = new ArrayList<>();

				for (Product product : user.getProducts()) {
					Map<String, Object> productMap = new HashMap<>();
					productMap.put("pname", product.getPname());
					productMap.put("pdesc", product.getPdesc());
					// Ensure proper handling of possible NULL dates
					String formattedDate = (product.getSubRenewalDate() != null)
							? DateUtil.dateToString(product.getSubRenewalDate(), "yyyy-MM-dd")
							: null;

					productMap.put("subRenewalDate", formattedDate);
					productMap.put("subRenewalStatus", product.getSubRenewalStatus());
					productDetails.add(productMap);
				}
				System.out.println(productDetails);
				userMap.put("products", productDetails);
			}

			// Adding customer information
			if (user.getSell() != null && !user.getSell().isEmpty()) {
				Sell firstSell = user.getSell().get(0);
				Customer customer = firstSell.getCustomer();

				if (customer != null) {
					userMap.put("customername", customer.getCustomername());
					userMap.put("customeremail", customer.getCustomeremail());
				}
			}

			// Adding payment/transaction details
			if (user.getSell() != null && !user.getSell().isEmpty()) {
				List<Map<String, Object>> transactionDetails = new ArrayList<>();
				for (Sell sell : user.getSell()) {
					if (sell.getPayments() != null && !sell.getPayments().isEmpty()) {
						for (Payment payment : sell.getPayments()) {
							Map<String, Object> transactionMap = new HashMap<>();
							transactionMap.put("transactionId", payment.getTransactionId());
//							transactionMap.put("transactionType", payment.getTransactionType());
							transactionMap.put("transactionDateTime", payment.getTransactionDate());
							transactionMap.put("transactionAmount", payment.getAmount());
							transactionDetails.add(transactionMap);
						}
					}
				}
				userMap.put("transactions", transactionDetails);
			}

			monitoringData.add(userMap);
		}

		return monitoringData;
	}

	public List<Map<String, Object>> getKYCPendingUsers() {
		List<User> users = uRepo.findByIsKYCDoneFalse(); // Fetch users with pending KYC
		return users.stream().map(user -> {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("username", user.getUsername());
			userMap.put("usermobile", user.getUsermobile());
			userMap.put("useremail", user.getUseremail());
			userMap.put("userrole", user.getRole());

			// Format the date if necessary
			String registerDate = user.getCreateddate() != null ? user.getCreateddate().toString() : null;

			userMap.put("register_date", registerDate); // Assuming createddate is the register date
			userMap.put("aadhar_card", user.getAADHAR());
			userMap.put("pan", user.getPAN());

			return userMap;
		}).collect(Collectors.toList());
	}
//		 public List<Map<String, Object>> getUsersManagedBy(Integer managerId, List<Map<String, Object>> allUsers) {
//			    List<Map<String, Object>> managedUsers = new ArrayList<>();
//
//			    // Create a queue to perform breadth-first search
//			    Queue<Integer> queue = new LinkedList<>();
//			    queue.offer(managerId);
//
//			    // Iterate until the queue is empty
//			    while (!queue.isEmpty()) {
//			        // Poll the first user ID from the queue
//			        Integer currentUserId = queue.poll();
//
//			        // Find users managed by the current user and add them to the result list
//			        for (Map<String, Object> user : allUsers) {
//			            if (currentUserId.equals(user.get("manageBy"))) {
//			                managedUsers.add(user);
//			                // Add the managed user's ID to the queue for further exploration
//			                queue.offer((Integer) user.get("userid"));
//			            }
//			        }
//			    }
//
//			    return managedUsers;
//			}

	public Admin getByPhoneNo(String phoneNo) {
		return adRepo.findByPhoneNo(phoneNo);
	}

	public Admin savePassword(Admin admin, String newPassword) {

		admin.setPassword(newPassword);
		return adRepo.save(admin);
	}

	public List<Map<String, Object>> getAllClientMonitoringData(int page, int size) throws ParseException {
		Pageable pageable = PageRequest.of(page, size, Sort.by("Userid").descending());
		Page<User> usersPage = uRepo.findUsersByRoleIn(Arrays.asList("AGENT", "SUBAGENT"), pageable);

		List<User> users = usersPage.getContent(); // Get content of the current page

		List<Map<String, Object>> monitoringData = new ArrayList<>();

		for (User user : users) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("userid", user.getUserid());
			userMap.put("username", user.getUsername());
			userMap.put("role", user.getRole());
			userMap.put("usermobile", user.getUsermobile());

			// Customer information
			List<Map<String, Object>> customerDetails = new ArrayList<>();
			List<Map<String, Object>> transactionDetails = new ArrayList<>();
			List<Sell> sells = user.getSell();

			for (Sell sell : sells) {
				// Customer information
				Customer customer = sell.getCustomer();
				if (customer != null) {
					Map<String, Object> customerMap = new HashMap<>();
					customerMap.put("customername", customer.getCustomername());
					customerMap.put("customeremail", customer.getCustomeremail());
					customerMap.put("customermobile", customer.getCustomermobile());
					customerMap.put("customerstate", customer.getCustomerstate());
					customerMap.put("customerdistrict", customer.getCustomerdistrict());
					customerMap.put("cutomercityorvillage", customer.getCustomercityorvillage());

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
						String formattedDate = product.getSubRenewalDate() != null
								? DateUtil.dateToString(product.getSubRenewalDate(), "yyyy-MM-dd")
								: null;
						customerMap.put("subRenewalDate", formattedDate);
						customerMap.put("subRenewalStatus", sell.getRenewalStatus());
					}

					customerDetails.add(customerMap);
				}

				// Transaction details
				if (sell.getPayments() != null && !sell.getPayments().isEmpty()) {
					for (Payment payment : sell.getPayments()) {
						Map<String, Object> transactionMap = new HashMap<>();
						transactionMap.put("transactionId", payment.getTransactionId());
						transactionMap.put("transactionDateTime", payment.getTransactionDate());
						transactionMap.put("transactionAmount", payment.getAmount());
						transactionDetails.add(transactionMap);
					}
				}
			}
			userMap.put("customers", customerDetails);
			userMap.put("transactions", transactionDetails);

			monitoringData.add(userMap);
		}
		monitoringData.forEach(map -> {
			List<Map<String, Object>> customers = (List<Map<String, Object>>) map.get("customers");
			if (customers != null && !customers.isEmpty()) {
				Collections.reverse(customers);
			}
		});

		return monitoringData;
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

	public Map<String, String> getTotalCommissionsByRoles() {
		Map<String, String> commissionMap = new HashMap<>();

		// Calculate total commission for each role
		commissionMap.put("stateHeadTotalCommission", formatIndianRupees(getTotalCommissionByRole("STATEHEAD")));
		commissionMap.put("districtHeadTotalCommission", formatIndianRupees(getTotalCommissionByRole("DISTRICTHEAD")));
		commissionMap.put("cityHeadTotalCommission", formatIndianRupees(getTotalCommissionByRole("CITYHEAD")));
		commissionMap.put("agentTotalCommission", formatIndianRupees(getTotalCommissionByRole("AGENT")));
		commissionMap.put("subAgentTotalCommission", formatIndianRupees(getTotalCommissionByRole("SUBAGENT")));
		commissionMap.put("countryHeadTotalCommission", formatIndianRupees(getTotalCommissionByRole("COUNTRYHEAD")));

		return commissionMap;
	}

	private double getTotalCommissionByRole(String role) {
		List<User> users = uRepo.findByRole(role);
		return users.stream().mapToDouble(User::getTotalCommissionAmount).sum();
	}

	private String formatIndianRupees(double amount) {
		NumberFormat indianFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		return indianFormat.format(amount);
	}

//	public Map<String, Double> groupAndCalculateSales() {
//	    List<Sell> sellList = sellRepo.findAll();
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

//		 public List<Map<String, Object>> getUserOverview() {
//			    // This method should return all users along with their manager information
//			    // Assuming it returns a list of maps with keys including "userid" and "managedBy"
//			    // Example user map: {"userid": 1, "managedBy": 0, "username": "admin"}
//			}
//
//			public List<Map<String, Object>> getUsersManagedBy(Integer managerId, List<Map<String, Object>> allUsers) {
//			    List<Map<String, Object>> managedUsers = new ArrayList<>();
//
//			    for (Map<String, Object> user : allUsers) {
//			        if (managerId.equals(user.get("managedBy"))) {
//			            managedUsers.add(user);
//			            managedUsers.addAll(getUsersManagedBy((Integer) user.get("userid"), allUsers));
//			        }
//			    }
//
//			    return managedUsers;
//			}

//	public List<Sell> getSalesDataForUserHierarchy(int userId) {
//	    // Ensure a valid user ID is provided
//	    if (userId <= 0) {
//	        throw new IllegalArgumentException("Invalid User ID");
//	    }
//
//	    // Retrieve the root user and throw a custom exception if not found
////	    User rootUser = uRepo.findById(userId).orElseThrow(() -> 
////	        new UserNotFoundException("User with ID " + userId + " not found")
////	    );
//	    User rootUser = uRepo.findById(userId).orElseThrow(() -> 
//	    new NoSuchElementException("User with ID " + userId + " not found")
//	);
//
//	    List<Sell> salesDataList = new ArrayList<>();
//	    // Fetch the sales data for the root user and its hierarchy
//	    List<User> hierarchicalUsers = getHierarchicalUsers(rootUser, new HashSet<>());
//	    
//	    for (User user : hierarchicalUsers) {
//	        List<Sell> userSales = sellRepo.findBySoldbyUserId(user.getUserid());
//	        if (userSales != null) {
//	            salesDataList.addAll(userSales);
//	        }
//	    }
//
//	    return salesDataList;
//	}
//	private List<User> getHierarchicalUsers(User rootUser, Set<Integer> visitedUserIds) {
//	    if (visitedUserIds == null) {
//	        throw new IllegalArgumentException("Visited User IDs set cannot be null");
//	    }
//
//	    // Return early if the user has already been visited to prevent cycles
//	    if (visitedUserIds.contains(rootUser.getUserid())) {
//	        return Collections.emptyList();  // Return an empty list to avoid recursion
//	    }
//
//	    visitedUserIds.add(rootUser.getUserid());
//	    List<User> hierarchicalUsers = new ArrayList<>();
//	    List<User> directSubordinates = uRepo.findByManageBy(rootUser.getUserid());
//
//	    if (directSubordinates != null) {
//	        for (User subordinate : directSubordinates) {
//	            hierarchicalUsers.add(subordinate);
//	            hierarchicalUsers.addAll(getHierarchicalUsers(subordinate, visitedUserIds));
//	        }
//	    }
//
//	    return hierarchicalUsers;
//	}

//
//	public Map<String, Object> populateCommissionDetails(Sell row) {
//	    if (row == null) {
//	        throw new IllegalArgumentException("Sell object cannot be null");
//	    }
//
//	    Map<String, Object> commissionDetails = new HashMap<>();
//	    commissionDetails.put("salestatus", safeGet(row.getSalestatus(), "Unknown"));
//	    commissionDetails.put("username", safeGet(row.getUsername(), "Unknown"));
//	    commissionDetails.put("userrole", safeGet(row.getUserrole(), "Unknown"));
//	    commissionDetails.put("date", safeGet(row.getRegisterDate(), "Unknown"));
//	    commissionDetails.put("transactionId", safeGet(row.getSellid(), -1));
//	    commissionDetails.put("productname", safeGet(row.getProductname(), "Unknown"));
//	    commissionDetails.put("payment_received", safeGet(row.getSaleamount(), 0.0));
//
//	    double commissionAmount = getCommissionAmount(row);
//	    commissionDetails.put("commissionAmount", commissionAmount);
//
//	    double commissionRate = getCommissionRate(row);
//	    commissionDetails.put("commissionRate", commissionRate);
//
//	    return commissionDetails;
//	}
//
//	// Helper method to safely get a value with a default
//	private <T> T safeGet(T value, T defaultValue) {
//	    return value != null ? value : defaultValue;
//	}
//
//	// Helper method to get commission amount based on user role
//	private double getCommissionAmount(Sell row) {
//	    switch (row.getUserrole()) {
//	        case "AGENT":
//	            return safeGet(row.getAcomm(), 0.0);
//	        case "SUBAGENT":
//	            return safeGet(row.getSAcomm(), 0.0);
//	        case "CITYHEAD":
//	            return safeGet(row.getCityhcomm(), 0.0);
//	        case "DISTRICTHEAD":
//	            return safeGet(row.getDHcomm(), 0.0);
//	        case "STATEHEAD":
//	            return safeGet(row.getSTcomm(), 0.0);
//	        case "COUNTRYHEAD":
//	            return safeGet(row.getCHcomm(), 0.0);
//	        default:
//	            return 0.0;
//	    }
//	}
//
//	// Helper method to get commission rate based on user role and product
//	private double getCommissionRate(Sell row) {
//	    Product product = pRepo.findProductByPname(safeGet(row.getProductname(), ""));
//	    if (product == null) {
//	        return 0.0;
//	    }
//	    switch (row.getUserrole()) {
//	        case "AGENT":
//	            return product.getAcomm();
//	        case "SUBAGENT":
//	            return product.getSAcomm();
//	        case "CITYHEAD":
//	            return product.getCityhcomm();
//	        case "DISTRICTHEAD":
//	            return product.getDHcomm();
//	        case "STATEHEAD":
//	            return product.getSTcomm();
//	        case "COUNTRYHEAD":
//	            return product.getCHcomm();
//	        default:
//	            return 0.0;
//	    }
//	}

//
//	public List<Sell> getCommissionDetailsByUserRole(String userRole) {
//		// Fetch commission details based on the user role from the database
//		List<Sell> commissionDetails = new ArrayList<>();
//		switch (userRole) {
//		case "AGENT":
//			commissionDetails = sellRepo.findByUserrole("AGENT");
//			break;
//		case "SUBAGENT":
//			commissionDetails = sellRepo.findByUserrole("SUBAGENT");
//			break;
//		case "CITYHEAD":
//			commissionDetails = sellRepo.findByUserrole("CITYHEAD");
//			break;
//		case "STATEHEAD":
//			commissionDetails = sellRepo.findByUserrole("STATEHEAD");
//			break;
//		case "COUNTRYEHAD":
//			commissionDetails = sellRepo.findByUserrole("COUNTRYHEAD");
//			break;
//		// Add cases for other user roles if needed
//		default:
//			// Handle invalid user roles
//			throw new IllegalArgumentException("Invalid user role: " + userRole);
//		}
//		return commissionDetails;
//	}

//
//	// Helper method to populate commission details
//	public Map<String, Object> populateCommissionDetails(Sell row) {
//		Map<String, Object> commissionDetails = new HashMap<>();
//		commissionDetails.put("salestatus", row.getSalestatus());
//		commissionDetails.put("username", row.getUsername());
//		commissionDetails.put("userrole", row.getUserrole());
//		commissionDetails.put("date", row.getRegisterDate());
//		commissionDetails.put("transactionId", row.getSellid());
//		commissionDetails.put("productname", row.getProductname());
//		commissionDetails.put("payment_received", row.getSaleamount());
//		double commissionAmount = getCommissionAmount(row);
//		commissionDetails.put("commissionAmount", commissionAmount);
//		double commissionRate = getCommissionRate(row);
//		commissionDetails.put("commissionRate", commissionRate);
//		return commissionDetails;
//	}
//
//	// Helper method to get commission amount based on user role
//	private double getCommissionAmount(Sell row) {
//		switch (row.getUserrole()) {
//		case "AGENT":
//			return row.getAcomm();
//		case "SUBAGENT":
//			return row.getSAcomm();
//		case "CITYHEAD":
//			return row.getCityhcomm();
//		case "DISTRICTHEAD":
//			return row.getDHcomm();
//		case "STATEHEAD":
//			return row.getSTcomm();
//		case "COUNTRYHEAD":
//			return row.getCHcomm();
//		default:
//			return 0.0;
//		}
//	}
//
//	// Helper method to get commission rate based on user role
//	private double getCommissionRate(Sell row) {
//		Product product = pRepo.findProductByPname(row.getProductname());
//		if (product == null) {
//			return 0.0;
//		}
//		switch (row.getUserrole()) {
//		case "AGENT":
//			return product.getAcomm();
//		case "SUBAGENT":
//			return product.getSAcomm();
//		case "CITYHEAD":
//			return product.getCityhcomm();
//		case "DISTRICTHEAD":
//			return product.getDHcomm();
//		case "STATEHEAD":
//			return product.getSTcomm();
//		case "COUNTRYHEAD":
//			return product.getCHcomm();
//		default:
//			return 0.0;
//		}
//	}

//  public boolean isAdmin(Integer adminId) {
//  // Fetch the user from the database based on the user ID
//  Optional<User> userOptional = uRepo.findById(userId);
//  
//  // Check if the user exists and if the user's role indicates admin privileges
//  return userOptional.isPresent() && userOptional.get().getRole().equalsIgnoreCase("admin");
//}

//public void setUserTarget(Map<String, Object> request) {
//	// Extract fields from the request map
//	Integer userId = (Integer) request.get("userId");
//	String userRole = (String) request.get("userRole");
//	ObjectMapper objectMapper = new ObjectMapper();
//	List<String> productName = objectMapper.convertValue(request.get("products"),
//			new TypeReference<List<String>>() {
//			});
//	String duration = (String) request.get("duration");
//	Integer targetAmount = (Integer) request.get("targetAmount");
//	boolean achivedAmount = (boolean) request.get("IsAmountachived");
//
//	// Validate duration
//	if (!isValidDuration(duration)) {
//		throw new IllegalArgumentException("Invalid duration. Duration must be 'month', 'quarter', or 'year'.");
//	}
//
//	// Check if all required fields are present
//	if (userId == null || userRole == null || productName == null || productName.isEmpty() || duration == null
//			|| targetAmount == null) {
//		throw new IllegalArgumentException("Missing required fields in request.");
//	}
//
//	// Find the user by ID
//	Optional<User> userOptional = uRepo.findById(userId);
//	if (!userOptional.isPresent()) {
//		throw new IllegalArgumentException("User with ID " + userId + " not found.");
//	}
//	User user = userOptional.get();
//
//	// Fetch the products based on their names
//	List<Product> products = pRepo.findByPname(productName);
//
//	// Set the target for the user
//	user.setRole(userRole);
//	user.setProducts(products);
//	user.setDuration(duration);
//	user.setTargetAmount(targetAmount);
//	user.setIsAmountachived(achivedAmount); // Initially set archived amount to 0
//
//	// Save the updated user entity
//	uRepo.save(user);
//}

//    public List<Map<String, Object>> generateReport() {
//	    List<Map<String, Object>> reportData = new ArrayList<>();
//
//	    // Fetch users and payments data
//	    List<User> users = uRepo.findAll();
//	    List<Payment> payments = paymentRepo.findAll();
//
//	    // Prepare report data
//	    for (User user : users) {
//	        for (Payment payment : payments) {
//	            if (user.getUserid() == payment.getUser().getUserid()) {
//	                Map<String, Object> userData = new HashMap<>();
//	                userData.put("name", user.getUsername());
//	                userData.put("phone_number", user.getUsermobile());
//	                userData.put("email", user.getUseremail());
//	                userData.put("register_date", user.getCreateddate());
//	                userData.put("user_role", user.getRole());
//	                userData.put("transaction_date", payment.getTransactionDateTime());
//	                userData.put("transaction_amount", payment.getTransactionAmount());
//	                userData.put("total_commission_earned", user.getTotalCommissionAmount());
//	                userData.put("payment_id", payment.getPaymentId());
//	                reportData.add(userData);
//	            }
//	        }
//	    }
//
//	    return reportData;
//	}

//    public void setPromotionCriteria(Map<String, Object> request) {
//        // Extract fields from the request map
//        Integer sellId = (Integer) request.get("sellId");
//        String userRole = (String) request.get("userRole");
//        Integer categoryId = (Integer) request.get("categoryId");
//        Long saleAmount = (Long) request.get("saleAmount");
//        Integer units = (Integer) request.get("units");
//        String duration = (String) request.get("duration");
//
//        if (!isValidDuration(duration)) {
//			throw new IllegalArgumentException("Invalid duration. Duration must be 'month', 'quarter', or 'year'.");
//		}
//        // Check if all required fields are present
//        if (sellId == null || userRole == null || categoryId == null || saleAmount == null || units == null || duration == null) {
//            throw new IllegalArgumentException("Missing required fields in request.");
//        }
//
//        // Find the sell by ID
//        Optional<Sell> sellOptional = sellRepo.findById(sellId);
//        if (!sellOptional.isPresent()) {
//            throw new IllegalArgumentException("Sell with ID " + sellId + " not found.");
//        }
//        Sell sell = sellOptional.get();
//
//        // Find the product category by ID
//        Optional<ProductCategory> categoryOptional = pcRepo.findById(categoryId);
//        if (!categoryOptional.isPresent()) {
//            throw new IllegalArgumentException("Product category with ID " + categoryId + " not found.");
//        }
//        ProductCategory productCategory = categoryOptional.get();
//
//        // Set the promotion criteria for the sell
//        sell.setUserrole(userRole);
//        sell.setProductcategory(productCategory);
//        sell.setSaleamount(saleAmount);
//        sell.setUnits(units);
//        // Assuming 'duration' is a field in Sell entity
//        sell.setDuration(duration);
//
//        // Save the updated sell entity
//        sellRepo.save(sell);
//    }
//    public void setPromotionCriteria(Map<String, Object> request) {
//        // Extract fields from the request map
//        Integer sellId = (Integer) request.get("sellId");
//        String userRole = (String) request.get("userRole");
//        List<Map<String, Object>> categories = (List<Map<String, Object>>) request.get("categories");
//        String duration = (String) request.get("duration");
//
//        if (!isValidDuration(duration)) {
//            throw new IllegalArgumentException("Invalid duration. Duration must be 'month', 'quarter', or 'year'.");
//        }
//
//        // Check if all required fields are present
//        if (sellId == null || userRole == null || categories == null || categories.isEmpty() || duration == null) {
//            throw new IllegalArgumentException("Missing required fields in request.");
//        }
//
//        // Find the sell by ID
//        Optional<Sell> sellOptional = sellRepo.findById(sellId);
//        if (!sellOptional.isPresent()) {
//            throw new IllegalArgumentException("Sell with ID " + sellId + " not found.");
//        }
//        Sell sell = sellOptional.get();
//
//        // Set the user role and duration for the sell
//        sell.setUserrole(userRole);
//        sell.setDuration(duration);
//
//        // Set the promotion criteria for each category
//        for (Map<String, Object> categoryMap : categories) {
//            String categoryName = (String) categoryMap.get("categoryName");
//            Integer units = (Integer) categoryMap.get("units");
//
//            // Find the product category by name
//            Optional<ProductCategory> categoryOptional = pcRepo.findByPCatagory(categoryName);
//            if (!categoryOptional.isPresent()) {
//                throw new IllegalArgumentException("Product category with name " + categoryName + " not found.");
//            }
//            ProductCategory productCategory = categoryOptional.get();
//
//            // Set the units for the product category
//            sell.setProductcategory(productCategory);
//            sell.setUnits(units);
//
//            // Save the updated sell entity
//            sellRepo.save(sell);
//        }
//    }
//    public void setPromotionCriteria(Map<String, Object> request) {
//        // Extract fields from the request map
//        List<Map<String, Object>> sells = (List<Map<String, Object>>) request.get("sells");
//        String duration = (String) request.get("duration");
//
//        if (!isValidDuration(duration)) {
//            throw new IllegalArgumentException("Invalid duration. Duration must be 'month', 'quarter', or 'year'.");
//        }
//
//        // Check if all required fields are present
//        if (sells == null || sells.isEmpty() || duration == null) {
//            throw new IllegalArgumentException("Missing required fields in request.");
//        }
//
//        // Iterate over each sell
//        for (Map<String, Object> sellMap : sells) {
//            Integer sellId = (Integer) sellMap.get("sellId");
//            String userRole = (String) sellMap.get("userRole");
//            String categoryName = (String) sellMap.get("categoryName"); // Assuming categoryName is the name of the category
//            Integer units = (Integer) sellMap.get("units");
//            long saleAmount = (Long) sellMap.get("saleAmount");
//
//            // Find the sell by ID
//            Optional<Sell> sellOptional = sellRepo.findById(sellId);
//            if (!sellOptional.isPresent()) {
//                throw new IllegalArgumentException("Sell with ID " + sellId + " not found.");
//            }
//            Sell sell = sellOptional.get();
//
//            // Find the product category by name
//            Optional<ProductCategory> categoryOptional = pcRepo.findByPCatagory(categoryName);;
//            if (!categoryOptional.isPresent()) {
//                throw new IllegalArgumentException("Product category with name " + categoryName + " not found.");
//            }
//            ProductCategory productCategory = categoryOptional.get();
//
//            // Set the user role, product category, units, and sale amount for the sell
//            sell.setUserrole(userRole);
//            sell.setProductcategory(productCategory);
//            sell.setUnits(units);
//            sell.setSaleamount(saleAmount);
//            sell.setDuration(duration);
//
//            // Save the updated sell entity
//            sellRepo.save(sell);
//        }
//    }
// 

//    public void setPromotionCriteria(Map<String, Object> request) {
//        // Extract fields from the request map
//        List<Map<String, Object>> userRoles = (List<Map<String, Object>>) request.get("userRoles");
//        String duration = (String) request.get("duration");
//
//        if (!isValidDuration(duration)) {
//            throw new IllegalArgumentException("Invalid duration. Duration must be 'month', 'quarter', or 'year'.");
//        }
//
//        // Check if all required fields are present
//        if (userRoles == null || userRoles.isEmpty() || duration == null) {
//            throw new IllegalArgumentException("Missing required fields in request.");
//        }
//
//        // Iterate over each user role
//        for (Map<String, Object> userRoleMap : userRoles) {
//            String userRoleName = (String) userRoleMap.get("userRole");
//            User userRole = (User) uRepo.findByURole(userRoleName);
//            if (userRole == null) {
//                throw new IllegalArgumentException("User role with name " + userRoleName + " not found.");
//            }
//
//            List<Map<String, Object>> sells = (List<Map<String, Object>>) userRoleMap.get("sells");
//
//            // Check if sells data is present for the user role
//            if (sells == null || sells.isEmpty()) {
//                throw new IllegalArgumentException("Missing sells data for user role: " + userRoleName);
//            }
//
//            // Iterate over each sell for the user role
//            for (Map<String, Object> sellMap : sells) {
//                String categoryName = (String) sellMap.get("categoryName");
//                Integer units = (Integer) sellMap.get("units");
//                Integer saleAmountPerUnit = (Integer) sellMap.get("saleAmountPerUnit");
//
//                // Calculate the total sales amount for the sell record
//                long totalSalesAmount = units * saleAmountPerUnit;
//
//                // Find the product category by name
//                Optional<ProductCategory> categoryOptional = pcRepo.findByPCatagory(categoryName);
//                if (!categoryOptional.isPresent()) {
//                    throw new IllegalArgumentException("Product category with name " + categoryName + " not found.");
//                }
//                ProductCategory productCategory = categoryOptional.get();
//
//                // Create a new Promotion entity
//                Promotion promotion = new Promotion();
//                promotion.setUser(userRole);
//                promotion.setProductCategory(productCategory);
//                promotion.setUnits(units);
//                promotion.setSaleAmount(totalSalesAmount);
//                promotion.setDuration(duration);
//
//                // Save the new promotion entity
//                proRepo.save(promotion);
//            }
//        }
//    }

//    public List<Sell> getAllCommissions() {
//        return sellRepository.findAll();
//    }
//
//    public List<Sell> getCommissionDetailsByUserRoleAndSaleStatus(String role, String saleStatus) {
//        return sellRepository.findByUserroleAndSalestatus(role, saleStatus);
//    }

}
