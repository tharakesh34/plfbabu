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
public class EquationCountry extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6235890705769120104L;
	
	private String countryCode;
	private String countryDesc;
	private BigDecimal countryParentLimit;
	private BigDecimal countryResidenceLimit;
	private BigDecimal countryRiskLimit;
	private boolean countryIsActive;
	
	public EquationCountry() {
		super();
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryDesc() {
		return countryDesc;
	}
	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}
	
	public BigDecimal getCountryParentLimit() {
		return countryParentLimit;
	}
	public void setCountryParentLimit(BigDecimal countryParentLimit) {
		this.countryParentLimit = countryParentLimit;
	}
	
	public BigDecimal getCountryResidenceLimit() {
		return countryResidenceLimit;
	}
	public void setCountryResidenceLimit(BigDecimal countryResidenceLimit) {
		this.countryResidenceLimit = countryResidenceLimit;
	}
	
	public BigDecimal getCountryRiskLimit() {
		return countryRiskLimit;
	}
	public void setCountryRiskLimit(BigDecimal countryRiskLimit) {
		this.countryRiskLimit = countryRiskLimit;
	}
	
	public boolean isCountryIsActive() {
		return countryIsActive;
	}
	public void setCountryIsActive(boolean countryIsActive) {
		this.countryIsActive = countryIsActive;
	}
}
