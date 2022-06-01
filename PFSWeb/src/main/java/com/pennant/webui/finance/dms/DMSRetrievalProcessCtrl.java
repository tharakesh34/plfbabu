
package com.pennant.webui.finance.dms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.DocumentManagementService;

public class DMSRetrievalProcessCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 223801324705386693L;
	private static final Logger logger = LogManager.getLogger(DMSRetrievalProcessCtrl.class);

	protected Window window_DMSRetrieve; // autoWired
	protected Button btnStartDMSRetrieve;
	protected Row rowDMSRetrieve;

	@Autowired
	protected DocumentManagementService dmsManagementService;

	public DMSRetrievalProcessCtrl() {
		super();
	}

	public void onCreate$window_DMSRetrieve(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DMSRetrieve);

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnStartDMSRetrieve(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		boolean flag = false;

		try {
			flag = dmsManagementService.dmsRetrieveProcess();

			if (flag) {
				MessageUtil.showMessage(
						"Documents Details have been retrieved from DMS and saved into respective queues.");
				btnStartDMSRetrieve.setVisible(true);
			}
		} catch (Exception e) {
			logger.trace(e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired(required = false)
	public void setDmsManagementService(DocumentManagementService dmsManagementService) {
		this.dmsManagementService = dmsManagementService;
	}

}
