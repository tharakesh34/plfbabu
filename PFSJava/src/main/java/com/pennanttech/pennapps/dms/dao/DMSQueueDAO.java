package com.pennanttech.pennapps.dms.dao;

import java.util.List;

import com.pennanttech.pennapps.dms.model.DMSQueue;

public interface DMSQueueDAO {

	public void log(DMSQueue dMSQueue);

	public List<Long> processDMSQueue();

	public DMSQueue getDMSQueue(long queueID);

	public void updateDMSQueue(DMSQueue dMSQueue);

	public byte[] getDocumentByURI(String docURI);

	public void insertDMSQueueLog(DMSQueue dMSQueue);

	int delete(long queueId);

	public void update(DMSQueue dMSQueue);

}
