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

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections4.CollectionUtils;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
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
	private long finID;
	private String finReference;
	private String event;
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
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private ExtendedFieldRender extendedFieldRender = null;
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<>();

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

	public FinMaintainInstruction copyEntity() {
		FinMaintainInstruction entity = new FinMaintainInstruction();
		entity.setFinMaintainId(this.finMaintainId);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setEvent(this.event);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setTdsPercentage(this.tdsPercentage);
		entity.setTdsStartDate(this.tdsStartDate);
		entity.setTdsEndDate(this.tdsEndDate);
		entity.setTdsLimit(this.tdsLimit);

		this.finCovenantTypeList.stream()
				.forEach(e -> entity.getFinCovenantTypeList().add(e == null ? null : e.copyEntity()));
		this.covenants.stream().forEach(e -> entity.getCovenants().add(e == null ? null : e.copyEntity()));
		this.finOptions.stream().forEach(e -> entity.getFinOptions().add(e == null ? null : e.copyEntity()));
		this.collateralAssignments.stream()
				.forEach(e -> entity.getCollateralAssignments().add(e == null ? null : e.copyEntity()));
		entity.setExtendedFieldHeader(this.extendedFieldHeader == null ? null : this.extendedFieldHeader.copyEntity());
		// FIXME
		// entity.setExtendedFieldRender(this.extendedFieldRender == null ? null :
		// this.extendedFieldRender.copyEntity());
		this.finServiceInstructions.stream()
				.forEach(e -> entity.getFinServiceInstructions().add(e == null ? null : e.copyEntity()));
		entity.settDSApplicable(this.tDSApplicable);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("covenants");
		excludeFields.add("finOptions");
		excludeFields.add("collateralAssignments");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("finServiceInstructions");
		return excludeFields;
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

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public FinServiceInstruction getFinServiceInstruction() {
		FinServiceInstruction finServiceInstruction = null;
		if (CollectionUtils.isNotEmpty(finServiceInstructions)) {
			finServiceInstruction = finServiceInstructions.get(0);
		}
		return finServiceInstruction;
	}

	public void setFinServiceInstruction(FinServiceInstruction finServiceInstruction) {
		if (finServiceInstruction != null) {
			if (finServiceInstructions == null) {
				finServiceInstructions = new ArrayList<FinServiceInstruction>();
			}
			this.finServiceInstructions.add(finServiceInstruction);
		}
	}

}
