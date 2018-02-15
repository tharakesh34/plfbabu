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
 * FileName    		:  DeviationHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-06-2015    														*
 *                                                                  						*
 * Modified Date    :  22-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.solutionfactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DeviationHeader table</b>.<br>
 *
 */
public class DeviationHeader extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long deviationID = Long.MIN_VALUE;
	private String finType;
	private String module;
	private String moduleCode;
	private String valueType;
	private boolean newRecord=false;
	private String lovValue;
	private DeviationHeader befImage;
	private LoggedInUser userDetails;
	
	private List<DeviationDetail> deviationDetails=new ArrayList<DeviationDetail>();

	public boolean isNew() {
		return isNewRecord();
	}

	public DeviationHeader() {
		super();
	}

	public DeviationHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("deviationDetails");
	return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return deviationID;
	}
	
	public void setId (long id) {
		this.deviationID = id;
	}
	
	public long getDeviationID() {
		return deviationID;
	}
	public void setDeviationID(long deviationID) {
		this.deviationID = deviationID;
	}
	
	
		
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	
		
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	
		
	
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	
	
		
	
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
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

	public DeviationHeader getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DeviationHeader beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<DeviationDetail> getDeviationDetails() {
	    return deviationDetails;
    }

	public void setDeviationDetails(List<DeviationDetail> deviationDetails) {
	    this.deviationDetails = deviationDetails;
    }
}
