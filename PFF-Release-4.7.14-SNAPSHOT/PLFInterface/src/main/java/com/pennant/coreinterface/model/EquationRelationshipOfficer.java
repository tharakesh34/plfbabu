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
 * FileName    		:  RelationshipOfficer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>RelationshipOfficer table</b>.<br>
 *
 */
public class EquationRelationshipOfficer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 493770789752870431L;

	public EquationRelationshipOfficer() {
		super();
	}
	
	private String rOfficerCode = null;
	private String rOfficerDesc;
	private String rOfficerDeptCode;
	private String lovDescROfficerDeptCodeName;
	private boolean rOfficerIsActive;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getROfficerCode() {
		return rOfficerCode;
	}
	public void setROfficerCode(String rOfficerCode) {
		this.rOfficerCode = rOfficerCode;
	}
	
	public String getROfficerDesc() {
		return rOfficerDesc;
	}
	public void setROfficerDesc(String rOfficerDesc) {
		this.rOfficerDesc = rOfficerDesc;
	}
		
	public String getROfficerDeptCode() {
		return rOfficerDeptCode;
	}
	public void setROfficerDeptCode(String rOfficerDeptCode) {
		this.rOfficerDeptCode = rOfficerDeptCode;
	}

	public String getLovDescROfficerDeptCodeName() {
		return this.lovDescROfficerDeptCodeName;
	}
	public void setLovDescROfficerDeptCodeName (String lovDescROfficerDeptCodeName) {
		this.lovDescROfficerDeptCodeName = lovDescROfficerDeptCodeName;
	}
	
	public boolean isROfficerIsActive() {
		return rOfficerIsActive;
	}
	public void setROfficerIsActive(boolean rOfficerIsActive) {
		this.rOfficerIsActive = rOfficerIsActive;
	}
}
