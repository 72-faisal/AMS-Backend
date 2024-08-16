package com.gujjumarket.AgentManagmentSystem.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gujjumarket.AgentManagmentSystem.model.Payment;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;
import com.gujjumarket.AgentManagmentSystem.service.SellService;
import com.gujjumarket.AgentManagmentSystem.service.TransactionService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


	 	@Autowired
	    private TransactionService transactionService;
	 	
	 	@Autowired
	 	SellRepo sellRepo;

//	 @PostMapping("/fetch")
//	    public ResponseEntity<?> getTransactions(@RequestBody(required = false) Map<String, Long> requestBody) {
//	        if (requestBody != null && requestBody.containsKey("id")) {
//	            Long id = requestBody.get("id");
//	            Payment transaction = transactionService.getTransactionById(id);
//	            return ResponseEntity.ok().body(transaction);
//	        } else {
//	            List<Payment> transactions = transactionService.getAllTransactions();
//	            Collections.reverse(transactions);
//	            return ResponseEntity.ok().body(transactions);
//	        }
//	    }
	    @PostMapping("/create")
	    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
	        Payment createdPayment = transactionService.createPayment(payment);
	        return ResponseEntity.status(201).body("PAYMENT STATUS"+payment.getTransactionStatus());
	    }
//	 
//	 @PostMapping("/create1")
//	    public ResponseEntity<?> createPayment1(@RequestBody Payment payment) {
//		 Integer sellId = payment.getSell().getSellid();
//		    Sell sell = sellRepo.findById(sellId).orElse(null);
//		    if (sell == null) {
//		        return ResponseEntity.status(404).body("Sell entity not found");
//		    }
//		    
//		    payment.setAmount((int)sell.getSaleamount());
//	        Payment createdPayment = transactionService.createPayment(payment);
//	        return ResponseEntity.status(201).body("PAYMENT STATUS "+payment.getTransactionStatus());
//	    }
//	    @PutMapping("/update")
//	    public ResponseEntity<Payment> updateTransaction(@RequestBody Payment transactionDetails) {
//	        Payment updatedTransaction = transactionService.updateTransaction(transactionDetails.getId(), transactionDetails);
//	        return ResponseEntity.ok(updatedTransaction);
//	    }
//
//	    @DeleteMapping("/delete")
//	    public ResponseEntity<Void> deleteTransaction(@RequestBody Map<String, Long> requestBody) {
//	        Long id = requestBody.get("id");
//	        transactionService.deleteTransaction(id);
//	        return ResponseEntity.noContent().build();
//	    }
	 	
//	 	  @PostMapping("/pay")
//	 	    public ResponseEntity<String> initiatePayment(@RequestParam String userid, @RequestParam double amount) {
//	 	        try {
//	 	            String orderId = transactionService.createOrder(userid, amount);
//	 	            if (orderId != null) {
//	 	                String razorpayPaymentLink = "https://api.razorpay.com/v1/payment?order_id=" + orderId  + "&amount=" + amount;
//	 	                return ResponseEntity.status(HttpStatus.OK).body(razorpayPaymentLink);
//	 	            } else {
//	 	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Razorpay order");
//	 	            }
//	 	        } catch (RazorpayException e) {
//	 	            e.printStackTrace();
//	 	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Razorpay error: " + e.getMessage());
//	 	        }
//	 	    }
	 	 @PostMapping("/generateLink")
	     public ResponseEntity<Payment> generatePaymentLink(@RequestBody Payment request) {
	         Payment response = transactionService.generatePaymentLink(request);
	         return ResponseEntity.ok(response);
	     }
}
