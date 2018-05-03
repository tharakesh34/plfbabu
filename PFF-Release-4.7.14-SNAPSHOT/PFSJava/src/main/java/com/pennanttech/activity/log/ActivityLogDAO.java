package com.pennanttech.activity.log;

import java.util.List;

public interface ActivityLogDAO {
	/**
	 * Get the list of activities performed on the specified record.
	 * 
	 * @param tableName
	 *            Name of the table.
	 * @param keyColumn
	 *            Name of the key column.
	 * @param keyValue
	 *            Value of the key column.
	 * 
	 * @return The list of activities performed on the specified record.
	 */
	List<Activity> getActivities(String tableName, String keyColumn, Object keyValue);

	/**
	 * Get the list of activities performed on the specified record.
	 * 
	 * @param tableName
	 *            Name of the table.
	 * @param keyColumn
	 *            Name of the key column.
	 * @param keyValue
	 *            Value of the key column.
	 * @param fromAuditId
	 *            Starting activity id of the specified record.
	 * @param toAuditId
	 *            ending activity id of the specified record.
	 * 
	 * @return The list of activities performed on the specified record.
	 */
	List<Activity> getActivities(String tableName, String keyColumn, Object keyValue, long fromAuditId, long toAuditId);
}
