package com.pennant.backend.service.cibil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.cibil.CIBILDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public class CIBILServiceImpl implements CIBILService {
	private static Logger logger = LogManager.getLogger(CIBILServiceImpl.class);

	@Autowired
	private CIBILDAO cibildao;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	@Override
	public CustomerDetails getCustomerDetails(long customerId, long finID, String segmentType) {
		logger.debug(Literal.ENTERING);
		logger.trace(Literal.ENTERING);

		CustomerDetails customer = new CustomerDetails();

		try {
			customer.setCustomer(cibildao.getCustomer(customerId, segmentType));
			customer.setAddressList(cibildao.getCustomerAddres(customerId, segmentType));
			customer.setCustomerDocumentsList(cibildao.getCustomerDocuments(customerId, segmentType));
			customer.setCustomerPhoneNumList(cibildao.getCustomerPhoneNumbers(customerId, segmentType));
			customer.setCustomerEMailList(cibildao.getCustomerEmails(customerId));

			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
				customer.setCustomerFinance(cibildao.getFinanceSummary(customerId, finID, segmentType));
			} else {
				customer.setCustomerFinances(cibildao.getFinanceSummary(customerId, segmentType));
			}

			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
				return customer;
			}

			String finReference;
			for (FinanceEnquiry finance : customer.getCustomerFinances()) {
				finReference = finance.getFinReference();
				finance.setFinOdDetails(cibildao.getFinODDetails(finID, finance.getFinCcy()));
				finance.setCollateralSetupDetails(cibildao.getCollateralDetails(finID, segmentType));
				finance.setChequeDetail(cibildao.getChequeBounceStatus(finID));

				List<CustomerDetails> guarenterList = new ArrayList<>();
				// Bank Customers
				List<Long> guarenters = cibildao.getGuarantorsDetails(finID, true);
				for (Long custId : guarenters) {
					CustomerDetails guarenter = new CustomerDetails();
					guarenter.setCustomer(cibildao.getCustomer(custId, segmentType));
					guarenter.setAddressList(cibildao.getCustomerAddres(custId, segmentType));
					guarenter.setCustomerPhoneNumList(cibildao.getCustomerPhoneNumbers(custId, segmentType));
					guarenter.setCustomerDocumentsList(cibildao.getCustomerDocuments(custId, segmentType));
					guarenterList.add(guarenter);
				}

				// Non Banking Customers needs to prepared
				// guarenters = cibildao.getGuarantorsDetails(finReference, false);
				for (Long custId : guarenters) {
					// CustomerDetails guarenter = new CustomerDetails();
					// guarenter.setCustomer(cibildao.getExternalCustomer(custId));
					// guarenter.setAddressList(cibildao.getExternalCustomerAddres(custId));
					// guarenter.setCustomerPhoneNumList(cibildao.getExternalCustomerPhoneNumbers(custId));
					// guarenter.setCustomerDocumentsList(cibildao.getExternalCustomerDocuments(custId));
					// guarenterList.add(guarenter);
				}

				finance.setFinGuarenters(guarenterList);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logger.trace(Literal.EXCEPTION, e);
			logger.debug(Literal.EXCEPTION, e);
			e.printStackTrace();
			customer = null;
		}
		logger.debug(Literal.LEAVING);
		logger.trace(Literal.LEAVING);
		return customer;
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeadersByRef(long finID, String type) {
		return null;

	}

	@Override
	public FinanceSummary getFinanceProfitDetails(long finID) {
		return financeMainDAO.getFinanceProfitDetails(finID);

	}

	@Override
	public Date getMaxReceiptDateByRef(long finID) {
		return finReceiptHeaderDAO.getMaxReceiptDateByRef(finID);
	}

	@Override
	public void deleteDetails() {
		cibildao.deleteDetails();
	}

	@Override
	public void logFileInfoException(long id, long finID, String finReference, String reason) {
		cibildao.logFileInfoException(id, finID, finReference, reason);

	}

	@Override
	public DataEngineStatus getLatestExecution() {
		return cibildao.getLatestExecution();
	}

	@Override
	public EventProperties getEventProperties(String configName, String eventType) {
		return cibildao.getEventProperties(configName, eventType);

	}

	@Override
	public void logFileInfo(CibilFileInfo fileInfo) {
		cibildao.logFileInfo(fileInfo);
	}

	@Override
	public long extractCustomers(String segmentType) throws Exception {
		return new Long(cibildao.extractCustomers(segmentType));
	}

	@Override
	public void updateFileStatus(CibilFileInfo fileInfo) {
		cibildao.updateFileStatus(fileInfo);

	}

	@Override
	public CibilMemberDetail getMemberDetails(String segmentType) {
		return cibildao.getMemberDetails(segmentType);
	}

	// changes to differentiate the CIBIL Member ID during CIBIL generation & enquiry
	@Override
	public CibilMemberDetail getMemberDetailsByType(String segmentType, String type) {
		return cibildao.getMemberDetailsByType(segmentType, type);
	}

	@Override
	public long getotalRecords(String segmentType) {
		return cibildao.getotalRecords(segmentType);
	}

	@Override
	public EventProperties getEventProperties(String configName) {
		return cibildao.getEventProperties(configName);
	}

	public void setCibildao(CIBILDAO cibildao) {
		this.cibildao = cibildao;
	}

}
