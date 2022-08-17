package com.pennant.backend.service.finance.manual.schedule.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.manual.schedule.ManualScheduleDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.service.finance.manual.schedule.ManualScheduleService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class ManualScheduleServiceImpl implements ManualScheduleService {
	private static final Logger logger = LogManager.getLogger(ManualScheduleServiceImpl.class);

	private ManualScheduleDAO manualScheduleDAO;

	@Override
	public boolean isFileNameExist(String fileName) {
		return this.manualScheduleDAO.isFileNameExist(fileName, "_View");
	}

	@Override
	public ManualScheduleHeader getManualScheduleDetails(long finID, String finEvent, TableType tableType) {
		String type = tableType.getSuffix();

		ManualScheduleHeader scheduleHeader = this.manualScheduleDAO.getManualSchdHeader(finID, finEvent, type);

		if (scheduleHeader == null) {
			return scheduleHeader;
		}

		List<ManualScheduleDetail> scheduleDetails = this.manualScheduleDAO
				.getManualSchdDetailsById(scheduleHeader.getId(), type);
		scheduleHeader.setManualSchedules(scheduleDetails);

		return scheduleHeader;
	}

	@Override
	public void saveOrUpdate(FinanceDetail fd, String moduleDefiner, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (!isProcced(moduleDefiner)) {
			return;
		}

		FinScheduleData schd = fd.getFinScheduleData();
		FinanceMain fm = schd.getFinanceMain();
		String finReference = fm.getFinReference();
		long finID = fm.getFinID();

		ManualScheduleHeader mshTemp = this.manualScheduleDAO.getManualSchdHeader(finID, moduleDefiner,
				tableType.getSuffix());

		if (mshTemp != null) {
			this.manualScheduleDAO.deleteById(mshTemp.getId(), tableType);
			this.manualScheduleDAO.delete(mshTemp, tableType);
		}

		ManualScheduleHeader msh = schd.getManualScheduleHeader();

		if (msh == null || msh.getFileName() == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		msh.setFinReference(finReference);

		String moduleDef = fd.getModuleDefiner();
		if (StringUtils.trimToNull(moduleDef) == null || FinServiceEvent.ORG.equals(moduleDef)) {
			msh.setFinEvent(FinServiceEvent.ORG);
		} else {
			msh.setFinEvent(moduleDef);
		}

		msh.setTotalSchedules(msh.getNumberOfTerms());

		long id = this.manualScheduleDAO.saveHeaderDetails(msh, tableType.getSuffix());

		List<ManualScheduleDetail> manualSchedules = msh.getManualSchedules();

		for (ManualScheduleDetail manualSchedule : manualSchedules) {
			if (fm.getFinStartDate().compareTo(manualSchedule.getSchDate()) == 0) {
				continue;
			}

			manualSchedule.setHeaderId(id);
		}

		this.manualScheduleDAO.saveManualSchdDetails(manualSchedules, tableType.getSuffix());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doApprove(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		if (!isProcced(financeDetail.getModuleDefiner())) {
			return;
		}

		ManualScheduleHeader scheduleHeader = financeDetail.getFinScheduleData().getManualScheduleHeader();

		if (scheduleHeader == null) {
			return;
		}

		List<ManualScheduleDetail> manualSchedules = scheduleHeader.getManualSchedules();

		if (CollectionUtils.isEmpty(manualSchedules)) {
			return;
		}

		this.manualScheduleDAO.saveHeaderDetails(scheduleHeader, "");
		this.manualScheduleDAO.saveManualSchdDetails(manualSchedules, "");

		// Delete in TEMP Tables
		long headerId = scheduleHeader.getId();
		this.manualScheduleDAO.deleteById(headerId, TableType.TEMP_TAB);
		this.manualScheduleDAO.delete(scheduleHeader, TableType.TEMP_TAB);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		if (!isProcced(financeDetail.getModuleDefiner())) {
			return;
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		if (!financeMain.isManualSchedule()) {
			return;
		}

		ManualScheduleHeader schdHeader = financeDetail.getFinScheduleData().getManualScheduleHeader();
		if (schdHeader != null) {
			this.manualScheduleDAO.deleteById(schdHeader.getId(), TableType.TEMP_TAB);
			this.manualScheduleDAO.delete(schdHeader, TableType.TEMP_TAB);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isProcced(String moduleDefiner) {
		if (FinServiceEvent.ADDDISB.equals(moduleDefiner) || FinServiceEvent.RECALCULATE.equals(moduleDefiner)
				|| FinServiceEvent.RECEIPT.equals(moduleDefiner) || FinServiceEvent.ORG.equals(moduleDefiner)) {
			return true;
		}

		return false;
	}

	public void setManualScheduleDAO(ManualScheduleDAO manualScheduleDAO) {
		this.manualScheduleDAO = manualScheduleDAO;
	}

}
