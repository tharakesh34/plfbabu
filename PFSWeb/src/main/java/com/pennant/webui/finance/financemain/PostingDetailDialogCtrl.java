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
 * * FileName : LoanDetailsEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class PostingDetailDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(PostingDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PostingDetailDialog;
	protected Listbox listBoxPosting;
	protected Checkbox showZeroCals;

	private List<ReturnDataSet> postingDetails;

	/**
	 * default constructor.<br>
	 */
	public PostingDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_PostingDetailDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PostingDetailDialog);

		try {

			if (arguments.containsKey("postingDetails")) {
				this.postingDetails = (List<ReturnDataSet>) arguments.get("postingDetails");
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PostingDetailDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");

		try {

			// Fill Posting Details
			doFillPostings();

			getBorderLayoutHeight();
			this.listBoxPosting.setHeight((this.borderLayoutHeight - 100) + "px");
			this.window_PostingDetailDialog.setHeight((this.borderLayoutHeight - 80) + "px");
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCheck$showZeroCals(Event event) {
		logger.debug("Entering");
		doFillPostings();
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of postings in Listbox
	 */
	private void doFillPostings() {
		logger.debug("Entering");
		this.listBoxPosting.getItems().clear();
		if (postingDetails != null && !postingDetails.isEmpty()) {
			Listitem item;
			for (ReturnDataSet returnDataSet : postingDetails) {

				if (!this.showZeroCals.isChecked()) {
					if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}
				}
				item = new Listitem();
				Listcell lc = new Listcell(PennantApplicationUtil.getLabelDesc(returnDataSet.getDrOrCr(),
						PennantStaticListUtil.getTranType()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(returnDataSet.getPostAmount(),
						CurrencyUtil.getFormat(returnDataSet.getAcCcy())));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				this.listBoxPosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "BtnPrintPostings" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintPostings(Event event) {
		logger.debug("Entering" + event.toString());
		String usrName = getUserWorkspace().getLoggedInUser().getUserName();
		List<Object> list = null;

		list = new ArrayList<Object>();
		List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
		for (ReturnDataSet dataSet : postingDetails) {
			TransactionDetail detail = new TransactionDetail();
			detail.setEventCode(dataSet.getFinEvent());
			detail.setEventDesc(dataSet.getLovDescEventCodeName());
			detail.setTranType("C".equals(dataSet.getDrOrCr()) ? "Credit" : "Debit");
			detail.setTransactionCode(dataSet.getTranCode());
			detail.setTransDesc(dataSet.getTranDesc());
			detail.setCcy(dataSet.getAcCcy());
			detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			detail.setPostAmount(
					CurrencyUtil.format(dataSet.getPostAmount(), CurrencyUtil.getFormat(dataSet.getAcCcy())));
			detail.setRevTranCode(dataSet.getRevTranCode());
			detail.setPostDate(DateUtil.format(dataSet.getPostDate(), DateFormat.LONG_DATE.getPattern()));
			detail.setValueDate(DateUtil.format(dataSet.getValueDate(), DateFormat.LONG_DATE.getPattern()));
			accountingDetails.add(detail);
		}

		Window window = (Window) this.window_PostingDetailDialog.getParent().getParent().getParent().getParent()
				.getParent().getParent().getParent();
		if (!accountingDetails.isEmpty()) {
			list.add(accountingDetails);
		}

		ReportsUtil.generatePDF("FINENQ_AccountingDetail", true, list, usrName, window);
		logger.debug("Leaving" + event.toString());
	}

}
