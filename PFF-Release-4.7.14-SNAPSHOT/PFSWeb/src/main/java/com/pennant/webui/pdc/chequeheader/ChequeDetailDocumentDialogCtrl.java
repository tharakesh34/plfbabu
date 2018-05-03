package com.pennant.webui.pdc.chequeheader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.financemain.ChequeDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ChequeDetailDocumentDialogCtrl extends GFCBaseCtrl<ChequeDetail> {
	private static final long		serialVersionUID	= 6004939933729664895L;
	private static final Logger		logger				= Logger.getLogger(ChequeDetailDocumentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window				window_ChequeDetailDocumentDialog;
	protected Borderlayout			borderlayoutChequeDocument;
	protected Grid					grid_basicDetails;

	protected Textbox				chequeId;
	protected Textbox				documentName;
	protected Button				btnUploadDoc;
	protected Iframe				chequeDocumentDivPdfView;
	protected Div					chequeDocumentDiv;
	protected Space					space_documnetName;

	private ChequeDetailDialogCtrl	chequeDetailDialogCtrl;
	private ChequeDetail			chequeDetail;
	private DocumentManagerDAO      documentManagerDAO;


	/**
	 * default constructor.<br>
	 */
	public ChequeDetailDocumentDialogCtrl() {
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
	public void onCreate$window_ChequeDetailDocumentDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChequeDetailDocumentDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqiryModule = (boolean) arguments.get("enqModule");
			} else {
				enqiryModule = false;
			}

			// READ OVERHANDED parameters !

			// ChequeDetailDialogCtrl details
			if (arguments.containsKey("ChequeDetailDialogCtrl")) {
				chequeDetailDialogCtrl = ((ChequeDetailDialogCtrl) arguments.get("ChequeDetailDialogCtrl"));
			}

			// ChequeDetail
			if (arguments.containsKey("chequeDetail")) {
				chequeDetail = (ChequeDetail) arguments.get("chequeDetail");
			}

			int dialogHeight = grid_basicDetails.getRows().getVisibleItemCount() * 20 + 80;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			this.chequeDocumentDivPdfView.setHeight(listboxHeight + "px");
			
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		
		chequeDetail.setDocImage(null);
		chequeDetail.setDocumentName("");
		chequeDetail.setDocumentRef(Long.MIN_VALUE);
		
		closeDialog();
		
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}
	
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		if(chequeDetailDialogCtrl != null) {
			chequeDetailDialogCtrl.getChequeDocuments().add(chequeDetail);
		}
		
		closeDialog();
		logger.debug("Leaving");
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
			// fill the components with the data
			doWriteBeanToComponents(chequeDetail);
			this.window_ChequeDetailDocumentDialog.setHeight("80%");
			this.window_ChequeDetailDocumentDialog.setWidth("80%");
			this.window_ChequeDetailDocumentDialog.doModal() ;
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ChequeDetailDocumentDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerDocument
	 *            CustomerDocument
	 */
	public void doWriteBeanToComponents(ChequeDetail chequeDetail) {
		logger.debug("Entering");

		this.chequeId.setValue(String.valueOf(chequeDetail.getChequeSerialNo()));
		this.documentName.setValue(chequeDetail.getDocumentName());
		this.documentName.setAttribute("data", chequeDetail);

		if(chequeDetail.getDocImage() == null && chequeDetail.getDocumentRef() != Long.MIN_VALUE) {
			DocumentManager docManager = documentManagerDAO.getById(chequeDetail.getDocumentRef());
			if(docManager != null) {
				chequeDetail.setDocImage(docManager.getDocImage());
			}
		}
		AMedia amedia = null;
		if (chequeDetail.getDocImage() != null) {
			amedia = new AMedia(chequeDetail.getDocumentName(), null, null, chequeDetail.getDocImage());
			// display the image
			this.chequeDocumentDivPdfView.setContent(amedia);
		}

		logger.debug("Leaving");
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documentName);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");
		try {
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			}

			//Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.chequeDocumentDivPdfView.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.chequeDocumentDivPdfView.setContent(media);
			} else if (docType.equals(PennantConstants.DOC_TYPE_WORD)
					|| docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				//this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + fileName + "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(docType);
				list.add(ddaImageData);
				ageementLink.addForward("onClick", window_ChequeDetailDocumentDialog, "onDocumentClicked", list);
				//this.docDiv.appendChild(ageementLink);
			}

			if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				//this.docDiv.setVisible(true);
				this.chequeDocumentDivPdfView.setVisible(false);
			} else {
				//this.docDiv.setVisible(false);
				this.chequeDocumentDivPdfView.setVisible(true);
			}

			textbox.setValue(fileName);
			chequeDetail.setDocumentName(fileName);
			chequeDetail.setDocImage(ddaImageData);
	
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
}
