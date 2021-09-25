package com.pennanttech.pennapps.core.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.pennanttech.pennapps.core.resource.Literal;

public class CacheMonitor implements Runnable {
	private static final Logger log = LogManager.getLogger(CacheMonitor.class);

	private CacheAdmin cacheAdmin;
	private static int nodeCount = 0;

	public CacheMonitor(CacheAdmin cacheAdmin) {
		super();
		this.cacheAdmin = cacheAdmin;
	}

	@Override
	public void run() {
		log.trace(Literal.ENTERING);

		ThreadContext.put("MODULE", "CACHE");
		CacheStats stats = CacheManager.getNodeDetails();

		String clusterNode = stats.getClusterNode();

		clusterNode = clusterNode.split("-")[0];

		if (stats.getClusterName() != null) {
			try {
				log.info(String.format("Deleting the old status of %s Cluster, %s IP, %s Node", stats.getClusterName(),
						stats.getClusterIp(), clusterNode));
				this.cacheAdmin.delete(stats.getClusterName(), stats.getClusterIp(), clusterNode);
			} catch (Exception e) {
				log.error("Error while deleting the existing cache details / No records to delete");
			}
		}

		while (true) {
			log.info("Running the cache monitor....");

			setNodeCount(this.cacheAdmin.getNodeCount());

			CacheManager.setNodes(nodeCount);

			CacheManager.verifyCache();
			stats = CacheManager.getNodeDetails();

			clusterNode = stats.getClusterNode();

			clusterNode = clusterNode.split("-")[0];

			if (CacheManager.isActivated() && CacheManager.getClusterSize() != CacheManager.getNodes()) {
				log.info("Deleting the Activated cache details, ");
				log.info(" ClusterName %s, ClusterIp %s, Cluster Node %s", stats.getClusterName(), stats.getClusterIp(),
						clusterNode);
				log.info(" Active Status :%s ", CacheManager.isActivated());
				log.info(" Cluster Size : %s ", CacheManager.getClusterSize());
				log.info(" Node Size : %s ", CacheManager.getClusterSize());

				this.cacheAdmin.delete(stats.getClusterName(), stats.getClusterIp(), clusterNode);
			}

			CacheStats existingCache = cacheAdmin.getCacheStats(stats.getClusterName(), clusterNode);

			if (existingCache != null) {
				cacheAdmin.update(stats);
			} else {
				cacheAdmin.insert(stats);
			}

			try {
				Thread.sleep(CacheManager.getSleepTime());
			} catch (InterruptedException e) {
				log.error(Literal.EXCEPTION, e);
			}

			if (CacheManager.getCacheManager() == null) {
				return;
			}

			log.info("Cache monitor is completed....");
		}

	}

	public static void setNodeCount(int nodeCount) {
		CacheMonitor.nodeCount = nodeCount;
	}

}
