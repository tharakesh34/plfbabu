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
 * * FileName : PRelationCode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified Date :
 * 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.systemmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PRelationCode table</b>.<br>
 * 
 */
public class PRelationCode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -6112299060860374205L;

	private String pRelationCode;
	private String pRelationDesc;
	private boolean relationCodeIsActive;
	private String lovValue;
	private PRelationCode befImage;
	private LoggedInUser userDetails;

	public PRelationCode() {
		super();
	}

	public PRelationCode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return pRelationCode;
	}

	public void setId(String id) {
		this.pRelationCode = id;
	}

	public String getPRelationCode() {
		return pRelationCode;
	}

	public void setPRelationCode(String pRelationCode) {
		this.pRelationCode = pRelationCode;
	}

	public String getPRelationDesc() {
		return pRelationDesc;
	}

	public void setPRelationDesc(String pRelationDesc) {
		this.pRelationDesc = pRelationDesc;
	}

	public boolean isRelationCodeIsActive() {
		return relationCodeIsActive;
	}

	public void setRelationCodeIsActive(boolean relationCodeIsActive) {
		this.relationCodeIsActive = relationCodeIsActive;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public PRelationCode getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PRelationCode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
