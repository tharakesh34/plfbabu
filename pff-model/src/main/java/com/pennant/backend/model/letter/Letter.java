package com.pennant.backend.model.letter;

import java.util.Date;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.pff.noc.model.GenerateLetter;

public class Letter extends GenerateLetter {
	private static final long serialVersionUID = 1L;

	private int sequence;
	private Date appDate;
	private String letterName;
	private String letterDesc;
	private String letterType;
	private String letterMode;
	private int saveFormat;
	private byte[] content;
	private String csdCode;

	private long custID;
	private String custCif;
	private String custCoreBank;
	private String custSalutationCode;
	private String custShrtName;
	private String custFullName;
	private String finType;
	private String finTypeDesc;
	private String finStartDate;
	private String strAppDate;

	private MailTemplate mailTemplate;

	public Letter() {
		super();
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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

	public String getLetterMode() {
		return letterMode;
	}

	public void setLetterMode(String letterMode) {
		this.letterMode = letterMode;
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

	public String getCsdCode() {
		return csdCode;
	}

	public void setCsdCode(String csdCode) {
		this.csdCode = csdCode;
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

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
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

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getStrAppDate() {
		return strAppDate;
	}

	public void setStrAppDate(String strAppDate) {
		this.strAppDate = strAppDate;
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

}
