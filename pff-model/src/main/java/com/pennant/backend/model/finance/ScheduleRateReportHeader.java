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
 * * FileName : FinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-08-2012 * *
 * Modified Date : 13-08-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRateReportHeader {

	private String cif;
	private String custName;
	private String finReference;
	private String disbursedAmt;
	private String instDaysBasis;

	private List<ScheduleRateReport> rateReports = new ArrayList<>();

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getDisbursedAmt() {
		return disbursedAmt;
	}

	public void setDisbursedAmt(String disbursedAmt) {
		this.disbursedAmt = disbursedAmt;
	}

	public String getInstDaysBasis() {
		return instDaysBasis;
	}

	public void setInstDaysBasis(String instDaysBasis) {
		this.instDaysBasis = instDaysBasis;
	}

	public List<ScheduleRateReport> getRateReports() {
		return rateReports;
	}

	public void setRateReports(List<ScheduleRateReport> rateReports) {
		this.rateReports = rateReports;
	}

}
