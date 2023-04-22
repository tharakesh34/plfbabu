package com.pennant.pff.lien.service;

import com.pennant.backend.model.finance.FinanceDetail;

public interface LienService {

	void save(FinanceDetail fd);

	void update(FinanceDetail fd);
}
