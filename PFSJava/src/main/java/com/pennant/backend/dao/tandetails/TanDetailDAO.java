package com.pennant.backend.dao.tandetails;

import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pff.core.TableType;

public interface TanDetailDAO {

	void update(TanDetail tanMapping, TableType tableType);

	long save(TanDetail tanMapping, TableType tableType);

	void delete(TanDetail tanMapping, TableType tableType);

	TanDetail getTanDetailList(long custId, TableType view);

	boolean isTanNumberAvailable(String tanNumber, String tanHolderName, TableType mainTab);

	long getTanIdByTanNumber(String tanNumber, TableType mainTab);

	boolean isDuplicateKey(long id, String tanNumber, TableType tableType);

}
