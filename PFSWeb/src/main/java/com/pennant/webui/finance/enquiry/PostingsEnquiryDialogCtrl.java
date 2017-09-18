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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsComparator;
import com.pennant.webui.finance.enquiry.model.FinanceEnquiryPostingsListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class PostingsEnquiryDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(PostingsEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_PostingsEnquiryDialog; 		
	protected Listbox 		listBoxFinPostings;					
	protected Label 		label_showAccruals;					
	protected Label 		label_showZeroCals;					
	protected Checkbox		showAccrual;						
	protected Checkbox		showZeroCals;						
	private Tabpanel 		tabPanel_dialogWindow;
	protected Combobox      postingGroup; 						

	
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;

	private List<ReturnDataSet> postingDetails;
	private String finReference = "";
	private FinanceDetailService financeDetailService;
	private FinanceEnquiry enquiry;
	StringBuilder accEvents = new StringBuilder("");

	/**
	 * default constructor.<br>
	 */
	public PostingsEnquiryDialogCtrl() {
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
	 * @throws Exception
	 */
	public void onCreate$window_PostingsEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PostingsEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget()
						.getParent().getParent();
			}

			if (arguments.containsKey("finReference")) {
				this.finReference = (String) arguments.get("finReference");
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("enquiry")) {
				this.enquiry = (FinanceEnquiry) arguments.get("enquiry");
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PostingsEnquiryDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		
		try {
			doCheckEnquiry();
			//Fill Posting Details
			this.showAccrual.setChecked(true);
			doFillPostings();
			
			if(tabPanel_dialogWindow != null){

				getBorderLayoutHeight();
				int rowsHeight = (financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20)+1;
				this.listBoxFinPostings.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_PostingsEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight+"px");
				tabPanel_dialogWindow.appendChild(this.window_PostingsEnquiryDialog);

			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PostingsEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(this.enquiry!=null){
			//this.showAccrual.setDisabled(true);
			//this.showZeroCals.setDisabled(true);
		}
		
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
	private void doFillPostings() {
		logger.debug("Entering");
		fillComboBox(this.postingGroup, PennantConstants.EVENTBASE, PennantStaticListUtil.getPostingGroupList(), "");
		StringBuilder events = new StringBuilder("'ADDDBSF','ADDDBSN','ADDDBSP','COMPOUND','DEFFRQ','DEFRPY','DPRCIATE','EARLYPAY','EARLYSTL','LATEPAY','PIS_NORM','NORM_PIS','RATCHG','REPAY','SCDCHG','WRITEOFF','CMTDISB', 'STAGE', 'ISTBILL', 'GRACEEND','DISBINS','FEEPAY','VASFEE','MANFEE','INSTDATE','PAYMTINS', 'REAGING','JVPOST'");
		
		if(this.showAccrual.isChecked()) {
			events.append(",'AMZ','AMZSUSP'");
		}
		accEvents=events;
		if(StringUtils.isNotEmpty(events.toString())) {
			postingDetails = getFinanceDetailService().getPostingsByFinRefAndEvent(finReference,
					events.toString(), this.showZeroCals.isChecked(),"");
		}
		doGetListItemRenderer(postingDetails);
		logger.debug("Leaving");
	}
	
	public void onSelect$postingGroup(Event event) {
		logger.debug("Entering" + event.toString());
		List<ReturnDataSet> postingList= new ArrayList<>();
		postingDetails = getFinanceDetailService().getPostingsByFinRefAndEvent(finReference,
				accEvents.toString(), this.showZeroCals.isChecked(),this.postingGroup.getSelectedItem().getValue().toString());
		logger.debug("Leaving" + event.toString());
		for (ReturnDataSet returnDataSet : postingDetails) {
			returnDataSet.setPostingGroupBy(this.postingGroup.getSelectedItem().getValue().toString());
		}
		postingList.addAll(postingDetails);
		doGetListItemRenderer(postingList);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doGetListItemRenderer(List<ReturnDataSet> postingDetails){
		this.listBoxFinPostings.setModel(new GroupsModelArray(
				postingDetails.toArray(),new FinanceEnquiryPostingsComparator()));
		this.listBoxFinPostings.setItemRenderer(new FinanceEnquiryPostingsListItemRenderer());
		logger.debug("Leaving");
	}
	/**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintAccounting(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		String       usrName     = getUserWorkspace().getLoggedInUser().getUserName();
		List<Object> list        = null;
		
		list = new ArrayList<Object>();
		List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
		for (ReturnDataSet dataSet : postingDetails) {
			TransactionDetail detail = new TransactionDetail();
			detail.setEventCode(dataSet.getFinEvent());
			detail.setEventDesc(dataSet.getLovDescEventCodeName());
			detail.setTranType("C".equals(dataSet.getDrOrCr()) ? "Credit" : "Debit");
			detail.setTransactionCode(dataSet.getTranCode());
			detail.setTransDesc(dataSet.getTranDesc());
			detail.setCcy(dataSet.getAcCcy());
			detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			detail.setPostAmount(PennantAppUtil.amountFormate(dataSet.getPostAmount(), CurrencyUtil.getFormat(dataSet.getAcCcy())));
			detail.setRevTranCode(dataSet.getRevTranCode());
			detail.setPostDate(DateUtility.formatDate(dataSet.getPostDate(), DateFormat.LONG_DATE.getPattern()));
			detail.setValueDate(DateUtility.formatDate(dataSet.getValueDate(), DateFormat.LONG_DATE.getPattern()));
			accountingDetails.add(detail);
		}

		Window window= (Window) this.window_PostingsEnquiryDialog.getParent().getParent().getParent().getParent().getParent().getParent();
		if(!accountingDetails.isEmpty()){
			list.add(accountingDetails);
		}

		ReportGenerationUtil.generateReport("FINENQ_AccountingDetail",enquiry, 
				list, true, 1, usrName,window);
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

}
