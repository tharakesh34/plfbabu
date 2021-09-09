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
 * * FileName : VasMovementDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * * Modified
 * Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.VasMovementDetail;

public interface VasMovementDetailDAO {

	List<VasMovementDetail> getVasMovementDetailById(long id, String type);

	void update(VasMovementDetail checkListDetail, String type);

	void delete(VasMovementDetail checkListDetail, String type);

	void delete(long checkListId, String type);

	long save(VasMovementDetail checkListDetail, String type);

	BigDecimal getVasMovementDetailByRef(String finReference, Date finStartDate, Date finEndDate, String type);

}