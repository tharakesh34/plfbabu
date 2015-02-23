package com.pennant.backend.dao.policecase;

import java.util.List;

import com.pennant.backend.model.policecase.PoliceCase;

public interface PoliceCaseDAO {
	
	void saveList(List<PoliceCase> policeCase);
	List<PoliceCase> fetchPoliceCase(String finReference, String queryCode);

}
