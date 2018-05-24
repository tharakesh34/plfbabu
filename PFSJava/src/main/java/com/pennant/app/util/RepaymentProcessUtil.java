package com.pennant.app.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;

public class RepaymentProcessUtil {
	private static final Logger			logger	= Logger.getLogger(RepaymentProcessUtil.class);

	private RepaymentPostingsUtil		repayPostingUtil;
	private FinODDetailsDAO				finODDetailsDAO;
	private FinExcessAmountDAO			finExcessAmountDAO;
	private FinReceiptHeaderDAO			finReceiptHeaderDAO;
	private FinReceiptDetailDAO			finReceiptDetailDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;

	private FinLogEntryDetailDAO		finLogEntryDetailDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceDisbursementDAO		financeDisbursementDAO;
	private RepayInstructionDAO			repayInstructionDAO;
	private ManualAdviseDAO				manualAdviseDAO;
	private FinFeeScheduleDetailDAO		finFeeScheduleDetailDAO;
	private FinInsurancesDAO			finInsurancesDAO;
	private LimitManagement				limitManagement;
	private ReceiptAllocationDetailDAO	allocationDetailDAO;	
	private PostingsPreparationUtil		postingsPreparationUtil;
	private FinanceMainDAO				financeMainDAO;

	public RepaymentProcessUtil() {
		super();
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 * @param scheduleData
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public void calcualteAndPayReceipt(FinanceMain financeMain, Customer customer,
			List<FinanceScheduleDetail> scheduleDetails, List<FinFeeDetail> finFeeDetailList, FinanceProfitDetail profitDetail,
			FinReceiptHeader receiptHeader, String repayHierarchy, Date valuedate,Date postDate) throws IllegalAccessException,
			InvocationTargetException, InterfaceException {
		logger.debug("Entering");
		
		String finrefer = financeMain.getFinReference();
		//Prepare schedule data for log
		FinScheduleData scheduleData = new FinScheduleData();
		scheduleData.setFinanceScheduleDetails(scheduleDetails);
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finrefer, "",
				false));
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finrefer, "", false));
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		
		//TDS Calculation, if Applicable
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if(financeMain.isTDSApplicable()){

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/
			
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
		}

		// Fetch total overdue details
		FinODDetails overdue = getFinODDetailsDAO().getFinODyFinRefSchDate(finrefer, valuedate);
		BigDecimal priPaynow = BigDecimal.ZERO;

		for (FinReceiptDetail receiptDetail : receiptDetails) {

			FinanceScheduleDetail curSchd = null;

			for (FinanceScheduleDetail financeScheduleDetail : scheduleDetails) {
				Date schdDate = financeScheduleDetail.getSchDate();
				// Skip if Repayment date after Current Business date
				if (schdDate.compareTo(valuedate) != 0) {
					continue;
				}
				curSchd = financeScheduleDetail;
				break;
			}

			// If no balance for repayment then return with out calculation
			BigDecimal totalReceiptAmt = receiptDetail.getAmount();
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				return;
			}

			scheduleDetails = sortSchdDetails(scheduleDetails);

			List<FinRepayHeader> repayHeaderList = new ArrayList<>();
			List<RepayScheduleDetail> pastdueRpySchdList = new ArrayList<>();
			char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
			BigDecimal totPriPaidNow = BigDecimal.ZERO;
			BigDecimal totPftPaidNow = BigDecimal.ZERO;
			BigDecimal totLPftPaidNow = BigDecimal.ZERO;
			BigDecimal totFeePaidNow = BigDecimal.ZERO;
			BigDecimal totInsPaidNow = BigDecimal.ZERO;
			BigDecimal totPenaltyPaidNow = BigDecimal.ZERO;
			Date valueDate = receiptDetail.getValueDate();

			// Load Pending Schedules until balance available for payment
			RepayScheduleDetail rsd = null;

			for (int j = 0; j < rpyOrder.length; j++) {

				char repayTo = rpyOrder[j];
				if (repayTo == RepayConstants.REPAY_PRINCIPAL) {

					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					if (balPri.compareTo(BigDecimal.ZERO) > 0) {
						if (balPri.compareTo(totalReceiptAmt) > 0) {
							balPri = totalReceiptAmt;
						}
						priPaynow = priPaynow.add(balPri);
						rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPri, valueDate);
						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						totPriPaidNow = totPriPaidNow.add(balPri);
					}
				} else if (repayTo == RepayConstants.REPAY_PROFIT) {

					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if (pftPayTo == RepayConstants.REPAY_PROFIT) {

							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (balPft.compareTo(BigDecimal.ZERO) > 0) {

								BigDecimal actPftAdjust = BigDecimal.ZERO;
								//actual tds
								actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
								// TDS Adjustments
								BigDecimal tdsAdjust = BigDecimal.ZERO;
								if(totalReceiptAmt.compareTo(actPftAdjust) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(actPftAdjust);
									tdsAdjust = balPft.subtract(actPftAdjust);
								}else{
									actPftAdjust = totalReceiptAmt;
									tdsAdjust = (actPftAdjust.multiply(tdsMultiplier)).subtract(actPftAdjust);
									totalReceiptAmt = BigDecimal.ZERO;
								}
								
								rsd = prepareRpyRecord(curSchd, rsd, repayTo, tdsAdjust.add(actPftAdjust), valueDate);
								
								if (tdsAdjust.compareTo(BigDecimal.ZERO)>0) {
									rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_TDS, tdsAdjust, valueDate);
								}
								
								// Reset Total Receipt Amount
								totPftPaidNow = totPftPaidNow.add(actPftAdjust.add(tdsAdjust));
							}

						} else if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {

							if (overdue != null) {
								BigDecimal balLatePft = overdue.getLPIBal();
								if (balLatePft.compareTo(BigDecimal.ZERO) > 0) {
									if (balLatePft.compareTo(totalReceiptAmt) > 0) {
										balLatePft = totalReceiptAmt;
									}
									rsd = prepareRpyRecord(curSchd, rsd, repayTo, balLatePft, valueDate);

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									totLPftPaidNow = totLPftPaidNow.add(balLatePft);
								}
							}
						}
					}

				} else if (repayTo == RepayConstants.REPAY_PENALTY) {
					if (overdue != null) {
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
							if (balPenalty.compareTo(totalReceiptAmt) > 0) {
								balPenalty = totalReceiptAmt;
							}
							rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPenalty, valueDate);

							// Reset Total Receipt Amount
							totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);
						}
					}

				} else if (repayTo == RepayConstants.REPAY_OTHERS) {

					// If Schedule has Unpaid Fee Amount
					BigDecimal balFee = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
					if (balFee.compareTo(BigDecimal.ZERO) > 0) {
						if (balFee.compareTo(totalReceiptAmt) > 0) {
							balFee = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_FEE, balFee, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balFee);
						totFeePaidNow = totFeePaidNow.add(balFee);
					}

					// If Schedule has Unpaid Insurance Amount
					BigDecimal balIns = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());
					if (balIns.compareTo(BigDecimal.ZERO) > 0) {
						if (balIns.compareTo(totalReceiptAmt) > 0) {
							balIns = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_INS, balIns, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balIns);
						totInsPaidNow = totInsPaidNow.add(balIns);
					}
				}

				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Add Repay Schedule detail List
			if (rsd != null) {
				pastdueRpySchdList.add(rsd);
			}

			FinRepayHeader repayHeader = null;
			if (receiptDetail.getAmount().compareTo(totalReceiptAmt) > 0) {
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(financeMain.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(receiptDetail.getAmount().subtract(totalReceiptAmt));
				repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
				repayHeader.setPriAmount(totPriPaidNow);
				repayHeader.setPftAmount(totPftPaidNow);
				repayHeader.setLatePftAmount(totLPftPaidNow);
				repayHeader.setTotalPenalty(totPenaltyPaidNow);
				repayHeader.setTotalIns(totInsPaidNow);
				repayHeader.setTotalSchdFee(totFeePaidNow);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(pastdueRpySchdList);
				repayHeaderList.add(repayHeader);
			}

			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
				receiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(financeMain.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(totalReceiptAmt);
				repayHeader.setFinEvent(RepayConstants.EXCESSADJUSTTO_EXCESS);
				repayHeader.setPriAmount(totalReceiptAmt);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(null);
				repayHeaderList.add(repayHeader);
			}

			// Adding Repay Headers to Receipt Details
			receiptDetail.setRepayHeaders(repayHeaderList);
		}

		List<Object> returnList = doProcessReceipts(financeMain, scheduleDetails, profitDetail, receiptHeader, finFeeDetailList, scheduleData,
				valuedate,postDate);
		
		scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(0);
		
		// Preparing Total Principal Amount
		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			if(receiptDetail.getRepayHeaders() != null && !receiptDetail.getRepayHeaders().isEmpty()){
				for (FinRepayHeader repayHeader : receiptDetail.getRepayHeaders()) {
					if(repayHeader.getRepayScheduleDetails() != null && !repayHeader.getRepayScheduleDetails().isEmpty()){
						for (RepayScheduleDetail rpySchd : repayHeader.getRepayScheduleDetails()) {
							totPriPaid = totPriPaid.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
						}
					}
				}
			}
		}
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(totPriPaid));
		
		FinanceScheduleDetail curSchd = null;
		
		for (FinanceScheduleDetail financeScheduleDetail : scheduleDetails) {
			Date schdDate = financeScheduleDetail.getSchDate();
			// Skip if Repayment date after Current Business date
			if (schdDate.compareTo(valuedate) != 0) {
				continue;
			}
			curSchd = financeScheduleDetail;
			financeScheduleDetailDAO.updateForRpy(curSchd);
			break;
		}
		
		
		doSaveReceipts(receiptHeader, null, true);
		financeMainDAO.updatePaymentInEOD(financeMain);
		limitManagement.processLoanRepay(financeMain, customer, priPaynow, profitDetail.getFinCategory());
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Payment details as per receipt details
	 * 
	 * @param receiptHeader
	 */
	@SuppressWarnings("unchecked")
	public  List<Object> doProcessReceipts(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, FinReceiptHeader receiptHeader, List<FinFeeDetail> finFeeDetailList,
			FinScheduleData logScheduleData, Date valueDate,Date postingDate) throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		BigDecimal uAmz = BigDecimal.ZERO;
		List<FinReceiptDetail> receiptDetailList = sortReceiptDetails(receiptHeader.getReceiptDetails());

		// Find out Is there any schedule payment done or not, If exists Log will be captured
		boolean isSchdLogReq = false;
		for (int i = 0; i < receiptDetailList.size(); i++) {
			List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
			for (int j = 0; j < repayHeaderList.size(); j++) {
				FinRepayHeader repayHeader = repayHeaderList.get(j);
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {
					isSchdLogReq = true;
				}
			}
		}

		long linkedTranId = 0;
		String finReference = financeMain.getFinReference();
		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = null;
		long logKey = 0;
		Date postDate = getPostDate(postingDate);
		if (isSchdLogReq && ((receiptHeader.getAllocations() != null && !receiptHeader.getAllocations().isEmpty()) 
				|| receiptHeader.isLogSchInPresentment())) {
			entryDetail = new FinLogEntryDetail();
			entryDetail.setFinReference(finReference);
			entryDetail.setEventAction(receiptHeader.getReceiptPurpose());
			entryDetail.setSchdlRecal(false);
			entryDetail.setPostDate(postDate);
			entryDetail.setReversalCompleted(false);
			logKey = getFinLogEntryDetailDAO().save(entryDetail);

			//Save Schedule Details For Future Modifications
			FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
			listSave(oldFinSchdData, "_Log", logKey);
		}

		boolean feesExecuted = false;
		boolean executePftChg = true;
		
		boolean payableLoopProcess = false;
		Map<String, BigDecimal> extDataMap = new HashMap<>();
		BigDecimal totPayable = BigDecimal.ZERO;
		int rcptSize = receiptDetailList.size();
		List<RepayScheduleDetail> repaySchdList = null;
		for (int rcpt = 0; rcpt < receiptDetailList.size(); rcpt++) {
			
			FinReceiptDetail receiptDetail = receiptDetailList.get(rcpt);

			// Repay Header list process individually based on List existence
			List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();

			if (rcpt != 0) {
				postDate = getPostDate(postingDate);
			}
			
			if(!payableLoopProcess && !StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				extDataMap = new HashMap<>();
				totPayable = BigDecimal.ZERO;
			}
			
			totPayable = totPayable.add(receiptDetail.getAmount());
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
				extDataMap.put("PA_ReceiptAmount", totPayable);
			}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)){
				extDataMap.put("EX_ReceiptAmount", receiptDetail.getAmount());
			}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
				extDataMap.put("EA_ReceiptAmount", receiptDetail.getAmount());
			}else{
				extDataMap.put("PB_ReceiptAmount", receiptDetail.getAmount());
			}
			
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
				if(extDataMap.containsKey(receiptDetail.getFeeTypeCode()+"_P")){
					extDataMap.put(receiptDetail.getFeeTypeCode()+"_P", extDataMap.get(receiptDetail.getFeeTypeCode()+"_P").add(receiptDetail.getAmount()));
				}else{
					extDataMap.put(receiptDetail.getFeeTypeCode()+"_P", receiptDetail.getAmount());
				}
			}

			boolean rpyProcessed = false;
			int rcptHSize = receiptDetail.getRepayHeaders().size();
			for (int rcph = 0; rcph < repayHeaderList.size(); rcph++) {
				
				FinanceProfitDetail pftDetailTemp = new FinanceProfitDetail();
				BeanUtils.copyProperties(profitDetail, pftDetailTemp);

				FinRepayHeader repayHeader = repayHeaderList.get(rcph);
				
				if(rcph != 0){
					extDataMap.clear();
				}
								
				if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					if(StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent()) || 
							StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent()) ||
							StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())){
						
						if(rcptHSize > rcph+1){
							
							FinRepayHeader nxtRcpH = receiptDetail.getRepayHeaders().get(rcph+1);
							if(StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, nxtRcpH.getFinEvent()) || 
									StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, nxtRcpH.getFinEvent()) ||
									StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, nxtRcpH.getFinEvent())){
								if(StringUtils.equals(nxtRcpH.getFinEvent(), repayHeader.getFinEvent())){
									payableLoopProcess = true;
								}else{
									payableLoopProcess = false;
								}
							}else{
								payableLoopProcess = false;
							}
						}else{
							if(rcptSize > rcpt+1){
								FinReceiptDetail nxtRcp = receiptDetailList.get(rcpt+1);
								if(StringUtils.equals(nxtRcp.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
									if(nxtRcp.getRepayHeaders() != null && !nxtRcp.getRepayHeaders().isEmpty()){
										FinRepayHeader nxtRcpH = nxtRcp.getRepayHeaders().get(0);
										if(StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, nxtRcpH.getFinEvent()) || 
												StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, nxtRcpH.getFinEvent()) ||
												StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, nxtRcpH.getFinEvent())){
											if(StringUtils.equals(nxtRcpH.getFinEvent(), repayHeader.getFinEvent())){
												payableLoopProcess = true;
											}else{
												payableLoopProcess = false;
											}
										}else{
											payableLoopProcess = false;
										}
									}else{
										payableLoopProcess = false;
									}
								}else{
									payableLoopProcess = false;
								}
							}else{
								payableLoopProcess = false;
							}
						}
					}else{
						payableLoopProcess = false;
					}
				}
				
				if (!StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
						&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
						&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {

					// Update Excess amount (Adding amount and balance updation)
					if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS) ||
							StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EMIINADV)){
						
						// Accounting Postings Process Execution
						AEEvent aeEvent = new AEEvent();
						AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
						if(amountCodes == null){
							amountCodes = new AEAmountCodes();
						}
						
						aeEvent.setCustID(financeMain.getCustID());
						aeEvent.setFinReference(finReference);
						aeEvent.setFinType(financeMain.getFinType());
						aeEvent.setPromotion(financeMain.getPromotionCode());
						aeEvent.setBranch(financeMain.getFinBranch());
						aeEvent.setCcy(financeMain.getFinCcy());
						aeEvent.setPostingUserBranch(receiptHeader.getPostBranch());
						aeEvent.setLinkedTranId(0);
						aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
						aeEvent.setValueDate(valueDate);
						
						amountCodes.setFinType(financeMain.getFinType());
						amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
						amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
						amountCodes.setToExcessAmt(BigDecimal.ZERO);
						amountCodes.setToEmiAdvance(BigDecimal.ZERO);
						if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS)){
							amountCodes.setToExcessAmt(repayHeader.getRepayAmount());
						}else {
							amountCodes.setToEmiAdvance(repayHeader.getRepayAmount());
						}
						
						if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
							aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
						} else {
							aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
						}

						HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
						if(!feesExecuted && StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)){
							feesExecuted = true;
							prepareFeeRulesMap(amountCodes, dataMap, finFeeDetailList, receiptDetail.getPaymentType());
						}
						
						// Receipt Detail external usage Fields Insertion into DataMap
						dataMap.putAll(extDataMap);
						
						aeEvent.setDataMap(dataMap);

						// Accounting Entry Execution
						aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
						repayHeader.setLinkedTranId(aeEvent.getLinkedTranId());
						repayHeader.setValueDate(postDate);
						
						if (!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE) && 
								receiptDetail.getPayAgainstID() != 0 && receiptDetail.getPayAgainstID() != Long.MIN_VALUE) {
							getFinExcessAmountDAO().updateExcessBal(receiptDetail.getPayAgainstID(),
									repayHeader.getRepayAmount());
						} else {
							int recordCount = getFinExcessAmountDAO().updateExcessBalByRef(finReference,
									repayHeader.getFinEvent(), repayHeader.getRepayAmount());
							// If record Not found then record count should be zero. Need to create new Excess Record
							if (recordCount <= 0) {
								FinExcessAmount excess = new FinExcessAmount();
								excess.setFinReference(finReference);
								excess.setAmountType(repayHeader.getFinEvent());
								excess.setAmount(repayHeader.getRepayAmount());
								excess.setBalanceAmt(repayHeader.getRepayAmount());
								excess.setUtilisedAmt(BigDecimal.ZERO);
								excess.setReservedAmt(BigDecimal.ZERO);
								getFinExcessAmountDAO().saveExcess(excess);
							}
							
							// Excess Movement Creation
							/*FinExcessMovement movement = new FinExcessMovement();
							movement.setExcessID(payAgainstID);
							movement.setReceiptID(receiptSeqID);
							movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
							movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
							movement.setAmount(repayHeader.getRepayAmount());
							getFinExcessAmountDAO().saveExcessMovement(movement);*/
						}
					}
					
					// Create New Payable Amount
					if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_PAYABLE)){
						
						ManualAdvise manualAdvise = new ManualAdvise();
						manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
						manualAdvise.setFinReference(receiptHeader.getReference());
						manualAdvise.setFeeTypeID(0);
						manualAdvise.setSequence(0);
						manualAdvise.setAdviseAmount(repayHeader.getRepayAmount());
						manualAdvise.setBalanceAmt(repayHeader.getRepayAmount());
						manualAdvise.setValueDate(receiptDetail.getValueDate());
						manualAdvise.setPostDate(receiptDetail.getValueDate());
						manualAdvise.setReceiptID(receiptHeader.getReceiptID());
						manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
					}

					continue;
				}
				
				if(repaySchdList == null){
					repaySchdList = repayHeader.getRepayScheduleDetails();
				}else{
					repaySchdList.addAll(repayHeader.getRepayScheduleDetails());
				}
				
				// If Payable Continue for All Advises
				if(payableLoopProcess){
					continue;
				}

				//Create log entry for Action for Schedule Modification
				if (rcpt != 0 && !rpyProcessed) {
					entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(finReference);
					entryDetail.setEventAction(receiptHeader.getReceiptPurpose());
					entryDetail.setSchdlRecal(true);
					entryDetail.setPostDate(postDate);
					entryDetail.setReversalCompleted(false);
					logKey = getFinLogEntryDetailDAO().save(entryDetail);

					//Save Schedule Details For Future Modifications
					logScheduleData.setFinanceMain(financeMain);
					logScheduleData.setFinReference(finReference);
					logScheduleData.setFinanceScheduleDetails(scheduleDetails);
					listSave(logScheduleData, "_Log", logKey);
				}

				boolean executeFeesNow = false;
				if(!feesExecuted && (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY) ||
						(!StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY) &&
								StringUtils.equals(receiptHeader.getReceiptPurpose(), repayHeader.getFinEvent())))){
					executeFeesNow = true;
				}
				
				String accEvent = getEventCode(repayHeader.getFinEvent());
				rpyProcessed = true;
				List<Object> returnList = doRepayPostings(financeMain, scheduleDetails, (executeFeesNow ? finFeeDetailList : null), pftDetailTemp, repaySchdList,
						accEvent, valueDate,postingDate, receiptDetail, receiptHeader.getPostBranch(), executePftChg,extDataMap);
				
				if(StringUtils.equals(accEvent, AccountEventConstants.ACCEVENT_EARLYPAY)){
					executePftChg = false;
				}else if(StringUtils.equals(accEvent, AccountEventConstants.ACCEVENT_EARLYSTL)){
					executePftChg = false;
				}

				if(executeFeesNow){
					feesExecuted = true;
				}
				if (!(Boolean) returnList.get(0)) {
					String errParm = (String) returnList.get(1);
					throw new InterfaceException("9999", errParm);
				}
				
				//Update Linked Transaction ID
				linkedTranId = (long) returnList.get(1);
				repayHeader.setLinkedTranId(linkedTranId);
				repayHeader.setValueDate(postDate);
				scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(2);
				
				// Unrealized Income amount
				uAmz = uAmz.add((BigDecimal) returnList.get(3));
				repayHeader.setRealizeUnAmz(uAmz);
				
				repaySchdList = null;
			}
			
			// Manual Advise Postings
			List<ManualAdviseMovements> movements = receiptDetail.getAdvMovements();
			if(movements != null && !movements.isEmpty()){
				
				// If Repay header exists on Receipt Detail, no need to re-execute external map data
				if(receiptDetail.getRepayHeaders() != null && !receiptDetail.getRepayHeaders().isEmpty()){
					extDataMap.clear();
				}
				
				procManualAdvPostings(receiptDetail, financeMain, movements, receiptHeader.getPostBranch(), extDataMap, valueDate);
			}

			// Setting/Maintaining Log key for Last log of Schedule Details
			receiptDetailList.get(rcpt).setLogKey(logKey);
		}
		

		List<Object> returnList = new ArrayList<>();
		returnList.add(scheduleDetails);
		returnList.add(uAmz);
		
		logger.debug("Leaving");
		return returnList;

	}
	
	/**
	 * Method for Processing Manual Advise Postings
	 * @param receiptDetail
	 * @param financeMain
	 * @param movements
	 * @param postBranch
	 * @param dateValueDate 
	 * @param postDate 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterfaceException 
	 */
	private void procManualAdvPostings(FinReceiptDetail receiptDetail, FinanceMain financeMain,
			List<ManualAdviseMovements> movements, String postBranch,Map<String, BigDecimal> extDataMap, Date dateValueDate) throws InterfaceException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		// Accounting Postings Process Execution
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if(amountCodes == null){
			amountCodes = new AEAmountCodes();
		}
		
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setFinType(financeMain.getFinType());
		aeEvent.setPromotion(financeMain.getPromotionCode());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setLinkedTranId(0);
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
		aeEvent.setValueDate(dateValueDate);
		
		aeEvent.getAcSetIDList().clear();
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), 
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), 
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
		}
		
		amountCodes.setFinType(financeMain.getFinType());
		amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
		dataMap.putAll(prepareMovementMap(movements));
		if(extDataMap != null){
			dataMap.putAll(extDataMap);
		}
		aeEvent.setDataMap(dataMap);

		// Accounting Entry Execution
		getPostingsPreparationUtil().postAccounting(aeEvent);

		logger.debug("Leaving");
	}
	
	private Map<String, BigDecimal> prepareMovementMap(List<ManualAdviseMovements> movements){
		
		// Summing Same Type of Fee Types to Single Field
		HashMap<String, BigDecimal> movementMap = new HashMap<>(); 
		for (int m = 0; m < movements.size(); m++) {
			ManualAdviseMovements movement = movements.get(m);

			BigDecimal amount = BigDecimal.ZERO;
			String keyCode = null;
			if(StringUtils.isEmpty(movement.getFeeTypeCode())){

				if(movementMap.containsKey("bounceChargePaid")){
					amount = movementMap.get("bounceChargePaid");
				}
				movementMap.put("bounceChargePaid", amount.add(movement.getPaidAmount()));

				amount = BigDecimal.ZERO;
				if(movementMap.containsKey("bounceChargeWaived")){
					amount = movementMap.get("bounceChargeWaived");
				}
				movementMap.put("bounceChargeWaived",  amount.add(movement.getWaivedAmount()));
				keyCode = "bounceCharge";
			}else{
				if(movementMap.containsKey(movement.getFeeTypeCode() + "_P")){
					amount = movementMap.get(movement.getFeeTypeCode() + "_P");
				}
				movementMap.put(movement.getFeeTypeCode() + "_P", amount.add(movement.getPaidAmount()));

				amount = BigDecimal.ZERO;
				if(movementMap.containsKey(movement.getFeeTypeCode() + "_W")){
					amount = movementMap.get(movement.getFeeTypeCode() + "_W");
				}
				movementMap.put(movement.getFeeTypeCode() + "_W",  amount.add(movement.getWaivedAmount()));
			}
			// Tax Details
			amount = BigDecimal.ZERO;
			if(movementMap.containsKey(keyCode + "_CGST_P")){
				amount = movementMap.get(keyCode + "_CGST_P");
			}
			movementMap.put(keyCode + "_CGST_P",  amount.add(movement.getPaidCGST()));

			amount = BigDecimal.ZERO;
			if(movementMap.containsKey(keyCode + "_SGST_P")){
				amount = movementMap.get(keyCode + "_SGST_P");
			}
			movementMap.put(keyCode + "_SGST_P",  amount.add(movement.getPaidSGST()));

			amount = BigDecimal.ZERO;
			if(movementMap.containsKey(keyCode + "_IGST_P")){
				amount = movementMap.get(keyCode + "_IGST_P");
			}
			movementMap.put(keyCode + "_IGST_P",  amount.add(movement.getPaidIGST()));

			amount = BigDecimal.ZERO;
			if(movementMap.containsKey(keyCode + "_UGST_P")){
				amount = movementMap.get(keyCode + "_UGST_P");
			}
			movementMap.put(keyCode + "_UGST_P",  amount.add(movement.getPaidUGST()));
		}
		return movementMap;
	}
	
	/**
	 * Method for Preparation of Fees Data in Receipts
	 * @param amountCodes
	 * @param dataMap
	 * @param finFeeDetailList
	 * @return
	 */
	private HashMap<String, Object> prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap, 
			List<FinFeeDetail> finFeeDetailList, String payType) {
		logger.debug("Entering");

		if (finFeeDetailList != null) {
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if(!finFeeDetail.isRcdVisible()){
					continue;
				}
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_N", finFeeDetail.getNetAmount());
				
				if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EXCESS)){
					payType = "EX_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EMIINADV)){
					payType = "EA_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_PAYABLE)){
					payType = "PA_";
				}else{
					payType = "PB_";
				}
				dataMap.put(payType + finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
				
				//Calculated Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_C", finFeeDetail.getFinTaxDetails().getActualCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_C", finFeeDetail.getFinTaxDetails().getActualSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_C", finFeeDetail.getFinTaxDetails().getActualIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_C", finFeeDetail.getFinTaxDetails().getActualUGST());
				
				//Paid Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_P", finFeeDetail.getFinTaxDetails().getPaidCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_P", finFeeDetail.getFinTaxDetails().getPaidSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_P", finFeeDetail.getFinTaxDetails().getPaidIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_P", finFeeDetail.getFinTaxDetails().getPaidUGST());
	
				//Net Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_N", finFeeDetail.getFinTaxDetails().getNetCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_N", finFeeDetail.getFinTaxDetails().getNetSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_N", finFeeDetail.getFinTaxDetails().getNetIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_N", finFeeDetail.getFinTaxDetails().getNetUGST());
			}
		}

		logger.debug("Leaving");
		return dataMap;
	}

	/**
	 * Method for Saving the Receipt records
	 * 
	 * @param receiptHeader
	 */
	public void doSaveReceipts(FinReceiptHeader receiptHeader, List<FinFeeDetail> finFeeDetails, boolean isApproval) {
		logger.debug("Entering");
		
		long receiptID = getFinReceiptHeaderDAO().save(receiptHeader, TableType.MAIN_TAB);
		receiptHeader.setReceiptID(receiptID);
		
		// Receipt Allocation Details
		Map<String, BigDecimal> allocationPaidMap = null;
		Map<String, BigDecimal> allocationWaivedMap = null;
		if(receiptHeader.getAllocations() != null && !receiptHeader.getAllocations().isEmpty()){
			if(isApproval){
				allocationPaidMap = new HashMap<>();
				allocationWaivedMap = new HashMap<>();
			}
			
			List<Long> bounceAdvises = null;
			for (int i = 0; i < receiptHeader.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = receiptHeader.getAllocations().get(i);
				allocation.setReceiptID(receiptID);
				allocation.setAllocationID(i+1);
				
				if(isApproval){
					allocationPaidMap.put(allocation.getAllocationType()+"_"+allocation.getAllocationTo(), allocation.getPaidAmount());
					allocationWaivedMap.put(allocation.getAllocationType()+"_"+allocation.getAllocationTo(), allocation.getWaivedAmount());

					// Manual Advises update
					if(StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_MANADV)){
						if(allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 || 
								allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0){
							
							ManualAdvise advise = new ManualAdvise();
							advise.setAdviseID(allocation.getAllocationTo());
							advise.setPaidAmount(allocation.getPaidAmount());
							advise.setWaivedAmount(allocation.getWaivedAmount());
							
							for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
								for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
									if(allocation.getAllocationTo() == movement.getAdviseID()){
										advise.setPaidCGST(advise.getPaidCGST().add(movement.getPaidCGST()));
										advise.setPaidSGST(advise.getPaidSGST().add(movement.getPaidSGST()));
										advise.setPaidIGST(advise.getPaidIGST().add(movement.getPaidIGST()));
										advise.setPaidUGST(advise.getPaidUGST().add(movement.getPaidUGST()));
									}
								}
							}
							
							getManualAdviseDAO().updateAdvPayment(advise , TableType.MAIN_TAB);
						}
					}
					
					// Bounce Charges Update
					if(StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)){
						if(allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 || 
								allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0){
							
							if(bounceAdvises == null){
								bounceAdvises = getManualAdviseDAO().getBounceAdvisesListByRef(receiptHeader.getReference(), 
										FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "");
							}
							
							List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
							for (FinReceiptDetail receiptDetail : receiptDetails) {
								for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
									if(bounceAdvises.contains(movement.getAdviseID())){
										
										ManualAdvise advise = new ManualAdvise();
										advise.setAdviseID(movement.getAdviseID());
										advise.setPaidAmount(movement.getPaidAmount());
										advise.setWaivedAmount(movement.getWaivedAmount());
										advise.setPaidCGST(movement.getPaidCGST());
										advise.setPaidSGST(movement.getPaidSGST());
										advise.setPaidIGST(movement.getPaidIGST());
										advise.setPaidUGST(movement.getPaidUGST());
										
										getManualAdviseDAO().updateAdvPayment(advise, TableType.MAIN_TAB);
									}
								}
							}
							
						}
					}
				}
			}

			getAllocationDetailDAO().saveAllocations(receiptHeader.getAllocations() , TableType.MAIN_TAB);
		}

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			if(isApproval){
				receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			}else{
				receiptDetail.setStatus(receiptHeader.getReceiptModeStatus());
			}
			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, TableType.MAIN_TAB);

			// Excess Amounts
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)
					|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)) {

				long payAgainstID = receiptDetail.getPayAgainstID();

				// Excess Amount make utilization
				if (payAgainstID != 0) {
					
					if(isApproval){
						if (receiptDetail.isNoReserve()) {
							//update only utilization
							getFinExcessAmountDAO().updateUtiliseOnly(payAgainstID, receiptDetail.getAmount());
						}else{
							getFinExcessAmountDAO().updateUtilise(payAgainstID, receiptDetail.getAmount());
						}

						// Delete Reserved Log against Excess and Receipt ID
						getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, payAgainstID, RepayConstants.RECEIPTTYPE_RECIPT);

						// Excess Movement Creation
						FinExcessMovement movement = new FinExcessMovement();
						movement.setExcessID(payAgainstID);
						movement.setReceiptID(receiptSeqID);
						movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
						movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
						movement.setAmount(receiptDetail.getAmount());
						getFinExcessAmountDAO().saveExcessMovement(movement);
					}else{
						
						// Excess Amount make utilization
						FinExcessAmountReserve exReserve = getFinExcessAmountDAO().getExcessReserve(receiptSeqID, payAgainstID);
						if(exReserve != null){

							// Update Reserve Amount in FinExcessAmount
							getFinExcessAmountDAO().updateExcessReserve(payAgainstID, exReserve.getReservedAmt().negate());

							// Delete Reserved Log against Excess and Receipt ID
							getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, payAgainstID, RepayConstants.RECEIPTTYPE_RECIPT);
						}
					}
				}
			}
			
			// Payable Advise Amounts
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)) {

				long payAgainstID = receiptDetail.getPayAgainstID();

				// Payable Advise Amount make utilization
				if (payAgainstID != 0) {
					
					if(isApproval){
						getManualAdviseDAO().updateUtilise(payAgainstID, receiptDetail.getAmount());

						// Delete Reserved Log against Advise and Receipt Seq ID
						getManualAdviseDAO().deletePayableReserve(receiptSeqID, payAgainstID);

						// Payable Advise Movement Creation
						ManualAdviseMovements movement = new ManualAdviseMovements();
						movement.setAdviseID(payAgainstID);
						movement.setReceiptID(receiptID);
						movement.setReceiptSeqID(receiptSeqID);
						movement.setMovementDate(DateUtility.getAppDate());
						movement.setMovementAmount(receiptDetail.getAmount());
						movement.setPaidAmount(receiptDetail.getAmount());
						getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());
					}else{
						// Payable Amount make utilization
						ManualAdviseReserve payableReserve = getManualAdviseDAO().getPayableReserve(receiptSeqID, payAgainstID);
						if(payableReserve != null){

							// Update Reserve Amount in ManualAdvise
							getManualAdviseDAO().updatePayableReserve(payAgainstID, payableReserve.getReservedAmt().negate());

							// Delete Reserved Log against Payable Advise ID and Receipt ID
							getManualAdviseDAO().deletePayableReserve(receiptSeqID, payAgainstID);
						}
					}
				}
			}

			// Manual Advise Movements
			if(isApproval){
				for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
					movement.setReceiptID(receiptID);
					movement.setReceiptSeqID(receiptSeqID);
					getManualAdviseDAO().saveMovement(movement, TableType.MAIN_TAB.getSuffix());
				}
			}

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				rpyHeader.setFinReference(receiptHeader.getReference());

				//Save Repay Header details
				long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB.getSuffix());

				List<RepayScheduleDetail> rpySchdList = rpyHeader.getRepayScheduleDetails();
				if (rpySchdList != null && !rpySchdList.isEmpty()) {
					for (int i = 0; i < rpySchdList.size(); i++) {

						RepayScheduleDetail rpySchd = rpySchdList.get(i);
						rpySchd.setFinReference(receiptHeader.getReference());
						rpySchd.setRepayID(repayID);
						rpySchd.setRepaySchID(i + 1);
						rpySchd.setLinkedTranId(rpyHeader.getLinkedTranId());
						
						if(isApproval){
							//update fee schedule details
							updateFeeDetails(rpySchd, finFeeDetails, allocationPaidMap, allocationWaivedMap);

							//update insurance schedule details
							updateInsuranceDetails(rpySchd);
						}
					}

					// Save Repayment Schedule Details
					getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, TableType.MAIN_TAB.getSuffix());
				}
			}
		}
		
		allocationPaidMap = null;
		allocationWaivedMap = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * 
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts) {

		if (receipts != null && !receipts.isEmpty()) {
			Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if (detail1.getPayOrder() < detail2.getPayOrder()) {
						return -1;
					}
					return 0;
				}
			});
		}
		return receipts;
	}

	private void updateInsuranceDetails(RepayScheduleDetail rpySchd) {
		logger.debug("Entering");
		
		BigDecimal remBalPaidAmount = rpySchd.getSchdInsPayNow();
		if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
		List<FinSchFrqInsurance> list = finInsurancesDAO.getInsScheduleBySchDate(rpySchd.getFinReference(),
				rpySchd.getSchDate());

		for (FinSchFrqInsurance insSchd : list) {

			if (remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			BigDecimal insBal = insSchd.getAmount().subtract(insSchd.getInsurancePaid())
					.subtract(insSchd.getInsuranceWaived());
			if (insBal.compareTo(remBalPaidAmount) > 0) {
				insBal = remBalPaidAmount;
			}
			insSchd.setInsurancePaid(insSchd.getInsurancePaid().add(insBal));
			updateInsList.add(insSchd);
			remBalPaidAmount = remBalPaidAmount.subtract(insBal);

		}

		if (!updateInsList.isEmpty()) {
			finInsurancesDAO.updateInsPaids(updateInsList);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Fee Schedule Details based on Allocations and Paid Amounts 
	 * @param rpySchd
	 * @param allocationPaidMap
	 * @param allocationWaivedMap
	 */
	private void updateFeeDetails(RepayScheduleDetail rpySchd, List<FinFeeDetail> finFeeDetails,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap) {
		logger.debug("Entering");
		
		BigDecimal paidBal = rpySchd.getSchdFeePayNow();
		BigDecimal waivedBal = rpySchd.getSchdFeeWaivedNow();

		if (paidBal.compareTo(BigDecimal.ZERO) == 0 && waivedBal.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		
		// If Process for EOD , Fees should be updated immediately
		List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
		List<FinFeeScheduleDetail> list = new ArrayList<>();
		if(finFeeDetails == null){
			list = finFeeScheduleDetailDAO.getFeeSchedules(rpySchd.getFinReference(), rpySchd.getSchDate());

			// Schedule Fee Updation
			for (FinFeeScheduleDetail feeSchd : list) {
				feeSchd = feeSchdUpdation(feeSchd, paidBal, waivedBal,allocationPaidMap, allocationWaivedMap);
				if(feeSchd != null){
					updateFeeList.add(feeSchd);
				}

			}
		}else{
			for (FinFeeDetail fee : finFeeDetails) {
				
				if(StringUtils.equals(fee.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)){
					continue;
				}
				if(fee.getFinFeeScheduleDetailList() != null && !fee.getFinFeeScheduleDetailList().isEmpty()){
					for (FinFeeScheduleDetail feeSchd : fee.getFinFeeScheduleDetailList()) {
						if(DateUtility.compare(feeSchd.getSchDate(), rpySchd.getSchDate()) == 0){
							feeSchdUpdation(feeSchd, paidBal, waivedBal,allocationPaidMap, allocationWaivedMap);
						}
					}
				}
			}
		}
		
		if (!updateFeeList.isEmpty()) {
			finFeeScheduleDetailDAO.updateFeePaids(updateFeeList);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fee Schedule Updation absed on Repayment Schedule date selection
	 */
	private FinFeeScheduleDetail feeSchdUpdation(FinFeeScheduleDetail feeSchd, BigDecimal paidBal,BigDecimal waivedBal,
			Map<String, BigDecimal> allocationPaidMap, Map<String, BigDecimal> allocationWaivedMap){
		logger.debug("Entering");

		// No balance to adjust, should return back
		if (paidBal.compareTo(BigDecimal.ZERO) == 0 && waivedBal.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		BigDecimal feeBal = feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount().subtract(feeSchd.getWaiverAmount()));
		if (feeBal.compareTo(paidBal) > 0) {
			feeBal = paidBal;
		}

		// If allocation map is present then Paid adjustment based on Allocations only
		if(allocationPaidMap != null){
			if(allocationPaidMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
				BigDecimal remPaidBal = allocationPaidMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
				if(feeBal.compareTo(remPaidBal) > 0){
					feeBal = remPaidBal;
				}
			}else{
				feeBal = BigDecimal.ZERO;
			}
		}

		if(paidBal.compareTo(BigDecimal.ZERO) == 0){
			if (feeBal.compareTo(waivedBal) > 0) {
				feeBal = waivedBal;
			}

			// If allocation map is present then Waived adjustment based on Allocations only
			if(allocationWaivedMap != null){
				if(allocationWaivedMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
					BigDecimal remWaivedBal = allocationWaivedMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
					if(feeBal.compareTo(remWaivedBal) > 0){
						feeBal = remWaivedBal;
					}
				}else{
					feeBal = BigDecimal.ZERO;
				}
			}

			// If Fees not allocated on paids then No updates
			if(feeBal.compareTo(BigDecimal.ZERO) == 0){
				return null;
			}

			feeSchd.setWaiverAmount(feeSchd.getWaiverAmount().add(feeBal));
			waivedBal = waivedBal.subtract(feeBal);

			// Allocation map Balance adjustment after Collection(Paid/waived)
			if(allocationWaivedMap != null){
				if(allocationWaivedMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
					BigDecimal remWaivedBal = allocationWaivedMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
					allocationWaivedMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), remWaivedBal.subtract(feeBal));
				}
			}

		}else{

			// If Fees not allocated on paids then No updates
			if(feeBal.compareTo(BigDecimal.ZERO) == 0){
				return null;
			}

			feeSchd.setPaidAmount(feeSchd.getPaidAmount().add(feeBal));
			paidBal = paidBal.subtract(feeBal);

			// Allocation map Balance adjustment after Collection(Paid/waived)
			if(allocationPaidMap != null){
				if(allocationPaidMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
					BigDecimal remPaidBal = allocationPaidMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
					allocationPaidMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), remPaidBal.subtract(feeBal));
				}
			}
		}

		feeSchd.setOsAmount(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()).subtract(feeSchd.getWaiverAmount()));

		logger.debug("Leaving");
		return feeSchd;
	}

	/**
	 * Method for Status updation on Finance
	 * 
	 * @param financeMain
	 * @param valueDate
	 * @param scheduleDetails
	 * @param profitDetail
	 * @return
	 * @throws Exception 
	 */
	public FinanceMain updateStatus(FinanceMain financeMain, Date valueDate,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail profitDetail, List<FinODDetails> overdueList, String receiptPurpose) throws Exception {
		return getRepayPostingUtil().updateStatus(financeMain, valueDate, scheduleDetails, profitDetail, overdueList, receiptPurpose);
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	private List<Object> doRepayPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			List<FinFeeDetail> finFeeDetailList, FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String eventCode,
			Date valuedate,Date postDate, FinReceiptDetail receiptDetail, String postBranch, boolean pftChgAccReq, Map<String, BigDecimal> extDataMap) throws IllegalAccessException,
			InterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			FinRepayQueue finRepayQueue = null;
			FinRepayQueueHeader rpyQueueHeader = new FinRepayQueueHeader();

			if (repaySchdList != null && !repaySchdList.isEmpty()) {
				for (int i = 0; i < repaySchdList.size(); i++) {

					finRepayQueue = new FinRepayQueue();
					finRepayQueue.setFinReference(financeMain.getFinReference());
					finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
					finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
					finRepayQueue.setRcdNotExist(true);
					finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i));

					finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
					finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
					finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
					finRepayQueue.setPenaltyBal(repaySchdList.get(i).getPenaltyAmt().subtract(repaySchdList.get(i).getPenaltyPayNow()));
					finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

					// Total Repayments Calculation for Principal, Profit 
					rpyQueueHeader.setPrincipal(rpyQueueHeader.getPrincipal().add(repaySchdList.get(i).getPrincipalSchdPayNow()));
					rpyQueueHeader.setProfit(rpyQueueHeader.getProfit().add(repaySchdList.get(i).getProfitSchdPayNow()));
					rpyQueueHeader.setTds(rpyQueueHeader.getTds().add(repaySchdList.get(i).getTdsSchdPayNow()));
					rpyQueueHeader.setLateProfit(rpyQueueHeader.getLateProfit().add(repaySchdList.get(i).getLatePftSchdPayNow()));
					rpyQueueHeader.setPenalty(rpyQueueHeader.getPenalty().add(repaySchdList.get(i).getPenaltyPayNow()));

					// Fee Details
					rpyQueueHeader.setFee(rpyQueueHeader.getFee().add(repaySchdList.get(i).getSchdFeePayNow()));
					rpyQueueHeader.setInsurance(rpyQueueHeader.getInsurance().add(repaySchdList.get(i).getSchdInsPayNow()));
					rpyQueueHeader.setSuplRent(rpyQueueHeader.getSuplRent().add(repaySchdList.get(i).getSchdSuplRentPayNow()));
					rpyQueueHeader.setIncrCost(rpyQueueHeader.getIncrCost().add(repaySchdList.get(i).getSchdIncrCostPayNow()));

					// Waiver Amounts
					rpyQueueHeader.setPriWaived(rpyQueueHeader.getPriWaived().add(finRepayQueue.getSchdPriWaivedNow()));
					rpyQueueHeader.setPftWaived(rpyQueueHeader.getPftWaived().add(finRepayQueue.getSchdPftWaivedNow()));
					rpyQueueHeader.setLatePftWaived(rpyQueueHeader.getLatePftWaived().add(finRepayQueue.getLatePayPftWaivedNow()));
					rpyQueueHeader.setPenaltyWaived(rpyQueueHeader.getPenaltyWaived().add(finRepayQueue.getWaivedAmount()));
					rpyQueueHeader.setFeeWaived(rpyQueueHeader.getFeeWaived().add(finRepayQueue.getSchdFeeWaivedNow()));
					rpyQueueHeader.setInsWaived(rpyQueueHeader.getInsWaived().add(finRepayQueue.getSchdInsWaivedNow()));
					rpyQueueHeader.setSuplRentWaived(rpyQueueHeader.getSuplRentWaived().add(finRepayQueue.getSchdSuplRentWaivedNow()));
					rpyQueueHeader.setIncrCostWaived(rpyQueueHeader.getIncrCostWaived().add(finRepayQueue.getSchdIncrCostWaivedNow()));

					finRepayQueues.add(finRepayQueue);
				}
			}

			//Repayments Process For Schedule Repay List	
			rpyQueueHeader.setQueueList(finRepayQueues);
			rpyQueueHeader.setPayType(receiptDetail.getPaymentType());
			rpyQueueHeader.setPostBranch(postBranch);
			rpyQueueHeader.setPartnerBankAc(receiptDetail.getPartnerBankAc());
			rpyQueueHeader.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
			rpyQueueHeader.setPftChgAccReq(pftChgAccReq);
			rpyQueueHeader.setExtDataMap(extDataMap);

			returnList = getRepayPostingUtil().postingProcess(financeMain, scheduleDetails, finFeeDetailList,profitDetail,
					rpyQueueHeader, eventCode, valuedate,postDate);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		// Principal Amount
		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPftWaivedNow(rsd.getPftSchdWaivedNow());
		finRepayQueue.setSchdTdsPayNow(rsd.getTdsSchdPayNow());

		// Profit Amount
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPriWaivedNow(rsd.getPriSchdWaivedNow());

		// Late Pay Profit Amount
		finRepayQueue.setLatePayPftPayNow(rsd.getLatePftSchdPayNow());
		finRepayQueue.setLatePayPftWaivedNow(rsd.getLatePftSchdWaivedNow());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());
		finRepayQueue.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());
		finRepayQueue.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());
		finRepayQueue.setSchdSuplRentWaivedNow(rsd.getSchdSuplRentWaivedNow());

		//	4. Schedule Increased Cost Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());
		finRepayQueue.setSchdIncrCostWaivedNow(rsd.getSchdIncrCostWaivedNow());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo,
			BigDecimal balPayNow, Date valueDate) {
		logger.debug("Entering");
		
		if (rsd == null) {
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));

			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));

			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));

			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));

			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));

			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate));
			rsd.setDaysEarly(0);
		}

		// Principal Payment 
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPrincipalSchdPayNow(balPayNow);
		}

		// Profit Payment 
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setProfitSchdPayNow(balPayNow);
		}

		// Late Payment Profit Payment 
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdPayNow(balPayNow);
		}

		// Fee Detail Payment 
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeePayNow(balPayNow);
		}
		
		// TDS Payment 
		if(rpyTo == RepayConstants.REPAY_TDS){
			rsd.setTdsSchdPayNow(balPayNow);
		}

		// Insurance Detail Payment 
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsPayNow(balPayNow);
		}

		// Penalty Charge Detail Payment 
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			rsd.setPenaltyPayNow(balPayNow);
		}

		logger.debug("Leaving");
		return rsd;

	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		logger.debug("Leaving");
		return finSchData;
	}

	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			curSchd.setFinReference(scheduleData.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = DateUtility.getAppDate();
			for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
				scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
				scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
				scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
				scheduleData.getDisbursementDetails().get(i).setLogKey(logKey);
			}
			getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		//Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for Fetching Accounting Event Code based on Finance Event Action
	 * 
	 * @param finEvent
	 * @return
	 */
	private String getEventCode(String finEvent) {

		if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			return AccountEventConstants.ACCEVENT_REPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			return AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (StringUtils.equals(finEvent, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			return AccountEventConstants.ACCEVENT_EARLYSTL;
		}
		return null;
	}

	private Date getPostDate(Date appDate) {
		Calendar cal = Calendar.getInstance();
		Calendar appCal = Calendar.getInstance();
		cal.setTime(DateUtility.getSysDate());
		appCal.setTime(appDate);
		cal.set(Calendar.YEAR, appCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, appCal.get(Calendar.MONTH));
		cal.set(Calendar.DATE, appCal.get(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * Method for Sorting Schedule Details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}
	
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public ReceiptAllocationDetailDAO getAllocationDetailDAO() {
		return allocationDetailDAO;
	}
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
