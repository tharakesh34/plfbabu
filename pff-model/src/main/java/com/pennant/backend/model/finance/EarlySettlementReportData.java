/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : EarlySettlement.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified Date
 * : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

/**
 * Model class for the <b>EarlySettlement Report Generation</b>.<br>
 * 
 */
public class EarlySettlementReportData implements java.io.Serializable {

	private static final long serialVersionUID = -3026443763391506067L;

	private String finReference;
	private String finType;
	private String finTypeDesc;
	private String custCIF;
	private String custShrtName;
	private String appDate;
	private String earlySettlementDate;
	private String deptFrom;
	private String finStartDate;
	private String totalTerms;
	private String totalPaidTerms;
	private String totalUnpaidTerms;
	private String totalPaidAmount;
	private String outStandingTotal;
	private String outStandingPft;
	private String discountPerc;
	private String discountAmount;
	private String totCustPaidAmount;

	public EarlySettlementReportData() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getEarlySettlementDate() {
		return earlySettlementDate;
	}

	public void setEarlySettlementDate(String earlySettlementDate) {
		this.earlySettlementDate = earlySettlementDate;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getTotalTerms() {
		return totalTerms;
	}

	public void setTotalTerms(String totalTerms) {
		this.totalTerms = totalTerms;
	}

	public String getTotalPaidTerms() {
		return totalPaidTerms;
	}

	public void setTotalPaidTerms(String totalPaidTerms) {
		this.totalPaidTerms = totalPaidTerms;
	}

	public String getTotalUnpaidTerms() {
		return totalUnpaidTerms;
	}

	public void setTotalUnpaidTerms(String totalUnpaidTerms) {
		this.totalUnpaidTerms = totalUnpaidTerms;
	}

	public String getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(String totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public String getOutStandingTotal() {
		return outStandingTotal;
	}

	public void setOutStandingTotal(String outStandingTotal) {
		this.outStandingTotal = outStandingTotal;
	}

	public String getOutStandingPft() {
		return outStandingPft;
	}

	public void setOutStandingPft(String outStandingPft) {
		this.outStandingPft = outStandingPft;
	}

	public String getDiscountPerc() {
		return discountPerc;
	}

	public void setDiscountPerc(String discountPerc) {
		this.discountPerc = discountPerc;
	}

	public String getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(String discountAmount) {
		this.discountAmount = discountAmount;
	}

	public String getTotCustPaidAmount() {
		return totCustPaidAmount;
	}

	public void setTotCustPaidAmount(String totCustPaidAmount) {
		this.totCustPaidAmount = totCustPaidAmount;
	}

	public String getDeptFrom() {
		return deptFrom;
	}

	public void setDeptFrom(String deptFrom) {
		this.deptFrom = deptFrom;
	}

}
