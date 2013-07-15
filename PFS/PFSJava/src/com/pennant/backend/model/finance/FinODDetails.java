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
 * FileName    		:  FinODDetails.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2012    														*
 *                                                                  						*
 * Modified Date    :  08-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Model class for the <b>FinODDetails table</b>.<br>
 *
 */
public class FinODDetails implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private String finReference = null;
	private String finBranch = null;
	private String finType;
	private long custID;
	private Date finODSchdDate;
	private String finODFor;
	private Date finODTillDate;
	private BigDecimal finCurODAmt = new BigDecimal(0);
	private BigDecimal finMaxODAmt = new BigDecimal(0);
	private int finCurODDays;
	private Date finLMdfDate;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return finReference;
	}

	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public Date getFinODSchdDate() {
		return finODSchdDate;
	}

	public void setFinODSchdDate(Date finODSchdDate) {
		this.finODSchdDate = finODSchdDate;
	}

	public void setFinODFor(String finODFor) {
		this.finODFor = finODFor;
	}

	public String getFinODFor() {
		return finODFor;
	}

	public Date getFinODTillDate() {
		return finODTillDate;
	}

	public void setFinODTillDate(Date finODTillDate) {
		this.finODTillDate = finODTillDate;
	}

	public BigDecimal getFinCurODAmt() {
		return finCurODAmt;
	}

	public void setFinCurODAmt(BigDecimal finCurODAmt) {
		this.finCurODAmt = finCurODAmt;
	}

	public BigDecimal getFinMaxODAmt() {
		return finMaxODAmt;
	}

	public void setFinMaxODAmt(BigDecimal finMaxODAmt) {
		this.finMaxODAmt = finMaxODAmt;
	}

	public int getFinCurODDays() {
		return finCurODDays;
	}

	public void setFinCurODDays(int finCurODDays) {
		this.finCurODDays = finCurODDays;
	}

	public Date getFinLMdfDate() {
		return finLMdfDate;
	}

	public void setFinLMdfDate(Date finLMdfDate) {
		this.finLMdfDate = finLMdfDate;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(FinODDetails finODDetails) {
		return getId() == finODDetails.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinODDetails) {
			FinODDetails finODDetails = (FinODDetails) obj;
			return equals(finODDetails);
		}
		return false;
	}
}
