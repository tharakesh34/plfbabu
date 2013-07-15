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
 * FileName    		:  ScheduleCalculationCode.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-11-2011    														*
 *                                                                  						*
 * Modified Date    :  01-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.rmtmasters;

/**
 * Model class for the <b>RMTSchCalCodes table</b>.<br>
 *
 */
public class ScheduleCalculationCode implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String CalCode = null;
	private String CalDesc;
	
	public String getCalCode() {
		return CalCode;
	}
	public void setCalCode(String calCode) {
		CalCode = calCode;
	}
	public String getCalDesc() {
		return CalDesc;
	}
	public void setCalDesc(String calDesc) {
		CalDesc = calDesc;
	}
}
