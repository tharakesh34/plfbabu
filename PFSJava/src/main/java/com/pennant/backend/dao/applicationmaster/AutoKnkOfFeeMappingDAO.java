package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennanttech.pff.core.TableType;

public interface AutoKnkOfFeeMappingDAO {

	AutoKnockOffFeeMapping getKnockOffMappingByID(AutoKnockOffFeeMapping feeMapping, TableType tableType);

	List<AutoKnockOffFeeMapping> getKnockOffMappingListByPayableName(long knockOffId, TableType tableType);

	void update(AutoKnockOffFeeMapping feeMapping, TableType tableType);

	long save(AutoKnockOffFeeMapping feeMapping, TableType tableType);

	void delete(AutoKnockOffFeeMapping feeMapping, TableType tableType);

	void deleteByPayableType(String feeTypeCode, TableType tableType);

	void delete(long knockOffId, TableType tableType);

	boolean isDuplicatefeeTypeId(long id, int feeTypeId, TableType tableType);

	boolean isDuplicatefeeTypeOrder(long id, int feeOrder, TableType tableType);

}
