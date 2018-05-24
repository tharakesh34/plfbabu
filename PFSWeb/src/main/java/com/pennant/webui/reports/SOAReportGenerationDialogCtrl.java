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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/SOAReportGenerationDialog.zul file.
 */
public class SOAReportGenerationDialogCtrl extends GFCBaseCtrl<StatementOfAccount> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = Logger.getLogger(SOAReportGenerationDialogCtrl.class);

	protected Window window_SOAReportGenerationDialogCtrl;
	protected ExtendedCombobox	finReference;
	protected Datebox	startDate;
	protected Datebox	endDate;
	
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
		
		//Close the current window
		this.window_SOAReportGenerationDialogCtrl.onClose();
		
		//Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
		
		logger.debug(Literal.LEAVING);
	}
	
	public void onClick$btnGenereate(Event event) {
		logger.debug(Literal.ENTERING);
		
		doSetValidation();
		
		doWriteComponentsToBean(this.statementOfAccount);
		
		List<Object> list = new ArrayList<Object>();
		list.add(this.statementOfAccount.getSoaSummaryReports());
		list.add(this.statementOfAccount.getTransactionReports());
		try {
				ReportGenerationUtil.generateReport("FINENQ_StatementOfAccount", this.statementOfAccount, list, true, 1,
						getUserWorkspace().getLoggedInUser().getFullName(), null);

			}
		 catch (InterruptedException e) {
			MessageUtil.showError(e);
		}
		this.window_SOAReportGenerationDialogCtrl.setVisible(true);
		logger.debug(Literal.LEAVING);
	}
	
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param statementOfAccount
	 */
	public void doWriteComponentsToBean(StatementOfAccount statementOfAccount) {
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
			setStatementOfAccount(soaReportGenerationService.getStatmentofAccountDetails(finReference, startDate, endDate));
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
		this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_SOAReportDialog_FinReference.value"), null, true, true));
		
		//Date appStartDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			//this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true, appStartDate, appEndDate, true));
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true));
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