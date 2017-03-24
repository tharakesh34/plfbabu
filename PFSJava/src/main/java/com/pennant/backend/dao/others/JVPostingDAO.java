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
 * FileName    		:  JVPostingDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.others;
import com.pennant.backend.model.others.JVPosting;

public interface JVPostingDAO {

	JVPosting getJVPosting();
	JVPosting getNewJVPosting();
	JVPosting getJVPostingById(long id, String type);
	JVPosting getJVPostingByFileName(String fileName);
	void update(JVPosting jVPosting, String type);
	void updateHeaderDetails(JVPosting jVPosting, String type);
	void updateValidationStatus(JVPosting jVPosting, String type);
	void updateBatchPostingStatus(JVPosting jVPosting, String type);
	void delete(JVPosting jVPosting, String type);
	long save(JVPosting jVPosting, String type);
	long getMaxSeqNum(JVPosting jvPosting);
	long getBatchRerbyExpRef(String expReference);
	
}