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
 * * FileName : SecurityUserDivBranch.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.administration;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SecurityUserDivBranch table</b>.<br>
 *
 */
public class SecurityUserAccess extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -306657295035931426L;

	private long usrId = Long.MIN_VALUE;
	private String division;
	private String branch;
	private String accessType;
	private String entity;
	private Long clusterId;
	private String clusterType;
	private Long parentCluster;
	private String parentClusterType;

	public long getUsrId() {
		return usrId;
	}

	public void setUsrId(long usrId) {
		this.usrId = usrId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	public Long getParentCluster() {
		return parentCluster;
	}

	public void setParentCluster(Long parentCluster) {
		this.parentCluster = parentCluster;
	}

	public String getParentClusterType() {
		return parentClusterType;
	}

	public void setParentClusterType(String parentClusterType) {
		this.parentClusterType = parentClusterType;
	}

}
