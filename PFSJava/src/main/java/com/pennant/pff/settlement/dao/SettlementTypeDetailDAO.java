package com.pennant.pff.settlement.dao;

import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennanttech.pff.core.TableType;

public interface SettlementTypeDetailDAO {

	SettlementTypeDetail getSettlementByCode(String code, String type);

	SettlementTypeDetail getSettlementById(long id, String type);

	long save(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	void update(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	void delete(SettlementTypeDetail settlementTypeDetail, TableType tableType);

	boolean isDuplicateKey(String settlementCode, long settlementTypeID, TableType tableType);
}
