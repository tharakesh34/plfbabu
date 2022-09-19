package com.pennanttech.receiptnotification;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.eod.ReceiptNotificationService;

public class ReceiptNotificationCtrl extends GFCBaseListCtrl<String> implements Serializable {

	private static final long serialVersionUID = 433563597371675777L;

	@Autowired(required = false)
	ReceiptNotificationService receiptNotificationService;

	Logger logger = LogManager.getLogger(ReceiptNotificationCtrl.class);

	protected Window window_ReceiptNotificationCtrl;
	protected Borderlayout borderLayout_ReceiptNotification;
	protected Paging pagingReceiptNotification;
	protected Button btnSendNotification;

	/**
	 * default constructor.<br>
	 */
	public ReceiptNotificationCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReceiptNotificationCtrl(Event event) {

		// Set the page level components.
		setPageComponents(window_ReceiptNotificationCtrl, borderLayout_ReceiptNotification, null,
				pagingReceiptNotification);
		this.btnSendNotification.setVisible(true);

		// Lender the page and display the data.
		doRenderPage();
	}

	/**
	 * when the "SendNotification" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSendNotification(Event event) {
		logger.debug(Literal.ENTERING);

		if (receiptNotificationService != null) {
			String notification = receiptNotificationService.sendReceiptNotifycation(SysParamUtil.getAppDate());
			MessageUtil.showMessage(notification);
		}
		logger.debug(Literal.LEAVING);
	}

}
