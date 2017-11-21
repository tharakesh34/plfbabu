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
 * FileName    		:  CacheDAO.java                                                   		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2017    														*
 *                                                                  						*
 * Modified Date    :  27-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2017       Pennant	                 0.1                                            * 
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


package com.pennant.backend.dao.cacheadministration;

import java.util.List;
import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.cache.CacheStats;

public interface CacheDAO extends BasicCrudDao<CacheStats> {
	List<CacheStats> getCacheList();
	void delete(String clusterName,String IP,String currentNode);
	int getNodeCount();
	void insert(CacheStats cacheStats);
	CacheStats getCacheStats(String clusterName, String currentNode);
	void update(CacheStats cacheStats);
	Map<String, Object> getCacheParameters();
}
