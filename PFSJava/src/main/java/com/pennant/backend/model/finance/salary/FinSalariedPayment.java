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
 * FileName    		:  ContractorAssetDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance.salary;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>FinSalariedPayment table</b>.<br>
 *
 */
public class FinSalariedPayment implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String 		finReference;
	private String 		priAccount;
	private String 		secAccount;
	private Date 		nextPayDate;
	private BigDecimal 	nextPayment = BigDecimal.ZERO;
	private Date 		valueDate;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getPriAccount() {
		return priAccount;
	}
	public void setPriAccount(String priAccount) {
		this.priAccount = priAccount;
	}
	
	public String getSecAccount() {
		return secAccount;
	}
	public void setSecAccount(String secAccount) {
		this.secAccount = secAccount;
	}
	
	public Date getNextPayDate() {
		return nextPayDate;
	}
	public void setNextPayDate(Date nextPayDate) {
		this.nextPayDate = nextPayDate;
	}
	
	public BigDecimal getNextPayment() {
		return nextPayment;
	}
	public void setNextPayment(BigDecimal nextPayment) {
		this.nextPayment = nextPayment;
	}
	
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	
}
