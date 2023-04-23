package com.pennanttech.pff.knockoff.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.knockoff.dao.ExcessKnockOffDAO;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;

public class ExcessKnockOffServiceImpl implements ExcessKnockOffService {

	private ExcessKnockOffDAO excessKnockOffDAO;

	public ExcessKnockOffServiceImpl() {
		super();
	}

	@Override
	public void logExcessForCrossLoanKnockOff(Date valueDate, String executionDay, String thresholdValue) {
		this.excessKnockOffDAO.logExcessForCrossLoanKnockOff(valueDate, executionDay, thresholdValue);

	}

	@Override
	public long prepareQueue() {
		return this.excessKnockOffDAO.prepareQueue();
	}

	@Override
	public void handleFailures() {
		this.excessKnockOffDAO.handleFailures();
	}

	@Override
	public long getQueueCount() {
		return this.excessKnockOffDAO.getQueueCount();
	}

	@Override
	public int updateThreadID(long from, long to, int i) {
		return this.excessKnockOffDAO.updateThreadID(from, to, i);
	}

	@Override
	public void updateProgress(CustomerCoreBank ccb, int progressInProcess) {
		this.excessKnockOffDAO.updateProgress(ccb, progressInProcess);
	}

	@Override
	public List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank) {
		return this.excessKnockOffDAO.loadData(customerCoreBank);

	}

	@Autowired
	public void setExcessKnockOffDAO(ExcessKnockOffDAO excessKnockOffDAO) {
		this.excessKnockOffDAO = excessKnockOffDAO;
	}

	@Override
	public List<ExcessKnockOffDetails> getStageDataByReference(long finID) {
		return this.excessKnockOffDAO.getStageDataByReference(finID);
	}

	@Override
	public List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finId) {
		return this.excessKnockOffDAO.getLoansbyCustId(custId, coreBankId, finId);
	}

}
