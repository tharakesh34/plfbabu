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

	public ExtendedFieldData() {

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
}
