package com.pennant.webui.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.PennantConstants;

public class ImageViewCtrl extends GFCBaseCtrl {

	private static final long serialVersionUID = -624134530458308782L;
	protected Window			window_ImageView;
	protected Iframe			document;

	private final static Logger	logger				= Logger.getLogger(ImageViewCtrl.class);

	public void onCreate$window_ImageView(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			
			Map<String, Object> map = getCreationArgsMap(event);
			AMedia amedia = null;
			if (map.containsKey("FinGurantorProofDetail")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.document.setVisible(true);
				
				GuarantorDetail guarantorDetail = (GuarantorDetail) map.get("FinGurantorProofDetail");
				if (guarantorDetail != null && guarantorDetail.getGuarantorProof() != null) {
					final InputStream data = new ByteArrayInputStream(guarantorDetail.getGuarantorProof());
					if (guarantorDetail.getGuarantorProofName().toLowerCase().endsWith(".pdf")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "pdf", "application/pdf", data);
					} else if(guarantorDetail.getGuarantorProofName().toLowerCase().endsWith(".doc") || guarantorDetail.getGuarantorProofName().toLowerCase().endsWith(".docx")){
			    		amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "msword", "application/msword", data);
			    	} else if (guarantorDetail.getGuarantorProofName().endsWith(".jpg") || guarantorDetail.getGuarantorProofName().toLowerCase().endsWith(".jpeg")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "jpeg", "image/jpeg", data);
					}else if (guarantorDetail.getGuarantorProofName().endsWith(".png") || guarantorDetail.getGuarantorProofName().toLowerCase().endsWith(".png")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "jpeg", "image/jpeg", data);
					}else if (guarantorDetail.getGuarantorProofName().endsWith(".msg")) {
						amedia = new AMedia(guarantorDetail.getGuarantorProofName(), "msg", "application/octet-stream", data);
					}
					document.setContent(amedia);
				}
				
			}
			
			if (map.containsKey("FinDocumentDetail")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.document.setVisible(true);
				
				DocumentDetails docDetail = (DocumentDetails) map.get("FinDocumentDetail");
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

			document.setHeight(getBorderLayoutHeight());
			this.window_ImageView.doModal();
		} catch (Exception e) {
			this.window_ImageView.onClose();
			PTMessageUtils.showErrorMessage("Document Not Found");
			e.printStackTrace();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) throws Exception {
		this.window_ImageView.onClose();
	}
}
