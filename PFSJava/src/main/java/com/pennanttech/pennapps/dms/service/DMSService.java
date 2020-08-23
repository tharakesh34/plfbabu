package com.pennanttech.pennapps.dms.service;

import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.dms.model.DMSQueue;

public interface DMSService {

	public long save(DMSQueue dmsQueue);

	public DocumentManager getDocumentManager(long id);

	public byte[] getById(long id);

	public Long getCustomerIdByFin(String FinReference);

	public Long getCustomerIdByCollateral(String FinReference);

	public Long getCustomerIdByCIF(String custCIF);

	void processDocuments();

	public void storeDocInFileSystem(DMSQueue dmsQueue);

	DMSQueue getOfferIdByFin(DMSQueue dmsQueue);

	public byte[] getImageByUri(String docUri);
	
	public DMSQueue isExistDocuri(String docUri,String reference);
	
	public void updateDMSQueue(DMSQueue dmsQueue);
}
