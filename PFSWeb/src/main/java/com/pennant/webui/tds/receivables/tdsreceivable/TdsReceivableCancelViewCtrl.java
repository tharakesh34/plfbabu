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
 * * FileName : TDSReceivableDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/tds.receivables/TDSReceivable/tDSReceivableDialog.zul file. <br>
 */
public class TdsReceivableCancelViewCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = 7219917076107821148L;
	private static final Logger logger = LogManager.getLogger(TdsReceivableCancelViewCtrl.class);
	private Window window_TDSReceivableView;

	protected Tabbox tabbox;
	public Div finDocumentDiv = null;
	public Iframe finDocumentPdfView = null;
	public TdsReceivable tdsReceivable;

	private DocumentDetails documentDetails;
	private transient TdsReceivableService tdsReceivableService;

	public TdsReceivableCancelViewCtrl() {
		super();
	}

	public void onCreate$window_TDSReceivableView(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			setPageComponents(window_TDSReceivableView);

			this.tdsReceivable = (TdsReceivable) arguments.get("tdsReceivable");

			documentDetails = tdsReceivableService.getDocumentDetails(tdsReceivable.getDocID(), "_View");

			tdsReceivable.setDocumentDetails(documentDetails);

			String docName = documentDetails.getDocName().toLowerCase();
			if (docName.endsWith(".doc") || docName.endsWith(".docx")) {
				Filedownload.save(new AMedia(docName, "msword", "application/msword", documentDetails.getDocImage()));
			} else if (docName.endsWith(".xls") || docName.endsWith(".xlsx") || docName.endsWith(".txt")) {
				Filedownload
						.save(new AMedia(docName, "xls", "application/vnd.ms-excel", documentDetails.getDocImage()));
			} else if (docName.endsWith(".png") || docName.endsWith(".jpeg") || docName.endsWith(".pdf")
					|| docName.endsWith(".jpg")) {
				setDocImage();
			} else if (docName.endsWith(".zip")) {
				Filedownload.save(new AMedia(docName, "x-zip-compressed", "application/x-zip-compressed",
						documentDetails.getDocImage()));
			} else if (docName.endsWith(".7z")) {
				Filedownload.save(
						new AMedia(docName, "octet-stream", "application/octet-stream", documentDetails.getDocImage()));
			} else if (docName.endsWith(".rar")) {
				Filedownload.save(new AMedia(docName, "x-rar-compressed", "application/x-rar-compressed",
						documentDetails.getDocImage()));
			}
			closeDialog();
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		closeDialog();
	}

	private void setDocImage() {
		logger.debug(Literal.ENTERING);
		AMedia amedia = null;

		if (documentDetails != null) {
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
			finDocumentPdfView.setContent(amedia);
		}
		this.finDocumentPdfView.setHeight(getBorderLayoutHeight());
		setDialog(DialogType.MODAL);
		logger.debug(Literal.LEAVING);
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

	public void setTdsReceivableService(TdsReceivableService tdsReceivableService) {
		this.tdsReceivableService = tdsReceivableService;
	}

}