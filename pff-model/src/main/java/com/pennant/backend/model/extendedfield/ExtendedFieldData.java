package com.pennant.backend.model.extendedfield;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is designed and used to fulfill API specification requirement
 * 
 * @author durgaprasad.d
 *
 */
@XmlType(propOrder = { "fieldName", "fieldValue" })
@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedFieldData implements Serializable {
	private static final long serialVersionUID = -5630142127775435053L;

	@XmlElement
	private String fieldName;

	@XmlElement
	private Object fieldValue;

	private String fieldLabel;
	private String fieldType;

	private int fieldSeqOrder;
	private int fieldPrec;

	public ExtendedFieldData() {
		super();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
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

	public int getFieldSeqOrder() {
		return fieldSeqOrder;
	}

	public void setFieldSeqOrder(int fieldSeqOrder) {
		this.fieldSeqOrder = fieldSeqOrder;
	}

	public int getFieldPrec() {
		return fieldPrec;
	}

	public void setFieldPrec(int fieldPrec) {
		this.fieldPrec = fieldPrec;
	}
}
