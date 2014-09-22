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
 * FileName    		:  CommidityLoanDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import java.util.List;

import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;

public interface CommidityLoanDetailDAO {

	CommidityLoanDetail getCommidityLoanDetail();
	CommidityLoanDetail getNewCommidityLoanDetail();
	CommidityLoanDetail getCommidityLoanDetailById(final String id,String itemType, String type);
	void update(CommidityLoanDetail commidityLoanDetailDAO,String type);
	void delete(CommidityLoanDetail commidityLoanDetailDAO,String type);
	String save(CommidityLoanDetail commidityLoanDetailDAO,String type);
	void initialize(CommidityLoanDetail commidityLoanDetailDAO);
	void refresh(CommidityLoanDetail entity);
	List<CommidityLoanDetail> getCommidityLoanDetailByFinRef(String id, String type);
	void deleteByFinRef(String finReference, String tableType);
}