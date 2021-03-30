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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  PostingsDAO.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  07-02-2012    
 *                                                                  
 * Modified Date    :  07-02-2012    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-02-2012       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface PostingsDAO {

	long saveHeader(ReturnDataSet rule, String status, String type);

	void saveBatch(List<ReturnDataSet> dataSetList, boolean isNewTranID);

	void updateStatusByLinkedTranId(long linkedTranId, String postStatus);

	void updateStatusByPostRef(long postingId, String postStatus);

	void updateStatusByFinRef(String finReference, String postStatus);

	long getLinkedTransId();

	long getPostingId();

	List<ReturnDataSet> getPostingsByPostRef(Long postref);

	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId);

	List<ReturnDataSet> getPostingsByPostRef(long postingId);

	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type);

	void updateBatch(List<ReturnDataSet> dataSetList, String type);

	void deleteAll(String type);

	BigDecimal getPostAmtByTranIdandEvent(String finReference, String finEvent, long linkedTranId);

	List<Long> getLinkTranIdByRef(String finReference);

	List<ReturnDataSet> getPostingsbyFinanceBranch(String branchCode);

	List<ReturnDataSet> getPostingsByVasref(String finReference, String[] finEvent);

	List<ReturnDataSet> getPostingsByFinRef(String finReference, boolean reqReversals);

	List<ReturnDataSet> getPostingsByTransIdList(List<Long> tranIdList);

	void updatePostCtg();

	List<ReturnDataSet> getPostingsByLinkTransId(List<Long> tranIdList, String finReference);

	List<ReturnDataSet> getPostingsByFinRef(String finReference);

	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent);

	List<ReturnDataSet> getPostingsByLinkedTranId(List<Long> linkedTranId, boolean reversal);

	List<ReturnDataSet> getPostings(String postRef, String finEvent);
}
