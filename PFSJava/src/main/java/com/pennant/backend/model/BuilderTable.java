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
 * * FileName : BuilderTable.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified Date :
 * 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-02-2011 BuilderTable 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model;

public class BuilderTable implements java.io.Serializable {

	private static final long serialVersionUID = 4645180153311924143L;
	private long id = Long.MIN_VALUE;
	private String fieldName;
	private String fieldDesc;
	private String fieldControl;
	private String data_type;
	private String character_maximum_length;

	public BuilderTable() {
	    super();
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDesc() {
		return fieldDesc;
	}

	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	public String getFieldControl() {
		return fieldControl;
	}

	public void setFieldControl(String fieldControl) {
		this.fieldControl = fieldControl;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String dataType) {
		this.data_type = dataType;
	}

	public String getCharacter_maximum_length() {
		return character_maximum_length;
	}

	public void setCharacter_maximum_length(String characterMaximumLength) {
		this.character_maximum_length = characterMaximumLength;
	}

}
