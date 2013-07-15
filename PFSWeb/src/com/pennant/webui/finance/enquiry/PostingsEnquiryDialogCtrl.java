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

package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsComparator;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PostingsEnquiryDialogCtrl extends GFCBaseListCtrl<ReturnDataSet> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(PostingsEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PostingsEnquiryDialog; 		// autoWired
	protected Listbox 		listBoxFinPostings;					// autoWired
	protected Label 		label_showAccruals;					// autoWired
	protected Label 		label_showZeroCals;					// autoWired
	protected Checkbox		showAccrual;						// autoWired
	protected Checkbox		showZeroCals;						// autoWired
	private Tabpanel 		tabPanel_dialogWindow;
	
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;

	private List<ReturnDataSet> postingDetails;
	private String finReference = "";
	private FinanceDetailService financeDetailService;

	/**
	 * default constructor.<br>
	 */
	public PostingsEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PostingsEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("finReference")) {
			this.finReference = (String) args.get("finReference");
		} 
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		} 
		
		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		
		try {
			
			//Fill Posting Details
			doFillPostings();
			
			if(tabPanel_dialogWindow != null){

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxFinPostings.setHeight(this.borderLayoutHeight-rowsHeight-90+"px");
				this.window_PostingsEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-45+"px");
				tabPanel_dialogWindow.appendChild(this.window_PostingsEnquiryDialog);

			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**============================================================*/
	/** 				Check Events For Postings				   */
	/**============================================================*/
	public void onCheck$showAccrual(Event event) throws Exception {
		logger.debug("Entering");
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
		logger.debug("Leaving");
	}

	public void onCheck$showZeroCals(Event event) throws Exception {
		logger.debug("Entering");
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of postings in Listbox
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void doFillPostings() {
		logger.debug("Entering");
		
		String events = "'ADDDBSF','ADDDBSN','ADDDBSP','COMPOUND','DEFFRQ','DEFRPY','DPRCIATE','EARLYPAY','EARLYSTL','LATEPAY','M_AMZ','M_NONAMZ','RATCHG','REPAY','SCDCHG','WRITEOFF','CMTDISB'";
		
		if(this.showAccrual.isChecked()) {
			events = "'ADDDBSF','ADDDBSN','ADDDBSP','AMZ','AMZSUSP','COMPOUND','DEFFRQ','DEFRPY','DPRCIATE','EARLYPAY','EARLYSTL','LATEPAY','M_AMZ','M_NONAMZ','RATCHG','REPAY','SCDCHG','WRITEOFF','CMTDISB'";
		}
		
		if(!events.equals("")) {
			postingDetails = getFinanceDetailService().getPostingsByFinRefAndEvent(finReference,
					events, this.showZeroCals.isChecked());
		}

		this.listBoxFinPostings.setModel(new GroupsModelArray(
				postingDetails.toArray(),new FinanceEnquiryPostingsComparator()));
		this.listBoxFinPostings.setItemRenderer(new FinanceEnquiryPostingsListItemRenderer(3));//TODO
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

}
