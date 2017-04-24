package com.pennant.backend.dao.policecase;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennanttech.pff.core.TableType;

public interface PoliceCaseDAO extends BasicCrudDao<PoliceCaseDetail> {

	void saveList(List<PoliceCase> policeCase, String type);
	List<PoliceCase> fetchPoliceCase(String finReference, String queryCode);
	List<PoliceCaseDetail> fetchCorePolice(PoliceCaseDetail policecase, String sqlQuery);
	void updatePoliceCaseList(List<PoliceCase> policeCase);
	List<PoliceCase> fetchFinPoliceCase(String finReference);
	void deleteList(String finReference);
	PoliceCaseDetail getPoliceCaseDetailById(String id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param custCIF
	 *            custCIF of the policeCase.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String custCIF, TableType tableType);
	void moveData(String finReference, String type);

}
