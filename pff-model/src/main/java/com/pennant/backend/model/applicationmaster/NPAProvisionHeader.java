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
 * FileName    		:  NPAProvisionHeader.java                                                   * 	  
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>NPAProvisionHeader table</b>.<br>
 *
 */
public class NPAProvisionHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	@XmlElement
	private String entity;
	private String entityName;
	@XmlElement
	private String finType;
	private String finTypeName;

	private boolean newRecord = false;
	private String lovValue;
	private NPAProvisionHeader befImage;
	private LoggedInUser userDetails;

	private List<NPAProvisionDetail> provisionDetailsList = new LinkedList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private Long npaTemplateId;
	private String npaTemplateCode;
	private String npaTemplateDesc;

	public boolean isNew() {
		return isNewRecord();
	}

	public NPAProvisionHeader() {
		super();
	}

	public NPAProvisionHeader(long id) {
		super();
		this.setId(id);
	}

	public NPAProvisionHeader copyEntity() {
		NPAProvisionHeader entity = new NPAProvisionHeader();
		entity.setId(this.id);
		entity.setEntity(this.entity);
		entity.setEntityName(this.entityName);
		entity.setFinType(this.finType);
		entity.setFinTypeName(this.finTypeName);
		entity.setNewRecord(this.newRecord);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		this.provisionDetailsList.stream()
				.forEach(e -> entity.getProvisionDetailsList().add(e == null ? null : e.copyEntity()));
		this.auditDetailMap.entrySet().stream().forEach(e -> {
			List<AuditDetail> newList = new ArrayList<AuditDetail>();
			if (e.getValue() != null) {
				e.getValue().forEach(
						auditDetail -> newList.add(auditDetail == null ? null : auditDetail.getNewCopyInstance()));
				entity.getAuditDetailMap().put(e.getKey(), newList);
			} else
				entity.getAuditDetailMap().put(e.getKey(), null);
		});

		entity.setNpaTemplateId(this.npaTemplateId);
		entity.setNpaTemplateCode(this.npaTemplateCode);
		entity.setNpaTemplateDesc(this.npaTemplateDesc);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("entityName");
		excludeFields.add("finTypeName");
		excludeFields.add("classificationHeaderList");
		excludeFields.add("npaTemplateCode");
		excludeFields.add("npaTemplateDesc");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeName() {
		return this.finTypeName;
	}

	public void setFinTypeName(String finTypeName) {
		this.finTypeName = finTypeName;
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

	public NPAProvisionHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(NPAProvisionHeader beforeImage) {
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

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<NPAProvisionDetail> getProvisionDetailsList() {
		return provisionDetailsList;
	}

	public void setProvisionDetailsList(List<NPAProvisionDetail> provisionDetailsList) {
		this.provisionDetailsList = provisionDetailsList;
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
