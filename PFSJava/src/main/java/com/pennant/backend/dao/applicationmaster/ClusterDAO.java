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
 * * FileName : ClusterDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * * Modified Date :
 * 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennanttech.pff.core.TableType;

public interface ClusterDAO extends BasicCrudDao<Cluster> {

	/**
	 * Fetch the Record Cluster by key field
	 * 
	 * @param clusterId clusterId of the Cluster.
	 * @param tableType The type of the table.
	 * @return Cluster
	 */
	Cluster getCluster(long clusterId, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param clusterId clusterId of the Cluster.
	 * @param entity    entity of the Cluster.
	 * @param code      code of the Cluster.
	 * @param tableType The type of the table.
	 * @return true if the record exists.
	 */
	List<ClusterHierarchy> getClusterHierarcheyList(String entity);

	boolean isDuplicateKey(long clusterId, String entity, String code, TableType tableType);

	List<Cluster> getClustersByEntity(String entity);

	boolean isChildsExists(Cluster cluster);

	long getClusterByCode(String code, String clusterType, String entity, String type);

	boolean isExsistClusterType(String entity, String clusterType);

	Long getClustersFilter(String branchCode);

	List<String> getClusterCodes(String ClusterType, String entity);

	boolean isValidClusterCode(String clusterCode, String entity);
}