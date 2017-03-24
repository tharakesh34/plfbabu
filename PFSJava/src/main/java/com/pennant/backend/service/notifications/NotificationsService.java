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
 * FileName    		:  NotificationsService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.notifications;

import java.util.List;
import java.util.Map;

import com.pennant.app.model.MailData;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Notifications;

/**
 * Service declaration for methods that depends on <b>Notifications</b>.<br>
 * 
 */
public interface NotificationsService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	Notifications getNotificationsById(String ruleCode);
	Notifications getApprovedNotificationsById(String ruleCode);
	List<Notifications> getApprovedNotificationsByModule(String ruleModule);
	List<Notifications> getApprovedNotificationsByRuleIdList(List<Long> notificationIdList);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<Long> getTemplateIds(String templateType);
	List<MailData> getMailData(String mailName);
	Map<String, Object> mergeFields(String query);
	int triggerMail(String query);

}