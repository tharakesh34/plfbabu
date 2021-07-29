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
 * * FileName : ClusterHierarchey.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * * Modified
 * Date : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ClusterHierarchey table</b>.<br>
 *
 */
public class ClusterHierarchy extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String entity;
	private String clusterType;
	private int seqOrder;
	private String lovValue;
	private ClusterHierarchy befImage;
	private LoggedInUser userDetails;
	private List<ClusterHierarchy> clusterTypes = new ArrayList<>();

	public ClusterHierarchy() {
		super();
	}

	public ClusterHierarchy(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("clusterTypes");
		return excludeFields;

	}

	public List<ClusterHierarchy> getClusterTypes() {
		return clusterTypes;
	}

	public void setClusterTypes(List<ClusterHierarchy> clusterTypes) {
		this.clusterTypes = clusterTypes;
	}

	public String getId() {
		return entity;
	}

	public void setId(String id) {
		this.entity = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	public int getSeqOrder() {
		return seqOrder;
	}

	public void setSeqOrder(int seqOrder) {
		this.seqOrder = seqOrder;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ClusterHierarchy getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ClusterHierarchy beforeImage) {
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
}
