package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finCcy", "lovDescFinCcyName", "finDaysCalType", "lovDescFinDaysCalTypeName", "finMinAmount",
		"finMaxAmount", "finCategory", "finAssetType", "finIsDwPayRequired", "downPayRule", "downPayRuleDesc",
		"fInIsAlwGrace", "finIsAlwMD", "alwMaxDisbCheckReq", "limitRequired", "overrideLimit", "finCollateralReq",
		"finCollateralOvrride", "collateralType", "partiallySecured", "finDivision", "finIsActive", "finCommitmentReq",
		"finCommitmentOvrride", "alwMultiPartyDisb", "tDSApplicable" })
@XmlRootElement(name = "basicDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class BasicDetail implements Serializable {

	private static final long serialVersionUID = -3980105614314704735L;

	public BasicDetail() {
	    super();
	}

	private String finCcy;
	@XmlElement(name = "finCcyDesc")
	private String lovDescFinCcyName;

	@XmlElement(name = "profitDaysBasis")
	private String finDaysCalType;

	@XmlElement(name = "profitDaysBasisDesc")
	private String lovDescFinDaysCalTypeName;

	private BigDecimal finMinAmount = BigDecimal.ZERO;
	private BigDecimal finMaxAmount = BigDecimal.ZERO;

	private String finCategory;

	private String finAssetType;

	@XmlElement(name = "downPayReq")
	private boolean finIsDwPayRequired;
	private long downPayRule;
	private String downPayRuleDesc;
	private boolean fInIsAlwGrace;
	private boolean finIsAlwMD;
	private boolean alwMaxDisbCheckReq;
	private boolean limitRequired;
	private boolean overrideLimit;
	private boolean finCollateralReq;
	private boolean finCollateralOvrride;
	private String collateralType;
	private String finDivision;
	private boolean finIsActive;
	private boolean finCommitmentReq;
	private boolean finCommitmentOvrride;

	private boolean alwMultiPartyDisb;
	private boolean tDSApplicable;
	private boolean partiallySecured;

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getLovDescFinCcyName() {
		return lovDescFinCcyName;
	}

	public void setLovDescFinCcyName(String lovDescFinCcyName) {
		this.lovDescFinCcyName = lovDescFinCcyName;
	}

	public String getFinDaysCalType() {
		return finDaysCalType;
	}

	public void setFinDaysCalType(String finDaysCalType) {
		this.finDaysCalType = finDaysCalType;
	}

	public String getLovDescFinDaysCalTypeName() {
		return lovDescFinDaysCalTypeName;
	}

	public void setLovDescFinDaysCalTypeName(String lovDescFinDaysCalTypeName) {
		this.lovDescFinDaysCalTypeName = lovDescFinDaysCalTypeName;
	}

	public BigDecimal getFinMinAmount() {
		return finMinAmount;
	}

	public void setFinMinAmount(BigDecimal finMinAmount) {
		this.finMinAmount = finMinAmount;
	}

	public BigDecimal getFinMaxAmount() {
		return finMaxAmount;
	}

	public void setFinMaxAmount(BigDecimal finMaxAmount) {
		this.finMaxAmount = finMaxAmount;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getFinAssetType() {
		return finAssetType;
	}

	public void setFinAssetType(String finAssetType) {
		this.finAssetType = finAssetType;
	}

	public boolean isFinIsDwPayRequired() {
		return finIsDwPayRequired;
	}

	public void setFinIsDwPayRequired(boolean finIsDwPayRequired) {
		this.finIsDwPayRequired = finIsDwPayRequired;
	}

	public long getDownPayRule() {
		return downPayRule;
	}

	public void setDownPayRule(long downPayRule) {
		this.downPayRule = downPayRule;
	}

	public String getDownPayRuleDesc() {
		return downPayRuleDesc;
	}

	public void setDownPayRuleDesc(String downPayRuleDesc) {
		this.downPayRuleDesc = downPayRuleDesc;
	}

	public boolean isfInIsAlwGrace() {
		return fInIsAlwGrace;
	}

	public void setfInIsAlwGrace(boolean fInIsAlwGrace) {
		this.fInIsAlwGrace = fInIsAlwGrace;
	}

	public boolean isFinIsAlwMD() {
		return finIsAlwMD;
	}

	public void setFinIsAlwMD(boolean finIsAlwMD) {
		this.finIsAlwMD = finIsAlwMD;
	}

	public boolean isLimitRequired() {
		return limitRequired;
	}

	public void setLimitRequired(boolean limitRequired) {
		this.limitRequired = limitRequired;
	}

	public boolean isOverrideLimit() {
		return overrideLimit;
	}

	public void setOverrideLimit(boolean overrideLimit) {
		this.overrideLimit = overrideLimit;
	}

	public boolean isFinCollateralReq() {
		return finCollateralReq;
	}

	public void setFinCollateralReq(boolean finCollateralReq) {
		this.finCollateralReq = finCollateralReq;
	}

	public boolean isFinCollateralOvrride() {
		return finCollateralOvrride;
	}

	public void setFinCollateralOvrride(boolean finCollateralOvrride) {
		this.finCollateralOvrride = finCollateralOvrride;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public boolean isFinCommitmentReq() {
		return finCommitmentReq;
	}

	public void setFinCommitmentReq(boolean finCommitmentReq) {
		this.finCommitmentReq = finCommitmentReq;
	}

	public boolean isFinCommitmentOvrride() {
		return finCommitmentOvrride;
	}

	public void setFinCommitmentOvrride(boolean finCommitmentOvrride) {
		this.finCommitmentOvrride = finCommitmentOvrride;
	}

	public boolean isAlwMultiPartyDisb() {
		return alwMultiPartyDisb;
	}

	public void setAlwMultiPartyDisb(boolean alwMultiPartyDisb) {
		this.alwMultiPartyDisb = alwMultiPartyDisb;
	}

	public boolean istDSApplicable() {
		return tDSApplicable;
	}

	public void settDSApplicable(boolean tDSApplicable) {
		this.tDSApplicable = tDSApplicable;
	}

	public boolean isAlwMaxDisbCheckReq() {
		return alwMaxDisbCheckReq;
	}

	public void setAlwMaxDisbCheckReq(boolean alwMaxDisbCheckReq) {
		this.alwMaxDisbCheckReq = alwMaxDisbCheckReq;
	}

	public boolean isPartiallySecured() {
		return partiallySecured;
	}

	public void setPartiallySecured(boolean partiallySecured) {
		this.partiallySecured = partiallySecured;
	}

}
