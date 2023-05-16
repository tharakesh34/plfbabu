package com.pennant.webui.cibil.reports;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;

public class CibilReportViewCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 7219917076107821148L;

	private Window window_Report;
	private Iframe report;
	private String data = null;
	Window dialogWindow = null;
	private String name;

	public void onCreate$window_Report(Event event) {

		setPageComponents(window_Report);
		// READ OVERHANDED parameters !
		if (arguments.containsKey("reportData")) {
			this.data = (String) arguments.get("reportData");
		} else {
			this.data = null;
		}

		if (arguments.containsKey("dialogWindow")) {
			dialogWindow = (Window) arguments.get("dialogWindow");
		}

		if (arguments.containsKey("mediaName")) {
			name = (String) arguments.get("mediaName");
		}

		// Prepare the AMedia for iFrame
		final AMedia amedia = new AMedia(name, "html", "text/html", data);

		// set iFrame content
		report.setContent(amedia);
		if (dialogWindow != null) {
			this.dialogWindow.setVisible(false);
		}

		setDialog(DialogType.MODAL);
	}

	public void onClick$btnClose(Event event) {
		closeDialog();
		if (dialogWindow != null) {
			this.dialogWindow.setVisible(true);
			setDialog(DialogType.OVERLAPPED);
		}
	}

}
