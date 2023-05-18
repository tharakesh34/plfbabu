package com.pennant.backend.model.emiholidays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EMIHolidays")
@XmlAccessorType(XmlAccessType.NONE)
public class EMIHolidays {

	@XmlElement
	private boolean planEMIHAlw = false;
	@XmlElement
	private boolean planEMIHAlwInGrace = false;
	@XmlElement
	private String planEMIHMethod = "";
	@XmlElement
	private int planEMIHMaxPerYear = 0;
	@XmlElement
	private int planEMIHMax = 0;
	@XmlElement
	private int planEMIHLockPeriod = 0;
	@XmlElement
	private boolean planEMICpz = false;
	@XmlElement
	private int unPlanEMIHLockPeriod = 0;
	@XmlElement
	private boolean unPlanEMICpz = false;
	@XmlElement
	private boolean reAgeCpz = false;
	@XmlElement
	private int maxUnplannedEmi;
	@XmlElement
	private int maxReAgeHolidays;

	public EMIHolidays() {
		super();
	}

	public boolean isPlanEMIHAlw() {
		return planEMIHAlw;
	}

	public void setPlanEMIHAlw(boolean planEMIHAlw) {
		this.planEMIHAlw = planEMIHAlw;
	}

	public boolean isPlanEMIHAlwInGrace() {
		return planEMIHAlwInGrace;
	}

	public void setPlanEMIHAlwInGrace(boolean planEMIHAlwInGrace) {
		this.planEMIHAlwInGrace = planEMIHAlwInGrace;
	}

	public String getPlanEMIHMethod() {
		return planEMIHMethod;
	}

	public void setPlanEMIHMethod(String planEMIHMethod) {
		this.planEMIHMethod = planEMIHMethod;
	}

	public int getPlanEMIHMaxPerYear() {
		return planEMIHMaxPerYear;
	}

	public void setPlanEMIHMaxPerYear(int planEMIHMaxPerYear) {
		this.planEMIHMaxPerYear = planEMIHMaxPerYear;
	}

	public int getPlanEMIHMax() {
		return planEMIHMax;
	}

	public void setPlanEMIHMax(int planEMIHMax) {
		this.planEMIHMax = planEMIHMax;
	}

	public int getPlanEMIHLockPeriod() {
		return planEMIHLockPeriod;
	}

	public void setPlanEMIHLockPeriod(int planEMIHLockPeriod) {
		this.planEMIHLockPeriod = planEMIHLockPeriod;
	}

	public boolean isPlanEMICpz() {
		return planEMICpz;
	}

	public void setPlanEMICpz(boolean planEMICpz) {
		this.planEMICpz = planEMICpz;
	}

	public int getUnPlanEMIHLockPeriod() {
		return unPlanEMIHLockPeriod;
	}

	public void setUnPlanEMIHLockPeriod(int unPlanEMIHLockPeriod) {
		this.unPlanEMIHLockPeriod = unPlanEMIHLockPeriod;
	}

	public boolean isUnPlanEMICpz() {
		return unPlanEMICpz;
	}

	public void setUnPlanEMICpz(boolean unPlanEMICpz) {
		this.unPlanEMICpz = unPlanEMICpz;
	}

	public boolean isReAgeCpz() {
		return reAgeCpz;
	}

	public void setReAgeCpz(boolean reAgeCpz) {
		this.reAgeCpz = reAgeCpz;
	}

	public int getMaxUnplannedEmi() {
		return maxUnplannedEmi;
	}

	public void setMaxUnplannedEmi(int maxUnplannedEmi) {
		this.maxUnplannedEmi = maxUnplannedEmi;
	}

	public int getMaxReAgeHolidays() {
		return maxReAgeHolidays;
	}

	public void setMaxReAgeHolidays(int maxReAgeHolidays) {
		this.maxReAgeHolidays = maxReAgeHolidays;
	}
}
