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
 * FileName    		:  DeviationDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-06-2015    														*
 *                                                                  						*
 * Modified Date    :  22-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.solutionfactory;

import java.util.List;

import com.pennant.backend.model.solutionfactory.DeviationDetail;

public interface DeviationDetailDAO {
	DeviationDetail getDeviationDetail();

	DeviationDetail getNewDeviationDetail();

	DeviationDetail getDeviationDetailById(long id, String userRole, String type);

	void update(DeviationDetail deviationDetail, String type);

	void delete(DeviationDetail deviationDetail, String type);

	long save(DeviationDetail deviationDetail, String type);

	List<DeviationDetail> getDeviationDetailsByDeviationId(long id, String type);

	List<DeviationDetail> getDeviationDetailsByModuleFinType(String finType, String module,String type);
}
