package com.pennant.backend.dao.applicationmaster;

import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennanttech.pff.core.TableType;

public interface AutoKnockOffDAO extends BasicCrudDao<AutoKnockOff> {

	public String save(AutoKnockOff knockOff, TableType tableType);

	AutoKnockOff getAutoKnockOffCode(long id, TableType tableType);

	AutoKnockOff getAutoKnockOffCode(String code, TableType tableType);

	boolean isDuplicateKey(long id, String code, TableType tableType);

	List<AutoKnockOff> getKnockOffDetails(long finID);

	void logExcessForKnockOff(Date valueDate, String day, String thresholdValue);

	void truncateData();

	long logKnockOffDetails(Date valueDate, String day);

	List<AutoKnockOffExcess> getKnockOffExcess(long custID, Date valueDate);

	public List<AutoKnockOffExcessDetails> getKnockOffExcessDetails(long id);

	void updateExcessData(AutoKnockOffExcess knockOff);

	void updateExcessDetails(List<AutoKnockOffExcessDetails> knockOffExcess);

	void backupExecutionData();
}