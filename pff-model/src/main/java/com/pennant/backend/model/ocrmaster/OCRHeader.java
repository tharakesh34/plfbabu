package com.pennant.backend.model.ocrmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class OCRHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private long headerID = Long.MIN_VALUE;
	private String ocrID;
	private String ocrDescription;
	private int customerPortion;
	private String ocrType;
	private boolean active;
	private List<OCRDetail> ocrDetailList = new ArrayList<>();
	private boolean newRecord = false;
	private String lovValue;
	private OCRHeader befImage;
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public OCRHeader() {
		super();
	}

	public OCRHeader(int id) {
		super();
		this.setId(id);
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return headerID;
	}

	public void setId(long id) {
		this.headerID = id;
	}

	public String getOcrID() {
		return ocrID;
	}

	public void setOcrID(String ocrID) {
		this.ocrID = ocrID;
	}

	public String getOcrDescription() {
		return ocrDescription;
	}

	public void setOcrDescription(String ocrDescription) {
		this.ocrDescription = ocrDescription;
	}

	public int getCustomerPortion() {
		return customerPortion;
	}

	public void setCustomerPortion(int customerPortion) {
		this.customerPortion = customerPortion;
	}

	public String getOcrType() {
		return ocrType;
	}

	public void setOcrType(String ocrType) {
		this.ocrType = ocrType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<OCRDetail> getOcrDetailList() {
		return ocrDetailList;
	}

	public void setOcrDetailList(List<OCRDetail> ocrDetailList) {
		this.ocrDetailList = ocrDetailList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public OCRHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(OCRHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
