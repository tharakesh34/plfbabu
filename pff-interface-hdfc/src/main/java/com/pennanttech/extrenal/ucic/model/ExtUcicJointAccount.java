package com.pennanttech.extrenal.ucic.model;

import java.math.BigDecimal;
import java.util.Date;

public class ExtUcicJointAccount {
	private BigDecimal custId;
	private String custCif;
	private BigDecimal finId;
	private String finreference;
	private String custCoreBankId;
	private String custEmployement;
	private String custName;
	private Date custDob;
	private String custMotherMaidenName;
	private String custCategory;
	private String custType;

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustCoreBankId() {
		return custCoreBankId;
	}

	public void setCustCoreBankId(String custCoreBankId) {
		this.custCoreBankId = custCoreBankId;
	}

	public String getCustEmployement() {
		return custEmployement;
	}

	public void setCustEmployement(String custEmployement) {
		this.custEmployement = custEmployement;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getCustDob() {
		return custDob;
	}

	public void setCustDob(Date custDob) {
		this.custDob = custDob;
	}

	public String getCustMotherMaidenName() {
		return custMotherMaidenName;
	}

	public void setCustMotherMaidenName(String custMotherMaidenName) {
		this.custMotherMaidenName = custMotherMaidenName;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public BigDecimal getCustId() {
		return custId;
	}

	public void setCustId(BigDecimal custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public BigDecimal getFinId() {
		return finId;
	}

	public void setFinId(BigDecimal finId) {
		this.finId = finId;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

}
