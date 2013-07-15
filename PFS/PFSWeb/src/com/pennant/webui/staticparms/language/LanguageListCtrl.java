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

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.service.staticparms.LanguageService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.staticparms.language.model.LanguageListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/Language/LanguageList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class LanguageListCtrl extends GFCBaseListCtrl<Language> implements Serializable {

	private static final long serialVersionUID = 6399482879167400531L;
	private final static Logger logger = Logger.getLogger(LanguageListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_LanguageList; 		// autoWired
	protected Borderlayout 	borderLayout_LanguageList; 	// autoWired
	protected Paging 		pagingLanguageList;			// autoWired
	protected Listbox 		listBoxLanguage; 			// autoWired

	// List headers
	protected Listheader listheader_LngCode; 			// autoWired
	protected Listheader listheader_LngDesc; 			// autoWired
	protected Listheader listheader_LngNumber; 			// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_LanguageList_NewLanguage; 			// autoWired
	protected Button button_LanguageList_LanguageSearchDialog; 	// autoWired
	protected Button button_LanguageList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Language> searchObj;

	private transient LanguageService languageService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public LanguageListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Language object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LanguageList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Language");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Language");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_LanguageList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingLanguageList.setPageSize(getListRows());
		this.pagingLanguageList.setDetailed(true);

		this.listheader_LngCode.setSortAscending(new FieldComparator("lngCode",true));
		this.listheader_LngCode.setSortDescending(new FieldComparator("lngCode", false));
		this.listheader_LngDesc.setSortAscending(new FieldComparator("lngDesc",true));
		this.listheader_LngDesc.setSortDescending(new FieldComparator("lngDesc", false));
		this.listheader_LngNumber.setSortAscending(new FieldComparator("lngNumber", true));
		this.listheader_LngNumber.setSortDescending(new FieldComparator("lngNumber", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<Language>(Language.class,	getListRows());
		this.searchObj.addSort("LngCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTLanguage_View");
			if (isFirstTask()) {
				button_LanguageList_NewLanguage.setVisible(true);
			} else {
				button_LanguageList_NewLanguage.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else{
			this.searchObj.addTabelName("BMTLanguage_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_LanguageList_NewLanguage.setVisible(false);
			this.button_LanguageList_LanguageSearchDialog.setVisible(false);
			this.button_LanguageList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxLanguage,this.pagingLanguageList);
			// set the itemRenderer
			this.listBoxLanguage.setItemRenderer(new LanguageListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("LanguageList");

		this.button_LanguageList_NewLanguage.setVisible(getUserWorkspace()
				.isAllowed("button_LanguageList_NewLanguage"));
		this.button_LanguageList_LanguageSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_LanguageList_LanguageFindDialog"));
		this.button_LanguageList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_LanguageList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.language.model.
	 * LanguageListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onLanguageItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Language object
		final Listitem item = this.listBoxLanguage.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Language aLanguage = (Language) item.getAttribute("data");
			final Language language = getLanguageService().getLanguageById(aLanguage.getId());

			if (language == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aLanguage.getLngCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_LngCode") + ":" + aLanguage.getLngCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND LngCode='" + language.getLngCode()
				+ "' AND version=" + language.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Language", whereCond, language.getTaskId(),language.getNextTaskId());
					if (userAcces) {
						showDetailView(language);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(language);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Language dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_LanguageList_NewLanguage(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Language object, We GET it from the back end.
		final Language aLanguage = getLanguageService().getNewLanguage();
		showDetailView(aLanguage);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Language
	 *            (aLanguage)
	 * @throws Exception
	 */
	private void showDetailView(Language aLanguage) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aLanguage.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aLanguage.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("language", aLanguage);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the LanguageListbox from the
		 * dialog when we do a delete, edit or insert a Language.
		 */
		map.put("languageListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/Language/LanguageDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_LanguageList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.pagingLanguageList.setActivePage(0);
		Events.postEvent("onCreate", this.window_LanguageList, event);
		this.window_LanguageList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Language dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_LanguageList_LanguageSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our LanguageDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Language. For handed over
		 * these parameter only a Map is accepted. So we put the Language object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("languageCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/Language/LanguageSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the language print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_LanguageList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Language", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	public LanguageService getLanguageService() {
		return this.languageService;
	}

	public JdbcSearchObject<Language> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Language> searchObj) {
		this.searchObj = searchObj;
	}
}