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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerChequeInfo.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerChequeInfo table</b>.<br>
 *
 */
@XmlType(propOrder = { "chequeSeq", "monthYear", "totChequePayment", "salary", "returnChequeAmt", "returnChequeCount" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerChequeInfo extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3217987429162088120L;
	
	private long custID = Long.MIN_VALUE;
	@XmlElement
	private int chequeSeq;
	@XmlElement
	private Date monthYear;
	@XmlElement
	private BigDecimal 	totChequePayment = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal 	salary = BigDecimal.ZERO;
	private BigDecimal 	debits = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal 	returnChequeAmt = BigDecimal.ZERO;
	@XmlElement
	private int returnChequeCount;
	private String remarks;
	
	private boolean newRecord=false;
	private String lovValue;
	private CustomerChequeInfo befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String sourceId;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerChequeInfo() {
		super();
	}

	public CustomerChequeInfo(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("sourceId");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public int getChequeSeq() {
		return chequeSeq;
	}
	public void setChequeSeq(int chequeSeq) {
		this.chequeSeq = chequeSeq;
	}

	public Date getMonthYear() {
		return monthYear;
	}
	public void setMonthYear(Date monthYear) {
		this.monthYear = monthYear;
	}

	public BigDecimal getTotChequePayment() {
		return totChequePayment;
	}
	public void setTotChequePayment(BigDecimal totChequePayment) {
		this.totChequePayment = totChequePayment;
	}

	public BigDecimal getSalary() {
		return salary;
	}
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public BigDecimal getDebits() {
		return debits;
	}
	public void setDebits(BigDecimal debits) {
		this.debits = debits;
	}

	public BigDecimal getReturnChequeAmt() {
		return returnChequeAmt;
	}
	public void setReturnChequeAmt(BigDecimal returnChequeAmt) {
		this.returnChequeAmt = returnChequeAmt;
	}

	public int getReturnChequeCount() {
		return returnChequeCount;
	}
	public void setReturnChequeCount(int returnChequeCount) {
		this.returnChequeCount = returnChequeCount;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustomerChequeInfo getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerChequeInfo beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getLoginUsrID());
		this.userDetails=userDetails;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
