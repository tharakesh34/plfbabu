package com.pennanttech.ws.model.customer;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;

public class DocumentList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CustomerDocument> customerDocumentsList;
	private List<DocumentDetails> financeDocumentsList;
	private WSReturnStatus returnStatus;

	public DocumentList() {
		super();
	}

	public List<CustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public void setCustomerDocumentsList(List<CustomerDocument> customerDocumentsList) {
		this.customerDocumentsList = customerDocumentsList;
	}

	public List<DocumentDetails> getFinanceDocumentsList() {
		return financeDocumentsList;
	}

	public void setFinanceDocumentsList(List<DocumentDetails> financeDocumentsList) {
		this.financeDocumentsList = financeDocumentsList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
