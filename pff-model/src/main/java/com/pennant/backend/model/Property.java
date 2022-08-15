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
 *
 * FileName : Property.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 07-04-2018 *
 * 
 * Modified Date : 07-04-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-04-2018 Sai Krishna 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model;

public class Property {
	private Object key;
	private String value;
	private String parent;

	private Object key1;
	private Object key2;
	private Object key3;
	private Object key4;
	private Object key5;

	public Property() {
		super();
	}

	public Property(Object key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Object getKey1() {
		return key1;
	}

	public void setKey1(Object key1) {
		this.key1 = key1;
	}

	public Object getKey2() {
		return key2;
	}

	public void setKey2(Object key2) {
		this.key2 = key2;
	}

	public Object getKey3() {
		return key3;
	}

	public void setKey3(Object key3) {
		this.key3 = key3;
	}

	public Object getKey4() {
		return key4;
	}

	public void setKey4(Object key4) {
		this.key4 = key4;
	}

	public Object getKey5() {
		return key5;
	}

	public void setKey5(Object key5) {
		this.key5 = key5;
	}

}
