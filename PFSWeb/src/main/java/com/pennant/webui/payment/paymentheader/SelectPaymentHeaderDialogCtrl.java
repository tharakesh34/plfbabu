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
package com.pennant.webui.payment.paymentheader;

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
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectPaymentHeaderDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(SelectPaymentHeaderDialogCtrl.class);

	protected Window window_SelectPaymentHeaderDialog;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	private PaymentHeaderListCtrl paymentHeaderListCtrl;
	private PaymentHeader paymentHeader;
	private PaymentHeaderService paymentHeaderService;
	private FinanceDetailService financeDetailService;
	private FeeRefundHeaderService feeRefundHeaderService;
	private FinOverDueService finOverDueService;

	List<String> allowedExcesTypes = PennantStaticListUtil.getAllowedExcessTypeList();

	public SelectPaymentHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectPaymentHeaderDialog(Event event) {
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
	private void showSelectPaymentHeaderDialog() {
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

		String list = "";
		int len = allowedExcesTypes.size();
		for (int i = 0; i < len; i++) {
			if (i == 0) {
				list = StringUtils.trimToEmpty(list).concat("'" + allowedExcesTypes.get(i) + "'");
			} else if (i != 0 && i < len - 1) {
				list = list.concat("," + "'" + allowedExcesTypes.get(i) + "'");
			} else if (i == len - 1) {
				list = list.concat("," + "'" + allowedExcesTypes.get(i) + "'");
			}
		}

		// Query for faetching the finreferences only avalable in FinExcessAmount and ManualAdvise.
		StringBuilder sql = new StringBuilder();
		sql.append(" FinReference in (Select FinReference from FinExcessAmount Where (BalanceAmt > 0 ");
		sql.append(" or ReservedAmt > 0 and ExcessID in (Select ExcessID From Cross_Loan_Transfer_Temp))");
		sql.append(" and AmountType in (");
		sql.append(list);
		sql.append(") union ");
		sql.append(" Select FinReference from ManualAdvise M");
		sql.append(" Inner JOIN FEETYPES ft on ft.feetypeid = M.FeeTypeId ");
		sql.append(" Where M.AdviseType = ");
		sql.append(AdviseType.PAYABLE.id());
		sql.append(" AND HoldDue=0 And ((adviseAmount - PaidAmount - WaivedAmount) > 0");
		sql.append(" or ReservedAmt > 0 and AdviseId in (Select ExcessID From Cross_Loan_Transfer_Temp))");
		sql.append(" And ft.Refundable = 1) ");

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMainMaintenance");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setWhereClause(sql.toString());

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		if (!doFieldValidation()) {
			return;
		}

		long finID = ComponentUtil.getFinID(this.finReference);

		if (this.paymentHeaderService.isInProgress(finID)) {
			MessageUtil.showMessage("Payment instruction already in progress for - " + this.finReference.getValue());
			return;
		}

		if (this.feeRefundHeaderService.isInProgress(finID)) {
			MessageUtil.showMessage("Fee Refund already in progress for - " + this.finReference.getValue());
			return;
		}

		String rcdMntnSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);

		if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.PAYMENTINST.equals(rcdMntnSts)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
			return;
		}

		FinanceMain financeMain = paymentHeaderService.getFinanceDetails(finID);

		if (financeMain.isWriteoffLoan()) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_WriteOffLoan"));
			return;
		}

		if (FinanceConstants.FEE_REFUND_HOLD.equals(financeMain.getHoldStatus())) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_HoldLoan"));
			return;
		}

		paymentHeader.setOdAgainstLoan(finOverDueService.getDueAgnistLoan(finID));
		paymentHeader.setOdAgainstCustomer(finOverDueService.getDueAgnistCustomer(finID, false));

		Map<String, Object> arg = new HashMap<String, Object>();
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
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
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
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.finReference);
		this.finReference.setConstraint("");
		this.finReference.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}

}
