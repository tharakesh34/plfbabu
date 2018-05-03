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
 * FileName    		:  CommitmentRateDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2016    														*
 *                                                                  						*
 * Modified Date    :  22-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.commitment;

import java.util.List;

import com.pennant.backend.model.commitment.CommitmentRate;

public interface CommitmentRateDAO {
	CommitmentRate getCommitmentRate();

	CommitmentRate getNewCommitmentRate();

	CommitmentRate getCommitmentRateById(String cmtReference, String id, String type);

	void update(CommitmentRate commitmentRate, String type);

	void delete(CommitmentRate commitmentRate, String type);

	String save(CommitmentRate commitmentRate, String type);

	List<CommitmentRate> getCommitmentRatesByCmtRef(final String id, String type);

	void deleteByCmtReference(String cmtReference, String type);
}