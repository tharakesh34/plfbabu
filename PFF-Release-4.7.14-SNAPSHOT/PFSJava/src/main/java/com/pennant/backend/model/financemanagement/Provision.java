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
 * FileName    		:  Provision.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Provision table</b>.<br>
 * 
 */
public class Provision extends AbstractWorkflowEntity {
	private static final long		serialVersionUID		= 1L;
	private String					finReference			= null;
	private String					finBranch;
	private String					finType;
	private long					custID;
	private Date					provisionCalDate;
	private BigDecimal				provisionedAmt			= BigDecimal.ZERO;
	private BigDecimal				provisionAmtCal			= BigDecimal.ZERO;
	private BigDecimal				provisionDue			= BigDecimal.ZERO;
	private BigDecimal				nonFormulaProv			= BigDecimal.ZERO;
	private boolean					useNFProv;
	private boolean					autoReleaseNFP;
	private BigDecimal				principalDue			= BigDecimal.ZERO;
	private BigDecimal				profitDue				= BigDecimal.ZERO;
	private Date					dueFromDate;
	private Date					lastFullyPaidDate;
	private boolean					newRecord				= false;
	private String					lovValue;
	private Provision				befImage;
	private LoggedInUser			userDetails;
	private Date					prevProvisionCalDate;
	private BigDecimal				prevProvisionedAmt;
	private String					transRef;

	private String					finCcy;
	private String					lovDescCustCIF;
	private String					lovDescCustShrtName;
	private String					lovDescFinDivision;
	private int						duedays;
	private long					dpdBucketID;
	private long					npaBucketID;
	private BigDecimal				pftBal;
	private BigDecimal				priBal;
	private BigDecimal				prvovisionRate;

	private FinanceDetail			financeDetail;

	private List<ProvisionMovement>	provisionMovementList	= new ArrayList<ProvisionMovement>();
	private String rcdAction = "";

	public boolean isNew() {
		return isNewRecord();
	}

	public Provision() {
		super();
	}

	public Provision(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("financeDetail");
		excludeFields.add("prevProvisionCalDate");
		excludeFields.add("prevProvisionedAmt");
		excludeFields.add("transRef");
		excludeFields.add("financeDetail");
		excludeFields.add("rcdAction");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public Date getProvisionCalDate() {
		return provisionCalDate;
	}

	public void setProvisionCalDate(Date provisionCalDate) {
		this.provisionCalDate = provisionCalDate;
	}

	public BigDecimal getProvisionedAmt() {
		return provisionedAmt;
	}

	public void setProvisionedAmt(BigDecimal provisionedAmt) {
		this.provisionedAmt = provisionedAmt;
	}

	public BigDecimal getProvisionAmtCal() {
		return provisionAmtCal;
	}

	public void setProvisionAmtCal(BigDecimal provisionAmtCal) {
		this.provisionAmtCal = provisionAmtCal;
	}

	public BigDecimal getProvisionDue() {
		return provisionDue;
	}

	public void setProvisionDue(BigDecimal provisionDue) {
		this.provisionDue = provisionDue;
	}

	public BigDecimal getNonFormulaProv() {
		return nonFormulaProv;
	}

	public void setNonFormulaProv(BigDecimal nonFormulaProv) {
		this.nonFormulaProv = nonFormulaProv;
	}

	public boolean isUseNFProv() {
		return useNFProv;
	}

	public void setUseNFProv(boolean useNFProv) {
		this.useNFProv = useNFProv;
	}

	public boolean isAutoReleaseNFP() {
		return autoReleaseNFP;
	}

	public void setAutoReleaseNFP(boolean autoReleaseNFP) {
		this.autoReleaseNFP = autoReleaseNFP;
	}

	public BigDecimal getPrincipalDue() {
		return principalDue;
	}

	public void setPrincipalDue(BigDecimal principalDue) {
		this.principalDue = principalDue;
	}

	public BigDecimal getProfitDue() {
		return profitDue;
	}

	public void setProfitDue(BigDecimal profitDue) {
		this.profitDue = profitDue;
	}

	public Date getDueFromDate() {
		return dueFromDate;
	}

	public void setDueFromDate(Date dueFromDate) {
		this.dueFromDate = dueFromDate;
	}

	public Date getLastFullyPaidDate() {
		return lastFullyPaidDate;
	}

	public void setLastFullyPaidDate(Date lastFullyPaidDate) {
		this.lastFullyPaidDate = lastFullyPaidDate;
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

	public Provision getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Provision beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public void setProvisionMovementList(List<ProvisionMovement> provisionMovementList) {
		this.provisionMovementList = provisionMovementList;
	}

	public List<ProvisionMovement> getProvisionMovementList() {
		return provisionMovementList;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public String getLovDescFinDivision() {
		return lovDescFinDivision;
	}

	public void setLovDescFinDivision(String lovDescFinDivision) {
		this.lovDescFinDivision = lovDescFinDivision;
	}

	public Date getPrevProvisionCalDate() {
		return prevProvisionCalDate;
	}

	public void setPrevProvisionCalDate(Date prevProvisionCalDate) {
		this.prevProvisionCalDate = prevProvisionCalDate;
	}

	public BigDecimal getPrevProvisionedAmt() {
		return prevProvisionedAmt;
	}

	public void setPrevProvisionedAmt(BigDecimal prevProvisionedAmt) {
		this.prevProvisionedAmt = prevProvisionedAmt;
	}

	public String getTransRef() {
		return transRef;
	}

	public void setTransRef(String transRef) {
		this.transRef = transRef;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public int getDuedays() {
		return duedays;
	}

	public void setDuedays(int duedays) {
		this.duedays = duedays;
	}

	public long getDpdBucketID() {
		return dpdBucketID;
	}

	public void setDpdBucketID(long dpdBucketID) {
		this.dpdBucketID = dpdBucketID;
	}

	public long getNpaBucketID() {
		return npaBucketID;
	}

	public void setNpaBucketID(long npaBucketID) {
		this.npaBucketID = npaBucketID;
	}

	public BigDecimal getPftBal() {
		return pftBal;
	}

	public void setPftBal(BigDecimal pftBal) {
		this.pftBal = pftBal;
	}

	public BigDecimal getPriBal() {
		return priBal;
	}

	public void setPriBal(BigDecimal priBal) {
		this.priBal = priBal;
	}

	public BigDecimal getPrvovisionRate() {
		return prvovisionRate;
	}

	public void setPrvovisionRate(BigDecimal prvovisionRate) {
		this.prvovisionRate = prvovisionRate;
	}

	public String getRcdAction() {
		return rcdAction;
	}

	public void setRcdAction(String rcdAction) {
		this.rcdAction = rcdAction;
	}

}
