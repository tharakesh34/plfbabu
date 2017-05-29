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
 * FileName    		:  CustomerTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>CustomerType model</b> class.<br>
 * 
 */
public interface CustomerTypeDAO extends BasicCrudDao<CustomerType>  {

	CustomerType getCustomerTypeById(String id,String type);
	int validateTypeAndCategory(String custTypeCode, String custCtgCode);
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param customerTypeCode
	 *            of CustomerTypeDAO
	 * @param tableType
	 *            of CustomerTypeDAO
	 * @return
	 */
	boolean isDuplicateKey(String customerTypeCode, TableType tableType);
	

}