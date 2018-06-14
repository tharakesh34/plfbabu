package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ReportDetail {
	@XmlElement(name = "FILLER_STRING_1")
	private String filler1;
	@XmlElement(name = "FILLER_STRING_2")
	private String filler2;
	@XmlElement(name = "FILLER_STRING_3")
	private String filler3;
	@XmlElement(name = "FILLER_STRING_4")
	private String filler4;
	@XmlElement(name = "FILLER_STRING_5")
	private String filler5;
	@XmlElement(name = "FILLER_STRING_6")
	private String filler6;
	@XmlElement(name = "FILLER_STRING_7")
	private String filler7;
	@XmlElement(name = "RejectDate")
	private String rejectDate;
	
	public String getFiller1() {
		return filler1;
	}
	public void setFiller1(String filler1) {
		this.filler1 = filler1;
	}
	public String getFiller2() {
		return filler2;
	}
	public void setFiller2(String filler2) {
		this.filler2 = filler2;
	}
	public String getFiller3() {
		return filler3;
	}
	public void setFiller3(String filler3) {
		this.filler3 = filler3;
	}
	public String getFiller4() {
		return filler4;
	}
	public void setFiller4(String filler4) {
		this.filler4 = filler4;
	}
	public String getFiller5() {
		return filler5;
	}
	public void setFiller5(String filler5) {
		this.filler5 = filler5;
	}
	public String getFiller6() {
		return filler6;
	}
	public void setFiller6(String filler6) {
		this.filler6 = filler6;
	}
	public String getFiller7() {
		return filler7;
	}
	public void setFiller7(String filler7) {
		this.filler7 = filler7;
	}
	public String getRejectDate() {
		return rejectDate;
	}
	public void setRejectDate(String rejectDate) {
		this.rejectDate = rejectDate;
	}
	@Override
	public String toString() {
		return "ReportDetail [filler1=" + filler1 + ", filler2=" + filler2
				+ ", filler3=" + filler3 + ", filler4=" + filler4
				+ ", filler5=" + filler5 + ", filler6=" + filler6
				+ ", filler7=" + filler7 + "]";
	}

}
