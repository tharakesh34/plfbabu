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
 * FileName    		:  FinanceMain.java                                                 	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * Model class for the <b>DdaPresentment</b>.<br>
 * 
 */
public class DdaPresentment implements java.io.Serializable {
	private static final long serialVersionUID = -3026443763391506067L;
	private static final Logger logger = Logger.getLogger(DdaPresentment.class);

	private String ddaReference;
	private Date ddaDate;
	private String ddaStatus;
	private String hostReference;
	private String requestData;
	private String responseData;
	private String[] request;
	private String[] response;

	public String getDdaReference() {
		return ddaReference;
	}

	public void setDdaReference(String ddaReference) {
		this.ddaReference = ddaReference;
	}

	public Date getDdaDate() {
		return ddaDate;
	}

	public void setDdaDate(Date ddaDate) {
		this.ddaDate = ddaDate;
	}

	public String getDdaStatus() {
		return ddaStatus;
	}

	public void setDdaStatus(String ddaStatus) {
		this.ddaStatus = ddaStatus;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;

		this.request = requestData.split(";");
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;

		this.response = responseData.split(";");
	}

	public String getFinReference() {
		if (request.length != 7) {
			return "";
		}

		return request[1];
	}

	public String getInstallmentDate() {
		if (request.length != 7) {
			return "";
		}

		try {
			return DateUtility.format(
					DateUtility.parse(request[4], "dd-MM-yyyy"),
					DateFormat.SHORT_DATE);
		} catch (ParseException e) {
			logger.warn("Exception: ", e);
			return "";
		}
	}

	public String getReason() {
		if (response.length != 8) {
			return "";
		}

		return response[6];
	}

	public String getDDARefNo() {
		return ddaReference + "-"
				+ DateUtility.format(DateUtility.getAppDate(), "yyyyMMdd");
	}
}
