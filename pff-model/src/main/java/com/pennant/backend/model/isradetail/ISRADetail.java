package com.pennant.backend.model.isradetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ISRADetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long id = Long.MIN_VALUE;
	private String finReference;
	private BigDecimal minISRAAmt = BigDecimal.ZERO;
	private BigDecimal minDSRAAmt = BigDecimal.ZERO;
	private BigDecimal totalAmt = BigDecimal.ZERO;
	private BigDecimal undisbursedLimit = BigDecimal.ZERO;
	private BigDecimal fundsAmt = BigDecimal.ZERO;
	private BigDecimal shortfallAmt = BigDecimal.ZERO;
	private BigDecimal excessCashCltAmt = BigDecimal.ZERO;
	private boolean newRecord;
	private ISRADetail befImage;
	private List<ISRALiquidDetail> israLiquidDetails = new ArrayList<>(1);

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("israLiquidDetails");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getMinISRAAmt() {
		return minISRAAmt;
	}

	public void setMinISRAAmt(BigDecimal minISRAAmt) {
		this.minISRAAmt = minISRAAmt;
	}

	public BigDecimal getMinDSRAAmt() {
		return minDSRAAmt;
	}

	public void setMinDSRAAmt(BigDecimal minDSRAAmt) {
		this.minDSRAAmt = minDSRAAmt;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public BigDecimal getUndisbursedLimit() {
		return undisbursedLimit;
	}

	public void setUndisbursedLimit(BigDecimal undisbursedLimit) {
		this.undisbursedLimit = undisbursedLimit;
	}

	public BigDecimal getFundsAmt() {
		return fundsAmt;
	}

	public void setFundsAmt(BigDecimal fundsAmt) {
		this.fundsAmt = fundsAmt;
	}

	public BigDecimal getShortfallAmt() {
		return shortfallAmt;
	}

	public void setShortfallAmt(BigDecimal shortfallAmt) {
		this.shortfallAmt = shortfallAmt;
	}

	public BigDecimal getExcessCashCltAmt() {
		return excessCashCltAmt;
	}

	public void setExcessCashCltAmt(BigDecimal excessCashCltAmt) {
		this.excessCashCltAmt = excessCashCltAmt;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public ISRADetail getBefImage() {
		return befImage;
	}

	public void setBefImage(ISRADetail befImage) {
		this.befImage = befImage;
	}

	public List<ISRALiquidDetail> getIsraLiquidDetails() {
		return israLiquidDetails;
	}

	public void setIsraLiquidDetails(List<ISRALiquidDetail> israLiquidDetails) {
		this.israLiquidDetails = israLiquidDetails;
	}

}
