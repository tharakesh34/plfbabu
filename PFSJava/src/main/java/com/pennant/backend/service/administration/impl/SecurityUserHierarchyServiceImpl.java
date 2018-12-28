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
 * FileName    		:  SecurityUserHierarchyServiceImpl.java                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  30-07-2011      Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.administration.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.administration.SecurityUserHierarchyDAO;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserHierarchy;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserHierarchyService;

public class SecurityUserHierarchyServiceImpl extends GenericService<SecurityUserHierarchy>
		implements SecurityUserHierarchyService {
	private static Logger logger = Logger.getLogger(SecurityUserHierarchyServiceImpl.class);

	private SecurityUserHierarchyDAO securityUserHierarchyDAO;

	SecurityUserHierarchyServiceImpl() {
		super();
	}

	@Override
	public void refreshUserHierarchy(SecurityUser securityUser) {
		int depth = 0;
		List<SecurityUserHierarchy> userHierarchyies = new ArrayList<>();

		List<ReportingManager> repotingManagers = securityUser.getReportingManagersList();
		securityUserHierarchyDAO.deleteUserHierarchy(securityUser.getUsrID());

		SecurityUserHierarchy userHierarchy;
		for (ReportingManager reportingManager : repotingManagers) {
			userHierarchy = new SecurityUserHierarchy();
			userHierarchy.setUserId(securityUser.getUsrID());
			userHierarchy.setBusinessVertical(reportingManager.getBusinessVertical());
			userHierarchy.setBranch(reportingManager.getBranch());
			userHierarchy.setProduct(reportingManager.getProduct());
			userHierarchy.setFinType(reportingManager.getFinType());
			userHierarchy.setReportingTo(securityUser.getUsrID());
			userHierarchy.setDepth(depth++);
			userHierarchy.setLastMntBy(securityUser.getLastMntBy());
			userHierarchy.setLastMntOn(new Timestamp(System.currentTimeMillis()));

			userHierarchyies.add(userHierarchy);

			userHierarchy = new SecurityUserHierarchy();
			userHierarchy.setUserId(securityUser.getUsrID());
			userHierarchy.setBusinessVertical(reportingManager.getBusinessVertical());
			userHierarchy.setBranch(reportingManager.getBranch());
			userHierarchy.setProduct(reportingManager.getProduct());
			userHierarchy.setFinType(reportingManager.getFinType());
			userHierarchy.setReportingTo(reportingManager.getReportingTo());
			userHierarchy.setDepth(depth++);
			userHierarchy.setLastMntBy(securityUser.getLastMntBy());
			userHierarchy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			userHierarchyies.add(userHierarchy);

			refreshDownlevel(userHierarchy, reportingManager, userHierarchyies);

		}

		securityUserHierarchyDAO.saveUserHierarchy(userHierarchyies);

	}

	private void refreshDownlevel(SecurityUserHierarchy userHierarchy, ReportingManager reportingManager,
			List<SecurityUserHierarchy> userHierarchyies) {
		List<SecurityUserHierarchy> downLevelUsers = securityUserHierarchyDAO.getDownLevelUsers(userHierarchy);
		List<SecurityUserHierarchy> upLevelUsers = securityUserHierarchyDAO.getUpLevelUsers(userHierarchy);

		if (CollectionUtils.isNotEmpty(downLevelUsers)) {
			for (SecurityUserHierarchy downLevelUser : downLevelUsers) {
				downLevelUser.setBusinessVertical(reportingManager.getBusinessVertical());
				downLevelUser.setBranch(reportingManager.getBranch());
				downLevelUser.setProduct(reportingManager.getProduct());
				downLevelUser.setFinType(reportingManager.getFinType());
				downLevelUser.setReportingTo(reportingManager.getReportingTo());
				downLevelUser.setDepth(downLevelUser.getDepth() + 1);
				userHierarchy.setLastMntBy(userHierarchy.getLastMntBy());
				userHierarchy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				userHierarchyies.add(downLevelUser);

				refreshDownlevel(downLevelUser, reportingManager, userHierarchyies);
			}
		}

		if (CollectionUtils.isNotEmpty(upLevelUsers)) {
			for (SecurityUserHierarchy upLevelUser : upLevelUsers) {
				upLevelUser.setUserId(userHierarchy.getUserId());
				upLevelUser.setBusinessVertical(reportingManager.getBusinessVertical());
				upLevelUser.setBranch(reportingManager.getBranch());
				upLevelUser.setProduct(reportingManager.getProduct());
				upLevelUser.setFinType(reportingManager.getFinType());
				upLevelUser.setReportingTo(upLevelUser.getReportingTo());
				upLevelUser.setDepth(upLevelUser.getDepth() + 1);
				userHierarchy.setLastMntBy(userHierarchy.getLastMntBy());
				userHierarchy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				userHierarchyies.add(upLevelUser);
			}
		}
	}

	public void setSecurityUserHierarchyDAO(SecurityUserHierarchyDAO securityUserHierarchyDAO) {
		this.securityUserHierarchyDAO = securityUserHierarchyDAO;
	}
}
