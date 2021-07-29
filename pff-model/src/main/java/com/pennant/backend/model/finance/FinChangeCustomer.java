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
 * * FileName : HoldDisbursement.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-11-2019 * * Modified
 * Date : 20-11-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-11-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>HoldDisbursement table</b>.<br>
 *
 */
public class FinChangeCustomer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private long id = Long.MIN_VALUE;
	private String finReference = "";
	private long oldCustId;
	private long coApplicantId;
	private String custCategory;
	private String finType;
	private String custCif;
	private String jcustCif;
	private boolean collateralDelinkStatus = true;
	private FinChangeCustomer befImage;
	private LoggedInUser userDetails;
	protected JointAccountDetail jointAccountDetail;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<CollateralSetup> collateralSetups = new ArrayList<CollateralSetup>();

	public FinChangeCustomer() {
		super();
	}

	public FinChangeCustomer(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCategory");
		excludeFields.add("finType");
		excludeFields.add("jointAccountDetail");
		excludeFields.add("collateralSetups");
		excludeFields.add("custCif");
		excludeFields.add("auditDetailMap");
		excludeFields.add("jcustCif");
		excludeFields.add("collateralDelinkStatus");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public FinChangeCustomer getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinChangeCustomer beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getOldCustId() {
		return oldCustId;
	}

	public void setOldCustId(long oldCustId) {
		this.oldCustId = oldCustId;
	}

	public long getCoApplicantId() {
		return coApplicantId;
	}

	public void setCoApplicantId(long coApplicantId) {
		this.coApplicantId = coApplicantId;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public JointAccountDetail getJointAccountDetail() {
		return jointAccountDetail;
	}

	public void setJointAccountDetail(JointAccountDetail jointAccountDetail) {
		this.jointAccountDetail = jointAccountDetail;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<CollateralSetup> getCollateralSetups() {
		return collateralSetups;
	}

	public void setCollateralSetups(List<CollateralSetup> collateralSetups) {
		this.collateralSetups = collateralSetups;
	}

	public String getJcustCif() {
		return jcustCif;
	}

	public void setJcustCif(String jcustCif) {
		this.jcustCif = jcustCif;
	}

	public boolean isCollateralDelinkStatus() {
		return collateralDelinkStatus;
	}

	public void setCollateralDelinkStatus(boolean collateralDelinkStatus) {
		this.collateralDelinkStatus = collateralDelinkStatus;
	}

}
