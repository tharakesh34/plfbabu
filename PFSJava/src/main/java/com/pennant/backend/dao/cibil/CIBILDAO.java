package com.pennant.backend.dao.cibil;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public interface CIBILDAO {

	CustomerDetails getCustomerDetails(long customerId);

	Customer getCustomer(long customerId, String bureauType);

	List<CustomerDocument> getCustomerDocuments(long customerId, String segmentType);

	List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId, String segmentType);

	List<CustomerAddres> getCustomerAddres(long customerId, String segmentType);

	FinanceEnquiry getFinanceSummary(long customerId, long finID, String segmentType);

	List<FinanceEnquiry> getFinanceSummary(long customerId, String segmentType);

	void deleteDetails();

	void logFileInfoException(long id, long finID, String finReference, String reason);

	DataEngineStatus getLatestExecution();

	EventProperties getEventProperties(String configName, String eventType);

	List<CustomerEMail> getCustomerEmails(long customerId);

	void logFileInfo(CibilFileInfo fileInfo);

	long extractCustomers(String segmentType, String entity) throws Exception;

	void updateFileStatus(CibilFileInfo fileInfo);

	CibilMemberDetail getMemberDetails(String bureauType);

	List<FinODDetails> getFinODDetails(long finID, String finCCY);

	List<CollateralSetup> getCollateralDetails(long finID, String segmentType);

	List<ChequeDetail> getChequeBounceStatus(String finReference);

	List<Long> getGuarantorsDetails(long finID, boolean isBankCustomer);

	long getotalRecords(String segmentType);

	Customer getExternalCustomer(Long custId);

	List<CustomerAddres> getExternalCustomerAddres(Long custId);

	List<CustomerPhoneNumber> getExternalCustomerPhoneNumbers(Long custId);

	List<CustomerDocument> getExternalCustomerDocuments(Long custId);

	EventProperties getEventProperties(String configName);

	// changes to differentiate the CIBIL Member ID during CIBIL generation & enquiry
	CibilMemberDetail getMemberDetailsByType(String bureauType, String type);

	List<Long> getJointAccountDetails(long finID);

	BigDecimal getLastRepaidAmount(String finReference);

	BigDecimal getGuarantorPercentage(long finID);

	List<String> getEntityCodes();

	String getCoAppRelation(String custCIF, long finID);

	FinanceSummary getFinanceProfitDetails(String finRef);
}
