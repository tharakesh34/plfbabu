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
 * FileName    		:  SalesOfficer.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SalesOfficer table</b>.<br>
 *
 */
public class SalesOfficer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2264963791613334060L;
	
	private String salesOffCode;
	private String salesOffFName;
	private String salesOffMName;
	private String salesOffLName;
	private String salesOffShrtName;
	private String salesOffDept;
	private String lovDescSalesOffDeptName;
	private boolean salesOffIsActive;
	private boolean newRecord;
	private String lovValue;
	private SalesOfficer befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SalesOfficer() {
		super();
	}

	public SalesOfficer(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return salesOffCode;
	}
	
	public void setId (String id) {
		this.salesOffCode = id;
	}
	
	public String getSalesOffCode() {
		return salesOffCode;
	}
	public void setSalesOffCode(String salesOffCode) {
		this.salesOffCode = salesOffCode;
	}
	
	public String getSalesOffFName() {
		return salesOffFName;
	}
	public void setSalesOffFName(String salesOffFName) {
		this.salesOffFName = salesOffFName;
	}
	
	public String getSalesOffMName() {
		return salesOffMName;
	}
	public void setSalesOffMName(String salesOffMName) {
		this.salesOffMName = salesOffMName;
	}
	
	public String getSalesOffLName() {
		return salesOffLName;
	}
	public void setSalesOffLName(String salesOffLName) {
		this.salesOffLName = salesOffLName;
	}
	
	public String getSalesOffShrtName() {
		return salesOffShrtName;
	}
	public void setSalesOffShrtName(String salesOffShrtName) {
		this.salesOffShrtName = salesOffShrtName;
	}
	
	public String getSalesOffDept() {
		return salesOffDept;
	}
	public void setSalesOffDept(String salesOffDept) {
		this.salesOffDept = salesOffDept;
	}
	
	public String getLovDescSalesOffDeptName() {
		return this.lovDescSalesOffDeptName;
	}

	public void setLovDescSalesOffDeptName (String lovDescSalesOffDeptName) {
		this.lovDescSalesOffDeptName = lovDescSalesOffDeptName;
	}
	
	public boolean isSalesOffIsActive() {
		return salesOffIsActive;
	}
	public void setSalesOffIsActive(boolean salesOffIsActive) {
		this.salesOffIsActive = salesOffIsActive;
	}
	
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public SalesOfficer getBefImage(){
		return this.befImage;
	}
	public void setBefImage(SalesOfficer beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
