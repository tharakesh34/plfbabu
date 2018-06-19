package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerDedupCheckService {

	public List<CustomerDedup> invokeDedup(CustomerDedup customerDedup) throws InterfaceException;

}
