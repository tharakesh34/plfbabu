package com.pennant.backend.dao.customermasters;

import com.pennant.backend.model.customermasters.CoreCustomer;

public interface CoreCustomerDAO {

	 void save(CoreCustomer coreCustomer);
	 void update(CoreCustomer coreCustomer);
}
