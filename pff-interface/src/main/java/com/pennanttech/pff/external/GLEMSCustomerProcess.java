package com.pennanttech.pff.external;

import java.util.List;

public interface GLEMSCustomerProcess {
	boolean processDownload(List<Long> custIds) throws Exception;
}
