package com.pennant.backend.model.finance;

public class RestructureType {

	private long id;
	private String rstTypeCode;
	private String rstTypeDesc;
	private int maxEmiHoliday = 0;
	private int maxPriHoliday = 0;
	private int maxEmiTerm = 0;
	private int maxTotTerm = 0;
	private boolean alwStep = false;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRstTypeCode() {
		return rstTypeCode;
	}

	public void setRstTypeCode(String rstTypeCode) {
		this.rstTypeCode = rstTypeCode;
	}

	public String getRstTypeDesc() {
		return rstTypeDesc;
	}

	public void setRstTypeDesc(String rstTypeDesc) {
		this.rstTypeDesc = rstTypeDesc;
	}

	public int getMaxEmiHoliday() {
		return maxEmiHoliday;
	}

	public void setMaxEmiHoliday(int maxEmiHoliday) {
		this.maxEmiHoliday = maxEmiHoliday;
	}

	public int getMaxPriHoliday() {
		return maxPriHoliday;
	}

	public void setMaxPriHoliday(int maxPriHoliday) {
		this.maxPriHoliday = maxPriHoliday;
	}

	public int getMaxEmiTerm() {
		return maxEmiTerm;
	}

	public void setMaxEmiTerm(int maxEmiTerm) {
		this.maxEmiTerm = maxEmiTerm;
	}

	public int getMaxTotTerm() {
		return maxTotTerm;
	}

	public void setMaxTotTerm(int maxTotTerm) {
		this.maxTotTerm = maxTotTerm;
	}

	public boolean isAlwStep() {
		return alwStep;
	}

	public void setAlwStep(boolean alwStep) {
		this.alwStep = alwStep;
	}
}
