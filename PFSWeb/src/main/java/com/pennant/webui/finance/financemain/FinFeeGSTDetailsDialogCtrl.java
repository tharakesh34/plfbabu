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
 * * FileName : FinFeeGSTDetailsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-06-2017 * *
 * Modified Date : 01-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinFeeGSTDetailsDialog.zul file.
 */
public class FinFeeGSTDetailsDialogCtrl extends GFCBaseCtrl<FinFeeDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(FinFeeGSTDetailsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinFeeGSTDetailsDialog;

	protected Label label_FeeType;
	protected Label label_TaxComponent;
	protected Decimalbox feeAmount;
	protected Decimalbox cgst;
	protected Decimalbox sgst;
	protected Decimalbox igst;
	protected Decimalbox ugst;
	protected Decimalbox cess;
	protected Decimalbox total;

	private FinanceDetail financeDetail;
	private FinFeeDetail finFeeDetail;

	/**
	 * default constructor.<br>
	 */
	public FinFeeGSTDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinFeeReceipt object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinFeeGSTDetailsDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_FinFeeGSTDetailsDialog);

		try {
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("finFeeDetail")) {
				setFinFeeDetail((FinFeeDetail) arguments.get("finFeeDetail"));
			}

			this.btnClose.setVisible(true);
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		// No properties to set for the fields.
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			doWriteBeanToComponents();
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param financeDetail
	 * 
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering ");
		String taxComponent = "";

		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
		} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finFeeDetail.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
		}

		this.label_FeeType.setValue(this.finFeeDetail.getFeeTypeDesc());
		this.label_TaxComponent.setValue(taxComponent);

		int formatter = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());

		this.feeAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.feeAmount.setScale(formatter);
		this.feeAmount
				.setValue(PennantApplicationUtil.formateAmount(this.finFeeDetail.getNetAmountOriginal(), formatter));
		readOnlyComponent(true, this.feeAmount);

		BigDecimal cgstAmount = BigDecimal.ZERO;
		BigDecimal sgstAmount = BigDecimal.ZERO;
		BigDecimal igstAmount = BigDecimal.ZERO;
		BigDecimal ugstAmount = BigDecimal.ZERO;
		BigDecimal cessAmount = BigDecimal.ZERO;
		if (finFeeDetail.getTaxHeader() != null) {
			List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();

			for (Taxes taxes : taxDetails) {
				if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_CGST)) {
					cgstAmount = cgstAmount.add(taxes.getNetTax());
				} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_SGST)) {
					sgstAmount = sgstAmount.add(taxes.getNetTax());
				} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_IGST)) {
					igstAmount = igstAmount.add(taxes.getNetTax());
				} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_UGST)) {
					ugstAmount = ugstAmount.add(taxes.getNetTax());
				} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_CESS)) {
					cessAmount = cessAmount.add(taxes.getNetTax());
				}
			}
		}

		this.cgst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.cgst.setValue(PennantApplicationUtil.formateAmount(cgstAmount, formatter));

		this.sgst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.sgst.setValue(PennantApplicationUtil.formateAmount(sgstAmount, formatter));

		this.igst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.igst.setValue(PennantApplicationUtil.formateAmount(igstAmount, formatter));

		this.ugst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.ugst.setValue(PennantApplicationUtil.formateAmount(ugstAmount, formatter));

		this.cess.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.cess.setValue(PennantApplicationUtil.formateAmount(cessAmount, formatter));

		BigDecimal totalAmount = BigDecimal.ZERO;
		totalAmount = this.finFeeDetail.getNetAmountOriginal().add(cgstAmount).add(sgstAmount).add(igstAmount)
				.add(ugstAmount).add(cessAmount);

		this.total.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.total.setValue(PennantApplicationUtil.formateAmount(totalAmount, formatter));

		logger.debug("Leaving ");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		this.window_FinFeeGSTDetailsDialog.onClose();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinFeeDetail(FinFeeDetail finFeeDetail) {
		this.finFeeDetail = finFeeDetail;
	}
}
