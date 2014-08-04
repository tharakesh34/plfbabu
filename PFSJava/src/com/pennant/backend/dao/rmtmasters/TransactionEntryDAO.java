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
 * FileName    		:  TransactionEntryDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;
import java.util.List;

import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

public interface TransactionEntryDAO {

	public TransactionEntry getTransactionEntry();
	public TransactionEntry getNewTransactionEntry();
	public TransactionEntry getTransactionEntryById(long id,int transOrder,String type);
	public void update(TransactionEntry transactionEntry,String type);
	public void delete(TransactionEntry transactionEntry,String type);
	public long save(TransactionEntry transactionEntry,String type);
	public void initialize(TransactionEntry transactionEntry);
	public void refresh(TransactionEntry entity);
	public List<TransactionEntry> getListTransactionEntryById(final long id, String type,boolean postingProcess);
	public List<Rule> getListFeeChargeRules(final long id, String ruleEvent, String type, int seqOrder);
	public void deleteByAccountingSetId(long accountSetid, String tableType);
	public List<TransactionEntry> getListFeeTransEntryById(long id, String type);
	public List<TransactionEntry> getODTransactionEntries();
	public List<TransactionEntry> getTransactionEntryList(String oDRuleCode);
	public void updateTransactionEntryList(List<TransactionEntry> entries);
	public List<TransactionEntry> getListTransactionEntryByRefType(String finType, int refType, String roleCode, 
			String type,boolean postingProcess);
	public List<TransactionEntry> getListTranEntryForBatch(long accSetid, String type);
	public List<Long> getAccountSetIds();
	public List<String> getListFeeCodes(long accountSetId);
}