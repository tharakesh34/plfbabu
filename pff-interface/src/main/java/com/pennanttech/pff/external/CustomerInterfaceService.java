package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerInterfaceService {
	CustomerDetails getCustomerDetail(Customer customer) throws InterfaceException;
}
