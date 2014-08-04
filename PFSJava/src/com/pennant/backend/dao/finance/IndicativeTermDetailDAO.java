package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.IndicativeTermDetail;

public interface IndicativeTermDetailDAO {

	public void save(IndicativeTermDetail detail,String type,boolean isWIF);
	public void update(IndicativeTermDetail detail,String type,boolean isWIF);
	public void delete(IndicativeTermDetail detail,String type,boolean isWIF);
	public IndicativeTermDetail getIndicateTermByRef(String finReference,String type,boolean isWIF);
	public void initialize(IndicativeTermDetail detail);
	public void refresh(IndicativeTermDetail detail);
	
}
