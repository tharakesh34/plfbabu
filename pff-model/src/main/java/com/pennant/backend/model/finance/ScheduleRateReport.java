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
 * * FileName : ScheduleRateReport.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-05-2019 * * Modified
 * Date : 14-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-05-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

public class ScheduleRateReport {

	private String instNo;
	private String balForPftCal;
	private String startDate;
	private String endDate;
	private String days;
	private String rate;
	private String calcPft;

	public String getInstNo() {
		return instNo;
	}

	public void setInstNo(String instNo) {
		this.instNo = instNo;
	}

	public String getBalForPftCal() {
		return balForPftCal;
	}

	public void setBalForPftCal(String balForPftCal) {
		this.balForPftCal = balForPftCal;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getCalcPft() {
		return calcPft;
	}

	public void setCalcPft(String calcPft) {
		this.calcPft = calcPft;
	}

}
