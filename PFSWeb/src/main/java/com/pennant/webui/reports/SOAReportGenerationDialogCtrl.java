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
 * FileName    		:  SOAReportGenerationDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012   														*
 *                                                                  						*
 * Modified Date    :  23-09-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012         Pennant	                 0.1                                        * 
 * 24-05-2018         Srikanth                  0.2           Merge the Code From Bajaj To Core                                                                                        * 
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportCreationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/SOAReportGenerationDialog.zul file.
 */
public class SOAReportGenerationDialogCtrl extends GFCBaseCtrl<StatementOfAccount> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = LogManager.getLogger(SOAReportGenerationDialogCtrl.class);

	protected Window window_SOAReportGenerationDialogCtrl;
	protected ExtendedCombobox finReference;
	protected Datebox startDate;
	protected Datebox endDate;
	private String finType;
	private boolean isAlwFlexi;

	protected Window dialogWindow;
	private boolean isCustomer360;

	private StatementOfAccount statementOfAccount = new StatementOfAccount();
	private transient SOAReportGenerationService soaReportGenerationService;

	public SOAReportGenerationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SOAReportGenerationDialogCtrl(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SOAReportGenerationDialogCtrl);

		try {
			if (arguments.containsKey("financeReference")) {
				this.finReference.setValue((String) arguments.get("financeReference"));
			}
			if (arguments.containsKey("dialogWindow")) {
				dialogWindow = (Window) arguments.get("dialogWindow");
			}
			if (arguments.containsKey("customer360")) {
				isCustomer360 = (boolean) arguments.get("customer360");
				if (arguments.containsKey("finStartDate")) {
					this.startDate.setValue((Date) arguments.get("finStartDate"));
					this.endDate.setValue(DateUtility.getAppDate());
				}
				this.finReference.setReadonly(true);
			}
			doSetFieldProperties();
			this.window_SOAReportGenerationDialogCtrl.doModal();
			//setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(140);

		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.endDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		if (isCustomer360) {
			this.window_SOAReportGenerationDialogCtrl.onClose();
		}

		else {
			//Close the current window
			this.window_SOAReportGenerationDialogCtrl.onClose();

			//Close the current menu item
			final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
					.getFellow("tabBoxIndexCenter");
			tabbox.getSelectedTab().close();
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnGenereate(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		doWriteComponentsToBean(this.statementOfAccount);

		List<Object> list = new ArrayList<Object>();
		list.add(this.statementOfAccount.getSoaSummaryReports());
		list.add(this.statementOfAccount.getTransactionReports());
		list.add(this.statementOfAccount.getApplicantDetails());
		list.add(this.statementOfAccount.getOtherFinanceDetails());
		list.add(this.statementOfAccount.getSheduleReports());
		list.add(this.statementOfAccount.getInterestRateDetails());

		List<String> finTypes = this.soaReportGenerationService.getSOAFinTypes();
		if (isCustomer360) {
			try {

				if (finTypes != null && finTypes.contains(finType)) {
					createReport("FINENQ_StatementOfAccount_FinType", this.statementOfAccount, list,
							getUserWorkspace().getLoggedInUser().getFullName(), window);
				} else if (isAlwFlexi) {
					createReport("FINENQ_StatementOfAccount_FinType_Hybrid", this.statementOfAccount, list,
							getUserWorkspace().getLoggedInUser().getFullName(), window);
				} else {
					createReport("FINENQ_StatementOfAccount", this.statementOfAccount, list,
							getUserWorkspace().getLoggedInUser().getFullName(), window);
				}
			} catch (InterruptedException e) {
				MessageUtil.showError(e);
			} catch (JRException e) {
				MessageUtil.showError(e);
			}
		} else {
			try {
				if (finTypes != null && finTypes.contains(finType)) {
					ReportGenerationUtil.generateReport("FINENQ_StatementOfAccount_FinType", this.statementOfAccount,
							list, true, 1, getUserWorkspace().getLoggedInUser().getFullName(), null);

				} else if (isAlwFlexi) {

					ReportGenerationUtil.generateReport("FINENQ_StatementOfAccount_FinType_Hybrid",
							this.statementOfAccount, list, true, 1, getUserWorkspace().getLoggedInUser().getFullName(),
							null);
				} else {
					ReportGenerationUtil.generateReport("FINENQ_StatementOfAccount", this.statementOfAccount, list,
							true, 1, getUserWorkspace().getLoggedInUser().getFullName(), null);

				}
			} catch (InterruptedException e) {
				MessageUtil.showError(e);
			}
		}
		this.window_SOAReportGenerationDialogCtrl.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				Date finApprovedDate = details.getFinApprovedDate();
				if (finApprovedDate != null && (finApprovedDate.compareTo(details.getFinStartDate()) < 0)) {
					this.startDate.setValue(finApprovedDate);
				} else {
					this.startDate.setValue(details.getFinStartDate());
				}
				this.finReference.setValue(details.getFinReference());
				this.endDate.setValue(SysParamUtil.getAppDate());
			} else {
				this.finReference.setValue("");
				this.startDate.setValue(null);
				this.endDate.setValue(null);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param statementOfAccount
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doWriteComponentsToBean(StatementOfAccount statementOfAccount)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String finReference = "";
		Date startDate = null;
		Date endDate = null;
		// FinReference
		try {
			finReference = this.finReference.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Start Date
		try {
			startDate = this.startDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Date
		try {
			endDate = this.endDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.isEmpty()) {
			setStatementOfAccount(
					soaReportGenerationService.getStatmentofAccountDetails(finReference, startDate, endDate, false));
		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.finReference.setConstraint("");
		this.startDate.setConstraint("");
		this.endDate.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.finReference.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.endDate.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		doClearMessage();
		doRemoveValidation();

		//Finance Type
		this.finReference.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SOAReportDialog_FinReference.value"), null, true, true));

		//Date appStartDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			//this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true, appStartDate, appEndDate, true));
			this.startDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true));
		}
		// end Date
		if (!this.endDate.isDisabled()) {
			try {
				this.startDate.getValue();
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_EndDate.value"),
						true, this.startDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_EndDate.value"),
						true, true, null, false));
			}
		}

		logger.debug("Leaving");
	}

	private void createReport(String reportName, Object object, List listData, String userName, Window dialogWindow)
			throws JRException, InterruptedException {
		logger.debug("Entering");
		try {
			byte[] buf = ReportCreationUtil.generatePDF(reportName, object, listData, userName);

			boolean reportView = true;
			//Assignments
			if ("AssignmentUploadDetails".equals(reportName)) {
				reportView = false;
			}
			if (reportView) {
				final Map<String, Object> auditMap = new HashMap<String, Object>();
				auditMap.put("reportBuffer", buf);
				String genReportName = Labels.getLabel(reportName);
				auditMap.put("reportName", StringUtils.isBlank(genReportName) ? reportName : genReportName);
				if (dialogWindow != null) {
					auditMap.put("dialogWindow", dialogWindow);
				}
				auditMap.put("Customer360", isCustomer360);
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Template does not exist.");
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}
		logger.debug("Leaving");
	}

	public StatementOfAccount getStatementOfAccount() {
		return statementOfAccount;
	}

	public void setStatementOfAccount(StatementOfAccount statementOfAccount) {
		this.statementOfAccount = statementOfAccount;
	}

	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}
}