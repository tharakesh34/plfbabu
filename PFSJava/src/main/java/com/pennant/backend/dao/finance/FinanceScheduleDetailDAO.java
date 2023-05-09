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
 * * FileName : WIFFinanceScheduleDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennanttech.pff.core.TableType;

public interface FinanceScheduleDetailDAO {

	FinanceScheduleDetail getFinanceScheduleDetailById(long finID, Date schdDate, String type, boolean isWIF);

	void deleteByFinReference(long finID, String type, boolean isWIF, long logKey);

	void save(FinanceScheduleDetail schedule, String type, boolean isWIF);

	List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, boolean isWIF, long logKey);

	List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, boolean isWIF);

	List<FinanceScheduleDetail> getFinSchedules(long finID, TableType tableType);

	void delete(FinanceScheduleDetail schedule, String type, boolean isWIF);

	int updateForRateReview(List<FinanceScheduleDetail> schedules);

	int saveList(List<FinanceScheduleDetail> schedule, String type, boolean isWIF);

	BigDecimal getSuspenseAmount(long finID, Date dateValueDate);

	BigDecimal getTotalRepayAmount(long finID);

	BigDecimal getTotalUnpaidPriAmount(long finID);

	BigDecimal getTotalUnpaidPftAmount(long finID);

	FinanceWriteoff getWriteoffTotals(long finID);

	void updateForRpy(FinanceScheduleDetail schedule);

	Date getFirstRepayDate(long finID);

	List<FinanceScheduleDetail> getFinSchdDetailsForBatch(long finID);

	FinanceScheduleDetail getTotals(long finID);

	boolean getFinScheduleCountByDate(long finID, Date fromDate, boolean isWIF);

	List<FinanceScheduleDetail> getFinScheduleDetails(long Custid, boolean isActive);

	void updateListForRpy(List<FinanceScheduleDetail> schedules);

	BigDecimal getPriPaidAmount(long finID);

	BigDecimal getOutStandingBalFromFees(long finID);

	List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, long logKey);

	void updateTDS(List<FinanceScheduleDetail> schedules);

	// Mandate Registration Request
	List<FinanceScheduleDetail> getFirstRepayAmt(long finID);

	// ## Ticket id:124998(receipt upload) 16/8/2018
	BigDecimal getClosingBalance(long finID, Date valueDate);

	FinanceScheduleDetail getPrvSchd(long finID, Date curBussDate);

	// Ticket id:124998(receipt upload)
	Date getPrevSchdDate(long finID, Date appDate);

	boolean isInstallSchd(long finID, Date lastPrevDate); // ## Ticket id:124998(receipt upload) 16/8/2018

	void updateSchPaid(FinanceScheduleDetail curSchd);

	List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(long finID);

	FinanceMain getFinanceMainForRateReport(long finID, String type);

	boolean isScheduleInQueue(long finID);

	int getDueBucket(long finID);

	List<FinanceScheduleDetail> getDueSchedulesByFacilityRef(long finID, Date valueDate);

	List<FinanceScheduleDetail> getFinScheduleDetailsBySchPriPaid(long finID, String type, boolean isWIF);

	FinanceScheduleDetail getNextUnpaidSchPayment(long finID, Date valueDate);

	void saveSchDueTaxDetail(ScheduleDueTaxDetail dueTaxDetail);

	Long getSchdDueInvoiceID(long finID, Date schdate);

	void updateTDSChange(List<FinanceScheduleDetail> schedules);

	Date getSchdDateForDPD(long finID, Date appDate);

	List<Date> getScheduleDates(long finID, Date valueDate);

	List<FinanceScheduleDetail> getSchedulesForLMSEvent(long finID);

	BigDecimal getUnpaidTdsAmount(String finReference);

	RestructureDetail getRestructureDetail(String finReference);

	FinanceProfitDetail getAmzTillLBD(String finReference);

	void updateDueTaxDetail(long oldInvoiceId);

	List<FinanceScheduleDetail> getFinSchdDetailsBtwDates(String finReference, Date fromdate, Date toDate);

	void updateSchdTotals(List<FinanceScheduleDetail> schdDtls);

	Date getNextSchdDate(long finID, Date appDate);

	Date getSchdDateForKnockOff(long finID, Date appDate);

	FinanceScheduleDetail getNextSchd(long finID, Date appDate, boolean businessDate);

	List<FinanceScheduleDetail> getBasicDetails(long finID);
}
