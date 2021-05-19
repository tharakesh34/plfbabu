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
 * FileName    		:  CostOfFundsStatusReportDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-07-2018    														*
 *                                                                  						*
 * Modified Date    :  18-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-07-2018       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/report/CostOfFundsStatusReportDialogCtrl.zul file.
 */
public class ReceiptUploadReportDialogCtrl extends GFCBaseCtrl<ReceiptUploadHeader> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ReceiptUploadReportDialogCtrl.class);

	protected Window window_ReceiptUploadReportDialogCtrl; // autoWired

	protected Datebox uploadDate;
	protected ExtendedCombobox fileName;

	protected Button btnFileName;
	protected Button btnRefresh;
	protected Tabbox tabbox;

	protected UploadHeader uploadHeader = null;

	/**
	 * default constructor.<br>
	 */
	public ReceiptUploadReportDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * 
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptUploadReportDialogCtrl(Event event) throws Exception {

		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_ReceiptUploadReportDialogCtrl);

		try {

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			tabbox = (Tabbox) borderlayoutMain.getFellow("center").getFellow("divCenter")
					.getFellow("tabBoxIndexCenter");

			doSetFieldProperties();
			doShowDialog();

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.fileName.setModuleName("ReceiptUploadHeader");
		this.fileName.setMandatoryStyle(false);
		this.fileName.setValueType(DataType.LONG);
		this.fileName.setValueType(DataType.BIGDECIMAL);
		this.fileName.setValueType(DataType.INT);
		this.fileName.setDisplayStyle(2);
		this.fileName.setValueColumn("FileName");
		this.fileName.setValidateColumns(new String[] { "FileName" });
		this.fileName.setMandatoryStyle(true);
		this.fileName.setFilters(null);
		this.fileName.setConstraint("");

		this.uploadDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	public void onChange$uploadDate(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.uploadDate.getValue() == null) {
			this.fileName.setFilters(null);
			return;
		}

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("TRANSACTIONDATE", this.uploadDate.getValue(), Filter.OP_EQUAL);

		this.fileName.setFilters(filter);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearch(Event event) throws IOException {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean();

		String whereCond = " where FILENAME in (" + "'" + this.fileName.getValue() + "'" + ")";
		StringBuilder searchCriteria = new StringBuilder(" ");
		searchCriteria.append("File Name is " + this.fileName.getValue());

		String reportName = "";

		reportName = "ReceiptUploadDetails";

		ReportGenerationUtil.generateReport(getUserWorkspace().getLoggedInUser().getFullName(), reportName, whereCond,
				searchCriteria);

		doClose();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClear(Event event) {
		logger.debug("Entering");
		clearData();
		logger.debug("Leaving");
	}

	private void clearData() {
		doRemoveValidation();
		doClearMessage();
		doClear();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ReceiptUploadReportDialogCtrl);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose();
	}

	private void doClose() {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReceiptUploadHeader
	 */
	public void doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		doRemoveValidation();

		try {
			if (!this.fileName.isReadonly() && (this.fileName.getValue() == null || this.fileName.getValue() == "")) {
				throw new WrongValueException(this.fileName, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptUploadReport_Filename.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aReceiptUploadHeader
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");

		this.window_ReceiptUploadReportDialogCtrl.doModal();
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.uploadDate.setConstraint("");
		this.fileName.setConstraint("");
		Clients.clearWrongValue(this.fileName);
		logger.debug("Leaving");
	}

	public void onFulfill$fileName(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.fileName.getObject();
		if (dataObject instanceof String || dataObject == null) {

		} else if (!(dataObject instanceof String)) {
			Clients.clearWrongValue(this.fileName);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fileName.setErrorMessage("");
		this.uploadDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.fileName.setValue("");
		this.fileName.setMandatoryStyle(true);
		this.uploadDate.setValue(null);
		this.fileName.setFilters(null);
		logger.debug("Leaving");
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReceiptUploadReportDialogCtrl, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

}
