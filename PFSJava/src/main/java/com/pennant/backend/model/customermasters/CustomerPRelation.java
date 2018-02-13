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
 * FileName    		:  CustomerPRelation.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.customermasters;

import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerPRelation table</b>.<br>
 *
 */
public class CustomerPRelation extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -4817133724784086357L;

	private long pRCustID =Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private int pRCustPRSNo;
	private String pRRelationCode;
	private String lovDescPRRelationCodeName;//new
	private String pRRelationCustID;
	private boolean pRisGuardian;
	private String pRFName;
	private String pRMName;
	private String pRLName;
	private String pRSName;
	private String pRFNameLclLng;
	private String pRMNameLclLng;
	private String pRLNameLclLng;
	private Date pRDOB;
	private String pRAddrHNbr;
	private String pRAddrFNbr;
	private String pRAddrStreet;
	private String pRAddrLine1;
	private String pRAddrLine2;
	private String pRAddrPOBox;
	private String pRAddrCity;
	private String lovDescPRAddrCityName;
	private String pRAddrProvince;
	private String lovDescPRAddrProvinceName;
	private String pRAddrCountry;
	private String lovDescPRAddrCountryName;
	private String pRAddrZIP;
	private String pRPhone;
	private String pRMail;
	private boolean newRecord;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private CustomerPRelation befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerPRelation() {
		super();
	}

	public CustomerPRelation(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return pRCustID;
	}
	public void setId (long id) {
		this.pRCustID = id;
	}

	public long getPRCustID() {
		return pRCustID;
	}
	public void setPRCustID(long pRCustID) {
		this.pRCustID = pRCustID;
	}

	public int getPRCustPRSNo() {
		return pRCustPRSNo;
	}
	public void setPRCustPRSNo(int pRCustPRSNo) {
		this.pRCustPRSNo = pRCustPRSNo;
	}

	public String getPRRelationCode() {
		return pRRelationCode;
	}
	public void setPRRelationCode(String pRRelationCode) {
		this.pRRelationCode = pRRelationCode;
	}

	public String getLovDescPRRelationCodeName() {
		return this.lovDescPRRelationCodeName;
	}
	public void setLovDescPRRelationCodeName(String lovDescPRRelationCodeName) {
		this.lovDescPRRelationCodeName = lovDescPRRelationCodeName;
	}

	public String getPRRelationCustID() {
		return pRRelationCustID;
	}
	public void setPRRelationCustID(String pRRelationCustID) {
		this.pRRelationCustID = pRRelationCustID;
	}

	public boolean isPRisGuardian() {
		return pRisGuardian;
	}
	public void setPRisGuardian(boolean pRisGuardian) {
		this.pRisGuardian = pRisGuardian;
	}

	public String getPRFName() {
		return pRFName;
	}
	public void setPRFName(String pRFName) {
		this.pRFName = pRFName;
	}

	public String getPRMName() {
		return pRMName;
	}
	public void setPRMName(String pRMName) {
		this.pRMName = pRMName;
	}

	public String getPRLName() {
		return pRLName;
	}
	public void setPRLName(String pRLName) {
		this.pRLName = pRLName;
	}

	public String getPRSName() {
		return pRSName;
	}
	public void setPRSName(String pRSName) {
		this.pRSName = pRSName;
	}

	public String getPRFNameLclLng() {
		return pRFNameLclLng;
	}
	public void setPRFNameLclLng(String pRFNameLclLng) {
		this.pRFNameLclLng = pRFNameLclLng;
	}

	public String getPRMNameLclLng() {
		return pRMNameLclLng;
	}
	public void setPRMNameLclLng(String pRMNameLclLng) {
		this.pRMNameLclLng = pRMNameLclLng;
	}

	public String getPRLNameLclLng() {
		return pRLNameLclLng;
	}
	public void setPRLNameLclLng(String pRLNameLclLng) {
		this.pRLNameLclLng = pRLNameLclLng;
	}

	public Date getPRDOB() {
		return pRDOB;
	}
	public void setPRDOB(Date pRDOB) {
		this.pRDOB = pRDOB;
	}

	public String getPRAddrHNbr() {
		return pRAddrHNbr;
	}
	public void setPRAddrHNbr(String pRAddrHNbr) {
		this.pRAddrHNbr = pRAddrHNbr;
	}

	public String getPRAddrFNbr() {
		return pRAddrFNbr;
	}
	public void setPRAddrFNbr(String pRAddrFNbr) {
		this.pRAddrFNbr = pRAddrFNbr;
	}

	public String getPRAddrStreet() {
		return pRAddrStreet;
	}
	public void setPRAddrStreet(String pRAddrStreet) {
		this.pRAddrStreet = pRAddrStreet;
	}

	public String getPRAddrLine1() {
		return pRAddrLine1;
	}
	public void setPRAddrLine1(String pRAddrLine1) {
		this.pRAddrLine1 = pRAddrLine1;
	}

	public String getPRAddrLine2() {
		return pRAddrLine2;
	}
	public void setPRAddrLine2(String pRAddrLine2) {
		this.pRAddrLine2 = pRAddrLine2;
	}

	public String getPRAddrPOBox() {
		return pRAddrPOBox;
	}
	public void setPRAddrPOBox(String pRAddrPOBox) {
		this.pRAddrPOBox = pRAddrPOBox;
	}

	public String getPRAddrCity() {
		return pRAddrCity;
	}
	public void setPRAddrCity(String pRAddrCity) {
		this.pRAddrCity = pRAddrCity;
	}

	public String getLovDescPRAddrCityName() {
		return this.lovDescPRAddrCityName;
	}
	public void setLovDescPRAddrCityName(String lovDescPRAddrCityName) {
		this.lovDescPRAddrCityName = lovDescPRAddrCityName;
	}

	public String getPRAddrProvince() {
		return pRAddrProvince;
	}
	public void setPRAddrProvince(String pRAddrProvince) {
		this.pRAddrProvince = pRAddrProvince;
	}

	public String getLovDescPRAddrProvinceName() {
		return this.lovDescPRAddrProvinceName;
	}
	public void setLovDescPRAddrProvinceName(String lovDescPRAddrProvinceName) {
		this.lovDescPRAddrProvinceName = lovDescPRAddrProvinceName;
	}

	public String getPRAddrCountry() {
		return pRAddrCountry;
	}
	public void setPRAddrCountry(String pRAddrCountry) {
		this.pRAddrCountry = pRAddrCountry;
	}

	public String getLovDescPRAddrCountryName() {
		return this.lovDescPRAddrCountryName;
	}
	public void setLovDescPRAddrCountryName(String lovDescPRAddrCountryName) {
		this.lovDescPRAddrCountryName = lovDescPRAddrCountryName;
	}

	public String getPRAddrZIP() {
		return pRAddrZIP;
	}
	public void setPRAddrZIP(String pRAddrZIP) {
		this.pRAddrZIP = pRAddrZIP;
	}

	public String getPRPhone() {
		return pRPhone;
	}
	public void setPRPhone(String pRPhone) {
		this.pRPhone = pRPhone;
	}

	public String getPRMail() {
		return pRMail;
	}
	public void setPRMail(String pRMail) {
		this.pRMail = pRMail;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public CustomerPRelation getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerPRelation beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;

	}

}
