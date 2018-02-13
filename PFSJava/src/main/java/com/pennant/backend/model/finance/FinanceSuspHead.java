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
 * FileName    		:  FinanceSuspHead.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-02-2012    														*
 *                                                                  						*
 * Modified Date    :  04-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;


/**
 * Model class for the <b>FinSuspHead table</b>.<br>
 *
 */
public class FinanceSuspHead extends AbstractWorkflowEntity {

    private static final long serialVersionUID = -7731584953589841445L;
    
	private String finReference;
	private String finBranch;
	private String finType;
	private long custId = Long.MIN_VALUE;
	private int finSuspSeq;
	private boolean finIsInSusp=false;
	private boolean manualSusp=false;
	private Date finSuspDate;
	private Date finSuspTrfDate;
	private BigDecimal finSuspAmt = BigDecimal.ZERO;
	private BigDecimal finCurSuspAmt = BigDecimal.ZERO;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceSuspHead befImage;
	private LoggedInUser userDetails;
	
	private String finCcy;
	private String lovDescCustCIFName;
	private String lovDescCustShrtName;
	private String lovDescFinDivision;
	
	private List<FinanceSuspDetails> suspDetailsList = new ArrayList<FinanceSuspDetails>();
	private List<ReturnDataSet> suspPostingsList = new ArrayList<ReturnDataSet>();
	private FinanceDetail financeDetail;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceSuspHead() {
		super();
	}

	public FinanceSuspHead(String id) {
		super();
		this.setId(id);
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("financeDetail");
		return excludeFields;
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
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	
	public int getFinSuspSeq() {
		return finSuspSeq;
	}
	public void setFinSuspSeq(int finSuspSeq) {
		this.finSuspSeq = finSuspSeq;
	}
	
	public boolean isFinIsInSusp() {
		return finIsInSusp;
	}
	public void setFinIsInSusp(boolean finIsInSusp) {
		this.finIsInSusp = finIsInSusp;
	}
	
	public boolean isManualSusp() {
    	return manualSusp;
    }
	public void setManualSusp(boolean manualSusp) {
    	this.manualSusp = manualSusp;
    }
	
	public Date getFinSuspDate() {
		return finSuspDate;
	}
	public void setFinSuspDate(Date finSuspDate) {
		this.finSuspDate = finSuspDate;
	}
	
	public Date getFinSuspTrfDate() {
	    return finSuspTrfDate;
    }
	public void setFinSuspTrfDate(Date finSuspTrfDate) {
	    this.finSuspTrfDate = finSuspTrfDate;
    }
	
	public BigDecimal getFinSuspAmt() {
		return finSuspAmt;
	}
	public void setFinSuspAmt(BigDecimal finSuspAmt) {
		this.finSuspAmt = finSuspAmt;
	}
	
	public BigDecimal getFinCurSuspAmt() {
		return finCurSuspAmt;
	}
	public void setFinCurSuspAmt(BigDecimal finCurSuspAmt) {
		this.finCurSuspAmt = finCurSuspAmt;
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

	public FinanceSuspHead getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceSuspHead beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public void setLovDescCustCIFName(String lovDescCustCIFName) {
	    this.lovDescCustCIFName = lovDescCustCIFName;
    }
	public String getLovDescCustCIFName() {
	    return lovDescCustCIFName;
    }
	public void setSuspDetailsList(List<FinanceSuspDetails> suspDetailsList) {
	    this.suspDetailsList = suspDetailsList;
    }
	public List<FinanceSuspDetails> getSuspDetailsList() {
	    return suspDetailsList;
    }
	
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
	    this.lovDescCustShrtName = lovDescCustShrtName;
    }
	public String getLovDescCustShrtName() {
	    return lovDescCustShrtName;
    }
	
	public void setSuspPostingsList(List<ReturnDataSet> suspPostingsList) {
	    this.suspPostingsList = suspPostingsList;
    }
	public List<ReturnDataSet> getSuspPostingsList() {
	    return suspPostingsList;
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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

}
