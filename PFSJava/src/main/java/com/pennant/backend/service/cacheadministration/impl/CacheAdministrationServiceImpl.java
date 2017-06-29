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
 * FileName    		:  CacheAdministrationServiceImpl.java                                 	* 	  
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

package com.pennant.backend.service.cacheadministration.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.cacheadministration.CacheDAO;
import com.pennant.backend.service.cacheadministration.CacheAdministrationService;
import com.pennanttech.cache.CacheStats;


public class CacheAdministrationServiceImpl implements CacheAdministrationService {
	private static final Logger		logger	= Logger.getLogger(CacheAdministrationServiceImpl.class);
	
	@Autowired
	private CacheDAO cacheDAO;

	public CacheAdministrationServiceImpl() {
		super();
	}
	
	@Override
	public void delete(String clusterName,String IP,String currentNode) {
		cacheDAO.delete(clusterName,IP,currentNode);
		
	}

	@Override
	public CacheStats getCacheStats() {
			cacheDAO.getCacheList();
			return null;
	}

	@Override
	public int getNodeCount() {
		return cacheDAO.getNodeCount();		
	}

	@Override
	public void insert(CacheStats cacheStats) {
		cacheDAO.insert(cacheStats);
		
	}

	@Override
	public CacheStats getCacheStats(String clusterName, String currentNode) {
		return cacheDAO.getCacheStats(clusterName, currentNode);
	}

	@Override
	public void update(CacheStats cacheStats) {
		cacheDAO.update(cacheStats);
		
	}

	@Override
	public Map<String, Object> getCacheParameters() {
		return cacheDAO.getCacheParameters();
	}	

}
