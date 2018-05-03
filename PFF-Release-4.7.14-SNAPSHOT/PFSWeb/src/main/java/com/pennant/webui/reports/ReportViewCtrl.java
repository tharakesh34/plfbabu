package com.pennant.webui.reports;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.webui.util.GFCBaseCtrl;

public class ReportViewCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 7219917076107821148L;
	private static final Logger logger = Logger.getLogger(ReportViewCtrl.class);
	private Window window_Report;

	protected Tabbox         tabbox;
	private Iframe report;
	public byte[] buf = null;
	Window dialogWindow = null;
	Window parentWindow = null;

	private boolean isAgreement = false;
	private int docFormat = 0;
	private String reportName = "PFSReport.pdf";
	private boolean searchClick = true;

	public ReportViewCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_Report(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_Report);

		if (arguments.containsKey("reportBuffer")) {
			this.buf = (byte[]) arguments.get("reportBuffer");
		} else {
			this.buf = null;
		}
		
		if (arguments.containsKey("dialogWindow")) {
			dialogWindow = (Window) arguments.get("dialogWindow");
		}
		if (arguments.containsKey("parentWindow")) {
			parentWindow = (Window) arguments.get("parentWindow");
		}
		
		if (arguments.containsKey("tabbox")) {
			tabbox = (Tabbox) arguments.get("tabbox");
		}
		
		if (arguments.containsKey("reportName")) {
			reportName = (String) arguments.get("reportName");
		}

		if (arguments.containsKey("isAgreement")) {
			isAgreement = (Boolean) arguments.get("isAgreement");
		}
		
		if (arguments.containsKey("docFormat")) {
			docFormat =Integer.parseInt(arguments.get("docFormat").toString());
		}
		
		if (arguments.containsKey("searchClick")) {
			searchClick = (Boolean) arguments.get("searchClick");
		}
		
		AMedia amedia = null;
		if(isAgreement){
			
			if (docFormat == SaveFormat.PDF) {
				Filedownload.save(new AMedia(reportName, "pdf", "application/pdf", buf));
			}else{
				amedia = new AMedia(reportName, "msword", "application/msword", buf);
			}
			report.setContent(amedia);
			
			buf = null;
			amedia= null;
		}else{
			amedia = new AMedia(reportName, "pdf", "application/pdf", buf);
			report.setContent(amedia);
			if (dialogWindow != null) {
				if (parentWindow!=null) {
					this.parentWindow.onClose();
				}
				this.dialogWindow.setVisible(false);
				this.report.setHeight(getBorderLayoutHeight());
			}
			
			buf = null;
			amedia= null;
			setDialog(DialogType.EMBEDDED);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		if (dialogWindow != null) {
			this.dialogWindow.setVisible(true);
			setDialog(DialogType.OVERLAPPED);
		}
		
		if(!searchClick){
			tabbox.getSelectedTab().close();
		}
		
		closeDialog();
	}
}