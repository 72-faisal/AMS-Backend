package com.gujjumarket.AgentManagmentSystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.repo.SellRepo;

import jakarta.transaction.Transactional;

@Component
public class SubscriptionRenewalChecker {

	@Autowired
	private SellRepo sellRepo;
	
	@Autowired
	UserService userService;
	
	@Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    @Transactional
    public void checkForExpiredSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Sell> sells = sellRepo.findAll();
        for (Sell sell : sells) {
            if (sell.getRenewaldate() != null && sell.getRenewaldate().toLocalDate().isBefore(today)) {
                sell.setRenewalStatus("Expired");
                sellRepo.save(sell);
            }
        }
    }
	
//	@Scheduled(cron = "*/1 * * * * *") // Run every second
//    public void checkTargets() {
//        userService.checkAndUpdateTargetAchieved();
//    }
}
