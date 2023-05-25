package com.pennanttech.pff.knockoff.service;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;

public interface ExcessKnockOffService {

	List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank);

	List<ExcessKnockOffDetails> getStageDataByID(long id);

	List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finID);

	void process(ExcessKnockOff ekf, FinanceMain fm);
}