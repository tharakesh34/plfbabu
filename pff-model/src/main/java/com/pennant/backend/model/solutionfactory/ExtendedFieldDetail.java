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
 * FileName    		:  ExtendedFieldDetail.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 19-06-2018       Sai Krishna              0.2          story #413 Allow scriptlet for    * 
 *                                                        extended fields without UI.       * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.model.solutionfactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ExtendedFieldDetail table</b>.<br>
 */
@XmlType(propOrder = { "fieldName", "fieldLabel", "fieldType", "fieldSeqOrder", "fieldLength", "fieldPrec",
		"fieldMandatory", "fieldUnique", "fieldList", "fieldDefaultValue", "fieldMinValue", "fieldMaxValue",
		"fieldConstraint" })
@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedFieldDetail extends AbstractWorkflowEntity implements Entity {
	
	private static final long	serialVersionUID	= -6761267821648279163L;
	
	private long moduleId = Long.MIN_VALUE;
	private String lovDescModuleName;
	private String lovDescSubModuleName;
	private String lovDescTableName;
	
	@XmlElement
	private String fieldName;
	
	@XmlElement
	private String fieldType;
	
	@XmlElement(name="maxLength")
	private int fieldLength;
	
	@XmlElement(name="precision")
	private int fieldPrec;
	
	@XmlElement
	private String fieldLabel;
	
	@XmlElement(name="mandatory")
	private boolean fieldMandatory;
	
	@XmlElement(name="constraint")
	private String fieldConstraint;
	
	@XmlElement(name="seqOrder")
	private int fieldSeqOrder;
	
	@XmlElement(name="listValues")
	private String fieldList;
	
	@XmlElement(name="dftValue")
	private String fieldDefaultValue;
	
	@XmlElement(name="minValue")
	private long fieldMinValue;
	
	@XmlElement(name="maxValue")
	private long fieldMaxValue;
	
	@XmlElement(name="uniqueField")
	private boolean fieldUnique;
	
	private int extendedType;
	private int		multiLine;
	private boolean inputElement;
	private String parentTag;
	private boolean editable;
	private boolean newRecord;
	private String lovValue;	
	private String lovDescErroDesc;
	private ExtendedFieldDetail befImage;
	private LoggedInUser userDetails;
	private boolean allowInRule=false;
	@XmlTransient
	private String scriptlet;
	private boolean visible=true;

	
	public boolean isNew() {
		return isNewRecord();
	}

	public ExtendedFieldDetail() {
		super();
	}

	public ExtendedFieldDetail(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return moduleId;
	}
	public void setId (long id) {
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

	public ExtendedFieldDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(ExtendedFieldDetail beforeImage){
		this.befImage=beforeImage;
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
}
