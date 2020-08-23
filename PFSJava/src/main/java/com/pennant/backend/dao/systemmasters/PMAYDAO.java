package com.pennant.backend.dao.systemmasters;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennanttech.pff.core.TableType;

public interface PMAYDAO extends BasicCrudDao<PMAY> {

	PMAY getPMAY(String finReference, String type);

	boolean isDuplicateKey(String finReference, TableType tableType);

	boolean isFinReferenceExists(String finReference);

	String save(PmayEligibilityLog pMaylog, TableType tableType);

	void update(PmayEligibilityLog pMaylog, TableType tableType);

	PmayEligibilityLog getEligibilityLog(String finReference, String type);

	public long generateDocSeq();

	List<PmayEligibilityLog> getEligibilityLogList(String finReference, String type);

	List<PmayEligibilityLog> getAllRecordIdForPmay();

	void update(PmayEligibilityLog pmayEligibilityLog);

	String getCustCif(String finreference);

	void update(String reference, String applicantId);
}
