package com.pennanttech.ws.model.finance;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "repayAmount", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class EmiResponse {

	private BigDecimal repayAmount;

	private WSReturnStatus returnStatus;

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
}
