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

	public JVPostingEntry getNewJVPostingEntry();

	public JVPostingEntry getJVPostingEntryById(long id, long txnReference,
			long acEntryRef, String type);

	public JVPostingEntry getJVPostingEntryById(long batchRef,
	        long txnReference, String account, String txnEntry, BigDecimal txnAmount, String type);

	public void update(JVPostingEntry jVPostingEntry, String type);

	public void delete(JVPostingEntry jVPostingEntry, String type);

	public long save(JVPostingEntry jVPostingEntry, String type);

	public void saveJVPostingEntryList(List<JVPostingEntry> aJVPostingEntryList, String type);

	public void initialize(JVPostingEntry jVPostingEntry);

	public void refresh(JVPostingEntry entity);

	public void deleteByID(JVPostingEntry jVPostingEntry, String tableType);

	public List<JVPostingEntry> getJVPostingEntryListById(long id, String type);

	public List<JVPostingEntry> getFailureJVPostingEntryListById(long id, String type);
	
	public List<JVPostingEntry> getDistinctJVPostingEntryListById(JVPostingEntry jVPostingEntry,
	        String type);

	public List<JVPostingEntry> getDistinctJVPostingEntryValidationStatusById(JVPostingEntry jVPostingEntry,
	        String type);

	public List<JVPostingEntry> getDistinctJVPostingEntryPostingStatusById(JVPostingEntry jVPostingEntry,
			String type);
	
	public List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef,
	        String type);

	public void updateValidationStatus(JVPostingEntry jVPostingEntry, String type);

	public void updateListValidationStatus(List<JVPostingEntry> aJVPostingEntryList, String type, boolean isAccountWise);

	public void updatePostingStatus(JVPostingEntry jVPostingEntry, String type);

	public void updateListPostingStatus(List<JVPostingEntry> aJVPostingEntryList, String type, boolean isTxnRefWise);
	
	public void updateWorkFlowDetails(JVPostingEntry jVPostingEntry, String type);

	public void updateDeletedDetails(JVPostingEntry jVPostingEntry, String type);

	// TODO
	public void updateDeleteFlag(JVPostingEntry jVPostingEntry, String type);

	public int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry);

	public void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry);
	
	public void updateDelteEntryDetails(JVPostingEntry jVPostingEntry, String type);

	public void deleteIAEntries(long batchReference);
	
}