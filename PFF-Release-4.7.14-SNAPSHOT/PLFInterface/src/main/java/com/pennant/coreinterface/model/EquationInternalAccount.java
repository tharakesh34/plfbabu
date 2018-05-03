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
 * FileName    		:  Currency.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2013    														*
 *                                                                  						*
 * Modified Date    :  17-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2013       Pennant	                 0.1                                            * 
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
package com.pennant.coreinterface.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class EquationInternalAccount extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 7597347632337408219L;

	public EquationInternalAccount() {
		super();
	}
	
	private String sIACode;
	private String sIAName;
	private String sIAShortName;
	private String sIAAcType;
	private String sIANumber;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	
	public String getsIACode() {
		return sIACode;
	}
	public void setsIACode(String sIACode) {
		this.sIACode = sIACode;
	}
	
	public String getsIAName() {
		return sIAName;
	}
	public void setsIAName(String sIAName) {
		this.sIAName = sIAName;
	}
	
	public String getsIAShortName() {
		return sIAShortName;
	}
	public void setsIAShortName(String sIAShortName) {
		this.sIAShortName = sIAShortName;
	}
	
	public String getsIAAcType() {
		return sIAAcType;
	}
	public void setsIAAcType(String sIAAcType) {
		this.sIAAcType = sIAAcType;
	}
	
	public String getsIANumber() {
		return sIANumber;
	}
	public void setsIANumber(String sIANumber) {
		this.sIANumber = sIANumber;
	}
}
