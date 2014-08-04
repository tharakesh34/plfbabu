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
 * FileName    		:  FinTypeAccountDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.rmtmasters.FinTypeAccount;

/**
 * DAO methods declaration for the <b>FinTypeAccount model</b> class.<br>
 * 
 */
public interface FinTypeAccountDAO {

	public FinTypeAccount getFinTypeAccount();
	public FinTypeAccount getNewFinTypeAccount();
	public FinTypeAccount getFinTypeAccountByID(FinTypeAccount finTypeAccount, String type); 
	public List<FinTypeAccount> getFinTypeAccountListByID(final String id, String type); 
	public void update(FinTypeAccount finTypeAccount, String type);
	public String save(FinTypeAccount finTypeAccount, String type);
	public void delete(FinTypeAccount finTypeAccount, String type);
	public void deleteByFinType(String finType, String type); 
	public void initialize(FinTypeAccount finTypeAccount);
	public void refresh(FinTypeAccount entity);

}