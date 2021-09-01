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
 * * FileName : VasMovementDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * * Modified Date
 * : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.model.finance.VasMovement;

public interface VasMovementDAO {

	VasMovement getVasMovementById(long finID, String type);

	void update(VasMovement vasMovement, String type);

	void delete(VasMovement vasMovement, String type);

	long save(VasMovement vasMovement, String type);
}