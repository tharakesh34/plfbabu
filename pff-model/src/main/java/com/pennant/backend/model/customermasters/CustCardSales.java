package com.pennant.backend.model.customermasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CustCardSales extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3217987429162088120L;
	@XmlElement
	private long id = Long.MIN_VALUE;
	private long custID = Long.MIN_VALUE;
	@XmlElement(name = "merchantId")
	private String merchantId;
	private boolean newRecord = false;
	private CustCardSales befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	@XmlElementWrapper(name = "custCardMonthSales")
	@XmlElement(name = "custCardMonthSales")
	private List<CustCardSalesDetails> custCardMonthSales = new ArrayList<>();
	private String sourceId;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("lovValue");
		excludeFields.add("lovDescCustCIF");
		excludeFields.add("lovDescCustShrtName");
		excludeFields.add("custCardMonthSales");
		excludeFields.add("sourceId");
		return excludeFields;
	}
	
	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CustCardSales getBefImage() {
		return befImage;
	}

	public void setBefImage(CustCardSales befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public String getLovValue() {
		return lovValue;
	}

	public List<CustCardSalesDetails> getCustCardMonthSales() {
		return custCardMonthSales;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public void setCustCardMonthSales(List<CustCardSalesDetails> custCardMonthSales) {
		this.custCardMonthSales = custCardMonthSales;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
