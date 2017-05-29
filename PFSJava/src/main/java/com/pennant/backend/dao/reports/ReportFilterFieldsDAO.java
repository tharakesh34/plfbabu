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
 * FileName    		:  ReportFilterFieldsDAO.java                                                   * 	  
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

import com.pennant.backend.model.reports.ReportFilterFields;

/**
 * DAO methods declaration for the <b>ReportFilterFields model</b> class.<br>
 * 
 */
public interface ReportFilterFieldsDAO {

	ReportFilterFields getReportFilterFields();
	ReportFilterFields getNewReportFilterFields();
	ReportFilterFields getReportFilterFieldsById(long id,String type);
	List<ReportFilterFields> getReportFilterFieldsByReportId(final long reportId,String type);
	void update(ReportFilterFields reportFilterFields,String type);
	void delete(ReportFilterFields reportFilterFields,String type);
	void deleteByReportId(final long reportId,String type);
	long save(ReportFilterFields reportFilterFields,String type);
}