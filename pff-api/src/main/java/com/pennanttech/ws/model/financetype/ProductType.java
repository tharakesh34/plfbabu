package com.pennanttech.ws.model.financetype;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "productCode", "productDesc", "financeTypeList", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductType {

	private String productCode;
	private String productDesc;
	private List<FinanceTypeResponse> financeTypeList;
	private WSReturnStatus returnStatus;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public List<FinanceTypeResponse> getFinanceTypeList() {
		return financeTypeList;
	}

	public void setFinanceTypeList(List<FinanceTypeResponse> financeTypeList) {
		this.financeTypeList = financeTypeList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
