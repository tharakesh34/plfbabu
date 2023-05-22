package com.pennant.backend.model.crossloanknockoff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CrossLoanKnockoffUpload extends UploadDetails {

	private static final long serialVersionUID = -5595233273372558144L;

	private long id;
	private Long fromFinID;
	private Long toFinID;
	private String fromFinReference;
	private String toFinReference;
	private String excessType;
	private String feeTypeCode;
	private BigDecimal excessAmount;
	private String allocationType;
	private Long feeId;
	private String code;
	private BigDecimal amount;
	private FinanceMain fromFm;
	private FinanceMain toFm;
	private List<FinExcessAmount> excessList;
	private String entityCode;
	private List<CrossLoanKnockoffUpload> allocations = new ArrayList<>();
	private LoggedInUser userDetails;
	private Long crossLoanId;
	private Date appDate;
	private List<ManualAdvise> advises = new ArrayList<>();
	private FinanceType finType;

	private BigDecimal balanceAmount = BigDecimal.ZERO;

	public CrossLoanKnockoffUpload() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getFromFinID() {
		return fromFinID;
	}

	public void setFromFinID(Long fromFinID) {
		this.fromFinID = fromFinID;
	}

	public Long getToFinID() {
		return toFinID;
	}

	public void setToFinID(Long toFinID) {
		this.toFinID = toFinID;
	}

	public String getFromFinReference() {
		return fromFinReference;
	}

	public void setFromFinReference(String fromFinReference) {
		this.fromFinReference = fromFinReference;
	}

	public String getToFinReference() {
		return toFinReference;
	}

	public void setToFinReference(String toFinReference) {
		this.toFinReference = toFinReference;
	}

	public String getExcessType() {
		return excessType;
	}

	public void setExcessType(String excessType) {
		this.excessType = excessType;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public Long getFeeId() {
		return feeId;
	}

	public void setFeeId(Long feeId) {
		this.feeId = feeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public List<CrossLoanKnockoffUpload> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<CrossLoanKnockoffUpload> allocations) {
		this.allocations = allocations;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinanceMain getFromFm() {
		return fromFm;
	}

	public void setFromFm(FinanceMain fromFm) {
		this.fromFm = fromFm;
	}

	public FinanceMain getToFm() {
		return toFm;
	}

	public void setToFm(FinanceMain toFm) {
		this.toFm = toFm;
	}

	public List<FinExcessAmount> getExcessList() {
		return excessList;
	}

	public void setExcessList(List<FinExcessAmount> excessList) {
		this.excessList = excessList;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public Long getCrossLoanId() {
		return crossLoanId;
	}

	public void setCrossLoanId(Long crossLoanId) {
		this.crossLoanId = crossLoanId;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public List<ManualAdvise> getAdvises() {
		return advises;
	}

	public void setAdvises(List<ManualAdvise> advises) {
		this.advises = advises;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
	}

}