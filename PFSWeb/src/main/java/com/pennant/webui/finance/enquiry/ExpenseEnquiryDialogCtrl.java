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
 * * FileName : FeeEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FeeEnquiryDialogCtrl.zul file.
 */
public class ExpenseEnquiryDialogCtrl extends GFCBaseCtrl<FinExpenseDetails> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ExpenseEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExpenseEnquiryDialog;
	protected Listbox listBoxExpenseDetail;
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinExpenseDetails> finExpenseDetails;
	private UploadHeaderService uploadHeaderService;
	private Long finID;
	private int ccyFormatter = 0;

	/**
	 * default constructor.<br>
	 */
	public ExpenseEnquiryDialogCtrl() {
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
	public void onCreate$window_ExpenseEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExpenseEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finExpenseDetails")) {
			this.finExpenseDetails = (List<FinExpenseDetails>) arguments.get("finExpenseDetails");
		} else {
			this.finExpenseDetails = null;
		}

		if (arguments.containsKey("finID")) {
			this.finID = (Long) arguments.get("finID");
		}

		if (arguments.containsKey("ccyformat")) {
			this.ccyFormatter = (int) arguments.get("ccyformat");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillExpenseTypeList(this.finExpenseDetails);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxExpenseDetail.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_ExpenseEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_ExpenseEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doFillExpenseTypeList(List<FinExpenseDetails> expenseTypeDetails) {
		logger.debug(Literal.ENTERING);

		this.listBoxExpenseDetail.getItems().clear();

		if (expenseTypeDetails != null && !expenseTypeDetails.isEmpty()) {
			for (FinExpenseDetails detail : expenseTypeDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getExpenseTypeCode());
				lc.setParent(item);

				lc = new Listcell(detail.getExpenseTypeDesc());
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(detail.getAmount(), ccyFormatter));
				lc.setParent(item);

				lc = new Listcell(DateUtil.formatToLongDate(detail.getLastMntOn()));
				lc.setParent(item);

				Button b = new Button();
				lc = new Listcell();
				b.setLabel("View");
				b.addForward("onClick", self, "onViewClick", detail);
				lc.appendChild(b);
				lc.setParent(item);

				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceExpenseItemDoubleClicked");
				this.listBoxExpenseDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);

	}

	public void onViewClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		FinExpenseDetails finExpenseDetails = (FinExpenseDetails) event.getData();

		List<FinExpenseMovements> finExpenseMovements = getUploadHeaderService().getFinExpenseMovementById(finID,
				finExpenseDetails.getFinExpenseId());
		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("finExpenseDetails", finExpenseDetails);
		map.put("finExpenseMovements", finExpenseMovements);
		map.put("ccyFormatter", this.ccyFormatter);

		try {
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/ExpenseMovementDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	// setter and getters
	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}
