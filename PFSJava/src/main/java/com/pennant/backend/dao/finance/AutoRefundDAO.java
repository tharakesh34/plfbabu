package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.AutoRefundLoan;

public interface AutoRefundDAO {
	void save(List<AutoRefundLoan> list);
}
