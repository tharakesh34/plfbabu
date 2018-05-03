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
 * FileName    		:  DashboardDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.dashboard;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.DashBoard;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface DashboardConfigurationService {
	DashboardConfiguration getNewDashboardDetail();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	DashboardConfiguration getDashboardDetailById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	Map<String, DashboardPosition> getDashboardPositionsByUser(long userId);

	void savePositions(List<DashboardPosition> dashboardDetailsList, long userid);

	DashBoard getDashBoardData(long userId, String roles);

	Map<String, DashboardConfiguration> getDashboardConfigurations(long userId);

	List<ChartSetElement> getLabelAndValues(DashboardConfiguration aDashboardConfiguration, String condition,
			LoggedInUser user, List<SecurityRole> roles);

	DashboardConfiguration getApprovedDashboardDetailById(String id);
}