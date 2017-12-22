/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LoanDetailsEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.reason.detail;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ReasonDetailsLogDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	 
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ReasonDetailsLogDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_ReasonDetailsLogDialog;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox custDocType;
	protected Textbox custID;
	protected Textbox mobileNo;
	protected Textbox emailID;
	protected Listbox listReasonDetailsLog;
	
	private CustomerFinanceDetail customerFinanceDetail;
	private List<Map<String, Object>> reasonDetails;
	
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
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ReasonDetailsLogDialog(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_ReasonDetailsLogDialog);

		try {
			if (arguments.containsKey("customerFinanceDetail")) {
				this.customerFinanceDetail = (CustomerFinanceDetail) arguments.get("customerFinanceDetail");
			} else {
				setCustomerFinanceDetail(null);
			}
			
			if (arguments.containsKey("reasonDetails")) {
				this.reasonDetails = (List<Map<String, Object>>) arguments.get("reasonDetails");
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
	public void doWriteBeanToComponents() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.custCIF.setValue(customerFinanceDetail.getCustCIF());
		this.custShrtName.setValue(customerFinanceDetail.getCustShrtName());
		this.finReference.setValue(customerFinanceDetail.getFinReference());
		this.finBranch.setValue(customerFinanceDetail.getFinBranch());
		this.custID.setValue(StringUtils.trimToEmpty(customerFinanceDetail.getFinCcy()));
		this.custDocType.setValue(customerFinanceDetail.getFinTypeDesc());
		this.mobileNo.setValue(PennantApplicationUtil.amountFormate(
				customerFinanceDetail.getFinAmount().add(customerFinanceDetail.getFeeChargeAmt()),
				CurrencyUtil.getFormat(customerFinanceDetail.getFinCcy())));
		this.emailID.setValue(DateUtility.formatToLongDate(customerFinanceDetail.getFinStartDate()));
		fillReasonDeatilsLog(this.reasonDetails);
		logger.debug(Literal.LEAVING);
	}
	 
	
	/**
	 * Fill Reason Details List
	 * 
	 * @param reasonDetails
	 */
	private void fillReasonDeatilsLog(List<Map<String, Object>> reasonDetails) {

		Map<String, Object> map = null;
		this.listReasonDetailsLog.getItems().clear();
		this.listReasonDetailsLog.setHeight(reasonDetails.size() * 26 + 100 + "px");
		Listitem item;

		for (int i = 0; i < reasonDetails.size(); i++) {
			map = reasonDetails.get(i);
			item = new Listitem();
			Listcell lc;

			String logTime = "";
			Object objTime = map.get("Logtime");
			if (objTime != null) {
				logTime = DateUtility.format((Date) map.get("Logtime"),
						DateUtility.DateFormat.LONG_DATE_TIME.getPattern());
			}
			lc = new Listcell(logTime);
			lc.setParent(item);

			String roleCode = "";
			Object objRoleCode = map.get("Rolecode");
			if (objRoleCode != null) {
				roleCode = (String) map.get("Rolecode");
			}
			lc = new Listcell(roleCode);
			lc.setParent(item);

			String toUser = "";
			Object objToUser = map.get("Touser");
			if (objToUser != null) {
				toUser = String.valueOf(objToUser);
			}
			lc = new Listcell(toUser);
			lc.setParent(item);

			String toModule = "";
			Object objModule = map.get("Module");
			if (objModule != null) {
				toModule = (String) map.get("Module");
			}
			lc = new Listcell(toModule);
			lc.setParent(item);

			String activity = "";
			Object objActivity = map.get("Activity");
			if (objActivity != null) {
				activity = (String) map.get("Activity");
			}
			lc = new Listcell(activity);
			lc.setParent(item);

			String code = "";
			Object objCode = map.get("Code");
			if (objCode != null) {
				code = (String) map.get("Code");
			}
			lc = new Listcell(code);
			lc.setParent(item);

			String description = "";
			Object objDescription = map.get("Description");
			if (objDescription != null) {
				description = (String) map.get("Description");
			}
			lc = new Listcell(description);
			lc.setParent(item);

			String remarks = "";
			Object objRemarks = map.get("Remarks");
			if (objRemarks != null) {
				remarks = (String) map.get("Remarks");
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
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
		} catch (Exception e) {
			throw e;
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
