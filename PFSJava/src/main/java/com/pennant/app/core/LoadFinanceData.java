package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.ProjectedAmortization;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;

public class LoadFinanceData extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(LoadFinanceData.class);

	public void prepareFinEODEvents(CustEODEvent custEODEvent) {
		Customer customer = custEODEvent.getCustomer();

		List<FinanceMain> custFinMains = financeMainDAO.getFinMainsForEODByCustId(customer);
		List<FinanceProfitDetail> custpftDet = financeProfitDetailDAO.getFinProfitDetailsByCustId(customer);

		EventProperties eventProperties = custEODEvent.getEventProperties();

		Date eodDate = custEODEvent.getEodValueDate();

		logger.info("Total Finances >> {}", custFinMains.size());

		for (FinanceMain fm : custFinMains) {
			long finID = fm.getFinID();
			Date matDate = fm.getMaturityDate();

			fm.setEOD(true);

			logger.info("Loading finance details for the FinID >> {} started...", finID);

			FinEODEvent finEODEvent = new FinEODEvent();

			fm.setEventProperties(eventProperties);

			finEODEvent.setFinanceMain(fm);

			finEODEvent.setFinType(getFinanceType(fm.getFinType()));

			FinanceProfitDetail pfd = getFinPftDetailRef(finID, custpftDet);

			if (pfd == null) {
				throw new AppException(String.format("%s FinReference is not in active state in FinPftDetails table.",
						fm.getFinReference()));
			}

			pfd.setEventProperties(eventProperties);

			finEODEvent.setFinProfitDetail(pfd);

			List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
			finEODEvent.setFinanceScheduleDetails(schedules);

			List<FinanceScheduleDetail> orgFinSchdDetails = new ArrayList<>();
			for (FinanceScheduleDetail schd : schedules) {
				orgFinSchdDetails.add(schd.copyEntity());
			}

			finEODEvent.setOrgFinSchdDetails(orgFinSchdDetails);

			finEODEvent.setFinExcessAmounts(finExcessAmountDAO.getAllExcessAmountsByRef(finID, ""));

			if (fm.isAllowSubvention()) {
				finEODEvent.setSubventionDetail(subventionDetailDAO.getSubventionDetail(finID, ""));
			}

			finEODEvent.setNpaStage(assetClassificationService.isEffNpaStage(finID));

			if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
				finEODEvent.setCancelManualAdvises(manualAdviseDAO.getAdvisesByMaturityDate(finID, matDate));
				finEODEvent.setPostingManualAdvises(manualAdviseDAO.getAdvisesByValueDate(finID, eodDate));
				finEODEvent.getPostingManualAdvises()
						.forEach(ma -> ma.setFeeType(feeTypeDAO.getFeeTypeById(ma.getFeeTypeID(), "_AView")));
			}

			if (ProductUtil.isOverDraft(fm)) {
				finEODEvent.setOverDraftFM(overdraftLoanDAO.getLoanDetails(finID));
			}

			setEventFlags(custEODEvent, finEODEvent);
			custEODEvent.getFinEODEvents().add(finEODEvent);

			logger.info("Loading finance details for the FinID >> {} completed.", finID);
		}

		custpftDet.clear();
		custFinMains.clear();
	}

	public CustEODEvent prepareInActiveFinEODEvents(CustEODEvent custEODEvent, long finID) {
		logger.debug(Literal.ENTERING);

		FinEODEvent finEODEvent = new FinEODEvent();

		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, false);
		FinanceProfitDetail pfd = financeProfitDetailDAO.getFinProfitDetailsByFinRef(finID, false);

		// FINANCE MAIN
		finEODEvent.setFinanceMain(fm);

		// FINANCE TYPE
		finEODEvent.setFinType(getFinanceType(fm.getFinType()));

		// FINPROFIT DETAILS
		finEODEvent.setFinProfitDetail(pfd);

		// FINSCHDULE DETAILS
		finEODEvent.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));

		custEODEvent.getFinEODEvents().add(finEODEvent);

		logger.debug(Literal.LEAVING);
		return custEODEvent;
	}

	private void setEventFlags(CustEODEvent custEODEvent, FinEODEvent finEODEvent) {
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		Date valueDate = custEODEvent.getEodValueDate();
		Date businessDate = custEODEvent.getEventProperties().getBusinessDate();

		FinanceMain fm = finEODEvent.getFinanceMain();

		boolean isAmountDue = false;
		String custBranch = custEODEvent.getCustomer().getCustAddrProvince();

		// Place schedule dates to Map
		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail schd = schedules.get(i);
			// Find various events required today or not
			if (schd.getSchDate().compareTo(businessDate) == 0) {
				// Disbursement Exist
				if (schd.isDisbOnSchDate()) {
					finEODEvent.setIdxDisb(i);
					custEODEvent.setDisbExist(true);
				}

				// Fee Due Exist
				BigDecimal dueAmount = schd.getFeeSchd().subtract(schd.getSchdFeePaid());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Installment Due Exist
				dueAmount = schd.getPrincipalSchd().add(schd.getProfitSchd()).subtract(schd.getSchdPriPaid())
						.subtract(schd.getSchdPftPaid());

				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Installment Due Exist
				dueAmount = schd.getCpzAmount().subtract(schd.getCpzBalance());
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
					finEODEvent.setIdxDue(i);
					custEODEvent.setDueExist(true);
				}

				// Presentment Required
				if (schd.getDefSchdDate().compareTo(businessDate) == 0) {
					finEODEvent.setIdxPresentment(i);
					custEODEvent.setCheckPresentment(true);
				}
			}

			// Date Rollover Setting
			if (schd.getSchDate().compareTo(businessDate) == 0) {
				setDateRollover(custEODEvent, finEODEvent, schd.getSchDate(), i);
			}

			// PastDue Index Setting
			if (finEODEvent.getIdxPD() > 0) {
				continue;
			}

			// Do not Include Today Late payment Calculation
			if (ImplementationConstants.LP_MARK_FIRSTDAY) {
				if (schd.getSchDate().compareTo(valueDate) > 0) {
					continue;
				}
			} else {
				if (schd.getSchDate().compareTo(valueDate) >= 0) {
					continue;
				}
			}

			if (ImplementationConstants.ALLOW_OLDEST_DUE) {
				isAmountDue = isOldestDueOverDue(schd);
			}

			BigDecimal balanceAmount = BigDecimal.ZERO;
			if (ProductUtil.isOverDraftChargeReq(fm)) {
				balanceAmount = overdrafLoanService.calculateODAmounts(fm, schd.getSchDate(), custBranch)
						.getCurOverdraftTxnChrg();
			}

			// Paid Principal OR Paid Interest Less than scheduled amounts
			if (schd.getSchdPriPaid().compareTo(schd.getPrincipalSchd()) < 0
					|| schd.getSchdPftPaid().compareTo(schd.getProfitSchd()) < 0
					|| balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				isAmountDue = true;
			}

			if (isAmountDue) {
				finEODEvent.setIdxPD(i);
				custEODEvent.setPastDueExist(true);
			}

			if (schd.getSchDate().compareTo(businessDate) >= 0) {
				break;
			}
		}

		// Check If LPP Method on capitalization basis and Due Index not exists
		if (finEODEvent.getIdxPD() <= 0 && isLPPCpzReq(finEODEvent)) {
			finEODEvent.setIdxPD(1);
			custEODEvent.setPastDueExist(true);
		}
	}

	private void setDateRollover(CustEODEvent custEODEvent, FinEODEvent finEODEvent, Date schdDate, int iSchd) {
		FinanceMain fm = finEODEvent.getFinanceMain();

		Date grcEndDate = fm.getGrcPeriodEndDate();

		if (fm.isAllowGrcPeriod() && schdDate.compareTo(grcEndDate) <= 0) {
			// Set Next Grace Capitalization Date
			Date nextGrcCpzDate = fm.getNextGrcCpzDate();
			if (fm.isAllowGrcCpz() && nextGrcCpzDate.compareTo(grcEndDate) < 0
					&& schdDate.compareTo(nextGrcCpzDate) == 0) {
				finEODEvent.setIdxGrcCpz(iSchd);
				custEODEvent.setDateRollover(true);
			}

			// Set Next Grace Profit Date
			Date nextGrcPftDate = fm.getNextGrcPftDate();
			if (nextGrcPftDate.compareTo(grcEndDate) < 0 && schdDate.compareTo(nextGrcPftDate) == 0) {
				finEODEvent.setIdxGrcPft(iSchd);
				custEODEvent.setDateRollover(true);
			}

			// Set Next Grace Profit Review Date
			Date nextGrcPftRvwDate = fm.getNextGrcPftRvwDate();
			if (fm.isAllowGrcPftRvw() && nextGrcPftRvwDate.compareTo(grcEndDate) <= 0
					&& schdDate.compareTo(nextGrcPftRvwDate) == 0) {
				finEODEvent.setIdxGrcPftRvw(iSchd);
				custEODEvent.setDateRollover(true);
			}
		}

		// Set Next Repay Capitalization Date

		if (fm.isAllowRepayCpz() && schdDate.compareTo(fm.getNextRepayCpzDate()) == 0) {
			finEODEvent.setIdxRpyCpz(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Date
		if (schdDate.compareTo(fm.getNextRepayDate()) == 0) {
			finEODEvent.setIdxRpy(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Profit Date
		if (schdDate.compareTo(fm.getNextRepayPftDate()) == 0) {
			finEODEvent.setIdxRpyPft(iSchd);
			custEODEvent.setDateRollover(true);
		}

		// Set Next Repayment Profit Review Date
		if (fm.isAllowRepayRvw() && schdDate.compareTo(fm.getNextRepayRvwDate()) == 0) {
			finEODEvent.setIdxRpyPftRvw(iSchd);
			custEODEvent.setDateRollover(true);
		}
	}

	public void updateFinEODEvents(CustEODEvent custEODEvent) throws Exception {
		long custID = custEODEvent.getCustomer().getCustID();
		logger.info("Updating EOD Events for the CustID >> {} started...", custID);
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		List<ReturnDataSet> returnDataSets = new ArrayList<>(1);

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			long finID = fm.getFinID();
			String finReference = fm.getFinReference();
			boolean updFinMain = finEODEvent.isUpdFinMain();
			boolean rateReview = finEODEvent.isupdFinSchdForRateRvw();
			boolean monthEnd = finEODEvent.isUpdMonthEndPostings();
			boolean lbdPosted = finEODEvent.isUpdLBDPostings();
			boolean changeGraceEnd = finEODEvent.isUpdFinSchdForChangeGrcEnd();
			boolean updRepayInstruct = finEODEvent.isUpdRepayInstruct();

			logger.info("FinReference >> {}", finReference);
			logger.info("UpdFinMain >> {}", updFinMain);
			logger.info("SchdForRateRvw >> {}", rateReview);
			logger.info("MonthEndPostings >> {}", monthEnd);
			logger.info("UpdLBDPostings >> {}", lbdPosted);
			logger.info("UpdFinSchdForChangeGrcEnd >> {}", changeGraceEnd);
			logger.info("UpdRepayInstruct >> {}", updRepayInstruct);

			if (updFinMain && !changeGraceEnd) {
				logger.info("Updating FinanceMain...");
				financeMainDAO.updateFinanceInEOD(fm, finEODEvent.getFinMainUpdateFields(), rateReview);
			}

			logger.info("Updating FinPftDetails...");
			financeProfitDetailDAO.updateEOD(finEODEvent.getFinProfitDetail(), lbdPosted, monthEnd);

			if (rateReview) {
				logger.info("Updating FinScheduleDetails to effect rate review.");
				saveLMSServiceLog(finEODEvent);
				int schedules = financeScheduleDetailDAO.updateForRateReview(finEODEvent.getFinanceScheduleDetails());
				// Schedule Version Updating
				try {
					financeMainDAO.updateSchdVersion(fm, false);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				logger.info("{} Schedules effected for the rate review", schedules);
			}

			if (changeGraceEnd) {
				logger.info("Updating FinanceMain for change grace end.");
				fm.setVersion(fm.getVersion() + 1);
				fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				financeMainDAO.updateFinanceForGraceEndInEOD(fm);

				logger.info("Logging Fin Log Entry Detail into FinLogEntryDetail table...");
				long logKey = saveFinLogEntryDetail(fm);
				logger.info("FinLogEntryDetail LogKey >> {}", logKey);

				listSave(finEODEvent, "_Log", logKey);

				listDeletion(finEODEvent, FinServiceEvent.CHGGRCEND, "");

				listSave(finEODEvent, "", 0);

				List<FinanceStepPolicyDetail> spdList = finEODEvent.getStepPolicyDetails();
				if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
						&& CollectionUtils.isNotEmpty(spdList) && fm.getNoOfGrcSteps() > 0) {
					financeStepDetailDAO.deleteList(finID, false, "");
					financeStepDetailDAO.saveList(spdList, false, "");
				}
			}

			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			List<FinODDetails> odDetailsLBD = finEODEvent.getFinODDetailsLBD();
			if (odDetails != null && !odDetails.isEmpty()) {
				if (finEODEvent.getIdxPD() > 1 && isLPPCpzReq(finEODEvent)) {
					finEODEvent.setIdxPD(1);
				}

				FinanceScheduleDetail odschd = finEODEvent.getFinanceScheduleDetails().get(finEODEvent.getIdxPD());
				Date appDate = fm.getEventProperties().getAppDate();
				String productCategory = fm.getProductCategory();

				int extnODGrcDays = 0;
				if (ProductUtil.isOverDraft(productCategory)) {
					extnODGrcDays = finODPenaltyRateDAO.getExtnODGrcDays(fm.getFinID());
				}

				if (odschd != null) {
					List<FinODDetails> listSave = new ArrayList<>();
					List<FinODDetails> listupdate = new ArrayList<>();
					for (FinODDetails od : odDetails) {
						if (ProductUtil.isOverDraft(productCategory)) {
							int odGraceDays = od.getODGraceDays();

							if (!od.isODIncGrcDays()) {
								odGraceDays = odGraceDays + extnODGrcDays;
							}

							Date odSchDateIncGrc = DateUtil.addDays(odschd.getSchDate(), odGraceDays);

							if (appDate.compareTo(odSchDateIncGrc) < 0) {
								continue;
							}
						}

						if (od.getFinODSchdDate().compareTo(odschd.getSchDate()) < 0) {
							continue;
						}

						boolean exists = checkExsistInList(od, odDetailsLBD);

						if (custEODEvent.getEodDate().compareTo(DateUtil.getMonthEnd(custEODEvent.getEodDate())) == 0
								|| custEODEvent.getEventProperties().isEomOnEOD()) {
							od.setLppDueAmt(od.getTotPenaltyAmt());
							od.setLppDueTillDate(custEODEvent.getEodDate());
							od.setLpiDueAmt(od.getLPIAmt());
							od.setLpiDueTillDate(custEODEvent.getEodDate());
						}

						if (exists) {
							listupdate.add(od);
						} else {
							listSave.add(od);
						}
					}

					int savedCount = 0;
					int updateCount = 0;
					if (!listSave.isEmpty()) {
						logger.info("Saving overdue details into FinODDetails table...");
						savedCount = finODDetailsDAO.saveList(listSave);
						logger.info("{} Overdue details are created", savedCount);
					}

					if (!listupdate.isEmpty()) {
						logger.info("updating existing overdue details into FinODDetails table...");
						updateCount = finODDetailsDAO.updateODDetailsBatch(listupdate);

						logger.info("{} overdue details are created", updateCount);
					}

					List<FinOverDueCharges> finODCAmounts = finEODEvent.getFinODCAmounts();

					if (CollectionUtils.isNotEmpty(finODCAmounts)) {
						List<FinOverDueCharges> saveList = new ArrayList<>();
						List<FinOverDueCharges> updateList = new ArrayList<>();

						for (FinOverDueCharges finODCAmount : finODCAmounts) {
							if (finODCAmount.isNewRecord()) {
								saveList.add(finODCAmount);
							} else {
								updateList.add(finODCAmount);
							}
						}

						if (!saveList.isEmpty()) {
							finODCAmountDAO.saveFinODCAmts(saveList);
						}
						if (!updateList.isEmpty()) {
							finODCAmountDAO.updateFinODCAmts(updateList);
						}
					}

					logger.info("{} are the total overdue", savedCount + updateCount);
				}
			}

			if (updRepayInstruct) {
				logger.info("Deleting Repay Instructions from the FinRepayInstruction table...");
				int count = repayInstructionDAO.deleteInEOD(finID);

				logger.info("{}  Repay Instructions deleted.", count);

				List<RepayInstruction> lisRepayIns = finEODEvent.getRepayInstructions();
				for (RepayInstruction repayInstruction : lisRepayIns) {
					repayInstruction.setFinID(finID);
					repayInstruction.setFinReference(finReference);
				}

				logger.info("Saving Repay Instructions into FinRepayInstruction table...");
				count = repayInstructionDAO.saveListInEOD(lisRepayIns);
				logger.info("{}  Repay Instructions saved.", count);
			}

			if (CollectionUtils.isNotEmpty(finEODEvent.getProvisions())) {
				saveProvisions(finEODEvent);
			}

			logger.info("Updating existing DisbursementDetails into FinDisbursementDetails table...");
			List<FinanceDisbursement> disbList = new ArrayList<>();

			for (FinanceDisbursement disbursement : finEODEvent.getFinanceDisbursements()) {
				if (disbursement.isPosted()) {
					disbList.add(disbursement);
				}
			}
			int count = financeDisbursementDAO.updateBatchDisb(disbList, "");
			logger.info("{}  DisbursementDetails updated.", count);

			if (!fm.isWriteoffLoan()) {
				returnDataSets.addAll(finEODEvent.getReturnDataSet());
			}
		}

		logger.info("Saving Projected Accruals Started...");
		saveProjAccruals(custEODEvent);
		logger.info("Saving Projected Accruals Completed.");

		String entityCode = custEODEvent.getEventProperties().getEntityCode();

		returnDataSets.stream().forEach(r -> r.setEntityCode(entityCode));

		logger.info("Saving the {} - Acoounting Entries into Postings table...", returnDataSets.size());
		saveAccountingEOD(returnDataSets);
		logger.info("EOD Acoounting Entries Saved Successfully.", returnDataSets.size());

		returnDataSets.clear();

		logger.info("Updating EOD Events for the CustID >> {} completed.", custID);
	}

	private boolean checkExsistInList(FinODDetails finODDetails, List<FinODDetails> fodDetails) {

		for (FinODDetails finODDet : fodDetails) {
			if (finODDetails.getFinODSchdDate().compareTo(finODDet.getFinODSchdDate()) == 0) {
				return true;
			}
		}

		return false;
	}

	private boolean isLPPCpzReq(FinEODEvent finEODEvent) {
		List<FinODDetails> odDetails = finEODEvent.getFinODDetails();

		if (CollectionUtils.isEmpty(odDetails)) {
			return false;
		}

		for (FinODDetails finODDetail : odDetails) {
			String odChargeCalOn = finODDetail.getODChargeCalOn();
			if ((finODDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) == 0)
					&& (finODDetail.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0)
					&& (FinanceConstants.ODCALON_PIPD_FRQ.equals(odChargeCalOn)
							|| FinanceConstants.ODCALON_PIPD_EOM.equals(odChargeCalOn))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Save Provisions and Provision Movements
	 * 
	 * @param finEODEvent
	 */
	private void saveProvisions(FinEODEvent finEODEvent) {
		for (Provision provision : finEODEvent.getProvisions()) {

			long finID = provision.getFinID();

			logger.warn("Checking Old provision Details in PROVISIONS table..");
			Provision oldProvision = provisionDAO.getProvisionByFinId(finID, TableType.MAIN_TAB, false);

			long provisionId = Long.MIN_VALUE;
			int count = 0;
			if (oldProvision != null) {
				provisionId = oldProvision.getId();
				provision.setId(provisionId);
				logger.warn("Updating the existing Provision in PROVISIONS table..");
				count = provisionDAO.update(provision, TableType.MAIN_TAB);
				logger.warn("{} Record updated in PROVISIONS table..", count);
			} else {
				logger.warn("Saving the Provision in PROVISIONS table..");
				provisionId = provisionDAO.save(provision, TableType.MAIN_TAB);
				logger.warn("Provision with Id{} saved in PROVISIONS table..", provisionId);
			}

			provision.setId(Long.MIN_VALUE);
			provision.setProvisionId(provisionId);
			logger.warn("Saving the Provision Movements in PROVISION_MOVEMENTS table..");
			provisionDAO.saveMovements(provision, TableType.MAIN_TAB);

			List<ProvisionAmount> list = provision.getProvisionAmounts();
			for (ProvisionAmount provisionAmount : list) {
				provisionAmount.setProvisionId(provisionId);
			}

			if (oldProvision != null) {
				logger.warn("Getting Old provision Amounts from PROVISION_AMOUNTS table..");
				List<ProvisionAmount> oldAmounts = provisionDAO.getProvisionAmounts(oldProvision.getId(),
						TableType.MAIN_TAB);
				List<ProvisionAmount> provisionAmounts = provision.getProvisionAmounts();
				List<ProvisionAmount> newprovisionAmounts = new ArrayList<>(provisionAmounts);

				for (ProvisionAmount oldAmount : oldAmounts) {
					for (ProvisionAmount provisionAmount : provisionAmounts) {
						if (StringUtils.equals(oldAmount.getProvisionType(), provisionAmount.getProvisionType())) {
							provisionAmount.setId(oldAmount.getId());
							newprovisionAmounts.remove(provisionAmount);
						}
					}
				}
				logger.warn("Updating Old provision Amounts in PROVISION_AMOUNTS table..");
				count = provisionDAO.updateAmounts(provisionAmounts, TableType.MAIN_TAB);
				logger.warn("{} ProvisionAmounts updated in PROVISION_AMOUNTS table..", count);

				if (CollectionUtils.isNotEmpty(newprovisionAmounts)) {
					provisionDAO.saveAmounts(newprovisionAmounts, TableType.MAIN_TAB, false);
				}

			} else {
				logger.warn("Saving provision Amounts in PROVISION_AMOUNTS table..");
				count = provisionDAO.saveAmounts(list, TableType.MAIN_TAB, false);
				logger.warn("{} ProvisionAmounts saved in PROVISION_AMOUNTS table..", count);
			}
			logger.warn("Saving provision Amounts in PROVISION_MOVMENT_AMOUNTS table..");
			count = provisionDAO.saveAmounts(list, TableType.MAIN_TAB, true);
			logger.warn("{} ProvisionAmounts saved in PROVISION_MOVMENT_AMOUNTS table..", count);

		}
	}

	/**
	 * 
	 * @param custEODEvent
	 */
	public void saveProjAccruals(CustEODEvent custEODEvent) {
		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {
			List<ProjectedAccrual> projAccrualList = finEODEvent.getProjectedAccrualList();
			if (CollectionUtils.isEmpty(projAccrualList)) {
				continue;
			}
			projectedAmortizationDAO.saveBatchProjAccruals(projAccrualList);
		}
	}

	/**
	 * 
	 * @param custEODEvent
	 */
	public void saveOrUpdateIncomeAMZDetails(CustEODEvent custEODEvent) {

		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {

			List<ProjectedAmortization> projSaveAMZList = new ArrayList<>();
			List<ProjectedAmortization> projUpdateAMZList = new ArrayList<>();

			List<ProjectedAmortization> incomeAMZList = finEODEvent.getIncomeAMZList();

			// Income Amortizations
			if (CollectionUtils.isNotEmpty(incomeAMZList)) {

				for (ProjectedAmortization projectedAMZ : incomeAMZList) {
					if (projectedAMZ.isUpdProjAMZ()) {
						projUpdateAMZList.add(projectedAMZ);

					} else if (projectedAMZ.isSaveProjAMZ()) {
						projSaveAMZList.add(projectedAMZ);
					}
				}
			}

			if (!projSaveAMZList.isEmpty()) {
				projectedAmortizationDAO.saveBatchIncomeAMZ(projSaveAMZList);
			}

			if (!projUpdateAMZList.isEmpty()) {
				projectedAmortizationDAO.updateBatchIncomeAMZ(projUpdateAMZList);
			}
		}
	}

	public void updateCustomerDate(CustEODEvent custEODEvent) {
		customerDAO.updateCustAppDate(custEODEvent);
	}

	private long saveFinLogEntryDetail(FinanceMain fm) {
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setPostDate(fm.getEventProperties().getAppDate());
		entryDetail.setFinID(fm.getFinID());
		entryDetail.setFinReference(fm.getFinReference());
		entryDetail.setSchdlRecal(fm.isScheduleChange());
		entryDetail.setEventAction(AccountingEvent.GRACEEND);

		return finLogEntryDetailDAO.save(entryDetail);
	}

	public void saveLMSServiceLog(FinEODEvent finEODEvent) {
		try {

			String lmsServiceLogReq = null;
			Date appDate = null;

			FinanceMain fm = finEODEvent.getFinanceMain();

			EventProperties eventProperties = fm.getEventProperties();
			long finID = fm.getFinID();
			String finReference = fm.getFinReference();

			if (eventProperties.isParameterLoaded()) {
				lmsServiceLogReq = eventProperties.getLmsServiceLogReq();
				appDate = eventProperties.getAppDate();
			} else {
				lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
				appDate = SysParamUtil.getAppDate();
			}

			if (!PennantConstants.YES.equals(lmsServiceLogReq)) {
				return;
			}

			List<LMSServiceLog> lmsServiceLogs = new ArrayList<>();

			logger.info("Fetching Old Rate from FinScheduleDetails table...");
			BigDecimal oldRate = finServiceInstructionDAO.getOldRate(finID, appDate);
			logger.info("The Old Rate is {}", oldRate);

			List<FinanceScheduleDetail> scheduleDetail = finEODEvent.getFinanceScheduleDetails();

			if (CollectionUtils.isEmpty(scheduleDetail)) {
				return;
			}

			FinanceScheduleDetail curSchd = null;
			for (FinanceScheduleDetail detail : scheduleDetail) {
				if (detail.getSchDate().compareTo(appDate) >= 0) {
					curSchd = detail;
					break;
				}
			}

			if (curSchd == null) {
				return;
			}

			LMSServiceLog lmsServiceLog = new LMSServiceLog();
			lmsServiceLog.setOldRate(oldRate);
			lmsServiceLog.setNewRate(curSchd.getCalculatedRate());
			lmsServiceLog.setEvent(FinServiceEvent.RATECHG);
			lmsServiceLog.setFinReference(finReference);
			lmsServiceLog.setNotificationFlag(PennantConstants.NO);
			lmsServiceLog.setEffectiveDate(appDate);
			lmsServiceLogs.add(lmsServiceLog);

			if (CollectionUtils.isNotEmpty(lmsServiceLogs)) {
				logger.info("Saving {} service event into LMSServiceLog table...", FinServiceEvent.RATECHG);
				finServiceInstructionDAO.saveLMSServiceLOGList(lmsServiceLogs);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private void listSave(FinEODEvent finEODEvent, String tableType, long logKey) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		Map<Date, Integer> mapDateSeq = new HashMap<>();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (CollectionUtils.isNotEmpty(schedules)) {
			if (logKey != 0) {
				schedules = finEODEvent.getOrgFinSchdDetails();
			}

			for (FinanceScheduleDetail schd : schedules) {
				schd.setLastMntBy(fm.getLastMntBy());
				schd.setFinID(finID);
				schd.setFinReference(finReference);
				int seqNo = 0;

				if (mapDateSeq.containsKey(schd.getSchDate())) {
					seqNo = mapDateSeq.get(schd.getSchDate());
					mapDateSeq.remove(schd.getSchDate());
				}

				seqNo = seqNo + 1;
				mapDateSeq.put(schd.getSchDate(), seqNo);
				schd.setSchSeq(seqNo);
				schd.setLogKey(logKey);
			}

			if (logKey != 0) {
				logger.info("Taking backup of current RPS into FinScheduleDetails_Log table...");
			} else {
				logger.info("Saving the efected RPS into FinScheduleDetails table...");
			}

			int count = financeScheduleDetailDAO.saveList(schedules, tableType, false);
			logger.info("{} schedules saved.", count);
		}

		List<RepayInstruction> repayInstructions = finEODEvent.getRepayInstructions();
		if (CollectionUtils.isNotEmpty(repayInstructions) && finEODEvent.isUpdRepayInstruct()) {
			if (logKey != 0) {
				repayInstructions = finEODEvent.getOrgRepayInsts();
			}

			for (RepayInstruction rpayIns : repayInstructions) {
				rpayIns.setFinID(finID);
				rpayIns.setFinReference(finReference);
				rpayIns.setLogKey(logKey);
			}

			if (logKey != 0) {
				logger.info("Taking backup of current RPI into FinRepayInstruction_Log table...");
			} else {
				logger.info("Saving the efected RPI into FinRepayInstruction table...");
			}

			int count = repayInstructionDAO.saveList(repayInstructions, tableType, false);
			logger.info("{} repay instructions saved.", count);
		}

		List<FinServiceInstruction> finServiceInstructions = finEODEvent.getFinServiceInstructions();
		if (CollectionUtils.isNotEmpty(finServiceInstructions) && logKey == 0) {
			logger.info("Saving FinServiceInstructions into FinServiceInstruction table...");
			int count = finServiceInstructionDAO.saveList(finServiceInstructions, tableType);
			logger.info("{} FinServiceInstructions saved.", count);
		}
	}

	private void listDeletion(FinEODEvent finEODEvent, String finEvent, String tableType) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		long finID = fm.getFinID();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		if (CollectionUtils.isNotEmpty(schedules)) {
			logger.info("Deleting FinScheduleDetails{} for the FinEvent >> {}", tableType, finEvent);
			financeScheduleDetailDAO.deleteByFinReference(finID, tableType, false, 0);
		}

		List<RepayInstruction> repayInstructions = finEODEvent.getRepayInstructions();
		if (CollectionUtils.isNotEmpty(repayInstructions) && finEODEvent.isUpdRepayInstruct()) {
			logger.info("Deleting FinRepayInstruction{} for the FinEvent >> {}", tableType, finEvent);
			repayInstructionDAO.deleteByFinReference(finID, tableType, false, 0);
		}
	}

	private FinanceProfitDetail getFinPftDetailRef(long finID, List<FinanceProfitDetail> list) {
		for (FinanceProfitDetail pfd : list) {
			if (finID == pfd.getFinID()) {
				return pfd;
			}
		}
		return null;
	}

	public static void setLogger(Logger logger) {
		LoadFinanceData.logger = logger;
	}
}