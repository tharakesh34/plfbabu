package com.pennant.pff.lien.service;

import com.pennant.backend.model.finance.FinanceDetail;

public interface LienService {

	void save(FinanceDetail fd, boolean isMandate);

	void update(FinanceDetail fd);
}
