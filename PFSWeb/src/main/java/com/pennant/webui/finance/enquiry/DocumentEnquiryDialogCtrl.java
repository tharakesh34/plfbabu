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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class DocumentEnquiryDialogCtrl extends GFCBaseCtrl<FinAgreementDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(DocumentEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DocumentEnquiryDialog;
	protected Listbox listBoxDocument;
	protected Borderlayout borderlayoutDocumentEnquiry;
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<DocumentDetails> finDocuments;

	private FinanceDetailService financeDetailService;
	private DMSService dMSService;

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
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DocumentEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DocumentEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finDocuments")) {
			this.finDocuments = (List<DocumentDetails>) arguments.get("finDocuments");
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			// fill the components with the data
			doFillDocList(this.finDocuments);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxDocument.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_DocumentEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindow.appendChild(this.window_DocumentEnquiryDialog);

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
	public void doFillDocList(List<DocumentDetails> docDetails) {
		logger.debug("Entering");

		List<ValueLabel> list = PennantAppUtil.getDocumentTypes();
		Listitem listitem = null;
		Listcell lc = null;
		for (DocumentDetails doc : docDetails) {

			listitem = new Listitem();
			lc = new Listcell(String.valueOf(doc.getDocId()));
			listitem.appendChild(lc);

			lc = new Listcell(PennantApplicationUtil.getLabelDesc(doc.getDocCategory(), list));
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDoctype());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDocName());
			listitem.appendChild(lc);

			lc = new Listcell();
			final Checkbox documentIsActive = new Checkbox();
			documentIsActive.setDisabled(true);
			documentIsActive.setChecked(doc.isDocOriginal());
			lc.appendChild(documentIsActive);
			listitem.appendChild(lc);

			lc = new Listcell();
			Button viewBtn = new Button("View");
			if (StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)
					|| StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_MSG)
					|| StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_DOC)
					|| StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_DOCX)
					|| StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_EXCEL)) {
				viewBtn.setLabel("Download");
			}
			viewBtn.addForward("onClick", window_DocumentEnquiryDialog, "onDocViewButtonClicked", doc);
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);

			this.listBoxDocument.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void onDocViewButtonClicked(Event event) {
		logger.debug("Entering" + event.toString());

		DocumentDetails detail = (DocumentDetails) event.getData();

		// Display the Message for the Not Available Doc Image.
		if (detail == null) {
			MessageUtil.showMessage(" Document not Avaialble / Generated Yet.");
			return;
		}

		String custCif = detail.getLovDescCustCIF();
		String docName = detail.getDocName();
		String docUri = detail.getDocUri();
		Long docRefId = detail.getDocRefId();

		DocumentDetails dd = null;
		if (StringUtils.isNotBlank(docUri)) {
			dd = dMSService.getExternalDocument(custCif, docName, docUri);
		} else {
			if (detail.getDocImage() == null) {
				if (docRefId != null && docRefId != Long.MIN_VALUE) {
					detail.setDocImage(dMSService.getById(docRefId));
				}
			}
		}

		if (StringUtils.isNotBlank(detail.getDocName()) && detail.getDocImage() != null
				&& StringUtils.isNotBlank(detail.getDocImage().toString())) {
			try {
				if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_MSG)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOC)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOCX)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_EXCEL)) {
					Filedownload.save(detail.getDocImage(), "application/octet-stream", detail.getDocName());
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("FinDocumentDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		} else if (StringUtils.isNotBlank(detail.getDocUri())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("documentRef", dd);
			map.put("docType", detail);
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

	public void setDMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
