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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlElement;

/**
 * Model class for the <b>VehicleDealer table</b>.<br>
 *
 */
public class VehicleDealer extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private long dealerId = Long.MIN_VALUE;
	@XmlElement
	private String dealerType;
	private String sellerType;
	@XmlElement
	private String dealerName;
	@XmlElement
	private String dealerTelephone;
	@XmlElement
	private String dealerFax;
	@XmlElement
	private String dealerAddress1;
	@XmlElement
	private String dealerAddress2;
	@XmlElement
	private String dealerAddress3;
	private String dealerAddress4;
	@XmlElement
	private String dealerCountry;
	@XmlElement
	private String dealerCity;
	@XmlElement
	private String dealerProvince;
	private String lovDescCountry;
	private String lovDescCity;
	private String lovDescProvince;
	@XmlElement
	private String email;
	@XmlElement
	private String pOBox;
	private String emirates;
	private String commisionPaidAt;
	private String calculationRule;
	private String paymentMode;
	private String accountNumber;
	private Long accountingSetId;
	private String accountingSetCode;
	private String accountingSetDesc;
	private String calRuleDesc;
	private String emiratesDescription;
	private boolean newRecord;
	private String lovValue;
	private VehicleDealer befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private boolean active;
	@XmlElement
	private String code;
	@XmlElement
	private String zipCode;
	private String productCtg;
	private String productCtgDesc;
	private String shortCode;
	private String dealerShortCode;
	private String productShortCode;
	@XmlElement
	private String panNumber;
	@XmlElement
	private String uidNumber;
	@XmlElement
	private String taxNumber;
	@XmlElement
	private String fromprovince;
	@XmlElement
	private String toprovince;
	@XmlElement
	private String fromprovinceName;
	@XmlElement
	private String toprovinceName;
	@XmlElement
	private String accountNo;
	@XmlElement
	private String accountType;
	@XmlElement
	private Long bankBranchID;
	private String bankBranchCode;
	private String bankBranchCodeName;
	private String bankName;
	private String branchIFSCCode;
	private String branchMICRCode;
	private String branchCity;
	private String branchCode; // newly added field
	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;
	private Long pinCodeId;
	private String areaName;

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
		excludeFields.add("productCtgDesc");
		excludeFields.add("dealerShortCode");
		excludeFields.add("productShortCode");
		excludeFields.add("fromprovinceName");
		excludeFields.add("toprovinceName");
		excludeFields.add("bankBranchCode");
		excludeFields.add("bankBranchCodeName");
		excludeFields.add("bankName");
		excludeFields.add("branchIFSCCode");
		excludeFields.add("branchMICRCode");
		excludeFields.add("branchCity");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("areaName");
		return excludeFields;
	}

	// Getter and Setter methods

	public long getId() {
		return dealerId;
	}

	public void setId(long id) {
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

	public VehicleDealer getBefImage() {
		return this.befImage;
	}

	public void setBefImage(VehicleDealer beforeImage) {
		this.befImage = beforeImage;
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

	public Long getAccountingSetId() {
		return accountingSetId;
	}

	public void setAccountingSetId(Long accountingSetId) {
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

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> customerMap = new HashMap<>();

		return getDeclaredFieldValues(customerMap);
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> customerMap) {
		customerMap = new HashMap<>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				if (StringUtils.equals(this.getClass().getDeclaredFields()[i].getName(), "active")) {
					if (this.active) {
						customerMap.put("vd_" + this.getClass().getDeclaredFields()[i].getName(), "1");
					} else {
						customerMap.put("vd_" + this.getClass().getDeclaredFields()[i].getName(), "0");
					}
				} else {
					// "ct_" Should be in small case only, if we want to change
					// the case we need to update the configuration fields as
					// well.
					customerMap.put("vd_" + this.getClass().getDeclaredFields()[i].getName(),
							this.getClass().getDeclaredFields()[i].get(this));
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return customerMap;

	}

	public String getpOBox() {
		return pOBox;
	}

	public void setpOBox(String pOBox) {
		this.pOBox = pOBox;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getUidNumber() {
		return uidNumber;
	}

	public void setUidNumber(String uidNumber) {
		this.uidNumber = uidNumber;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getProductCtg() {
		return productCtg;
	}

	public void setProductCtg(String productCtg) {
		this.productCtg = productCtg;
	}

	public String getProductCtgDesc() {
		return productCtgDesc;
	}

	public void setProductCtgDesc(String productCtgDesc) {
		this.productCtgDesc = productCtgDesc;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getDealerShortCode() {
		// FIXME : All the dealer code settings in accounting move to single place
		return StringUtils.trimToEmpty(dealerShortCode);
	}

	public void setDealerShortCode(String dealerShortCode) {
		this.dealerShortCode = dealerShortCode;
	}

	public String getProductShortCode() {
		return productShortCode;
	}

	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}

	public String getFromprovince() {
		return fromprovince;
	}

	public void setFromprovince(String fromprovince) {
		this.fromprovince = fromprovince;
	}

	public String getToprovince() {
		return toprovince;
	}

	public void setToprovince(String toprovince) {
		this.toprovince = toprovince;
	}

	public String getFromprovinceName() {
		return fromprovinceName;
	}

	public void setFromprovinceName(String fromprovinceName) {
		this.fromprovinceName = fromprovinceName;
	}

	public String getToprovinceName() {
		return toprovinceName;
	}

	public void setToprovinceName(String toprovinceName) {
		this.toprovinceName = toprovinceName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(Long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public String getBankBranchCodeName() {
		return bankBranchCodeName;
	}

	public void setBankBranchCodeName(String bankBranchCodeName) {
		this.bankBranchCodeName = bankBranchCodeName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchIFSCCode() {
		return branchIFSCCode;
	}

	public void setBranchIFSCCode(String branchIFSCCode) {
		this.branchIFSCCode = branchIFSCCode;
	}

	public String getBranchMICRCode() {
		return branchMICRCode;
	}

	public void setBranchMICRCode(String branchMICRCode) {
		this.branchMICRCode = branchMICRCode;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(Long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}