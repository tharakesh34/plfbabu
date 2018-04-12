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
 *
 * FileName    		:  SecurityRightDAO.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityUser;

public interface SecurityRightDAO {
	/**
	 * Get the menu rights for the specified user.
	 * 
	 * @param user
	 *            The model object that contains the parameters.
	 * @return The menu rights of the specified user.
	 */
	List<SecurityRight> getMenuRightsByUser(SecurityUser user);

	/**
	 * Get the rights that the user has for a specified page.
	 * 
	 * @param right
	 *            The model object that contains the parameters.
	 * @return The rights that the user has for a specified page.
	 */
	List<SecurityRight> getPageRights(SecurityRight right);

	/**
	 * Check whether right is exists with the given RightName.
	 * 
	 * @param rightName
	 * @return true if right is exist's with given RightName otherwise false.
	 */
	boolean isRightNameExists(String rightName);

	/**
	 * Inserts the given right in the SecRights.
	 * 
	 * @param right
	 * @return rightId
	 */
	long save(SecurityRight right);

}
