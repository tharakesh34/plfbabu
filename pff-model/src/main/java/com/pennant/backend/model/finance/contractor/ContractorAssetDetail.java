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
 * FileName    		:  ContractorAssetDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance.contractor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ContractorAssetDetail table</b>.<br>
 *
 */
public class ContractorAssetDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private long contractorId;
	private String contractorName;
	private BigDecimal dftRetentionPerc= BigDecimal.ZERO;
	private Date retentionTillDate;
	private long custID;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String assetDesc;
	private BigDecimal assetValue= BigDecimal.ZERO;
	private BigDecimal totClaimAmt= BigDecimal.ZERO;
	private BigDecimal totAdvanceAmt= BigDecimal.ZERO;
 	private BigDecimal lovDescClaimPercent;
 	
	private boolean newRecord=false;
	private String lovValue;
	private ContractorAssetDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public ContractorAssetDetail() {
		super();
	}

	public ContractorAssetDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finReference;
	}
	
	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public long getContractorId() {
		return contractorId;
	}
	public void setContractorId(long contractorId) {
		this.contractorId = contractorId;
	}
	
	public long getCustID() {
    	return custID;
    }

	public void setCustID(long custID) {
    	this.custID = custID;
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

	public String getAssetDesc() {
		return assetDesc;
	}
	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}
	
	
		
	
	public BigDecimal getAssetValue() {
		return assetValue;
	}
	public void setAssetValue(BigDecimal assetValue) {
		this.assetValue = assetValue;
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

	public ContractorAssetDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ContractorAssetDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getLovDescClaimPercent() {
    	return lovDescClaimPercent;
    }
	public void setLovDescClaimPercent(BigDecimal lovDescClaimPercent) {
    	this.lovDescClaimPercent = lovDescClaimPercent;
    }

	public BigDecimal getTotClaimAmt() {
    	return totClaimAmt;
    }
	public void setTotClaimAmt(BigDecimal totClaimAmt) {
    	this.totClaimAmt = totClaimAmt;
    }

	public BigDecimal getTotAdvanceAmt() {
    	return totAdvanceAmt;
    }
	public void setTotAdvanceAmt(BigDecimal totAdvanceAmt) {
    	this.totAdvanceAmt = totAdvanceAmt;
    }

	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}

	public BigDecimal getDftRetentionPerc() {
		return dftRetentionPerc;
	}
	public void setDftRetentionPerc(BigDecimal dftRetentionPerc) {
		this.dftRetentionPerc = dftRetentionPerc;
	}

	public Date getRetentionTillDate() {
		return retentionTillDate;
	}
	public void setRetentionTillDate(Date retentionTillDate) {
		this.retentionTillDate = retentionTillDate;
	}
}
