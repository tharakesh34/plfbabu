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
 * FileName    		:  SelectCollateralTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *14-12-2016        Pennant	                 0.1                                            * 
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
package com.pennant.webui.payment.paymentheader;

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
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectPaymentHeaderDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(SelectPaymentHeaderDialogCtrl.class);

	protected Window window_SelectPaymentHeaderDialog;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private PaymentHeaderListCtrl paymentHeaderListCtrl;
	private PaymentHeader paymentHeader;
	private PaymentHeaderService paymentHeaderService;

	public SelectPaymentHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectPaymentHeaderDialog(Event event) throws Exception {
		logger.debug("Entering");

		try {
			this.paymentHeader = (PaymentHeader) arguments.get("paymentHeader");
			this.paymentHeaderListCtrl = (PaymentHeaderListCtrl) arguments.get("paymentheaderListCtrl");
			if (this.paymentHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		setPageComponents(window_SelectPaymentHeaderDialog);
		showSelectPaymentHeaderDialog();
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the SelectPaymentHeaderDialog window modal.
	 */
	private void showSelectPaymentHeaderDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			this.window_SelectPaymentHeaderDialog.doModal();
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

		// Query for faetching the finreferences only avalable in FinExcessAmount and ManualAdvise.
		StringBuilder sql = new StringBuilder();
		sql.append(" FinReference in (Select FinReference from FinExcessAmount  where BalanceAmt > 0 union ");
		sql.append(" Select FinReference from ManualAdvise Where  AdviseType = ");
		sql.append(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		sql.append(" And BalanceAmt > 0)");
		
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setWhereClause(sql.toString());
		
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
		FinanceMain financeMain = paymentHeaderService.getFinanceDetails(StringUtils.trimToEmpty(this.finReference.getValue()));
		
		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("paymentHeader", paymentHeader);
		arg.put("paymentHeaderListCtrl", paymentHeaderListCtrl);
		arg.put("financeMain", financeMain);
		try {
			Executions.createComponents("/WEB-INF/pages/Payment/PaymentHeaderDialog.zul", null, arg);
			this.window_SelectPaymentHeaderDialog.onClose();
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
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
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY", new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
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

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}
	
	
}
