package com.pennant.backend.model.customermasters;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "customer", "customerDedupList", "addressList", "customerPhoneNumList", "customerEMailList",
		"returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class ProspectCustomerDetails extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 624772459953148441L;

	@XmlElement
	private String reference;

	@XmlElement
	private String custCtgCode;

	@XmlElementWrapper(name = "dedups")
	@XmlElement(name = "dedup")
	private List<CustomerDedup> customerDedupList;

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public List<CustomerDedup> getCustomerDedupList() {
		return customerDedupList;
	}

	public void setCustomerDedupList(List<CustomerDedup> customerDedupList) {
		this.customerDedupList = customerDedupList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {

	}

}
