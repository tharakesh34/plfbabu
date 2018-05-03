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
 * FileName    		:  CustomerBankInfo.java                                                * 	  
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerBankInfo table</b>.<br>
 * 
 */
@XmlType(propOrder = { "bankId","bankName", "accountNumber", "accountType","salaryAccount" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerBankInfo extends AbstractWorkflowEntity implements Entity  {
	private static final long serialVersionUID = -3217987429162088120L;

	private long custID = Long.MIN_VALUE;
	
	@XmlElement
	private long bankId = Long.MIN_VALUE;

	private String bankCode;
	@XmlElement
	private String bankName;
	private String lovDescBankName;
	@XmlElement
	private String accountNumber;
	@XmlElement
	private String accountType;
	private String lovDescAccountType;
	private boolean newRecord = false;
	private String lovValue;
	private CustomerBankInfo befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	@XmlElement
	private boolean salaryAccount;
	private String sourceId;
	
	//As per Profectus documnet below fields added
	private int creditTranNo = 0;
	private BigDecimal creditTranAmt = BigDecimal.ZERO;
	private BigDecimal creditTranAvg = BigDecimal.ZERO;
	private int debitTranNo = 0;
	private BigDecimal debitTranAmt = BigDecimal.ZERO;
	private int cashDepositNo = 0;
	private BigDecimal cashDepositAmt = BigDecimal.ZERO;
	private int cashWithdrawalNo = 0;
	private BigDecimal cashWithdrawalAmt = BigDecimal.ZERO;
	private int chqDepositNo = 0;
	private BigDecimal chqDepositAmt = BigDecimal.ZERO;
	private int chqIssueNo = 0;
	private BigDecimal chqIssueAmt = BigDecimal.ZERO;
	private int inwardChqBounceNo = 0;
	private int outwardChqBounceNo = 0;
	private BigDecimal eodBalMin = BigDecimal.ZERO;
	private BigDecimal eodBalMax = BigDecimal.ZERO;
	private BigDecimal eodBalAvg = BigDecimal.ZERO;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerBankInfo() {
		super();
	}

	public CustomerBankInfo(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("bankCode");
		excludeFields.add("sourceId");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getLovDescBankName() {
		return lovDescBankName;
	}

	public void setLovDescBankName(String lovDescBankName) {
		this.lovDescBankName = lovDescBankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getLovDescAccountType() {
		return lovDescAccountType;
	}

	public void setLovDescAccountType(String lovDescAccountType) {
		this.lovDescAccountType = lovDescAccountType;
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

	public CustomerBankInfo getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CustomerBankInfo beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public boolean isSalaryAccount() {
		return salaryAccount;
	}

	public void setSalaryAccount(boolean salaryAccount) {
		this.salaryAccount = salaryAccount;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public int getCreditTranNo() {
		return creditTranNo;
	}

	public void setCreditTranNo(int creditTranNo) {
		this.creditTranNo = creditTranNo;
	}

	public BigDecimal getCreditTranAmt() {
		return creditTranAmt;
	}

	public void setCreditTranAmt(BigDecimal creditTranAmt) {
		this.creditTranAmt = creditTranAmt;
	}

	public BigDecimal getCreditTranAvg() {
		return creditTranAvg;
	}

	public void setCreditTranAvg(BigDecimal creditTranAvg) {
		this.creditTranAvg = creditTranAvg;
	}

	public int getDebitTranNo() {
		return debitTranNo;
	}

	public void setDebitTranNo(int debitTranNo) {
		this.debitTranNo = debitTranNo;
	}

	public BigDecimal getDebitTranAmt() {
		return debitTranAmt;
	}

	public void setDebitTranAmt(BigDecimal debitTranAmt) {
		this.debitTranAmt = debitTranAmt;
	}

	public int getCashDepositNo() {
		return cashDepositNo;
	}

	public void setCashDepositNo(int cashDepositNo) {
		this.cashDepositNo = cashDepositNo;
	}

	public BigDecimal getCashDepositAmt() {
		return cashDepositAmt;
	}

	public void setCashDepositAmt(BigDecimal cashDepositAmt) {
		this.cashDepositAmt = cashDepositAmt;
	}

	public int getCashWithdrawalNo() {
		return cashWithdrawalNo;
	}

	public void setCashWithdrawalNo(int cashWithdrawalNo) {
		this.cashWithdrawalNo = cashWithdrawalNo;
	}

	public BigDecimal getCashWithdrawalAmt() {
		return cashWithdrawalAmt;
	}

	public void setCashWithdrawalAmt(BigDecimal cashWithdrawalAmt) {
		this.cashWithdrawalAmt = cashWithdrawalAmt;
	}

	public int getChqDepositNo() {
		return chqDepositNo;
	}

	public void setChqDepositNo(int chqDepositNo) {
		this.chqDepositNo = chqDepositNo;
	}

	public BigDecimal getChqDepositAmt() {
		return chqDepositAmt;
	}

	public void setChqDepositAmt(BigDecimal chqDepositAmt) {
		this.chqDepositAmt = chqDepositAmt;
	}

	public int getChqIssueNo() {
		return chqIssueNo;
	}

	public void setChqIssueNo(int chqIssueNo) {
		this.chqIssueNo = chqIssueNo;
	}

	public BigDecimal getChqIssueAmt() {
		return chqIssueAmt;
	}

	public void setChqIssueAmt(BigDecimal chqIssueAmt) {
		this.chqIssueAmt = chqIssueAmt;
	}

	public int getInwardChqBounceNo() {
		return inwardChqBounceNo;
	}

	public void setInwardChqBounceNo(int inwardChqBounceNo) {
		this.inwardChqBounceNo = inwardChqBounceNo;
	}

	public int getOutwardChqBounceNo() {
		return outwardChqBounceNo;
	}

	public void setOutwardChqBounceNo(int outwardChqBounceNo) {
		this.outwardChqBounceNo = outwardChqBounceNo;
	}

	public BigDecimal getEodBalMin() {
		return eodBalMin;
	}

	public void setEodBalMin(BigDecimal eodBalMin) {
		this.eodBalMin = eodBalMin;
	}

	public BigDecimal getEodBalMax() {
		return eodBalMax;
	}

	public void setEodBalMax(BigDecimal eodBalMax) {
		this.eodBalMax = eodBalMax;
	}

	public BigDecimal getEodBalAvg() {
		return eodBalAvg;
	}

	public void setEodBalAvg(BigDecimal eodBalAvg) {
		this.eodBalAvg = eodBalAvg;
	}
	
}
