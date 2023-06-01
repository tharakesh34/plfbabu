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
 * * FileName : SelectCollateralTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2016 *
 * * Modified Date : 14-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.payment.feerefundheader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.core.FinOverDueService;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectFeeRefundHeaderDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(SelectFeeRefundHeaderDialogCtrl.class);

	protected Window window_SelectFeeRefundHeaderDialog;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private FeeRefundHeaderListCtrl feeRefundHeaderListCtrl;
	private FeeRefundHeader feeRefundHeader;
	private FeeRefundHeaderService feeRefundHeaderService;
	private PaymentHeaderService paymentHeaderService;
	private HoldRefundUploadDAO holdRefundUploadDAO;
	private FinOverDueService finOverDueService;

	private ManualAdviseService manualAdviseService;

	public SelectFeeRefundHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectFeeRefundHeaderDialog(Event event) {
		logger.debug("Entering");

		try {
			this.feeRefundHeader = (FeeRefundHeader) arguments.get("feeRefundHeader");
			this.feeRefundHeaderListCtrl = (FeeRefundHeaderListCtrl) arguments.get("feeRefundHeaderListCtrl");
			if (this.feeRefundHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		setPageComponents(window_SelectFeeRefundHeaderDialog);
		showSelectFeeRefundHeaderDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the FeeRefundHeaderDialog window modal.
	 */
	private void showSelectFeeRefundHeaderDialog() {
		logger.debug("Entering");
		try {
			this.window_SelectFeeRefundHeaderDialog.doModal();
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
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		logger.debug("Leaving");
	}

	private boolean isValidateCancelManualAdvise(long finID) {
		List<ManualAdvise> list = manualAdviseService.getCancelledManualAdvise(finID);
		return list.stream().anyMatch(m -> m.getStatus() == null);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);

		if (!doFieldValidation()) {
			return;
		}

		long finID = ComponentUtil.getFinID(this.finReference);

		FinanceMain fm = feeRefundHeaderService.getFinanceDetails(finID);
		String holdStatus = holdRefundUploadDAO.getHoldRefundStatus(finID);

		if (this.feeRefundHeaderService.isInProgress(finID)) {
			MessageUtil.showMessage(Labels.getLabel("Finance_Inprogresss_" + FinServiceEvent.FEEREFUNDINST));
			return;
		}

		if (this.paymentHeaderService.isInProgress(finID)) {
			MessageUtil.showMessage(Labels.getLabel("Finance_Inprogresss_" + FinServiceEvent.PAYMENTINST));
			return;
		}

		String rcdMntnSts = fm.getRcdMaintainSts();

		if (StringUtils.isNotEmpty(rcdMntnSts)
				&& (FinServiceEvent.MANUALADVISE.equals(rcdMntnSts) ? isValidateCancelManualAdvise(finID) : true)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
			return;
		}

		if (UploadConstants.REFUND_HOLD.equals(holdStatus)) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_HoldLoan"));
			return;
		}

		if (fm.isWriteoffLoan()) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_WriteOffLoan"));
			return;
		}

		feeRefundHeader.setOverDueAgainstLoan(finOverDueService.getDueAgnistLoan(finID));
		feeRefundHeader.setOverDueAgainstCustomer(finOverDueService.getDueAgnistCustomer(finID, false));

		Map<String, Object> arg = new HashMap<>();
		arg.put("feeRefundHeader", feeRefundHeader);
		arg.put("feeRefundHeaderListCtrl", feeRefundHeaderListCtrl);
		arg.put("financeMain", fm);
		try {
			Executions.createComponents("/WEB-INF/pages/FeeRefund/FeeRefundHeaderDialog.zul", null, arg);
			this.window_SelectFeeRefundHeaderDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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
						new String[] { Labels.getLabel("label_SelectFeeRefundHeaderDialog_FinaType.value") }));
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

	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setHoldRefundUploadDAO(HoldRefundUploadDAO holdRefundUploadDAO) {
		this.holdRefundUploadDAO = holdRefundUploadDAO;
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

}
