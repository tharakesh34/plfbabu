package com.pennant.backend.dao.finance.putcall;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pff.core.TableType;

public interface FinOptionDAO extends BasicCrudDao<FinOption> {

	FinOption getFinOption(long id, TableType tableType);

	public List<FinOption> getFinOptions(long finID, TableType tableType);

	public List<FinOption> getPutCallAlertList();

	public void deleteByFinRef(long finID, String tableType);

}
