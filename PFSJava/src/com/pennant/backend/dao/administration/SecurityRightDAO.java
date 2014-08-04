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

	public SecurityRight getSecurityRight();

	public SecurityRight getNewSecurityRight();

	public SecurityRight getSecurityRightByID(long id, String type);

	public List<SecurityRight> getAllRights(int type);

	public List<SecurityRight> getAllRights();

	public int getCountAllSecurityRights();

	public SecurityRight getRightById(Long right_id);

	public List<SecurityRight> getRightsByUser(SecurityUser user);

	public List<SecurityRight> getAllRights(List<Integer> aListOfRightTyps);

	public List<SecurityRight> getRightsLikeRightName(String aRightName);

	public List<SecurityRight> getRightsLikeRightNameAndType(String aRightName, int aRightType);

	public List<SecurityRight> getRightsLikeRightNameAndTypes(String aRightName,
	        List<Integer> listOfRightTyps);

	public void update(SecurityRight secRight, String type);

	public void delete(SecurityRight secRight, String type);

	public long save(SecurityRight secRight, String type);

	public List<SecurityRight> getMenuRightsByUser(SecurityUser user);

	public List<SecurityRight> getPageRights(SecurityRight secRight, String menuRightName);

	public List<SecurityRight> getRoleRights(SecurityRight secRight, String menuRightName);

	public void initialize(SecurityRight secRight);

	public void refresh(SecurityRight entity);

	public SecurityRight getSecurityRightByRightName(final String rightName, String type);

	public List<SecurityRight> getRoleRights(SecurityRight secRight, String[] roles);
}
