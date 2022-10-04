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
 * * FileName : FinanceApplicationCode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceApplicationCode table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BankDetail extends AbstractWorkflowEntity implements java.io.Serializable {
	private static final long serialVersionUID = -6305409759684865400L;

	private String bankCode;
	@XmlElement
	private String ifsc;
	@XmlElement
	private String bankName;
	@XmlElement
	private String bankBranch;
	private String bankShortCode;
	private boolean active;
	private int accNoLength;
	private int minAccNoLength;
	private String lovValue;
	private BankDetail befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private WSReturnStatus returnStatus = null;
	private boolean allowMultipleIFSC;
	private boolean cheque;
	private boolean dd;
	private boolean ecs;
	private boolean nach;
	private boolean dda;
	private boolean emandate;
	private String allowedSources;
	private boolean updateBranches;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("bankBranch");
		excludeFields.add("ifsc");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	public BankDetail() {
		super();
	}

	public BankDetail(String id) {
		super();
		this.setId(id);
	}

	public String getId() {
		return bankCode;
	}

	public void setId(String id) {
		this.bankCode = id;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BankDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BankDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getAccNoLength() {
		return accNoLength;
	}

	public void setAccNoLength(int accNoLength) {
		this.accNoLength = accNoLength;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getBankShortCode() {
		return bankShortCode;
	}

	public void setBankShortCode(String bankShortCode) {
		this.bankShortCode = bankShortCode;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public int getMinAccNoLength() {
		return minAccNoLength;
	}

	public void setMinAccNoLength(int minAccNoLength) {
		this.minAccNoLength = minAccNoLength;
	}

	public boolean isAllowMultipleIFSC() {
		return allowMultipleIFSC;
	}

	public void setAllowMultipleIFSC(boolean allowMultipleIFSC) {
		this.allowMultipleIFSC = allowMultipleIFSC;
	}

	public boolean isCheque() {
		return cheque;
	}

	public boolean isDd() {
		return dd;
	}

	public boolean isEcs() {
		return ecs;
	}

	public boolean isNach() {
		return nach;
	}

	public boolean isDda() {
		return dda;
	}

	public boolean isEmandate() {
		return emandate;
	}

	public String getAllowedSources() {
		return allowedSources;
	}

	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}

	public void setDd(boolean dd) {
		this.dd = dd;
	}

	public void setEcs(boolean ecs) {
		this.ecs = ecs;
	}

	public void setNach(boolean nach) {
		this.nach = nach;
	}

	public void setDda(boolean dda) {
		this.dda = dda;
	}

	public void setEmandate(boolean emandate) {
		this.emandate = emandate;
	}

	public void setAllowedSources(String allowedSources) {
		this.allowedSources = allowedSources;
	}

	public boolean isUpdateBranches() {
		return updateBranches;
	}

	public void setUpdateBranches(boolean updateBranches) {
		this.updateBranches = updateBranches;
	}

}
