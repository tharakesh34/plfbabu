package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pennapps.core.InterfaceException;

public interface Crm {

	public CustomerDetails create(final CustomerDetails customer) throws InterfaceException;

	public CustomerDetails update(final CustomerDetails customer) throws InterfaceException;
}
