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
 * FileName    		:  BounceReason.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BounceReason table</b>.<br>
 *
 */
@XmlType(propOrder = {"bounceID","bounceCode","reasonType","category","reason","action","feeID","returnID","active"})
@XmlAccessorType(XmlAccessType.FIELD)
public class BounceReason extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long bounceID = 0;
	private String bounceCode;
	private int reasonType;
	private int category;
	private String categoryDesc;
	private String reason;
	private int action;
	private long ruleID=0;
	private String ruleCode;
	private String ruleCodeDesc;
	private String returnCode;
	private String lovdesccategory;
	private boolean active;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private BounceReason befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public BounceReason() {
		super();
	}

	public BounceReason(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("ruleCode");
		excludeFields.add("ruleCodeDesc");
		excludeFields.add("categoryDesc");
		excludeFields.add("lovDescCategory");
		return excludeFields;
	}

	public long getId() {
		return bounceID;
	}
	
	public void setId (long id) {
		this.bounceID = id;
	}
	public long getBounceID() {
		return bounceID;
	}
	public void setBounceID(long bounceID) {
		this.bounceID = bounceID;
	}
	
	public String getBounceCode() {
		return bounceCode;
	}
	public void setBounceCode(String bounceCode) {
		this.bounceCode = bounceCode;
	}
	
	public int getReasonType() {
		return reasonType;
	}
	public void setReasonType(int reasonType) {
		this.reasonType = reasonType;
	}
	
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
		this.categoryDesc = PennantStaticListUtil.getPropertyValue(PennantStaticListUtil.getCategoryType(), category);
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
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

	public BounceReason getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(BounceReason beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}


	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}

	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}

	public long getRuleID() {
		return ruleID;
	}

	public void setRuleID(long ruleID) {
		this.ruleID = ruleID;
	}

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> bounceReasonMap = new HashMap<String, Object>();
		getDeclaredFieldValues(bounceReasonMap);
		return bounceReasonMap;
	}

	public void getDeclaredFieldValues(HashMap<String, Object> bounceReasonMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "br_" Should be in small case only, if we want to change the case we need to update the configuration fields as well.
				bounceReasonMap.put("br_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}

	public String getCategoryDesc() {
		this.categoryDesc = PennantStaticListUtil.getPropertyValue(PennantStaticListUtil.getCategoryType(), category);
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public String getLovdesccategory() {
		return lovdesccategory;
	}

	public void setLovdesccategory(String lovdesccategory) {
		this.lovdesccategory = lovdesccategory;
	}
	
}
