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
 * FileName    		:  SecondaryMandate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.mandate;

/**
 * Model class for the <b>SecondaryMandateStatus table</b>.<br>
 * 
 */
public class UploadSecondaryMandate {
	private static final long serialVersionUID = 1L;

	private long uploadId = Long.MIN_VALUE;;
	private long mandateID = Long.MIN_VALUE;
	private String mandateType;
	private String bankCode;
	private String mICR;
	private String accNumber;
	private String accHolderName;
	private String accType;
	private String barCodeNumber;
	private String reason;
	private String fileName;
	private boolean status;
	private long 	lastMntBy;
	private String module;
	
	public UploadSecondaryMandate() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public long getMandateID() {
		return mandateID;
	}

	public void setMandateID(long mandateID) {
		this.mandateID = mandateID;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getmICR() {
		return mICR;
	}

	public void setmICR(String mICR) {
		this.mICR = mICR;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getAccHolderName() {
		return accHolderName;
	}

	public void setAccHolderName(String accHolderName) {
		this.accHolderName = accHolderName;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}




	
}
