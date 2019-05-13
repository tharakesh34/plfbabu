package com.pennant.webui.loanquery.querydetail;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.webui.util.GFCBaseCtrl;

public class QueryDocumentViewCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = 7219917076107821148L;
	private static final Logger logger = Logger.getLogger(QueryDocumentViewCtrl.class);
	private Window window_Query;

	protected Tabbox tabbox;
	public Div finDocumentDiv = null;
	public Iframe finDocumentPdfView = null;

	private DocumentDetails documentDetails;

	public QueryDocumentViewCtrl() {
		super();
	}

	public void onCreate$window_Query(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_Query);

		this.documentDetails = (DocumentDetails) arguments.get("documentDetails");
		setDocImage();

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		closeDialog();
	}

	private void setDocImage() {
		logger.debug("Entering");
		AMedia amedia = null;

		if (documentDetails != null) {
			amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
			finDocumentPdfView.setContent(amedia);
		}
		this.finDocumentPdfView.setHeight(getBorderLayoutHeight());
		setDialog(DialogType.MODAL);
		logger.debug("Leaving");
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

}