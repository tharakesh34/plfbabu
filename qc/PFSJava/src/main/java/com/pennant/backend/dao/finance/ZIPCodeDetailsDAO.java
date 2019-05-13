package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.ZIPCodeDetails;

public interface ZIPCodeDetailsDAO {

	public ZIPCodeDetails getPinCodeDetail(String pinCode);

}
