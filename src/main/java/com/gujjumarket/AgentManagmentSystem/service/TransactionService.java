package com.gujjumarket.AgentManagmentSystem.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gujjumarket.AgentManagmentSystem.model.Customer;
import com.gujjumarket.AgentManagmentSystem.model.Payment;
import com.gujjumarket.AgentManagmentSystem.model.RoleAmount;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.repo.CustomerRepo;
import com.gujjumarket.AgentManagmentSystem.repo.RoleAmountRepo;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.repo.TransactionRepository;
import com.gujjumarket.AgentManagmentSystem.repo.Userrepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CustomerRepo cRepo;
	
	@Autowired
	Userrepo userrepo;
	
	@Autowired
	SellRepo sellRepo;
	
	@Value("${razorpay.api.key}")
	private String razorpayKeyId;

	@Value("${razorpay.api.secret}")
	private String razorpayKeySecret;
	
	private RazorpayClient razorpayClient;
	
	@Autowired
	private RoleAmountRepo amountRepo;
	
	private RestTemplate restTemplate = new RestTemplate();

    public void RazorpayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
	
	@PostConstruct
	public void init() throws RazorpayException {
		this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
	}
	
//	 public List<Payment> getAllTransactions() {
//	        return transactionRepository.findAll();
//	    }
//
//	    public Payment getTransactionById(Long id) {
//	        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
//	    }
//	
//	    public Payment updateTransaction(Long id, Payment transactionDetails) {
//	        Payment transaction = transactionRepository.findById(id)
//	                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//
//	        transaction.setTransactionId(transactionDetails.getTransactionId());
////	        transaction.setAmount(transactionDetails.getAmount());
////	        transaction.setTransactionDate(transactionDetails.getTransactionDate());
//	        transaction.setTransactionStatus(transactionDetails.getTransactionStatus());
//
//	        return transactionRepository.save(transaction);
//	    }
//
//	    public void deleteTransaction(Long id) {
//	        Payment transaction = transactionRepository.findById(id)
//	                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//	        transactionRepository.delete(transaction);
//	    }
//
	    public Payment createPayment(Payment payment) {
	        // Fetch the customer based on the provided cid
	        if (payment.getCustomer() != null && payment.getCustomer().getCid() != null) {
	            Customer customer = cRepo.findById(payment.getCustomer().getCid())
	                    .orElseThrow(() -> new RuntimeException("Customer not found"));
	            payment.setCustomer(customer);
	        } else {
	            throw new RuntimeException("Customer ID is required");
	        }

	        // Fetch the sell based on the provided sellId
	        if (payment.getSell() != null && payment.getSell().getSellid() != 0) {
	            Sell sell = sellRepo.findById(payment.getSell().getSellid())
	                    .orElseThrow(() -> new RuntimeException("Sell not found"));
	            payment.setSell(sell);
	        }

	        // Set the transaction date to the current date if not provided
	        if (payment.getTransactionDate() == null) {
	            payment.setTransactionDate(Date.valueOf(LocalDate.now(ZoneId.of("UTC"))));
	        }

	        return transactionRepository.save(payment);
	    }

//		 public Order createOrder(double amount, String currency) throws Exception {
//		        JSONObject orderRequest = new JSONObject();
//		        orderRequest.put("amount", amount * 100); // amount in the smallest currency unit
//		        orderRequest.put("currency", currency);
//		        orderRequest.put("receipt", "txn_123456");
//		        orderRequest.put("payment_capture", 1); // auto capture
//
//		        return (Order) razorpayClient.orders.create(orderRequest);
//		    }
	

    public String createOrder(String userid, double amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        try {
            // Convert amount to paise
            long amountInPaise = (long) (amount * 100);
            // Create order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "receipt_" + userid);
            orderRequest.put("payment_capture", 1);
           
            // Create order
            Order order = client.orders.create(orderRequest);
            
            
            return order.get("id").toString();
        } catch (RazorpayException e) {
            e.printStackTrace();
            throw e;
        }
    }

	    public void handlePaymentSuccess(String orderId, String userid) {
	        // Update ispaid field for the user in the database
	        User user = userrepo.findByUserid(userid);
	        if (user != null) {
	            user.setPaid(true);
	            userrepo.save(user);
	        }
	    }

	    public Payment generatePaymentLink(Payment request) {
	        // Retrieve user details from database based on user ID
	        User user = userrepo.findById(request.getUser().getUserid())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        // Logic to generate payment link based on user's role and amount
	        String paymentLink = generateRazorpayLink(user.getRole(), amountRepo.findByRole(user.getRole()));
	        updateUserPaymentStatus(user);

	        
	       // Construct and return response
	        Payment response = new Payment();
	        response.setPaymentLink(paymentLink);
	        // Set other fields in response if necessary
	        return response;
	    }

		private String generateRazorpayLink(String role, RoleAmount byRole) {
			   String paymentLink = "";

		        if (role.equalsIgnoreCase("countryhead")) {
		            paymentLink = "https://rzp.io/i/sMdVNeqt";
		        } else if (role.equalsIgnoreCase("statehead")) {
		            paymentLink = "https://rzp.io/i/8T2h5f07yu" ;
		        } else if (role.equalsIgnoreCase("districthead")) {
		            paymentLink = "https://rzp.io/i/yl8mCYRc" ;
		        } else if (role.equalsIgnoreCase("cityhead")) {
		            paymentLink = "https://rzp.io/i/ezXg2P7O";
		        } else if (role.equalsIgnoreCase("agent")) {
		            paymentLink = "https://rzp.io/i/xRYjfxTF3k";
		        } else if (role.equalsIgnoreCase("subagent")) {
		            paymentLink = "https://rzp.io/i/zUBEZlDXG";
		        }

		        return paymentLink;
		}
		
		
		  private void updateUserPaymentStatus(User user) {
		        user.setPaid(true);
		        userrepo.save(user);
		    }
	
}
