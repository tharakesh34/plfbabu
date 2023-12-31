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
 * * FileName : SICCodes * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-08-2011 * * Modified Date :
 * 12-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.bmtmasters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>RiskTypeDetails table</b>.<br>
 * 
 */
public class SICCodes extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3936360447748889441L;

	private String sicCode = null;
	private String sicDesc;
	private String lovValue;
	private SICCodes befImage;
	private LoggedInUser userDetails;

	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public SICCodes() {
		super();
	}

	public SICCodes(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return sicCode;
	}

	public void setId(String id) {
		this.sicCode = id;
	}

	public String getSicCode() {
		return sicCode;
	}

	public void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public String getSicDesc() {
		return sicDesc;
	}

	public void setSicDesc(String sicDesc) {
		this.sicDesc = sicDesc;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public SICCodes getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SICCodes beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
}
