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
package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer for <code>Academic</code> with set of CRUD operations.
 * 
 * @param <T>
 */
public interface AcademicDAO extends BasicCrudDao<Academic> {
	Academic getAcademicById(long academicID, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            Id of the academic.
	 * @param level
	 *            Level of the academic.
	 * @param discipline
	 *            Discipline of the academic.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String level, String discipline, TableType tableType);
}
