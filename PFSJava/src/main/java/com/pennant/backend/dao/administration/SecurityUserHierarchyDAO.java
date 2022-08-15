/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, withodeleteUserHierarchyut the prior written consent of the copyright
 * holder, is a violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SecurityUserHierarchyDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUserHierarchy;

public interface SecurityUserHierarchyDAO {

	public void deleteUserHierarchy(long userId);

	public void deleteUserHierarchy(SecurityUserHierarchy securityUserHierarchy);

	public void saveUserHierarchy(List<SecurityUserHierarchy> securityUserHierarchy);

	public List<SecurityUserHierarchy> getDownLevelUsers(SecurityUserHierarchy userHierarchy);

	public List<SecurityUserHierarchy> getUpLevelUsers(SecurityUserHierarchy userHierarchy);

	public void getUpLevelUser(SecurityUserHierarchy userHierarchy);

	public void updateUserHierarchy(SecurityUserHierarchy userHierarchy);

	public void updateUserHierarchy(SecurityUserHierarchy userHierarchy, ReportingManager reportingTo);

}
