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
 * FileName    		:  LanguageListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.language;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.service.staticparms.LanguageService;
import com.pennant.webui.staticparms.language.model.LanguageListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/Language/LanguageList.zul file.
 */
public class LanguageListCtrl extends GFCBaseListCtrl<Language> {
	private static final long serialVersionUID = 6399482879167400531L;
	private static final Logger logger = Logger.getLogger(LanguageListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LanguageList;
	protected Borderlayout borderLayout_LanguageList;
	protected Paging pagingLanguageList;
	protected Listbox listBoxLanguage;

	protected Listheader listheader_LngCode;
	protected Listheader listheader_LngDesc;
	protected Listheader listheader_LngNumber;

	protected Button button_LanguageList_NewLanguage;
	protected Button button_LanguageList_LanguageSearchDialog;

	protected Textbox lngCode;
	protected Textbox lngDesc;
	protected Intbox lngNumber;

	protected Listbox sortOperator_lngCode;
	protected Listbox sortOperator_lngDesc;
	protected Listbox sortOperator_lngNumber;

	private transient LanguageService languageService;

	/**
	 * default constructor.<br>
	 */
	public LanguageListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Language";
		super.pageRightName = "LanguageList";
		super.tableName = "BMTLanguage_AView";
		super.queueTableName = "BMTLanguage_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LanguageList(Event event) {
		// Set the page level components.
		setPageComponents(window_LanguageList, borderLayout_LanguageList, listBoxLanguage, pagingLanguageList);
		setItemRender(new LanguageListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LanguageList_NewLanguage, "button_LanguageList_NewLanguage", true);
		registerButton(button_LanguageList_LanguageSearchDialog);

		registerField("lngCode", listheader_LngCode, SortOrder.ASC, lngCode, sortOperator_lngCode, Operators.STRING);
		registerField("lngDesc", listheader_LngDesc, SortOrder.NONE, lngDesc, sortOperator_lngDesc, Operators.STRING);
		registerField("lngNumber", listheader_LngNumber, SortOrder.NONE, lngNumber, sortOperator_lngNumber,
				Operators.NUMERIC);

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
	public void onClick$button_LanguageList_LanguageSearchDialog(Event event) {
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
	public void onClick$button_LanguageList_NewLanguage(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Language language = new Language();
		language.setNewRecord(true);
		language.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(language);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLanguageItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLanguage.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Language language = languageService.getLanguageById(id);

		if (language == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND LngCode='" + language.getLngCode() + "' AND version=" + language.getVersion() + " ";

		if (doCheckAuthority(language, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && language.getWorkflowId() == 0) {
				language.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(language);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aLanguage
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Language aLanguage) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("language", aLanguage);
		arg.put("languageListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/Language/LanguageDialog.zul", null, arg);
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

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
}