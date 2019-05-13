package com.pennant.backend.dao.impl;

import org.springframework.dao.DataAccessException;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pff.core.TableType;

public interface BasicCrudDao<T> {
	/**
	 * Saves the record. If required, this will generate the identity sequence number for the record before saving.
	 * 
	 * @param entity
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @return Identity sequence number as string or primary key code of the saved record.
	 * @throws DataAccessException
	 *             If there is any problem issuing the save.
	 */
	String save(T entity, TableType tableType);

	/**
	 * Updates the record.
	 * 
	 * @param entity
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @throws ConcurrencyException
	 *             If failure due to concurrency.
	 * @throws DataAccessException
	 *             If there is any problem issuing the update.
	 */
	void update(T entity, TableType tableType);

	/**
	 * Deletes the record.
	 * 
	 * @param entity
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @throws DependencyFoundException
	 *             If there are any dependencies for the record.
	 * @throws ConcurrencyException
	 *             If failure due to concurrency.
	 * @throws DataAccessException
	 *             If there is any problem issuing the delete.
	 */
	void delete(T entity, TableType tableType);
}
