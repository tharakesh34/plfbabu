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
 * FileName    		:  CustomerNotesType.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerNotesType table</b>.<br>
 *
 */
public class CustomerNotesType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4184815336367307646L;
	
	private String custNotesTypeCode;
	private String custNotesTypeDesc;
	private boolean custNotesTypeIsPerminent;
	private String custNotesTypeArchiveFrq;
	private String lovDescCustNotesTypeArcFrqName;
	private boolean custNotesTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private CustomerNotesType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerNotesType() {
		super();
	}

	public CustomerNotesType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return custNotesTypeCode;
	}
	public void setId (String id) {
		this.custNotesTypeCode = id;
	}
	
	public String getCustNotesTypeCode() {
		return custNotesTypeCode;
	}
	public void setCustNotesTypeCode(String custNotesTypeCode) {
		this.custNotesTypeCode = custNotesTypeCode;
	}
	
	public String getCustNotesTypeDesc() {
		return custNotesTypeDesc;
	}
	public void setCustNotesTypeDesc(String custNotesTypeDesc) {
		this.custNotesTypeDesc = custNotesTypeDesc;
	}
	
	public boolean isCustNotesTypeIsPerminent() {
		return custNotesTypeIsPerminent;
	}
	public void setCustNotesTypeIsPerminent(boolean custNotesTypeIsPerminent) {
		this.custNotesTypeIsPerminent = custNotesTypeIsPerminent;
	}
	
	public String getCustNotesTypeArchiveFrq() {
		return custNotesTypeArchiveFrq;
	}
	public void setCustNotesTypeArchiveFrq(String custNotesTypeArchiveFrq) {
		this.custNotesTypeArchiveFrq = custNotesTypeArchiveFrq;
	}

	public String getLovDescCustNotesTypeArcFrqName() {
		return this.lovDescCustNotesTypeArcFrqName;
	}
	public void setLovDescCustNotesTypeArcFrqName(String lovDescCustNotesTypeArcFrqName) {
		this.lovDescCustNotesTypeArcFrqName = lovDescCustNotesTypeArcFrqName;
	}
	
	public void setCustNotesTypeIsActive(boolean custNotesTypeIsActive) {
		this.custNotesTypeIsActive = custNotesTypeIsActive;
	}
	public boolean isCustNotesTypeIsActive() {
		return custNotesTypeIsActive;
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

	public CustomerNotesType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerNotesType beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
