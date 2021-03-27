package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.RestructureDetail;

public interface RestructureDAO {

	long save(RestructureDetail restructureDetail, String tableType);

	RestructureDetail getRestructureDetailById(long restructureId, String type);

	RestructureDetail getRestructureDetailByFinReference(String finReference, String type);

	void update(RestructureDetail restructureDetail, String tableType);

	void delete(long restructureId, String tableType);

}
