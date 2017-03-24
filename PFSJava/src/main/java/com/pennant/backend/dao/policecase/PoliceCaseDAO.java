package com.pennant.backend.dao.policecase;

import java.util.List;

import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.policecase.PoliceCase;

public interface PoliceCaseDAO {

	void saveList(List<PoliceCase> policeCase, String type);
	List<PoliceCase> fetchPoliceCase(String finReference, String queryCode);
	List<PoliceCaseDetail> fetchCorePolice(PoliceCaseDetail policecase, String sqlQuery);
	void updatePoliceCaseList(List<PoliceCase> policeCase);
	List<PoliceCase> fetchFinPoliceCase(String finReference);
	void deleteList(String finReference);
	PoliceCaseDetail getPoliceCaseDetailById(String id, String type);
	void update(PoliceCaseDetail policeCaseDetail, String type);
	void delete(PoliceCaseDetail policeCaseDetail, String type);
	String save(PoliceCaseDetail policeCaseDetail, String type);
	void moveData(String finReference, String type);

}
