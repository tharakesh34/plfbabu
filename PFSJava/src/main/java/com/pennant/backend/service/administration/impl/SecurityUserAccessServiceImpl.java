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
 * * FileName : SecurityUserAccessServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.administration.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.administration.SecurityUserAccessDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserAccess;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityUserAccessService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class SecurityUserAccessServiceImpl extends GenericService<SecurityUserAccess>
		implements SecurityUserAccessService {
	private static Logger logger = LogManager.getLogger(SecurityUserServiceImpl.class);

	private SecurityUserAccessDAO securityUserAccessDAO;

	public SecurityUserAccessServiceImpl() {
		super();
	}

	@Override
	public void saveDivisionBranches(SecurityUser user, String method) {
		logger.debug(Literal.ENTERING);

		long userId = user.getUsrID();
		securityUserAccessDAO.deleteDivisionBranchesByUser(userId, "UserAccess");

		if (CollectionUtils.isNotEmpty(user.getSecurityUserDivBranchList())) {
			List<SecurityUserAccess> list = new ArrayList<>();

			for (SecurityUserDivBranch divisionBranch : user.getSecurityUserDivBranchList()) {
				list.add(prepareUserAccess(user, divisionBranch));
			}

			securityUserAccessDAO.saveDivisionBranches(list);

			if ("doApprove".equals(method)) {
				saveDivisionBranches(user, list);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private SecurityUserAccess prepareUserAccess(SecurityUser user, SecurityUserDivBranch divisionBranch) {
		SecurityUserAccess access = new SecurityUserAccess();

		access.setUsrId(user.getUsrID());
		access.setBranch(divisionBranch.getUserBranch());
		access.setDivision(divisionBranch.getUserDivision());
		access.setAccessType(divisionBranch.getAccessType());
		access.setEntity(divisionBranch.getEntity());
		access.setClusterId(divisionBranch.getClusterId());
		access.setClusterType(divisionBranch.getClusterType());
		access.setParentCluster(divisionBranch.getParentCluster());
		access.setLastMntBy(user.getLastMntBy());
		access.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		access.setVersion(1);
		access.setRecordStatus("Approved");
		access.setRoleCode("");
		access.setNextRoleCode("");
		access.setTaskId("");
		access.setNextTaskId("");
		access.setWorkflowId(0);
		return access;
	}

	@Override
	public void saveDIvisionBranchesByMode(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		List<SecurityUserAccess> list = new ArrayList<>();
		for (SecurityUserDivBranch division : user.getSecurityUserDivBranchList()) {
			switch (division.getRecordType()) {
			case PennantConstants.RCD_ADD:
				list.add(prepareUserAccess(user, division));
				break;
			case PennantConstants.RCD_DEL:
				securityUserAccessDAO.deleteDivisionByAccessType(division);
				break;
			default:
				break;
			}
		}

		securityUserAccessDAO.saveDivisionBranches(list);

		logger.debug(Literal.LEAVING);
	}

	private void saveDivisionBranches(SecurityUser user, List<SecurityUserAccess> list) {
		long userId = user.getUsrID();
		long lastMntBy = user.getLastMntBy();
		Timestamp lastMntOn = new Timestamp(System.currentTimeMillis());
		securityUserAccessDAO.deleteDivisionBranchesByUser(userId, null);

		List<Branch> branches = securityUserAccessDAO.getBranches();

		// List<Cluster> clusters1 = securityUserAccessDAO.getClusters();

		Map<String, List<Cluster>> clusterMap = new HashMap<>();

		List<SecurityUserDivBranch> divBranches = new ArrayList<>();

		SecurityUserDivBranch divBranch;
		for (SecurityUserAccess access : list) {
			if (PennantConstants.ACCESSTYPE_ENTITY.equals(access.getAccessType())) {
				for (Branch branch : branches) {
					if (StringUtils.equals(branch.getEntity(), access.getEntity())) {
						divBranch = new SecurityUserDivBranch();
						divBranch.setUsrID(userId);
						divBranch.setUserDivision(access.getDivision());
						divBranch.setUserBranch(branch.getBranchCode());
						divBranch.setLastMntBy(lastMntBy);
						divBranch.setLastMntOn(lastMntOn);

						divBranch.setVersion(1);
						divBranch.setRecordStatus("Approved");
						divBranch.setRoleCode("");
						divBranch.setNextRoleCode("");
						divBranch.setTaskId("");
						divBranch.setNextTaskId("");
						divBranch.setWorkflowId(0);

						divBranches.add(divBranch);
					}
				}
			} else if (PennantConstants.ACCESSTYPE_CLUSTER.equals(access.getAccessType())) {
				String key = access.getEntity() + access.getClusterType() + String.valueOf(access.getClusterId());

				List<Cluster> clusters = clusterMap.get(key);

				if (clusters == null) {
					clusters = getClusters(access.getEntity(), access.getClusterType(), access.getClusterId());
					clusterMap.put(key, clusters);
				}

				for (Branch branch : branches) {
					String entity1 = branch.getEntity();
					String entity2 = access.getEntity();

					if (!StringUtils.equals(entity1, entity2)) {
						continue;
					}

					for (Cluster cluster : clusters) {
						entity1 = cluster.getEntity();
						entity2 = access.getEntity();

						if (!StringUtils.equals(cluster.getEntity(), access.getEntity())) {
							continue;
						}

						if (cluster.getId() == null || branch.getClusterId() == null) {
							continue;
						}

						long clusterId = cluster.getId().longValue();
						long branchClusterId = branch.getClusterId().longValue();

						if (clusterId != branchClusterId) {
							continue;
						}

						divBranch = new SecurityUserDivBranch();
						divBranch.setUsrID(userId);
						divBranch.setUserDivision(access.getDivision());
						divBranch.setUserBranch(branch.getBranchCode());
						divBranch.setLastMntBy(lastMntBy);
						divBranch.setLastMntOn(lastMntOn);

						divBranch.setVersion(1);
						divBranch.setRecordStatus("Approved");
						divBranch.setRoleCode("");
						divBranch.setNextRoleCode("");
						divBranch.setTaskId("");
						divBranch.setNextTaskId("");
						divBranch.setWorkflowId(0);

						divBranches.add(divBranch);
					}
				}

			} else if (PennantConstants.ACCESSTYPE_BRANCH.equals(access.getAccessType())) {
				for (Branch branch : branches) {
					if (StringUtils.equals(branch.getBranchCode(), access.getBranch())) {
						divBranch = new SecurityUserDivBranch();
						divBranch.setUsrID(userId);
						divBranch.setUserDivision(access.getDivision());
						divBranch.setUserBranch(branch.getBranchCode());
						divBranch.setLastMntBy(lastMntBy);
						divBranch.setLastMntOn(lastMntOn);

						divBranch.setVersion(1);
						divBranch.setRecordStatus("Approved");
						divBranch.setRoleCode("");
						divBranch.setNextRoleCode("");
						divBranch.setTaskId("");
						divBranch.setNextTaskId("");
						divBranch.setWorkflowId(0);

						divBranches.add(divBranch);
					}
				}

			}

		}

		if (CollectionUtils.isNotEmpty(divBranches)) {
			securityUserAccessDAO.saveDivBranches(divBranches);
		}

	}

	private List<Cluster> getClusters(String entity, String clusterType, Long clusterId) {
		return securityUserAccessDAO.getClusters(entity, clusterType, clusterId);
	}

	public void setSecurityUserAccessDAO(SecurityUserAccessDAO securityUserAccessDAO) {
		this.securityUserAccessDAO = securityUserAccessDAO;
	}

	@Override
	public void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch) {
		securityUserAccessDAO.deleteDivBranchDetails(securityUserDivBranch);

	}

}
