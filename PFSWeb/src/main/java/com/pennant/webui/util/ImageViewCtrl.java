package com.pennant.webui.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.PennantConstants;

public class ImageViewCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = -624134530458308782L;
	protected Window			window_ImageView;
	protected Iframe			document;
	protected Label				label_RefId;

	private static final Logger	logger				= Logger.getLogger(ImageViewCtrl.class);

	public ImageViewCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	public void onCreate$window_ImageView(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ImageView);

		try {
			
			AMedia amedia = null;
			if (arguments.containsKey("FinGurantorProofDetail")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.document.setVisible(true);
				
				GuarantorDetail guarantorDetail = (GuarantorDetail) arguments.get("FinGurantorProofDetail");
				if (guarantorDetail != null && guarantorDetail.getGuarantorProof() != null) {
					
					String docType = guarantorDetail.getGuarantorProofName().substring(guarantorDetail.getGuarantorProofName().indexOf('.')+1);
					
					final InputStream data = new ByteArrayInputStream(guarantorDetail.getGuarantorProof());
					if (StringUtils.equalsIgnoreCase(docType,"pdf")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "pdf", "application/pdf", data);
					} else if(StringUtils.equalsIgnoreCase(docType,"doc") || StringUtils.equalsIgnoreCase(docType,"docx")){
			    		amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "msword", "application/msword", data);
			    	} else if (StringUtils.equalsIgnoreCase(docType,"jpg") || StringUtils.equalsIgnoreCase(docType,"jpeg") ||
			    			StringUtils.equalsIgnoreCase(docType,"png")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "jpeg", "image/jpeg", data);
					}else if (StringUtils.equalsIgnoreCase(docType,"msg")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "msg", "application/octet-stream", data);
					}
					document.setContent(amedia);
				}
				
			}
			
			if (arguments.containsKey("FinDocumentDetail")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.document.setVisible(true);
				
				DocumentDetails docDetail = (DocumentDetails) arguments.get("FinDocumentDetail");
				if (docDetail != null && docDetail.getDocImage() != null) {
					final InputStream data = new ByteArrayInputStream(docDetail.getDocImage());
					if (PennantConstants.DOC_TYPE_PDF.equals(docDetail.getDoctype())) {
						amedia = new AMedia(docDetail.getDocName(), "pdf", "application/pdf", data);
					} else if(PennantConstants.DOC_TYPE_WORD.equals(docDetail.getDoctype())){
			    		amedia = new AMedia(docDetail.getDocName(), "msword", "application/msword", data);
					} else if(PennantConstants.DOC_TYPE_MSG.equals(docDetail.getDoctype())){
						amedia = new AMedia(docDetail.getDocName(), "msg", "application/octet-stream", data);
			    	} else if (PennantConstants.DOC_TYPE_IMAGE.equals(docDetail.getDoctype())) {
						amedia = new AMedia(docDetail.getDocName(), "jpeg", "image/jpeg", data);
					}
					document.setContent(amedia);
				}

			}
			if (arguments.containsKey("documentRef")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.label_RefId.setVisible(true);
				
				DocumentDetails docDetail = (DocumentDetails) arguments.get("documentRef");
				if (docDetail != null && docDetail.getDocUri() != null) {
					label_RefId.setVisible(true);
					document.setVisible(false);
					label_RefId.setValue(docDetail.getDocUri());
				}
			}
			if (arguments.containsKey("mandate")) {
				this.window_ImageView.setWidth("75%");
				this.window_ImageView.setHeight("80%");
				this.label_RefId.setVisible(true);

				Mandate mandate = (Mandate) arguments.get("mandate");
				String docType = StringUtils.trimToEmpty(mandate.getDocumentName()).toLowerCase();
				if (docType.endsWith(".pdf")) {
					amedia = new AMedia(mandate.getDocumentName(), "pdf", "application/pdf", mandate.getDocImage());
				} else if (docType.endsWith(".jpg")
						|| docType.endsWith(".jpeg")
						|| docType.endsWith(".png")) {
					amedia = new AMedia(mandate.getDocumentName(), "jpeg", "image/jpeg", mandate.getDocImage());
				}

				if (mandate != null) {
					label_RefId.setVisible(true);
					document.setContent(amedia);
				}
			}
			document.setHeight(getBorderLayoutHeight());
			this.window_ImageView.doModal();
		} catch (Exception e) {
			this.window_ImageView.onClose();
			MessageUtil.showError("Document Not Found");
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) throws Exception {
		this.window_ImageView.onClose();
	}
}
