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
 * FileName    		:  BasicFinanceTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.model.rmtmasters.BasicFinanceType;

/**
 * DAO methods declaration for the <b>BasicFinanceType model</b> class.<br>
 * 
 */
public interface BasicFinanceTypeDAO {

	 BasicFinanceType getBasicFinanceType();
	 BasicFinanceType getNewBasicFinanceType();
	 BasicFinanceType getBasicFinanceTypeById(String id,String type);
	 void update(BasicFinanceType basicFinanceType,String type);
	 void delete(BasicFinanceType basicFinanceType,String type);
	 String save(BasicFinanceType basicFinanceType,String type);
	 void initialize(BasicFinanceType basicFinanceType);
	 void refresh(BasicFinanceType entity);

}