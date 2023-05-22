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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class CovenantEnquiryDialogCtrl extends GFCBaseCtrl<FinAgreementDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(CovenantEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CovenantEnquiryDialog;
	protected Listbox listBoxFinCovenantType;
	protected Borderlayout borderlayoutDocumentEnquiry;
	private Tabpanel tabPanel_dialogWindow;
	private SecurityRoleService securityRoleService;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinCovenantType> finCovenants;

	private FinanceDetailService financeDetailService;

	/**
	 * default constructor.<br>
	 */
	public CovenantEnquiryDialogCtrl() {
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
	public void onCreate$window_CovenantEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CovenantEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finCovenants")) {
			this.finCovenants = (List<FinCovenantType>) arguments.get("finCovenants");
		} else {
			this.finCovenants = null;
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
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillCovenantList(this.finCovenants);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxFinCovenantType.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_CovenantEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_CovenantEnquiryDialog);

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
	public void doFillCovenantList(List<FinCovenantType> finCovenantTypeDetails) {
		logger.debug("Entering");
		this.listBoxFinCovenantType.getItems().clear();
		if (finCovenantTypeDetails != null && !finCovenantTypeDetails.isEmpty()) {
			for (FinCovenantType detail : finCovenantTypeDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getCovenantTypeDesc());
				lc.setParent(item);
				List<SecurityRole> securityRoles = securityRoleService.getSecRoleCodeDesc(detail.getMandRole());
				lc = new Listcell(securityRoles.size() > 0 ? securityRoles.get(0).getRoleDesc() : "");
				lc.setParent(item);
				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwPostpone());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwOtc());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(detail.getReceivableDate(), DateFormat.LONG_DATE.getPattern()));
				lc.setParent(item);
				lc = new Listcell(DateUtil.format(detail.getDocReceivedDate(), DateFormat.LONG_DATE.getPattern()));
				lc.setParent(item);
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCovenantTypeItemDoubleClicked");
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	public void onFinCovenantTypeItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinCovenantType aFinCovenantType = (FinCovenantType) listitem.getAttribute("data");

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finCovenantTypes", aFinCovenantType);
			map.put("enqModule", true);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}

		logger.debug("Leaving" + event.toString());
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

	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}

}
