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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  CustomerCalData.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 *//*
package com.pennant.app.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomerCalData implements Serializable {

	private static final long serialVersionUID = 4418749343428149504L;

	private String finType;
	private String custCIF;
	private long custID;

	private int custLiveFinCount;
	private BigDecimal custLiveFinAmount = BigDecimal.ZERO;
	private int custReqFtpCount;
	private BigDecimal custReqFtpAmount = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custRepayTot = BigDecimal.ZERO;

	private BigDecimal custPastDueCount = BigDecimal.ZERO;
	private BigDecimal custPastDueAmt = BigDecimal.ZERO;
	private BigDecimal custPDHist30D = BigDecimal.ZERO;
	private BigDecimal custPDHist60D = BigDecimal.ZERO;
	private BigDecimal custPDHist90D = BigDecimal.ZERO;
	private BigDecimal custPDHist120D = BigDecimal.ZERO;
	private BigDecimal custPDHist180D = BigDecimal.ZERO;
	private BigDecimal custPDHist180DP = BigDecimal.ZERO;
	private BigDecimal custPDLive30D = BigDecimal.ZERO;
	private BigDecimal custPDLive60D = BigDecimal.ZERO;
	private BigDecimal custPDLive90D = BigDecimal.ZERO;
	private BigDecimal custPDLive120D = BigDecimal.ZERO;
	private BigDecimal custPDLive180D = BigDecimal.ZERO;
	private BigDecimal custPDLive180DP = BigDecimal.ZERO;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public int getCustLiveFinCount() {
		return custLiveFinCount;
	}
	public void setCustLiveFinCount(int custLiveFinCount) {
		this.custLiveFinCount = custLiveFinCount;
	}

	public BigDecimal getCustLiveFinAmount() {
		return custLiveFinAmount;
	}
	public void setCustLiveFinAmount(BigDecimal custLiveFinAmount) {
		this.custLiveFinAmount = custLiveFinAmount;
	}

	public int getCustReqFtpCount() {
		return custReqFtpCount;
	}
	public void setCustReqFtpCount(int custReqFinCount) {
		this.custReqFtpCount = custReqFinCount;
	}

	public BigDecimal getCustReqFtpAmount() {
		return custReqFtpAmount;
	}
	public void setCustReqFtpAmount(BigDecimal custReqFinAmount) {
		this.custReqFtpAmount = custReqFinAmount;
	}

	public BigDecimal getCustRepayBank() {
		return custRepayBank;
	}
	public void setCustRepayBank(BigDecimal custRepayBank) {
		this.custRepayBank = custRepayBank;
	}

	public BigDecimal getCustRepayOther() {
		return custRepayOther;
	}
	public void setCustRepayOther(BigDecimal custRepayOther) {
		this.custRepayOther = custRepayOther;
	}

	public BigDecimal getCustRepayTot() {
		return custRepayTot;
	}
	public void setCustRepayTot(BigDecimal custRepayTot) {
		this.custRepayTot = custRepayTot;
	}

	public BigDecimal getCustPastDueCount() {
		return custPastDueCount;
	}
	public void setCustPastDueCount(BigDecimal custPastDueCount) {
		this.custPastDueCount = custPastDueCount;
	}

	public BigDecimal getCustPastDueAmt() {
		return custPastDueAmt;
	}
	public void setCustPastDueAmt(BigDecimal custPastDueAmt) {
		this.custPastDueAmt = custPastDueAmt;
	}

	public BigDecimal getCustPDHist30D() {
		return custPDHist30D;
	}
	public void setCustPDHist30D(BigDecimal custPDHist30D) {
		this.custPDHist30D = custPDHist30D;
	}

	public BigDecimal getCustPDHist60D() {
		return custPDHist60D;
	}
	public void setCustPDHist60D(BigDecimal custPDHist60D) {
		this.custPDHist60D = custPDHist60D;
	}

	public BigDecimal getCustPDHist90D() {
		return custPDHist90D;
	}
	public void setCustPDHist90D(BigDecimal custPDHist90D) {
		this.custPDHist90D = custPDHist90D;
	}

	public BigDecimal getCustPDHist120D() {
		return custPDHist120D;
	}
	public void setCustPDHist120D(BigDecimal custPDHist120D) {
		this.custPDHist120D = custPDHist120D;
	}

	public BigDecimal getCustPDHist180D() {
		return custPDHist180D;
	}
	public void setCustPDHist180D(BigDecimal custPDHist180D) {
		this.custPDHist180D = custPDHist180D;
	}

	public BigDecimal getCustPDHist180DP() {
		return custPDHist180DP;
	}
	public void setCustPDHist180DP(BigDecimal custPDHist180DP) {
		this.custPDHist180DP = custPDHist180DP;
	}

	public BigDecimal getCustPDLive30D() {
		return custPDLive30D;
	}
	public void setCustPDLive30D(BigDecimal custPDLive30D) {
		this.custPDLive30D = custPDLive30D;
	}

	public BigDecimal getCustPDLive60D() {
		return custPDLive60D;
	}
	public void setCustPDLive60D(BigDecimal custPDLive60D) {
		this.custPDLive60D = custPDLive60D;
	}

	public BigDecimal getCustPDLive90D() {
		return custPDLive90D;
	}
	public void setCustPDLive90D(BigDecimal custPDLive90D) {
		this.custPDLive90D = custPDLive90D;
	}

	public BigDecimal getCustPDLive120D() {
		return custPDLive120D;
	}
	public void setCustPDLive120D(BigDecimal custPDLive120D) {
		this.custPDLive120D = custPDLive120D;
	}

	public BigDecimal getCustPDLive180D() {
		return custPDLive180D;
	}
	public void setCustPDLive180D(BigDecimal custPDLive180D) {
		this.custPDLive180D = custPDLive180D;
	}

	public BigDecimal getCustPDLive180DP() {
		return custPDLive180DP;
	}
	public void setCustPDLive180DP(BigDecimal custPDLive180DP) {
		this.custPDLive180DP = custPDLive180DP;
	}

	public BigDecimal getCustTotalIncome() {
		return custTotalIncome;
	}
	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		this.custTotalIncome = custTotalIncome;
	}

}
*/