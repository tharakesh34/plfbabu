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
 * * FileName : ExcessEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-08-2019 * *
 * Modified Date : 08-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-08-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/ExcessEnquiryDialog.zul file.
 */
public class ExcessEnquiryDialogCtrl extends GFCBaseCtrl<FinExcessAmount> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ExcessEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExcessEnquiryDialog;
	protected Listbox listBoxExcess;
	protected Borderlayout borderlayoutExcessEnquiry;
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinExcessAmount> excessAmtList;
	private int ccyFormatter = 0;

	/**
	 * default constructor.<br>
	 */
	public ExcessEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExcessEnquiryDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExcessEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("excessDetails")) {
			this.excessAmtList = (List<FinExcessAmount>) arguments.get("excessDetails");
		} else {
			this.excessAmtList = null;
		}
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (int) arguments.get("ccyFormatter");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}
		doShowDialog();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 */
	public void doShowDialog() {
		logger.debug(Literal.ENTERING);
		try {
			// fill the components with the data
			doFillExcessList(this.excessAmtList);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxExcess.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_ExcessEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");
				tabPanel_dialogWindow.appendChild(this.window_ExcessEnquiryDialog);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill the Finance Document Details List
	 * 
	 * @param docDetails
	 */
	public void doFillExcessList(List<FinExcessAmount> excessAmtList) {
		logger.debug(Literal.ENTERING);
		Listitem item = null;
		for (FinExcessAmount excessAmt : excessAmtList) {
			item = new Listitem();
			Listcell lc;
			String amountType = excessAmt.getAmountType();
			switch (ExcessType.valueOf(amountType)) {
			case ADVEMI:
				lc = new Listcell(PennantJavaUtil.getLabel("label_ReceiptPaymentMode_ADVEMI"));
				lc.setParent(item);
				break;
			case ADVINT:
				lc = new Listcell(PennantJavaUtil.getLabel("label_ReceiptPaymentMode_ADVINT"));
				lc.setParent(item);
				break;
			case DSF:
				lc = new Listcell(PennantJavaUtil.getLabel("label_DSF"));
				lc.setParent(item);
				break;
			case CASHCLT:
				lc = new Listcell(PennantJavaUtil.getLabel("label_CASHCLT"));
				lc.setParent(item);
				break;
			case E:
				lc = new Listcell(PennantJavaUtil.getLabel("label_Excess"));
				lc.setParent(item);
				break;
			case A:
				lc = new Listcell(PennantJavaUtil.getLabel("label_EMI_Advance"));
				lc.setParent(item);
				break;
			}

			lc = new Listcell(CurrencyUtil.format(excessAmt.getAmount(), ccyFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(excessAmt.getUtilisedAmt(), ccyFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(excessAmt.getReservedAmt(), ccyFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(excessAmt.getBalanceAmt(), ccyFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxExcess.appendChild(item);
		}
		logger.debug(Literal.LEAVING);
	}

	// Excess Amount Types Enum
	public enum ExcessType {
		ADVEMI, ADVINT, DSF, CASHCLT, E, A
	}
}
