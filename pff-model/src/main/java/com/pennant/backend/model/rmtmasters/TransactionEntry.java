/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : TransactionEntry.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * * Modified
 * Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.rmtmasters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>TransactionEntry table</b>.<br>
 *
 */
public class TransactionEntry extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -477902982694204735L;
	private long accountSetid;
	private int transOrder;
	private String transDesc;
	private String debitcredit;
	private boolean shadowPosting;
	private String account;
	private String accountType;
	private String accountBranch;
	private String lovDescAccountTypeName;
	private String lovDescAccountBranchName;
	private String accountSubHeadRule;
	private String lovDescAccountSubHeadRuleName;
	private String transcationCode;
	private String lovDescTranscationCodeName;
	private String rvsTransactionCode;
	private String lovDescRvsTransactionCodeName;
	private String amountRule;
	private String chargeType;
	private String postToSys;
	private int derivedTranOrder;

	private String feeCode;
	// private String ruleDecider;
	private String lovDescFeeCodeName;
	private boolean entryByInvestment;
	private boolean openNewFinAc;
	private String lovValue;
	private TransactionEntry befImage;

	@XmlTransient
	private LoggedInUser userDetails;

	private String lovDescEventCodeName;
	private String lovDescEventCodeDesc;
	private String lovDescAccSetCodeName;
	private String lovDescAccSetCodeDesc;
	private String lovDescSysInAcTypeName;

	// ### START SFA_20210405 -->
	private boolean feeRepeat = false;
	private int receivableOrPayable;
	private boolean assignmentEntry = false;
	private boolean bulking = false;
	private String glCode;

	private List<TransactionEntry> singleFeeCGSTTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeSGSTTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeUGSTTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeIGSTTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeCESSTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeTDSTxn = new ArrayList<>();
	private List<TransactionEntry> singleFeeWaiverOrRefundTxn = new ArrayList<>();

	private boolean singelFeeEntry;

	// ### END SFA_20210405 <--

	public TransactionEntry() {
		super();
	}

	public TransactionEntry(long id) {
		super();
		this.setId(id);
	}

	public TransactionEntry copyEntity() {
		TransactionEntry entity = new TransactionEntry();
		entity.setAccountSetid(this.accountSetid);
		entity.setTransOrder(this.transOrder);
		entity.setTransDesc(this.transDesc);
		entity.setDebitcredit(this.debitcredit);
		entity.setShadowPosting(this.shadowPosting);
		entity.setAccount(this.account);
		entity.setAccountType(this.accountType);
		entity.setAccountBranch(this.accountBranch);
		entity.setLovDescAccountTypeName(this.lovDescAccountTypeName);
		entity.setLovDescAccountBranchName(this.lovDescAccountBranchName);
		entity.setAccountSubHeadRule(this.accountSubHeadRule);
		entity.setLovDescAccountSubHeadRuleName(this.lovDescAccountSubHeadRuleName);
		entity.setTranscationCode(this.transcationCode);
		entity.setLovDescTranscationCodeName(this.lovDescTranscationCodeName);
		entity.setRvsTransactionCode(this.rvsTransactionCode);
		entity.setLovDescRvsTransactionCodeName(this.lovDescRvsTransactionCodeName);
		entity.setAmountRule(this.amountRule);
		entity.setChargeType(this.chargeType);
		entity.setPostToSys(this.postToSys);
		entity.setDerivedTranOrder(this.derivedTranOrder);
		entity.setFeeCode(this.feeCode);
		entity.setLovDescFeeCodeName(this.lovDescFeeCodeName);
		entity.setEntryByInvestment(this.entryByInvestment);
		entity.setOpenNewFinAc(this.openNewFinAc);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setLovDescEventCodeName(this.lovDescEventCodeName);
		entity.setLovDescEventCodeDesc(this.lovDescEventCodeDesc);
		entity.setLovDescAccSetCodeName(this.lovDescAccSetCodeName);
		entity.setLovDescAccSetCodeDesc(this.lovDescAccSetCodeDesc);
		entity.setLovDescSysInAcTypeName(this.lovDescSysInAcTypeName);
		entity.setFeeRepeat(this.feeRepeat);
		entity.setReceivableOrPayable(this.receivableOrPayable);
		entity.setAssignmentEntry(this.assignmentEntry);
		entity.setBulking(this.bulking);
		entity.setGlCode(this.glCode);
		this.singleFeeCGSTTxn.stream().forEach(e -> entity.getSingleFeeCGSTTxn().add(e));
		this.singleFeeSGSTTxn.stream().forEach(e -> entity.getSingleFeeSGSTTxn().add(e));
		this.singleFeeUGSTTxn.stream().forEach(e -> entity.getSingleFeeUGSTTxn().add(e));
		this.singleFeeIGSTTxn.stream().forEach(e -> entity.getSingleFeeIGSTTxn().add(e));
		this.singleFeeCESSTxn.stream().forEach(e -> entity.getSingleFeeCESSTxn().add(e));
		this.singleFeeTDSTxn.stream().forEach(e -> entity.getSingleFeeTDSTxn().add(e));
		this.singleFeeWaiverOrRefundTxn.stream().forEach(e -> entity.getSingleFeeWaiverOrRefundTxn().add(e));
		entity.setSingelFeeEntry(this.singelFeeEntry);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("glCode");
		excludeFields.add("singleFeeCGSTTxn");
		excludeFields.add("singleFeeSGSTTxn");
		excludeFields.add("singleFeeUGSTTxn");
		excludeFields.add("singleFeeIGSTTxn");
		excludeFields.add("singleFeeCESSTxn");
		excludeFields.add("singleFeeTDSTxn");
		excludeFields.add("singleFeeWaiverOrRefundTxn");
		excludeFields.add("singelFeeEntry");

		return excludeFields;
	}

	// Getter and Setter methods

	public long getId() {
		return accountSetid;
	}

	public void setId(long id) {
		this.accountSetid = id;
	}

	public long getAccountSetid() {
		return accountSetid;
	}

	public void setAccountSetid(long accountSetid) {
		this.accountSetid = accountSetid;
	}

	public int getTransOrder() {
		return transOrder;
	}

	public void setTransOrder(int transOrder) {
		this.transOrder = transOrder;
	}

	public String getTransDesc() {
		return transDesc;
	}

	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}

	public String getDebitcredit() {
		return debitcredit;
	}

	public void setDebitcredit(String debitcredit) {
		this.debitcredit = debitcredit;
	}

	/**
	 * @return the shadowPosting
	 */
	public boolean isShadowPosting() {
		return shadowPosting;
	}

	/**
	 * @param shadowPosting the shadowPosting to set
	 */
	public void setShadowPosting(boolean shadowPosting) {
		this.shadowPosting = shadowPosting;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the accountBranch
	 */
	public String getAccountBranch() {
		return accountBranch;
	}

	/**
	 * @param accountBranch the accountBranch to set
	 */
	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getLovDescAccountTypeName() {
		return this.lovDescAccountTypeName;
	}

	public void setLovDescAccountTypeName(String lovDescAccountTypeName) {
		this.lovDescAccountTypeName = lovDescAccountTypeName;
	}

	public String getAccountSubHeadRule() {
		return accountSubHeadRule;
	}

	public void setAccountSubHeadRule(String accountSubHeadRule) {
		this.accountSubHeadRule = accountSubHeadRule;
	}

	public String getLovDescAccountSubHeadRuleName() {
		return this.lovDescAccountSubHeadRuleName;
	}

	public void setLovDescAccountSubHeadRuleName(String lovDescAccountSubHeadRuleName) {
		this.lovDescAccountSubHeadRuleName = lovDescAccountSubHeadRuleName;
	}

	public String getTranscationCode() {
		return transcationCode;
	}

	public void setTranscationCode(String transcationCode) {
		this.transcationCode = transcationCode;
	}

	public String getLovDescTranscationCodeName() {
		return this.lovDescTranscationCodeName;
	}

	public void setLovDescTranscationCodeName(String lovDescTranscationCodeName) {
		this.lovDescTranscationCodeName = lovDescTranscationCodeName;
	}

	public String getRvsTransactionCode() {
		return rvsTransactionCode;
	}

	public void setRvsTransactionCode(String rvsTransactionCode) {
		this.rvsTransactionCode = rvsTransactionCode;
	}

	public String getLovDescRvsTransactionCodeName() {
		return this.lovDescRvsTransactionCodeName;
	}

	public void setLovDescRvsTransactionCodeName(String lovDescRvsTransactionCodeName) {
		this.lovDescRvsTransactionCodeName = lovDescRvsTransactionCodeName;
	}

	public String getAmountRule() {
		return amountRule;
	}

	public void setAmountRule(String amountRule) {
		this.amountRule = amountRule;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public TransactionEntry getBefImage() {
		return this.befImage;
	}

	public void setBefImage(TransactionEntry beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescEventCodeName(String lovDescEventCodeName) {
		this.lovDescEventCodeName = lovDescEventCodeName;
	}

	public String getLovDescEventCodeName() {
		return lovDescEventCodeName;
	}

	public void setLovDescAccSetCodeName(String lovDescAccSetCodeName) {
		this.lovDescAccSetCodeName = lovDescAccSetCodeName;
	}

	public String getLovDescAccSetCodeName() {
		return lovDescAccSetCodeName;
	}

	public void setLovDescAccSetCodeDesc(String lovDescAccSetCodeDesc) {
		this.lovDescAccSetCodeDesc = lovDescAccSetCodeDesc;
	}

	public String getLovDescAccSetCodeDesc() {
		return lovDescAccSetCodeDesc;
	}

	/**
	 * @return the lovDescAccountBranchName
	 */
	public String getLovDescAccountBranchName() {
		return lovDescAccountBranchName;
	}

	/**
	 * @param lovDescAccountBranchName the lovDescAccountBranchName to set
	 */
	public void setLovDescAccountBranchName(String lovDescAccountBranchName) {
		this.lovDescAccountBranchName = lovDescAccountBranchName;
	}

	public String getLovDescEventCodeDesc() {
		return lovDescEventCodeDesc;
	}

	public void setLovDescEventCodeDesc(String lovDescEventCodeDesc) {
		this.lovDescEventCodeDesc = lovDescEventCodeDesc;
	}

	public void setLovDescSysInAcTypeName(String lovDescSysInAcTypeName) {
		this.lovDescSysInAcTypeName = lovDescSysInAcTypeName;
	}

	public String getLovDescSysInAcTypeName() {
		return lovDescSysInAcTypeName;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	/*
	 * public String getRuleDecider() { return ruleDecider; }
	 */

	public String getLovDescFeeCodeName() {
		return lovDescFeeCodeName;
	}

	public void setLovDescFeeCodeName(String lovDescFeeCodeName) {
		this.lovDescFeeCodeName = lovDescFeeCodeName;
	}

	/*
	 * public void setRuleDecider(String ruleDecider) { this.ruleDecider = ruleDecider; }
	 */

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public String getChargeType() {
		return chargeType;
	}

	public String getPostToSys() {
		return postToSys;
	}

	public void setPostToSys(String postToSys) {
		this.postToSys = postToSys;
	}

	public void setEntryByInvestment(boolean entryByInvestment) {
		this.entryByInvestment = entryByInvestment;
	}

	public boolean isEntryByInvestment() {
		return entryByInvestment;
	}

	public void setOpenNewFinAc(boolean openNewFinAc) {
		this.openNewFinAc = openNewFinAc;
	}

	public boolean isOpenNewFinAc() {
		return openNewFinAc;
	}

	public int getDerivedTranOrder() {
		return derivedTranOrder;
	}

	public void setDerivedTranOrder(int derivedTranOrder) {
		this.derivedTranOrder = derivedTranOrder;
	}

	public boolean isFeeRepeat() {
		return feeRepeat;
	}

	public void setFeeRepeat(boolean feeRepeat) {
		this.feeRepeat = feeRepeat;
	}

	public int getReceivableOrPayable() {
		return receivableOrPayable;
	}

	public void setReceivableOrPayable(int receivableOrPayable) {
		this.receivableOrPayable = receivableOrPayable;
	}

	public boolean isAssignmentEntry() {
		return assignmentEntry;
	}

	public void setAssignmentEntry(boolean assignmentEntry) {
		this.assignmentEntry = assignmentEntry;
	}

	public boolean isBulking() {
		return bulking;
	}

	public void setBulking(boolean bulking) {
		this.bulking = bulking;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public List<TransactionEntry> getSingleFeeCGSTTxn() {
		return singleFeeCGSTTxn;
	}

	public void setSingleFeeCGSTTxn(List<TransactionEntry> singleFeeCGSTTxn) {
		this.singleFeeCGSTTxn = singleFeeCGSTTxn;
	}

	public List<TransactionEntry> getSingleFeeSGSTTxn() {
		return singleFeeSGSTTxn;
	}

	public void setSingleFeeSGSTTxn(List<TransactionEntry> singleFeeSGSTTxn) {
		this.singleFeeSGSTTxn = singleFeeSGSTTxn;
	}

	public List<TransactionEntry> getSingleFeeUGSTTxn() {
		return singleFeeUGSTTxn;
	}

	public void setSingleFeeUGSTTxn(List<TransactionEntry> singleFeeUGSTTxn) {
		this.singleFeeUGSTTxn = singleFeeUGSTTxn;
	}

	public List<TransactionEntry> getSingleFeeIGSTTxn() {
		return singleFeeIGSTTxn;
	}

	public void setSingleFeeIGSTTxn(List<TransactionEntry> singleFeeIGSTTxn) {
		this.singleFeeIGSTTxn = singleFeeIGSTTxn;
	}

	public List<TransactionEntry> getSingleFeeCESSTxn() {
		return singleFeeCESSTxn;
	}

	public void setSingleFeeCESSTxn(List<TransactionEntry> singleFeeCESSTxn) {
		this.singleFeeCESSTxn = singleFeeCESSTxn;
	}

	public List<TransactionEntry> getSingleFeeTDSTxn() {
		return singleFeeTDSTxn;
	}

	public void setSingleFeeTDSTxn(List<TransactionEntry> singleFeeTDSTxn) {
		this.singleFeeTDSTxn = singleFeeTDSTxn;
	}

	public List<TransactionEntry> getSingleFeeWaiverOrRefundTxn() {
		return singleFeeWaiverOrRefundTxn;
	}

	public void setSingleFeeWaiverOrRefundTxn(List<TransactionEntry> singleFeeWaiverOrRefundTxn) {
		this.singleFeeWaiverOrRefundTxn = singleFeeWaiverOrRefundTxn;
	}

	public boolean isSingelFeeEntry() {
		return singelFeeEntry;
	}

	public void setSingelFeeEntry(boolean singelFeeEntry) {
		this.singelFeeEntry = singelFeeEntry;
	}

}
