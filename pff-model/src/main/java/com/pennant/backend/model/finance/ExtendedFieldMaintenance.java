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
 * * FileName : FinanceEnquiry.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-03-2012 * * Modified Date
 * : 16-03-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ExtendedFieldMaintenance extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -7702107666101609103L;

	private long id = Long.MIN_VALUE;
	private String reference;
	private String event;
	private int seqNo = 0;
	private String tableName;
	private String typeCode;
	private String typeCodeDesc;
	private Map<String, Object> mapValues = new HashMap<>();
	private Map<String, Object> auditMapValues = new HashMap<>();
	private boolean newRecord = false;
	private ExtendedFieldMaintenance befImage;
	private List<ExtendedFieldRender> extFieldRenderList = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private ExtendedFieldHeader extendedFieldHeader;
	private String type;
	private String lovDescFinProduct;
	@XmlTransient
	private LoggedInUser userDetails;
	private long instructionUID = Long.MIN_VALUE;

	public ExtendedFieldMaintenance() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("seqNo");
		excludeFields.add("tableName");
		excludeFields.add("typeCode");
		excludeFields.add("typeCodeDesc");
		excludeFields.add("mapValues");
		excludeFields.add("auditMapValues");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("instructionUID");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCodeDesc() {
		return typeCodeDesc;
	}

	public void setTypeCodeDesc(String typeCodeDesc) {
		this.typeCodeDesc = typeCodeDesc;
	}

	public Map<String, Object> getMapValues() {
		return mapValues;
	}

	public void setMapValues(Map<String, Object> mapValues) {
		this.mapValues = mapValues;
	}

	public Map<String, Object> getAuditMapValues() {
		return auditMapValues;
	}

	public void setAuditMapValues(Map<String, Object> auditMapValues) {
		this.auditMapValues = auditMapValues;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public ExtendedFieldMaintenance getBefImage() {
		return befImage;
	}

	public void setBefImage(ExtendedFieldMaintenance befImage) {
		this.befImage = befImage;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public List<ExtendedFieldRender> getExtFieldRenderList() {
		return extFieldRenderList;
	}

	public void setExtFieldRenderList(List<ExtendedFieldRender> extFieldRenderList) {
		this.extFieldRenderList = extFieldRenderList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLovDescFinProduct() {
		return lovDescFinProduct;
	}

	public void setLovDescFinProduct(String lovDescFinProduct) {
		this.lovDescFinProduct = lovDescFinProduct;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}
}
