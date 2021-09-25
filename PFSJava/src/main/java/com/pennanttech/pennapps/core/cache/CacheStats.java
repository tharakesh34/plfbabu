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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CacheStats implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String clusterName;
	private int clusterSize = 0;
	private String clusterNode;
	private String currentNode;
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
	private long verifySleepTime;
	private long updateSleepTime;

	private List<String> cacheNames = new ArrayList<String>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("Cluster Name   : ");
		builder.append(getClusterName());

		builder.append("\n");
		builder.append("Current Node   : ");
		builder.append(getClusterNode());

		builder.append("\n");
		builder.append("Cluster IP     : ");
		builder.append(getClusterIp());

		builder.append("\n");
		builder.append("Cluster Size   : ");
		builder.append(getClusterSize());

		builder.append("\n");
		builder.append("Cluster Members: ");
		builder.append(getClusterMembers());

		builder.append("Manager Cache Status    : ");
		builder.append(getManagerCacheStatus());
		builder.append("\n");

		builder.append("\n");
		builder.append("Cache Count    : ");
		builder.append(getCacheCount());

		for (String cacheName : cacheNames) {
			builder.append("\n");
			builder.append("Cache Name     : ");
			builder.append(cacheName);
		}

		builder.append("\n");
		builder.append("Enabled    : ");
		builder.append(isEnabled());
		builder.append("\n");
		builder.append("Active    : ");
		builder.append(isActive());

		return builder.toString();
	}

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public long getVerifySleepTime() {
		return verifySleepTime;
	}

	public void setVerifySleepTime(long verifySleepTime) {
		this.verifySleepTime = verifySleepTime;
	}

	public long getUpdateSleepTime() {
		return updateSleepTime;
	}

	public void setUpdateSleepTime(long updateSleepTime) {
		this.updateSleepTime = updateSleepTime;
	}
}
