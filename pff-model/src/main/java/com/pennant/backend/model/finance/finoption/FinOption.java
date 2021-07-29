package com.pennant.backend.model.finance.finoption;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinOption extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String finReference;
	private String optionType;
	private String frequency;
	private Date currentOptionDate;
	private boolean optionExercise;
	private int noticePeriodDays;
	private int alertDays;
	private Date nextOptionDate;
	private String alertType;
	private Date alertsentOn;
	private BigDecimal totalPriBal;
	private BigDecimal penaltyPaid;
	private BigDecimal totalAmt;
	private String alertTypeName;
	private String alertToRoles;
	private String alertToRolesName;
	private Long userTemplate;
	private String userTemplateName;
	private Long customerTemplate;
	private String customerTemplateName;
	private String userTemplateCode;
	private String customerTemplateCode;
	private String lovValue;
	private FinOption befImage;
	private LoggedInUser userDetails;
	private String remarks;

	private BigDecimal pftAmz = BigDecimal.ZERO;
	private BigDecimal tdSchdPftBal = BigDecimal.ZERO;// Interest receivable
	private BigDecimal pftAccrued = BigDecimal.ZERO;// Interest receivable
	private BigDecimal totPenal = BigDecimal.ZERO;// Total Penality
	private BigDecimal penaltyDue = BigDecimal.ZERO;
	private BigDecimal interestInclAccrued = BigDecimal.ZERO;
	private BigDecimal penaltyWaived = BigDecimal.ZERO;
	private BigDecimal otherChargers = BigDecimal.ZERO;

	public FinOption() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("alertTypeName");
		excludeFields.add("alertToRolesName");
		excludeFields.add("userTemplateName");
		excludeFields.add("customerTemplateName");
		excludeFields.add("userTemplateCode");
		excludeFields.add("customerTemplateCode");
		excludeFields.add("totalPriBal");
		excludeFields.add("penaltyPaid");
		excludeFields.add("totalAmt");
		excludeFields.add("alertsentOn");
		excludeFields.add("pftAmz");
		excludeFields.add("tdSchdPftBal");
		excludeFields.add("pftAccrued");
		excludeFields.add("totPenal");
		excludeFields.add("penaltyDue");
		excludeFields.add("penaltyWaived");
		excludeFields.add("interestInclAccrued");
		excludeFields.add("otherChargers");
		return excludeFields;

	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getCurrentOptionDate() {
		return currentOptionDate;
	}

	public void setCurrentOptionDate(Date currentOptionDate) {
		this.currentOptionDate = currentOptionDate;
	}

	public boolean isOptionExercise() {
		return optionExercise;
	}

	public void setOptionExercise(boolean optionExercise) {
		this.optionExercise = optionExercise;
	}

	public Date getNextOptionDate() {
		return nextOptionDate;
	}

	public void setNextOptionDate(Date nextOptionDate) {
		this.nextOptionDate = nextOptionDate;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinOption getBefImage() {
		return befImage;
	}

	public void setBefImage(FinOption befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getAlertsentOn() {
		return alertsentOn;
	}

	public void setAlertsentOn(Date alertsentOn) {
		this.alertsentOn = alertsentOn;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(BigDecimal penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOptionType() {
		return optionType;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getNoticePeriodDays() {
		return noticePeriodDays;
	}

	public void setNoticePeriodDays(int noticePeriodDays) {
		this.noticePeriodDays = noticePeriodDays;
	}

	public int getAlertDays() {
		return alertDays;
	}

	public void setAlertDays(int alertDays) {
		this.alertDays = alertDays;
	}

	public String getAlertToRoles() {
		return alertToRoles;
	}

	public void setAlertToRoles(String alertToRoles) {
		this.alertToRoles = alertToRoles;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getAlertTypeName() {
		return alertTypeName;
	}

	public void setAlertTypeName(String alertTypeName) {
		this.alertTypeName = alertTypeName;
	}

	public String getAlertToRolesName() {
		return alertToRolesName;
	}

	public void setAlertToRolesName(String alertToRolesName) {
		this.alertToRolesName = alertToRolesName;
	}

	public Long getUserTemplate() {
		return userTemplate;
	}

	public void setUserTemplate(Long userTemplate) {
		this.userTemplate = userTemplate;
	}

	public String getUserTemplateName() {
		return userTemplateName;
	}

	public void setUserTemplateName(String userTemplateName) {
		this.userTemplateName = userTemplateName;
	}

	public Long getCustomerTemplate() {
		return customerTemplate;
	}

	public void setCustomerTemplate(Long customerTemplate) {
		this.customerTemplate = customerTemplate;
	}

	public String getCustomerTemplateName() {
		return customerTemplateName;
	}

	public void setCustomerTemplateName(String customerTemplateName) {
		this.customerTemplateName = customerTemplateName;
	}

	public String getUserTemplateCode() {
		return userTemplateCode;
	}

	public void setUserTemplateCode(String userTemplateCode) {
		this.userTemplateCode = userTemplateCode;
	}

	public String getCustomerTemplateCode() {
		return customerTemplateCode;
	}

	public void setCustomerTemplateCode(String customerTemplateCode) {
		this.customerTemplateCode = customerTemplateCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getTotPenal() {
		return totPenal;
	}

	public void setTotPenal(BigDecimal totPenal) {
		this.totPenal = totPenal;
	}

	public BigDecimal getPenaltyDue() {
		return penaltyDue;
	}

	public void setPenaltyDue(BigDecimal penaltyDue) {
		this.penaltyDue = penaltyDue;
	}

	public BigDecimal getPenaltyWaived() {
		return penaltyWaived;
	}

	public void setPenaltyWaived(BigDecimal penaltyWaived) {
		this.penaltyWaived = penaltyWaived;
	}

	public BigDecimal getInterestInclAccrued() {
		return interestInclAccrued;
	}

	public void setInterestInclAccrued(BigDecimal interestInclAccrued) {
		this.interestInclAccrued = interestInclAccrued;
	}

	public BigDecimal getOtherChargers() {
		return otherChargers;
	}

	public void setOtherChargers(BigDecimal otherChargers) {
		this.otherChargers = otherChargers;
	}

	public BigDecimal getTdSchdPftBal() {
		return tdSchdPftBal;
	}

	public void setTdSchdPftBal(BigDecimal tdSchdPftBal) {
		this.tdSchdPftBal = tdSchdPftBal;
	}

	public BigDecimal getPftAmz() {
		return pftAmz;
	}

	public void setPftAmz(BigDecimal pftAmz) {
		this.pftAmz = pftAmz;
	}

	public BigDecimal getPftAccrued() {
		return pftAccrued;
	}

	public void setPftAccrued(BigDecimal pftAccrued) {
		this.pftAccrued = pftAccrued;
	}

}
