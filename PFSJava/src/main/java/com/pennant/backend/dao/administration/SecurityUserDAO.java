/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SecurityUserDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified Date
 * : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Entity;

public interface SecurityUserDAO {

	SecurityUser getSecurityUserById(long id, String type);

	void update(SecurityUser secUser, String type);

	void delete(SecurityUser secUser, String type);

	long save(SecurityUser secUser, String type);

	void changePassword(SecurityUser secUser);

	SecurityUser getSecurityUserByLogin(String usrLogin, String type);

	long saveDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type);

	void updateDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type);

	void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch, String type);

	List<SecurityUserDivBranch> getSecUserDivBrList(long usrID, String type);

	SecurityUserDivBranch getSecUserDivBrDetailsById(SecurityUserDivBranch securityUserDivBranch, String type);

	void deleteBranchs(SecurityUser securityUser, String type);

	int getActiveUsersCount(long userId);

	int getActiveUsersCount();

	long getUserByName(String userName);

	List<Entity> getEntityList(String entity);

	void saveDivBranchDetails(List<SecurityUserDivBranch> securityUserDivBranchList, String type);

	List<SecurityUser> getSecUsersByRoles(String[] roles);

	SecurityUser getSecurityUserAccessToAllBranches(long id);

	void lockUserAccounts();

	void markAsDelete(SecurityUser securityUser, String type);

	List<SecurityUser> getDisableUserAccounts();

	void updateDisableUser(List<SecurityUser> userAccounts);

	boolean isUserExist(String usrLogin);

	void updateUserStatus(SecurityUser user);

	List<String> getLovFieldCodeValues(String lovFieldCode);

	boolean isexisitvertical(long code);

	boolean isexisitBranchCode(String branchCode);

	boolean isDepartmentExsist(String deptCode);

	boolean getDesignationCount(String usrDesg);

	List<SecurityUserDivBranch> getDivisionsByAccessType(SecurityUserDivBranch branch);

}
