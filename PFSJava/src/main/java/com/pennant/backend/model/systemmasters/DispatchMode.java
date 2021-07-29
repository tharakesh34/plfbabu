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
 * * FileName : DispatchMode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-08-2011 * * Modified Date :
 * 18-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.systemmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DispatchMode table</b>.<br>
 *
 */
public class DispatchMode extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 6902263645594946336L;

	private String dispatchModeCode;
	private String dispatchModeDesc;
	private boolean dispatchModeIsActive;
	private String lovValue;
	private DispatchMode befImage;
	private LoggedInUser userDetails;

	public DispatchMode() {
		super();
	}

	public DispatchMode(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return dispatchModeCode;
	}

	public void setId(String id) {
		this.dispatchModeCode = id;
	}

	public String getDispatchModeCode() {
		return dispatchModeCode;
	}

	public void setDispatchModeCode(String dispatchModeCode) {
		this.dispatchModeCode = dispatchModeCode;
	}

	public String getDispatchModeDesc() {
		return dispatchModeDesc;
	}

	public void setDispatchModeDesc(String dispatchModeDesc) {
		this.dispatchModeDesc = dispatchModeDesc;
	}

	public boolean isDispatchModeIsActive() {
		return dispatchModeIsActive;
	}

	public void setDispatchModeIsActive(boolean dispatchModeIsActive) {
		this.dispatchModeIsActive = dispatchModeIsActive;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public DispatchMode getBefImage() {
		return this.befImage;
	}

	public void setBefImage(DispatchMode beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
