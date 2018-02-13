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
 * FileName    		:  ManagerCheque.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ManagerCheque table</b>.<br>
 *
 */
public class ManagerCheque extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long chequeID = Long.MIN_VALUE;
	private String chqPurposeCode;
	private String chqPurposeCodeName;
	private String chequeRef;
	private String chequeNo;
	private String beneficiaryName;
	private String custCIF;
	private String draftCcy;
	private String fundingCcy;
	private String fundingAccount;
	private String nostroAccount;
	private String nostroFullName;
	private BigDecimal chequeAmount = BigDecimal.ZERO; 
	private Date valueDate;
	private String narration1;
	private String narration2;
	private boolean reprint;
	private long oldChequeID;
	private boolean cancel;
	private boolean newRecord;
	private String lovValue;
	private ManagerCheque befImage;
	private LoggedInUser userDetails;
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private List<ReturnDataSet> returnDataSetList;
	private ManagerCheque reprintManagerCheque;
	
 	private String branchCode;
	private String lovDescBranchDesc;
	private String stopOrderRef;
	
	private BigDecimal chargeAmount = BigDecimal.ZERO; 
	private BigDecimal fundingAmount = BigDecimal.ZERO;
	private Date issueDate;
	private String todayDate;
	private String loginUsrName;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;
	private String addressLine5;
	private String amtInWords;
	private BigDecimal amtInLocalCcy = BigDecimal.ZERO;
	
	//Added for Accounting Report Purpose
	private String finReference;
	private String finType;
	private String lovDescFinTypeName;
	private String lovDescCustShrtName;

	public boolean isNew() {
		return isNewRecord();
	}

	public ManagerCheque() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("ManagerCheque"));
	}

	public ManagerCheque(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("chqPurposeCodeName");
		excludeFields.add("reprintManagerCheque");
		excludeFields.add("lovDescBranchDesc");
		excludeFields.add("fundingAmount");
		excludeFields.add("todayDate");
		excludeFields.add("loginUsrName");
		excludeFields.add("amtInWords");
		excludeFields.add("amtInLocalCcy");
		excludeFields.add("finReference");
		excludeFields.add("finType");
		excludeFields.add("lovDescFinTypeName");
		excludeFields.add("lovDescCustShrtName");
	 
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return chequeID;
	}

	public void setId (long id) {
		this.chequeID = id;
	}

	public long getChequeID() {
		return chequeID;
	}
	public void setChequeID(long chequeID) {
		this.chequeID = chequeID;
	}

	public String getChqPurposeCode() {
		return chqPurposeCode;
	}
	public void setChqPurposeCode(String chqPurposeCode) {
		this.chqPurposeCode = chqPurposeCode;
	}

	public String getChqPurposeCodeName() {
		return this.chqPurposeCodeName;
	}

	public void setChqPurposeCodeName (String chqPurposeCodeName) {
		this.chqPurposeCodeName = chqPurposeCodeName;
	}

	public String getChequeRef() {
		return chequeRef;
	}
	public void setChequeRef(String chequeRef) {
		this.chequeRef = chequeRef;
	}

	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getDraftCcy() {
		return draftCcy;
	}
	public void setDraftCcy(String draftCcy) {
		this.draftCcy = draftCcy;
	}

	public String getFundingCcy() {
		return fundingCcy;
	}
	public void setFundingCcy(String fundingCcy) {
		this.fundingCcy = fundingCcy;
	}

	public String getFundingAccount() {
		return fundingAccount;
	}
	public void setFundingAccount(String fundingAccount) {
		this.fundingAccount = fundingAccount;
	}

	public String getNostroAccount() {
		return nostroAccount;
	}
	public void setNostroAccount(String nostroAccount) {
		this.nostroAccount = nostroAccount;
	}
	public String getNostroFullName() {
		return nostroFullName;
	}
	public void setNostroFullName(String nostroFullName) {
		this.nostroFullName = nostroFullName;
	}

	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getNarration1() {
		return narration1;
	}
	public void setNarration1(String narration1) {
		this.narration1 = narration1;
	}

	public String getNarration2() {
		return narration2;
	}
	public void setNarration2(String narration2) {
		this.narration2 = narration2;
	}

	public boolean isReprint() {
		return reprint;
	}
	public void setReprint(boolean reprint) {
		this.reprint = reprint;
	}


	public long getOldChequeID() {
		return oldChequeID;
	}
	public void setOldChequeID(long oldChequeID) {
		this.oldChequeID = oldChequeID;
	}

	public boolean isCancel() {
		return cancel;
	}
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
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

	public ManagerCheque getBefImage(){
		return this.befImage;
	}

	public void setBefImage(ManagerCheque beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}
	
	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public ManagerCheque getReprintManagerCheque() {
		return reprintManagerCheque;
	}

	public void setReprintManagerCheque(ManagerCheque reprintManagerCheque) {
		this.reprintManagerCheque = reprintManagerCheque;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getLovDescBranchDesc() {
	    return lovDescBranchDesc;
    }
	public void setLovDescBranchDesc(String lovDescBranchDesc) {
	    this.lovDescBranchDesc = lovDescBranchDesc;
    }

	public String getStopOrderRef() {
	    return stopOrderRef;
    }
	public void setStopOrderRef(String stopOrderRef) {
	    this.stopOrderRef = stopOrderRef;
    }

	public List<ReturnDataSet> getReturnDataSetList() {
		return returnDataSetList;
	}

	public void setReturnDataSetList(List<ReturnDataSet> returnDataSetList) {
		this.returnDataSetList = returnDataSetList;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public BigDecimal getFundingAmount() {
		return fundingAmount;
	}

	public void setFundingAmount(BigDecimal fundingAmount) {
		this.fundingAmount = fundingAmount;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(String todayDate) {
		this.todayDate = todayDate;
	}

	public String getLoginUsrName() {
		return loginUsrName;
	}

	public void setLoginUsrName(String loginUsrName) {
		this.loginUsrName = loginUsrName;
	}

	public String getAmtInWords() {
	    return amtInWords;
    }

	public void setAmtInWords(String amtInWords) {
	    this.amtInWords = amtInWords;
    }

	public BigDecimal getAmtInLocalCcy() {
	    return amtInLocalCcy;
    }

	public void setAmtInLocalCcy(BigDecimal amtInLocalCcy) {
	    this.amtInLocalCcy = amtInLocalCcy;
    }

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressLine5() {
		return addressLine5;
	}

	public void setAddressLine5(String addressLine5) {
		this.addressLine5 = addressLine5;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovDescFinTypeName() {
		return lovDescFinTypeName;
	}

	public void setLovDescFinTypeName(String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
	}

	public String getLovDescCustShrtName() {
	    return lovDescCustShrtName;
    }

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
	    this.lovDescCustShrtName = lovDescCustShrtName;
    } 
}
