package com.pennanttech.pennapps.core.cache;

public class CacheQueries {
	public static final String SELECT_CACHE_STATUS_LIST;
	public static final String SELECT_CACHE_STATUS;
	public static final String INSERT_CACHE_STATUS;
	public static final String UPDATE_CACHE_STATUS;
	public static final String SELECT_CACHE_PARAMETER;
	public static final String DELETE_CACHE_STATUS;
	public static final String SELECT_CACHE_STATUS_NODE_COUNT;
	public static final String UPDATE_CACHE_PARAM;

	private CacheQueries() {
		//
	}

	static {
		SELECT_CACHE_STATUS_LIST = getCacheStatusListSelectQuery();
		SELECT_CACHE_STATUS = getCacheStatusSelectQuery();
		INSERT_CACHE_STATUS = getCacheStatusInSertQuery();
		UPDATE_CACHE_STATUS = getCacheStatusUpdateQuery();
		SELECT_CACHE_PARAMETER = getCacheParameterSelectQuery();
		DELETE_CACHE_STATUS = getCacheStatusDeleteQuery();
		SELECT_CACHE_STATUS_NODE_COUNT = getCacheStatusNodeCountSelectQuery();
		UPDATE_CACHE_PARAM = getCacheParamUpdateQuery();
	}

	private static String getCacheStatusListSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Id, cluster_name, current_node, cluster_ip, cluster_size, cluster_members");
		sql.append(", cache_count, cache_names, manager_cache_status, enabled, active, node_count");
		sql.append(", last_mnt_on, last_mnt_by");
		sql.append(" from cache_stats");
		sql.append(" Order by ID");

		return sql.toString();
	}

	private static String getCacheStatusSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Id, cluster_name, current_node, cluster_ip, cluster_size, cluster_members");
		sql.append(", cache_count, cache_names, manager_cache_status, enabled, active, node_count");
		sql.append(" from cache_stats");
		sql.append(" where cluster_name = ? and current_node like ?");

		return sql.toString();
	}

	private static String getCacheStatusInSertQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into cache_stats");
		sql.append("(cluster_name, current_node, cluster_ip, cluster_size, cluster_members");
		sql.append(", cache_count, cache_names, manager_cache_status, enabled, active, node_count");
		sql.append(", last_mnt_by, last_mnt_on)");
		sql.append(" values(:ClusterName, :ClusterNode, :ClusterIp, :ClusterSize, :ClusterMembers");
		sql.append(", :CacheCount, :CacheNamesDet, :ManagerCacheStatus, :Enabled, :Active, :NodeCount");
		sql.append(", :LastMntBy, :LastMntOn)");

		return sql.toString();
	}

	private static String getCacheStatusUpdateQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("update cache_stats");
		sql.append(" set cluster_ip = :ClusterIp, cluster_size = :ClusterSize, cluster_members = :ClusterMembers");
		sql.append(", cache_count = :CacheCount, cache_names = :CacheNamesDet");
		sql.append(", manager_cache_status = :ManagerCacheStatus");
		sql.append(", enabled = :Enabled, active = :Active,  node_count  = :NodeCount");
		sql.append(", last_mnt_by = :LastMntBy, last_mnt_on = :LastMntOn");
		sql.append(" where cluster_name = :ClusterName and current_node = :ClusterNode ");

		return sql.toString();
	}

	private static String getCacheParameterSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("select node_count, cache_verify_sleep, cache_update_sleep");
		sql.append(" from cache_parameters");

		return sql.toString();
	}

	private static String getCacheStatusDeleteQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from cache_stats");
		sql.append(" where cluster_name =:ClusterName");
		sql.append(" and cluster_ip like :ClusterIp and current_node like :CurrentNode");

		return sql.toString();
	}

	private static String getCacheStatusNodeCountSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("select node_count from cache_stats where id=(select max(id) from cache_stats)");

		return sql.toString();
	}

	private static String getCacheParamUpdateQuery() {
		StringBuilder sql = new StringBuilder("update cache_parameters set");
		sql.append(" node_count = :NodeCount, cache_verify_sleep  = :VerifySleepTime");
		sql.append(", cache_update_sleep = :UpdateSleepTime");
		sql.append(", Last_Mnt_By =:LastMntBy, Last_Mnt_On =:LastMntOn");

		return sql.toString();
	}
}
