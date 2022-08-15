package com.pennanttech.pff.overdraft.dao;

import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;

public interface VariableOverdraftScheduleDAO {

	boolean isFileNameExist(String fileName, String type);

	long saveHeader(VariableOverdraftSchdHeader scheduleHeader, String tableType);

	VariableOverdraftSchdHeader getHeader(String finReference, String finEvent, String tableType);

	List<VariableOverdraftSchdDetail> getDetails(long uploadId, String tableType);

	void delete(VariableOverdraftSchdHeader uploadVariableODSchdGeader, TableType tableType);

	void deleteById(long uploadId, TableType tableType);

	void saveDetails(List<VariableOverdraftSchdDetail> details, String tableType);
}
