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
 *
 * FileName    		:  ErrorControl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
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
*/

package com.pennant.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Messagebox;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.MultiLineMessageBox;

public class ErrorControl extends Messagebox implements Serializable {
	private static final long	serialVersionUID	= 6395769771121558224L;
	private static final Logger	logger				= Logger.getLogger(ErrorControl.class);

	private int					returnCode			= PennantConstants.porcessCONTINUE;
	AuditHeader					auditHeader;

	public ErrorControl() {
		super();
	}

	public static int showErrorControl(Component parent, AuditHeader auditHeader) throws InterruptedException {
		return new ErrorControl(parent, auditHeader).getReturnCode();
	}

	public static AuditHeader showErrorDetails(Component parent, AuditHeader auditHeader) throws InterruptedException {
		return new ErrorControl(parent, auditHeader).getAuditHeader();
	}

	@SuppressWarnings("unused")
	private ErrorControl(Component parent, AuditHeader auditHeader) throws InterruptedException {
		super();
		MultiLineMessageBox.doErrorTemplate();

		if (auditHeader != null) {

			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {

				for (int i = 0; i < auditHeader.getErrorMessage().size(); i++) {
					ErrorDetails errorDetail = auditHeader.getErrorMessage().get(i);
					showDetails(errorDetail);
					this.returnCode = PennantConstants.porcessCANCEL;
					break;
				}

			} else if (!auditHeader.isOveride() && auditHeader.getOverideMessage() != null
					&& auditHeader.getOverideMessage().size() > 0) {
				int selectedBtn = PennantConstants.porcessCONTINUE;
				HashMap<String, ArrayList<ErrorDetails>> overideMap = auditHeader.getOverideMap();

				for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
					ErrorDetails overideDetail = auditHeader.getOverideMessage().get(i);

					if (!isOverride(overideMap, overideDetail)) {
						selectedBtn = showDetails(overideDetail);
						if (selectedBtn == 2) {
							this.returnCode = PennantConstants.porcessCANCEL;
							break;
						} else {
							selectedBtn = PennantConstants.porcessOVERIDE;
							setOverideMap(overideMap, overideDetail);
						}
					}

				}

				this.returnCode = selectedBtn;
				auditHeader.setOverideMap(overideMap);
			} else if (auditHeader.getInfoMessage() != null && auditHeader.getInfoMessage().size() > 0) {

				for (int i = 0; i < auditHeader.getInfoMessage().size(); i++) {
					ErrorDetails infoDetail = auditHeader.getInfoMessage().get(i);
					showDetails(infoDetail);
				}
			}
		}

		auditHeader.setProcessStatus(returnCode);
		setAuditHeader(auditHeader);
	}

	private int showDetails(ErrorDetails errorDetail) throws InterruptedException {
		int retValue;
		String title = Labels.getLabel("message.Information");
		int buttons = MultiLineMessageBox.OK;
		String icon = MultiLineMessageBox.INFORMATION;
		logger.info("Error Detail: " + errorDetail.getErrorCode() + " - " + errorDetail.getError());

		if (errorDetail.getErrorSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)) {
			buttons = MultiLineMessageBox.ABORT;
			title = Labels.getLabel("message.Error");
			icon = MultiLineMessageBox.ERROR;

		} else if (errorDetail.getErrorSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_WARNING)) {

			buttons = MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE;
			title = Labels.getLabel("message.Overide");
			icon = MultiLineMessageBox.EXCLAMATION;
		}

		retValue = MultiLineMessageBox.show(errorDetail.getErrorCode() + "-" + errorDetail.getError(), title, buttons,
				icon, true);

		return retValue;

	}

	public int getReturnCode() {
		return returnCode;
	}

	private HashMap<String, ArrayList<ErrorDetails>> setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap,
			ErrorDetails errorDetail) {

		if (StringUtils.isNotBlank(errorDetail.getErrorField())) {

			ArrayList<ErrorDetails> errorDetails = null;

			if (overideMap.containsKey(errorDetail.getErrorField())) {
				errorDetails = overideMap.get(errorDetail.getErrorField());

				for (int i = 0; i < errorDetails.size(); i++) {
					if (errorDetails.get(i).getErrorCode().equals(errorDetail.getErrorCode())) {
						errorDetails.remove(i);
						break;
					}
				}

				overideMap.remove(errorDetail.getErrorField());

			} else {
				errorDetails = new ArrayList<ErrorDetails>();

			}

			errorDetail.setErrorOveride(true);
			errorDetails.add(errorDetail);

			overideMap.put(errorDetail.getErrorField(), errorDetails);

		}
		return overideMap;
	}

	public AuditHeader getAuditHeader() {
		return auditHeader;
	}

	public void setAuditHeader(AuditHeader auditHeader) {
		this.auditHeader = auditHeader;
	}

	private boolean isOverride(HashMap<String, ArrayList<ErrorDetails>> overideMap, ErrorDetails errorDetail) {

		if (overideMap.containsKey(errorDetail.getErrorField())) {

			ArrayList<ErrorDetails> errorDetails = overideMap.get(errorDetail.getErrorField());

			for (int i = 0; i < errorDetails.size(); i++) {

				if (errorDetails.get(i).getErrorCode().equals(errorDetail.getErrorCode())) {
					return errorDetails.get(i).isErrorOveride();
				}
			}

		}

		return false;
	}
}
