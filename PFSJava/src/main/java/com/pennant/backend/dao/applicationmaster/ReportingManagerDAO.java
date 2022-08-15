package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.pennant.backend.model.administration.ReportingManager;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.TableType;

public interface ReportingManagerDAO {

	/**
	 * Fetch the Record Cluster by key field
	 * 
	 * @param clusterId clusterId of the Cluster.
	 * @param tableType The type of the table.
	 * @return Cluster
	 */
	ReportingManager getReportingManager(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param clusterId clusterId of the Cluster.
	 * @param entity    entity of the Cluster.
	 * @param code      code of the Cluster.
	 * @param tableType The type of the table.
	 * @return true if the record exists.
	 */

	List<ReportingManager> getReportingManagers(long id, String type);

	/**
	 * Saves the record. If required, this will generate the identity sequence number for the record before saving.
	 * 
	 * @param entity    The model object that contains the parameters.
	 * @param tableType The type of the table.
	 * @return Identity sequence number as string or primary key code of the saved record.
	 * @throws DataAccessException If there is any problem issuing the save.
	 */
	String save(ReportingManager entity, String tableType);

	/**
	 * Updates the record.
	 * 
	 * @param entity    The model object that contains the parameters.
	 * @param tableType The type of the table.
	 * @throws ConcurrencyException If failure due to concurrency.
	 * @throws DataAccessException  If there is any problem issuing the update.
	 */
	void update(ReportingManager entity, TableType tableType);

	boolean isDuplicateKey(ReportingManager entity, TableType tableType);

	void deleteByUserId(long usrID, TableType tableType);

	void deleteById(long ID, TableType tableType);
}
