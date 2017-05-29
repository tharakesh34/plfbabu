package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.DealerResponse;

public interface DealerResponseDAO {

	long save(DealerResponse dealerResponse, String type);
	List<DealerResponse> getDealerResponse(String finReference,String type);
	void updateSatus(DealerResponse dealerResponse, String type);
	List<DealerResponse> getByProcessed(String finReference, boolean processed, String type);
	void updateProcessed(String finReference, boolean processed, String type);
	int getCountByProcessed(String finReference, boolean processed, String type);

}
