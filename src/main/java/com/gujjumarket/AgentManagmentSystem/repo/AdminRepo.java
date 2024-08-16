package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.Admin;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {

	
	@Query("SELECT a FROM Admin a WHERE a.phoneNo = :phoneNo AND a.password = :password")
    Admin findByPhoneNoAndPassword(@Param("phoneNo") String phoneNo, @Param("password") String password);

//	@Query("SELECT a FROM Admin a WHERE ROWNUM = 1")
//	Optional<Admin> findAny();

	@Query(value = "SELECT * FROM Admin LIMIT 1", nativeQuery = true)
	Optional<Admin> findFirst();

	 Admin findByPhoneNo(String phoneNo);

//	@Query("SELECT a FROM Admin a WHERE a.name = :name")
//	Optional<Admin> getByUsername(String name);

//	@Query("SELECT a FROM Admin a WHERE a.name = :name")
//	Optional<Admin> findByUsername(String uniqueIdentifier);

//	User getUserById(Integer userId);
	
	

}
