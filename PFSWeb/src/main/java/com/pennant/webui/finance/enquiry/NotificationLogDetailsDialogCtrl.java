package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.finance.NotificationLogDetailsService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.NotificationAttribute;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class NotificationLogDetailsDialogCtrl extends GFCBaseCtrl<Notification> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(NotificationLogDetailsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */

	protected Window window_NotificationLogEnquiryDialog;
	protected Listbox listBoxNotificationLogEmail;
	protected Listbox listBoxNotificationLogSms;
	protected Paging pagingNotificationEnquiryList;
	protected Borderlayout borderlayoutNotificationLogEnquiry;
	protected Tab emailLogTab;
	protected Tab smsLogTab;
	protected Grid grid_NotificationLogDetail;
	protected Paging pagingNotificationLog;
	private Tabpanel tabPanel_dialogWindow;

	protected Textbox finReference_header;
	private Tabpanel tabPanel_dialogWindowSms;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<Notification> notificationList;
	@Autowired
	protected NotificationLogDetailsService notificationLogDetailsService;

	protected String module = "";
	protected String finReference = "";

	private transient FinanceEnquiry financeEnquiry;

	/**
	 * default constructor.<br>
	 */
	public NotificationLogDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_NotificationLogEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_NotificationLogEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
			tabPanel_dialogWindowSms = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("list")) {
			this.notificationList = (List<Notification>) arguments.get("list");
		} else {
			this.notificationList = null;
		}
		if (arguments.containsKey("ccyformat")) {

		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");

		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doFillNotificationEmail(this.notificationList);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxNotificationLogEmail.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_NotificationLogEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");

				tabPanel_dialogWindow.appendChild(this.window_NotificationLogEnquiryDialog);
			}

			if (tabPanel_dialogWindowSms != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxNotificationLogSms.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_NotificationLogEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
				tabPanel_dialogWindowSms.appendChild(this.window_NotificationLogEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving...");
	}

	public void onClick$smsLogTab() {

		logger.debug("Entering..");
		String finReference = (String) arguments.get("finReference");
		String module = (String) arguments.get("module");
		arguments.get("finReference");
		arguments.get("module");
		List<Notification> notificationDetailsSms = getNotificationDetailsService()
				.getNotificationLogDetailSmsList(finReference, module);
		doFillNotificationSms(notificationDetailsSms);
		logger.debug("Leaving...");
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public FinanceEnquiry getFinanceEnquiry() {
		return financeEnquiry;
	}

	public void setFinanceEnquiry(FinanceEnquiry financeEnquiry) {
		this.financeEnquiry = financeEnquiry;
	}

	public void doFillNotificationEmail(List<Notification> notifications) {
		this.listBoxNotificationLogEmail.getItems().clear();
		if (notifications != null) {

			for (Notification notification : notifications) {
				String emailId = "";
				for (MessageAddress messageAddress : notification.getAddressesList()) {
					emailId = emailId + messageAddress.getEmailId() + ", ";
				}
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(emailId);
				lc.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
				lc.setTooltiptext(emailId);
				lc.setParent(item);

				lc = new Listcell(notification.getSubject());
				lc.setTooltiptext(notification.getSubject());
				lc.setParent(item);

				lc = new Listcell(notification.getSubModule());
				lc.setParent(item);

				lc = new Listcell(notification.getStage());
				lc.setParent(item);

				String notificationCode = null;
				if (CollectionUtils.isNotEmpty(notification.getAttributes())) {
					for (NotificationAttribute attribute : notification.getAttributes()) {
						if ("Notification_Code".equals(attribute.getAttribute())) {
							notificationCode = attribute.getValue();
							break;

						}
					}
					if (notification != null) {
						for (NotificationAttribute attribute : notification.getAttributes()) {
							if ("Notification_Desc".equals(attribute.getAttribute())) {
								notificationCode = notificationCode.concat(" ").concat(attribute.getValue());
								break;
							}
						}
					}
				}
				lc = new Listcell(notificationCode);
				lc.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
				lc.setTooltiptext(notificationCode);
				lc.setParent(item);

				listBoxNotificationLogEmail.appendChild(item);

			}
		}

	}

	public void doFillNotificationSms(List<Notification> notifications) {
		this.listBoxNotificationLogSms.getItems().clear();

		if (notifications != null) {
			for (Notification notification : notifications) {

				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(notification.getMobileNumber());
				lc.setParent(item);

				lc = new Listcell(notification.getMessage());
				lc.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
				lc.setTooltiptext(notification.getMessage());
				lc.setParent(item);

				lc = new Listcell(notification.getSubModule());
				lc.setParent(item);

				lc = new Listcell(notification.getStage());
				lc.setParent(item);

				String notificationCode = null;
				if (CollectionUtils.isNotEmpty(notification.getAttributes())) {
					for (NotificationAttribute attribute : notification.getAttributes()) {
						if ("Notification_Code".equals(attribute.getAttribute())) {
							notificationCode = attribute.getValue();
							break;
						}
					}
					if (notification != null) {
						for (NotificationAttribute attribute : notification.getAttributes()) {
							if ("Notification_Desc".equals(attribute.getAttribute())) {
								notificationCode = notificationCode.concat(" ").concat(attribute.getValue());
								break;
							}
						}
					}
					lc = new Listcell(notificationCode);
					lc.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
					lc.setTooltiptext(notificationCode);
					lc.setParent(item);

					listBoxNotificationLogSms.appendChild(item);

				}
			}
		}
	}

	public NotificationLogDetailsService getNotificationDetailsService() {
		return notificationLogDetailsService;
	}

	public void setNotificationLogDetailsService(NotificationLogDetailsService notificationLogDetailsService) {
		this.notificationLogDetailsService = notificationLogDetailsService;
	}
}