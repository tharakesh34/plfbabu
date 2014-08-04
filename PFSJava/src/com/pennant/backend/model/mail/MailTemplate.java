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
 * FileName    		:  MailTemplate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.mail;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>MailTemplate table</b>.<br>
 *
 */
public class MailTemplate implements java.io.Serializable, Entity {

    private static final long serialVersionUID = -7999948592404630380L;
    
	private long   templateId = Long.MIN_VALUE;
	private String templateFor;
	private String module;
	private String templateCode = null;
	private String templateDesc;
	private boolean smsTemplate;
	private String smsContent;	
	private boolean emailTemplate;
	private byte[] emailContent;
	private byte[] lovDescEmailAttachment;
	private String lovDescAttachmentName;
	private String emailFormat;
	private String emailSendTo;
	private String lovDescEmailSendTo;
	private String lovDescEmailFormatName;
	private String emailSubject;
	private int turnAroundTime;
	private boolean repeat;
	private boolean active;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private MailTemplate befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private String lovDescFormattedContent="";
	private String[] lovDescMailId;
	

	public boolean isNew() {
		return isNewRecord();
	}

	public MailTemplate() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("MailTemplate");
	}

	public MailTemplate(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return templateId;
	}
	public void setId (long id) {
		this.templateId = id;
	}
	
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	public String getTemplateFor() {
		return templateFor;
	}
	public void setTemplateFor(String templateFor) {
		this.templateFor = templateFor;
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}

	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
	public String getTemplateDesc() {
		return templateDesc;
	}
	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}

	public boolean isSmsTemplate() {
		return smsTemplate;
	}
	public void setSmsTemplate(boolean smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public boolean isEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(boolean emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public byte[] getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(byte[] emailContent) {
		this.emailContent = emailContent;
	}

	public String getEmailFormat() {
		return emailFormat;
	}
	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}
	
	public String getEmailSendTo() {
		return emailSendTo;
	}
	public void setEmailSendTo(String emailSendTo) {
		this.emailSendTo = emailSendTo;
	}
	
	public String getLovDescEmailSendTo() {
		return lovDescEmailSendTo;
	}
	public void setLovDescEmailSendTo(String lovDescEmailSendTo) {
		this.lovDescEmailSendTo = lovDescEmailSendTo;
	}

	public String getLovDescEmailFormatName() {
		return this.lovDescEmailFormatName;
	}
	public void setLovDescEmailFormatName (String lovDescEmailFormatName) {
		this.lovDescEmailFormatName = lovDescEmailFormatName;
	}
	
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	
	public int getTurnAroundTime() {
		return turnAroundTime;
	}
	public void setTurnAroundTime(int turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public MailTemplate getBefImage(){
		return this.befImage;
	}
	public void setBefImage(MailTemplate beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	public String getLovDescFormattedContent() {
		return lovDescFormattedContent;
	}
	public void setLovDescFormattedContent(String lovDescFormattedContent) {
		this.lovDescFormattedContent = lovDescFormattedContent;
	}

	public String[] getLovDescMailId() {
		return lovDescMailId;
	}
	public void setLovDescMailId(String[] lovDescMailId) {
		this.lovDescMailId = lovDescMailId;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(MailTemplate mailTemplate) {
		return getId() == mailTemplate.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof MailTemplate) {
			MailTemplate mailTemplate = (MailTemplate) obj;
			return equals(mailTemplate);
		}
		return false;
	}

	public byte[] getLovDescEmailAttachment() {
    	return lovDescEmailAttachment;
    }

	public void setLovDescEmailAttachment(byte[] emailAttachment) {
    	this.lovDescEmailAttachment = emailAttachment;
    }

	public String getLovDescAttachmentName() {
    	return lovDescAttachmentName;
    }

	public void setLovDescAttachmentName(String attachmentName) {
    	this.lovDescAttachmentName = attachmentName;
    }
	
}
