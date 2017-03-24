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

import com.pennant.backend.model.systemmasters.Academic;

/**
 * DAO methods declaration for the <b>Academic model</b> class.<br>
 * 
 */
public interface AcademicDAO {

	Academic getAcademicById(long academicID, String type);

	void update(Academic academic, String type);

	void delete(Academic academic, String type);

	long save(Academic academic, String type);

	Academic getAcademic(String academicLevel, String academicDecipline, String type);
}