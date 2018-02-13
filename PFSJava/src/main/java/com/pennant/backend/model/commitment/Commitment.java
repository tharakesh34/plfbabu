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
 * FileName    		:  Commitment.java  	                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.commitment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Commitment table</b>.<br>
 * 
 */
public class Commitment extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String								cmtReference;
	private long 								custID;
	private String								cmtBranch;
	private boolean 							openAccount;
	private String 								cmtAccount;
	private String 								cmtCcy;
	private BigDecimal 							cmtPftRateMin;
	private BigDecimal 							cmtPftRateMax;
	private BigDecimal 							cmtAmount;
	private BigDecimal 							cmtUtilizedAmount;
	private BigDecimal 							cmtAvailable;
	private Date 								cmtPromisedDate;
	private Date 								cmtStartDate;
	private Date 								cmtExpDate;
	private boolean 							activeStatus;
	private boolean 							nonperformingStatus;
	private String 								cmtTitle;
	private String 								cmtNotes;
	private boolean 							revolving;
	private boolean 							sharedCmt;
	private boolean 							multiBranch;

	private BigDecimal 							cmtCharges;
	private String 								chargesAccount;
	private boolean 							cmtActive;
	private boolean 							cmtStopRateRange;
	private boolean 							nonPerforming;

	private String 								custShrtName;
	private String 								custCIF;
	private String 								branchDesc;
	private String 								chargesAccountName;
	private String 								cmtAccountName;
	private String 								custCtgCode;
	private Date 								custDOB;
	private String 								facilityRef;
	private String 								facilityRefDesc;
	private long 								limitLineId;
	private String 								limitLineCode;
	private String 								limitLineCodeDesc;

	private boolean 							newRecord 				= false;
	private String 								lovValue;
	private Commitment 							befImage;
	private LoggedInUser 						userDetails;
	public CommitmentMovement 					commitmentMovement;

	private int 								cmtAvailableMonths;
	private Date 								cmtRvwDate;
	private boolean 							collateralRequired;
	private Date 								cmtEndDate;

	private HashMap<String, List<AuditDetail>>	auditDetailMap 			= new HashMap<String, List<AuditDetail>>();
	private Map<Long, Long> 					selAnsCountMap 			= new HashMap<Long, Long>(1);

	private CustomerDetails 					customerDetails;
	private List<FinanceReferenceDetail> 		aggrements 				= null;
	private List<DocumentDetails> 				documents				= null;
	private List<FinanceReferenceDetail> 		checkLists				= null;
	private List<FinanceCheckListReference> 	commitmentCheckLists	= null;
	private List<FinFlagsDetail> 				cmtFlagDetailList;
	private List<CommitmentRate> 				commitmentRateList;
	private List<CollateralAssignment> 			collateralAssignmentList = new ArrayList<CollateralAssignment>();


	public boolean isNew() {
		return isNewRecord();
	}

	public Commitment() {
		super();
		this.commitmentMovement = new CommitmentMovement();
	}

	public Commitment(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("commitmentMovement");
		excludeFields.add("custShrtName");
		excludeFields.add("branchDesc");
		excludeFields.add("chargesAccountName");
		excludeFields.add("cmtAccountName");
		excludeFields.add("activeStatus");
		excludeFields.add("nonperformingStatus");
		excludeFields.add("custCtgCode");
		excludeFields.add("custDOB");
		excludeFields.add("custCIF");
		excludeFields.add("facilityRefDesc");
		excludeFields.add("limitLineCode");
		excludeFields.add("limitLineCodeDesc");

		excludeFields.add("cmtFlagDetailList");
		excludeFields.add("commitmentRateList");
		excludeFields.add("customerDetails");
		excludeFields.add("aggrements");
		excludeFields.add("checkLists");
		excludeFields.add("documents");
		excludeFields.add("commitmentCheckLists");
		excludeFields.add("selAnsCountMap");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return cmtReference;
	}

	public CommitmentMovement getCommitmentMovement() {
		return commitmentMovement;
	}

	public void setCommitmentMovement(CommitmentMovement commitmentMovement) {
		this.commitmentMovement = commitmentMovement;
	}

	public void setId(String id) {
		this.cmtReference = id;
	}

	public String getCmtReference() {
		return cmtReference;
	}

	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCmtBranch() {
		return cmtBranch;
	}

	public void setCmtBranch(String cmtBranch) {
		this.cmtBranch = cmtBranch;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public boolean isOpenAccount() {
		return openAccount;
	}

	public void setOpenAccount(boolean openAccount) {
		this.openAccount = openAccount;
	}

	public String getCmtAccount() {
		return cmtAccount;
	}

	public void setCmtAccount(String cmtAccount) {
		this.cmtAccount = cmtAccount;
	}

	public String getCmtAccountName() {
		return cmtAccountName;
	}

	public void setCmtAccountName(String cmtAccountName) {
		this.cmtAccountName = cmtAccountName;
	}

	public String getCmtCcy() {
		return cmtCcy;
	}

	public void setCmtCcy(String cmtCcy) {
		this.cmtCcy = cmtCcy;
	}

	public BigDecimal getCmtPftRateMin() {
		return cmtPftRateMin;
	}

	public void setCmtPftRateMin(BigDecimal cmtPftRateMin) {
		this.cmtPftRateMin = cmtPftRateMin;
	}

	public BigDecimal getCmtPftRateMax() {
		return cmtPftRateMax;
	}

	public void setCmtPftRateMax(BigDecimal cmtPftRateMax) {
		this.cmtPftRateMax = cmtPftRateMax;
	}

	public BigDecimal getCmtAmount() {
		return cmtAmount;
	}

	public void setCmtAmount(BigDecimal cmtAmount) {
		this.cmtAmount = cmtAmount;
	}

	public BigDecimal getCmtUtilizedAmount() {
		return cmtUtilizedAmount;
	}

	public void setCmtUtilizedAmount(BigDecimal cmtUtilizedAmount) {
		this.cmtUtilizedAmount = cmtUtilizedAmount;
	}

	public BigDecimal getCmtAvailable() {
		return cmtAvailable;
	}

	public void setCmtAvailable(BigDecimal cmtAvailable) {
		this.cmtAvailable = cmtAvailable;
	}

	public Date getCmtPromisedDate() {
		return cmtPromisedDate;
	}

	public void setCmtPromisedDate(Date cmtPromisedDate) {
		this.cmtPromisedDate = cmtPromisedDate;
	}

	public Date getCmtStartDate() {
		return cmtStartDate;
	}

	public void setCmtStartDate(Date cmtStartDate) {
		this.cmtStartDate = cmtStartDate;
	}

	public Date getCmtExpDate() {
		return cmtExpDate;
	}

	public void setCmtExpDate(Date cmtExpDate) {
		this.cmtExpDate = cmtExpDate;
	}

	public Date getCmtRvwDate() {
		return cmtRvwDate;
	}

	public void setCmtRvwDate(Date cmtRvwDate) {
		this.cmtRvwDate = cmtRvwDate;
	}

	public String getCmtTitle() {
		return cmtTitle;
	}

	public BigDecimal getCmtCharges() {
		return cmtCharges;
	}

	public void setCmtCharges(BigDecimal cmtCharges) {
		this.cmtCharges = cmtCharges;
	}

	public String getChargesAccount() {
		return chargesAccount;
	}

	public void setChargesAccount(String chargesAccount) {
		this.chargesAccount = chargesAccount;
	}

	public String getChargesAccountName() {
		return chargesAccountName;
	}

	public void setChargesAccountName(String chargesAccountName) {
		this.chargesAccountName = chargesAccountName;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public boolean isNonperformingStatus() {
		return nonperformingStatus;
	}

	public void setNonperformingStatus(boolean nonperformingStatus) {
		this.nonperformingStatus = nonperformingStatus;
	}

	public void setCmtTitle(String cmtTitle) {
		this.cmtTitle = cmtTitle;
	}

	public String getCmtNotes() {
		return cmtNotes;
	}

	public void setCmtNotes(String cmtNotes) {
		this.cmtNotes = cmtNotes;
	}

	public boolean isRevolving() {
		return revolving;
	}

	public void setRevolving(boolean revolving) {
		this.revolving = revolving;
	}

	public boolean isSharedCmt() {
		return sharedCmt;
	}

	public void setSharedCmt(boolean sharedCmt) {
		this.sharedCmt = sharedCmt;
	}

	public boolean isMultiBranch() {
		return multiBranch;
	}

	public void setMultiBranch(boolean multiBranch) {
		this.multiBranch = multiBranch;
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

	public Commitment getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Commitment beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setCmtActive(boolean cmtActive) {
		this.cmtActive = cmtActive;
	}

	public boolean isCmtActive() {
		return cmtActive;
	}

	public void setCmtStopRateRange(boolean cmtStopRateRange) {
		this.cmtStopRateRange = cmtStopRateRange;
	}

	public boolean isCmtStopRateRange() {
		return cmtStopRateRange;
	}

	public void setNonPerforming(boolean nonPerforming) {
		this.nonPerforming = nonPerforming;
	}

	public boolean isNonPerforming() {
		return nonPerforming;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setFacilityRef(String facilityRef) {
		this.facilityRef = facilityRef;
	}

	public String getFacilityRef() {
		return facilityRef;
	}

	public void setFacilityRefDesc(String facilityRefDesc) {
		this.facilityRefDesc = facilityRefDesc;
	}

	public String getFacilityRefDesc() {
		return facilityRefDesc;
	}
	public int getCmtAvailableMonths() {
		return cmtAvailableMonths;
	}

	public void setCmtAvailableMonths(int cmtAvailableMonths) {
		this.cmtAvailableMonths = cmtAvailableMonths;
	}

	public boolean isCollateralRequired() {
		return collateralRequired;
	}

	public void setCollateralRequired(boolean collateralRequired) {
		this.collateralRequired = collateralRequired;
	}

	public Date getCmtEndDate() {
		return cmtEndDate;
	}

	public void setCmtEndDate(Date cmtEndDate) {
		this.cmtEndDate = cmtEndDate;
	}

	public List<CommitmentRate> getCommitmentRateList() {
		return commitmentRateList;
	}

	public void setCommitmentRateList(List<CommitmentRate> commitmentRateList) {
		this.commitmentRateList = commitmentRateList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public List<FinanceReferenceDetail> getAggrements() {
		return aggrements;
	}

	public void setAggrements(List<FinanceReferenceDetail> aggrements) {
		this.aggrements = aggrements;
	}

	public List<FinanceReferenceDetail> getCheckLists() {
		return checkLists;
	}

	public void setCheckLists(List<FinanceReferenceDetail> checkLists) {
		this.checkLists = checkLists;
	}

	public List<DocumentDetails> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDetails> documents) {
		this.documents = documents;
	}

	public List<FinanceCheckListReference> getCommitmentCheckLists() {
		return commitmentCheckLists;
	}

	public void setCommitmentCheckLists(List<FinanceCheckListReference> commitmentCheckLists) {
		this.commitmentCheckLists = commitmentCheckLists;
	}

	public List<FinFlagsDetail> getCmtFlagDetailList() {
		return cmtFlagDetailList;
	}

	public void setCmtFlagDetailList(List<FinFlagsDetail> cmtFlagDetailList) {
		this.cmtFlagDetailList = cmtFlagDetailList;
	}

	public Map<Long, Long> getSelAnsCountMap() {
		return selAnsCountMap;
	}

	public void setSelAnsCountMap(Map<Long, Long> selAnsCountMap) {
		this.selAnsCountMap = selAnsCountMap;
	}

	public List<CollateralAssignment> getCollateralAssignmentList() {
		return collateralAssignmentList;
	}

	public void setCollateralAssignmentList(List<CollateralAssignment> collateralAssignmentList) {
		this.collateralAssignmentList = collateralAssignmentList;
	}

	public long getLimitLineId() {
		return limitLineId;
	}

	public void setLimitLineId(long limitLineId) {
		this.limitLineId = limitLineId;
	}

	public String getLimitLineCode() {
		return limitLineCode;
	}

	public void setLimitLineCode(String limitLineCode) {
		this.limitLineCode = limitLineCode;
	}

	public String getLimitLineCodeDesc() {
		return limitLineCodeDesc;
	}

	public void setLimitLineCodeDesc(String limitLineCodeDesc) {
		this.limitLineCodeDesc = limitLineCodeDesc;
	}
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> commitmentMap = new HashMap<String, Object>();	
		
		return getDeclaredFieldValues(commitmentMap);
	}

	public HashMap<String, Object> getDeclaredFieldValues(HashMap<String, Object> commitmentMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				commitmentMap.put("cmt_" + this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return commitmentMap;
	}

}
