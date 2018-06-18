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
 * FileName    		:  VehicleDealer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.amtmasters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VehicleDealer table</b>.<br>
 *
 */
public class VehicleDealer extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long dealerId = Long.MIN_VALUE;
	private String dealerType;
	private String sellerType;
	private String dealerName;
	private String dealerTelephone;
	private String dealerFax;
	private String dealerAddress1;
	private String dealerAddress2;
	private String dealerAddress3;
	private String dealerAddress4;
	private String dealerCountry;
	private String dealerCity;
	private String dealerProvince;
	private String lovDescCountry;
	private String lovDescCity;
	private String lovDescProvince;
	private String email;
	private String pOBox;
	private String emirates;
	private String commisionPaidAt;
	private String calculationRule;
	private String paymentMode;
	private String accountNumber;
	private long accountingSetId;
	private String accountingSetCode;
	private String  accountingSetDesc;
	private String  calRuleDesc;
	private String 	emiratesDescription;
    private boolean newRecord;
	private String lovValue;
	private VehicleDealer befImage;
	private LoggedInUser userDetails;
	private boolean active;
	private String  zipCode;
	private String code;

	public VehicleDealer() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public VehicleDealer(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("calculationRule");
		excludeFields.add("accountingSetCode");
		excludeFields.add("accountingSetDesc");
		excludeFields.add("calRuleDesc");
		excludeFields.add("emiratesDescription");
		return excludeFields;
	}

	//Getter and Setter methods
	
	public long getId() {
		return dealerId;
	}
	
	public void setId (long id) {
		this.dealerId = id;
	}
	
	public long getDealerId() {
		return dealerId;
	}
	public void setDealerId(long dealerId) {
		this.dealerId = dealerId;
	}
	
	public String getDealerType() {
    	return dealerType;
    }

	public void setDealerType(String dealerType) {
    	this.dealerType = dealerType;
    }

	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	public String getDealerTelephone() {
    	return dealerTelephone;
    }

	public void setDealerTelephone(String dealerTelephone) {
    	this.dealerTelephone = dealerTelephone;
    }

	public String getDealerFax() {
    	return dealerFax;
    }

	public void setDealerFax(String dealerFax) {
    	this.dealerFax = dealerFax;
    }

	public String getDealerAddress1() {
    	return dealerAddress1;
    }

	public void setDealerAddress1(String dealerAddress1) {
    	this.dealerAddress1 = dealerAddress1;
    }

	public String getDealerAddress2() {
    	return dealerAddress2;
    }

	public void setDealerAddress2(String dealerAddress2) {
    	this.dealerAddress2 = dealerAddress2;
    }
	
	public String getDealerAddress3() {
    	return dealerAddress3;
    }

	public void setDealerAddress3(String dealerAddress3) {
    	this.dealerAddress3 = dealerAddress3;
    }

	public String getDealerAddress4() {
    	return dealerAddress4;
    }

	public void setDealerAddress4(String dealerAddress4) {
    	this.dealerAddress4 = dealerAddress4;
    }

	public String getDealerCountry() {
    	return dealerCountry;
    }

	public void setDealerCountry(String dealerCountry) {
    	this.dealerCountry = dealerCountry;
    }

	public String getDealerCity() {
    	return dealerCity;
    }

	public void setDealerCity(String dealerCity) {
    	this.dealerCity = dealerCity;
    }

	public String getDealerProvince() {
    	return dealerProvince;
    }

	public void setDealerProvince(String dealerProvince) {
    	this.dealerProvince = dealerProvince;
    }

	public String getLovDescCountry() {
    	return lovDescCountry;
    }

	public void setLovDescCountry(String lovDescCountry) {
    	this.lovDescCountry = lovDescCountry;
    }

	public String getLovDescCity() {
    	return lovDescCity;
    }

	public void setLovDescCity(String lovDescCity) {
    	this.lovDescCity = lovDescCity;
    }

	public String getLovDescProvince() {
    	return lovDescProvince;
    }

	public void setLovDescProvince(String lovDescProvince) {
    	this.lovDescProvince = lovDescProvince;
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

	public VehicleDealer getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VehicleDealer beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getEmirates() {
		return emirates;
	}

	public void setEmirates(String emirates) {
		this.emirates = emirates;
	}

	public String getCommisionPaidAt() {
		return commisionPaidAt;
	}

	public void setCommisionPaidAt(String commisionPaidAt) {
		this.commisionPaidAt = commisionPaidAt;
	}

	public String getCalculationRule() {
		return calculationRule;
	}

	public void setCalculationRule(String calculationRule) {
		this.calculationRule = calculationRule;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public String getAccountingSetCode() {
		return accountingSetCode;
	}

	public void setAccountingSetCode(String accountingSetCode) {
		this.accountingSetCode = accountingSetCode;
	}

	public String getAccountingSetDesc() {
		return accountingSetDesc;
	}

	public void setAccountingSetDesc(String accountingSetDesc) {
		this.accountingSetDesc = accountingSetDesc;
	}

	public String getCalRuleDesc() {
		return calRuleDesc;
	}

	public void setCalRuleDesc(String calRuleDesc) {
		this.calRuleDesc = calRuleDesc;
	}

	public long getAccountingSetId() {
	    return accountingSetId;
    }

	public void setAccountingSetId(long accountingSetId) {
	    this.accountingSetId = accountingSetId;
    }

	public String getPOBox() {
	    return pOBox;
    }

	public void setPOBox(String pOBox) {
	    this.pOBox = pOBox;
    }

	public String getEmiratesDescription() {
	    return emiratesDescription;
    }

	public void setEmiratesDescription(String emiratesDescription) {
	    this.emiratesDescription = emiratesDescription;
    }

	public String getSellerType() {
	    return sellerType;
    }

	public void setSellerType(String sellerType) {
	    this.sellerType = sellerType;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerMap = new HashMap<String, Object>();
		
		return getDeclaredFieldValues(customerMap);
	}

	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> customerMap) {
		customerMap = new HashMap<String, Object>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				//"ct_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				customerMap.put("vd_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return customerMap;
	}	
	
}
