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

import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

/**
 * DAO methods declaration for the <b>ReturnDataSet model</b> class.<br>
 * 
 */
public interface PostingsDAO {
	
	List<ReturnDataSet> getPostingsByFinReference(String id,String type);
	long saveHeader(ReturnDataSet rule, String status,String type);
	void saveBatch(List<ReturnDataSet> dataSetList, String type,boolean isEODPostings);
	long getLinkedTransId(ReturnDataSet dataSet);
	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId);
	void saveEODBatch(List<ReturnDataSet> dataSetList, String type, String isDummy);
	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal);
	FinanceSummary getTotalFeeCharges(FinanceSummary summary);
	void saveChargesBatch(List<FeeRule> chargeList, boolean isWIF, String tableType);
	List<FeeRule> getFeeChargesByFinRef(String financeReference, boolean isWIF, String tableType);
	void deleteChargesBatch(String finrefrence,boolean isWIF,  String tableType);
	void updateBatch(List<ReturnDataSet> dataSetList, String type);
	void deleteAll(String type);
	FeeRule getTakafulFee(String finReference, String type);
	BigDecimal getPostAmtByTranIdandEvent(String finReference, String finEvent, long linkedTranId);
}
