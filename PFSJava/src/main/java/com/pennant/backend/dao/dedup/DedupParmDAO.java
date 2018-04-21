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
 * FileName    		:  DedupParmDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.dedup;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

/**
 * DAO methods declaration for the <b>DedupParm model</b> class.<br>
 * 
 */
public interface DedupParmDAO {
	DedupParm getDedupParmByID(String id,String queryModule,String querySubCode, String type);
	void update(DedupParm dedupParm,String type);
	void delete(DedupParm dedupParm,String type);
	long save(DedupParm dedupParm,String type);
	List<DedupParm> getDedupParmByModule(String queryModule, String querySubCode, String type);
	@SuppressWarnings("rawtypes")
	List validate(String resultQuery,CustomerDedup customerDedup);
	List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup,String sqlQuery);
	List<FinanceDedup> fetchFinDedupDetails(FinanceDedup dedup, String sqlQuery);
	List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail, String tableType);
	List<String> getRuleFieldNames(String blacklistType);
	List<CollateralSetup> queryExecution(String query, Map<String, Object> fielValueMap);
	
}