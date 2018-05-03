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
 * FileName    		:  ReportConfigurationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.reports;

import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.reports.ReportConfiguration;

/**
 * DAO methods declaration for the <b>ReportConfiguration model</b> class.<br>
 * 
 */
public interface ReportConfigurationDAO {

	ReportConfiguration getReportConfiguration();
	ReportConfiguration getNewReportConfiguration();
	ReportConfiguration getReportConfigurationById(long id,String type);
	void update(ReportConfiguration reportConfiguration,String type);
	void delete(ReportConfiguration reportConfiguration,String type);
	long save(ReportConfiguration reportConfiguration,String type);
	
	//Month End Report Details
	List<ValueLabel> getMonthEndReportGrpCodes();
	List<ValueLabel> getReportListByGrpCode(String groupCode);
}