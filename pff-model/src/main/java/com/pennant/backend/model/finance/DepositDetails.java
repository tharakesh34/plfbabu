package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DepositDetails extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -58727889587717168L;

	private long depositId = Long.MIN_VALUE; // Auto Generated Sequence
	private String depositType;
	private String branchCode;
	private String branchDesc;
	private BigDecimal actualAmount = BigDecimal.ZERO;
	private BigDecimal reservedAmount = BigDecimal.ZERO;
	private DepositDetails befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private DepositMovements depositMovements = null;
	private List<DepositMovements> depositMovementsList = new ArrayList<DepositMovements>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public DepositDetails() {
		super();
	}

	public DepositDetails(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("branchDesc");
		excludeFields.add("depositMovements");
		excludeFields.add("depositMovementsList");
		excludeFields.add("auditDetailMap");

		return excludeFields;
	}

	public long getDepositId() {
		return depositId;
	}

	public void setDepositId(long depositId) {
		this.depositId = depositId;
	}

	public String getDepositType() {
		return depositType;
	}

	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}

	public DepositDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(DepositDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getId() {
		return this.depositId;
	}

	public void setId(long id) {
		this.depositId = id;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public List<DepositMovements> getDepositMovementsList() {
		return depositMovementsList;
	}

	public void setDepositMovementsList(List<DepositMovements> depositMovementsList) {
		this.depositMovementsList = depositMovementsList;
	}

	public DepositMovements getDepositMovements() {
		return depositMovements;
	}

	public void setDepositMovements(DepositMovements depositMovements) {
		this.depositMovements = depositMovements;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}
}
