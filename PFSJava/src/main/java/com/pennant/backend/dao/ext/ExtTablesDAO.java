package com.pennant.backend.dao.ext;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.ExtTable;
import com.pennant.backend.model.finance.salary.FinSalariedPayment;

public interface ExtTablesDAO {

	List<ExtTable> getPDDetails();
	void saveCtrlTableData(ExtTable extTable);
	void updateCtrlTableStatus(String syscode,Date cobdate);
	void saveBenchMarkData(ExtTable extTable);
	
	String insertPushData(String tabdata,String ouptut,String messageReturn);
	void deleteByid(ExtTable autoHunting);

	void updateByid(ExtTable autoHunting);

	void updateBatch(List<ExtTable> autoHunting);
	void saveODAccDetails(int refId, String repayAccNum);
	void deleteODAccDetails();
	
	void saveFinSalariedPayment(FinSalariedPayment salariedPayment);

}
