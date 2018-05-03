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
 * FileName    		:  ProvisionMovementListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.suspense;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.financemanagement.suspense.model.SuspenseDetailListModelItemRenderer;
import com.pennant.webui.reports.model.LoanEnquiryPostingsComparator;
import com.pennant.webui.reports.model.LoanEnquiryPostingsListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Provision/ProvisionMovement/ProvisionMovementList.zul file.
 */
public class SuspenseDetailEnquiryDialogCtrl extends GFCBaseCtrl<FinanceSuspDetails> {
	private static final long serialVersionUID = -1620412127444337321L;
	private static final Logger logger = Logger.getLogger(SuspenseDetailEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_SuspenseEnquiryDialog; 			// autowired

	protected Listbox 		listBoxSuspDetails; 				// autowired
	protected Listbox 		listBoxSuspPostings; 				// autowired
	protected Grid 			grid_Basicdetails;					// autowired
	protected Div 			div_toolbar;						// autowired

	protected Textbox 		finReference;			// autowired
	protected Textbox 		finBranch; 				// autowired
	protected Textbox 		finType; 				// autowired
	protected Longbox 		custID; 				// autowired
	protected Textbox 		lovDescCustCIF; 		// autowired
	protected Label   		custShrtName;			// autowired
	protected Intbox   		finSuspSeq;				// autowired
	protected Checkbox 		finIsInSusp; 			// autowired
	protected Checkbox 		manualSusp; 			// autowired
	protected Decimalbox 	finSuspAmt; 			// autowired
	protected Decimalbox 	finCurSuspAmt; 			// autowired
	protected Datebox 		finSuspDate; 			// autowired
	protected Datebox 		finSuspTrfDate; 		// autowired
	private Tabpanel 		tabPanel_dialogWindow;

	// checkRights
	protected Button btnHelp; 		// autowired

	// NEEDED for the ReUse in the SearchWindow
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private transient FinanceSuspHead suspHead = null;
	private SuspenseListCtrl suspenseListCtrl;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public SuspenseDetailEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SuspenseEnquiry";
	}

	// Component Events

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ProvisionMovement object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SuspenseEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SuspenseEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget()
						.getParent().getParent();
			}

			if (arguments.containsKey("suspHead")) {
				this.setSuspHead((FinanceSuspHead) arguments.get("suspHead"));
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			if (arguments.containsKey("suspenseListCtrl")) {
				setSuspenseListCtrl((SuspenseListCtrl) arguments
						.get("suspenseListCtrl"));
			} else {
				setSuspenseListCtrl(null);
			}

			doShowDialog(getSuspHead());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SuspenseEnquiryDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSuspHead
	 * @throws Exception
	 */
	public void doShowDialog(FinanceSuspHead aSuspHead) throws Exception {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSuspHead != null) {
			setSuspHead(aSuspHead);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSuspHead);

			// stores the initial data for comparing if they are changed
			// during user action.

			if(tabPanel_dialogWindow != null){
				
				this.div_toolbar.setVisible(false);
				this.window_SuspenseEnquiryDialog.setBorder("none");
				this.window_SuspenseEnquiryDialog.setTitle("");

				getBorderLayoutHeight();
				int headerRowHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				int rowsHeight = headerRowHeight + this.grid_Basicdetails.getRows().getVisibleItemCount()*20 + 100;
				this.listBoxSuspDetails.setHeight(this.borderLayoutHeight-rowsHeight + 20 +"px");
				this.listBoxSuspPostings.setHeight(this.borderLayoutHeight-rowsHeight -10+"px");
				this.window_SuspenseEnquiryDialog.setHeight(this.borderLayoutHeight-headerRowHeight+"px");
				tabPanel_dialogWindow.appendChild(this.window_SuspenseEnquiryDialog);

			}else{
				setDialog(DialogType.EMBEDDED);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SuspenseEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param FinanceSuspHead
	 *            aSuspHead
	 */
	public void doWriteBeanToComponents(FinanceSuspHead aSuspHead) {
		logger.debug("Entering") ;

		if(aSuspHead != null){
			if(tabPanel_dialogWindow == null){

				this.finReference.setValue(aSuspHead.getFinReference());
				this.finBranch.setValue(aSuspHead.getFinBranch());
				this.finType.setValue(aSuspHead.getFinType());
				this.custID.setValue(aSuspHead.getCustId());
				this.lovDescCustCIF.setValue(aSuspHead.getLovDescCustCIFName());
				this.custShrtName.setValue(aSuspHead.getLovDescCustShrtName());
				this.finSuspSeq.setValue(aSuspHead.getFinSuspSeq());

			}

			int format = CurrencyUtil.getFormat(aSuspHead.getFinCcy());
			this.finIsInSusp.setChecked(aSuspHead.isFinIsInSusp());
			this.manualSusp.setChecked(aSuspHead.isManualSusp());
			this.finSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinSuspAmt(),
					format));
			this.finCurSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinCurSuspAmt(),
					format));
			this.finSuspDate.setValue(aSuspHead.getFinSuspDate());
			this.finSuspTrfDate.setValue(aSuspHead.getFinSuspTrfDate());

			//Suspense Details List
			doFilllistbox(aSuspHead.getSuspDetailsList(), format);
			doFillPostingslistbox(aSuspHead.getSuspPostingsList(), format);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<FinanceSuspDetails> financeSuspDetails,int formatter) {
		logger.debug("Entering");
		if (financeSuspDetails != null) {
			getPagedListWrapper().initList(financeSuspDetails, 
					this.listBoxSuspDetails, new Paging());
			this.listBoxSuspDetails.setItemRenderer(new SuspenseDetailListModelItemRenderer(formatter));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doFillPostingslistbox(List<ReturnDataSet> financeSuspPostings,int formatter) {
		logger.debug("Entering");
		this.listBoxSuspPostings.setModel(new GroupsModelArray(
				financeSuspPostings.toArray(),new LoanEnquiryPostingsComparator()));
		this.listBoxSuspPostings.setItemRenderer(new LoanEnquiryPostingsListItemRenderer(formatter));
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			if(tabPanel_dialogWindow == null){
				closeDialog();
			}
		} catch (final WrongValuesException e) {
			logger.debug(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_SuspenseEnquiryDialog);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setSuspHead(FinanceSuspHead suspHead) {
		this.suspHead = suspHead;
	}
	public FinanceSuspHead getSuspHead() {
		return suspHead;
	}

	public void setSuspenseListCtrl(SuspenseListCtrl suspenseListCtrl) {
		this.suspenseListCtrl = suspenseListCtrl;
	}
	public SuspenseListCtrl getSuspenseListCtrl() {
		return suspenseListCtrl;
	}
}