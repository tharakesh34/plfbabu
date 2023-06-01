package com.pennanttech.pff.knockoff.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;

public interface ExcessKnockOffDAO {

	void clearStageData();

	long logExcessForCrossLoanKnockOff(Date valueDate, String executionDay, String thresholdValue);

	List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank);

	void logExcessForCrossLoanDetails(Date valueDate, String day);

	List<ExcessKnockOffDetails> getStageDataByID(long id);

	List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finId);

}
