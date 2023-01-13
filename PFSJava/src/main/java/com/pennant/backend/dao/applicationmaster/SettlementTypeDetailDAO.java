package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennanttech.pff.core.TableType;

public interface SettlementTypeDetailDAO {

	SettlementTypeDetail getSettlementByCode(String code, String type);

	SettlementTypeDetail getSettlementById(long id, String string);

	long save(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	void update(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	void delete(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	boolean isDuplicateKey(String settlementCode, long settlementTypeID, TableType tableType);

}
