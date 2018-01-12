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
package com.pennanttech.pennapps.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CacheStats {
	private String clusterName;
	private int clusterSize = 0;
	private String clusterNode;
	private String clusterIp;
	private String clusterMembers;
	private int cacheCount;
	private String managerCacheStatus;
	private boolean appNode = false;
	private boolean enabled = false;
	private boolean active = false;
	private int nodeCount;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String cacheNamesDet;

	private List<String> cacheNames = new ArrayList<String>();

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public int getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}
		
	public String getClusterNode() {
		return clusterNode;
	}

	public void setClusterNode(String clusterNode) {
		this.clusterNode = clusterNode;
	}

	public String getClusterIp() {
		return clusterIp;
	}

	public void setClusterIp(String clusterIp) {
		this.clusterIp = clusterIp;
	}

	public String getClusterMembers() {
		return clusterMembers;
	}

	public void setClusterMembers(String clusterMembers) {
		this.clusterMembers = clusterMembers;
	}

	public int getCacheCount() {
		return cacheCount;
	}

	public void setCacheCount(int cacheCount) {
		this.cacheCount = cacheCount;
	}

	public List<String> getCacheNames() {
		return cacheNames;
	}

	public void setCacheNames(List<String> cacheNames) {
		if (this.cacheNames != null) {
			this.cacheNames = cacheNames;
		} else {
			this.cacheNames = new ArrayList<String>();
		}
	}

	public void setCacheNames(String cacheName) {
		if (StringUtils.trimToNull(cacheName) != null) {
			this.cacheNames.add(cacheName);
		}
	}
	
	public String getManagerCacheStatus() {
		return managerCacheStatus;
	}

	public void setManagerCacheStatus(String managerCacheStatus) {
		this.managerCacheStatus = managerCacheStatus;
	}

	public boolean isAppNode() {
		return appNode;
	}

	public void setAppNode(boolean currentNode) {
		this.appNode = currentNode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getCacheNamesDet() {
		return cacheNamesDet;
	}

	public void setCacheNamesDet(String cacheNamesDet) {
		this.cacheNamesDet = cacheNamesDet;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Cluster Name   : ");
		buffer.append(getClusterName());

		buffer.append("\n");
		buffer.append("Current Node   : ");
		buffer.append(getClusterNode());

		buffer.append("\n");
		buffer.append("Cluster IP     : ");
		buffer.append(getClusterIp());

		buffer.append("\n");
		buffer.append("Cluster Size   : ");
		buffer.append(getClusterSize());

		buffer.append("\n");
		buffer.append("Cluster Members: ");
		buffer.append(getClusterMembers());

		buffer.append("Manager Cache Status    : ");
		buffer.append(getManagerCacheStatus());
		buffer.append("\n");

		buffer.append("\n");
		buffer.append("Cache Count    : ");
		buffer.append(getCacheCount());

		for (String cacheName : cacheNames) {
			buffer.append("\n");
			buffer.append("Cache Name     : ");
			buffer.append(cacheName);
		}
		buffer.append("\n");
		buffer.append("Enabled    : ");
		buffer.append(isEnabled());
		buffer.append("\n");
		buffer.append("Active    : ");
		buffer.append(isActive());

		return buffer.toString();
	}
}
