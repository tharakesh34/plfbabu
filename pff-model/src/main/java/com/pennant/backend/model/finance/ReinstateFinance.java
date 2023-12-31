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
 * * FileName : ReinstateFinance.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ReinstateFinance table</b>.<br>
 *
 */
public class ReinstateFinance extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -1472467289111692722L;

	private long finID;
	private String finReference;
	private String custCIF;
	private String custShrtName;
	private String finType;
	private String lovDescFinTypeName;
	private String finBranch;
	private String lovDescFinBranchName;
	private String finCcy;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private BigDecimal totalProfit = BigDecimal.ZERO;
	private Date finStartDate;
	private Date maturityDate;
	private String rejectedBy;
	private Date rejectedOn;
	private String rejectStatus;
	private String rejectRemarks;
	private boolean allowGrcPeriod;
	private Date grcPeriodEndDate;
	private String scheduleMethod;
	private String product = "";

	private long lovDescWorkflowId;
	private String lovDescRoleCode;
	private String lovDescNextRoleCode;
	private String lovDescTaskId;
	private String lovDescNextTaskId;

	private String profitDaysBasis;
	private String finCategory;
	private String finPreApprovedRef;
	private int numberOfTerms = 0;
	private int graceTerms = 0;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	private boolean scheduleChange;
	private String lovValue;
	private ReinstateFinance befImage;
	private LoggedInUser userDetails;
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private ExtendedFieldRender extendedFieldRender = null;
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private String finEvent = null;

	public ReinstateFinance() {
		super();
	}

	public ReinstateFinance(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("finType");
		excludeFields.add("lovDescFinTypeName");
		excludeFields.add("finBranch");
		excludeFields.add("lovDescFinBranchName");
		excludeFields.add("finCcy");
		excludeFields.add("lovDescFinCcyName");
		excludeFields.add("lovDescFinFormatter");
		excludeFields.add("finAmount");
		excludeFields.add("downPayment");
		excludeFields.add("totalProfit");
		excludeFields.add("finStartDate");
		excludeFields.add("maturityDate");
		excludeFields.add("rejectedBy");
		excludeFields.add("rejectedOn");
		excludeFields.add("rejectStatus");
		excludeFields.add("rejectRemarks");
		excludeFields.add("feeChargeAmt");
		excludeFields.add("finRepaymentAmount");
		excludeFields.add("formatter");
		excludeFields.add("numberOfTerms");
		excludeFields.add("graceTerms");
		excludeFields.add("profitDaysBasis");
		excludeFields.add("finCategory");
		excludeFields.add("finPreApprovedRef");
		excludeFields.add("scheduleChange");
		excludeFields.add("allowGrcPeriod");
		excludeFields.add("grcPeriodEndDate");
		excludeFields.add("scheduleMethod");
		excludeFields.add("product");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("finServiceInstructions");
		excludeFields.add("auditDetailMap");
		excludeFields.add("finEvent");

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

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
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

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getLovDescFinBranchName() {
		return lovDescFinBranchName;
	}

	public void setLovDescFinBranchName(String lovDescFinBranchName) {
		this.lovDescFinBranchName = lovDescFinBranchName;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public Date getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(Date rejectedOn) {
		this.rejectedOn = rejectedOn;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getRejectRemarks() {
		return rejectRemarks;
	}

	public void setRejectRemarks(String rejectRemarks) {
		this.rejectRemarks = rejectRemarks;
	}

	public long getLovDescWorkflowId() {
		return lovDescWorkflowId;
	}

	public void setLovDescWorkflowId(long lovDescWorkflowId) {
		this.lovDescWorkflowId = lovDescWorkflowId;
	}

	public String getLovDescRoleCode() {
		return lovDescRoleCode;
	}

	public void setLovDescRoleCode(String lovDescRoleCode) {
		this.lovDescRoleCode = lovDescRoleCode;
	}

	public String getLovDescNextRoleCode() {
		return lovDescNextRoleCode;
	}

	public void setLovDescNextRoleCode(String lovDescNextRoleCode) {
		this.lovDescNextRoleCode = lovDescNextRoleCode;
	}

	public String getLovDescTaskId() {
		return lovDescTaskId;
	}

	public void setLovDescTaskId(String lovDescTaskId) {
		this.lovDescTaskId = lovDescTaskId;
	}

	public String getLovDescNextTaskId() {
		return lovDescNextTaskId;
	}

	public void setLovDescNextTaskId(String lovDescNextTaskId) {
		this.lovDescNextTaskId = lovDescNextTaskId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ReinstateFinance getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ReinstateFinance beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getFinPreApprovedRef() {
		return finPreApprovedRef;
	}

	public void setFinPreApprovedRef(String finPreApprovedRef) {
		this.finPreApprovedRef = finPreApprovedRef;
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

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public boolean isScheduleChange() {
		return scheduleChange;
	}

	public void setScheduleChange(boolean scheduleChange) {
		this.scheduleChange = scheduleChange;
	}

	public boolean isAllowGrcPeriod() {
		return allowGrcPeriod;
	}

	public void setAllowGrcPeriod(boolean allowGrcPeriod) {
		this.allowGrcPeriod = allowGrcPeriod;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public FinServiceInstruction getFinServiceInstruction() {
		FinServiceInstruction finServiceInstruction = null;
		if (finServiceInstructions != null && !finServiceInstructions.isEmpty()) {
			finServiceInstruction = finServiceInstructions.get(0);
		}
		return finServiceInstruction;
	}

	public void setFinServiceInstruction(FinServiceInstruction finServiceInstruction) {

		if (finServiceInstruction != null) {
			if (finServiceInstructions == null) {
				finServiceInstructions = new ArrayList<FinServiceInstruction>();
			}
			this.finServiceInstructions.add(finServiceInstruction);
		}
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}
}
