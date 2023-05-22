package com.pennant.backend.model.extendedfield;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ExtendedFieldRender extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3393253877805479673L;

	private String reference;
	private int seqNo = 0;
	private String tableName;
	private String typeCode;
	private String typeCodeDesc;
	@XmlElement
	private Map<String, Object> mapValues = null;
	private Map<String, Object> auditMapValues = null;
	private ExtendedFieldRender befImage;
	private long instructionUID = Long.MIN_VALUE;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public int getId() {
		return seqNo;
	}

	public void setId(int id) {
		this.seqNo = id;
	}

	public Map<String, Object> getMapValues() {
		return mapValues;
	}

	public void setMapValues(Map<String, Object> map) {
		this.mapValues = map;
	}

	public ExtendedFieldRender getBefImage() {
		return befImage;
	}

	public void setBefImage(ExtendedFieldRender befImage) {
		this.befImage = befImage;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getAuditMapValues() {
		return auditMapValues;
	}

	public void setAuditMapValues(Map<String, Object> auditMapValues) {
		this.auditMapValues = auditMapValues;
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

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

}
