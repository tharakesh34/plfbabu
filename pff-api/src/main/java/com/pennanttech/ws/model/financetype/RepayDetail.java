package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finRateType", "lovDescFinRateTypeName", "finIntRate", "finBaseRate", "lovDescFinBaseRateName",
		"finSplRate", "lovDescFinSplRateName", "finMargin", "fInMinRate", "finMaxRate", "finDftIntFrq",
		"finRepayPftOnFrq", "finRpyFrq", "finSchdMthd", "lovDescFinSchdMthdName", "finIsIntCpz", "finCpzFrq",
		"finIsRvwAlw", "finRvwFrq", "finRvwRateApplFor", "finSchCalCodeOnRvw", "finMinTerm", "finMaxTerm",
		"finDftTerms", "fInRepayMethod", "lovDescFInRepayMethodName", "finIsAlwPartialRpy", "finIsAlwDifferment",
		"finMaxDifferment", "alwPlanDeferment", "planDeferCount", "finScheduleOn", "alwEarlyPayMethods",
		"finPftUnChanged", "equalRepayment", "finHistRetension", "finODRpyTries", "finAlwRateChangeAnyDate",
		"finIsAlwEarlyRpy", "finIsAlwEarlySettle", "planEMIHAlw", "planEMIHMethod", "planEMIHMaxPerYear", "planEMIHMax",
		"planEMIHLockPeriod", "planEMICpz", "unPlanEMIHLockPeriod", "unPlanEMICpz", "reAgeCpz", "fddLockPeriod",
		"maxUnplannedEmi", "maxReAgeHolidays", "frequencyDays" })
@XmlAccessorType(XmlAccessType.FIELD)
public class RepayDetail implements Serializable {

	private static final long serialVersionUID = -9071743448729728471L;

	public RepayDetail() {
	    super();
	}

	private String finRateType;

	@XmlElement(name = "finRateTypeDesc")
	private String lovDescFinRateTypeName;

	@XmlElement(name = "finPftRate")
	private BigDecimal finIntRate = BigDecimal.ZERO;
	private String finBaseRate;

	@XmlElement(name = "rpyBaseRateCodeDesc")
	private String lovDescFinBaseRateName;
	private String finSplRate;

	@XmlElement(name = "rpySpecialRateCodeDesc")
	private String lovDescFinSplRateName;
	private BigDecimal finMargin = BigDecimal.ZERO;

	@XmlElement(name = "finMinRate")
	private BigDecimal fInMinRate = BigDecimal.ZERO;
	private BigDecimal finMaxRate = BigDecimal.ZERO;

	@XmlElement(name = "finDftPftFrq")
	private String finDftIntFrq;
	private boolean finRepayPftOnFrq;
	private String finRpyFrq;
	private String finSchdMthd;

	@XmlElement(name = "schdMethodDesc")
	private String lovDescFinSchdMthdName;

	@XmlElement(name = "finIsPftCpz")
	private boolean finIsIntCpz;
	private String finCpzFrq;
	private boolean finIsRvwAlw;
	private String finRvwFrq;
	private String finRvwRateApplFor;
	private String finSchCalCodeOnRvw;
	private int finMinTerm;
	private int finMaxTerm;
	private int finDftTerms;
	private String fInRepayMethod;

	@XmlElement(name = "finRepayMethodDesc")
	private String lovDescFInRepayMethodName;
	private boolean finIsAlwPartialRpy;
	private boolean finIsAlwDifferment;
	private int finMaxDifferment;
	private boolean alwPlanDeferment;
	private int planDeferCount;
	private String finScheduleOn;
	private String alwEarlyPayMethods;
	private boolean finPftUnChanged;
	private boolean equalRepayment;
	private int finHistRetension;
	private int finODRpyTries;
	private boolean finAlwRateChangeAnyDate;
	private boolean finIsAlwEarlyRpy;
	private boolean finIsAlwEarlySettle;

	private boolean planEMIHAlw;
	private String planEMIHMethod;
	private int planEMIHMaxPerYear;
	private int planEMIHMax;
	private int planEMIHLockPeriod;
	private boolean planEMICpz;
	private int unPlanEMIHLockPeriod;
	private boolean unPlanEMICpz;
	private boolean reAgeCpz;
	private int fddLockPeriod;
	private int maxUnplannedEmi;
	private int maxReAgeHolidays;
	private String frequencyDays;

	public String getFinRateType() {
		return finRateType;
	}

	public void setFinRateType(String finRateType) {
		this.finRateType = finRateType;
	}

	public String getLovDescFinRateTypeName() {
		return lovDescFinRateTypeName;
	}

	public void setLovDescFinRateTypeName(String lovDescFinRateTypeName) {
		this.lovDescFinRateTypeName = lovDescFinRateTypeName;
	}

	public BigDecimal getFinIntRate() {
		return finIntRate;
	}

	public void setFinIntRate(BigDecimal finIntRate) {
		this.finIntRate = finIntRate;
	}

	public String getFinBaseRate() {
		return finBaseRate;
	}

	public void setFinBaseRate(String finBaseRate) {
		this.finBaseRate = finBaseRate;
	}

	public String getLovDescFinBaseRateName() {
		return lovDescFinBaseRateName;
	}

	public void setLovDescFinBaseRateName(String lovDescFinBaseRateName) {
		this.lovDescFinBaseRateName = lovDescFinBaseRateName;
	}

	public String getFinSplRate() {
		return finSplRate;
	}

	public void setFinSplRate(String finSplRate) {
		this.finSplRate = finSplRate;
	}

	public String getLovDescFinSplRateName() {
		return lovDescFinSplRateName;
	}

	public void setLovDescFinSplRateName(String lovDescFinSplRateName) {
		this.lovDescFinSplRateName = lovDescFinSplRateName;
	}

	public BigDecimal getFinMargin() {
		return finMargin;
	}

	public void setFinMargin(BigDecimal finMargin) {
		this.finMargin = finMargin;
	}

	public BigDecimal getfInMinRate() {
		return fInMinRate;
	}

	public void setfInMinRate(BigDecimal fInMinRate) {
		this.fInMinRate = fInMinRate;
	}

	public BigDecimal getFinMaxRate() {
		return finMaxRate;
	}

	public void setFinMaxRate(BigDecimal finMaxRate) {
		this.finMaxRate = finMaxRate;
	}

	public String getFinDftIntFrq() {
		return finDftIntFrq;
	}

	public void setFinDftIntFrq(String finDftIntFrq) {
		this.finDftIntFrq = finDftIntFrq;
	}

	public boolean isFinRepayPftOnFrq() {
		return finRepayPftOnFrq;
	}

	public void setFinRepayPftOnFrq(boolean finRepayPftOnFrq) {
		this.finRepayPftOnFrq = finRepayPftOnFrq;
	}

	public String getFinRpyFrq() {
		return finRpyFrq;
	}

	public void setFinRpyFrq(String finRpyFrq) {
		this.finRpyFrq = finRpyFrq;
	}

	public String getFinSchdMthd() {
		return finSchdMthd;
	}

	public void setFinSchdMthd(String finSchdMthd) {
		this.finSchdMthd = finSchdMthd;
	}

	public String getLovDescFinSchdMthdName() {
		return lovDescFinSchdMthdName;
	}

	public void setLovDescFinSchdMthdName(String lovDescFinSchdMthdName) {
		this.lovDescFinSchdMthdName = lovDescFinSchdMthdName;
	}

	public boolean isFinIsIntCpz() {
		return finIsIntCpz;
	}

	public void setFinIsIntCpz(boolean finIsIntCpz) {
		this.finIsIntCpz = finIsIntCpz;
	}

	public String getFinCpzFrq() {
		return finCpzFrq;
	}

	public void setFinCpzFrq(String finCpzFrq) {
		this.finCpzFrq = finCpzFrq;
	}

	public boolean isFinIsRvwAlw() {
		return finIsRvwAlw;
	}

	public void setFinIsRvwAlw(boolean finIsRvwAlw) {
		this.finIsRvwAlw = finIsRvwAlw;
	}

	public String getFinRvwFrq() {
		return finRvwFrq;
	}

	public void setFinRvwFrq(String finRvwFrq) {
		this.finRvwFrq = finRvwFrq;
	}

	public String getFinRvwRateApplFor() {
		return finRvwRateApplFor;
	}

	public void setFinRvwRateApplFor(String finRvwRateApplFor) {
		this.finRvwRateApplFor = finRvwRateApplFor;
	}

	public String getFinSchCalCodeOnRvw() {
		return finSchCalCodeOnRvw;
	}

	public void setFinSchCalCodeOnRvw(String finSchCalCodeOnRvw) {
		this.finSchCalCodeOnRvw = finSchCalCodeOnRvw;
	}

	public int getFinMinTerm() {
		return finMinTerm;
	}

	public void setFinMinTerm(int finMinTerm) {
		this.finMinTerm = finMinTerm;
	}

	public int getFinMaxTerm() {
		return finMaxTerm;
	}

	public void setFinMaxTerm(int finMaxTerm) {
		this.finMaxTerm = finMaxTerm;
	}

	public int getFinDftTerms() {
		return finDftTerms;
	}

	public void setFinDftTerms(int finDftTerms) {
		this.finDftTerms = finDftTerms;
	}

	public String getfInRepayMethod() {
		return fInRepayMethod;
	}

	public void setfInRepayMethod(String fInRepayMethod) {
		this.fInRepayMethod = fInRepayMethod;
	}

	public String getLovDescFInRepayMethodName() {
		return lovDescFInRepayMethodName;
	}

	public void setLovDescFInRepayMethodName(String lovDescFInRepayMethodName) {
		this.lovDescFInRepayMethodName = lovDescFInRepayMethodName;
	}

	public boolean isFinIsAlwPartialRpy() {
		return finIsAlwPartialRpy;
	}

	public void setFinIsAlwPartialRpy(boolean finIsAlwPartialRpy) {
		this.finIsAlwPartialRpy = finIsAlwPartialRpy;
	}

	public boolean isFinIsAlwDifferment() {
		return finIsAlwDifferment;
	}

	public void setFinIsAlwDifferment(boolean finIsAlwDifferment) {
		this.finIsAlwDifferment = finIsAlwDifferment;
	}

	public int getFinMaxDifferment() {
		return finMaxDifferment;
	}

	public void setFinMaxDifferment(int finMaxDifferment) {
		this.finMaxDifferment = finMaxDifferment;
	}

	public boolean isAlwPlanDeferment() {
		return alwPlanDeferment;
	}

	public void setAlwPlanDeferment(boolean alwPlanDeferment) {
		this.alwPlanDeferment = alwPlanDeferment;
	}

	public int getPlanDeferCount() {
		return planDeferCount;
	}

	public void setPlanDeferCount(int planDeferCount) {
		this.planDeferCount = planDeferCount;
	}

	public String getFinScheduleOn() {
		return finScheduleOn;
	}

	public void setFinScheduleOn(String finScheduleOn) {
		this.finScheduleOn = finScheduleOn;
	}

	public String getAlwEarlyPayMethods() {
		return alwEarlyPayMethods;
	}

	public void setAlwEarlyPayMethods(String alwEarlyPayMethods) {
		this.alwEarlyPayMethods = alwEarlyPayMethods;
	}

	public boolean isFinPftUnChanged() {
		return finPftUnChanged;
	}

	public void setFinPftUnChanged(boolean finPftUnChanged) {
		this.finPftUnChanged = finPftUnChanged;
	}

	public boolean isEqualRepayment() {
		return equalRepayment;
	}

	public void setEqualRepayment(boolean equalRepayment) {
		this.equalRepayment = equalRepayment;
	}

	public int getFinHistRetension() {
		return finHistRetension;
	}

	public void setFinHistRetension(int finHistRetension) {
		this.finHistRetension = finHistRetension;
	}

	public int getFinODRpyTries() {
		return finODRpyTries;
	}

	public void setFinODRpyTries(int finODRpyTries) {
		this.finODRpyTries = finODRpyTries;
	}

	public boolean isFinAlwRateChangeAnyDate() {
		return finAlwRateChangeAnyDate;
	}

	public void setFinAlwRateChangeAnyDate(boolean finAlwRateChangeAnyDate) {
		this.finAlwRateChangeAnyDate = finAlwRateChangeAnyDate;
	}

	public boolean isFinIsAlwEarlyRpy() {
		return finIsAlwEarlyRpy;
	}

	public void setFinIsAlwEarlyRpy(boolean finIsAlwEarlyRpy) {
		this.finIsAlwEarlyRpy = finIsAlwEarlyRpy;
	}

	public boolean isFinIsAlwEarlySettle() {
		return finIsAlwEarlySettle;
	}

	public void setFinIsAlwEarlySettle(boolean finIsAlwEarlySettle) {
		this.finIsAlwEarlySettle = finIsAlwEarlySettle;
	}

	public boolean isPlanEMIHAlw() {
		return planEMIHAlw;
	}

	public void setPlanEMIHAlw(boolean planEMIHAlw) {
		this.planEMIHAlw = planEMIHAlw;
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

	public int getFddLockPeriod() {
		return fddLockPeriod;
	}

	public void setFddLockPeriod(int fddLockPeriod) {
		this.fddLockPeriod = fddLockPeriod;
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

	public boolean isPlanEMICpz() {
		return planEMICpz;
	}

	public void setPlanEMICpz(boolean planEMICpz) {
		this.planEMICpz = planEMICpz;
	}

	public String getPlanEMIHMethod() {
		return planEMIHMethod;
	}

	public void setPlanEMIHMethod(String planEMIHMethod) {
		this.planEMIHMethod = planEMIHMethod;
	}

	public String getFrequencyDays() {
		return frequencyDays;
	}

	public void setFrequencyDays(String frequencyDays) {
		this.frequencyDays = frequencyDays;
	}
}