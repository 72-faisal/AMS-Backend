package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.User;
import com.gujjumarket.AgentManagmentSystem.model.WithdrawalRequest;

@Repository
public interface withdrawalRequestRepo extends JpaRepository<WithdrawalRequest, Long>{

//	@Query("SELECT * FROM WithdrawalRequest WHERE status = :status")
	List<WithdrawalRequest> findByStatus(String string);

//	List<WithdrawalRequest> findAllByUser(User user);

	List<WithdrawalRequest> findAllByUser(User user);

	List<WithdrawalRequest> findByUser(User user);

	@Query("SELECT w.withdrawalId, w.requestDate, u.Username, w.role, w.status, w.amount, u.Useremail, u.Usermobile, w.WitdhrawalDate " +
		       "FROM WithdrawalRequest w JOIN w.user u " +
		       "WHERE w.status IN ('pending', 'approved', 'UnApproved')")
		List<Object[]> findByStatusInPendingOrApprovedOrUnApproved();

//	WithdrawalRequest getByUserId(int userid);

//	WithdrawalRequest getByUser(User user);

	

}
	