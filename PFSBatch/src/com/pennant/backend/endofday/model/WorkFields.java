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
 * FileName    		:  WorkFields.java													*                           
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
package com.pennant.backend.endofday.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class WorkFields implements Serializable{

	private static final long serialVersionUID = 2690631384358849589L;
	
	private BigDecimal workChargeAmount = BigDecimal.ZERO;
	private String workCalOn;
	private int workGraceDays; 
	private BigDecimal workPLShare = BigDecimal.ZERO;
	private String workType;
	private String workPLAC;
	private String workPLACSH; 
	private String workCAC; 
	private String workCACSH; 
	private boolean workWaiver; 
	private BigDecimal workMaxWaiver = BigDecimal.ZERO; 
	private boolean workSweep;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public BigDecimal getWorkChargeAmount() {
		return workChargeAmount;
	}
	public void setWorkChargeAmount(BigDecimal workChargeAmount) {
		this.workChargeAmount = workChargeAmount;
	}
	
	public String getWorkCalOn() {
		return workCalOn;
	}
	public void setWorkCalOn(String workCalOn) {
		this.workCalOn = workCalOn;
	}
	
	public int getWorkGraceDays() {
		return workGraceDays;
	}
	public void setWorkGraceDays(int workGraceDays) {
		this.workGraceDays = workGraceDays;
	}
	
	public BigDecimal getWorkPLShare() {
		return workPLShare;
	}
	public void setWorkPLShare(BigDecimal workPLShare) {
		this.workPLShare = workPLShare;
	}
	
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
	public String getWorkPLAC() {
		return workPLAC;
	}
	public void setWorkPLAC(String workPLAC) {
		this.workPLAC = workPLAC;
	}
	
	public String getWorkPLACSH() {
		return workPLACSH;
	}
	public void setWorkPLACSH(String workPLACSH) {
		this.workPLACSH = workPLACSH;
	}
	
	public String getWorkCAC() {
		return workCAC;
	}
	public void setWorkCAC(String workCAC) {
		this.workCAC = workCAC;
	}
	
	public String getWorkCACSH() {
		return workCACSH;
	}
	public void setWorkCACSH(String workCACSH) {
		this.workCACSH = workCACSH;
	}
	
	public boolean isWorkWaiver() {
		return workWaiver;
	}
	public void setWorkWaiver(boolean workWaiver) {
		this.workWaiver = workWaiver;
	}
	
	public BigDecimal getWorkMaxWaiver() {
		return workMaxWaiver;
	}
	public void setWorkMaxWaiver(BigDecimal workMaxWaiver) {
		this.workMaxWaiver = workMaxWaiver;
	}
	
	public boolean isWorkSweep() {
		return workSweep;
	}
	public void setWorkSweep(boolean workSweep) {
		this.workSweep = workSweep;
	}
	
}
*/