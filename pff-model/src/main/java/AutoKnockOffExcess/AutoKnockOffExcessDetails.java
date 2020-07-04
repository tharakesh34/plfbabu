package AutoKnockOffExcess;

import java.io.Serializable;
import java.math.BigDecimal;

public class AutoKnockOffExcessDetails implements Serializable {
	private static final long serialVersionUID = 2800538447276766022L;

	private long iD;
	private long knockOffID;
	private long excessID;
	private String code;
	private String description;
	private String executionDays;
	private String finType;
	private String finTypeDesc;
	private String feeTypeCode;
	private String knockOffOrder;
	private int feeOrder;
	private long receiptID;
	private BigDecimal utilizedAmnt = BigDecimal.ZERO;
	private String status;
	private String reason;
	private String finCcy;

	//Exclude Fields.
	private int frhCount;
	private int fmtCount;

	public AutoKnockOffExcessDetails() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long iD) {
		this.iD = iD;
	}

	public long getKnockOffID() {
		return knockOffID;
	}

	public void setKnockOffID(long knockOffID) {
		this.knockOffID = knockOffID;
	}

	public long getExcessID() {
		return excessID;
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecutionDays() {
		return executionDays;
	}

	public void setExecutionDays(String executionDays) {
		this.executionDays = executionDays;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getKnockOffOrder() {
		return knockOffOrder;
	}

	public void setKnockOffOrder(String knockOffOrder) {
		this.knockOffOrder = knockOffOrder;
	}

	public int getFeeOrder() {
		return feeOrder;
	}

	public void setFeeOrder(int feeOrder) {
		this.feeOrder = feeOrder;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public BigDecimal getUtilizedAmnt() {
		return utilizedAmnt;
	}

	public void setUtilizedAmnt(BigDecimal utilizedAmnt) {
		this.utilizedAmnt = utilizedAmnt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public int getFrhCount() {
		return frhCount;
	}

	public void setFrhCount(int frhCount) {
		this.frhCount = frhCount;
	}

	public int getFmtCount() {
		return fmtCount;
	}

	public void setFmtCount(int fmtCount) {
		this.fmtCount = fmtCount;
	}

}