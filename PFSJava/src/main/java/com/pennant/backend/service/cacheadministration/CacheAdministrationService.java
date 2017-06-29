package com.pennant.backend.service.cacheadministration;



import java.util.Map;

import com.pennanttech.cache.CacheStats;

public interface CacheAdministrationService {
	void delete(String clusterName,String IP,String currentNode);
	CacheStats getCacheStats();
	int getNodeCount();
	void insert(CacheStats cacheStats);		
	CacheStats getCacheStats(String clusterName,String currentNode);
	void update(CacheStats cacheStats);	
	Map<String, Object> getCacheParameters();	
}
