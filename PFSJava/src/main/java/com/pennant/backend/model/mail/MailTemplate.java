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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>MailTemplate table</b>.<br>
 *
 */
public class MailTemplate extends AbstractWorkflowEntity implements Entity {

    private static final long serialVersionUID = -7999948592404630380L;
    
	private long   templateId = Long.MIN_VALUE;
	private String templateFor;
	private String module;
	private String templateCode;
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
	private boolean newRecord;
	private String lovValue;
	private MailTemplate befImage;
	private LoggedInUser userDetails;
	
	private String lovDescFormattedContent="";
	private String[] lovDescMailId;
	

	public boolean isNew() {
		return isNewRecord();
	}

	public MailTemplate() {
		super();
	}

	public MailTemplate(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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
