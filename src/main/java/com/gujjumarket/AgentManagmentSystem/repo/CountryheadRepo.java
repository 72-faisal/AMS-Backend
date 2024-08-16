package com.gujjumarket.AgentManagmentSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gujjumarket.AgentManagmentSystem.model.User;

public interface CountryheadRepo extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.Usermobile = :Usermobile")
	User findByusermobile(@Param("Usermobile") long Usermobile);

//	User getReferenceById(User managedBy);


}
