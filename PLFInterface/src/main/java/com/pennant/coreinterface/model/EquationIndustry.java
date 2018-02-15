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

import java.math.BigDecimal;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class EquationIndustry extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 7676091139142583852L;

	public EquationIndustry() {
		super();
	}
	
	private String industryCode;
	private String industryDesc;
	private String subSectorCode;
	private BigDecimal industryLimit;
	private boolean industryIsActive;	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getIndustryCode() {
		return industryCode;
	}
	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}
	
	public String getIndustryDesc() {
		return industryDesc;
	}
	public void setIndustryDesc(String industryDesc) {
		this.industryDesc = industryDesc;
	}
	
	public String getSubSectorCode() {
		return subSectorCode;
	}
	public void setSubSectorCode(String subSectorCode) {
		this.subSectorCode = subSectorCode;
	}
	
	public BigDecimal getIndustryLimit() {
		return industryLimit;
	}
	public void setIndustryLimit(BigDecimal industryLimit) {
		this.industryLimit = industryLimit;
	}
	
	public boolean isIndustryIsActive() {
		return industryIsActive;
	}
	public void setIndustryIsActive(boolean industryIsActive) {
		this.industryIsActive = industryIsActive;
	}
}
