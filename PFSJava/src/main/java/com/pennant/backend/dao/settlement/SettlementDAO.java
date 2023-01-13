package com.pennant.backend.dao.settlement;

import java.util.List;

import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.model.settlement.SettlementAllocationDetail;
import com.pennanttech.pff.core.TableType;

public interface SettlementDAO {

	FinSettlementHeader getSettlementById(long id, String type);

	void delete(FinSettlementHeader settlement, String string);

	long save(FinSettlementHeader settlement, String tableType);

	void update(FinSettlementHeader settlement, String tableType);

	boolean isDuplicateKey(long settlementTypeId, String finRefernce, long headerId, TableType tableType);

	FinSettlementHeader getSettlementByRef(String finReference, String type);

	FinSettlementHeader getSettlementByFinID(long finID, String type);

	FinSettlementHeader getInitiateSettlementByFinID(long finID, String type);

	void saveSettlementAllcDetails(SettlementAllocationDetail settlementAllocationDetail, String tableType);

	void deleteSettlementAllcById(SettlementAllocationDetail settlementAllocationDetail, String tableType);

	void deleteSettlementAllcByHeaderId(long headerId, String type);

	List<SettlementAllocationDetail> getSettlementAllcDetailByHdrID(long headerId, String tableType);

	void updateSettlementStatus(long settlementHeaderID, String status);

}
