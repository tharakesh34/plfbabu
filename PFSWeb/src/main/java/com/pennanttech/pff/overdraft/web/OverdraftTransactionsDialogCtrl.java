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
 * * FileName : ScheduleEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pff.overdraft.web;

import java.math.BigDecimal;
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

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class OverdraftTransactionsDialogCtrl extends GFCBaseCtrl<OverdraftLimitTransation> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(OverdraftTransactionsDialogCtrl.class);

	protected Window window_overdraftTransactionsDialog;
	protected Listbox listBoxODLimitDetails;
	protected Borderlayout borderlayoutODLimitEnquiry;
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<OverdraftLimitTransation> trsnsactions;
	private int ccyformat = 0;

	/**
	 * default constructor.<br>
	 */
	public OverdraftTransactionsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_overdraftTransactionsDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_overdraftTransactionsDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("trsnsactions")) {
			this.trsnsactions = (List<OverdraftLimitTransation>) arguments.get("trsnsactions");
		} else {
			this.trsnsactions = null;
		}

		if (arguments.containsKey("ccyformat")) {
			this.ccyformat = (Integer) arguments.get("ccyformat");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillODLimitDetails(this.trsnsactions);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxODLimitDetails.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_overdraftTransactionsDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_overdraftTransactionsDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void doFillODLimitDetails(List<OverdraftLimitTransation> transactions) {
		this.listBoxODLimitDetails.getItems().clear();

		if (transactions == null) {
			return;
		}

		for (OverdraftLimitTransation trnsaction : transactions) {
			Listitem item = new Listitem();
			Listcell lc;
			String transactionType = trnsaction.getTxnType();

			String txnDate = "";
			String narration = "";
			BigDecimal transactionAmt = BigDecimal.ZERO;
			BigDecimal txnChrg = BigDecimal.ZERO;
			BigDecimal monthlyLmtBal = BigDecimal.ZERO;
			BigDecimal actualLmtBal = BigDecimal.ZERO;

			if (OverdraftConstants.TRANS_TYPE_ADD_DISB.equals(transactionType)) {
				txnDate = DateUtil.formatToLongDate(trnsaction.getValueDate());
				narration = trnsaction.getNarration();
				transactionAmt = trnsaction.getTxnAmount();
				txnChrg = trnsaction.getTxnCharge();
				monthlyLmtBal = trnsaction.getMonthlyLimitBal();
				actualLmtBal = trnsaction.getActualLimitBal();
			} else if (OverdraftConstants.TRANS_TYPE_LOAN_ORG.equals(transactionType)) {
				txnDate = DateUtil.formatToLongDate(trnsaction.getValueDate());
				narration = trnsaction.getNarration();
				actualLmtBal = trnsaction.getActualLimitBal();
			} else if (OverdraftConstants.TRANS_TYPE_EOM.equals(transactionType)) {
				txnDate = DateUtil.formatToLongDate(trnsaction.getValueDate());
				narration = trnsaction.getNarration();
				monthlyLmtBal = trnsaction.getMonthlyLimitBal();
				actualLmtBal = trnsaction.getActualLimitBal();
			} else {
				txnDate = DateUtil.formatToLongDate(trnsaction.getValueDate());
				narration = trnsaction.getNarration();
				transactionAmt = trnsaction.getTxnAmount();
				monthlyLmtBal = trnsaction.getMonthlyLimitBal();
				actualLmtBal = trnsaction.getActualLimitBal();
			}

			lc = new Listcell(txnDate);
			lc.setParent(item);
			lc = new Listcell(narration);
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(transactionAmt, ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(txnChrg, ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(monthlyLmtBal, ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(actualLmtBal, ccyformat));
			lc.setParent(item);

			this.listBoxODLimitDetails.appendChild(item);
		}
	}

}
