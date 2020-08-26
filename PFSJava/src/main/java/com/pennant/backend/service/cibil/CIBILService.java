package com.pennant.backend.service.cibil;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public interface CIBILService {
	CustomerDetails getCustomerDetails(long customerId, String finReference, String bureauType);

	void deleteDetails();

	void logFileInfoException(long id, String finReference, String reason);

	DataEngineStatus getLatestExecution();

	EventProperties getEventProperties(String configName, String eventType);

	void logFileInfo(CibilFileInfo fileInfo);

	long extractCustomers(String bureauType) throws Exception;

	void updateFileStatus(CibilFileInfo fileInfo);

	public CibilMemberDetail getMemberDetails(String bureauType);

	long getotalRecords(String pffCustctgCorp);
	
	EventProperties getEventProperties(String configName);


}
