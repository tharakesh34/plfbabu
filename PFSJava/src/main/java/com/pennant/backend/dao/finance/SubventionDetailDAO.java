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
 * * FileName : SubventionDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * * Modified
 * Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;

public interface SubventionDetailDAO extends BasicCrudDao<SubventionDetail> {

	SubventionDetail getSubventionDetail(long finID, String type);

	long save(SubventionScheduleDetail scheduleDetail, String type);

	SubventionScheduleDetail getSubvenScheduleDetail(SubventionScheduleDetail subVenschedule, String type);

	void deleteByFinReference(String finReference, String type);

	List<SubventionScheduleDetail> getSubventionScheduleDetails(String finReference, long disbSeqID, String type);

	void updateSubVebtionAmt(String finReference, BigDecimal totalSubVentionAmt);

	public BigDecimal getTotalSubVentionAmt(String finReference);

}