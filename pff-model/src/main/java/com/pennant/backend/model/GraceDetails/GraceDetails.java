package com.pennant.backend.model.gracedetails;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "grace")
@XmlAccessorType(XmlAccessType.NONE)
public class GraceDetails {

	private Long finID;
	private String finReference;
	@XmlElement
	private Boolean allowGrcPeriod;
	@XmlElement(name = "grcTerms")
	private Integer graceTerms;
	@XmlElement
	private Date grcPeriodEndDate;
	@XmlElement
	private String grcRateBasis;
	@XmlElement
	private BigDecimal grcPftRate;
	@XmlElement(name = "grcBaseRate")
	private String graceBaseRate;
	@XmlElement(name = "grcSpecialRate")
	private String graceSpecialRate;
	@XmlElement
	private String grcProfitDaysBasis;
	@XmlElement
	private Date nextGrcPftDate;
	@XmlElement
	private String grcPftRvwFrq;
	@XmlElement
	private Date nextGrcPftRvwDate;
	@XmlElement
	private Boolean allowGrcCpz;
	@XmlElement
	private String grcCpzFrq;
	@XmlElement
	private Date nextGrcCpzDate;
	@XmlElement
	private Boolean allowGrcRepay;
	@XmlElement
	private String grcSchdMthd;
	@XmlElement
	private BigDecimal grcMinRate;
	@XmlElement
	private BigDecimal grcMaxRate;
	@XmlElement
	private BigDecimal grcMaxAmount;
	@XmlElement
	private Integer numberOfTerms = 0;
	@XmlElement
	private String grcAdvType;
	@XmlElement(name = "grcadvEMITerms")
	private Integer grcAdvTerms;
	@XmlElement
	private Boolean alwGrcAdj;
	@XmlElement
	private Boolean endGrcPeriodAftrFullDisb;
	@XmlElement
	private Boolean autoIncGrcEndDate;
	@XmlElement
	private Integer noOfGrcSteps = 0;
	@XmlElement
	private Date grcStartDate;

	public GraceDetails() {
		super();
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Boolean getAllowGrcPeriod() {
		return allowGrcPeriod;
	}

	public void setAllowGrcPeriod(Boolean allowGrcPeriod) {
		this.allowGrcPeriod = allowGrcPeriod;
	}

	public Integer getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(Integer graceTerms) {
		this.graceTerms = graceTerms;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public String getGrcRateBasis() {
		return grcRateBasis;
	}

	public void setGrcRateBasis(String grcRateBasis) {
		this.grcRateBasis = grcRateBasis;
	}

	public BigDecimal getGrcPftRate() {
		return grcPftRate;
	}

	public void setGrcPftRate(BigDecimal grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	public String getGraceBaseRate() {
		return graceBaseRate;
	}

	public void setGraceBaseRate(String graceBaseRate) {
		this.graceBaseRate = graceBaseRate;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public String getGrcProfitDaysBasis() {
		return grcProfitDaysBasis;
	}

	public void setGrcProfitDaysBasis(String grcProfitDaysBasis) {
		this.grcProfitDaysBasis = grcProfitDaysBasis;
	}

	public Date getNextGrcPftDate() {
		return nextGrcPftDate;
	}

	public void setNextGrcPftDate(Date nextGrcPftDate) {
		this.nextGrcPftDate = nextGrcPftDate;
	}

	public String getGrcPftRvwFrq() {
		return grcPftRvwFrq;
	}

	public void setGrcPftRvwFrq(String grcPftRvwFrq) {
		this.grcPftRvwFrq = grcPftRvwFrq;
	}

	public Date getNextGrcPftRvwDate() {
		return nextGrcPftRvwDate;
	}

	public void setNextGrcPftRvwDate(Date nextGrcPftRvwDate) {
		this.nextGrcPftRvwDate = nextGrcPftRvwDate;
	}

	public Boolean isAllowGrcCpz() {
		return allowGrcCpz;
	}

	public void setAllowGrcCpz(Boolean allowGrcCpz) {
		this.allowGrcCpz = allowGrcCpz;
	}

	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}

	public void setGrcCpzFrq(String grcCpzFrq) {
		this.grcCpzFrq = grcCpzFrq;
	}

	public Date getNextGrcCpzDate() {
		return nextGrcCpzDate;
	}

	public void setNextGrcCpzDate(Date nextGrcCpzDate) {
		this.nextGrcCpzDate = nextGrcCpzDate;
	}

	public Boolean isAllowGrcRepay() {
		return allowGrcRepay;
	}

	public void setAllowGrcRepay(Boolean allowGrcRepay) {
		this.allowGrcRepay = allowGrcRepay;
	}

	public String getGrcSchdMthd() {
		return grcSchdMthd;
	}

	public void setGrcSchdMthd(String grcSchdMthd) {
		this.grcSchdMthd = grcSchdMthd;
	}

	public BigDecimal getGrcMinRate() {
		return grcMinRate;
	}

	public void setGrcMinRate(BigDecimal grcMinRate) {
		this.grcMinRate = grcMinRate;
	}

	public BigDecimal getGrcMaxRate() {
		return grcMaxRate;
	}

	public void setGrcMaxRate(BigDecimal grcMaxRate) {
		this.grcMaxRate = grcMaxRate;
	}

	public BigDecimal getGrcMaxAmount() {
		return grcMaxAmount;
	}

	public void setGrcMaxAmount(BigDecimal grcMaxAmount) {
		this.grcMaxAmount = grcMaxAmount;
	}

	public Integer getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(Integer numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getGrcAdvType() {
		return grcAdvType;
	}

	public void setGrcAdvType(String grcAdvType) {
		this.grcAdvType = grcAdvType;
	}

	public Integer getGrcAdvTerms() {
		return grcAdvTerms;
	}

	public void setGrcAdvTerms(Integer grcAdvTerms) {
		this.grcAdvTerms = grcAdvTerms;
	}

	public Boolean isAlwGrcAdj() {
		return alwGrcAdj;
	}

	public void setAlwGrcAdj(Boolean alwGrcAdj) {
		this.alwGrcAdj = alwGrcAdj;
	}

	public Boolean isEndGrcPeriodAftrFullDisb() {
		return endGrcPeriodAftrFullDisb;
	}

	public void setEndGrcPeriodAftrFullDisb(Boolean endGrcPeriodAftrFullDisb) {
		this.endGrcPeriodAftrFullDisb = endGrcPeriodAftrFullDisb;
	}

	public Boolean isAutoIncGrcEndDate() {
		return autoIncGrcEndDate;
	}

	public void setAutoIncGrcEndDate(Boolean autoIncGrcEndDate) {
		this.autoIncGrcEndDate = autoIncGrcEndDate;
	}

	public Integer getNoOfGrcSteps() {
		return noOfGrcSteps;
	}

	public void setNoOfGrcSteps(Integer noOfGrcSteps) {
		this.noOfGrcSteps = noOfGrcSteps;
	}

	public Date getGrcStartDate() {
		return grcStartDate;
	}

	public void setGrcStartDate(Date grcStartDate) {
		this.grcStartDate = grcStartDate;
	}

}
