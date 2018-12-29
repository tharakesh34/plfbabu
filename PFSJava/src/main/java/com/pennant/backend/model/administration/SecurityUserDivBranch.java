/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SecurityUserDivBranch.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.administration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SecurityUserDivBranch table</b>.<br>
 *
 */
public class SecurityUserDivBranch extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -306657295035931426L;

	private long usrID = Long.MIN_VALUE;
	private String userDivision;
	private String userBranch;
	private String userBranchDesc;
	private boolean newRecord = false;
	private String lovValue;
	private String lovDescPriKey;
	private SecurityUserDivBranch befImage;
	private String branchSwiftBrnCde;
	private String branchProvince;
	private LoggedInUser userDetails;
	

	//for SecurityUserAccess Table
	private String entity;
	private String entityDesc;
	private String accessType;
	private Long clusterId;
	private String clusterCode;
	private String clusterName;
	private String clusterType;
	private Long parentCluster;
	private String parentClusterCode;
	private String parentClusterName;
	private String parentClusterType;
	private String divisionDesc;
	private String branchDesc;

	private transient Map<String, Object> entities = new HashMap<>();
	private transient Map<String, Object> clusters = new HashMap<>();
	private transient Map<String, Object> branches = new HashMap<>();

	public SecurityUserDivBranch() {
		super();
	}

	public boolean isNew() {
		return getUsrID() == Long.MIN_VALUE;
	}

	public SecurityUserDivBranch(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("userBranchDesc");
		excludeFields.add("branchSwiftBrnCde");
		excludeFields.add("branchProvince");
		excludeFields.add("entity");
		excludeFields.add("entityDesc");
		excludeFields.add("accessType");
		excludeFields.add("cluster");
		excludeFields.add("clusterCode");
		excludeFields.add("clusterName");
		excludeFields.add("clusterType");
		excludeFields.add("parentCluster");
		excludeFields.add("parentClusterCode");
		excludeFields.add("parentClusterName");
		excludeFields.add("parentClusterType");
		excludeFields.add("divisionDesc");
		excludeFields.add("branchDesc");

		excludeFields.add("entities");
		excludeFields.add("clusters");
		excludeFields.add("branches");
		return excludeFields;
	}

	public void setId(long id) {
		this.usrID = id;
	}

	public long getId() {
		return usrID;
	}

	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}

	public long getUsrID() {
		return usrID;
	}

	public String getUserDivision() {
		return userDivision;
	}

	public void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public void setUserBranchDesc(String userBranchDesc) {
		this.userBranchDesc = userBranchDesc;
	}

	public String getUserBranchDesc() {
		return userBranchDesc;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public SecurityUserDivBranch getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SecurityUserDivBranch beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescPriKey() {
		return lovDescPriKey;
	}

	public void setLovDescPriKey(String lovDescPriKey) {
		this.lovDescPriKey = lovDescPriKey;
	}

	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}

	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}

	public String getBranchProvince() {
		return branchProvince;
	}

	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long cluster) {
		this.clusterId = cluster;
	}

	public String getClusterCode() {
		return clusterCode;
	}

	public void setClusterCode(String clusterCode) {
		this.clusterCode = clusterCode;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
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

	public String getParentClusterCode() {
		return parentClusterCode;
	}

	public void setParentClusterCode(String parentClusterCode) {
		this.parentClusterCode = parentClusterCode;
	}

	public String getParentClusterName() {
		return parentClusterName;
	}

	public void setParentClusterName(String parentClusterName) {
		this.parentClusterName = parentClusterName;
	}

	public String getParentClusterType() {
		return parentClusterType;
	}

	public void setParentClusterType(String parentClusterType) {
		this.parentClusterType = parentClusterType;
	}

	public String getDivisionDesc() {
		return divisionDesc;
	}

	public void setDivisionDesc(String divisionDesc) {
		this.divisionDesc = divisionDesc;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public Map<String, Object> getEntities() {
		return entities;
	}

	public void setEntities(Map<String, Object> entities) {
		this.entities = entities;
	}

	public Map<String, Object> getClusters() {
		return clusters;
	}

	public void setClusters(Map<String, Object> clusters) {
		this.clusters = clusters;
	}

	public Map<String, Object> getBranches() {
		return branches;
	}

	public void setBranches(Map<String, Object> branches) {
		this.branches = branches;
	}
	
}
