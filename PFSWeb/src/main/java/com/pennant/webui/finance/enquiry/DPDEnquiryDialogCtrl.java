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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FeeEnquiryDialogCtrl.zul file.
 */
public class DPDEnquiryDialogCtrl extends GFCBaseCtrl<FinExpenseDetails> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ExpenseEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DPDEnquiryDialog;
	protected Listbox listBoxDPDDetail;
	private Tabpanel tabPanel_DPDEnquiry;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinStatusDetail> finStatusDetails;

	/**
	 * default constructor.<br>
	 */
	public DPDEnquiryDialogCtrl() {
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
	public void onCreate$window_DPDEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DPDEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_DPDEnquiry = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finStatusDetails")) {
			this.finStatusDetails = (List<FinStatusDetail>) arguments.get("finStatusDetails");
		} else {
			this.finStatusDetails = null;
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
			doFillFinStatusList(this.finStatusDetails);

			if (tabPanel_DPDEnquiry != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxDPDDetail.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				this.window_DPDEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_DPDEnquiry.appendChild(this.window_DPDEnquiryDialog);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doFillFinStatusList(List<FinStatusDetail> finStatusDetails) {
		logger.debug(Literal.ENTERING);

		this.listBoxDPDDetail.getItems().clear();

		if (finStatusDetails != null && !finStatusDetails.isEmpty()) {
			for (FinStatusDetail detail : finStatusDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(DateUtil.formatToLongDate(detail.getValueDate()));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(detail.getODDays()));
				lc.setParent(item);
				this.listBoxDPDDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);

	}

}
