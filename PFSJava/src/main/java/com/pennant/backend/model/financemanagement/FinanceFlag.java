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
 * * FileName : FinanceFlag.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinCollaterals table</b>.<br>
 * 
 */
@XmlType(propOrder ={
		"finReference", "finFlagDetailList","returnStatus"})
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceFlag extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private String 			finReference;
	
	private String 			finCategory;
	private String 			custCIF;
	private String 			finType;
	private String 			finTypeDesc;
	private String 			finCcy;
	private String 			finBranch;
	private String 			finBranchDesc;
	private int 			numberOfTerms = 0;
	private int 			graceTerms = 0;
	private Date 			finStartDate;
	private Date 			maturityDate;
	private BigDecimal 		finAmount = BigDecimal.ZERO;
	private BigDecimal 		downPayBank = BigDecimal.ZERO;
	private BigDecimal 		downPaySupl = BigDecimal.ZERO;
	private BigDecimal 		feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal 		finRepaymentAmount = BigDecimal.ZERO;
	private BigDecimal 		totalProfit = BigDecimal.ZERO;
	private BigDecimal 		effectiveRateOfReturn;

	private boolean 		newRecord;
	private FinanceFlag 	befImage;
	private LoggedInUser userDetails;

	private boolean  scheduleChange;
	@XmlElementWrapper(name = "flags")
	@XmlElement(name = "flag")
	private List<FinFlagsDetail> finFlagDetailList = new ArrayList<FinFlagsDetail>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	@XmlElement
	private WSReturnStatus returnStatus;

	public boolean isNew() {
		return isNewRecord();
	}
	public FinanceFlag() {
		super();
	}
	
	public FinanceFlag(String  finReference) {
		super();
		this.finReference = finReference;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finCategory");
		excludeFields.add("custCIF");
		excludeFields.add("finType");
		excludeFields.add("finTypeDesc");
		excludeFields.add("finCcy");
		excludeFields.add("finCcyDesc");
		excludeFields.add("finBranch");
		excludeFields.add("finBranchDesc");
		excludeFields.add("profitDaysBasis");
		excludeFields.add("numberOfTerms");
		excludeFields.add("graceTerms");
		excludeFields.add("finStartDate");
		excludeFields.add("maturityDate");
		excludeFields.add("finAmount");
		excludeFields.add("downPayBank");
		excludeFields.add("downPaySupl");
		excludeFields.add("feeChargeAmt");
		excludeFields.add("finRepaymentAmount");
		excludeFields.add("formatter");
		excludeFields.add("totalProfit");
		excludeFields.add("effectiveRateOfReturn");
		excludeFields.add("scheduleChange");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	// Getter and Setter methods

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

	public Date getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	
	public FinanceFlag getBefImage() {
		return befImage;
	}
	public void setBefImage(FinanceFlag befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}
	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}
	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}
	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public int getGraceTerms() {
		return graceTerms;
	}
	public void setGraceTerms(int graceTerms) {
		this.graceTerms = graceTerms;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}
	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getDownPaySupl() {
		return downPaySupl;
	}
	public void setDownPaySupl(BigDecimal downPaySupl) {
		this.downPaySupl = downPaySupl;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}
	
	public String getFinCcy() {
		return finCcy;
	}
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getEffectiveRateOfReturn() {
		return effectiveRateOfReturn;
	}
	public void setEffectiveRateOfReturn(BigDecimal effectiveRateOfReturn) {
		this.effectiveRateOfReturn = effectiveRateOfReturn;
	}

	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	
	public String getFinCategory() {
		return finCategory;
	}
	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}
	
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}
	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}
	
	public String getCustCIF() {
	    return custCIF;
    }
	public void setCustCIF(String custCIF) {
	    this.custCIF = custCIF;
    }
	public String getFinTypeDesc() {
	    return finTypeDesc;
    }
	public void setFinTypeDesc(String finTypeDesc) {
	    this.finTypeDesc = finTypeDesc;
    }
	public String getFinBranchDesc() {
	    return finBranchDesc;
    }
	public void setFinBranchDesc(String finBranchDesc) {
	    this.finBranchDesc = finBranchDesc;
    }
	public BigDecimal getDownPayBank() {
	    return downPayBank;
    }
	public void setDownPayBank(BigDecimal downPayBank) {
	    this.downPayBank = downPayBank;
    }
	public List<FinFlagsDetail> getFinFlagDetailList() {
	    return finFlagDetailList;
    }
	public void setFinFlagDetailList(List<FinFlagsDetail> finFlagDetailList) {
	    this.finFlagDetailList = finFlagDetailList;
    }
	public boolean isScheduleChange() {
		return scheduleChange;
	}
	public void setScheduleChange(boolean scheduleChange) {
		this.scheduleChange = scheduleChange;
	}
	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}	
}
