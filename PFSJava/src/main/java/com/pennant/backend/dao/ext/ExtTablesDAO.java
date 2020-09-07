package com.pennant.backend.dao.ext;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.ExtTable;

public interface ExtTablesDAO {

	void saveCtrlTableData(ExtTable extTable);

	void updateCtrlTableStatus(String syscode, Date cobdate);

	void saveBenchMarkData(ExtTable extTable);

	String insertPushData(String tabdata, String ouptut, String messageReturn);

	void saveODAccDetails(int refId, String repayAccNum);

	void deleteODAccDetails();

}
