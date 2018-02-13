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
 * FileName    		:  ChequePurpose.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ChequePurpose table</b>.<br>
 *
 */
public class SysNotification extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long sysNotificationId = Long.MIN_VALUE;
	private String queryCode;
	private String lovDescQueryDesc;
	private String lovDescSqlQuery;
	private String description;
	private String templateCode;
	private String lovDescTemplateDesc;
	private String doctype;
	private String docName;
	private String docImage;
	private boolean newRecord;
	private SysNotification befImage;
	private LoggedInUser userDetails;

	private List<SysNotificationDetails> sysNotificationDetailsList;

	public boolean isNew() {
		return isNewRecord();
	}

	public SysNotification() {
		super();
	}

	public SysNotification(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return sysNotificationId;
	}

	public void setId(long id) {
		this.sysNotificationId = id;
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public String getLovDescQueryDesc() {
		return lovDescQueryDesc;
	}

	public void setLovDescQueryDesc(String lovDescQueryDesc) {
		this.lovDescQueryDesc = lovDescQueryDesc;
	}

	public String getLovDescSqlQuery() {
		return lovDescSqlQuery;
	}

	public void setLovDescSqlQuery(String lovDescSqlQuery) {
		this.lovDescSqlQuery = lovDescSqlQuery;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getLovDescTemplateDesc() {
		return lovDescTemplateDesc;
	}

	public void setLovDescTemplateDesc(String templateDesc) {
		this.lovDescTemplateDesc = templateDesc;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public SysNotification getBefImage() {
		return befImage;
	}

	public void setBefImage(SysNotification befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocImage() {
		return docImage;
	}

	public void setDocImage(String docImage) {
		this.docImage = docImage;
	}

	public long getSysNotificationId() {
		return sysNotificationId;
	}

	public void setSysNotificationId(long sysNotificationId) {
		this.sysNotificationId = sysNotificationId;
	}

	public List<SysNotificationDetails> getSysNotificationDetailsList() {
		return sysNotificationDetailsList;
	}

	public void setSysNotificationDetailsList(List<SysNotificationDetails> sysNotificationDetailsList) {
		this.sysNotificationDetailsList = sysNotificationDetailsList;
	}
}