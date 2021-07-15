package com.pennanttech.ws.model.statement;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ForeClosureReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "customer", "finance", "finReference", "docImage","foreclosureReport", "returnStatus" })
public class FinStatementResponse {
	@XmlElement
	private CustomerDetails customer;
	@XmlElement
	private List<FinanceDetail> finance = null;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String finReference;
	@XmlElement(name = "docContent")
	private byte[] docImage;
	@XmlElement
	private StatementOfAccount statementSOA;
	@XmlElement
	private ForeClosureReport foreclosureReport;

	public CustomerDetails getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDetails customer) {
		this.customer = customer;
	}

	public List<FinanceDetail> getFinance() {
		return finance;
	}

	public void setFinance(List<FinanceDetail> finance) {
		this.finance = finance;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public StatementOfAccount getStatementSOA() {
		return statementSOA;
	}

	public void setStatementSOA(StatementOfAccount statementSOA) {
		this.statementSOA = statementSOA;
	}

	public ForeClosureReport getForeclosureReport() {
		return foreclosureReport;
	}

	public void setForeclosureReport(ForeClosureReport foreclosureReport) {
		this.foreclosureReport = foreclosureReport;
	}

}
