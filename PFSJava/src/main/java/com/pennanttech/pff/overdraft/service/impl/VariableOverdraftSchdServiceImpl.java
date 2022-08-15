package com.pennanttech.pff.overdraft.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.dao.VariableOverdraftScheduleDAO;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;
import com.pennanttech.pff.overdraft.service.VariableOverdraftSchdService;

public class VariableOverdraftSchdServiceImpl implements VariableOverdraftSchdService {
	private static final Logger logger = LogManager.getLogger(VariableOverdraftSchdServiceImpl.class);

	private VariableOverdraftScheduleDAO variableOverdraftScheduleDAO;

	@Override
	public boolean isFileNameExist(String fileName) {
		return this.variableOverdraftScheduleDAO.isFileNameExist(fileName, "_View");
	}

	@Override
	public VariableOverdraftSchdHeader getHeader(String finReference, String FinEvent, TableType tableType) {
		logger.debug(Literal.ENTERING);

		VariableOverdraftSchdHeader header = this.variableOverdraftScheduleDAO.getHeader(finReference, FinEvent,
				tableType.getSuffix());

		if (header != null) {
			header.setVariableOverdraftSchdDetails(
					this.variableOverdraftScheduleDAO.getDetails(header.getId(), tableType.getSuffix()));
		}

		logger.debug(Literal.LEAVING);
		return header;
	}

	@Override
	public void saveOrUpdate(FinanceDetail fd, String moduleDefiner, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (!FinServiceEvent.ORG.equals(moduleDefiner)) {
			return;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();

		VariableOverdraftSchdHeader tempHeader = variableOverdraftScheduleDAO.getHeader(finReference, moduleDefiner,
				tableType.getSuffix());

		if (tempHeader != null) {
			this.variableOverdraftScheduleDAO.deleteById(tempHeader.getId(), tableType);
			this.variableOverdraftScheduleDAO.delete(tempHeader, tableType);
		}

		VariableOverdraftSchdHeader header = fd.getFinScheduleData().getVariableOverdraftSchdHeader();

		if (header != null) {
			header.setFinReference(finReference);
			if (StringUtils.trimToNull(fd.getModuleDefiner()) == null
					|| FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
				header.setFinEvent(FinServiceEvent.ORG);
			} else {
				header.setFinEvent(fd.getModuleDefiner());
			}

			header.setTotalSchedules(header.getNumberOfTerms());

			long id = this.variableOverdraftScheduleDAO.saveHeader(header, tableType.getSuffix());

			List<VariableOverdraftSchdDetail> details = header.getVariableOverdraftSchdDetails();

			for (VariableOverdraftSchdDetail detail : details) {
				if (fm.getFinStartDate().compareTo(detail.getSchDate()) == 0) {
					continue;
				}
				detail.setHeaderId(id);
			}
			this.variableOverdraftScheduleDAO.saveDetails(details, tableType.getSuffix());
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doApprove(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		if (!FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			return;
		}

		VariableOverdraftSchdHeader header = fd.getFinScheduleData().getVariableOverdraftSchdHeader();

		if (header == null) {
			return;
		}

		List<VariableOverdraftSchdDetail> details = header.getVariableOverdraftSchdDetails();

		if (CollectionUtils.isEmpty(details)) {
			return;
		}

		this.variableOverdraftScheduleDAO.saveHeader(header, "");
		this.variableOverdraftScheduleDAO.saveDetails(details, "");

		// Delete in TEMP Tables
		long headerId = header.getId();
		this.variableOverdraftScheduleDAO.deleteById(headerId, TableType.TEMP_TAB);
		this.variableOverdraftScheduleDAO.delete(header, TableType.TEMP_TAB);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		if (!FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			return;
		}

		VariableOverdraftSchdHeader header = fd.getFinScheduleData().getVariableOverdraftSchdHeader();

		if (header != null) {
			List<VariableOverdraftSchdDetail> details = header.getVariableOverdraftSchdDetails();

			if (CollectionUtils.isEmpty(details)) {
				return;
			}

			this.variableOverdraftScheduleDAO.deleteById(header.getId(), TableType.TEMP_TAB);
			this.variableOverdraftScheduleDAO.delete(header, TableType.TEMP_TAB);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setVariableOverdraftScheduleDAO(VariableOverdraftScheduleDAO variableOverdraftScheduleDAO) {
		this.variableOverdraftScheduleDAO = variableOverdraftScheduleDAO;
	}

}
