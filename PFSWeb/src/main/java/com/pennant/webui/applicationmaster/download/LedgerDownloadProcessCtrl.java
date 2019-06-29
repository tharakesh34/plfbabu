package com.pennant.webui.applicationmaster.download;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.external.clix.services.impl.LedgerDownloadServiceImpl;

public class LedgerDownloadProcessCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 223801324705386693L;
	private static final Logger logger = Logger.getLogger(LedgerDownloadProcessCtrl.class);

	protected Window window_Download; // autoWired
	protected Button btnStartLedgerFile;
	private LedgerDownloadServiceImpl ledgerDownloadService;

	public LedgerDownloadProcessCtrl() {
		super();
	}

	public void onCreate$window_Download(Event event) throws Exception {
		logger.debug("Entering");
		setPageComponents(window_Download);
		logger.debug("Leaving" + event.toString());
	}

	public void setLedgerDownloadService(LedgerDownloadServiceImpl ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

	public void onClick$btnStartLedgerFile(ForwardEvent event) throws Exception {
		ledgerDownloadService.processDownload(DateUtility.getAppDate(),DateUtility.getLastBusinessdate(),false);
	}

}