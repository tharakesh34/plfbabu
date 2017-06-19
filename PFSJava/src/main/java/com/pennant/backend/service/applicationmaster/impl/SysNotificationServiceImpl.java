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
 * FileName    		:  SysNotificationServiceImpl.java                                                   * 	  
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
package com.pennant.backend.service.applicationmaster.impl;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.applicationmaster.SysNotificationDAO;
import com.pennant.backend.model.applicationmaster.SysNotification;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SysNotificationService;

/**
 * Service implementation for methods that depends on <b>SysNotification</b>.<br>
 * 
 */
public class SysNotificationServiceImpl extends GenericService<SysNotification> implements SysNotificationService {
	private static final Logger logger = Logger.getLogger(SysNotificationServiceImpl.class);

	private SysNotificationDAO sysNotificationDAO;

	/**
	 * @return the sysNotificationDAO
	 */
	public SysNotificationDAO getSysNotificationDAO() {
		return sysNotificationDAO;
	}
	/**
	 * @param sysNotificationDAO the sysNotificationDAO to set
	 */
	public void setSysNotificationDAO(SysNotificationDAO sysNotificationDAO) {
		this.sysNotificationDAO = sysNotificationDAO;
	}

	@Override
	public void saveSysNotification(SysNotification sysNotification) {
		getSysNotificationDAO().save(sysNotification, "");
	}

	@Override
	public void updateSysNotification(SysNotification sysNotification) {
		getSysNotificationDAO().update(sysNotification, "");
	}

	@Override
	public void deleteSysNotification(long id) {
		getSysNotificationDAO().delete(id, "");
	}

	@Override
	public SysNotification getSysNotificationById(long id) {
		return null;
	}
	@Override
	public SysNotification getApprovedSysNotificationById(long id, boolean mailMenu) {
		logger.debug("Entering");
		SysNotification sysNotification =  getSysNotificationDAO().getSysNotificationById(id, "_AView");
		if (sysNotification != null && mailMenu) {
			sysNotification.setSysNotificationDetailsList(getSysNotificationDAO().getCustomerDetails(sysNotification.getLovDescSqlQuery()));
		}
		logger.debug("Leaving");
		return sysNotification;

	}
	@Override
	public long getTemplateId(String templateCode) {
 		return getSysNotificationDAO().getTemplateId(templateCode);
	}
	@Override
	public String getCustomerEMail(long custID) {
 		return getSysNotificationDAO().getCustomerEMail(custID);
	}

}