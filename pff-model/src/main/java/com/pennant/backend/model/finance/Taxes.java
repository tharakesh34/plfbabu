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
 * FileName    		:  Taxes.java                                                   		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  
 *                                                                  						*
 * Modified Date    :      																	*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-07-2019       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.Entity;

public class Taxes implements Entity {
	
	private long id = Long.MIN_VALUE;
	private long referenceId = Long.MIN_VALUE;
	private String taxType;
	private BigDecimal taxPerc = BigDecimal.ZERO;

	private BigDecimal actualTax = BigDecimal.ZERO;
	private BigDecimal paidTax = BigDecimal.ZERO;
	private BigDecimal netTax = BigDecimal.ZERO;
	private BigDecimal remFeeTax = BigDecimal.ZERO;
	private BigDecimal waivedTax = BigDecimal.ZERO;

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getTaxPerc() {
		return taxPerc;
	}

	public void setTaxPerc(BigDecimal taxPerc) {
		this.taxPerc = taxPerc;
	}

	public BigDecimal getActualTax() {
		return actualTax;
	}

	public BigDecimal getPaidTax() {
		return paidTax;
	}

	public BigDecimal getNetTax() {
		return netTax;
	}

	public void setActualTax(BigDecimal actualTax) {
		this.actualTax = actualTax;
	}

	public void setPaidTax(BigDecimal paidTax) {
		this.paidTax = paidTax;
	}

	public void setNetTax(BigDecimal netTax) {
		this.netTax = netTax;
	}

	public BigDecimal getRemFeeTax() {
		return remFeeTax;
	}
	public void setRemFeeTax(BigDecimal remFeeTax) {
		this.remFeeTax = remFeeTax;
	}

	public void setWaivedTax(BigDecimal waivedTax) {
		this.waivedTax = waivedTax;
	}

	public BigDecimal getWaivedTax() {
		return waivedTax;
	}

}