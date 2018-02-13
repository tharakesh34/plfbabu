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
 * FileName    		:  EmployerDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>EmployerDetail table</b>.<br>
 *
 */
public class EmployerDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long employerId = Long.MIN_VALUE;
	private String empIndustry;
	private String lovDescIndustryDesc;
	private String empName;
	private Date establishDate;
	private String empAddrHNbr;
	private String empFlatNbr;
	private String empAddrStreet;
	private String empAddrLine1;
	private String empAddrLine2;
	private String empPOBox;
	private String empCountry;
	private String lovDescCountryDesc;
	private String empProvince;
	private String lovDescProvinceName;
	private String empCity;
	private String lovDescCityName;
	private String empPhone;
	private String empFax;
	private String empTelexNo;
	private String empEmailId;
	private String empWebSite;
	private String contactPersonName;
	private String contactPersonNo;
	private String empAlocationType;
	private String empAlocationTypeName;
	private String bankRefNo;
	private boolean empIsActive;
	
	private boolean newRecord;
	private String lovValue;
	private EmployerDetail befImage;
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public EmployerDetail() {
		super();
	}

	public EmployerDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("empIndustryName");
			excludeFields.add("empCountryName");
			excludeFields.add("empProvinceName");
			excludeFields.add("empCityName");
			excludeFields.add("empAlocationTypeName");
	return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return employerId;
	}
	
	public void setId (long id) {
		this.employerId = id;
	}
	
	public long getEmployerId() {
		return employerId;
	}
	public void setEmployerId(long employerId) {
		this.employerId = employerId;
	}
	
	
		
	
	public String getEmpIndustry() {
		return empIndustry;
	}
	public void setEmpIndustry(String empIndustry) {
		this.empIndustry = empIndustry;
	}
	
	
	
		
	
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
	
		
	
	public Date getEstablishDate() {
		return establishDate;
	}
	public void setEstablishDate(Date establishDate) {
		this.establishDate = establishDate;
	}
	
	
		
	
	public String getEmpAddrHNbr() {
		return empAddrHNbr;
	}
	public void setEmpAddrHNbr(String empAddrHNbr) {
		this.empAddrHNbr = empAddrHNbr;
	}
	
	
		
	
	public String getEmpFlatNbr() {
		return empFlatNbr;
	}
	public void setEmpFlatNbr(String empFlatNbr) {
		this.empFlatNbr = empFlatNbr;
	}
	
	
		
	
	public String getEmpAddrStreet() {
		return empAddrStreet;
	}
	public void setEmpAddrStreet(String empAddrStreet) {
		this.empAddrStreet = empAddrStreet;
	}
	
	
		
	
	public String getEmpAddrLine1() {
		return empAddrLine1;
	}
	public void setEmpAddrLine1(String empAddrLine1) {
		this.empAddrLine1 = empAddrLine1;
	}
	
	
		
	
	public String getEmpAddrLine2() {
		return empAddrLine2;
	}
	public void setEmpAddrLine2(String empAddrLine2) {
		this.empAddrLine2 = empAddrLine2;
	}
	
	
		
	
	public String getEmpPOBox() {
		return empPOBox;
	}
	public void setEmpPOBox(String empPOBox) {
		this.empPOBox = empPOBox;
	}
	
	
		
	
	public String getEmpCountry() {
		return empCountry;
	}
	public void setEmpCountry(String empCountry) {
		this.empCountry = empCountry;
	}
	
	
	
		
	
	public String getEmpProvince() {
		return empProvince;
	}
	public void setEmpProvince(String empProvince) {
		this.empProvince = empProvince;
	}
	
	
	
		
	
	public String getEmpCity() {
		return empCity;
	}
	public void setEmpCity(String empCity) {
		this.empCity = empCity;
	}
	
	
	
		
	
	public String getLovDescIndustryDesc() {
    	return lovDescIndustryDesc;
    }

	public void setLovDescIndustryDesc(String lovDescIndustryDesc) {
    	this.lovDescIndustryDesc = lovDescIndustryDesc;
    }

	public String getLovDescCountryDesc() {
    	return lovDescCountryDesc;
    }

	public void setLovDescCountryDesc(String lovDescCountryDesc) {
    	this.lovDescCountryDesc = lovDescCountryDesc;
    }

	public String getLovDescProvinceName() {
    	return lovDescProvinceName;
    }

	public void setLovDescProvinceName(String lovDescProvinceName) {
    	this.lovDescProvinceName = lovDescProvinceName;
    }

	public String getLovDescCityName() {
    	return lovDescCityName;
    }

	public void setLovDescCityName(String lovDescCityName) {
    	this.lovDescCityName = lovDescCityName;
    }

	public String getEmpPhone() {
		return empPhone;
	}
	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}
	
	
		
	
	public String getEmpFax() {
		return empFax;
	}
	public void setEmpFax(String empFax) {
		this.empFax = empFax;
	}
	
	
		
	
	public String getEmpTelexNo() {
		return empTelexNo;
	}
	public void setEmpTelexNo(String empTelexNo) {
		this.empTelexNo = empTelexNo;
	}
	
	
		
	
	public String getEmpEmailId() {
		return empEmailId;
	}
	public void setEmpEmailId(String empEmailId) {
		this.empEmailId = empEmailId;
	}
	
	
		
	
	public String getEmpWebSite() {
		return empWebSite;
	}
	public void setEmpWebSite(String empWebSite) {
		this.empWebSite = empWebSite;
	}
	
	
		
	
	public String getContactPersonName() {
		return contactPersonName;
	}
	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}
	
	
		
	
	public String getContactPersonNo() {
		return contactPersonNo;
	}
	public void setContactPersonNo(String contactPersonNo) {
		this.contactPersonNo = contactPersonNo;
	}
	
	
		
	
	public String getEmpAlocationType() {
		return empAlocationType;
	}
	public void setEmpAlocationType(String empAlocationType) {
		this.empAlocationType = empAlocationType;
	}
	
	public String getEmpAlocationTypeName() {
		return this.empAlocationTypeName;
	}

	public void setEmpAlocationTypeName (String empAlocationTypeName) {
		this.empAlocationTypeName = empAlocationTypeName;
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

	public EmployerDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(EmployerDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getBankRefNo() {
		return bankRefNo;
	}

	public void setBankRefNo(String bankRefNo) {
		this.bankRefNo = bankRefNo;
	}
	
	public boolean isEmpIsActive() {
		return empIsActive;
	}

	public void setEmpIsActive(boolean empIsActive) {
		this.empIsActive = empIsActive;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
