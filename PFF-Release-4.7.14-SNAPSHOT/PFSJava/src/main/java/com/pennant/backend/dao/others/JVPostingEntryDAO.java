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
 * FileName    		:  JVPostingEntryDAO.java                                                   * 	  
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
import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.others.JVPostingEntry;

public interface JVPostingEntryDAO {

	JVPostingEntry getNewJVPostingEntry();
	JVPostingEntry getJVPostingEntryById(long id, long txnReference,
			long acEntryRef, String type);
	JVPostingEntry getJVPostingEntryById(long batchRef,
	        long txnReference, String account, String txnEntry, BigDecimal txnAmount, String type);
	void update(JVPostingEntry jVPostingEntry, String type);
	void delete(JVPostingEntry jVPostingEntry, String type);
	long save(JVPostingEntry jVPostingEntry, String type);
	void saveJVPostingEntryList(List<JVPostingEntry> aJVPostingEntryList, String type);
	void deleteByID(JVPostingEntry jVPostingEntry, String tableType);
	List<JVPostingEntry> getJVPostingEntryListById(long id, String type);
	List<JVPostingEntry> getFailureJVPostingEntryListById(long id, String type);
	List<JVPostingEntry> getDistinctJVPostingEntryListById(JVPostingEntry jVPostingEntry,
	        String type);
	List<JVPostingEntry> getDistinctJVPostingEntryValidationStatusById(JVPostingEntry jVPostingEntry,
	        String type);
	List<JVPostingEntry> getDistinctJVPostingEntryPostingStatusById(JVPostingEntry jVPostingEntry,
			String type);
	List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef,
	        String type);
	void updateValidationStatus(JVPostingEntry jVPostingEntry, String type);
	void updateListValidationStatus(List<JVPostingEntry> aJVPostingEntryList, String type, boolean isAccountWise);
	void updatePostingStatus(JVPostingEntry jVPostingEntry, String type);
	void updateListPostingStatus(List<JVPostingEntry> aJVPostingEntryList, String type, boolean isTxnRefWise);
	void updateWorkFlowDetails(JVPostingEntry jVPostingEntry, String type);
	void updateDeletedDetails(JVPostingEntry jVPostingEntry, String type);
	void updateDeleteFlag(JVPostingEntry jVPostingEntry, String type);
	int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry);
	void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry);
	void updateDelteEntryDetails(JVPostingEntry jVPostingEntry, String type);
	void deleteIAEntries(long batchReference);
	JVPostingEntry getJVPostingEntrybyDerivedTxnRef(long derivedTxnRef,long batchReference);
	
}