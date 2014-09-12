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

import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;

/**
 * DAO methods declaration for the <b>DedupParm model</b> class.<br>
 * 
 */
public interface DedupParmDAO {

	public DedupParm getDedupParm();
	public DedupParm getNewDedupParm();
	public DedupParm getDedupParmByID(String id,String queryModule,String querySubCode, String type);
	public void update(DedupParm dedupParm,String type);
	public void delete(DedupParm dedupParm,String type);
	public String save(DedupParm dedupParm,String type);
	public void initialize(DedupParm dedupParm);
	public void refresh(DedupParm entity);
	@SuppressWarnings("rawtypes")
	public List validate(String resultQuery,CustomerDedup customerDedup);
	public FinanceDedup getFinDedupByCustId(long custID);
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup,String sqlQuery);
	public List<FinanceDedup> fetchFinDedupDetails(FinanceDedup dedup, String sqlQuery);
}