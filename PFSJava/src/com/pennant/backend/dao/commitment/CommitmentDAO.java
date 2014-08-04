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
 * FileName    		:  CommitmentDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.reports.AvailCommitment;

public interface CommitmentDAO {

	public Commitment getCommitment();
	public Commitment getNewCommitment();
	public Commitment getCommitmentById(String id,String type);
	public void update(Commitment commitment,String type);
	public void delete(Commitment commitment,String type);
	public String save(Commitment commitment,String type);
	public void initialize(Commitment commitment);
	public void refresh(Commitment entity);
	public int getCmtAmountCount(long custID);
	public int getCmtAmountTotal(long custID);
	public int getUtilizedAmountTotal(long custID);
	public Map<String, Object> getAmountSummary(long custID);
	public boolean updateCommitmentAmounts(String cmtReference, BigDecimal postingAmount, Date cmtExpDate);
	public List<AvailCommitment> getCommitmentListByCustId(long custId, String type);
	public Commitment getCommitmentByFacilityRef(final String id, String type);
	public List<CommitmentSummary> getCommitmentSummary(long custID);
	public void updateNonPerformStatus(String finCommitmentRef);
	public void deleteByRef(String cmtReference, String type);
	public Commitment getCommitmentByRef(String id, String type);
}