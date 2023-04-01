/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SysNotificationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * *
 * Modified Date : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.sysnotification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.QueryBuilder;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.SysNotification;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.SysNotificationService;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.model.SysNotificationDialogModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

import freemarker.template.TemplateException;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SysNotification/sysNotificationDialog.zul file.
 * <br>
 */
public class SysNotificationDialogCtrl extends GFCBaseCtrl<SysNotification> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SysNotificationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SysNotificationDialog;
	protected Row row0;
	protected Label label_Code;
	protected Hlayout hlayout_Code;
	protected Space space_Code;

	protected Textbox code;
	protected Label label_Description;
	protected Hlayout hlayout_Description;
	protected Space space_Description;

	protected Textbox description;
	protected Row row1;
	protected Label label_Active;
	protected Hlayout hlayout_Active;
	protected Space space_Active;

	protected Checkbox active;

	// not auto wired vars
	private SysNotification sysNotification; // overhanded per param
	private transient SysNotificationListCtrl sysNotificationListCtrl; // overhanded per param

	// ServiceDAOs / Domain Classes
	private transient SysNotificationService sysNotificationService;
	private transient PagedListService pagedListService;

	protected Tab tab_Query;
	protected ExtendedCombobox queryCode;
	protected ExtendedCombobox template;
	protected Hbox hbox_queryCode;
	protected QueryBuilder rule;
	protected Textbox documnetName;
	protected Button btnUploadDoc;
	protected Button btnSendMail;
	protected Tab tab_CustomersList;
	protected Tabpanel tabpanel_Customers;
	protected Listbox listBox_CustomersList;
	protected Paging paging_CustomersList;
	protected Grid grid_SysNotification;
	protected Label label_Wintitle;

	private PagedListWrapper<SysNotificationDetails> custListWrapper;
	private NotificationService notificationService;

	private String sendNotification;

	/**
	 * default constructor.<br>
	 */
	public SysNotificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SysNotificationDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected SysNotification object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SysNotificationDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SysNotificationDialog);

		try {
			if (arguments.containsKey("sysNotification")) {
				this.sysNotification = (SysNotification) arguments.get("sysNotification");
				SysNotification befImage = new SysNotification();
				BeanUtils.copyProperties(this.sysNotification, befImage);
				this.sysNotification.setBefImage(befImage);

				setSysNotification(this.sysNotification);
			} else {
				setSysNotification(null);
			}

			if (arguments.containsKey("sysNotificationListCtrl")) {
				setSysNotificationListCtrl((SysNotificationListCtrl) arguments.get("sysNotificationListCtrl"));
			} else {
				setSysNotificationListCtrl(null);
			}

			sendNotification = (String) arguments.get("sendNotification");

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSysNotification());

			this.listBox_CustomersList.setHeight(Integer
					.parseInt(getListBoxHeight(grid_SysNotification.getRows().getVisibleItemCount()).substring(0,
							getListBoxHeight(grid_SysNotification.getRows().getVisibleItemCount()).indexOf("px")))
					- 70 + "px");
			this.paging_CustomersList.setPageSize(Integer
					.parseInt(getListBoxHeight(grid_SysNotification.getRows().getVisibleItemCount()).substring(0,
							getListBoxHeight(grid_SysNotification.getRows().getVisibleItemCount()).indexOf("px")))
					- 70);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSysNotification
	 * @throws InterruptedException
	 */
	public void doShowDialog(SysNotification aSysNotification) throws InterruptedException {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSysNotification);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.queryCode.setVisible(true);
		this.rule.setEditable(false);

		this.queryCode.setMaxlength(50);
		this.queryCode.setTextBoxWidth(150);
		this.queryCode.setMandatoryStyle(true);
		this.queryCode.setModuleName("Query");
		this.queryCode.setValueColumn("QueryCode");
		this.queryCode.setDescColumn("QueryDesc");
		this.queryCode.setValidateColumns(new String[] { "QueryCode" });

		/*
		 * Filter filter[] = new Filter[1]; filter[0] = new Filter("QueryModule", "SCHNOTIFICATIONS", Filter.OP_EQUAL);
		 * this.queryCode.setFilters(filter);
		 */

		this.template.setMaxlength(100);
		this.template.setTextBoxWidth(150);
		this.template.setMandatoryStyle(true);
		this.template.setModuleName("MailTemplate");
		this.template.setValueColumn("TemplateCode");
		this.template.setDescColumn("TemplateDesc");
		this.template.setValidateColumns(new String[] { "TemplateCode" });

		if ("Y".equals(sendNotification)) {
			this.label_Wintitle.setValue(Labels.getLabel("window_SendNotificationDialog.title"));
			this.btnSave.setVisible(false);
			this.btnSendMail.setVisible(true);
			this.tab_CustomersList.setVisible(true);
			this.tab_CustomersList.setSelected(true);
			this.tab_Query.setVisible(false);
			doReadOnly();
		} else {
			this.label_Wintitle.setValue(Labels.getLabel("window_SysNotificationDialog.title"));
		}

		if (!getSysNotification().isNewRecord() && !"Y".equals(sendNotification)) {
			this.btnDelete.setVisible(true);
		}

		logger.debug("Leaving");
	}

	private void doReadOnly() {
		logger.debug("Entering");

		this.queryCode.setReadonly(true);
		this.description.setReadonly(true);
		this.template.setReadonly(true);
		this.btnUploadDoc.setVisible(false);

		this.queryCode.setMandatoryStyle(false);
		this.template.setMandatoryStyle(false);

		logger.debug("Leaving");

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSysNotification SysNotification
	 */
	public void doWriteBeanToComponents(SysNotification aSysNotification) {
		logger.debug("Entering");

		this.queryCode.setValue(aSysNotification.getQueryCode());
		this.queryCode.setDescription(aSysNotification.getLovDescQueryDesc());
		this.rule.setSqlQuery(aSysNotification.getLovDescSqlQuery());
		this.description.setValue(aSysNotification.getDescription());
		this.template.setValue(aSysNotification.getTemplateCode());
		this.template.setDescription(aSysNotification.getLovDescTemplateDesc());
		this.documnetName.setValue(aSysNotification.getDocName());

		this.recordStatus.setValue(aSysNotification.getRecordStatus());
		// this.recordType.setValue(PennantJavaUtil.getLabel(aSysNotification.getRecordType()));

		if ("Y".equals(sendNotification)) {
			getCustListWrapper().initList(aSysNotification.getSysNotificationDetailsList(), this.listBox_CustomersList,
					this.paging_CustomersList);
			this.listBox_CustomersList.setItemRenderer(new SysNotificationDialogModelItemRenderer());
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "SearchqueryCode" button
	 * 
	 * @param event
	 */
	public void onFulfill$queryCode(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = this.queryCode.getObject();
		if (dataObject instanceof String) {
			this.queryCode.setValue(dataObject.toString());
			this.queryCode.setDescription("");
		} else {
			this.tab_Query.setSelected(true);
			/* Set FinanceWorkFlow object */
			Query details = (Query) dataObject;
			if (details != null) {
				this.queryCode.setValue(details.getQueryCode());
				this.queryCode.setDescription(details.getQueryDesc());
				this.rule.setSqlQuery(details.getSQLQuery());
				this.rule.setEditable(false);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "SearchqueryCode" button
	 * 
	 * @param event
	 */
	public void onFulfill$template(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = this.template.getObject();
		if (dataObject instanceof String) {
			this.template.setValue(dataObject.toString());
			this.template.setDescription("");
		} else {
			/* Set FinanceWorkFlow object */
			MailTemplate details = (MailTemplate) dataObject;
			if (details != null) {
				this.template.setValue(details.getTemplateCode());
				this.template.setDescription(details.getTemplateDesc());
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	protected void doSave() {
		logger.debug("Entering");

		final SysNotification aSysNotification = new SysNotification();
		BeanUtils.copyProperties(getSysNotification(), aSysNotification);

		// fill the SysNotification object with the components data
		doSetValidation();
		doWriteComponentsToBean(aSysNotification);

		// save it to database
		try {
			if (getSysNotification().isNewRecord()) {
				getSysNotificationService().saveSysNotification(aSysNotification);
			} else {
				getSysNotificationService().updateSysNotification(aSysNotification);
			}
			refreshList();
			closeDialog();

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		getSysNotificationService().deleteSysNotification(getSysNotification().getSysNotificationId());
		refreshList();
		closeDialog();

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSendMail(Event event) {
		logger.debug("Entering" + event.toString());

		if (!this.listBox_CustomersList.getSelectedItems().isEmpty()) {

			boolean success = true;
			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();

			// Mail Alert Notification for Customer/Dealer/Provider...etc
			long templateId = getSysNotificationService().getTemplateId(getSysNotification().getTemplateCode());

			for (Listitem listItem : this.listBox_CustomersList.getSelectedItems()) {
				SysNotificationDetails details = (SysNotificationDetails) listItem.getAttribute("data");
				String mailID = getSysNotificationService().getCustomerEMail(details.getCustID());

				details.setLastMntBy(securityUser.getUsrID());
				details.setFinCurODAmtInStr(
						CurrencyUtil.format(details.getFinCurODAmt(), CurrencyUtil.getFormat(details.getFinCcy())));

				// Mail Send
				if (StringUtils.isNotEmpty(mailID)) {
					try {
						notificationService.sendMailtoCustomer(templateId, mailID, details);
					} catch (TemplateException e) {
						logger.error("Exception: ", e);
						success = false;
						MessageUtil.showError("Please Configure valid Mail Template.");
						return;
					} catch (Exception e) {
						logger.error("Exception: ", e);
						success = false;
						MessageUtil.showError(
								"Mail sending failed...! \n Connection Failed.. Please contact administrator");
						return;
					}
				} else {
					success = false;
					MessageUtil.showError("Please Configure the Email for the Customer : " + details.getCustCIF());
					return;
				}
			}
			if (success) {
				Clients.showNotification("Mail Send Successfully");
				refreshList();
				closeDialog();
			}
		} else {
			final String msg = "Please Select Customers for Sending a Mail";
			MessageUtil.showError(msg);
			return;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.queryCode.isReadonly()) {
			this.queryCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SysNotificationDialog_QueryCode.value"), null, true, true));
		}

		if (!this.template.isReadonly()) {
			this.template.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SysNotificationDialog_Template.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSysNotificationListCtrl().search();
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSysNotification
	 */
	public void doWriteComponentsToBean(SysNotification aSysNotification) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aSysNotification.setQueryCode(this.queryCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aSysNotification.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template
		try {
			aSysNotification.setTemplateCode(this.template.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template
		try {
			aSysNotification.setDocName(this.documnetName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template
		try {
			aSysNotification.setDocImage("");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (aSysNotification.isNewRecord()) {
			aSysNotification.setLastMntBy(getUserWorkspace().getUserDetails().getUserId());
			aSysNotification.setLastMntOn(DateUtil.getTimestamp(SysParamUtil.getAppDate()));
			aSysNotification.setRecordStatus("Approved");
			aSysNotification.setVersion(1);
			aSysNotification.setRoleCode("");
			aSysNotification.setNextRoleCode("");
			aSysNotification.setTaskId("");
			aSysNotification.setNextTaskId("");
			aSysNotification.setRecordType("");
			aSysNotification.setWorkflowId(0);
		}

		doRemoveValidation();
		// doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to remove constraints
	 * 
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.queryCode.setConstraint("");
		this.template.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages
	 * 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.queryCode.clearErrorMessage();
		this.template.clearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			closeDialog();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();

		this.documnetName.setValue(media.getName());
		browseDoc(media);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media) throws InterruptedException {
		logger.debug("Entering");

		List<DocType> allowed = new ArrayList<>();
		allowed.add(DocType.PDF);
		allowed.add(DocType.JPG);
		allowed.add(DocType.JPEG);
		allowed.add(DocType.PNG);
		allowed.add(DocType.DOC);
		allowed.add(DocType.DOCX);
		allowed.add(DocType.XLS);
		allowed.add(DocType.XLSX);

		if (!MediaUtil.isValid(media, allowed)) {
			MessageUtil.showError("Only PDF, Word, Excel and Images(JPG/JPEG/PNG) are allowed");
			return;
		}

		DocType docType = MediaUtil.getDocType(media);
		getSysNotification().setDoctype(docType.name());

		logger.debug("Leaving");
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<SysNotificationDetails> getCustListWrapper() {
		if (this.custListWrapper == null) {
			this.custListWrapper = (PagedListWrapper<SysNotificationDetails>) SpringUtil.getBean("pagedListWrapper");
		}
		return custListWrapper;
	}

	public SysNotificationListCtrl getSysNotificationListCtrl() {
		return sysNotificationListCtrl;
	}

	public void setSysNotificationListCtrl(SysNotificationListCtrl sysNotificationListCtrl) {
		this.sysNotificationListCtrl = sysNotificationListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public SysNotification getSysNotification() {
		return sysNotification;
	}

	public void setSysNotification(SysNotification sysNotification) {
		this.sysNotification = sysNotification;
	}

	public SysNotificationService getSysNotificationService() {
		return sysNotificationService;
	}

	public void setSysNotificationService(SysNotificationService sysNotificationService) {
		this.sysNotificationService = sysNotificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
