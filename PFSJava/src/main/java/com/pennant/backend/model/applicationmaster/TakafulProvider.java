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
 * FileName    		:  TakafulProvider.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>TakafulProvider table</b>.<br>
 *
 */
public class TakafulProvider extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private String takafulCode;
	private String takafulName;
	private String takafulType;
	private String accountNumber;
	private BigDecimal 	takafulRate = BigDecimal.ZERO;
	private Date establishedDate;
	private String street;
	private String houseNumber;
	private String addrLine1;
	private String addrLine2;
	private String country;
	private String lovDescCountryDesc;
	private String province;
	private String lovDescProvinceDesc;
	private String city;
	private String lovDescCityDesc;
	private String zipCode;
	private String phone;
	private String fax;
	private String emailId;
	private String webSite;
	private String contactPerson;
	private String contactPersonNo;
	private Date expiryDate;
	private String providerType;

	private boolean newRecord;
	private String lovValue;
	private TakafulProvider befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public TakafulProvider() {
		super();
	}

	public TakafulProvider(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("lovDescCountryDesc");
			excludeFields.add("lovDescProvinceDesc");
			excludeFields.add("lovDescCityDesc");
	return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return takafulCode;
	}
	
	public void setId (String id) {
		this.takafulCode = id;
	}
	
	
	public String getTakafulCode() {
		return takafulCode;
	}
	public void setTakafulCode(String takafulCode) {
		this.takafulCode = takafulCode;
	}

	public String getTakafulName() {
		return takafulName;
	}
	public void setTakafulName(String takafulName) {
		this.takafulName = takafulName;
	}

	public String getTakafulType() {
		return takafulType;
	}
	public void setTakafulType(String takafulType) {
		this.takafulType = takafulType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getTakafulRate() {
		return takafulRate;
	}
	public void setTakafulRate(BigDecimal takafulRate) {
		this.takafulRate = takafulRate;
	}

	public Date getEstablishedDate() {
		return establishedDate;
	}
	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate;
	}

	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getAddrLine1() {
		return addrLine1;
	}
	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public String getAddrLine2() {
		return addrLine2;
	}
	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getLovDescCountryDesc() {
		return lovDescCountryDesc;
	}
	public void setLovDescCountryDesc(String lovDescCountryDesc) {
		this.lovDescCountryDesc = lovDescCountryDesc;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public String getLovDescProvinceDesc() {
		return lovDescProvinceDesc;
	}
	public void setLovDescProvinceDesc(String lovDescProvinceDesc) {
		this.lovDescProvinceDesc = lovDescProvinceDesc;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getLovDescCityDesc() {
		return lovDescCityDesc;
	}
	public void setLovDescCityDesc(String lovDescCityDesc) {
		this.lovDescCityDesc = lovDescCityDesc;
	}

	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPersonNo() {
		return contactPersonNo;
	}
	public void setContactPersonNo(String contactPersonNo) {
		this.contactPersonNo = contactPersonNo;
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

	public TakafulProvider getBefImage() {
		return befImage;
	}
	public void setBefImage(TakafulProvider befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getExpiryDate() {
	    return expiryDate;
    }

	public void setExpiryDate(Date expiryDate) {
	    this.expiryDate = expiryDate;
    }

	public String getProviderType() {
	    return providerType;
    }

	public void setProviderType(String providerType) {
	    this.providerType = providerType;
    }
}
