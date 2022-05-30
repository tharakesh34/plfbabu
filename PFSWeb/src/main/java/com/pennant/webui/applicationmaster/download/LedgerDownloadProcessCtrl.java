package com.pennant.webui.applicationmaster.download;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.LedgerDownloadService;

public class LedgerDownloadProcessCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 223801324705386693L;
	private static final Logger logger = LogManager.getLogger(LedgerDownloadProcessCtrl.class);

	protected Window window_Download; // autoWired
	protected Button btnStartLedgerFile;
	@Autowired(required = false)
	@Qualifier(value = "ledgerDownloadService")
	private LedgerDownloadService ledgerDownloadService;

	public void setLedgerDownloadService(LedgerDownloadService ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

	public LedgerDownloadProcessCtrl() {
		super();
	}

	public void onCreate$window_Download(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_Download);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnStartLedgerFile(ForwardEvent event) {
		if (ledgerDownloadService != null) {
			ledgerDownloadService.downloadLedgerData();
		}
	}

}