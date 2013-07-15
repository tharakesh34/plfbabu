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
 * FileName    		:  SecurityGroupRightsDAO.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  2-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  2-08-2011	     Pennant	                 0.1                                        * 
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

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;

public interface SecurityGroupRightsDAO {

	public List<SecurityGroupRights> getSecurityGroupRightsByGrpId(SecurityGroup securityGroup);

	public SecurityGroupRights getGroupRightsByGrpAndRightIds(long grpId, long rightId);

	public void save(SecurityGroupRights securityGroupRights);

	public void delete(SecurityGroupRights securityGroupRights);

	public int getGroupIdCount(long GroupId);

	public int getRightIdCount(long RightID);

	public List<SecurityRight> getRightsByGroupId(long grpID, boolean isAssigned);

	public SecurityGroupRights getSecurityGroupRights();

}
