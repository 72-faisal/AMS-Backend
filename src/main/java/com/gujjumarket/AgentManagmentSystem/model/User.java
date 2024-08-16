
package com.gujjumarket.AgentManagmentSystem.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int Userid;
	private String Username, Useremail, Userpassword, IFSCcode, role, PAN, AADHAR, Bankname, branchname, Accounttype,
			Accountholdername, Userprofile, Useraddress,State,cityorvillage,District,accountNo;
	private boolean isFirstTimeLogin, IsSubUser;
	private boolean IsKYCDone, UsDisabled;
	@Column(unique = true)
	private Long Usermobile;
	private int ManageBy;
	private double TotalCommissionAmount;
	// private Date dob, createddate, updateddate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dob;
//	@Column(name = "is_agent")
	private boolean isAgent,isPaid;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createddate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date updateddate;
	private String duration;
	private Integer targetAmount, targetAchivedAmount=0;
	
	private String gender;

	private boolean IsAmountachived=false;
	// private LocalDate dob;
	
	private String KYCstatus;

	// --f
	@ManyToOne
	@JoinColumn(name = "createdby")
	@JsonIgnore
	private Admin createdby;

	@ManyToOne
	@JoinColumn(name = "updatedby")
	@JsonIgnore
	private Admin updatedby;
	
	private Boolean active = true;

	@OneToMany(mappedBy = "soldby")
	private List<Sell> sell;

	private double monthlyTarget;
	private double quarterlyTarget;
	private double halfYearlyTarget;
	private double yearlyTarget;
	private double remainigPercentage=100;
	private double assignpercentage;

	@OneToMany
	private List<Product> products;
	
	@Transient
	private String manageByUsername;

	@Transient
	private long manageByUsermobile;
	
//	private double comission_detail_sell;
	
	public static final String ROLE_STATE_HEAD = "STATEHEAD";
	public static final String ROLE_DISTRICT_HEAD = "DISTRICTHEAD";
	public static final String ROLE_CITY_HEAD = "CITYHEAD";
	public static final String ROLE_AGENT = "AGENT";
	public static final String ROLE_SUB_AGENT = "SUBAGENT";


}