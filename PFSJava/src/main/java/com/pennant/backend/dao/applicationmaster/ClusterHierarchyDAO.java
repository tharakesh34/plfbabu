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
 * * FileName : ClusterHierarcheyDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * * Modified
 * Date : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennanttech.pff.core.TableType;

public interface ClusterHierarchyDAO extends BasicCrudDao<ClusterHierarchy> {

	/**
	 * Fetch the EntityId by key field
	 * 
	 * @param entity entity of the ClusterHierarchey.
	 * @return Entity ID
	 */
	ClusterHierarchy getClusterHierarcheybyId(String entity, String type);

	/**
	 * Fetch the Record ClusterHierarchey by key field
	 * 
	 * @param entity      entity of the ClusterHierarchey.
	 * @param clusterType clusterType of the ClusterHierarchey.
	 * @param tableType   The type of the table.
	 * @return ClusterHierarchey
	 */
	ClusterHierarchy getClusterHierarchey(String entity, String type);

	/**
	 * Fetch the list of records ClusterHierarchey by key field
	 * 
	 * @param entity      entity of the ClusterHierarchey.
	 * @param clusterType clusterType of the ClusterHierarchey.
	 * @param tableType   The type of the table.
	 * @return ClusterHierarchey
	 */
	List<ClusterHierarchy> getClusterHierarcheyList(String entity, String type);

	boolean isDuplicateKey(String entity, String clustertype, TableType tableType);

	boolean isDuplicateKey(String entity, int seqOrder, TableType tableType);

	boolean isExisitEntity(String entityCode);

	boolean isClusterTypeExists(String clusterType, String entity);
}