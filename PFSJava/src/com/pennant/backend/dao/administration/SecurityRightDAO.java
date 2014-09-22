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

	SecurityRight getSecurityRight();
	SecurityRight getNewSecurityRight();
	SecurityRight getSecurityRightByID(long id, String type);
	List<SecurityRight> getAllRights(int type);
	List<SecurityRight> getAllRights();
	int getCountAllSecurityRights();
	SecurityRight getRightById(Long rightID);
	List<SecurityRight> getRightsByUser(SecurityUser user);
	List<SecurityRight> getAllRights(List<Integer> aListOfRightTyps);
	List<SecurityRight> getRightsLikeRightName(String aRightName);
	List<SecurityRight> getRightsLikeRightNameAndType(String aRightName, int aRightType);
	List<SecurityRight> getRightsLikeRightNameAndTypes(String aRightName,
	        List<Integer> listOfRightTyps);
	void update(SecurityRight secRight, String type);
	void delete(SecurityRight secRight, String type);
	long save(SecurityRight secRight, String type);
	List<SecurityRight> getMenuRightsByUser(SecurityUser user);
	List<SecurityRight> getPageRights(SecurityRight secRight, String menuRightName);
	List<SecurityRight> getRoleRights(SecurityRight secRight, String menuRightName);
	void initialize(SecurityRight secRight);
	void refresh(SecurityRight entity);
	SecurityRight getSecurityRightByRightName(final String rightName, String type);
	List<SecurityRight> getRoleRights(SecurityRight secRight, String[] roles);
}
