package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.DealerResponse;

public interface DealerResponseDAO {

	long save(DealerResponse dealerResponse, String type);

	List<DealerResponse> getDealerResponse(long finID, String type);

	void updateSatus(DealerResponse dealerResponse, String type);

	List<DealerResponse> getByProcessed(long finID, boolean processed, String type);

	void updateProcessed(long finID, boolean processed, String type);

	int getCountByProcessed(long finID, boolean processed, String type);

}
