package com.pennanttech.pff.provision.model;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class NpaProvisionStage extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -7099393261992931276L;

	private Long id;
	private Date eodDate;
	private String entityCode;
	private long custID;
	private String custCoreBank;
	private String custCategoryCode;
	private String finType;
	private String product;
	private String finCcy;
	private String finBranch;
	private Long finID;
	private String finReference;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private BigDecimal osPrincipal = BigDecimal.ZERO;
	private BigDecimal osProfit = BigDecimal.ZERO;
	private BigDecimal futurePrincipal = BigDecimal.ZERO;
	private BigDecimal odPrincipal = BigDecimal.ZERO;
	private BigDecimal odProfit = BigDecimal.ZERO;
	private BigDecimal totPriBal = BigDecimal.ZERO;
	private BigDecimal totPriPaid = BigDecimal.ZERO;
	private BigDecimal totPftPaid = BigDecimal.ZERO;
	private BigDecimal totPftAccrued = BigDecimal.ZERO;
	private BigDecimal amzTillLBDate = BigDecimal.ZERO;
	private BigDecimal tillDateSchdPri = BigDecimal.ZERO;
	private int pastDueDays;
	private Date pastDueDate;
	private Long effFinID;
	private String effFinReference;
	private boolean linkedLoan;
	private Date derivedPastDueDate;
	private Date finStartDate;
	private Date maturityDate;
	private int effPastDueDays;
	private Date effPastDueDate;
	private int effNpaPastDueDays;
	private BigDecimal emiRe = BigDecimal.ZERO;
	private BigDecimal instIncome = BigDecimal.ZERO;
	private BigDecimal futurePri = BigDecimal.ZERO;
	private BigDecimal prvEmiRe = BigDecimal.ZERO;
	private BigDecimal prvInstIncome = BigDecimal.ZERO;
	private BigDecimal prvFuturePri = BigDecimal.ZERO;
	private boolean selfEffected;

	public NpaProvisionStage() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getEodDate() {
		return eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustCategoryCode() {
		return custCategoryCode;
	}

	public void setCustCategoryCode(String custCategoryCode) {
		this.custCategoryCode = custCategoryCode;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
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

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public BigDecimal getOsPrincipal() {
		return osPrincipal;
	}

	public void setOsPrincipal(BigDecimal osPrincipal) {
		this.osPrincipal = osPrincipal;
	}

	public BigDecimal getOsProfit() {
		return osProfit;
	}

	public void setOsProfit(BigDecimal osProfit) {
		this.osProfit = osProfit;
	}

	public BigDecimal getFuturePrincipal() {
		return futurePrincipal;
	}

	public void setFuturePrincipal(BigDecimal futurePrincipal) {
		this.futurePrincipal = futurePrincipal;
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

	public BigDecimal getTotPriBal() {
		return totPriBal;
	}

	public void setTotPriBal(BigDecimal totPriBal) {
		this.totPriBal = totPriBal;
	}

	public BigDecimal getTotPriPaid() {
		return totPriPaid;
	}

	public void setTotPriPaid(BigDecimal totPriPaid) {
		this.totPriPaid = totPriPaid;
	}

	public BigDecimal getTotPftPaid() {
		return totPftPaid;
	}

	public void setTotPftPaid(BigDecimal totPftPaid) {
		this.totPftPaid = totPftPaid;
	}

	public BigDecimal getTotPftAccrued() {
		return totPftAccrued;
	}

	public void setTotPftAccrued(BigDecimal totPftAccrued) {
		this.totPftAccrued = totPftAccrued;
	}

	public BigDecimal getAmzTillLBDate() {
		return amzTillLBDate;
	}

	public void setAmzTillLBDate(BigDecimal amzTillLBDate) {
		this.amzTillLBDate = amzTillLBDate;
	}

	public BigDecimal getTillDateSchdPri() {
		return tillDateSchdPri;
	}

	public void setTillDateSchdPri(BigDecimal tillDateSchdPri) {
		this.tillDateSchdPri = tillDateSchdPri;
	}

	public int getPastDueDays() {
		return pastDueDays;
	}

	public void setPastDueDays(int pastDueDays) {
		this.pastDueDays = pastDueDays;
	}

	public Date getPastDueDate() {
		return pastDueDate;
	}

	public void setPastDueDate(Date pastDueDate) {
		this.pastDueDate = pastDueDate;
	}

	public Long getEffFinID() {
		return effFinID;
	}

	public void setEffFinID(Long effFinID) {
		this.effFinID = effFinID;
	}

	public String getEffFinReference() {
		return effFinReference;
	}

	public void setEffFinReference(String effFinReference) {
		this.effFinReference = effFinReference;
	}

	public boolean isLinkedLoan() {
		return linkedLoan;
	}

	public void setLinkedLoan(boolean linkedLoan) {
		this.linkedLoan = linkedLoan;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public int getEffPastDueDays() {
		return effPastDueDays;
	}

	public void setEffPastDueDays(int effPastDueDays) {
		this.effPastDueDays = effPastDueDays;
	}

	public Date getEffPastDueDate() {
		return effPastDueDate;
	}

	public void setEffPastDueDate(Date effPastDueDate) {
		this.effPastDueDate = effPastDueDate;
	}

	public int getEffNpaPastDueDays() {
		return effNpaPastDueDays;
	}

	public void setEffNpaPastDueDays(int effNpaPastDueDays) {
		this.effNpaPastDueDays = effNpaPastDueDays;
	}

	public BigDecimal getEmiRe() {
		return emiRe;
	}

	public void setEmiRe(BigDecimal emiRe) {
		this.emiRe = emiRe;
	}

	public BigDecimal getInstIncome() {
		return instIncome;
	}

	public void setInstIncome(BigDecimal instIncome) {
		this.instIncome = instIncome;
	}

	public BigDecimal getFuturePri() {
		return futurePri;
	}

	public void setFuturePri(BigDecimal futurePri) {
		this.futurePri = futurePri;
	}

	public BigDecimal getPrvEmiRe() {
		return prvEmiRe;
	}

	public void setPrvEmiRe(BigDecimal prvEmiRe) {
		this.prvEmiRe = prvEmiRe;
	}

	public BigDecimal getPrvInstIncome() {
		return prvInstIncome;
	}

	public void setPrvInstIncome(BigDecimal prvInstIncome) {
		this.prvInstIncome = prvInstIncome;
	}

	public BigDecimal getPrvFuturePri() {
		return prvFuturePri;
	}

	public void setPrvFuturePri(BigDecimal prvFuturePri) {
		this.prvFuturePri = prvFuturePri;
	}

	public Date getDerivedPastDueDate() {
		return derivedPastDueDate;
	}

	public void setDerivedPastDueDate(Date derivedPastDueDate) {
		this.derivedPastDueDate = derivedPastDueDate;
	}

	public boolean isSelfEffected() {
		return selfEffected;
	}

	public void setSelfEffected(boolean selfEffected) {
		this.selfEffected = selfEffected;
	}
}
