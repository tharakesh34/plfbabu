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
 * FileName    		:  SecurityUserDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *30-07-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;

public interface SecurityUserDAO {

	public SecurityUser getSecurityUser();

	public SecurityUser getNewSecurityUser();

	public SecurityUser getSecurityUserById(long id, String type);

	public void update(SecurityUser secUser, String type);

	public void delete(SecurityUser secUser, String type);

	public long save(SecurityUser secUser, String type);

	public void initialize(SecurityUser secUser);

	public void refresh(SecurityUser secUser);

	public void changePassword(SecurityUser secUser);

	public SecurityUser getSecurityUserByLogin(final String id, String type);
	
	public long saveDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	
	public void updateDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	
	public void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	
	public List<SecurityUserDivBranch> getSecUserDivBrList(long usrID,String type);
	
	public SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch securityUserDivBranch, String type);
	
	public void deleteBranchs(SecurityUser securityUser,String type) ;
}
