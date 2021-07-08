package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinMaintainInstruction table</b>.<br>
 *
 */

public class FinMaintainInstruction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long finMaintainId = Long.MIN_VALUE;
	private String finReference;
	private String event;
	private boolean newRecord;
	private String lovValue;
	private FinMaintainInstruction befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private BigDecimal tdsPercentage;
	private Date tdsStartDate;
	private Date tdsEndDate;
	private BigDecimal tdsLimit = BigDecimal.ZERO;

	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private List<FinCovenantType> finCovenantTypeList = new ArrayList<>(1);
	private List<Covenant> covenants = new ArrayList<>();
	private List<FinOption> finOptions = new ArrayList<>();
	private List<CollateralAssignment> collateralAssignments = new ArrayList<>();

	// Is TDS Applicable
	private boolean tDSApplicable = false;// Clix added new TDS Applicable Flag

	public boolean istDSApplicable() {
		return tDSApplicable;
	}

	public void settDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public FinMaintainInstruction() {
		super();
	}

	public FinMaintainInstruction(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("covenants");
		excludeFields.add("finOptions");
		excludeFields.add("collateralAssignments");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return finMaintainId;
	}

	public void setId(long id) {
		this.finMaintainId = id;
	}

	public long getFinMaintainId() {
		return getId();
	}

	public void setFinMaintainId(long finMaintainId) {
		this.finMaintainId = finMaintainId;
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

	public FinMaintainInstruction getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinMaintainInstruction beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public List<FinCovenantType> getFinCovenantTypeList() {
		return finCovenantTypeList;
	}

	public void setFinCovenantTypeList(List<FinCovenantType> finCovenantTypeList) {
		this.finCovenantTypeList = finCovenantTypeList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public List<Covenant> getCovenants() {
		return covenants;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

	public List<FinOption> getFinOptions() {
		return finOptions;
	}

	public void setFinOptions(List<FinOption> finOptions) {
		this.finOptions = finOptions;
	}

	public BigDecimal getTdsPercentage() {
		return tdsPercentage;
	}

	public void setTdsPercentage(BigDecimal tdsPercentage) {
		this.tdsPercentage = tdsPercentage;
	}

	public Date getTdsStartDate() {
		return tdsStartDate;
	}

	public void setTdsStartDate(Date tdsStartDate) {
		this.tdsStartDate = tdsStartDate;
	}

	public Date getTdsEndDate() {
		return tdsEndDate;
	}

	public void setTdsEndDate(Date tdsEndDate) {
		this.tdsEndDate = tdsEndDate;
	}

	public BigDecimal getTdsLimit() {
		return tdsLimit;
	}

	public void setTdsLimit(BigDecimal tdsLimit) {
		this.tdsLimit = tdsLimit;
	}

	public List<CollateralAssignment> getCollateralAssignments() {
		return collateralAssignments;
	}

	public void setCollateralAssignments(List<CollateralAssignment> collateralAssignments) {
		this.collateralAssignments = collateralAssignments;
	}

}
