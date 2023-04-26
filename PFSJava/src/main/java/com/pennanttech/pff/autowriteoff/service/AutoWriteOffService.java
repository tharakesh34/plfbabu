package com.pennanttech.pff.autowriteoff.service;

import java.util.Date;

import com.pennant.backend.model.finance.FeeType;
import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;

public interface AutoWriteOffService {

	long getQueueCount();

	int updateThreadID(long from, long to, int i);

	void updateProgress(long finID, int progressInProcess);

	long prepareQueueForEOM();

	String prepareWriteOff(long finID, Date appDate);

	AutoWriteOffLoan processReceipts(long finID, Date appDate, FeeType feeType, String schdMthd);

	void insertlog(AutoWriteOffLoan awl);

}
