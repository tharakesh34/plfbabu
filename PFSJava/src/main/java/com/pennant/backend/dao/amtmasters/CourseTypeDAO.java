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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CourseTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * * Modified Date :
 * 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.amtmasters;

import com.pennant.backend.model.amtmasters.CourseType;

public interface CourseTypeDAO {
	CourseType getCourseTypeById(String id, String type);

	void update(CourseType courseType, String type);

	void delete(CourseType courseType, String type);

	String save(CourseType courseType, String type);
}