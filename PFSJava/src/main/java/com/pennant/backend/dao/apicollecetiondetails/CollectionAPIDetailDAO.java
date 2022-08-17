package com.pennant.backend.dao.apicollecetiondetails;

import com.pennanttech.pff.model.external.collection.CollectionAPIDetail;

public interface CollectionAPIDetailDAO {

	void save(CollectionAPIDetail collectionAPI);

	void update(long linkedTranId, String type, long receiptId);

	boolean isEntryExists(long receiptId, String serviceName);
}
