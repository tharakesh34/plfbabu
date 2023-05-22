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
 * * FileName : InterfaceMapping.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * * Modified
 * Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.interfacemapping;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>InterfaceMapping table</b>.<br>
 *
 */
public class MasterMapping extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long masterMappingId = Long.MIN_VALUE;
	private long interfaceMappingId = 0;
	private String plfValue;
	private String interfaceValue;
	private String interfaceSequence;
	private String lovValue;
	private MasterMapping befImage;
	private LoggedInUser userDetails;

	public MasterMapping() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getMasterMappingId() {
		return masterMappingId;
	}

	public void setMasterMappingId(long masterMappingId) {
		this.masterMappingId = masterMappingId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public MasterMapping getBefImage() {
		return this.befImage;
	}

	public void setBefImage(MasterMapping beforeImage) {
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

	public long getId() {
		return masterMappingId;
	}

	public void setId(long id) {
		this.masterMappingId = id;

	}

	public String getPlfValue() {
		return plfValue;
	}

	public void setPlfValue(String plfValue) {
		this.plfValue = plfValue;
	}

	public String getInterfaceValue() {
		return interfaceValue;
	}

	public void setInterfaceValue(String interfaceValue) {
		this.interfaceValue = interfaceValue;
	}

	public Long getInterfaceMappingId() {
		return interfaceMappingId;
	}

	public void setInterfaceMappingId(Long interfaceMappingId) {
		this.interfaceMappingId = interfaceMappingId;
	}

	public String getInterfaceSequence() {
		return interfaceSequence;
	}

	public void setInterfaceSequence(String interfaceSequence) {
		this.interfaceSequence = interfaceSequence;
	}

}
