package com.pennant.backend.service.reports.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.reports.ReschedulReportGenerationDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RescheduleLog;
import com.pennant.backend.model.finance.RescheduleLogHeader;
import com.pennant.backend.service.reports.RescheduleReportGenerationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

public class RescheduleReportGenerationServiceImpl implements RescheduleReportGenerationService {
	private static final Logger logger = LogManager.getLogger(RescheduleReportGenerationServiceImpl.class);

	private ReschedulReportGenerationDAO rescheduleReportGenerationDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	public List<RescheduleLog> getReschedulementList(long finID, Date fromDate, Date toDate) {
		logger.debug(Literal.ENTERING);

		List<FinLogEntryDetail> detailList = rescheduleReportGenerationDAO.getFinLogEntryDetailList(finID, fromDate,
				toDate);

		if (CollectionUtils.isEmpty(detailList)) {
			return new ArrayList<>();
		}

		List<RescheduleLog> loglist = new ArrayList<>();

		for (FinLogEntryDetail finLogEntryDetail : detailList) {
			RescheduleLog rescheduleLog = new RescheduleLog();
			rescheduleLog.setFinID(finLogEntryDetail.getFinID());
			rescheduleLog.setFinReference(finLogEntryDetail.getFinReference());
			FinServiceInstruction instruction = getServiceInstruction(rescheduleLog, finLogEntryDetail.getLogKey());

			if (instruction.getLogKey() == null) {
				continue;
			}

			rescheduleLog.setTransactionDate(finLogEntryDetail.getPostDate());
			rescheduleLog.setTransactionType(instruction.getFinEvent());
			getCustBasicDetails(rescheduleLog, finID);
			getReschedulementDetails(rescheduleLog, finLogEntryDetail.getLogKey());
			loglist.add(rescheduleLog);
		}

		logger.debug(Literal.LEAVING);
		return loglist;
	}

	private FinServiceInstruction getServiceInstruction(RescheduleLog rescheduleLog, long logKey) {
		logger.debug(Literal.ENTERING);

		List<FinServiceInstruction> instructions = new ArrayList<>();
		instructions = this.rescheduleReportGenerationDAO.getFinServiceInstructions(rescheduleLog.getFinID(), logKey);

		if (CollectionUtils.isEmpty(instructions)) {
			return new FinServiceInstruction();
		}

		logger.debug(Literal.LEAVING);
		return instructions.get(0);
	}

	private void getReschedulementDetails(RescheduleLog rescheduleLog, long logKey) {
		logger.debug(Literal.ENTERING);

		long finID = rescheduleLog.getFinID();
		List<FinServiceInstruction> instructions = this.rescheduleReportGenerationDAO.getFinServiceInstructions(finID);

		for (FinServiceInstruction instruction : instructions) {
			if (instruction.getLogKey() == logKey) {
				List<FinanceScheduleDetail> schdDetails = this.rescheduleReportGenerationDAO.getScheduleDetails(finID,
						"_LOG", instruction.getLogKey());
				getBeforeReschedulementDetails(schdDetails, rescheduleLog, instruction, false);
				break;
			}
		}

		for (FinServiceInstruction instruction : instructions) {
			if (instruction.getLogKey() != null && instruction.getLogKey() > logKey) {
				List<FinanceScheduleDetail> schdDetails = this.rescheduleReportGenerationDAO.getScheduleDetails(finID,
						"_LOG", instruction.getLogKey());
				getBeforeReschedulementDetails(schdDetails, rescheduleLog, instruction, true);
				break;
			}
		}

		if (rescheduleLog.getNewEMIAmt().compareTo(BigDecimal.ZERO) == 0 && rescheduleLog.getNewTenure() == 0) {
			List<FinanceScheduleDetail> schdDetails = this.rescheduleReportGenerationDAO.getScheduleDetails(finID, "",
					0);
			getNewReschedulementDetails(schdDetails, rescheduleLog, logKey);
		}

		logger.debug(Literal.LEAVING);
	}

	private void getNewReschedulementDetails(List<FinanceScheduleDetail> schdDetails, RescheduleLog rescheduleLog,
			long logKey) {
		logger.debug(Literal.ENTERING);

		FinServiceInstruction instruction = getServiceInstruction(rescheduleLog, logKey);

		if (CollectionUtils.isNotEmpty(schdDetails)) {

			for (FinanceScheduleDetail curSchd : schdDetails) {
				if (DateUtil.compare(curSchd.getSchDate(), instruction.getFromDate()) > 0
						&& curSchd.isRepayOnSchDate()) {
					BigDecimal repayAmt = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd())
							.add(curSchd.getFeeSchd());
					rescheduleLog.setNewEMIAmt(PennantApplicationUtil.formateAmount(repayAmt, 2));
					break;
				}
			}

			int count = 0;
			FinanceProfitDetail pfd = this.rescheduleReportGenerationDAO.getProfitDetail(instruction.getFinID());

			for (FinanceScheduleDetail curSchd : schdDetails) {
				String BpiorHoliday = curSchd.getBpiOrHoliday() == null ? "" : curSchd.getBpiOrHoliday();
				if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
					if ((curSchd.isFrqDate() && !isHoliday(StringUtils.trimToEmpty(BpiorHoliday)))
							|| curSchd.getSchDate().compareTo(pfd.getMaturityDate()) == 0) {
						if (!FinanceConstants.FLAG_BPI.equals(BpiorHoliday)) {
							count++;
						}
					}
				}
			}

			rescheduleLog.setNewTenure(count);
		}
		logger.debug(Literal.LEAVING);
	}

	private void getBeforeReschedulementDetails(List<FinanceScheduleDetail> schdDetails, RescheduleLog rescheduleLog,
			FinServiceInstruction instruction, boolean isAfterSchd) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(schdDetails)) {
			return;
		}

		for (FinanceScheduleDetail curSchd : schdDetails) {
			if (DateUtil.compare(curSchd.getSchDate(), instruction.getFromDate()) > 0 && curSchd.isRepayOnSchDate()) {
				BigDecimal repayAmt = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).add(curSchd.getFeeSchd());
				if (isAfterSchd) {
					rescheduleLog.setNewEMIAmt(PennantApplicationUtil.formateAmount(repayAmt, 2));
				} else {
					rescheduleLog.setOldEMIAmt(PennantApplicationUtil.formateAmount(repayAmt, 2));
				}
				break;
			}
		}

		int count = 0;
		FinanceProfitDetail pfd = this.rescheduleReportGenerationDAO.getProfitDetail(instruction.getFinID());

		for (FinanceScheduleDetail curSchd : schdDetails) {
			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
				String BpiorHoliday = curSchd.getBpiOrHoliday() == null ? "" : curSchd.getBpiOrHoliday();
				if ((curSchd.isFrqDate() && !isHoliday(StringUtils.trimToEmpty(BpiorHoliday)))
						|| curSchd.getSchDate().compareTo(pfd.getMaturityDate()) == 0) {
					if (!StringUtils.equals(BpiorHoliday, FinanceConstants.FLAG_BPI)) {
						count++;
					}
				}
			}
		}

		if (isAfterSchd) {
			rescheduleLog.setNewTenure(count);
		} else {
			rescheduleLog.setOldTenure(count);
		}

		logger.debug(Literal.LEAVING);
	}

	private static boolean isHoliday(String bpiOrHoliday) {
		switch (bpiOrHoliday) {
		case FinanceConstants.FLAG_HOLIDAY:
		case FinanceConstants.FLAG_POSTPONE:
		case FinanceConstants.FLAG_MORTEMIHOLIDAY:
		case FinanceConstants.FLAG_UNPLANNED:
			return true;

		default:
			return false;
		}
	}

	private void getCustBasicDetails(RescheduleLog reschedulementLog, long finID) {
		RescheduleLog reschedulement = this.rescheduleReportGenerationDAO.getFinBasicDetails(finID);
		reschedulementLog.setFinBranch(reschedulement.getFinBranch());
		reschedulementLog.setCustName(reschedulement.getCustName());
	}

	@Override
	public List<RescheduleLogHeader> getReschedulementList(Date fromDate, Date toDate) {
		List<RescheduleLogHeader> logHeaderList = rescheduleReportGenerationDAO.getFinBasicDetails();

		for (RescheduleLogHeader reSchdLog : logHeaderList) {
			long finID = reSchdLog.getFinID();
			reSchdLog.setRescheduleLogList(getReschedulementList(finID, fromDate, toDate));
		}

		return logHeaderList;
	}

	@Override
	public long getFinIDByFinReference(String finReference) {
		return financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
	}

	public void setRescheduleReportGenerationDAO(ReschedulReportGenerationDAO rescheduleReportGenerationDAO) {
		this.rescheduleReportGenerationDAO = rescheduleReportGenerationDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
