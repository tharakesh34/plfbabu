package com.pennanttech.ws.model.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerIncome;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "customerIncome", "incomeExpense", "category", "custIncomeType", "returnStatus" })
public class CustomerIncomeDetail {

	@XmlElement
	private String cif;

	@XmlElement(name = "customerIncome")
	private CustomerIncome customerIncome;

	@XmlElement
	private String incomeExpense;

	@XmlElement
	private String category;

	@XmlElement
	private String custIncomeType;

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public CustomerIncome getCustomerIncome() {
		return customerIncome;
	}

	public void setCustomerIncome(CustomerIncome customerIncome) {
		this.customerIncome = customerIncome;
	}

	public String getIncomeExpense() {
		return incomeExpense;
	}

	public void setIncomeExpense(String incomeExpense) {
		this.incomeExpense = incomeExpense;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCustIncomeType() {
		return custIncomeType;
	}

	public void setCustIncomeType(String custIncomeType) {
		this.custIncomeType = custIncomeType;
	}
}
