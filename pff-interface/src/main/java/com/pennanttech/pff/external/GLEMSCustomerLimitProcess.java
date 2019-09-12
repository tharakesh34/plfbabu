package com.pennanttech.pff.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface GLEMSCustomerLimitProcess {
	boolean processDownload(List<Long> custids);

	public String getFilePath() throws FileNotFoundException, IOException;
}
