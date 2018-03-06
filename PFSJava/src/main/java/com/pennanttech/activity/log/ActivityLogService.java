package com.pennanttech.activity.log;

import java.util.List;

import com.pennant.backend.model.Notes;

/**
 * Service declaration for methods that depends on <b>Activity</b>.
 */
public interface ActivityLogService {
	/**
	 * Get the list of activities performed on the specified record.
	 * 
	 * @param moduleCode
	 *            Code of the Module.
	 * @param keyValue
	 *            Value of the key column.
	 * 
	 * @return The list of activities performed on the specified record.
	 */
	List<Activity> getActivities(String moduleCode, Object keyValue);

	/**
	 * Get the list of activities performed on the specified record.
	 * 
	 * @param moduleCode
	 *            Code of the Module.
	 * @param keyValue
	 *            Value of the key column.
	 * @param fromAuditId
	 *            Starting activity id of the specified record.
	 * @param toAuditId
	 *            ending activity id of the specified record.
	 * 
	 * @return The list of activities performed on the specified record.
	 */
	List<Activity> getActivities(String moduleCode, Object keyValue, long fromAuditId, long toAuditId);

	/**
	 * Get the list of Remarks on the specified record.
	 * 
	 * @param reference
	 *            Reference of the the specified record.
	 * @param moduleNames
	 *            Name of the Module.
	 * 
	 * @return The list of Remarks.
	 */
	List<Notes> getNotesList(Object reference, List<String> moduleNames);
}
