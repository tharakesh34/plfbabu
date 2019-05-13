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
 * FileName    		:  DashboardDetailDAO.java                                              * 	  
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
package com.pennant.backend.dao.dashboard;

import java.util.List;

import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.fusioncharts.ChartSetElement;

public interface DashboardConfigurationDAO {
	DashboardConfiguration getNewDashboardDetail();

	DashboardConfiguration getDashboardDetailByID(String id, String type);

	void update(DashboardConfiguration dashboardConfiguration, String type);

	void delete(DashboardConfiguration dashboardConfiguration, String type);

	String save(DashboardConfiguration dashboardConfiguration, String type);

	List<DashboardPosition> getDashboardPositionsByUser(long userId);

	void SavePositions(DashboardPosition dashboardPosition);

	void delete(long userId);

	List<ChartSetElement> getLabelAndValues(DashboardConfiguration dashboardDetail);

	List<DashboardConfiguration> getDashboardConfigurations(long userId);
}