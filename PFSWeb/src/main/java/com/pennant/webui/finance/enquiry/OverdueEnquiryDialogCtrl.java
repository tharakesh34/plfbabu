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
package com.pennant.webui.finance.enquiry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class OverdueEnquiryDialogCtrl extends GFCBaseCtrl<FinODDetails> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(OverdueEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OverdueEnquiryDialog; // autoWired
	protected Listbox listBoxOverdue; // autoWired
	protected Listheader finCurODTxnChrg;
	protected Listheader finMaxODTxnChrg;
	protected Borderlayout borderlayoutOverdueEnquiry; // autoWired
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinODDetails> finODDetailList;
	private int ccyformat = 0;
	private String finReference = null;
	private FinanceMainDAO financeMainDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;

	private FinanceDetailService financeDetailService;
	protected boolean disbEnquiry = false;
	protected Combobox enquiryCombobox;
	protected Groupbox finBasicdetails;
	private ArrayList<Object> headerList;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	/**
	 * default constructor.<br>
	 */
	public OverdueEnquiryDialogCtrl() {
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
	public void onCreate$window_OverdueEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_OverdueEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("list")) {
			this.finODDetailList = (List<FinODDetails>) arguments.get("list");
		} else {
			this.finODDetailList = null;
		}

		if (arguments.containsKey("ccyformat")) {
			this.ccyformat = (Integer) arguments.get("ccyformat");
		}

		if (arguments.containsKey("FinReference")) {
			this.finReference = (String) arguments.get("FinReference");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		if (arguments.containsKey("disbEnquiry")) {
			disbEnquiry = (boolean) arguments.get("disbEnquiry");
		}

		if (arguments.containsKey("finHeaderList")) {
			headerList = (ArrayList<Object>) arguments.get("finHeaderList");
		}

		enquiryCombobox = (Combobox) arguments.get("enuiryCombobox");

		doShowDialog();

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

			// fill the components with the data
			doFillOverdueDetails(this.finODDetailList, this.finReference);

			if (tabPanel_dialogWindow != null) {
				this.btnClose.setVisible(false);
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxOverdue.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_OverdueEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_OverdueEnquiryDialog);

			} else if (disbEnquiry) {
				appendFinBasicDetails();
				setDialog(DialogType.MODAL);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Finance Document Details List
	 * 
	 * @param docDetails
	 */
	public void doFillOverdueDetails(List<FinODDetails> odDetails, String finReference) {
		this.listBoxOverdue.getItems().clear();

		if (odDetails == null) {
			return;
		}

		boolean isOverDraft = financeMainDAO.isOverDraft(finReference);
		int extnODGrcDays = 0;

		if (isOverDraft) {
			long finID = financeMainDAO.getFinID(finReference);
			extnODGrcDays = finODPenaltyRateDAO.getExtnODGrcDays(finID);
		}

		this.finCurODTxnChrg.setVisible(isOverDraft);
		this.finMaxODTxnChrg.setVisible(isOverDraft);

		for (FinODDetails od : odDetails) {
			Listitem item = new Listitem();
			Listcell lc;

			if (isOverDraft) {

				Date grcDate = od.getFinODSchdDate();
				if (od.isODIncGrcDays()) {
					grcDate = DateUtil.addDays(od.getFinODSchdDate(), od.getODGraceDays());
				} else {
					grcDate = DateUtil.addDays(od.getFinODSchdDate(), od.getODGraceDays() + extnODGrcDays);
				}
				lc = new Listcell(DateUtil.formatToLongDate(grcDate));
			} else {
				lc = new Listcell(DateUtil.formatToLongDate(od.getFinODSchdDate()));
			}

			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(od.getFinODTillDate()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinCurODAmt(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinCurODPri(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinCurODPft(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getCurOverdraftTxnChrg(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinMaxODAmt(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinMaxODPri(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getFinMaxODPft(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getMaxOverdraftTxnChrg(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getTotPenaltyAmt(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getTotWaived(), ccyformat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(od.getTotPenaltyPaid(), ccyformat));
			lc.setParent(item);
			this.listBoxOverdue.appendChild(item);
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			map.put("moduleName", moduleCode);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		if (disbEnquiry && enquiryCombobox != null) {
			this.enquiryCombobox.setSelectedIndex(0);
			this.window_OverdueEnquiryDialog.onClose();
		} else {
			doClose(false);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

}
