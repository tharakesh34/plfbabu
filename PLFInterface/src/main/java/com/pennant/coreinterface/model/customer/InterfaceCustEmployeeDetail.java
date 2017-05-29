/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>CustomerEmploymentDetail table</b>.<br>
 * 
 */
public class InterfaceCustEmployeeDetail implements Serializable {

	private static final long serialVersionUID = -3608304442460245461L;
	
	private long custID;
	private String empStatus;
	private String lovDescEmpStatus;
	private String empSector;
	private String lovDescEmpSector;
	private String profession;
	private String lovDescProfession;
	private long empName;
	private String lovDescEmpName;
	private String empNameForOthers;
	private String empDesg;
	private String lovDescEmpDesg;
	private String empDept;
	private String lovDescEmpDept;
	private Date empFrom;
	private BigDecimal monthlyIncome = BigDecimal.ZERO;
	private String otherIncome;
	private String lovDescOtherIncome;
	private BigDecimal additionalIncome = BigDecimal.ZERO;

	private String lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	
	public InterfaceCustEmployeeDetail() {
		
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getLovDescEmpStatus() {
		return lovDescEmpStatus;
	}

	public void setLovDescEmpStatus(String lovDescEmpStatus) {
		this.lovDescEmpStatus = lovDescEmpStatus;
	}

	public String getEmpSector() {
		return empSector;
	}

	public void setEmpSector(String empSector) {
		this.empSector = empSector;
	}

	public String getLovDescEmpSector() {
		return lovDescEmpSector;
	}

	public void setLovDescEmpSector(String lovDescEmpSector) {
		this.lovDescEmpSector = lovDescEmpSector;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getLovDescProfession() {
		return lovDescProfession;
	}

	public void setLovDescProfession(String lovDescProfession) {
		this.lovDescProfession = lovDescProfession;
	}

	public long getEmpName() {
		return empName;
	}

	public void setEmpName(long empName) {
		this.empName = empName;
	}

	public String getLovDescEmpName() {
		return lovDescEmpName;
	}

	public void setLovDescEmpName(String lovDescEmpName) {
		this.lovDescEmpName = lovDescEmpName;
	}

	public String getEmpNameForOthers() {
		return empNameForOthers;
	}

	public void setEmpNameForOthers(String empNameForOthers) {
		this.empNameForOthers = empNameForOthers;
	}

	public String getEmpDesg() {
		return empDesg;
	}

	public void setEmpDesg(String empDesg) {
		this.empDesg = empDesg;
	}

	public String getLovDescEmpDesg() {
		return lovDescEmpDesg;
	}

	public void setLovDescEmpDesg(String lovDescEmpDesg) {
		this.lovDescEmpDesg = lovDescEmpDesg;
	}

	public String getEmpDept() {
		return empDept;
	}

	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}

	public String getLovDescEmpDept() {
		return lovDescEmpDept;
	}

	public void setLovDescEmpDept(String lovDescEmpDept) {
		this.lovDescEmpDept = lovDescEmpDept;
	}

	public Date getEmpFrom() {
		return empFrom;
	}

	public void setEmpFrom(Date empFrom) {
		this.empFrom = empFrom;
	}

	public BigDecimal getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(BigDecimal monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public String getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(String otherIncome) {
		this.otherIncome = otherIncome;
	}

	public String getLovDescOtherIncome() {
		return lovDescOtherIncome;
	}

	public void setLovDescOtherIncome(String lovDescOtherIncome) {
		this.lovDescOtherIncome = lovDescOtherIncome;
	}

	public BigDecimal getAdditionalIncome() {
		return additionalIncome;
	}

	public void setAdditionalIncome(BigDecimal additionalIncome) {
		this.additionalIncome = additionalIncome;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}
}
