package com.pennanttech.ws.model.statement;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class FinStatementRequest {
	
	@XmlElement
	private String	cif;
	@XmlElement
	private String	finActiveStatus;
	@XmlElement
	private String	finReference;
	@XmlElement
	private Date	fromDate;
	@XmlElement
	private Date	toDate;
	@XmlElement
	private int		days;
	@XmlElement
	private String  type;
	@XmlElement
	private String template;


	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getFinActiveStatus() {
		return finActiveStatus;
	}

	public void setFinActiveStatus(String finActiveStatus) {
		this.finActiveStatus = finActiveStatus;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
