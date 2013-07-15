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
 * FileName    		:  MenuDetailsServiceImpl.java											*                           
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

package com.pennant.backend.service.impl;

import java.util.List;

import com.pennant.backend.dao.MenuDetailsDAO;
import com.pennant.backend.model.MenuDetails;
import com.pennant.backend.service.MenuDetailsService;

/**
 * Service implementation for methods that depends on <b>Customers</b>.<br>
 * 
 */
public class MenuDetailsServiceImpl implements MenuDetailsService {

	private MenuDetailsDAO MenuDetailsDAO;

	/**
	 * @return the menuDetailsDAO
	 */
	public MenuDetailsDAO getMenuDetailsDAO() {
		return MenuDetailsDAO;
	}

	/**
	 * @param menuDetailsDAO the menuDetailsDAO to set
	 */
	public void setMenuDetailsDAO(MenuDetailsDAO menuDetailsDAO) {
		MenuDetailsDAO = menuDetailsDAO;
	}

	/**
	 * @param appCode
	 * @return
	 * @see de.forsthaus.backend.dao.MenuDetailsDAO#getMenuDetailsByApp(java.lang.String)
	 */
	public List<MenuDetails> getMenuDetailsByApp(String appCode) {
		return MenuDetailsDAO.getMenuDetailsByApp(appCode);
	}


}
