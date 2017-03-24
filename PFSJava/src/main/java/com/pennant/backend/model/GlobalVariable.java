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
 * FileName    		:  GlobalVariable.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model;

public class GlobalVariable implements java.io.Serializable {
	private static final long serialVersionUID = 8273231076965887889L;
	
	private long vID = Long.MIN_VALUE;
	private String varCode;
	private String varName;
	private String varValue;
	private String varType;
	

	public GlobalVariable() {

	}
	
	public GlobalVariable(long vId) {
		super();
		this.vID = vId;
	}

	public long getvID() {
		return vID;
	}

	public void setvID(long vID) {
		this.vID = vID;
	}

	public String getVarCode() {
		return varCode;
	}

	public void setVarCode(String varCode) {
		this.varCode = varCode;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarValue() {
		return varValue;
	}

	public void setVarValue(String varValue) {
		this.varValue = varValue;
	}

	public String getVarType() {
	    return varType;
    }

	public void setVarType(String varType) {
	    this.varType = varType;
    }
}
