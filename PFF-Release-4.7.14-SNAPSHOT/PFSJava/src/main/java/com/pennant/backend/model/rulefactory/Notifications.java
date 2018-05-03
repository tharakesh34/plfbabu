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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  Notifications.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.model.rulefactory;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Notifications table</b>.<br>
 *
 */
public class Notifications extends AbstractWorkflowEntity implements Entity{

	private static final long serialVersionUID = 522289325946000330L;

	private long ruleId = Long.MIN_VALUE;
	private String ruleCode;
	private String ruleModule;
	private String templateType;
	private String ruleCodeDesc;
	private String ruleTemplate;
	private String actualBlockTemplate;
	private String ruleReciepent;
	private String actualBlockReciepent;
	private String ruleAttachment;
	private String actualBlockAtachment;
	private boolean newRecord;
	private String lovValue;
	private Notifications befImage;
	private LoggedInUser userDetails;
	
	private String templateTypeFields;
	private String ruleReciepentFields;
	private String ruleAttachmentFields;

	public boolean isNew() {
		return isNewRecord();
	}

	public Notifications() {
		super();
	}

	public Notifications(String id) {
		super();
		this.setRuleCode(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//	
	
	public long getId() {
		return ruleId;
	}
	public void setId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	public String getRuleCode() {
		return ruleCode;
	}
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}
	
	public String getRuleModule() {
		return ruleModule;
	}
	public void setRuleModule(String ruleModule) {
		this.ruleModule = ruleModule;
	}
	
	public String getTemplateType() {
	    return templateType;
    }
	public void setTemplateType(String templateType) {
	    this.templateType = templateType;
    }

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}
	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}
	
	public String getRuleTemplate() {
	    return ruleTemplate;
    }

	public void setRuleTemplate(String ruleTemplate) {
	    this.ruleTemplate = ruleTemplate;
    }

	public String getActualBlockTemplate() {
	    return actualBlockTemplate;
    }

	public void setActualBlockTemplate(String actualBlockTemplate) {
	    this.actualBlockTemplate = actualBlockTemplate;
    }

	public String getRuleReciepent() {
	    return ruleReciepent;
    }

	public void setRuleReciepent(String ruleReciepent) {
	    this.ruleReciepent = ruleReciepent;
    }

	public String getActualBlockReciepent() {
	    return actualBlockReciepent;
    }

	public void setActualBlockReciepent(String actualBlockReciepent) {
	    this.actualBlockReciepent = actualBlockReciepent;
    }

	public String getRuleAttachment() {
	    return ruleAttachment;
    }

	public void setRuleAttachment(String ruleAttachment) {
	    this.ruleAttachment = ruleAttachment;
    }

	public String getActualBlockAtachment() {
	    return actualBlockAtachment;
    }

	public void setActualBlockAtachment(String actualBlockAtachment) {
	    this.actualBlockAtachment = actualBlockAtachment;
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

	public Notifications getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Notifications beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getTemplateTypeFields() {
		return templateTypeFields;
	}

	public void setTemplateTypeFields(String templateTypeFields) {
		this.templateTypeFields = templateTypeFields;
	}

	public String getRuleReciepentFields() {
		return ruleReciepentFields;
	}

	public void setRuleReciepentFields(String ruleReciepentFields) {
		this.ruleReciepentFields = ruleReciepentFields;
	}

	public String getRuleAttachmentFields() {
		return ruleAttachmentFields;
	}

	public void setRuleAttachmentFields(String ruleAttachmentFields) {
		this.ruleAttachmentFields = ruleAttachmentFields;
	}
}