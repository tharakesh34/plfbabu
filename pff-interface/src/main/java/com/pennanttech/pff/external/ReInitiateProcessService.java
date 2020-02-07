package com.pennanttech.pff.external;

import com.pennanttech.pff.model.IDBInterfaceLogDetail;

public interface ReInitiateProcessService {
	public void processErrorRecords(IDBInterfaceLogDetail detail) throws Exception;
}