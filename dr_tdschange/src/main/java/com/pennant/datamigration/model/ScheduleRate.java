package com.pennant.datamigration.model;

import java.math.BigDecimal;
import java.util.Date;

public class ScheduleRate {

	private String finReference = "";
	private long finID;
	private Date schDate;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private String baseRate = "";
	private String splRate = "";
	private BigDecimal mrgRate = BigDecimal.ZERO;
	private BigDecimal actRate = BigDecimal.ZERO;
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public Date getSchDate() {
		return schDate;
	}
	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}
	public BigDecimal getCalculatedRate() {
		return calculatedRate;
	}
	public void setCalculatedRate(BigDecimal calculatedRate) {
		this.calculatedRate = calculatedRate;
	}
	public String getBaseRate() {
		return baseRate;
	}
	public void setBaseRate(String baserate) {
		this.baseRate = baserate;
	}
	public String getSplRate() {
		return splRate;
	}
	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}
	public BigDecimal getMrgRate() {
		return mrgRate;
	}
	public void setMrgRate(BigDecimal mrgRate) {
		this.mrgRate = mrgRate;
	}
	public BigDecimal getActRate() {
		return actRate;
	}
	public void setActRate(BigDecimal actRate) {
		this.actRate = actRate;
	}
	public long getFinID() {
		return finID;
	}
	public void setFinID(long finID) {
		this.finID = finID;
	}
	
}
