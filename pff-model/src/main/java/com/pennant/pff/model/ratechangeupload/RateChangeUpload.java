package com.pennant.pff.model.ratechangeupload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class RateChangeUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String finReference;
	private Long batchId;
	private String baseRateCode;
	private BigDecimal margin = BigDecimal.ZERO;
	private Date effectiveFrom;
	private BigDecimal actualRate = BigDecimal.ZERO;
	private String specialRate;
	private Long finServInstId;
	private String recalType;
	private Date recalFromDate;
	private Date recalToDate;
	
	private String remarks;
	private String status;
	private FinanceMain fm;
	private List<ErrorDetail> errors = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRecalType() {
		return recalType;
	}

	public void setRecalType(String recalType) {
		this.recalType = recalType;
	}

	public Date getRecalFromDate() {
		return recalFromDate;
	}

	public void setRecalFromDate(Date recalFromDate) {
		this.recalFromDate = recalFromDate;
	}

	public Date getRecalToDate() {
		return recalToDate;
	}

	public void setRecalToDate(Date recalToDate) {
		this.recalToDate = recalToDate;
	}


	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBaseRateCode() {
		return baseRateCode;
	}

	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Long getFinServInstId() {
		return finServInstId;
	}

	public void setFinServInstId(Long finServInstId) {
		this.finServInstId = finServInstId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSpecialRate() {
		return specialRate;
	}

	public void setSpecialRate(String specialRate) {
		this.specialRate = specialRate;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public FinanceMain getFinanceMain() {
		return fm;
	}

	public void setFinanceMain(FinanceMain fm) {
		this.fm = fm;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errors;
	}

	public void setErrorDetail(ErrorDetail errorDetails) {
		if (errorDetails == null) {
			return;
		}
		errors.add(errorDetails);
	}

	public void setErrorDetail(long errorCode) {
		setErrorDetail(new ErrorDetail("Key", String.valueOf(errorCode), null, null));
	}

	public BigDecimal getActualRate() {
		return actualRate;
	}

	public void setActualRate(BigDecimal actualRate) {
		this.actualRate = actualRate;
	}

}
