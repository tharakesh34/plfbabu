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

	SecurityUser getSecurityUser();
	SecurityUser getNewSecurityUser();
	SecurityUser getSecurityUserById(long id, String type);
	void update(SecurityUser secUser, String type);
	void delete(SecurityUser secUser, String type);
	long save(SecurityUser secUser, String type);
	void changePassword(SecurityUser secUser);
	SecurityUser getSecurityUserByLogin(final long id, String type);
	long saveDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	void updateDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch,String type);
	List<SecurityUserDivBranch> getSecUserDivBrList(long usrID,String type);
	SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch securityUserDivBranch, String type);
	void deleteBranchs(SecurityUser securityUser,String type) ;
}
