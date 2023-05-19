package com.pennant.backend.model.letter;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.pff.noc.model.GenerateLetter;

public class Letter extends GenerateLetter {
	private static final long serialVersionUID = 1L;

	private long custID;
	private String custCif;
	private String custCoreBank;
	private String custName;
	private String finType;
	private String finTypeDesc;
	private String finReference;
	private BigDecimal osPriBal = BigDecimal.ZERO;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private Date appDate;
	private String letterName;
	private String letterDesc;
	private String letterType;
	private String letterMode;
	private int saveFormat;
	private byte[] content;

	public Letter() {
		super();
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getOsPriBal() {
		return osPriBal;
	}

	public void setOsPriBal(BigDecimal osPriBal) {
		this.osPriBal = osPriBal;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getLetterName() {
		return letterName;
	}

	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	public String getLetterDesc() {
		return letterDesc;
	}

	public void setLetterDesc(String letterDesc) {
		this.letterDesc = letterDesc;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public int getSaveFormat() {
		return saveFormat;
	}

	public void setSaveFormat(int saveFormat) {
		this.saveFormat = saveFormat;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getLetterMode() {
		return letterMode;
	}

	public void setLetterMode(String letterMode) {
		this.letterMode = letterMode;
	}
}
