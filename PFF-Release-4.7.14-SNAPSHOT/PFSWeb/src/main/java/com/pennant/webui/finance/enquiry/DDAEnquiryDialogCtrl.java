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
 * FileName    		:  ScheduleEnquiryDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class DDAEnquiryDialogCtrl extends GFCBaseCtrl<DDAProcessData> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(DDAEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DDAEnquiryDialog; 
	protected Listbox listBoxDDA; 
	protected Borderlayout borderlayoutDDAEnquiry; 
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<DDAProcessData> finDocuments;

	private FinanceDetailService financeDetailService;
	private AgreementDetailService agreementDetailService;

	/**
	 * default constructor.<br>
	 */
	public DDAEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
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
	public void onCreate$window_DDAEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DDAEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("list")) {
			this.finDocuments = (List<DDAProcessData>) arguments.get("list");
		} else {
			this.finDocuments = null;
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillDocList(this.finDocuments);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxDDA.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_DDAEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_DDAEnquiryDialog);

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
	public void doFillDocList(List<DDAProcessData> docDetails) {
		logger.debug("Entering");

		Listitem listitem = null;
		Listcell lc = null;
		for (DDAProcessData doc : docDetails) {

			listitem = new Listitem();
			lc = new Listcell(doc.getPurpose());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getCustCIF());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getCustomerName());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getBankName());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getAccountType());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getIban());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getMobileNum());
			listitem.appendChild(lc);

			lc = new Listcell(String.valueOf(doc.getAllowedInstances()));
			listitem.appendChild(lc);

			String status = doc.getDdaAckStatus();

			if (doc.getPurpose().equals(PennantConstants.REQ_TYPE_VALIDATE)) {
				status = PennantConstants.POSTSTS_SUCCESS;
			} else if (doc.getPurpose().equals(PennantConstants.REQ_TYPE_REG)) {
				if (doc.getDdaAckStatus() == null) {
					status = PennantConstants.DDA_PENDING;
				} else if ("".equals(doc.getDdaAckStatus())) {
					status = PennantConstants.POSTSTS_SUCCESS;
				} else {
					status = PennantConstants.DDA_REJECTED;
				}
			}

			lc = new Listcell(status);
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDdaReference());
			listitem.appendChild(lc);

			this.listBoxDDA.appendChild(listitem);
		}
		logger.debug("Leaving");
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

	public void setAgreementDetailService(AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
	}

	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}

}
