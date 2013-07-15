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
import com.pennant.backend.model.finance.FinAgreementDetail;

public class ImageViewCtrl extends GFCBaseCtrl {

	private static final long serialVersionUID = -624134530458308782L;
	protected Window			window_ImageView;
	protected Iframe			document;

	private final static Logger	logger				= Logger.getLogger(ImageViewCtrl.class);

	public void onCreate$window_ImageView(Event event) throws Exception {
		logger.debug(event);
		try {
			Map<String, Object> map = getCreationArgsMap(event);
			AMedia amedia = null;
			if (map.containsKey("FinAgreementDetail")) {
				this.window_ImageView.setWidth("90%");
				this.window_ImageView.setHeight("80%");
				this.document.setVisible(true);
				
				FinAgreementDetail agreementDetail = (FinAgreementDetail) map.get("FinAgreementDetail");
				if (agreementDetail != null && agreementDetail.getAgrContent() != null) {
					final InputStream data = new ByteArrayInputStream(agreementDetail.getAgrContent());
					if (agreementDetail.getAgrName().toLowerCase().endsWith(".pdf")) {
						amedia = new AMedia("Agreement.pdf", "pdf", "application/pdf", data);
					} else if (agreementDetail.getAgrName().endsWith(".jpg") || agreementDetail.getAgrName().toLowerCase().endsWith(".jpeg")) {
						amedia = new AMedia("Agreement.jpg", "jpeg", "image/jpeg", data);
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
					if ("PDF".equals(docDetail.getDoctype())) {
						amedia = new AMedia("document.pdf", "pdf", "application/pdf", data);
					} else if ("IMG".equals(docDetail.getDoctype())) {
						amedia = new AMedia("document.jpg", "jpeg", "image/jpeg", data);
					}
					document.setContent(amedia);
				}

			}

			this.window_ImageView.doModal();
		} catch (Exception e) {
			this.window_ImageView.onClose();
			PTMessageUtils.showErrorMessage("Document Not Found");
			e.printStackTrace();
		}

	}

	public void onClick$btnClose(Event event) throws Exception {
		this.window_ImageView.onClose();
	}
}
