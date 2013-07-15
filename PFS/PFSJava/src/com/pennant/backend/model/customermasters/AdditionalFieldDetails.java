package com.pennant.backend.model.customermasters;

import com.pennant.backend.model.Entity;

public class AdditionalFieldDetails implements java.io.Serializable,Entity{

	
	private static final long serialVersionUID = 2879801837590124084L;
	
	private String moduleName;
	private String fieldName;
	private String fieldLabel;
	private String fieldType;
	private int fieldLength;
	private String fieldPrec;
	private String fieldFormat;
	private boolean fieldIsMandatory;
	private String fieldConstraint;
	private String fieldList;
	private int seqOrder;

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override 
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub
		
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
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

	public String getFieldPrec() {
		return fieldPrec;
	}

	public void setFieldPrec(String fieldPrec) {
		this.fieldPrec = fieldPrec;
	}

	public String getFieldFormat() {
		return fieldFormat;
	}

	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	public boolean isFieldIsMandatory() {
		return fieldIsMandatory;
	}

	public void setFieldIsMandatory(boolean fieldIsMandatory) {
		this.fieldIsMandatory = fieldIsMandatory;
	}

	public String getFieldConstraint() {
		return fieldConstraint;
	}

	public void setFieldConstraint(String fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
	}

	public int getSeqOrder() {
		return seqOrder;
	}

	public void setSeqOrder(int seqOrder) {
		this.seqOrder = seqOrder;
	}

	public String getFieldList() {
		return fieldList;
	}

	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}

}
