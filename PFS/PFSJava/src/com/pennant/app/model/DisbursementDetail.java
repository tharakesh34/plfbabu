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
 * FileName    		:  DisbursementDetail.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-10-2011    														*
 *                                                                  						*
 * Modified Date    :  00-00-0000    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DisbursementDetail implements Serializable {

    private static final long serialVersionUID = 9139394649687224793L;
    
	private String finReference = null;
	private Date disbDate;
	private BigDecimal disbAmount;
	
	public DisbursementDetail() {
		super();
	}
	
	public DisbursementDetail(Date disbDate, BigDecimal disbAmount) {
		this.disbDate = disbDate;
		this.disbAmount = disbAmount;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public Date getDisbDate() {
		return disbDate;
	}
	public void setDisbDate(Date disbDate) {
		this.disbDate = disbDate;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}
	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getId() {
		return finReference;
	}
	public void setId (String id) {
		this.finReference = id;
	}

}
