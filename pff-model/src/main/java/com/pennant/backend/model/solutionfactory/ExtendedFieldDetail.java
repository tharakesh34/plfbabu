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
 * * FileName : ExtendedFieldDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 28-12-2011 * * Modified
 * Date : 28-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 28-12-2011 Pennant 0.1 * * 19-06-2018 Sai Krishna 0.2 story #413 Allow scriptlet for * extended fields without UI. *
 * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.solutionfactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ExtendedFieldDetail table</b>.<br>
 */
@XmlType(propOrder = { "fieldName", "fieldLabel", "fieldType", "fieldSeqOrder", "fieldLength", "fieldPrec",
		"fieldMandatory", "fieldUnique", "fieldList", "fieldDefaultValue", "fieldMinValue", "fieldMaxValue",
		"fieldConstraint" })
@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedFieldDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6761267821648279163L;

	private long moduleId = Long.MIN_VALUE;
	private String lovDescModuleName;
	private String lovDescSubModuleName;
	private String lovDescTableName;

	@XmlElement
	private String fieldName;

	@XmlElement
	private String fieldType;

	@XmlElement(name = "maxLength")
	private int fieldLength;

	@XmlElement(name = "precision")
	private int fieldPrec;

	@XmlElement
	private String fieldLabel;

	@XmlElement(name = "mandatory")
	private boolean fieldMandatory;

	@XmlElement(name = "constraint")
	private String fieldConstraint;

	@XmlElement(name = "seqOrder")
	private int fieldSeqOrder;

	@XmlElement(name = "listValues")
	private String fieldList;

	@XmlElement(name = "dftValue")
	private String fieldDefaultValue;

	@XmlElement(name = "minValue")
	private long fieldMinValue;

	@XmlElement(name = "maxValue")
	private long fieldMaxValue;

	@XmlElement(name = "uniqueField")
	private boolean fieldUnique;
	private String filters;

	private int extendedType;
	private int multiLine;
	private boolean inputElement;
	@XmlElement
	private String parentTag = "";
	private boolean editable;
	private String lovValue;
	private String lovDescErroDesc;
	private ExtendedFieldDetail befImage;
	private LoggedInUser userDetails;
	private boolean allowInRule = false;
	@XmlTransient
	private String scriptlet;
	private boolean visible = true;
	private boolean valFromScript;
	private String defValue;
	// Used to map the data in Agreement generation
	private String agrField;
	private boolean maintAlwd;

	public ExtendedFieldDetail() {
		super();
	}

	public ExtendedFieldDetail(long id) {
		super();
		this.setId(id);
	}

	public ExtendedFieldDetail copyEntity() {
		ExtendedFieldDetail entity = new ExtendedFieldDetail();
		entity.setModuleId(this.moduleId);
		entity.setLovDescModuleName(this.lovDescModuleName);
		entity.setLovDescSubModuleName(this.lovDescSubModuleName);
		entity.setLovDescTableName(this.lovDescTableName);
		entity.setFieldName(this.fieldName);
		entity.setFieldType(this.fieldType);
		entity.setFieldLength(this.fieldLength);
		entity.setFieldPrec(this.fieldPrec);
		entity.setFieldLabel(this.fieldLabel);
		entity.setFieldMandatory(this.fieldMandatory);
		entity.setFieldConstraint(this.fieldConstraint);
		entity.setFieldSeqOrder(this.fieldSeqOrder);
		entity.setFieldList(this.fieldList);
		entity.setFieldDefaultValue(this.fieldDefaultValue);
		entity.setFieldMinValue(this.fieldMinValue);
		entity.setFieldMaxValue(this.fieldMaxValue);
		entity.setFieldUnique(this.fieldUnique);
		entity.setFilters(this.filters);
		entity.setExtendedType(this.extendedType);
		entity.setMultiLine(this.multiLine);
		entity.setInputElement(this.inputElement);
		entity.setParentTag(this.parentTag);
		entity.setEditable(this.editable);
		entity.setLovValue(this.lovValue);
		entity.setLovDescErroDesc(this.lovDescErroDesc);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setAllowInRule(this.allowInRule);
		entity.setScriptlet(this.scriptlet);
		entity.setVisible(this.visible);
		entity.setValFromScript(this.valFromScript);
		entity.setDefValue(this.defValue);
		entity.setAgrField(this.agrField);
		entity.setMaintAlwd(this.maintAlwd);
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

	public long getId() {
		return moduleId;
	}

	public void setId(long id) {
		this.moduleId = id;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getLovDescModuleName() {
		return lovDescModuleName;
	}

	public void setLovDescModuleName(String lovDescModuleName) {
		this.lovDescModuleName = lovDescModuleName;
	}

	public String getLovDescSubModuleName() {
		return lovDescSubModuleName;
	}

	public void setLovDescSubModuleName(String lovDescSubModuleName) {
		this.lovDescSubModuleName = lovDescSubModuleName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public int getFieldPrec() {
		return fieldPrec;
	}

	public void setFieldPrec(int fieldPrec) {
		this.fieldPrec = fieldPrec;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public boolean isFieldMandatory() {
		return fieldMandatory;
	}

	public void setFieldMandatory(boolean fieldMandatory) {
		this.fieldMandatory = fieldMandatory;
	}

	public String getFieldConstraint() {
		return fieldConstraint;
	}

	public void setFieldConstraint(String fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
	}

	public int getFieldSeqOrder() {
		return fieldSeqOrder;
	}

	public void setFieldSeqOrder(int fieldSeqOrder) {
		this.fieldSeqOrder = fieldSeqOrder;
	}

	public String getFieldList() {
		return fieldList;
	}

	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}

	public String getFieldDefaultValue() {
		return fieldDefaultValue;
	}

	public void setFieldDefaultValue(String fieldDefaultValue) {
		this.fieldDefaultValue = fieldDefaultValue;
	}

	public long getFieldMinValue() {
		return fieldMinValue;
	}

	public void setFieldMinValue(long fieldMinValue) {
		this.fieldMinValue = fieldMinValue;
	}

	public long getFieldMaxValue() {
		return fieldMaxValue;
	}

	public void setFieldMaxValue(long fieldMaxValue) {
		this.fieldMaxValue = fieldMaxValue;
	}

	public boolean isFieldUnique() {
		return fieldUnique;
	}

	public void setFieldUnique(boolean fieldUnique) {
		this.fieldUnique = fieldUnique;
	}

	public int getExtendedType() {
		return extendedType;
	}

	public void setExtendedType(int extendedType) {
		this.extendedType = extendedType;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ExtendedFieldDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ExtendedFieldDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescErroDesc() {
		return lovDescErroDesc;
	}

	public void setLovDescErroDesc(String lovDescErroDesc) {
		this.lovDescErroDesc = lovDescErroDesc;
	}

	public int getMultiLine() {
		return multiLine;
	}

	public void setMultiLine(int multiLine) {
		this.multiLine = multiLine;
	}

	public boolean isInputElement() {
		return inputElement;
	}

	public void setInputElement(boolean inputElement) {
		this.inputElement = inputElement;
	}

	public String getParentTag() {
		return parentTag;
	}

	public void setParentTag(String parentTag) {
		this.parentTag = parentTag;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getLovDescTableName() {
		return lovDescTableName;
	}

	public void setLovDescTableName(String lovDescTableName) {
		this.lovDescTableName = lovDescTableName;
	}

	public boolean isAllowInRule() {
		return allowInRule;
	}

	public void setAllowInRule(boolean allowInRule) {
		this.allowInRule = allowInRule;
	}

	public String getScriptlet() {
		return scriptlet;
	}

	public void setScriptlet(String scriptlet) {
		this.scriptlet = scriptlet;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public boolean isValFromScript() {
		return valFromScript;
	}

	public void setValFromScript(boolean valFromScript) {
		this.valFromScript = valFromScript;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public String getAgrField() {
		return agrField;
	}

	public void setAgrField(String agrField) {
		this.agrField = agrField;
	}

	public boolean isMaintAlwd() {
		return maintAlwd;
	}

	public void setMaintAlwd(boolean maintAlwd) {
		this.maintAlwd = maintAlwd;
	}
}
