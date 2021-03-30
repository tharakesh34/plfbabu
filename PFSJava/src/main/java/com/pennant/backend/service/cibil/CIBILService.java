package com.pennant.backend.service.cibil;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceSummary;
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

	//changes to differentiate the CIBIL Member ID during CIBIL generation & enquiry
	CibilMemberDetail getMemberDetailsByType(String segmentType, String type);

	FinanceSummary getFinanceProfitDetails(String finRef);

	List<FinReceiptHeader> getReceiptHeadersByRef(String finReference, String type);

	public Date getMaxReceiptDateByRef(String finReference);

}
