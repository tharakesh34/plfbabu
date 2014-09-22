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
 * FileName    		:  AccountEngineRuleDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.rmtmasters.AccountEngineRule;

/**
 * DAO methods declaration for the <b>AccountEngineRule model</b> class.<br>
 * 
 */
public interface AccountEngineRuleDAO {

	 AccountEngineRule getAccountEngineRule();
	 AccountEngineRule getNewAccountEngineRule();
	 AccountEngineRule getAccountEngineRuleById(long id,String type);
	 void update(AccountEngineRule accountEngineRule,String type);
	 void delete(AccountEngineRule accountEngineRule,String type);
	 long save(AccountEngineRule accountEngineRule,String type);
	 void initialize(AccountEngineRule accountEngineRule);
	 void refresh(AccountEngineRule entity);
	 AccountEngineRule getAccountEngineRuleBySysDflt(AccountEngineRule accountEngineRule,String type,boolean idExists);
	 List<AccountEngineRule> getListAERuleBySysDflt(String type);

}