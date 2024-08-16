	package com.gujjumarket.AgentManagmentSystem.repo;
	
	import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.Product;
import com.gujjumarket.AgentManagmentSystem.model.Sell;
import com.gujjumarket.AgentManagmentSystem.model.User;
	
	@Repository
	public interface SellRepo extends JpaRepository<Sell, Integer> {
	
		@Query("SELECT s FROM Sell s WHERE s.Userrole = :Userrole")
		List<Sell> findByUserrole(@Param("Userrole") String Userrole);
	//	List<Sell> findBysellidAndTimePeriod(Integer sellId, String saleStatus);
	//
	//	List<Sell> findByTeamLevelAndTimePeriod(String teamLevel, String timePeriod);
	
		@Query("SELECT COUNT(s) FROM Sell s WHERE s.soldby.Userid = :Userid")
		int countBySoldbyId(Integer Userid);
	
		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid")
		List<Sell> findBySoldbyUserId(Integer Userid);
	
	//	@Query("SELECT s FROM Sell s ORDER BY s.approvedDate DESC")
	//	Sell findFirstByOrderByApprovedDateDesc();
	
		@Query("SELECT s FROM Sell s ORDER BY s.approvedDate DESC")
		List<Sell> findAllByOrderByApprovedDateDesc();
		
	    @Query("SELECT s FROM Sell s WHERE s.approvedDate = (SELECT MAX(s2.approvedDate) FROM Sell s2)")
		Collection<Sell> findLastByOrderByApprovedDateDescLimit1();
	
		void save(User salesTarget);

		
//		List<Sell> getSalesDetailsByUserId(int userId);
		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid")
		List<Sell> findBySoldbyUserId(int Userid);

		@Query("SELECT s FROM Sell s WHERE s.Userrole = :role AND s.salestatus = :saleStatus")
	    List<Sell> findByUserRoleAndSaleStatus(@Param("role") String role, @Param("saleStatus") String saleStatus);

//		@Query("SELECT * FROM Sell WHERE soldby = :Userid")
//		List<Sell> findBySoldBy(User user);

//		List<Sell> findBySoldBy(User u);

//		@Query("SELECT SUM(s.Saleamount) FROM Sell s WHERE s.soldby.Userid = :userid AND s.product.productcategory.pcid = :pcid")
//		Double calculateTotalSaleAmount(@Param("userid") int userid, @Param("pcid") int pcid);

//		double findSaleAmountByUserId(Long id);

//		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid ORDER BY s.approvedDate DESC")
//		Sell findFirstByOrderByApprovedDateDescForUser(@Param("Userid") int userId);
//
//		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid ORDER BY s.approvedDate DESC")
//		List<Sell> findAllByUserAndOrderByApprovedDateDesc(@Param("Userid") int userId);

		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid ORDER BY s.approvedDate DESC")
		List<Sell> findTop1ByUserIdOrderByApprovedDateDesc(@Param("Userid") int userId);

		@Query("SELECT s FROM Sell s WHERE s.soldby.Userid = :Userid ORDER BY s.approvedDate DESC")
		List<Sell> findBySoldbyUserIdOrderByApprovedDateDesc(@Param("Userid") int userId);

		

		@Query("SELECT s FROM Sell s WHERE s.approvedDate BETWEEN :startOfMonth AND :endOfMonth")
	    List<Sell> findByApprovedDateBetween(@Param("startOfMonth") Date startOfMonth, @Param("endOfMonth") Date endOfMonth);

		 @Query("SELECT COUNT(s) FROM Sell s WHERE s.Productname = :productName AND s.approvedDate BETWEEN :startDate AND :endDate")
		    int countByProductNameAndApprovedDateBetween(
		            @Param("productName") String productName,
		            @Param("startDate") Date startDate,
		            @Param("endDate") Date endDate
		    );
		

		
//		List<Sell> findBySoldby_Userid(int userId);

	
//		@Query("SELECT s.Productname, s.sellid, s.Saleamount, s.salestatus, u.role " +
//			       "FROM Sell s " +
//			       "INNER JOIN s.soldby u " +  // Assuming Sell entity has a ManyToOne relationship with User entity as soldby
//			       "WHERE u.Userid = :userId")
//			List<Object[]> getSalesDetailsForUserAndTeam(@Param("userId") Integer userId);
//
//			@Query("SELECT s.Productname, s.sellid, s.Saleamount, s.salestatus, u.role " +
//			       "FROM Sell s " +
//			       "INNER JOIN s.soldby u " +  // Assuming Sell entity has a ManyToOne relationship with User entity as soldby
//			       "WHERE u.Userid = :userId")
//			List<Object[]> getSalesDetailsByUserId(@Param("userId") Integer userId);

	
	
	
		
	//	Sell findByUserId(int userid);
	
	}
