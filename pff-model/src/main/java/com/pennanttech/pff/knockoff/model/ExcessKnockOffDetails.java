/**
 * 
 */
package com.pennanttech.pff.knockoff.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author saikiran.n
 *
 */
public class ExcessKnockOffDetails implements Serializable {
	private static final long serialVersionUID = 2800538447276766022L;

	private long id;
	private long knockOffID;
	private long excessID;
	private String code;
	private String executionDays;
	private String finType;
	private String feeTypeCode;
	private String knockOffOrder;
	private int feeOrder;
	private String finCcy;
	private BigDecimal utilizedAmnt = BigDecimal.ZERO;

	public ExcessKnockOffDetails() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getUtilizedAmnt() {
		return utilizedAmnt;
	}

	public void setUtilizedAmnt(BigDecimal utilizedAmnt) {
		this.utilizedAmnt = utilizedAmnt;
	}

}
