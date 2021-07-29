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
 * * FileName : TownCode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-03-2020 * * Modified Date : * *
 * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-03-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class TownCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long townCode;
	private String townName;
	private long stateCode;
	private String stateName;
	private long distCode;
	private String districtName;
	private boolean active;

	public TownCode() {
		super();
	}

	public TownCode(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return townCode;
	}

	public void setId(long id) {
		this.townCode = id;
	}

	public long getTownCode() {
		return townCode;
	}

	public void setTownCode(long townCode) {
		this.townCode = townCode;
	}

	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	public long getStateCode() {
		return stateCode;
	}

	public void setStateCode(long stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public long getDistCode() {
		return distCode;
	}

	public void setDistCode(long distCode) {
		this.distCode = distCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
