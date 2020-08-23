package com.pennanttech.pennapps.dms.dao;

import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.dms.service.DMSService;

public interface DMSQueueDAO {

	public void log(DMSQueue dMSQueue);

	public void processDMSQueue(DMSService dmsService);

	public void updateDMSQueue(DMSQueue dMSQueue);

	public byte[] getDocumentByURI(String docURI);
	
	public DMSQueue isExistDocuri(String docUri, String reference);
}
