package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

/**
 * @author manikanta.p
 *
 */
@XmlType(propOrder = { "loginId", "stageCodes", "customerName", "mobileNumber", "finType", "finReference",
		"finGenerationDate", "applicationNo" })
@XmlRootElement(name = "financeData")
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceData implements Serializable {
	private static final long serialVersionUID = -4823984437205894855L;

	@XmlElement
	private String loginId;
	@XmlElement
	private String stageCodes;

	@XmlElement
	private String customerName;
	@XmlElement
	private String mobileNumber;
	@XmlElement
	private String finType;
	@XmlElement
	private String finReference;
	@XmlElement
	private String finGenerationDate;
	@XmlElement
	private String applicationNo;

	@XmlElement
	private WSReturnStatus returnStatus;

	@XmlElement
	private List<FinanceData> financeDataList = new ArrayList<>();

	public FinanceData() {
		super();
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getStageCodes() {
		return stageCodes;
	}

	public void setStageCodes(String stageCodes) {
		this.stageCodes = stageCodes;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinGenerationDate() {
		return finGenerationDate;
	}

	public void setFinGenerationDate(String finGenerationDate) {
		this.finGenerationDate = finGenerationDate;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public List<FinanceData> getFinanceDataList() {
		return financeDataList;
	}

	public void setFinanceDataList(List<FinanceData> financeDataList) {
		this.financeDataList = financeDataList;
	}

}
