package com.gujjumarket.AgentManagmentSystem.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gujjumarket.AgentManagmentSystem.model.User;

@Repository
public interface Userrepo extends JpaRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.Usermobile = :Usermobile")
	User findByusermobile(@Param("Usermobile") long Usermobile);

	@Query("SELECT u FROM User u WHERE u.ManageBy = :userid1")
	List<User> getmyteam(Integer userid1);

	List<User> findByRoleIn(List<String> asList);

	@Query("SELECT u FROM User u WHERE u.ManageBy = :userId")
    List<User> findByManageBy(@Param("userId") Integer userId);

//	Optional<User> findFristByRole(String targetUserRole);

//	List<User> findByRole(String targetUserRole);
	@Query("SELECT u FROM User u WHERE u.role = :role")
	List<User> findByRole(@Param("role") String role);
//	
//	@Query("SELECT u FROM User u WHERE u.ManageBy IN (SELECT usr.Userid FROM User usr WHERE usr.role = :role)")
//    List<User> findUsersManagedByRole(@Param("role") String role);

	@Query("SELECT u FROM User u WHERE u.Username = :Username")
	User getUserByUsername(String Username);

	@Query("SELECT u FROM User u WHERE u.Userid = :Userid")
	User findByUserid(String Userid);

	@Query("SELECT u FROM User u WHERE u.ManageBy = :userId")
	List<User> findTeamMembersByUserid(@Param("userId") int userId);

	@Query("SELECT u FROM User u WHERE u.ManageBy = :username1")
	List<User> findTeamMembersByUsername(String username1);

	List<User> findAllByRole(String role);

	@Query("SELECT u FROM User u WHERE u.Userid = :Userid AND u.Username = :username")
	List<User> findByUseridAndUsername(Integer Userid, String username);

	@Query("SELECT u FROM User u WHERE u.Userid = :Userid")
	User findByUserid(@Param("Userid") Integer userId);

	@Query("SELECT u FROM User u WHERE u.ManageBy = :Userid AND u.role = 'SUBAGENT'")
	List<User> findSubagentByUserid(@Param("Userid") int userId);

	@Query("SELECT u FROM User u WHERE u.ManageBy IN (SELECT manager.Userid FROM User manager WHERE manager.role = :role)")
	List<User> findUsersManagedByRole(@Param("role") String role);

	@Query("SELECT u FROM User u WHERE u.IsKYCDone = false")
	List<User> findByIsKYCDoneFalse();

//	@Query("SELECT u FROM User u WHERE u.Userid = :userId AND u.isAgent = true")
//	User findByIdAndIsAgentTrue(@Param("userId") Integer userId);

	@Query("SELECT u FROM User u WHERE u.Userid = :Userid AND u.isAgent = true")
	User findByUseridAndIsAgentTrue(@Param("Userid") Integer userId);

	
	List<User> findByrole(String role);

//	@Query("SELECT u FROM User u WHERE u.ManageBy = :managerId")
//    List<User> findByManageBy(@Param("managerId") int managerId);

	@Query("SELECT u FROM User u WHERE u.role = :role AND u.ManageBy = :ManageBy")
    List<User> findByRoleAndManageBy(@Param("role") String role, @Param("ManageBy") int ManageBy);

	@Query("SELECT u.Username, u.Usermobile, u.Useremail, u.State, u.District, u.cityorvillage,  u.createddate " +
           "FROM User u WHERE u.ManageBy = :managerId")
    List<User> findSubagentsByManageBy(@Param("managerId") Integer managerId);

	@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Double countByRole(String role);

	@Query("SELECT u FROM User u WHERE u.role IN :roles")
    Page<User> findUsersByRoleIn(List<String> roles, Pageable pageable);
//
//	List<User> findByActiveTrue();
//
//	Optional<User> findByIdAndActiveTrue(Integer userId);

	@Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.Userid = :id AND u.active = true")
    Optional<User> findActiveUserById(Integer id);

    @Query("SELECT u FROM User u WHERE u.ManageBy = :manageBy AND u.role = :role")
    List<User> findByManageByAndRole(@Param("manageBy") int manageBy, @Param("role") String role);
	
	
//	List<User> findByRole1(String role);
//	List<User> findUsersManagedBy(int userid);

//	List<User> findByManageBy(User user);

//	 @Query("SELECT u FROM User u WHERE u.Userid = :UserId AND u.Username = :username")
//	   List<User> findByUseridAndUsername(Integer UserId, String username);

//	@Query("SELECT u FROM User u WHERE u.role = :role")
//	User findByURole(String userRoleName);

//	@Query("SELECT u FROM User u WHERE u.UserRole = :userRole")
//	User findByFirstRole(String role);

//	 @Query("SELECT SUM(pc.units) FROM ProductCategory pc")
//	 int calculateTotalUnits();
//
//	 @Query("SELECT u FROM User u WHERE u.Userid = :Userid")
//	Object findByUserId(Integer userRoleId);

//	String findUserRoleById(Long id);
	
	

}
