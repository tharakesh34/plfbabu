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
package com.pennant.webui.reason.detail;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ReasonDetailsLogDialogCtrl extends GFCBaseCtrl<FinanceMain> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(ReasonDetailsLogDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReasonDetailsLogDialog;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox custDocType;
	protected Textbox custID;
	protected Textbox mobileNo;
	protected Textbox emailID;
	protected Listbox listReasonDetailsLog;
	protected Row row1;
	protected Row row2;
	protected Row row3;
	protected Row row4;

	private CustomerFinanceDetail customerFinanceDetail;
	private List<ReasonDetailsLog> reasonDetails;

	/**
	 * default constructor.<br>
	 */
	public ReasonDetailsLogDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "financeMain";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ReasonDetailsLogDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_ReasonDetailsLogDialog);

		try {
			if (arguments.containsKey("customerFinanceDetail")) {
				this.customerFinanceDetail = (CustomerFinanceDetail) arguments.get("customerFinanceDetail");
			} else {
				setCustomerFinanceDetail(null);
			}

			if (arguments.containsKey("reasonDetails")) {
				this.reasonDetails = (List<ReasonDetailsLog>) arguments.get("reasonDetails");
			}

			doShowDialog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			this.window_ReasonDetailsLogDialog.onClose();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 *
	 */
	public void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);
		if (customerFinanceDetail != null) {
			this.custCIF.setValue(customerFinanceDetail.getCustCIF());
			this.custShrtName.setValue(customerFinanceDetail.getCustShrtName());
			this.finReference.setValue(customerFinanceDetail.getFinReference());
			this.finBranch.setValue(customerFinanceDetail.getFinBranch());
			this.custID.setValue(StringUtils.trimToEmpty(customerFinanceDetail.getFinCcy()));
			this.custDocType.setValue(customerFinanceDetail.getFinTypeDesc());
			this.mobileNo.setValue(PennantApplicationUtil.amountFormate(
					customerFinanceDetail.getFinAmount().add(customerFinanceDetail.getFeeChargeAmt()),
					CurrencyUtil.getFormat(customerFinanceDetail.getFinCcy())));
			this.emailID.setValue(DateUtil.formatToLongDate(customerFinanceDetail.getFinStartDate()));
		} else {
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.row3.setVisible(false);
			this.row4.setVisible(false);
		}
		fillReasonDeatilsLog();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fill Reason Details List
	 * 
	 * @param reasonDetails
	 */
	private void fillReasonDeatilsLog() {

		this.listReasonDetailsLog.getItems().clear();
		if (this.reasonDetails == null) {
			return;
		}

		this.listReasonDetailsLog.setHeight(reasonDetails.size() * 26 + 100 + "px");
		Listitem item;

		for (ReasonDetailsLog detailsLog : reasonDetails) {
			item = new Listitem();
			Listcell lc;

			String date = "";
			if (detailsLog.getLogTime() != null) {
				date = DateUtil.formatToLongDate(detailsLog.getLogTime());
			}
			lc = new Listcell(date);
			lc.setParent(item);

			String logTime = "";
			if (detailsLog.getLogTime() != null) {
				logTime = DateUtil.format(detailsLog.getLogTime(), DateUtil.DateFormat.LONG_TIME.getPattern());
			}
			lc = new Listcell(logTime);
			lc.setParent(item);

			String roleCode = "";
			if (detailsLog.getRoleCode() != null) {
				roleCode = detailsLog.getRoleDesc();
			}
			lc = new Listcell(roleCode);
			lc.setParent(item);

			String toUser = "";
			if (StringUtils.trimToNull(detailsLog.getUsrFname()) != null) {
				toUser = toUser.concat(detailsLog.getUsrFname());
			}
			if (StringUtils.trimToNull(detailsLog.getUsrMname()) != null) {
				toUser = toUser.concat(" ").concat(toUser.concat(detailsLog.getUsrMname()));
			}
			if (StringUtils.trimToNull(detailsLog.getUsrLname()) != null) {
				toUser = toUser.concat(" ").concat(toUser.concat(detailsLog.getUsrLname()));
			}
			lc = new Listcell(toUser);
			lc.setParent(item);

			String toModule = "";
			if (detailsLog.getModule() != null) {
				toModule = detailsLog.getModule();
			}
			lc = new Listcell(toModule);
			lc.setParent(item);

			String activity = "";
			if (detailsLog.getActivity() != null) {
				activity = detailsLog.getActivity();
			}
			lc = new Listcell(activity);
			lc.setParent(item);

			String code = "";
			if (detailsLog.getCode() != null) {
				code = detailsLog.getCode();
			}
			lc = new Listcell(code);
			lc.setParent(item);

			String description = "";
			if (detailsLog.getDescription() != null) {
				description = detailsLog.getDescription();
			}
			lc = new Listcell(description);
			lc.setParent(item);

			String remarks = "";
			if (detailsLog.getRemarks() != null) {
				remarks = detailsLog.getRemarks();
			}
			lc = new Listcell(remarks);
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			this.listReasonDetailsLog.appendChild(item);
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug(Literal.ENTERING);
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents();
			// stores the initial data for comparing if they are changed
			// during user action.

			this.window_ReasonDetailsLogDialog.setWidth("70%");
			this.window_ReasonDetailsLogDialog.setHeight("70%");
			this.window_ReasonDetailsLogDialog.doModal();

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReasonDetailsLogDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.finReference.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDocType.setReadonly(true);
		this.custID.setReadonly(true);
		this.mobileNo.setReadonly(true);
		this.emailID.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		this.window_ReasonDetailsLogDialog.onClose();

		logger.debug(Literal.LEAVING + event.toString());
	}

	public CustomerFinanceDetail getCustomerFinanceDetail() {
		return customerFinanceDetail;
	}

	public void setCustomerFinanceDetail(CustomerFinanceDetail customerFinanceDetail) {
		this.customerFinanceDetail = customerFinanceDetail;
	}

}
