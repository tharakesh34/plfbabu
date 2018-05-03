package com.pennanttech.pff.logging.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;

public interface InterfaceLoggingDAO {

	void save(InterfaceLogDetail interfaceLogDetail);
	
}
