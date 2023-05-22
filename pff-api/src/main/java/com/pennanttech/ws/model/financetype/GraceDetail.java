package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finGrcRateType", "lovDescFinGrcRateTypeName", "finGrcIntRate", "finGrcBaseRate",
		"lovDescFinGrcBaseRateName", "finGrcSplRate", "lovDescFinGrcSplRateName", "finGrcMargin", "fInGrcMinRate",
		"finGrcMaxRate", "finGrcDftIntFrq", "finIsAlwGrcRepay", "finGrcSchdMthd", "finGrcIsIntCpz", "finGrcCpzFrq",
		"finGrcIsRvwAlw", "finGrcRvwFrq", "finGrcRvwRateApplFor", "finIsIntCpzAtGrcEnd" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GraceDetail implements Serializable {

	private static final long serialVersionUID = 4195934248519928784L;

	public GraceDetail() {
	    super();
	}

	private String finGrcRateType;

	@XmlElement(name = "grcRateTypeDesc")
	private String lovDescFinGrcRateTypeName;

	@XmlElement(name = "finGrcPftRate")
	private BigDecimal finGrcIntRate = BigDecimal.ZERO;
	private String finGrcBaseRate;

	@XmlElement(name = "grcBaseRateCodeDesc")
	private String lovDescFinGrcBaseRateName;
	private String finGrcSplRate;

	@XmlElement(name = "grcSpecialRateCodeDesc")
	private String lovDescFinGrcSplRateName;
	private BigDecimal finGrcMargin = BigDecimal.ZERO;
	private BigDecimal fInGrcMinRate = BigDecimal.ZERO;
	private BigDecimal finGrcMaxRate = BigDecimal.ZERO;

	@XmlElement(name = "finGrcDftPftFrq")
	private String finGrcDftIntFrq;
	private boolean finIsAlwGrcRepay;
	private String finGrcSchdMthd;

	@XmlElement(name = "finGrcIsPftCpz")
	private boolean finGrcIsIntCpz;
	private String finGrcCpzFrq;
	private boolean finGrcIsRvwAlw;
	private String finGrcRvwFrq;
	private String finGrcRvwRateApplFor;

	@XmlElement(name = "finIsPftCpzAtGrcEnd")
	private boolean finIsIntCpzAtGrcEnd;

	// private String schdMethodDesc;//Not available in PFF: FinanceType

	public String getFinGrcRateType() {
		return finGrcRateType;
	}

	public void setFinGrcRateType(String finGrcRateType) {
		this.finGrcRateType = finGrcRateType;
	}

	public String getLovDescFinGrcRateTypeName() {
		return lovDescFinGrcRateTypeName;
	}

	public void setLovDescFinGrcRateTypeName(String lovDescFinGrcRateTypeName) {
		this.lovDescFinGrcRateTypeName = lovDescFinGrcRateTypeName;
	}

	public BigDecimal getFinGrcIntRate() {
		return finGrcIntRate;
	}

	public void setFinGrcIntRate(BigDecimal finGrcIntRate) {
		this.finGrcIntRate = finGrcIntRate;
	}

	public String getFinGrcBaseRate() {
		return finGrcBaseRate;
	}

	public void setFinGrcBaseRate(String finGrcBaseRate) {
		this.finGrcBaseRate = finGrcBaseRate;
	}

	public String getLovDescFinGrcBaseRateName() {
		return lovDescFinGrcBaseRateName;
	}

	public void setLovDescFinGrcBaseRateName(String lovDescFinGrcBaseRateName) {
		this.lovDescFinGrcBaseRateName = lovDescFinGrcBaseRateName;
	}

	public String getFinGrcSplRate() {
		return finGrcSplRate;
	}

	public void setFinGrcSplRate(String finGrcSplRate) {
		this.finGrcSplRate = finGrcSplRate;
	}

	public String getLovDescFinGrcSplRateName() {
		return lovDescFinGrcSplRateName;
	}

	public void setLovDescFinGrcSplRateName(String lovDescFinGrcSplRateName) {
		this.lovDescFinGrcSplRateName = lovDescFinGrcSplRateName;
	}

	public BigDecimal getFinGrcMargin() {
		return finGrcMargin;
	}

	public void setFinGrcMargin(BigDecimal finGrcMargin) {
		this.finGrcMargin = finGrcMargin;
	}

	public BigDecimal getfInGrcMinRate() {
		return fInGrcMinRate;
	}

	public void setfInGrcMinRate(BigDecimal fInGrcMinRate) {
		this.fInGrcMinRate = fInGrcMinRate;
	}

	public BigDecimal getFinGrcMaxRate() {
		return finGrcMaxRate;
	}

	public void setFinGrcMaxRate(BigDecimal finGrcMaxRate) {
		this.finGrcMaxRate = finGrcMaxRate;
	}

	public String getFinGrcDftIntFrq() {
		return finGrcDftIntFrq;
	}

	public void setFinGrcDftIntFrq(String finGrcDftIntFrq) {
		this.finGrcDftIntFrq = finGrcDftIntFrq;
	}

	public boolean isFinIsAlwGrcRepay() {
		return finIsAlwGrcRepay;
	}

	public void setFinIsAlwGrcRepay(boolean finIsAlwGrcRepay) {
		this.finIsAlwGrcRepay = finIsAlwGrcRepay;
	}

	public String getFinGrcSchdMthd() {
		return finGrcSchdMthd;
	}

	public void setFinGrcSchdMthd(String finGrcSchdMthd) {
		this.finGrcSchdMthd = finGrcSchdMthd;
	}

	public boolean isFinGrcIsIntCpz() {
		return finGrcIsIntCpz;
	}

	public void setFinGrcIsIntCpz(boolean finGrcIsIntCpz) {
		this.finGrcIsIntCpz = finGrcIsIntCpz;
	}

	public String getFinGrcCpzFrq() {
		return finGrcCpzFrq;
	}

	public void setFinGrcCpzFrq(String finGrcCpzFrq) {
		this.finGrcCpzFrq = finGrcCpzFrq;
	}

	public boolean isFinGrcIsRvwAlw() {
		return finGrcIsRvwAlw;
	}

	public void setFinGrcIsRvwAlw(boolean finGrcIsRvwAlw) {
		this.finGrcIsRvwAlw = finGrcIsRvwAlw;
	}

	public String getFinGrcRvwFrq() {
		return finGrcRvwFrq;
	}

	public void setFinGrcRvwFrq(String finGrcRvwFrq) {
		this.finGrcRvwFrq = finGrcRvwFrq;
	}

	public String getFinGrcRvwRateApplFor() {
		return finGrcRvwRateApplFor;
	}

	public void setFinGrcRvwRateApplFor(String finGrcRvwRateApplFor) {
		this.finGrcRvwRateApplFor = finGrcRvwRateApplFor;
	}

	public boolean isFinIsIntCpzAtGrcEnd() {
		return finIsIntCpzAtGrcEnd;
	}

	public void setFinIsIntCpzAtGrcEnd(boolean finIsIntCpzAtGrcEnd) {
		this.finIsIntCpzAtGrcEnd = finIsIntCpzAtGrcEnd;
	}
}
