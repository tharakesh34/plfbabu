package com.pennant.backend.model.customermasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "custId", "gstNumber", "frequencytype", "customerGSTDetailslist" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerGST extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2527200144895549992L;
	@XmlElementWrapper(name = "id")
	@XmlElement
	private long id = Long.MIN_VALUE;
	@XmlElement
	private long custId;

	private String lovDescCustCIF;
	private String custCif;
	private String lovDescCustShrtName;
	@XmlElement
	private String gstNumber;
	@XmlElement
	private String frequencytype;
	@XmlElementWrapper(name = "customerGSTDetailslist")
	@XmlElement
	private List<CustomerGSTDetails> customerGSTDetailslist = new ArrayList<CustomerGSTDetails>();
	private String lovValue;
	private CustomerGST befImage;
	private LoggedInUser userDetails;
	private String sourceId;

	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getFrequencytype() {
		return frequencytype;
	}

	public void setFrequencytype(String frequencytype) {
		this.frequencytype = frequencytype;
	}

	public List<CustomerGSTDetails> getCustomerGSTDetailslist() {
		return customerGSTDetailslist;
	}

	public void setCustomerGSTDetailslist(List<CustomerGSTDetails> customerGSTDetailslist) {
		this.customerGSTDetailslist = customerGSTDetailslist;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustomerGST getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerGST befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("customerGSTDetailslist");
		excludeFields.add("auditDetailMap");
		excludeFields.add("custCif");
		excludeFields.add("lovDescCustShrtName");
		excludeFields.add("lovDescCustCIF");
		excludeFields.add("sourceId");
		return excludeFields;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

}
