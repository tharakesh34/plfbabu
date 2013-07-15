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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DocumentEnquiryDialogCtrl extends GFCBaseListCtrl<FinAgreementDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(DocumentEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DocumentEnquiryDialog; 		// autoWired
	protected Listbox 		listBoxAgreement; 					// autoWired
	protected Listbox 		listBoxDocument; 					// autoWired
	protected Borderlayout  borderlayoutDocumentEnquiry;		// autoWired
	private Tabpanel 		tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinAgreementDetail> finAgreements;
	private List<DocumentDetails> finDocuments;
	
	private FinanceDetailService financeDetailService;
	
	/**
	 * default constructor.<br>
	 */
	public DocumentEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DocumentEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("finAgreements")) {
			this.finAgreements = (List<FinAgreementDetail>) args.get("finAgreements");
		}else{
			this.finAgreements = null;
		}
		if (args.containsKey("finDocuments")) {
			this.finDocuments = (List<DocumentDetails>) args.get("finDocuments");
		}else{
			this.finDocuments = null;
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
			
			// fill the components with the data
			doFillAgrList(this.finAgreements);
			doFillDocList(this.finDocuments);
			
			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxAgreement.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.listBoxDocument.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_DocumentEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_DocumentEnquiryDialog);

			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	/**
	 * Method to fill the Finance Agreement Details List
	 * @param agrDetails
	 */
	public void doFillAgrList(List<FinAgreementDetail> agrDetails) {
		logger.debug("Entering");
		Listitem listitem = null;
		Listcell lc = null;
		for (FinAgreementDetail agrDetail : agrDetails) {
			
			listitem = new Listitem();
			lc = new Listcell(String.valueOf(agrDetail.getAgrId()));
			listitem.appendChild(lc);
			
			lc = new Listcell(agrDetail.getLovDescAgrName() + " - " +agrDetail.getAgrName());
			listitem.appendChild(lc);
			
			lc = new Listcell();
			Button viewBtn = new Button("View");
			viewBtn.addForward("onClick",window_DocumentEnquiryDialog,"onAgrViewButtonClicked",agrDetail.getAgrId());
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);
			
			this.listBoxAgreement.appendChild(listitem);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill the Finance Document Details List
	 * @param docDetails
	 */
	public void doFillDocList(List<DocumentDetails> docDetails) {
		logger.debug("Entering");
		Listitem listitem = null;
		Listcell lc = null;
		for (DocumentDetails doc : docDetails) {
			
			listitem = new Listitem();
			lc = new Listcell(String.valueOf(doc.getDocId()));
			listitem.appendChild(lc);
			
			lc = new Listcell(doc.getDocCategory());
			listitem.appendChild(lc);
			
			lc = new Listcell(doc.getDoctype());
			listitem.appendChild(lc);
			
			lc = new Listcell(doc.getDocName());
			listitem.appendChild(lc);
			
			lc = new Listcell();
			Button viewBtn = new Button("View");
			viewBtn.addForward("onClick",window_DocumentEnquiryDialog,"onDocViewButtonClicked",doc.getDocId());
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);
			
			this.listBoxDocument.appendChild(listitem);
		}
		logger.debug("Leaving");
	}
	
	public void onAgrViewButtonClicked(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		
		long agrId  = Long.valueOf(event.getData().toString());
		FinAgreementDetail detail = getFinanceDetailService().getFinAgrDetailByAgrId(
				this.financeEnquiryHeaderDialogCtrl.finReference_header.getValue() , agrId);

		if(!StringUtils.trimToEmpty(detail.getAgrName()).equals("") && 
				!StringUtils.trimToEmpty(detail.getAgrContent().toString()).equals("")){

			try {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("FinAgreementDetail", detail);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
			} catch (Exception e) {
				logger.debug(e);
			}
		}else{
			PTMessageUtils.showErrorMessage("Agreement Details not Found.");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onDocViewButtonClicked(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		
		long docId  = Long.valueOf(event.getData().toString());
		DocumentDetails detail = getFinanceDetailService().getFinDocDetailByDocId(docId);

		if(!StringUtils.trimToEmpty(detail.getDocName()).equals("") && 
				!StringUtils.trimToEmpty(detail.getDocImage().toString()).equals("")){

			try {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("FinDocumentDetail", detail);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
			} catch (Exception e) {
				logger.debug(e);
			}
		}else{
			PTMessageUtils.showErrorMessage("Document Details not Found.");
		}
		logger.debug("Leaving" + event.toString());
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
