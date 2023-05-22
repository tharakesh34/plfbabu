package com.pennant.pff.settlement.dao;

import java.util.List;

import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementAllocationDetail;
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

	boolean isSettlementTypeUsed(long settlementType, TableType tableType);

	void updateSettlementStatus(long finId, String status);

	void deleteQueue();

	long prepareQueue();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadID);

	void updateProgress(long finID, int progress);

	boolean isSettlementInitiated(long finID);
}
