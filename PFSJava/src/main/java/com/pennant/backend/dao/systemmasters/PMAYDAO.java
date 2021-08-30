package com.pennant.backend.dao.systemmasters;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennanttech.pff.core.TableType;

public interface PMAYDAO extends BasicCrudDao<PMAY> {

	PMAY getPMAY(long finID, String type);

	boolean isDuplicateKey(long finID, TableType tableType);

	boolean isFinReferenceExists(long finID);

	String save(PmayEligibilityLog pMaylog, TableType tableType);

	void update(PmayEligibilityLog pMaylog, TableType tableType);

	PmayEligibilityLog getEligibilityLog(long finID, String type);

	public long generateDocSeq();

	List<PmayEligibilityLog> getEligibilityLogList(long finID, String type);

	List<PmayEligibilityLog> getAllRecordIdForPmay();

	void update(PmayEligibilityLog pmayEligibilityLog);

	String getCustCif(long finID);

	void update(String reference, String applicantId);
}
