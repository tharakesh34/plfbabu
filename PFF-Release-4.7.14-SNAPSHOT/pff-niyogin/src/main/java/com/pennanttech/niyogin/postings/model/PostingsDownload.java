package com.pennanttech.niyogin.postings.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PostingsDownload implements Serializable {

	private static final long	serialVersionUID	= -6486440833785900370L;

	private Date				date;
	private long				number;
	private String				type;
	private String				glCode;
	private String				description;
	private String				profit_Center;
	private String				debit_Credit;
	private BigDecimal			amount				= BigDecimal.ZERO;
	private String				narration;

	public PostingsDownload(long number, String type, String glCode, String description,
			String profit_Center, String debit_Credit, BigDecimal amount, String narration) {
		//this.date = postDate;
		this.number = number;
		this.type = type;
		this.glCode = glCode;
		this.description = description;
		this.profit_Center = profit_Center;
		this.debit_Credit = debit_Credit;
		this.amount = amount;
		this.narration = narration;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfit_Center() {
		return profit_Center;
	}

	public void setProfit_Center(String profit_Center) {
		this.profit_Center = profit_Center;
	}

	public String getDebit_Credit() {
		return debit_Credit;
	}

	public void setDebit_Credit(String debit_Credit) {
		this.debit_Credit = debit_Credit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}
}
