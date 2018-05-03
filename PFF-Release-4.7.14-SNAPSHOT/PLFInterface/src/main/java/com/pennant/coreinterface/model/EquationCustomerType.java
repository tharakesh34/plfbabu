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
 * FileName    		:  CustomerType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>CustomerType table</b>.<br>
 * 
 */
public class EquationCustomerType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3412634410431903886L;

	public EquationCustomerType() {
		super();
	}
	
	private String custTypeCode = null;
	private String custTypeCtg = null;
	private String custTypeDesc;
	private boolean custTypeIsActive;

	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getCustTypeCtg() {
		return custTypeCtg;
	}

	public void setCustTypeCtg(String custTypeCtg) {
		this.custTypeCtg = custTypeCtg;
	}

	public String getCustTypeDesc() {
		return custTypeDesc;
	}
	public void setCustTypeDesc(String custTypeDesc) {
		this.custTypeDesc = custTypeDesc;
	}

	public boolean isCustTypeIsActive() {
		return custTypeIsActive;
	}
	public void setCustTypeIsActive(boolean custTypeIsActive) {
		this.custTypeIsActive = custTypeIsActive;
	}
}
