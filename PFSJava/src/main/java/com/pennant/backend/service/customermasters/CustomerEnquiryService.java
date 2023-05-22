package com.pennant.backend.service.customermasters;

import com.pennant.backend.model.customermasters.CustomerDetails;

public interface CustomerEnquiryService {

	public CustomerDetails getCustomerDetails(long custID);

}
