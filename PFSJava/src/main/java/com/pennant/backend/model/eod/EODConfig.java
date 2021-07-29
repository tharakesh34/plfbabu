/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : EODConfig.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-05-2017 * * Modified Date :
 * 24-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.eod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EODConfig table</b>.<br>
 *
 */
public class EODConfig extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long eodConfigId = Long.MIN_VALUE;
	private boolean extMnthRequired;
	private Date mnthExtTo;
	private boolean active;
	private String lovValue;
	private EODConfig befImage;
	private LoggedInUser userDetails;
	private boolean inExtMnth;
	private Date prvExtMnth;

	private boolean autoEodRequired;
	private String eODStartJobFrequency;
	private boolean enableAutoEod;
	private boolean eODAutoDisable;
	private boolean sendEmailRequired;
	private String sMTPHost;
	private String sMTPPort;
	private boolean sMTPAutenticationRequired;
	private String sMTPUserName;
	private String sMTPPwd;
	private String encryptionType;
	private String fromEmailAddress;
	private String fromName;
	private String toEmailAddress;
	private String cCEmailAddress;
	private boolean emailNotifReqrd;
	private boolean publishNotifReqrd;
	private String reminderFrequency;
	private boolean delayNotifyReq;
	private String delayFrequency;

	public EODConfig() {
		super();
	}

	public EODConfig(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public long getId() {
		return eodConfigId;
	}

	public void setId(long id) {
		this.eodConfigId = id;
	}

	public long getEodConfigId() {
		return eodConfigId;
	}

	public void setEodConfigId(long eodConfigId) {
		this.eodConfigId = eodConfigId;
	}

	public boolean isExtMnthRequired() {
		return extMnthRequired;
	}

	public void setExtMnthRequired(boolean extMnthRequired) {
		this.extMnthRequired = extMnthRequired;
	}

	public Date getMnthExtTo() {
		return mnthExtTo;
	}

	public void setMnthExtTo(Date mnthExtTo) {
		this.mnthExtTo = mnthExtTo;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public EODConfig getBefImage() {
		return this.befImage;
	}

	public void setBefImage(EODConfig beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isInExtMnth() {
		return inExtMnth;
	}

	public void setInExtMnth(boolean inExtMnth) {
		this.inExtMnth = inExtMnth;
	}

	public Date getPrvExtMnth() {
		return prvExtMnth;
	}

	public void setPrvExtMnth(Date prvExtMnth) {
		this.prvExtMnth = prvExtMnth;
	}

	public boolean isAutoEodRequired() {
		return autoEodRequired;
	}

	public void setAutoEodRequired(boolean autoEodRequired) {
		this.autoEodRequired = autoEodRequired;
	}

	public String getEODStartJobFrequency() {
		return eODStartJobFrequency;
	}

	public void setEODStartJobFrequency(String eODStartJobFrequency) {
		this.eODStartJobFrequency = eODStartJobFrequency;
	}

	public boolean isEnableAutoEod() {
		return enableAutoEod;
	}

	public void setEnableAutoEod(boolean enableAutoEod) {
		this.enableAutoEod = enableAutoEod;
	}

	public boolean isEODAutoDisable() {
		return eODAutoDisable;
	}

	public void setEODAutoDisable(boolean eODAutoDisable) {
		this.eODAutoDisable = eODAutoDisable;
	}

	public boolean isSendEmailRequired() {
		return sendEmailRequired;
	}

	public void setSendEmailRequired(boolean sendEmailRequired) {
		this.sendEmailRequired = sendEmailRequired;
	}

	public String getSMTPHost() {
		return sMTPHost;
	}

	public void setSMTPHost(String sMTPHost) {
		this.sMTPHost = sMTPHost;
	}

	public String getSMTPPort() {
		return sMTPPort;
	}

	public void setSMTPPort(String sMTPPort) {
		this.sMTPPort = sMTPPort;
	}

	public boolean isSMTPAutenticationRequired() {
		return sMTPAutenticationRequired;
	}

	public void setSMTPAutenticationRequired(boolean sMTPAutenticationRequired) {
		this.sMTPAutenticationRequired = sMTPAutenticationRequired;
	}

	public String getSMTPUserName() {
		return sMTPUserName;
	}

	public void setSMTPUserName(String sMTPUserName) {
		this.sMTPUserName = sMTPUserName;
	}

	public String getSMTPPwd() {
		return sMTPPwd;
	}

	public void setSMTPPwd(String sMTPPwd) {
		this.sMTPPwd = sMTPPwd;
	}

	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getFromEmailAddress() {
		return fromEmailAddress;
	}

	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToEmailAddress() {
		return toEmailAddress;
	}

	public void setToEmailAddress(String toEmailAddress) {
		this.toEmailAddress = toEmailAddress;
	}

	public String getCCEmailAddress() {
		return cCEmailAddress;
	}

	public void setCCEmailAddress(String cCEmailAddress) {
		this.cCEmailAddress = cCEmailAddress;
	}

	public String getReminderFrequency() {
		return reminderFrequency;
	}

	public void setReminderFrequency(String reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}

	public boolean isEmailNotifReqrd() {
		return emailNotifReqrd;
	}

	public void setEmailNotifReqrd(boolean emailNotifReqrd) {
		this.emailNotifReqrd = emailNotifReqrd;
	}

	public boolean isPublishNotifReqrd() {
		return publishNotifReqrd;
	}

	public void setPublishNotifReqrd(boolean publishNotifReqrd) {
		this.publishNotifReqrd = publishNotifReqrd;
	}

	public boolean isDelayNotifyReq() {
		return delayNotifyReq;
	}

	public void setDelayNotifyReq(boolean delayNotifyReq) {
		this.delayNotifyReq = delayNotifyReq;
	}

	public String getDelayFrequency() {
		return delayFrequency;
	}

	public void setDelayFrequency(String delayFrequency) {
		this.delayFrequency = delayFrequency;
	}

}
