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
 * * FileName : FinODDetailsDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-05-2012 * * Modified Date
 * : 08-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinODDetails;

/**
 * DAO methods declaration for the <b>FinODDetails model</b> class.<br>
 * 
 */
public interface FinODDetailsDAO {

	void update(FinODDetails finOdDetails);

	int getPendingOverDuePayment(long finID);

	void updateTotals(FinODDetails detail);

	void updateTotals(List<FinODDetails> list);

	void updateLatePftTotals(List<FinODDetails> list);

	void resetTotals(FinODDetails detail);

	int getFinODDays(long finID);

	void updateBatch(FinODDetails finOdDetails);

	int getMaxODDaysOnDeferSchd(long finID, List<Date> pastdueDefDateList);

	FinODDetails getMaxDaysFinODDetails(long finID);

	void updatePenaltyTotals(FinODDetails detail);

	FinODDetails getTotals(long finID);

	FinODDetails getFinODSummary(long finID);

	BigDecimal getTotalPenaltyBal(long finID, List<Date> presentmentDates);

	// Receipts
	List<FinODDetails> getFinODBalByFinRef(long finID);

	void updateLatePftTotals(long finID, Date odSchDate, BigDecimal paidNow, BigDecimal waivedNow);

	void updateReversals(long finID, Date odSchDate, BigDecimal penaltyPaid, BigDecimal latePftPaid);

	FinODDetails getFinODDetailsForBatch(long finID, Date schdDate);

	int getFinCurSchdODDays(long finID, Date finODSchdDate);

	void updateList(List<FinODDetails> overdues);

	// EOD
	List<FinODDetails> getFinODDByFinRef(long finID, Date odSchdDate);

	int saveList(List<FinODDetails> finOdDetails);

	List<FinODDetails> getFinODPenalityByFinRef(long finID, boolean ispft, boolean isRender);

	void updateWaiverAmount(long finID, Date odDate, BigDecimal waivedAmount, BigDecimal penAmount);

	List<FinODDetails> getCustomerDues(long custId);

	int updateODDetailsBatch(List<FinODDetails> overdues);

	FinODDetails getFinODByFinRef(long finID, Date schDate);

	List<FinODDetails> getFinODDetailsByFinRef(long finID);

	void updatePaidPenalties(List<FinODDetails> overdues);

	void updateFinODTotals(List<FinODDetails> list);

	BigDecimal getOverDueAmount(long finID);

	void delete(long finID);

	List<FinODDetails> getLPPDueAmount(long finID);
}