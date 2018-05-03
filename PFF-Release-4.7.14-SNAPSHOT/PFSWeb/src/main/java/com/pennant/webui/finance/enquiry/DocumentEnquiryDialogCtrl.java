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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class DocumentEnquiryDialogCtrl extends GFCBaseCtrl<FinAgreementDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(DocumentEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_DocumentEnquiryDialog; 		
	protected Listbox 		listBoxDocument; 					
	protected Borderlayout  borderlayoutDocumentEnquiry;		
	private Tabpanel 		tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<DocumentDetails> finDocuments;
	
	private FinanceDetailService financeDetailService;
	private AgreementDetailService agreementDetailService;
	
	/**
	 * default constructor.<br>
	 */
	public DocumentEnquiryDialogCtrl() {
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
	public void onCreate$window_DocumentEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DocumentEnquiryDialog);

		if(event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finDocuments")) {
			this.finDocuments = (List<DocumentDetails>) arguments.get("finDocuments");
		}else{
			this.finDocuments = null;
		}
		
		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
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
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			
			// fill the components with the data
			doFillDocList(this.finDocuments);
			
			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxDocument.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_DocumentEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_DocumentEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Finance Document Details List
	 * @param docDetails
	 */
	public void doFillDocList(List<DocumentDetails> docDetails) {
		logger.debug("Entering");
		
		List<ValueLabel> list = PennantAppUtil.getDocumentTypes();
		Listitem listitem = null;
		Listcell lc = null;
		for (DocumentDetails doc : docDetails) {
			
			listitem = new Listitem();
			lc = new Listcell(String.valueOf(doc.getDocId()));
			listitem.appendChild(lc);
			
			lc = new Listcell(PennantAppUtil.getlabelDesc(doc.getDocCategory(), list));
			listitem.appendChild(lc);
			
			lc = new Listcell(doc.getDoctype());
			listitem.appendChild(lc);
			
			lc = new Listcell(doc.getDocName());
			listitem.appendChild(lc);
			
			lc = new Listcell();
			Button viewBtn = new Button("View");
			if (StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
				viewBtn.setLabel("Download");
			}
			viewBtn.addForward("onClick",window_DocumentEnquiryDialog,"onDocViewButtonClicked",doc.getDocId());
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);
			
			this.listBoxDocument.appendChild(listitem);
		}
		logger.debug("Leaving");
	}
	
	public void onDocViewButtonClicked(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		
		long docId  = Long.parseLong(event.getData().toString());
		DocumentDetails detail = getFinanceDetailService().getFinDocDetailByDocId(docId, "_View", true);

		if (StringUtils.isNotBlank(detail.getDocName()) && detail.getDocImage() != null
				&& StringUtils.isNotBlank(detail.getDocImage().toString())) {
			try {
				if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
					Filedownload.save(detail.getDocImage(), "application/msword", detail.getDocName());
				} else {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("FinDocumentDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		} else if (StringUtils.isNotBlank(detail.getDocUri())) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("documentRef", detail);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
		} else {
			MessageUtil.showError("Document Details not Found.");
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

	public void setAgreementDetailService(AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
	}

	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}
	
}
