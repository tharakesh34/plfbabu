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
 * * FileName : LimitHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified Date :
 * 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.limit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Model class for the <b>LimitHeader table</b>.<br>
 *
 */
@XmlType(propOrder = { "headerId", "custCIF", "custGrpCode", "customerName", "responsibleBranch",
		"responsibleBranchName", "limitCcy", "ccyDesc", "limitExpiryDate", "limitRvwDate", "limitSetupRemarks",
		"limitStructureCode", "structureName", "active", "customerLimitDetailsList", "institutionlimitDetail",
		"returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "limitSetup")
public class LimitHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "limitId")
	private long headerId = Long.MIN_VALUE;
	@XmlElement
	private String ruleCode;
	private String ruleValue;

	private long customerGroup = 0;
	private long customerId = 0;

	@XmlElement
	private String responsibleBranch;

	@XmlElement
	private String responsibleBranchName;

	@XmlElement(name = "ccy")
	private String limitCcy;

	@XmlElement(name = "expiryDate")
	private Date limitExpiryDate;

	@XmlElement(name = "reviewDate")
	private Date limitRvwDate;

	@XmlElement
	private boolean active;
	private boolean rebuild = true;
	@XmlElement
	private boolean validateMaturityDate;

	private String status = "";

	private String showLimitsIn;

	@XmlElement(name = "structureCode")
	private String limitStructureCode;

	@XmlElement(name = "remarks")
	private String limitSetupRemarks;

	private String custFName;
	private String custMName;
	private String custFullName;
	private String custShrtName;

	private String groupName;
	private String custCoreBank;
	private String custDftBranch;
	private String custDftBranchName;
	private String custSalutationCode;

	@XmlElement
	private String structureName;

	@XmlElement(name = "cif")
	private String custCIF;

	@XmlElement(name = "customerGroup")
	private String custGrpCode;

	@XmlElement
	private String ccyDesc;
	private String QueryDesc;
	private String errDesc = "";

	private long createdBy;
	private String createdUser;
	private Timestamp createdOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar createdDate;
	private String lovValue;
	private LimitHeader befImage;
	private LoggedInUser userDetails;

	@XmlElement(name = "limitDetail")
	private List<LimitDetails> customerLimitDetailsList = null;
	@XmlElement(name = "institutionlimitDetail")
	private List<LimitDetails> institutionLimitDetailsList = null;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String customerName;

	private boolean blocklimit;

	// API validation purpose only
	@SuppressWarnings("unused")
	private LimitHeader validateLimitHeader = this;

	/* Processing variables */
	private boolean validateOnly;
	private String tranType;
	private boolean override;
	private boolean allowOverride;
	private int disbSeq;
	private BigDecimal tranAmt = BigDecimal.ZERO;
	private BigDecimal reserveTranAmt = BigDecimal.ZERO;
	private BigDecimal prevLimitAmt = BigDecimal.ZERO;
	private BigDecimal limitAmount = BigDecimal.ZERO;
	private BigDecimal reserveLimitAmt = BigDecimal.ZERO;
	private BigDecimal blockAmount = BigDecimal.ZERO;
	private Date valueDate;
	private Date loanMaturityDate;

	public LimitHeader() {
		super();
	}

	public LimitHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("createdUser");
		excludeFields.add("createdDate");
		excludeFields.add("custFullName");
		excludeFields.add("custShrtName");
		excludeFields.add("groupName");
		excludeFields.add("customerLimitDetailsList");
		excludeFields.add("structureName");
		excludeFields.add("custCoreBank");
		excludeFields.add("custDftBranch");
		excludeFields.add("custDftBranchName");
		excludeFields.add("custSalutationCode");
		excludeFields.add("responsibleBranchName");
		excludeFields.add("custCIF");
		excludeFields.add("custGrpCode");
		excludeFields.add("ccyDesc");
		excludeFields.add("ccyEditField");
		excludeFields.add("QueryDesc");
		excludeFields.add("errDesc");
		excludeFields.add("status");
		excludeFields.add("showLimitsIn");
		excludeFields.add("customerName");
		excludeFields.add("returnStatus");
		excludeFields.add("referenceCode");
		excludeFields.add("referenceNumber");
		excludeFields.add("amount");
		excludeFields.add("validateLimitHeader");
		excludeFields.add("custFName");
		excludeFields.add("custMName");

		excludeFields.add("validateOnly");
		excludeFields.add("tranType");
		excludeFields.add("override");
		excludeFields.add("allowOverride");
		excludeFields.add("disbSeq");
		excludeFields.add("tranAmt");
		excludeFields.add("reserveTranAmt");
		excludeFields.add("prevLimitAmt");
		excludeFields.add("limitAmount");
		excludeFields.add("reserveLimitAmt");
		excludeFields.add("blockAmount");
		excludeFields.add("valueDate");
		excludeFields.add("loanMaturityDate");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public long getId() {
		return headerId;
	}

	public void setId(long id) {
		this.headerId = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public long getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(long customerGroup) {
		this.customerGroup = customerGroup;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getResponsibleBranch() {
		return responsibleBranch;
	}

	public void setResponsibleBranch(String responsibleBranch) {
		this.responsibleBranch = responsibleBranch;
	}

	public String getLimitStructureCode() {
		return limitStructureCode;
	}

	public void setLimitStructureCode(String limitStructureCode) {
		this.limitStructureCode = limitStructureCode;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public void setCreatedDate(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			createdOn = DateUtil.ConvertFromXMLTime(xmlCalendar);
			createdDate = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getCreatedDate() throws DatatypeConfigurationException {

		if (createdOn == null) {
			return null;
		}
		return DateUtil.getXMLDate(createdOn);
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LimitHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitHeader beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public List<LimitDetails> getCustomerLimitDetailsList() {
		return customerLimitDetailsList;
	}

	public void setCustomerLimitDetailsList(List<LimitDetails> customerLimitDetailsList) {
		this.customerLimitDetailsList = customerLimitDetailsList;
	}

	public List<LimitDetails> getInstitutionLimitDetailsList() {
		return institutionLimitDetailsList;
	}

	public void setInstitutionLimitDetailsList(List<LimitDetails> institutionLimitDetailsList) {
		this.institutionLimitDetailsList = institutionLimitDetailsList;
	}

	public String getStructureName() {
		return structureName;
	}

	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCustDftBranchName() {
		return custDftBranchName;
	}

	public void setCustDftBranchName(String custDftBranchName) {
		this.custDftBranchName = custDftBranchName;
	}

	public String getResponsibleBranchName() {
		return responsibleBranchName;
	}

	public void setResponsibleBranchName(String responsibleBranchName) {
		this.responsibleBranchName = responsibleBranchName;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustGrpCode() {
		return custGrpCode;
	}

	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}

	public String getCcyDesc() {
		return ccyDesc;
	}

	public void setCcyDesc(String ccyDesc) {
		this.ccyDesc = ccyDesc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getQueryDesc() {
		return QueryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		QueryDesc = queryDesc;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLimitRvwDate() {
		return limitRvwDate;
	}

	public void setLimitRvwDate(Date limitRvwDat) {
		limitRvwDate = limitRvwDat;
	}

	public Date getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(Date limitExpiryDat) {
		limitExpiryDate = limitExpiryDat;
	}

	public String getLimitSetupRemarks() {
		return limitSetupRemarks;
	}

	public void setLimitSetupRemarks(String limitSetupRemark) {
		limitSetupRemarks = limitSetupRemark;
	}

	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

	public boolean isRebuild() {
		return rebuild;
	}

	public void setRebuild(boolean rebuild) {
		this.rebuild = rebuild;
	}

	public String getShowLimitsIn() {
		return showLimitsIn;
	}

	public void setShowLimitsIn(String showLimitsIn) {
		this.showLimitsIn = showLimitsIn;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustMName() {
		return custMName;
	}

	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}

	public boolean isValidateMaturityDate() {
		return validateMaturityDate;
	}

	public void setValidateMaturityDate(boolean validateMaturityDate) {
		this.validateMaturityDate = validateMaturityDate;
	}

	public boolean isBlocklimit() {
		return blocklimit;
	}

	public void setBlocklimit(boolean blocklimit) {
		this.blocklimit = blocklimit;
	}

	public boolean isValidateOnly() {
		return validateOnly;
	}

	public void setValidateOnly(boolean validateOnly) {
		this.validateOnly = validateOnly;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public boolean isAllowOverride() {
		return allowOverride;
	}

	public void setAllowOverride(boolean allowOverride) {
		this.allowOverride = allowOverride;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public int getDisbSeq() {
		return disbSeq;
	}

	public void setDisbSeq(int disbSeq) {
		this.disbSeq = disbSeq;
	}

	public BigDecimal getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(BigDecimal tranAmt) {
		this.tranAmt = tranAmt;
	}

	public BigDecimal getReserveTranAmt() {
		return reserveTranAmt;
	}

	public void setReserveTranAmt(BigDecimal reserveTranAmt) {
		this.reserveTranAmt = reserveTranAmt;
	}

	public BigDecimal getPrevLimitAmt() {
		return prevLimitAmt;
	}

	public void setPrevLimitAmt(BigDecimal prevLimitAmt) {
		this.prevLimitAmt = prevLimitAmt;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public BigDecimal getReserveLimitAmt() {
		return reserveLimitAmt;
	}

	public void setReserveLimitAmt(BigDecimal reserveLimitAmt) {
		this.reserveLimitAmt = reserveLimitAmt;
	}

	public BigDecimal getBlockAmount() {
		return blockAmount;
	}

	public void setBlockAmount(BigDecimal blockAmount) {
		this.blockAmount = blockAmount;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getLoanMaturityDate() {
		return loanMaturityDate;
	}

	public void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

}
