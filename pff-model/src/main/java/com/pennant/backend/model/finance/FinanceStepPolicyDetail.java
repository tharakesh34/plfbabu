package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "stepNo", "tenorSplitPerc", "rateMargin", "emiSplitPerc", "installments", "steppedEMI",
		"stepSpecifier", "autoCal" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceStepPolicyDetail extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -2343217039719002642L;

	private long finID;
	private String finReference;
	@XmlElement(name = "stepNumber")
	private int stepNo;
	@XmlElement
	private BigDecimal tenorSplitPerc = BigDecimal.ZERO;
	@XmlElement
	private int installments;
	@XmlElement
	private BigDecimal rateMargin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal emiSplitPerc = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal steppedEMI = BigDecimal.ZERO;
	private String lovValue;
	private FinanceStepPolicyDetail befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private String stepSpecifier;
	private Date stepStart;
	private Date stepEnd;
	@XmlElement
	private boolean autoCal = false;

	public FinanceStepPolicyDetail() {
		super();
	}

	public FinanceStepPolicyDetail copyEntity() {
		FinanceStepPolicyDetail entity = new FinanceStepPolicyDetail();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setStepNo(this.stepNo);
		entity.setTenorSplitPerc(this.tenorSplitPerc);
		entity.setInstallments(this.installments);
		entity.setRateMargin(this.rateMargin);
		entity.setEmiSplitPerc(this.emiSplitPerc);
		entity.setSteppedEMI(this.steppedEMI);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setStepSpecifier(this.stepSpecifier);
		entity.setStepStart(this.stepStart);
		entity.setStepEnd(this.stepEnd);
		entity.setAutoCal(this.autoCal);
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

	public FinanceStepPolicyDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
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

	public int getStepNo() {
		return stepNo;
	}

	public void setStepNo(int stepNo) {
		this.stepNo = stepNo;
	}

	public int getInstallments() {
		return installments;
	}

	public void setInstallments(int installments) {
		this.installments = installments;
	}

	public BigDecimal getSteppedEMI() {
		return steppedEMI;
	}

	public void setSteppedEMI(BigDecimal steppedEMI) {
		this.steppedEMI = steppedEMI;
	}

	public BigDecimal getTenorSplitPerc() {
		return tenorSplitPerc;
	}

	public void setTenorSplitPerc(BigDecimal tenorSplitPerc) {
		this.tenorSplitPerc = tenorSplitPerc;
	}

	public BigDecimal getRateMargin() {
		return rateMargin;
	}

	public void setRateMargin(BigDecimal rateMargin) {
		this.rateMargin = rateMargin;
	}

	public BigDecimal getEmiSplitPerc() {
		return emiSplitPerc;
	}

	public void setEmiSplitPerc(BigDecimal emiSplitPerc) {
		this.emiSplitPerc = emiSplitPerc;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceStepPolicyDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceStepPolicyDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public String getStepSpecifier() {
		return stepSpecifier;
	}

	public void setStepSpecifier(String stepSpecifier) {
		this.stepSpecifier = stepSpecifier;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getStepStart() {
		return stepStart;
	}

	public void setStepStart(Date stepStart) {
		this.stepStart = stepStart;
	}

	public Date getStepEnd() {
		return stepEnd;
	}

	public void setStepEnd(Date stepEnd) {
		this.stepEnd = stepEnd;
	}

	public boolean isAutoCal() {
		return autoCal;
	}

	public void setAutoCal(boolean autoCal) {
		this.autoCal = autoCal;
	}
}
