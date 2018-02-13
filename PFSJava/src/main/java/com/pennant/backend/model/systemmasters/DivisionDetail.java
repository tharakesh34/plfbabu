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
 * FileName    		:  DivisionDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DivisionDetail table</b>.<br>
 *
 */
public class DivisionDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String divisionCode;
	private String divisionCodeDesc;
	private String divSuspTrigger;
	private String divSuspRemarks;
	private boolean alwPromotion;
	private boolean active;
	private String entityCode;
	private String entityDesc;
	private boolean newRecord;
	private String lovValue;
	private DivisionDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public DivisionDetail() {
		super();
	}

	public DivisionDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("entityDesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return divisionCode;
	}
	
	public void setId (String id) {
		this.divisionCode = id;
	}
	
	public String getDivisionCode() {
		return divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	
	
		
	
	public String getDivisionCodeDesc() {
		return divisionCodeDesc;
	}
	public void setDivisionCodeDesc(String divisionCodeDesc) {
		this.divisionCodeDesc = divisionCodeDesc;
	}

	public boolean isAlwPromotion() {
	    return alwPromotion;
    }

	public void setAlwPromotion(boolean alwPromotion) {
	    this.alwPromotion = alwPromotion;
    }

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public DivisionDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DivisionDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getDivSuspTrigger() {
		return divSuspTrigger;
	}

	public void setDivSuspTrigger(String divSuspTrigger) {
		this.divSuspTrigger = divSuspTrigger;
	}

	public String getDivSuspRemarks() {
		return divSuspRemarks;
	}

	public void setDivSuspRemarks(String divSuspRemarks) {
		this.divSuspRemarks = divSuspRemarks;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

}
