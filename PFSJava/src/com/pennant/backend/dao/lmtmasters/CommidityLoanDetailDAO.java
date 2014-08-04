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

	public CommidityLoanDetail getCommidityLoanDetail();
	public CommidityLoanDetail getNewCommidityLoanDetail();
	public CommidityLoanDetail getCommidityLoanDetailById(final String id,String itemType, String type);
	public void update(CommidityLoanDetail commidityLoanDetailDAO,String type);
	public void delete(CommidityLoanDetail commidityLoanDetailDAO,String type);
	public String save(CommidityLoanDetail commidityLoanDetailDAO,String type);
	public void initialize(CommidityLoanDetail commidityLoanDetailDAO);
	public void refresh(CommidityLoanDetail entity);
	public List<CommidityLoanDetail> getCommidityLoanDetailByFinRef(String id, String type);
	public void deleteByFinRef(String finReference, String tableType);
}