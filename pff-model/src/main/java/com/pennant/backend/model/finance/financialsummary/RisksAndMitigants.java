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
 * * FileName : CustomerDocument.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance.financialsummary;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custDocCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName", "custDocIssuedOn",
		"custDocExpDate", "docPurpose", "custDocName", "custDocType", "custDocImage", "docUri" })
@XmlAccessorType(XmlAccessType.NONE)
public class RisksAndMitigants extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6420966711989511378L;

	private long id = Long.MIN_VALUE;
	private long seqNo;
	private String risk;
	private String mitigants;
	private RisksAndMitigants befImage;
	private LoggedInUser userDetails;
	private String finReference;

	public RisksAndMitigants() {
		super();
	}

	public RisksAndMitigants(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRisk() {
		return risk;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public String getMitigants() {
		return mitigants;
	}

	public void setMitigants(String mitigants) {
		this.mitigants = mitigants;
	}

	public RisksAndMitigants getBefImage() {
		return befImage;
	}

	public void setBefImage(RisksAndMitigants befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

}
