package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinStatusDetail;

public interface DPDEnquiryService {
	List<FinStatusDetail> getFinStatusDetailByRefId(long finID);

}
