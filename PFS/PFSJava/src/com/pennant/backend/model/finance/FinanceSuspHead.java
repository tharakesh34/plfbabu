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
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;


/**
 * Model class for the <b>FinSuspHead table</b>.<br>
 *
 */
public class FinanceSuspHead {

	private String finReference;
	private String finBranch;
	private String finType;
	private long custId = Long.MIN_VALUE;
	private int finSuspSeq;
	private boolean finIsInSusp=false;
	private boolean manualSusp=false;
	private Date finSuspDate;
	private BigDecimal finSuspAmt = new BigDecimal(0);
	private BigDecimal finCurSuspAmt = new BigDecimal(0);
	
	private String lovDescCustCIFName;
	private int lovDescFinFormatter;
	private String lovDescCustShrtName;
	private List<FinanceSuspDetails> suspDetailsList = new ArrayList<FinanceSuspDetails>();
	private List<ReturnDataSet> suspPostingsList = new ArrayList<ReturnDataSet>();
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		
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
	
	public void setLovDescCustCIFName(String lovDescCustCIFName) {
	    this.lovDescCustCIFName = lovDescCustCIFName;
    }
	public String getLovDescCustCIFName() {
	    return lovDescCustCIFName;
    }

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
	    this.lovDescFinFormatter = lovDescFinFormatter;
    }
	public int getLovDescFinFormatter() {
	    return lovDescFinFormatter;
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
	
	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinanceSuspHead) {
			FinanceSuspHead financeSuspHead = (FinanceSuspHead) obj;
			return equals(financeSuspHead);
		}
		return false;
	}
	
}
