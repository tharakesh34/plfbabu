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
 * FileName    		:  BundledProductsDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>BundledProductsDetail table</b>.<br>
 *
 */
public class BundledProductsDetail extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String finReference;
	private String cardProduct;
	private String salesStaff;
	private String embossingName;
	private String statusOfCust;
	private BigDecimal minRepay = BigDecimal.ZERO;
	private String billingAcc;
	private String stmtAddress;
	private String stmtEmail;
	private String physicalAddress;
	private String contactNumber;
	private String ref1Name;
	private String ref1PhoneNum;
	private String ref1Email;
	private String ref2Name;
	private String ref2PhoneNum;
	private String ref2Email;
	private String bankName;
	private String chequeNo;
	private BigDecimal chequeAmt = BigDecimal.ZERO;
	private String cardType;
	private String classType;
	private BigDecimal limitRecommended = BigDecimal.ZERO;
	private BigDecimal limitApproved = BigDecimal.ZERO;
	private BigDecimal profitRate = BigDecimal.ZERO;
	private boolean crossSellCard;
	private boolean urgentIssuance;
	
	private boolean newRecord=false;
	private String lovValue;
	private BundledProductsDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public BundledProductsDetail() {
		super();
	}

	public BundledProductsDetail(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCardProduct() {
		return cardProduct;
	}
	public void setCardProduct(String cardProduct) {
		this.cardProduct = cardProduct;
	}

	public String getSalesStaff() {
		return salesStaff;
	}
	public void setSalesStaff(String salesStaff) {
		this.salesStaff = salesStaff;
	}

	public String getEmbossingName() {
		return embossingName;
	}
	public void setEmbossingName(String embossingName) {
		this.embossingName = embossingName;
	}

	public String getStatusOfCust() {
		return statusOfCust;
	}
	public void setStatusOfCust(String statusOfCust) {
		this.statusOfCust = statusOfCust;
	}

	public BigDecimal getMinRepay() {
		return minRepay;
	}
	public void setMinRepay(BigDecimal minRepay) {
		this.minRepay = minRepay;
	}

	public String getBillingAcc() {
		return billingAcc;
	}
	public void setBillingAcc(String billingAcc) {
		this.billingAcc = billingAcc;
	}

	public String getStmtAddress() {
		return stmtAddress;
	}
	public void setStmtAddress(String stmtAddress) {
		this.stmtAddress = stmtAddress;
	}

	public String getStmtEmail() {
		return stmtEmail;
	}
	public void setStmtEmail(String stmtEmail) {
		this.stmtEmail = stmtEmail;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}
	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getRef1Name() {
		return ref1Name;
	}
	public void setRef1Name(String ref1Name) {
		this.ref1Name = ref1Name;
	}

	public String getRef1PhoneNum() {
		return ref1PhoneNum;
	}
	public void setRef1PhoneNum(String ref1PhoneNum) {
		this.ref1PhoneNum = ref1PhoneNum;
	}

	public String getRef1Email() {
		return ref1Email;
	}
	public void setRef1Email(String ref1Email) {
		this.ref1Email = ref1Email;
	}

	public String getRef2Name() {
		return ref2Name;
	}
	public void setRef2Name(String ref2Name) {
		this.ref2Name = ref2Name;
	}

	public String getRef2PhoneNum() {
		return ref2PhoneNum;
	}
	public void setRef2PhoneNum(String ref2PhoneNum) {
		this.ref2PhoneNum = ref2PhoneNum;
	}

	public String getRef2Email() {
		return ref2Email;
	}
	public void setRef2Email(String ref2Email) {
		this.ref2Email = ref2Email;
	}

	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public BigDecimal getChequeAmt() {
		return chequeAmt;
	}
	public void setChequeAmt(BigDecimal chequeAmt) {
		this.chequeAmt = chequeAmt;
	}

	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}

	public BigDecimal getLimitRecommended() {
		return limitRecommended;
	}
	public void setLimitRecommended(BigDecimal limitRecommended) {
		this.limitRecommended = limitRecommended;
	}

	public BigDecimal getLimitApproved() {
		return limitApproved;
	}
	public void setLimitApproved(BigDecimal limitApproved) {
		this.limitApproved = limitApproved;
	}

	public BigDecimal getProfitRate() {
		return profitRate;
	}
	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	public boolean isCrossSellCard() {
		return crossSellCard;
	}
	public void setCrossSellCard(boolean crossSellCard) {
		this.crossSellCard = crossSellCard;
	}

	public boolean isUrgentIssuance() {
		return urgentIssuance;
	}
	public void setUrgentIssuance(boolean urgentIssuance) {
		this.urgentIssuance = urgentIssuance;
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

	public BundledProductsDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(BundledProductsDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
