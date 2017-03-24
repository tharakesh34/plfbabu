package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.IndicativeTermDetail;

public interface IndicativeTermDetailDAO {

	void save(IndicativeTermDetail detail,String type,boolean isWIF);
	void update(IndicativeTermDetail detail,String type,boolean isWIF);
	void delete(IndicativeTermDetail detail,String type,boolean isWIF);
	IndicativeTermDetail getIndicateTermByRef(String finReference,String type,boolean isWIF);
}
