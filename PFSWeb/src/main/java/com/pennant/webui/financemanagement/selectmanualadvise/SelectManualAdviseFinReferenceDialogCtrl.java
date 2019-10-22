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
 * FileName    		:  SelectFinReferenceDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-08-2016    														*
 *                                                                  						*
 * Modified Date    :  30-08-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.selectmanualadvise;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.webui.finance.holddisbursement.HoldDisbursementListCtrl;
import com.pennant.webui.finance.manualadvise.ManualAdviseListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/ SelectFinanceTypeDialog.zul file.
 */
public class SelectManualAdviseFinReferenceDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(SelectManualAdviseFinReferenceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectFinanceReferenceDialog; // autoWired
	protected ExtendedCombobox finReference; // autoWired
	protected Button btnProceed; // autoWireddialogCtrl
	private ManualAdvise manualAdvise = null;
	private ManualAdviseListCtrl manualAdviseListCtrl;
	private transient ManualAdviseService manualAdviseService;
	private HoldDisbursement holdDisbursement;
	private HoldDisbursementListCtrl holdDisbursementListCtrl;
	private String moduleDefiner = "";
	private HoldDisbursementService holdDisbursementService;

	/**
	 * default constructor.<br>
	 */
	public SelectManualAdviseFinReferenceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectFinanceReferenceDialog(Event event) throws Exception {
		logger.debug("Entering");
		setPageComponents(window_SelectFinanceReferenceDialog);
		try {
			if (arguments.get("moduleDefiner") != null && arguments.get("moduleDefiner").equals("holdDisbursement")) {
				moduleDefiner = arguments.get("moduleDefiner").toString();
				this.holdDisbursement = (HoldDisbursement) arguments.get("holdDisbursement");
				this.holdDisbursementListCtrl = (HoldDisbursementListCtrl) arguments.get("holdDisbursementListCtrl");
				if (this.holdDisbursement == null) {
					throw new Exception(Labels.getLabel("error.unhandled"));
				}
			} else {
				this.manualAdvise = (ManualAdvise) arguments.get("manualAdvise");
				this.manualAdviseListCtrl = (ManualAdviseListCtrl) arguments.get("manualAdviseListCtrl");

				if (this.manualAdvise == null) {
					throw new Exception(Labels.getLabel("error.unhandled"));
				}
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		showSelectPaymentHeaderDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the SelectPaymentHeaderDialog window modal.
	 */
	private void showSelectPaymentHeaderDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			this.window_SelectFinanceReferenceDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		if (moduleDefiner.equals("holdDisbursement")) {
			Filter[] filtersFin = new Filter[2];
			filtersFin[0] = new Filter("finisactive", true, Filter.OP_EQUAL);
			if (App.DATABASE == Database.POSTGRES) {
				filtersFin[1] = new Filter("CLOSINGSTATUS", "", Filter.OP_NULL);
			} else {
				filtersFin[1] = new Filter("CLOSINGSTATUS", null, Filter.OP_NULL);

			}

			this.finReference.setFilters(filtersFin);
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (!doFieldValidation()) {
			return;
		}
		HashMap<String, Object> arg = new HashMap<String, Object>();
		if (StringUtils.equals(moduleDefiner, "holdDisbursement")) {
			arg.put("holdDisbursement", holdDisbursement);
			arg.put("holdDisbursementListCtrl", holdDisbursementListCtrl);
			//arg.put("financeMain", financeMain);
			try {
				//FinanceMain financeMain = (FinanceMain) this.finReference.getObject();
				//holdDisbursement.setDisbursedAmount(financeMain.getFinCurrAssetValue());
				//holdDisbursement.setTotalLoanAmt(financeMain.getFinAssetValue());
				holdDisbursement.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
				Executions.createComponents("/WEB-INF/pages/Finance/HoldDisbursement/HoldDisbursementDialog.zul", null,
						arg);
				this.window_SelectFinanceReferenceDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}
		} else {
			FinanceMain financeMain = manualAdviseService
					.getFinanceDetails(StringUtils.trimToEmpty(this.finReference.getValue()));

			arg.put("manualAdvise", manualAdvise);
			arg.put("manualAdviseListCtrl", manualAdviseListCtrl);
			arg.put("financeMain", financeMain);
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManualAdvise/ManualAdviseDialog.zul",
						null, arg);
				this.window_SelectFinanceReferenceDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {

		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
			}
			if (moduleDefiner.equals("holdDisbursement")) {
				if (getHoldDisbursementService().isFinServiceInstructionExist(this.finReference.getValue(), "_temp",
						"AddDisbursement")) {
					throw new WrongValueException(this.finReference, "Not Allowed for Hold Disbursement");
				}
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		return true;
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Setting the amount formats based on currency
	 * 
	 * @param event
	 */
	public void onFulfill$finReference(Event event) {
		logger.debug("Entering " + event.toString());

		Clients.clearWrongValue(this.finReference);
		this.finReference.setConstraint("");
		this.finReference.setErrorMessage("");

		logger.debug("Leaving " + event.toString());
	}

	// Getters and Setters
	public ManualAdviseListCtrl getManualAdviseListCtrl() {
		return manualAdviseListCtrl;
	}

	public void setManualAdviseListCtrl(ManualAdviseListCtrl manualAdviseListCtrl) {
		this.manualAdviseListCtrl = manualAdviseListCtrl;
	}

	public ManualAdvise getManualAdvise() {
		return manualAdvise;
	}

	public void setManualAdvise(ManualAdvise manualAdvise) {
	}

	public ManualAdviseService getManualAdviseService() {
		return manualAdviseService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public HoldDisbursementService getHoldDisbursementService() {
		return holdDisbursementService;
	}

	public void setHoldDisbursementService(HoldDisbursementService holdDisbursementService) {
		this.holdDisbursementService = holdDisbursementService;
	}

}
