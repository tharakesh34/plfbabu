package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.ZIPCodeDetails;

public interface ZIPCodeDetailsService {

	ZIPCodeDetails getZIPCodeDetails(String pinCode);
}
