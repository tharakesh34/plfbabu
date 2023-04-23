package com.pennanttech.pff.knockoff.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;

public interface ExcessKnockOffDAO {

	void logExcessForCrossLoanKnockOff(Date valueDate, String executionDay, String thresholdValue);

	long prepareQueue();

	void handleFailures();

	long getQueueCount();

	int updateThreadID(long from, long to, int i);

	void deleteQueue();

	void updateProgress(CustomerCoreBank ccb, int progressInProcess);

	List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank);

	void logExcessForCrossLoanDetails(Date valueDate, String day);

	List<ExcessKnockOffDetails> getStageDataByID(long id);

	List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finId);

}
