package com.gujjumarket.AgentManagmentSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.User;

@Repository
public interface DistrictheadRepo extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.Usermobile = :Usermobile")
	User findByusermobile(@Param("Usermobile") long Usermobile);
}
