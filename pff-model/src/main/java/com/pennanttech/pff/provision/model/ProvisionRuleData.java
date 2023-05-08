package com.pennanttech.pff.provision.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProvisionRuleData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long custID;
	private String entityCode;
	private Long finID;
	private String finReference;
	private String finCCY;
	private String finBranch;
	private String finType;
	private String custCategory;
	private Long npaClassID;
	private String npaClassCode;
	private Long npaSubClassID;
	private String npaSubClassCode;
	private int pastDueDays;
	private int npaPastDueDays;
	private boolean npaStage;
	private boolean effNpaStage;
	private Long effNpaClassID;
	private String effNpaClassCode;
	private Long effNpaSubbClassID;
	private String effNpaSubClassCode;
	private int effNpaPastDueDays;
	private String productCategory;
	private boolean secured;
	private BigDecimal securedPercentage;
	private String collateralType;
	private BigDecimal insuranceAmount;
	private boolean restrutureLoan;
	private boolean repossessedLoan;
	private BigDecimal outstandingprincipal;
	private BigDecimal overdueEMI;
	private BigDecimal loanAmount;
	private BigDecimal disbursedAmount;
	private String regProvsnRule;
	private String intProvsnRule;
	private BigDecimal osProfit;
	private BigDecimal totPftAccrued;
	private BigDecimal tillDateSchdPri;
	private BigDecimal odPrincipal;
	private BigDecimal odProfit;
	private int npaAge;
	private int effNpaAge;
	private long effAssetClassID;
	private long effAssetSubClassID;

	public ProvisionRuleData() {
		super();
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinCCY() {
		return finCCY;
	}

	public void setFinCCY(String finCCY) {
		this.finCCY = finCCY;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public Long getNpaClassID() {
		return npaClassID;
	}

	public void setNpaClassID(Long npaClassID) {
		this.npaClassID = npaClassID;
	}

	public String getNpaClassCode() {
		return npaClassCode;
	}

	public void setNpaClassCode(String npaClassCode) {
		this.npaClassCode = npaClassCode;
	}

	public Long getNpaSubClassID() {
		return npaSubClassID;
	}

	public void setNpaSubClassID(Long npaSubClassID) {
		this.npaSubClassID = npaSubClassID;
	}

	public String getNpaSubClassCode() {
		return npaSubClassCode;
	}

	public void setNpaSubClassCode(String npaSubClassCode) {
		this.npaSubClassCode = npaSubClassCode;
	}

	public int getPastDueDays() {
		return pastDueDays;
	}

	public void setPastDueDays(int pastDueDays) {
		this.pastDueDays = pastDueDays;
	}

	public int getNpaPastDueDays() {
		return npaPastDueDays;
	}

	public void setNpaPastDueDays(int npaPastDueDays) {
		this.npaPastDueDays = npaPastDueDays;
	}

	public boolean isNpaStage() {
		return npaStage;
	}

	public void setNpaStage(boolean npaStage) {
		this.npaStage = npaStage;
	}

	public boolean isEffNpaStage() {
		return effNpaStage;
	}

	public void setEffNpaStage(boolean effNpaStage) {
		this.effNpaStage = effNpaStage;
	}

	public Long getEffNpaClassID() {
		return effNpaClassID;
	}

	public void setEffNpaClassID(Long effNpaClassID) {
		this.effNpaClassID = effNpaClassID;
	}

	public String getEffNpaClassCode() {
		return effNpaClassCode;
	}

	public void setEffNpaClassCode(String effNpaClassCode) {
		this.effNpaClassCode = effNpaClassCode;
	}

	public Long getEffNpaSubbClassID() {
		return effNpaSubbClassID;
	}

	public void setEffNpaSubbClassID(Long effNpaSubbClassID) {
		this.effNpaSubbClassID = effNpaSubbClassID;
	}

	public String getEffNpaSubClassCode() {
		return effNpaSubClassCode;
	}

	public void setEffNpaSubClassCode(String effNpaSubClassCode) {
		this.effNpaSubClassCode = effNpaSubClassCode;
	}

	public int getEffNpaPastDueDays() {
		return effNpaPastDueDays;
	}

	public void setEffNpaPastDueDays(int effNpaPastDueDays) {
		this.effNpaPastDueDays = effNpaPastDueDays;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public BigDecimal getSecuredPercentage() {
		return securedPercentage;
	}

	public void setSecuredPercentage(BigDecimal securedPercentage) {
		this.securedPercentage = securedPercentage;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public BigDecimal getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public boolean isRestrutureLoan() {
		return restrutureLoan;
	}

	public void setRestrutureLoan(boolean restrutureLoan) {
		this.restrutureLoan = restrutureLoan;
	}

	public boolean isRepossessedLoan() {
		return repossessedLoan;
	}

	public void setRepossessedLoan(boolean repossessedLoan) {
		this.repossessedLoan = repossessedLoan;
	}

	public BigDecimal getOutstandingprincipal() {
		return outstandingprincipal;
	}

	public void setOutstandingprincipal(BigDecimal outstandingprincipal) {
		this.outstandingprincipal = outstandingprincipal;
	}

	public BigDecimal getOverdueEMI() {
		return overdueEMI;
	}

	public void setOverdueEMI(BigDecimal overdueEMI) {
		this.overdueEMI = overdueEMI;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public String getRegProvsnRule() {
		return regProvsnRule;
	}

	public void setRegProvsnRule(String regProvsnRule) {
		this.regProvsnRule = regProvsnRule;
	}

	public String getIntProvsnRule() {
		return intProvsnRule;
	}

	public void setIntProvsnRule(String intProvsnRule) {
		this.intProvsnRule = intProvsnRule;
	}

	public BigDecimal getOsProfit() {
		return osProfit;
	}

	public void setOsProfit(BigDecimal osProfit) {
		this.osProfit = osProfit;
	}

	public BigDecimal getTotPftAccrued() {
		return totPftAccrued;
	}

	public void setTotPftAccrued(BigDecimal totPftAccrued) {
		this.totPftAccrued = totPftAccrued;
	}

	public BigDecimal getTillDateSchdPri() {
		return tillDateSchdPri;
	}

	public void setTillDateSchdPri(BigDecimal tillDateSchdPri) {
		this.tillDateSchdPri = tillDateSchdPri;
	}

	public BigDecimal getOdPrincipal() {
		return odPrincipal;
	}

	public void setOdPrincipal(BigDecimal odPrincipal) {
		this.odPrincipal = odPrincipal;
	}

	public BigDecimal getOdProfit() {
		return odProfit;
	}

	public void setOdProfit(BigDecimal odProfit) {
		this.odProfit = odProfit;
	}

	public int getNpaAge() {
		return npaAge;
	}

	public void setNpaAge(int npaAge) {
		this.npaAge = npaAge;
	}

	public int getEffNpaAge() {
		return effNpaAge;
	}

	public void setEffNpaAge(int effNpaAge) {
		this.effNpaAge = effNpaAge;
	}

	public long getEffAssetClassID() {
		return effAssetClassID;
	}

	public void setEffAssetClassID(long effAssetClassID) {
		this.effAssetClassID = effAssetClassID;
	}

	public long getEffAssetSubClassID() {
		return effAssetSubClassID;
	}

	public void setEffAssetSubClassID(long effAssetSubClassID) {
		this.effAssetSubClassID = effAssetSubClassID;
	}

	public Map<String, Object> getDeclaredFields() {
		Map<String, Object> map = new HashMap<>();

		map.put("custID", this.custID);
		map.put("entityCode", this.entityCode);
		map.put("finID", this.finID);
		map.put("finReference", this.finReference);
		map.put("finCCY", this.finCCY);
		map.put("finBranch", this.finBranch);
		map.put("finType", this.finType);
		map.put("custCategory", this.custCategory);
		map.put("npaClassID", this.npaClassID);
		map.put("npaClassCode", this.npaClassCode);
		map.put("npaSubClassID", this.npaSubClassID);
		map.put("npaSubClassCode", this.npaSubClassCode);
		map.put("pastDueDays", this.pastDueDays);
		map.put("effNpabClassID", this.effNpaClassID);
		map.put("effNpaClassCode", this.effNpaClassCode);
		map.put("effNpaSubbClassID", this.effNpaSubbClassID);
		map.put("effNpaSubClassCode", this.effNpaSubClassCode);
		map.put("effNpaPastDueDays", this.effNpaPastDueDays);
		map.put("productCategory", this.productCategory);
		map.put("secured", this.secured);
		map.put("securedPercentage", this.securedPercentage);
		map.put("collateralType", this.collateralType);
		map.put("insuranceAmount", this.insuranceAmount);
		map.put("restrutureLoan", this.restrutureLoan);
		map.put("repossessedLoan", this.repossessedLoan);
		map.put("outstandingprincipal", this.outstandingprincipal);
		map.put("overdueEMI", this.overdueEMI);
		map.put("loanAmount", this.loanAmount);
		map.put("disbursedAmount", this.disbursedAmount);
		map.put("regProvsnRule", this.regProvsnRule);
		map.put("intProvsnRule", this.intProvsnRule);
		map.put("osProfit", this.osProfit);
		map.put("totPftAccrued", this.totPftAccrued);
		map.put("tillDateSchdPri", this.tillDateSchdPri);
		map.put("odPrincipal", this.odPrincipal);
		map.put("odProfit", this.odProfit);

		return map;
	}

}
