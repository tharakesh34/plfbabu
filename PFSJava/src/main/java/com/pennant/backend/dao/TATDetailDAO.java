package com.pennant.backend.dao;

import com.pennant.backend.model.finance.TATDetail;

public interface TATDetailDAO {
	TATDetail getTATDetail(String reference, String rolecode);

	void save(TATDetail tatDetail);

	void update(TATDetail tatDetail);
}
