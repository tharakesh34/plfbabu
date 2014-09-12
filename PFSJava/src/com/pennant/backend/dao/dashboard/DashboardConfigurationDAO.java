/**
s * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  DashboardDetailDAO.java                                                   * 	  
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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.fusioncharts.ChartSetElement;


public interface DashboardConfigurationDAO {

 	public DashboardConfiguration getNewDashboardDetail();
	public DashboardConfiguration getDashboardDetailByID(String id,String type);
	public void update(DashboardConfiguration dashboardConfiguration,String type);
	public void delete(DashboardConfiguration dashboardConfiguration,String type);
	public String save(DashboardConfiguration dashboardConfiguration,String type);
	public void initialize(DashboardConfiguration dashboardConfiguration);
	public void refresh(DashboardConfiguration entity);
	public ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters);

 	public List<DashboardPosition> getDashboardPositionsByUser(long userId);
  	public void SavePositions(DashboardPosition dashboardPosition); 
	public void delete(long userId);
	public List<ChartSetElement> getLabelAndValues(DashboardConfiguration dashboardDetail);
	public List<DashboardConfiguration> getDashboardConfigurations(long userId);
 
}