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
 * * FileName : Accounts.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-01-2012 * * Modified Date :
 * 02-01-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-01-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.accounts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Accounts table</b>.<br>
 * 
 */
public class Accounts extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1673137129792916291L;

	private String accountId;
	private String acCcy;
	private String acType;
	private String acBranch;
	private long acCustId;
	private String acFullName;
	private String acShortName;
	private String acPurpose;
	private boolean internalAc;
	private boolean custSysAc;
	private String acNumber;

	private BigDecimal shadowBal = BigDecimal.ZERO;
	private BigDecimal acBalance = BigDecimal.ZERO;
	private Date acOpenDate;
	private Date acCloseDate;
	private Date acLastCustTrnDate;
	private Date acLastSysTrnDate;
	private boolean acActive;
	private boolean acBlocked;
	private boolean acClosed;

	private String hostAcNumber = "";
	private String lovValue;
	private Accounts befImage;
	private LoggedInUser userDetails;

	private int lovDescFinFormatter;
	private String lovDescCustCIF;
	private String lovDescAccTypeDesc;
	private String lovDescCurrency;
	private String lovDescBranchCodeName;
	private String lovDescAcHeadCode;
	private String lovDescCcyNumber;
	private String groupCode;

	public Accounts() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("acNumber");
		excludeFields.add("groupCode");
		return excludeFields;
	}

	public Accounts(String accountId) {
		super();
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAcCcy() {
		return acCcy;
	}

	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcBranch() {
		return acBranch;
	}

	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}

	public long getAcCustId() {
		return acCustId;
	}

	public void setAcCustId(long acCustId) {
		this.acCustId = acCustId;
	}

	public String getAcFullName() {
		return acFullName;
	}

	public void setAcFullName(String acFullName) {
		this.acFullName = acFullName;
	}

	public String getAcShortName() {
		return acShortName;
	}

	public void setAcShortName(String acShortName) {
		this.acShortName = acShortName;
	}

	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public boolean isCustSysAc() {
		return custSysAc;
	}

	public void setCustSysAc(boolean custSysAc) {
		this.custSysAc = custSysAc;
	}

	public boolean isAcActive() {
		return acActive;
	}

	public void setAcActive(boolean acActive) {
		this.acActive = acActive;
	}

	public boolean isAcBlocked() {
		return acBlocked;
	}

	public void setAcBlocked(boolean acBlocked) {
		this.acBlocked = acBlocked;
	}

	public boolean isAcClosed() {
		return acClosed;
	}

	public void setAcClosed(boolean acClosed) {
		this.acClosed = acClosed;
	}

	public String getHostAcNumber() {
		return hostAcNumber;
	}

	public void setHostAcNumber(String hostAcNumber) {
		this.hostAcNumber = hostAcNumber;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Accounts getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Accounts beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setAcPurpose(String acPurpose) {
		this.acPurpose = acPurpose;
	}

	public String getAcPurpose() {
		return acPurpose;
	}

	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}

	public void setLovDescAcHeadCode(String lovDescAcHeadCode) {
		this.lovDescAcHeadCode = lovDescAcHeadCode;
	}

	public String getLovDescAcHeadCode() {
		return lovDescAcHeadCode;
	}

	public void setLovDescCcyNumber(String lovDescCcyNumber) {
		this.lovDescCcyNumber = lovDescCcyNumber;
	}

	public String getLovDescCcyNumber() {
		return lovDescCcyNumber;
	}

	public Date getAcOpenDate() {
		return acOpenDate;
	}

	public void setAcOpenDate(Date acOpenDate) {
		this.acOpenDate = acOpenDate;
	}

	public Date getAcLastCustTrnDate() {
		return acLastCustTrnDate;
	}

	public void setAcLastCustTrnDate(Date acLastCustTrnDate) {
		this.acLastCustTrnDate = acLastCustTrnDate;
	}

	public Date getAcLastSysTrnDate() {
		return acLastSysTrnDate;
	}

	public void setAcLastSysTrnDate(Date acLastSysTrnDate) {
		this.acLastSysTrnDate = acLastSysTrnDate;
	}

	public void setAcCloseDate(Date acCloseDate) {
		this.acCloseDate = acCloseDate;
	}

	public Date getAcCloseDate() {
		return acCloseDate;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescAccTypeDesc() {
		return lovDescAccTypeDesc;
	}

	public void setLovDescAccTypeDesc(String lovDescAccTypeDesc) {
		this.lovDescAccTypeDesc = lovDescAccTypeDesc;
	}

	public String getLovDescCurrency() {
		return lovDescCurrency;
	}

	public void setLovDescCurrency(String lovDescCurrency) {
		this.lovDescCurrency = lovDescCurrency;
	}

	public String getLovDescBranchCodeName() {
		return lovDescBranchCodeName;
	}

	public void setLovDescBranchCodeName(String lovDescBranchCodeName) {
		this.lovDescBranchCodeName = lovDescBranchCodeName;
	}

	public BigDecimal getShadowBal() {
		return shadowBal;
	}

	public void setShadowBal(BigDecimal shadowBal) {
		this.shadowBal = shadowBal;
	}

	public BigDecimal getAcBalance() {
		return acBalance;
	}

	public void setAcBalance(BigDecimal acBalance) {
		this.acBalance = acBalance;
	}

	public String getAcNumber() {
		return acNumber;
	}

	public void setAcNumber(String acNumber) {
		this.acNumber = acNumber;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
}