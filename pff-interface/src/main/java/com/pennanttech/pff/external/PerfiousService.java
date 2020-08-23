package com.pennanttech.pff.external;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.perfios.PerfiosHeader;

public interface PerfiousService {

	Map<String, Object> statementUpaload(List<ExternalDocument> externalDocuments, Map<String, Object> map);

	boolean isDocumentExists(long docId, String type);

	PerfiosHeader getPerfiosReport(String transactionId);

	void updatePerfiosHeader(PerfiosHeader perfiosHeader);

	Map<String, Object> fetchCustomerBankInfoId(String transactionId);

	PerfiosHeader getPerfiosReponseDocDetails(long docId, String type);

	void savePerfiosDocuments(CustomerBankInfo custBankInfo);

	FinanceMain getFinanceDetailsByRef(String financeReference);

	String getCustomerDetailsBycustId(long custID);
}
