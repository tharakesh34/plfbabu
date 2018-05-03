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
import java.util.Map;

import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

public interface TransactionEntryDAO {

	TransactionEntry getTransactionEntry();
	TransactionEntry getNewTransactionEntry();
	TransactionEntry getTransactionEntryById(long id,int transOrder,String type);
	void update(TransactionEntry transactionEntry,String type);
	void delete(TransactionEntry transactionEntry,String type);
	long save(TransactionEntry transactionEntry,String type);
	List<TransactionEntry> getListTransactionEntryById(final long id, String type,boolean postingProcess);
	List<Rule> getListFeeChargeRules(List<Long> accSetIdList, String ruleEvent, String type, int seqOrder);
	void deleteByAccountingSetId(long accountSetid, String tableType);
	List<TransactionEntry> getListFeeTransEntryById(long id, String type);
	List<TransactionEntry> getODTransactionEntries();
	List<TransactionEntry> getTransactionEntryList(String oDRuleCode);
	void updateTransactionEntryList(List<TransactionEntry> entries);
	List<TransactionEntry> getListTransactionEntryByRefType(String finType, String finEvent, int refType, String roleCode, 
			String type,boolean postingProcess);
	List<TransactionEntry> getListTranEntryForBatch(long accSetid, String type);
	List<Long> getAccountSetIds();
	List<String> getFeeCodeList(List<Long> accountSetId);
	Map<String,String> getAccountingFeeCodes(List<Long> accountSetId);
	List<TransactionEntry> getTransactionEntriesbyFinType(String fintype, String type);
	List<Rule> getSubheadRules(List<String> subHeadRules, String string);
	int getTransactionEntryByRuleCode(String ruleCode, String type);

}