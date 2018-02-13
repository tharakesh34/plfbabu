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
 * FileName    		:  CustomerAdditionalDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.customermasters;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerAdditionalDetail table</b>.<br>
 *
 */
public class CustomerAdditionalDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6021576460912988391L;
	
	private long custID=Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custAcademicLevel;
	private String lovDescCustAcademicLevelName;
	private String academicDecipline;
	private String lovDescAcademicDeciplineName;
	private long custRefCustID;
	private String custRefStaffID;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerAdditionalDetail befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerAdditionalDetail() {
		super();
	}

	public CustomerAdditionalDetail(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}
	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public String getCustAcademicLevel() {
		return custAcademicLevel;
	}
	public void setCustAcademicLevel(String custAcademicLevel) {
		this.custAcademicLevel = custAcademicLevel;
	}

	public String getLovDescCustAcademicLevelName() {
		return this.lovDescCustAcademicLevelName;
	}
	public void setLovDescCustAcademicLevelName(String lovDescCustAcademicLevelName) {
		this.lovDescCustAcademicLevelName = lovDescCustAcademicLevelName;
	}
	
	public String getAcademicDecipline() {
		return academicDecipline;
	}
	public void setAcademicDecipline(String academicDecipline) {
		this.academicDecipline = academicDecipline;
	}

	public String getLovDescAcademicDeciplineName() {
		return this.lovDescAcademicDeciplineName;
	}
	public void setLovDescAcademicDeciplineName(String lovDescAcademicDeciplineName) {
		this.lovDescAcademicDeciplineName = lovDescAcademicDeciplineName;
	}
	
	public long getCustRefCustID() {
		return custRefCustID;
	}
	public void setCustRefCustID(long custRefCustID) {
		this.custRefCustID = custRefCustID;
	}
	
	public String getCustRefStaffID() {
		return custRefStaffID;
	}
	public void setCustRefStaffID(String custRefStaffID) {
		this.custRefStaffID = custRefStaffID;
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

	public CustomerAdditionalDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerAdditionalDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}
	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}
	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
}
