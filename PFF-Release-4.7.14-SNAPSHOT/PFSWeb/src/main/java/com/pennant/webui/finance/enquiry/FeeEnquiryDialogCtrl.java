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
 * FileName    		:  FeeEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.webui.finance.enquiry.model.FeeEnquiryComparator;
import com.pennant.webui.finance.enquiry.model.FeeEnquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FeeEnquiryDialogCtrl.zul file.
 */
public class FeeEnquiryDialogCtrl extends GFCBaseCtrl<FinFeeDetail> {
	private static final long				serialVersionUID				= 3184249234920071313L;
	private static final Logger				logger							= Logger.getLogger(FeeEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_FinFeeEnquiryDialog;
	protected Listbox						listBoxFeeDetail;
	protected Borderlayout					borderlayoutFinFeeEnquiry;
	private Tabpanel						tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl	financeEnquiryHeaderDialogCtrl	= null;
	private List<FinFeeDetail>				finFeeDetails;
	private int								ccyFormatter					= 0;

	/**
	 * default constructor.<br>
	 */
	public FeeEnquiryDialogCtrl() {
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinFeeEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinFeeEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("feeDetails")) {
			this.finFeeDetails = (List<FinFeeDetail>) arguments.get("feeDetails");
		} else {
			this.finFeeDetails = null;
		}
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (int) arguments.get("ccyFormatter");
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
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			// fill the components with the data
			if (finFeeDetails != null) {
				this.listBoxFeeDetail
						.setModel(new GroupsModelArray(finFeeDetails.toArray(), new FeeEnquiryComparator()));
				this.listBoxFeeDetail.setItemRenderer(new FeeEnquiryListModelItemRenderer(ccyFormatter));
			}

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxFeeDetail.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_FinFeeEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_FinFeeEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

}
