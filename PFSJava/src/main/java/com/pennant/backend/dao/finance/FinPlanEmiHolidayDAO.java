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
 * * FileName : FinPlanEmiHolidayDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-10-2011 * * Modified
 * Date : 13-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinPlanEmiHoliday;

/**
 * DAO methods declaration for the <b>FinPlanEmiHolidayDAO</b> class.<br>
 */
public interface FinPlanEmiHolidayDAO {

	List<Integer> getPlanEMIHMonthsByRef(long finID, String type);

	List<Date> getPlanEMIHDatesByRef(long finID, String type);

	void deletePlanEMIHMonths(long finID, String type);

	void savePlanEMIHMonths(List<FinPlanEmiHoliday> planEMIHMonths, String type);

	void deletePlanEMIHDates(long finID, String type);

	void savePlanEMIHDates(List<FinPlanEmiHoliday> planEMIHDates, String type);
}