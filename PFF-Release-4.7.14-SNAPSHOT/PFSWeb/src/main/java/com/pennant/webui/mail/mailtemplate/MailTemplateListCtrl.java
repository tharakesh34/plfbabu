/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  MailTemplateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.mail.mailtemplate;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.webui.mail.mailtemplate.model.MailTemplateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Mail/MailTemplate/MailTemplateList.zul file.
 */
public class MailTemplateListCtrl extends GFCBaseListCtrl<MailTemplate> {
	private static final long serialVersionUID = 7079846100434942353L;
	private static final Logger logger = Logger.getLogger(MailTemplateListCtrl.class);

	protected Window window_MailTemplateList;
	protected Borderlayout borderLayout_MailTemplateList;
	protected Paging pagingMailTemplateList;
	protected Listbox listBoxMailTemplate;

	protected Listheader listheader_TemplateCode;
	protected Listheader listheader_TemplateForSMS;
	protected Listheader listheader_TemplateForEMail;
	protected Listheader listheader_TemplateActive;

	protected Button button_MailTemplateList_NewMailTemplate;
	protected Button button_MailTemplateList_MailTemplateSearch;

	protected Textbox templateCode;
	protected Textbox templateName;
	protected Textbox templateType;
	protected Checkbox active;
	protected Textbox templateFor;

	protected Listbox sortOperator_templateCode;
	protected Listbox sortOperator_templateName;
	protected Listbox sortOperator_templateType;
	protected Listbox sortOperator_active;

	private transient MailTemplateService mailTemplateService;

	/**
	 * default constructor.<br>
	 */
	public MailTemplateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MailTemplate";
		super.pageRightName = "MailTemplateList";
		super.tableName = "Templates_AView";
		super.queueTableName = "Templates_View";
		super.enquiryTableName = "Templates_View";

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MailTemplateList(Event event) {
		// Set the page level components.
		setPageComponents(window_MailTemplateList, borderLayout_MailTemplateList, listBoxMailTemplate,
				pagingMailTemplateList);
		setItemRender(new MailTemplateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MailTemplateList_NewMailTemplate, "button_MailTemplateList_NewMailTemplate", true);
		registerButton(button_MailTemplateList_MailTemplateSearch);

		registerField("templateId");
		registerField("templateCode", listheader_TemplateCode, SortOrder.ASC, templateCode, sortOperator_templateCode,
				Operators.STRING);
		registerField("active", listheader_TemplateActive, SortOrder.NONE, active, sortOperator_active,
				Operators.BOOLEAN);
		registerField("smsTemplate", listheader_TemplateForSMS);
		registerField("emailTemplate", listheader_TemplateForEMail);
		
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_MailTemplateList_MailTemplateSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_MailTemplateList_NewMailTemplate(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		MailTemplate mailTemplate = new MailTemplate();
		mailTemplate.setNewRecord(true);
		mailTemplate.setWorkflowId(getWorkFlowId());
		mailTemplate.setEmailTemplate(true);
		mailTemplate.setActive(true);

		// Display the dialog page.
		doShowDialogPage(mailTemplate);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onMailTemplateItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxMailTemplate.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		MailTemplate mailTemplate = mailTemplateService.getMailTemplateById(id);

		if (mailTemplate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND TemplateCode='" + mailTemplate.getTemplateCode() + "' AND version="
				+ mailTemplate.getVersion() + " ";

		if (doCheckAuthority(mailTemplate, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && mailTemplate.getWorkflowId() == 0) {
				mailTemplate.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(mailTemplate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param mailTemplate
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(MailTemplate mailTemplate) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("mailTemplate", mailTemplate);
		arg.put("mailTemplateListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Mail/MailTemplate/MailTemplateDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}
}