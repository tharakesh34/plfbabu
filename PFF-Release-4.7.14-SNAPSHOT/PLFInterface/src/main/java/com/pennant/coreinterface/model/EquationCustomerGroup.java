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
 * FileName    		:  CustomerGroup.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>CustomerGroup table</b>.<br>
 *
 */
public class EquationCustomerGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -719919649510983373L;

	public EquationCustomerGroup() {
		super();
	}
	
	private long custGrpID = Long.MIN_VALUE;
	private String custGrpCode;
	private String custGrpDesc;
	private String custGrpRO1;
	private String lovDescCustGrpRO1Name;
	private long custGrpLimit;
	private boolean custGrpIsActive;
	
	public long getCustGrpID() {
		return custGrpID;
	}
	public void setCustGrpID(long custGrpID) {
		this.custGrpID = custGrpID;
	}
	
	public String getCustGrpCode() {
		return custGrpCode;
	}
	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}
	
	public String getCustGrpDesc() {
		return custGrpDesc;
	}
	public void setCustGrpDesc(String custGrpDesc) {
		this.custGrpDesc = custGrpDesc;
	}
	
	public String getCustGrpRO1() {
		return custGrpRO1;
	}
	public void setCustGrpRO1(String custGrpRO1) {
		this.custGrpRO1 = custGrpRO1;
	}

	public String getLovDescCustGrpRO1Name() {
		return lovDescCustGrpRO1Name;
	}
	public void setLovDescCustGrpRO1Name(String lovDescCustGrpRO1Name) {
		this.lovDescCustGrpRO1Name = lovDescCustGrpRO1Name;
	}

	public long getCustGrpLimit() {
		return custGrpLimit;
	}
	public void setCustGrpLimit(long custGrpLimit) {
		this.custGrpLimit = custGrpLimit;
	}

	public boolean isCustGrpIsActive() {
		return custGrpIsActive;
	}
	public void setCustGrpIsActive(boolean custGrpIsActive) {
		this.custGrpIsActive = custGrpIsActive;
	}
}
