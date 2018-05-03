/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  Customer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.gcd;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class for the <b>Customer table</b>.<br>
 */
public class GcdCustomer {
	private static final long serialVersionUID = 216846029043076055L;
	private long custId;//Customer id
	private String finCustId; // FinnOne CustomerID in case of updating the customer details
	private String sourceSystem = "PLF"; // HARD CODE
	private String customerName; // Full Name of Customer (Individual)/ Name of Corporate Customer
	private long constId; // Customer Type ID of PLF (Customer Type ID in PLF and Constitution ID in FinnOne are same)
	private long industryId; // IndustryID in PLF (IndustryID in PLF and IndustryID in FinnOne are same)
	private Long categoryId = null; // Shall be Blank
	private String spousename; // Shall be Blank
	private String indvCorpFlag; // Indicator for Corporate and Individual Customer ("I" for Individual and "C" for
									// Corporate customer)
	private String fName; // First Name of Individual Customer
	private String mName; // Middle Name of Individual Customer
	private String lname; // Last Name of Individual Customer
	private Date DOB; // Date of Birth of individual Customer
	private String sex; // Gender of
	private String incomeSource; // Shall be blank
	private Date yearsOfCurrJob; // Applicable for Individual Customer - Customer Employment Details - Employee From
									// (Current Employer)
	private Date DOI; // Applicable for Corporate Customer - Date of Incorporation
	private String mpAkerId = "PENNANT"; // Shall be hard coded value "PENNANT"
	private Date makerDate; // Shall be the current business date
	private String authId = "PENNANT"; // Shall be hard coded value "PENNANT"
	private Timestamp authDate; // Shall be the current business date
	private String accType; // Shall be blank
	private String apCcocatg; // Shall be blank
	private Date dateLastUpdate; // Shall be blank
	private String nationalId = "NA"; // Shall be populated with hard coded value as "NA"
	private String passportNo; // Shall be Passport No. of Customer
	private String nationality = "INDIA"; // Shall be populated with hard coded value as "INDIA"
	private String panNo; // PAN No. of Customer
	private int regionId = 1; // Shall be populated with Region of Customer's Branch if not available then it shall be
								// populated with value as "1"If the Region is North then the value shall be 1If the
								// Region is South then the value shall be 2If the Region is East then the value shall
								// be 3If the Region is West then the value shall be 4
	private String bankType = "R"; // Shall be populated with hard coded value as "R"
	private String entityFlag ; // Shall be Blank
	private String contactPerson = "NOT AVAILABLE"; // Shall be populated with Contact Person Name of corporate customer
													// or "NOT AVAILABLE"
	private String custSearchId; // Shall be populated with PAN No.
	private long sectorId; // Shall be populated with the Sector ID (Sector ID in PLF and Economic Section ID in FinnOne
							// are same)
	private String fraudFlag ; // Shall be Blank
	private long fraudScore; // Shall be Blank
	private String emiCardElig ; // Shall be Blank
	private String addressDetail; // Address details to be populated address details by seeing structure
	private String bankDetail; // Mandate details to be populated structure given below
	private String nomineeName; // Name of Nominee if available
	private String nomineeAddress; // Address of Nominee if available
	private String nomineeRelationship ; // Nominee Relationship
	private String field9 ; // Tenure of Insurance Policy
	private String field10 ; // Sum assured of Insurance Policy
	private String insertUpdateFlag; // "I" for creating a new customer & "U" for updating the details of the existing
										// customer
	private String statusFromFinnOne ; // Value to be returned by FinnOne. Possible values are "S"uccess or "R"eject
	private String rejectionReason; // Rejection reason shall be populated if P_Success_Reject is "R"
	private String finnCustId; // Shall be populated with FinnOne Customer ID where P_INS_UPD_FLAG is "U". For
								// P_INS_UPD_FLAG "I" value in this field shall be blank. FinnOne shall return the
								// FinnOne Customer ID after successful creation or updation of customer
	private long sfdcCustomerId; // Shall be populated with PLF CustomerID
	private long branchId; // Shall be populated with FinnOne BranchID. FinnOne BranchID shall be fetched on the basis
							// of Customer's PLF Branch Code
	private int requestSeq=0;
	public GcdCustomer(){
		super();
	}
	
	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinCustId() {
		return finCustId;
	}

	public void setFinCustId(String finCustId) {
		this.finCustId = finCustId;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getConstId() {
		return constId;
	}

	public void setConstId(long constId) {
		this.constId = constId;
	}

	public long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(long industryId) {
		this.industryId = industryId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getSpousename() {
		return spousename;
	}

	public void setSpousename(String spousename) {
		this.spousename = spousename;
	}

	public String getIndvCorpFlag() {
		return indvCorpFlag;
	}

	public void setIndvCorpFlag(String indvCorpFlag) {
		this.indvCorpFlag = indvCorpFlag;
	}

	public void setInsertUpdateFlag(String insertUpdateFlag) {
		this.insertUpdateFlag = insertUpdateFlag;
	}

	public String getInsertUpdateFlag() {
		return insertUpdateFlag;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public Date getDOB() {
		return DOB;
	}

	public void setDOB(Date dOB) {
		DOB = dOB;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIncomeSource() {
		return incomeSource;
	}

	public void setIncomeSource(String incomeSource) {
		this.incomeSource = incomeSource;
	}

	public Date getYearsOfCurrJob() {
		return yearsOfCurrJob;
	}

	public void setYearsOfCurrJob(Date yearsOfCurrJob) {
		this.yearsOfCurrJob = yearsOfCurrJob;
	}

	public Date getDOI() {
		return DOI;
	}

	public void setDOI(Date dOI) {
		DOI = dOI;
	}

	public String getMpAkerId() {
		return mpAkerId;
	}

	public void setMpAkerId(String mpAkerId) {
		this.mpAkerId = mpAkerId;
	}

	public Date getMakerDate() {
		return makerDate;
	}

	public void setMakerDate(Date makerDate) {
		this.makerDate = makerDate;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public Timestamp getAuthDate() {
		return authDate;
	}

	public void setAuthDate(Timestamp authDate) {
		this.authDate = authDate;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getApCcocatg() {
		return apCcocatg;
	}

	public void setApCcocatg(String apCcocatg) {
		this.apCcocatg = apCcocatg;
	}

	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getEntityFlag() {
		return entityFlag;
	}

	public void setEntityFlag(String entityFlag) {
		this.entityFlag = entityFlag;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getCustSearchId() {
		return custSearchId;
	}

	public void setCustSearchId(String custSearchId) {
		this.custSearchId = custSearchId;
	}

	public long getSectorId() {
		return sectorId;
	}

	public void setSectorId(long sectorId) {
		this.sectorId = sectorId;
	}

	public String getFraudFlag() {
		return fraudFlag;
	}

	public void setFraudFlag(String fraudFlag) {
		this.fraudFlag = fraudFlag;
	}

	public long getFraudScore() {
		return fraudScore;
	}

	public void setFraudScore(long fraudScore) {
		this.fraudScore = fraudScore;
	}

	public String getEmiCardElig() {
		return emiCardElig;
	}

	public void setEmiCardElig(String emiCardElig) {
		this.emiCardElig = emiCardElig;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public String getBankDetail() {
		return bankDetail;
	}

	public void setBankDetail(String bankDetail) {
		this.bankDetail = bankDetail;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeAddress() {
		return nomineeAddress;
	}

	public void setNomineeAddress(String nomineeAddress) {
		this.nomineeAddress = nomineeAddress;
	}

	public String getNomineeRelationship() {
		return nomineeRelationship;
	}

	public void setNomineeRelationship(String nomineeRelationship) {
		this.nomineeRelationship = nomineeRelationship;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
	}

	public String getStatusFromFinnOne() {
		return statusFromFinnOne;
	}

	public void setStatusFromFinnOne(String statusFromFinnOne) {
		this.statusFromFinnOne = statusFromFinnOne;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getFinnCustId() {
		return finnCustId;
	}

	public void setFinnCustId(String finnCustId) {
		this.finnCustId = finnCustId;
	}

	public long getSfdcCustomerId() {
		return sfdcCustomerId;
	}

	public void setSfdcCustomerId(long sfdcCustomerId) {
		this.sfdcCustomerId = sfdcCustomerId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getRequestSeq() {
		return requestSeq;
	}

	public void setRequestSeq(int requestSeq) {
		this.requestSeq = requestSeq;
	}
	
}
