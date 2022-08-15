package com.pennant.backend.model.finance.manual.schedule;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ManualScheduleDetail implements Serializable {
	private static final long serialVersionUID = -7500733081908555881L;

	private long id = Long.MIN_VALUE;;
	private long headerId;
	private Date schDate;
	private boolean pftOnSchDate;
	private boolean rvwOnSchDate;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private String status;
	private String reason;

	public ManualScheduleDetail() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public boolean isPftOnSchDate() {
		return pftOnSchDate;
	}

	public void setPftOnSchDate(boolean pftOnSchDate) {
		this.pftOnSchDate = pftOnSchDate;
	}

	public boolean isRvwOnSchDate() {
		return rvwOnSchDate;
	}

	public void setRvwOnSchDate(boolean rvwOnSchDate) {
		this.rvwOnSchDate = rvwOnSchDate;
	}

	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}

	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
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

}