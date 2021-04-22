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
 * FileName    		:  AssetClassificationHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-05-2020    														*
 *                                                                  						*
 * Modified Date    :  04-05-2020    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-05-2020       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AssetClassificationHeader table</b>.<br>
 *
 */
public class AssetClassificationHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String code;
	private String description;
	private int stageOrder;
	private boolean active = true;
	private boolean newRecord = false;
	private String lovValue;
	private AssetClassificationHeader befImage;
	private LoggedInUser userDetails;
	private AssetClassificationDetail classificationDetail;
	private Long npaTemplateId;
	private String npaTemplateCode;
	private String npaTemplateDesc;

	private List<AssetClassificationDetail> assetClassificationDetailList = new ArrayList<AssetClassificationDetail>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public AssetClassificationHeader() {
		super();
	}

	public AssetClassificationHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("classificationDetail");
		excludeFields.add("npaTemplateDesc");
		excludeFields.add("npaTemplateCode");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStageOrder() {
		return stageOrder;
	}

	public void setStageOrder(int stageOrder) {
		this.stageOrder = stageOrder;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public AssetClassificationHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AssetClassificationHeader beforeImage) {
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

	public List<AssetClassificationDetail> getAssetClassificationDetailList() {
		return assetClassificationDetailList;
	}

	public void setAssetClassificationDetailList(List<AssetClassificationDetail> assetClassificationDetailList) {
		this.assetClassificationDetailList = assetClassificationDetailList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public AssetClassificationDetail getClassificationDetail() {
		return classificationDetail;
	}

	public void setClassificationDetail(AssetClassificationDetail classificationDetail) {
		this.classificationDetail = classificationDetail;
	}

	public Long getNpaTemplateId() {
		return npaTemplateId;
	}

	public void setNpaTemplateId(Long npaTemplateId) {
		this.npaTemplateId = npaTemplateId;
	}

	public String getNpaTemplateCode() {
		return npaTemplateCode;
	}

	public void setNpaTemplateCode(String npaTemplateCode) {
		this.npaTemplateCode = npaTemplateCode;
	}

	public String getNpaTemplateDesc() {
		return npaTemplateDesc;
	}

	public void setNpaTemplateDesc(String npaTemplateDesc) {
		this.npaTemplateDesc = npaTemplateDesc;
	}

}
