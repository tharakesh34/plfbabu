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
 * * FileName : SecurityUserAccessDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityUserAccess;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;

public interface SecurityUserAccessDAO {

	public void saveDivisionBranches(List<SecurityUserAccess> list);

	public void deleteDivisionBranchesByUser(long userId, String category);

	List<Branch> getBranches();

	List<Cluster> getClusters(String entity, String clusterType, Long clusterId);

	void saveDivBranches(List<SecurityUserDivBranch> list);

	void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch);

	public List<SecurityUserAccess> getSecUserAccessByClusterId(Long clusterId);

	public void deleteDivisionBranches(String branchCode, long userId, String userDivision);

	void deleteDivisionByAccessType(SecurityUserDivBranch branch);

}
