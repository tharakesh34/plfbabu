package com.pennanttech.pff.external;

import java.io.IOException;
import java.util.List;

public interface GLEMSCustomerProcess {
	boolean processDownload(List<Long> custIds) throws Exception;

	public String getFilePath() throws IOException;
}
