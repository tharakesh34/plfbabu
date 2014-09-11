package com.pennant.webui.reports;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;

public class ReportsCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 7219917076107821148L;
	private final static Logger logger = Logger.getLogger(ReportsCtrl.class);
	private Window window_Report;
	//private Window window_parent;
	protected Tabbox         tabbox;
	private Iframe report;
	public byte[] buf = null;
	Window dialogWindow = null;

	Window parentWindow = null;
	Window window_FinEnqHeaderDialog ;

	private boolean isAgreement = false;
	private String reportName = "PFSReport.pdf";


	public void onCreate$window_Report(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final Map<String, Object> args = getCreationArgsMap(event);
		
		if (args.containsKey("reportBuffer")) {
			this.buf = (byte[]) args.get("reportBuffer");
		} else {
			this.buf = null;
		}
		if (args.containsKey("dialogWindow")) {
			dialogWindow = (Window) args.get("dialogWindow");
		}
		/*if (args.containsKey("parentWindow")) {
			window_parent = (Window) args.get("parentWindow");
		}*/
		if (args.containsKey("tabbox")) {
			tabbox = (Tabbox) args.get("tabbox");
		}
		
		if (args.containsKey("reportName")) {
			reportName = (String) args.get("reportName");
		}

		if (args.containsKey("isAgreement")) {
			isAgreement = (Boolean) args.get("isAgreement");
		}
		
		AMedia amedia = null;
		if(isAgreement){
			amedia = new AMedia(reportName, "msword", "application/msword", buf);
			report.setContent(amedia);
		}else{
			amedia = new AMedia(reportName, "pdf", "application/pdf", buf);
			report.setContent(amedia);
			if (dialogWindow != null) {
				this.dialogWindow.setVisible(false);
				this.report.setHeight(getBorderLayoutHeight());
			}
			setDialog(window_Report);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		if (dialogWindow != null) {
			this.dialogWindow.setVisible(true);
			setDialog2(dialogWindow);
		}
		closeDialog2(window_Report, "");
	}
}