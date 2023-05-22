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
 * * FileName : MasterDef.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-05-2018 * * Modified Date :
 * 19-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-05-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model;

/**
 * Model class for the <b>Master_Def table</b>.<br>
 *
 */
public class MasterDef {
	private String masterType;
	private String keyType;
	private String keyCode;
	private boolean validationReq;
	private boolean proceedException;

	public String getMasterType() {
		return masterType;
	}

	public void setMasterType(String masterType) {
		this.masterType = masterType;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public boolean isValidationReq() {
		return validationReq;
	}

	public void setValidationReq(boolean validationReq) {
		this.validationReq = validationReq;
	}

	public boolean isProceedException() {
		return proceedException;
	}

	public void setProceedException(boolean proceedException) {
		this.proceedException = proceedException;
	}

}
