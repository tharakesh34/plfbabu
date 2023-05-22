package com.pennanttech.pff.overdraft.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface OverdrafLoanService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	OverdraftLimit getLimit(long finID);

	List<OverdraftLimitTransation> getTransactions(long finID);

	boolean isLimitBlock(long finID);

	boolean isAutoBlock(long finID);

	void unBlockLimit(long finID);

	boolean isFullyPaid(long finID, FinODDetails overdue, FinanceScheduleDetail schd);

	/**
	 * <p>
	 * This method will called on approval of the overdraft loan.
	 * </p>
	 * 
	 * <p>
	 * Overdraft limit will be defined, one record will be inserted into <strong>OVERDRAFT_LOAN_LIMITS</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Transaction will be created with narration <strong>Disbursement</strong>, record will be inserted into
	 * <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * @param fm {@link FinanceMain}
	 */
	void createDisbursment(FinanceMain fm);

	/**
	 * <p>
	 * This method will called on approval of tranch disbursement.
	 * </p>
	 * 
	 * <p>
	 * Receivable advise will be created as transaction charges, when loan is opted for transaction charges, record will
	 * be stored in <strong>ManualAdvise</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Transaction charge will be either <string>FIXED/PERCENTAGE<string>, this will be configurable at the time of loan
	 * booking.
	 * </p>
	 * 
	 * <p>
	 * Transaction will be created with narration <strong>Disbursement</strong>, record will be inserted into
	 * <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Log the existing limit balances from <strong>OVERDRAFT_LOAN_LIMITS</strong> table to
	 * <strong>OVERDRAFT_LOAN_LIMITS_LOG</strong> table for the corresponding loan.
	 * </p>
	 * 
	 * <p>
	 * Actual and Monthly limits will be updated in <strong>OVERDRAFT_LOAN_LIMITS</strong> table.
	 * </p>
	 * 
	 * @throws ConcurrencyException if 0 records updated due to concurrency.
	 * 
	 * @param fap {@link FinAdvancePayments}
	 */
	void createDisbursement(FinAdvancePayments fap);

	/**
	 * <p>
	 * This method will called on approval of the receipt either from Screen/EOD (presentment
	 * receipt)/API/Receipt-upload.
	 * </p>
	 * 
	 * <p>
	 * Here available limit will be increase with the sum of Paid <strong>Principle</strong> and
	 * <strong>Receivable-Advice(Transaction Charge)</strong> amount.
	 * <p>
	 * Transaction will be created with narration <strong>Customer Payment</strong>, record will be inserted into
	 * <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Log the existing limit balances from <strong>OVERDRAFT_LOAN_LIMITS</strong> table to
	 * <strong>OVERDRAFT_LOAN_LIMITS_LOG</strong> table for the corresponding loan.
	 * </p>
	 * 
	 * <p>
	 * Actual and Monthly limits will be updated in <strong>OVERDRAFT_LOAN_LIMITS</strong> table.
	 * </p>
	 * 
	 * @throws ConcurrencyException if 0 records updated due to concurrency.
	 * 
	 * @param frh {@link FinReceiptHeader}
	 */
	void createPayment(FinReceiptHeader frh);

	/**
	 * This method will called on approval of fee-waivers.
	 * 
	 * <p>
	 * Here available limit will be increase with the sum of <strong>Waiver</strong> amount of all the waivers.
	 * <p>
	 * Transaction will be created with narration <strong>Transaction Charge Waiver</strong>, record will be inserted
	 * into <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Log the existing limit balances from <strong>OVERDRAFT_LOAN_LIMITS</strong> table to
	 * <strong>OVERDRAFT_LOAN_LIMITS_LOG</strong> table for the corresponding loan.
	 * </p>
	 * 
	 * <p>
	 * Actual and Monthly limits will be updated in <strong>OVERDRAFT_LOAN_LIMITS</strong> table.
	 * </p>
	 * 
	 * @throws ConcurrencyException if 0 records updated due to concurrency.
	 * 
	 * @param fwdList - {@link List<FeeWaiverDetail>}
	 */
	void createWaiver(List<FeeWaiverDetail> fwdList);

	/**
	 * This method will called on approval of the receipt cancellation.
	 * <p>
	 * Here available limit will be increase with the sum of Paid <strong>Principle</strong> and
	 * <strong>Receivable-Advice(Transaction Charge)</strong> amount.
	 * <p>
	 * Transaction will be created with narration <strong>Payment Cancelled/Bounced</strong>, record will be inserted
	 * into <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Log the existing limit balances from <strong>OVERDRAFT_LOAN_LIMITS</strong> table to
	 * <strong>OVERDRAFT_LOAN_LIMITS_LOG</strong> table for the corresponding loan.
	 * </p>
	 * 
	 * <p>
	 * Actual and Monthly limits will be updated in <strong>OVERDRAFT_LOAN_LIMITS</strong> table.
	 * </p>
	 * 
	 * @throws ConcurrencyException if 0 records updated due to concurrency.
	 * @param adviseAmount
	 * @param totalPriAmount
	 * @param fm
	 */
	void cancelPayment(BigDecimal adviseAmount, BigDecimal totalPriAmount, FinanceMain fm);

	/**
	 * This method will be called on Micro-EOD for each customer during installment due date postings.
	 * 
	 *
	 * <p>
	 * Transaction will be created with narration <strong>Monthly Bill</strong>, record will be inserted into
	 * <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table for each overdraft loan of the customer.
	 * </p>
	 * 
	 * @param custEODEvent {@link CustEODEvent}
	 */
	void createBills(CustEODEvent custEODEvent);

	/**
	 * This method will be called on Micro-EOD for each customer.
	 * <p>
	 * If there any penalties exist(<code>FinODPenaltyRates.OverDraftColAmt</code>) for the loan and schedule is not
	 * fully paid, receivable advises will be created for each unpaid schedule with
	 * <code>FinODPenaltyRates.OverDraftColAmt</code>
	 * </p>
	 * 
	 * <p>
	 * Limit will be blocked, when transaction charge is created for any of the unpaid schedule.
	 * </p>
	 * 
	 * 
	 * <p>
	 * Transaction will be created with narration <strong>Cash Collection Penalty</strong>, one or more records will be
	 * inserted into <strong>OVERDRAFT_LOAN_TRANSACTIONS</strong> table.
	 * </p>
	 * 
	 * @param custEODEvent {@link CustEODEvent}
	 */
	void createPenalties(CustEODEvent custEODEvent);

	/**
	 * <p>
	 * This method will be called on Micro-EOD for each customer.
	 * </p>
	 * 
	 * <p>
	 * All the customer loans will be marked as Maturity, if the corresponding loan is fully paid and crosses the
	 * maturity date.
	 * <p>
	 * 
	 * @param custEODEvent {@link CustEODEvent}
	 */
	void closeByMaturity(CustEODEvent custEODEvent);

	int getGraceDays(FinanceMain fm);

	/**
	 * <p>
	 * This method will be called from the below method
	 * </p>
	 * 
	 * <li><code>LatePayMarkingService.findLatePay</code></li>
	 * 
	 * <li><code>LatePayMarkingService.latePayMarking</code></li>
	 * 
	 * <li><code>LoadFinanceData.setEventFlags</code></li>
	 * 
	 * <li><code>ReceiptPaymentService.processrReceipts</code></li>
	 * 
	 * <p>
	 * <strong>Current</strong> and <strong>Max</strong> transaction charges will be calculated from the manual-advises
	 * against to the provided schedule date
	 * </p>
	 * 
	 * @param fm         {@link FinanceMain}
	 * @param schDate    {@link Date}
	 * @param custBranch {@link String}
	 * @return
	 */
	FinODDetails calculateODAmounts(FinanceMain fm, Date schDate, String custBranch);

	/**
	 * <p>
	 * This method will be called on approval of the receipt payment
	 * </p>
	 * 
	 * <p>
	 * Unblock the limit when loan is fully paid and limit is in <strong>AUTO</strong> block state.
	 * </p>
	 * 
	 * @param finReference
	 * @param schedules
	 * @param valueDate
	 */
	void unBlockLimit(long finID, List<FinanceScheduleDetail> schedules, Date valueDate);

	/**
	 * <p>
	 * This method will be called while extracting the presentments when the
	 * implementationConstants<strong>OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE</stromg> is true.
	 * </p>
	 * 
	 * <p>
	 * Calculate Presentment charges when the application date is grater than due date
	 * </p>
	 * 
	 * <p>
	 * The presentment charges will be calculated from receivable advises, when the <strong>AdviceFeeTypeID</strong> and
	 * loan <strong>OverdraftTxnChrgFeeType</Strong> is matched.
	 * </p>
	 * 
	 * <p>
	 * If <strong>BounceID</strong> > 0, then the charge amount will be considered as <strong>BOUNCE</strong> otherwise
	 * <strong>MANUAL</strong>
	 * </p>
	 * 
	 * <p>
	 * Add the charge amount to presentment Amount and update in <strong>PresentmentDetails</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Save the presentment charges in <strong>Presentment_Charges</strong> table.
	 * </p>
	 * 
	 * <p>
	 * Update <strong>PresentmentId</strong> in the below tables.
	 * <li><strong>ManualAdvise</strong></li>
	 * <li><strong>FinODDetails</strong></li>
	 * </p>
	 * 
	 * @param presentments {@link List<PresentmentDetail>}
	 */
	void createCharges(List<PresentmentDetail> presentments);

	/**
	 * <p>
	 * This method will be called while cancel the presentment.
	 * </p>
	 * 
	 * <p>
	 * Update <strong>PresentmentId</strong> as 0 in the below tables.
	 * <li><strong>ManualAdvise</strong></li>
	 * <li><strong>FinODDetails</strong></li>
	 * </p>
	 * 
	 * <p>
	 * Delete the presentment charges from <strong>Presentment_Charges</strong> table.
	 * </p>
	 * 
	 * 
	 * @param presentmetID
	 */
	void cancelCharges(long presentmetID);

	/**
	 * <p>
	 * This method will be called on approval of the tranch disbursement
	 * <p>
	 * 
	 * <p>
	 * Validate the below
	 * <p>
	 * 
	 * <p>
	 * <li>Check whether the <strong>Monthly Limit</strong> Balance is grater than <strong>Disbursement + Fee</strong>
	 * Amount</li>
	 * <li>Check whether the <strong>Actual Limit</strong> Balance is grater than <strong>Disbursement + Fee</strong>
	 * Amount</li>
	 * <li>Check whether the Limit is blocked (<strong>BlockLimit</strong>) or not</li>
	 * </p>
	 * 
	 * @param schdData
	 * @param disbAmt
	 * @param fromDate
	 * @return Returns List of Error Details when the above validations are failed.
	 */
	List<ErrorDetail> validateDisbursment(FinScheduleData schdData, BigDecimal disbAmt, Date fromDate);

	BigDecimal getTransactionCharge(List<ManualAdviseMovements> movements, Date schDate, Date grcDate);

	long getOverdraftTxnChrgFeeType(String finType);

	OverdraftLimit getLimitByReference(long finID, String type);
}
