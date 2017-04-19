package com.pennant.eod;

import java.util.Date;

import org.apache.log4j.Logger;

public class EodThread implements Runnable {

	private static Logger	logger	= Logger.getLogger(EodThread.class);

	private EodService		eodService;
	private String			threadId;
	private Date			eodDate;

	@Override
	public void run() {
		logger.debug("Entering");
		getEodService().startProcess(eodDate, threadId);
		logger.debug("Leaving");
	}

	public EodService getEodService() {
		return eodService;
	}

	public void setEodService(EodService eodService) {
		this.eodService = eodService;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public Date getEodDate() {
		return eodDate!=null?(Date)eodDate.clone():eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate!=null?(Date)eodDate.clone():eodDate;
	}

}
