package com.pennanttech.pff.provision.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Provision extends NpaProvisionStage {
	private static final long serialVersionUID = 4462877022963514677L;

	private Date provisionDate;
	private boolean manualProvision;
	private BigDecimal regProvsnPer = BigDecimal.ZERO;
	private BigDecimal regProvsnAmt = BigDecimal.ZERO;
	private BigDecimal regSecProvsnPer = BigDecimal.ZERO;
	private BigDecimal regSecProvsnAmt = BigDecimal.ZERO;
	private BigDecimal regUnSecProvsnPer = BigDecimal.ZERO;
	private BigDecimal regUnSecProvsnAmt = BigDecimal.ZERO;
	private BigDecimal totRegProvsnAmt = BigDecimal.ZERO;
	private BigDecimal intProvsnPer = BigDecimal.ZERO;
	private BigDecimal intProvsnAmt = BigDecimal.ZERO;
	private BigDecimal intSecProvsnPer = BigDecimal.ZERO;
	private BigDecimal intSecProvsnAmt = BigDecimal.ZERO;
	private BigDecimal intUnSecProvsnPer = BigDecimal.ZERO;
	private BigDecimal intUnSecProvsnAmt = BigDecimal.ZERO;
	private BigDecimal totIntProvsnAmt = BigDecimal.ZERO;
	private BigDecimal manProvsnPer = BigDecimal.ZERO;
	private BigDecimal manProvsnAmt = BigDecimal.ZERO;
	private int npaAging;
	private int effNpaAging;
	private int npaPastDueDays;
	private Long npaClassID;
	private String effNpaClassCode;
	private String effNpaSubClassCode;
	private Long effNpaClassID;
	private BigDecimal collateralAmt = BigDecimal.ZERO;
	private BigDecimal insuranceAmt = BigDecimal.ZERO;
	private boolean npaClassChng;
	private Long linkedTranId;
	private Long chgLinkedTranId;
	private Long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;

	private String loanClassification;
	private String effectiveClassification;
	private String custCIF;
	private String custShrtName;
	private Provision befImage;
	private LoggedInUser userDetails;
	private List<String> assetClassCodes;
	private List<String> assetSubClassCodes;
	private String effManualAssetClass;
	private String effManualAssetSubClass;
	private BigDecimal newRegProvisionPer = BigDecimal.ZERO;
	private BigDecimal newRegProvisionAmt = BigDecimal.ZERO;
	private BigDecimal newIntProvisionPer = BigDecimal.ZERO;
	private BigDecimal newIntProvisionAmt = BigDecimal.ZERO;

	private boolean newRecord = false;
	private boolean overrideProvision = false;

	public Provision() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("effNpaClassCode");
		excludeFields.add("effNpaSubClassCode");
		excludeFields.add("npaClassChng");
		excludeFields.add("loanClassification");
		excludeFields.add("effectiveClassification");
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("eodDate");
		excludeFields.add("entityCode");
		excludeFields.add("custID");
		excludeFields.add("custCategoryCode");
		excludeFields.add("finType");
		excludeFields.add("product");
		excludeFields.add("finCcy");
		excludeFields.add("finBranch");
		excludeFields.add("finAssetValue");
		excludeFields.add("finCurrAssetValue");
		excludeFields.add("futurePrincipal");
		excludeFields.add("odPrincipal");
		excludeFields.add("odProfit");
		excludeFields.add("totPriBal");
		excludeFields.add("totPriPaid");
		excludeFields.add("totPftPaid");
		excludeFields.add("totPftAccrued");
		excludeFields.add("amzTillLBDate");
		excludeFields.add("tillDateSchdPri");
		excludeFields.add("pastDueDate");
		excludeFields.add("effFinID");
		excludeFields.add("effFinReference");
		excludeFields.add("nonEffLoan");
		excludeFields.add("finStartDate");
		excludeFields.add("maturityDate");
		excludeFields.add("linkedLoan");
		excludeFields.add("effPastDueDays");
		excludeFields.add("effPastDueDate");
		excludeFields.add("effManualAssetClass");
		excludeFields.add("effManualAssetSubClass");
		excludeFields.add("newRegProvisionPer");
		excludeFields.add("newRegProvisionAmt");
		excludeFields.add("newIntProvisionPer");
		excludeFields.add("newIntProvisionAmt");
		excludeFields.add("custCoreBank");
		excludeFields.add("assetClassCodes");
		excludeFields.add("assetSubClassCodes");
		excludeFields.add("id");
		excludeFields.add("osPrincipal");
		excludeFields.add("osProfit");
		excludeFields.add("pastDueDays");
		excludeFields.add("derivedPastDueDate");
		excludeFields.add("effNpaPastDueDays");
		excludeFields.add("emiRe");
		excludeFields.add("instIncome");
		excludeFields.add("futurePri");
		excludeFields.add("prvEmiRe");
		excludeFields.add("prvInstIncome");
		excludeFields.add("prvFuturePri");
		excludeFields.add("selfEffected");
		excludeFields.add("writeOffLoan");
		excludeFields.add("underSettlement");
		excludeFields.add("overrideProvision");

		return excludeFields;
	}

	public Date getProvisionDate() {
		return provisionDate;
	}

	public void setProvisionDate(Date provisionDate) {
		this.provisionDate = provisionDate;
	}

	public boolean isManualProvision() {
		return manualProvision;
	}

	public void setManualProvision(boolean manualProvision) {
		this.manualProvision = manualProvision;
	}

	public BigDecimal getRegProvsnPer() {
		return regProvsnPer;
	}

	public void setRegProvsnPer(BigDecimal regProvsnPer) {
		this.regProvsnPer = regProvsnPer;
	}

	public BigDecimal getRegProvsnAmt() {
		return regProvsnAmt;
	}

	public void setRegProvsnAmt(BigDecimal regProvsnAmt) {
		this.regProvsnAmt = regProvsnAmt;
	}

	public BigDecimal getRegSecProvsnPer() {
		return regSecProvsnPer;
	}

	public void setRegSecProvsnPer(BigDecimal regSecProvsnPer) {
		this.regSecProvsnPer = regSecProvsnPer;
	}

	public BigDecimal getRegSecProvsnAmt() {
		return regSecProvsnAmt;
	}

	public void setRegSecProvsnAmt(BigDecimal regSecProvsnAmt) {
		this.regSecProvsnAmt = regSecProvsnAmt;
	}

	public BigDecimal getRegUnSecProvsnPer() {
		return regUnSecProvsnPer;
	}

	public void setRegUnSecProvsnPer(BigDecimal regUnSecProvsnPer) {
		this.regUnSecProvsnPer = regUnSecProvsnPer;
	}

	public BigDecimal getRegUnSecProvsnAmt() {
		return regUnSecProvsnAmt;
	}

	public void setRegUnSecProvsnAmt(BigDecimal regUnSecProvsnAmt) {
		this.regUnSecProvsnAmt = regUnSecProvsnAmt;
	}

	public BigDecimal getTotRegProvsnAmt() {
		return totRegProvsnAmt;
	}

	public void setTotRegProvsnAmt(BigDecimal totRegProvsnAmt) {
		this.totRegProvsnAmt = totRegProvsnAmt;
	}

	public BigDecimal getIntProvsnPer() {
		return intProvsnPer;
	}

	public void setIntProvsnPer(BigDecimal intProvsnPer) {
		this.intProvsnPer = intProvsnPer;
	}

	public BigDecimal getIntProvsnAmt() {
		return intProvsnAmt;
	}

	public void setIntProvsnAmt(BigDecimal intProvsnAmt) {
		this.intProvsnAmt = intProvsnAmt;
	}

	public BigDecimal getIntSecProvsnPer() {
		return intSecProvsnPer;
	}

	public void setIntSecProvsnPer(BigDecimal intSecProvsnPer) {
		this.intSecProvsnPer = intSecProvsnPer;
	}

	public BigDecimal getIntSecProvsnAmt() {
		return intSecProvsnAmt;
	}

	public void setIntSecProvsnAmt(BigDecimal intSecProvsnAmt) {
		this.intSecProvsnAmt = intSecProvsnAmt;
	}

	public BigDecimal getIntUnSecProvsnPer() {
		return intUnSecProvsnPer;
	}

	public void setIntUnSecProvsnPer(BigDecimal intUnSecProvsnPer) {
		this.intUnSecProvsnPer = intUnSecProvsnPer;
	}

	public BigDecimal getIntUnSecProvsnAmt() {
		return intUnSecProvsnAmt;
	}

	public void setIntUnSecProvsnAmt(BigDecimal intUnSecProvsnAmt) {
		this.intUnSecProvsnAmt = intUnSecProvsnAmt;
	}

	public BigDecimal getTotIntProvsnAmt() {
		return totIntProvsnAmt;
	}

	public void setTotIntProvsnAmt(BigDecimal totIntProvsnAmt) {
		this.totIntProvsnAmt = totIntProvsnAmt;
	}

	public BigDecimal getManProvsnPer() {
		return manProvsnPer;
	}

	public void setManProvsnPer(BigDecimal manProvsnPer) {
		this.manProvsnPer = manProvsnPer;
	}

	public BigDecimal getManProvsnAmt() {
		return manProvsnAmt;
	}

	public void setManProvsnAmt(BigDecimal manProvsnAmt) {
		this.manProvsnAmt = manProvsnAmt;
	}

	public int getNpaAging() {
		return npaAging;
	}

	public void setNpaAging(int npaAging) {
		this.npaAging = npaAging;
	}

	public int getEffNpaAging() {
		return effNpaAging;
	}

	public void setEffNpaAging(int effNpaAging) {
		this.effNpaAging = effNpaAging;
	}

	public int getNpaPastDueDays() {
		return npaPastDueDays;
	}

	public void setNpaPastDueDays(int npaPastDueDays) {
		this.npaPastDueDays = npaPastDueDays;
	}

	public Long getNpaClassID() {
		return npaClassID;
	}

	public void setNpaClassID(Long npaClassID) {
		this.npaClassID = npaClassID;
	}

	public String getEffNpaClassCode() {
		return effNpaClassCode;
	}

	public void setEffNpaClassCode(String effNpaClassCode) {
		this.effNpaClassCode = effNpaClassCode;
	}

	public String getEffNpaSubClassCode() {
		return effNpaSubClassCode;
	}

	public void setEffNpaSubClassCode(String effNpaSubClassCode) {
		this.effNpaSubClassCode = effNpaSubClassCode;
	}

	public Long getEffNpaClassID() {
		return effNpaClassID;
	}

	public void setEffNpaClassID(Long effNpaClassID) {
		this.effNpaClassID = effNpaClassID;
	}

	public boolean isNpaClassChng() {
		return npaClassChng;
	}

	public void setNpaClassChng(boolean npaClassChng) {
		this.npaClassChng = npaClassChng;
	}

	public BigDecimal getCollateralAmt() {
		return collateralAmt;
	}

	public void setCollateralAmt(BigDecimal collateralAmt) {
		this.collateralAmt = collateralAmt;
	}

	public BigDecimal getInsuranceAmt() {
		return insuranceAmt;
	}

	public void setInsuranceAmt(BigDecimal insuranceAmt) {
		this.insuranceAmt = insuranceAmt;
	}

	public Long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(Long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public Long getChgLinkedTranId() {
		return chgLinkedTranId;
	}

	public void setChgLinkedTranId(Long chgLinkedTranId) {
		this.chgLinkedTranId = chgLinkedTranId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getLoanClassification() {
		return loanClassification;
	}

	public void setLoanClassification(String loanClassification) {
		this.loanClassification = loanClassification;
	}

	public String getEffectiveClassification() {
		return effectiveClassification;
	}

	public void setEffectiveClassification(String effectiveClassification) {
		this.effectiveClassification = effectiveClassification;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Provision getBefImage() {
		return befImage;
	}

	public void setBefImage(Provision befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public BigDecimal getNewRegProvisionPer() {
		return newRegProvisionPer;
	}

	public void setNewRegProvisionPer(BigDecimal newRegProvisionPer) {
		this.newRegProvisionPer = newRegProvisionPer;
	}

	public BigDecimal getNewRegProvisionAmt() {
		return newRegProvisionAmt;
	}

	public void setNewRegProvisionAmt(BigDecimal newRegProvisionAmt) {
		this.newRegProvisionAmt = newRegProvisionAmt;
	}

	public BigDecimal getNewIntProvisionPer() {
		return newIntProvisionPer;
	}

	public void setNewIntProvisionPer(BigDecimal newIntProvisionPer) {
		this.newIntProvisionPer = newIntProvisionPer;
	}

	public BigDecimal getNewIntProvisionAmt() {
		return newIntProvisionAmt;
	}

	public void setNewIntProvisionAmt(BigDecimal newIntProvisionAmt) {
		this.newIntProvisionAmt = newIntProvisionAmt;
	}

	public boolean isOverrideProvision() {
		return overrideProvision;
	}

	public void setOverrideProvision(boolean overrideProvision) {
		this.overrideProvision = overrideProvision;
	}

	public List<String> getAssetClassCodes() {
		return assetClassCodes;
	}

	public void setAssetClassCodes(List<String> assetClassCodes) {
		this.assetClassCodes = assetClassCodes;
	}

	public String getEffManualAssetClass() {
		return effManualAssetClass;
	}

	public void setEffManualAssetClass(String effManualAssetClass) {
		this.effManualAssetClass = effManualAssetClass;
	}

	public String getEffManualAssetSubClass() {
		return effManualAssetSubClass;
	}

	public void setEffManualAssetSubClass(String effManualAssetSubClass) {
		this.effManualAssetSubClass = effManualAssetSubClass;
	}

	public List<String> getAssetSubClassCodes() {
		return assetSubClassCodes;
	}

	public void setAssetSubClassCodes(List<String> assetSubClassCodes) {
		this.assetSubClassCodes = assetSubClassCodes;
	}
	
	

}
