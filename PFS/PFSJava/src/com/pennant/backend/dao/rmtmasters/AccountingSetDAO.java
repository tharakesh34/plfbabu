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
 * FileName    		:  AccountingSetDAO.java                                                   * 	  
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

import com.pennant.backend.model.rmtmasters.AccountingSet;

public interface AccountingSetDAO {

	public AccountingSet getAccountingSet();
	public AccountingSet getNewAccountingSet();
	public AccountingSet getAccountingSetById(long id,String type);
	public void update(AccountingSet accountingSet,String type);
	public void delete(AccountingSet accountingSet,String type);
	public long save(AccountingSet accountingSet,String type);
	public void initialize(AccountingSet accountingSet);
	public void refresh(AccountingSet entity);
	public List<AccountingSet> getListAERuleBySysDflt(String type);
	public AccountingSet getAccSetSysDflByEvent(String event, String type);
	
	//Commitments
	public Long getAccountingSetId(String eventCode, String accSetCode);

}