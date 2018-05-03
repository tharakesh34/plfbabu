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
import java.util.Date;

/**
 * Model class for the <b>CustomerEmploymentDetail table</b>.<br>
 * 
 */
public class InterfaceCustomerEmploymentDetail implements Serializable {


	private static final long serialVersionUID = -6181309908843301343L;
	
	private long custID;
	private long custEmpName;
	private String lovDesccustEmpName;
	private Date custEmpFrom;
	private Date custEmpTo;
	private boolean currentEmployer;
	private String custEmpDesg;
	private String lovDescCustEmpDesgName;
	private String custEmpDept;
	private String lovDescCustEmpDeptName;
	private String custEmpType;
	private String lovDescCustEmpTypeName;
	private String lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	public InterfaceCustomerEmploymentDetail() {

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

	public long getCustEmpName() {
		return custEmpName;
	}

	public void setCustEmpName(long custEmpName) {
		this.custEmpName = custEmpName;
	}

	public String getLovDesccustEmpName() {
		return lovDesccustEmpName;
	}

	public void setLovDesccustEmpName(String lovDesccustEmpName) {
		this.lovDesccustEmpName = lovDesccustEmpName;
	}

	public Date getCustEmpFrom() {
		return custEmpFrom;
	}

	public void setCustEmpFrom(Date custEmpFrom) {
		this.custEmpFrom = custEmpFrom;
	}

	public Date getCustEmpTo() {
		return custEmpTo;
	}

	public void setCustEmpTo(Date custEmpTo) {
		this.custEmpTo = custEmpTo;
	}

	public boolean isCurrentEmployer() {
		return currentEmployer;
	}

	public void setCurrentEmployer(boolean currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public String getCustEmpDesg() {
		return custEmpDesg;
	}

	public void setCustEmpDesg(String custEmpDesg) {
		this.custEmpDesg = custEmpDesg;
	}

	public String getLovDescCustEmpDesgName() {
		return lovDescCustEmpDesgName;
	}

	public void setLovDescCustEmpDesgName(String lovDescCustEmpDesgName) {
		this.lovDescCustEmpDesgName = lovDescCustEmpDesgName;
	}

	public String getCustEmpDept() {
		return custEmpDept;
	}

	public void setCustEmpDept(String custEmpDept) {
		this.custEmpDept = custEmpDept;
	}

	public String getLovDescCustEmpDeptName() {
		return lovDescCustEmpDeptName;
	}

	public void setLovDescCustEmpDeptName(String lovDescCustEmpDeptName) {
		this.lovDescCustEmpDeptName = lovDescCustEmpDeptName;
	}

	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public String getLovDescCustEmpTypeName() {
		return lovDescCustEmpTypeName;
	}

	public void setLovDescCustEmpTypeName(String lovDescCustEmpTypeName) {
		this.lovDescCustEmpTypeName = lovDescCustEmpTypeName;
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
