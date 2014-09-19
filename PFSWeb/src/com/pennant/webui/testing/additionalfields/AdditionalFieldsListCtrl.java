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
 * FileName    		:  AdditionalFieldsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2011    														*
 *                                                                  						*
 * Modified Date    :  22-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.testing.additionalfields;


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
import com.pennant.backend.model.testing.AdditionalFields;
import com.pennant.backend.service.testing.AdditionalFieldsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.testing.additionalfields.model.AdditionalFieldsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Testing/AdditionalFields/AdditionalFieldsList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AdditionalFieldsListCtrl extends GFCBaseListCtrl<AdditionalFields> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AdditionalFieldsListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AdditionalFieldsList; // autowired
	protected Borderlayout borderLayout_AdditionalFieldsList; // autowired
	protected Paging pagingAdditionalFieldsList; // autowired
	protected Listbox listBoxAdditionalFields; // autowired

	// List headers
	protected Listheader listheader_Code; // autowired
	protected Listheader listheader_Description; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_AdditionalFieldsList_NewAdditionalFields; // autowired
	protected Button button_AdditionalFieldsList_AdditionalFieldsSearchDialog; // autowired
	protected Button button_AdditionalFieldsList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AdditionalFields> searchObj;
	
	private transient AdditionalFieldsService AdditionalFieldsService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public AdditionalFieldsListCtrl() {
		super();
	}

	public void onCreate$window_AdditionalFieldsList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AdditionalFields");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AdditionalFields");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_AdditionalFieldsList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingAdditionalFieldsList.setPageSize(getListRows());
		this.pagingAdditionalFieldsList.setDetailed(true);

		this.listheader_Code.setSortAscending(new FieldComparator("code", true));
		this.listheader_Code.setSortDescending(new FieldComparator("code", false));
		this.listheader_Description.setSortAscending(new FieldComparator("description", true));
		this.listheader_Description.setSortDescending(new FieldComparator("description", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<AdditionalFields>(AdditionalFields.class,getListRows());
		this.searchObj.addSort("Code", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("AdditionalFields_View");
			if (isFirstTask()) {
				button_AdditionalFieldsList_NewAdditionalFields.setVisible(true);
			} else {
				button_AdditionalFieldsList_NewAdditionalFields.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("AdditionalFields_View");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_AdditionalFieldsList_NewAdditionalFields.setVisible(false);
			this.button_AdditionalFieldsList_AdditionalFieldsSearchDialog.setVisible(false);
			this.button_AdditionalFieldsList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxAdditionalFields,this.pagingAdditionalFieldsList);
			// set the itemRenderer
			this.listBoxAdditionalFields.setItemRenderer(new AdditionalFieldsListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AdditionalFieldsList");
		
		this.button_AdditionalFieldsList_NewAdditionalFields.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsList_NewAdditionalFields"));
		this.button_AdditionalFieldsList_AdditionalFieldsSearchDialog.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsList_AdditionalFieldsFindDialog"));
		this.button_AdditionalFieldsList_PrintList.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.testing.AdditionalFields.model.AdditionalFieldsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onAdditionalFieldsItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected AdditionalFields object
		final Listitem item = this.listBoxAdditionalFields.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AdditionalFields aAdditionalFields = (AdditionalFields) item.getAttribute("data");
			final AdditionalFields additionalFields = getAdditionalFieldsService().getAdditionalFieldsById(aAdditionalFields.getId());
			
			if(additionalFields==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aAdditionalFields.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_Code")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND Code='"+ additionalFields.getCode()+"' AND version=" + additionalFields.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "AdditionalFields", whereCond, additionalFields.getTaskId(), additionalFields.getNextTaskId());
					if (userAcces){
						showDetailView(additionalFields);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(additionalFields);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the AdditionalFields dialog with a new empty entry. <br>
	 */
	public void onClick$button_AdditionalFieldsList_NewAdditionalFields(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new AdditionalFields object, We GET it from the backend.
		final AdditionalFields aAdditionalFields = getAdditionalFieldsService().getNewAdditionalFields();
		
		showDetailView(aAdditionalFields);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param AdditionalFields (aAdditionalFields)
	 * @throws Exception
	 */
	private void showDetailView(AdditionalFields aAdditionalFields) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aAdditionalFields.getWorkflowId()==0 && isWorkFlowEnabled()){
			aAdditionalFields.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AdditionalFields", aAdditionalFields);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the AdditionalFieldsListbox from the
		 * dialog when we do a delete, edit or insert a AdditionalFields.
		 */
		map.put("additionalFieldsListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Testing/AdditionalFields/AdditionalFieldsDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_AdditionalFieldsList);
		logger.debug("Leaving");
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
		logger.debug(event.toString());
		this.pagingAdditionalFieldsList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AdditionalFieldsList, event);
		this.window_AdditionalFieldsList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the AdditionalFields dialog
	 */
	
	public void onClick$button_AdditionalFieldsList_AdditionalFieldsSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our AdditionalFieldsDialog zul-file with parameters. So we can
		 * call them with a object of the selected AdditionalFields. For handed over
		 * these parameter only a Map is accepted. So we put the AdditionalFields object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AdditionalFieldsCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Testing/AdditionalFields/AdditionalFieldsSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the AdditionalFields print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_AdditionalFieldsList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("AdditionalFields", getSearchObj());
		logger.debug("Leaving");
	}

	public void setAdditionalFieldsService(AdditionalFieldsService additionalFieldsService) {
		this.AdditionalFieldsService = additionalFieldsService;
	}

	public AdditionalFieldsService getAdditionalFieldsService() {
		return this.AdditionalFieldsService;
	}

	public JdbcSearchObject<AdditionalFields> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<AdditionalFields> searchObj) {
		this.searchObj = searchObj;
	}
}