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
 * FileName    		:  SystemInternalAccountDefinitionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.masters.systeminternalaccountdefinition;


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
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.masters.systeminternalaccountdefinition.model.SystemInternalAccountDefinitionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Account/SystemInternalAccountDefinition/SystemInternalAccountDefinitionList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SystemInternalAccountDefinitionListCtrl extends GFCBaseListCtrl<SystemInternalAccountDefinition> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SystemInternalAccountDefinitionList; // autowired
	protected Borderlayout borderLayout_SystemInternalAccountDefinitionList; // autowired
	protected Paging pagingSystemInternalAccountDefinitionList; // autowired
	protected Listbox listBoxSystemInternalAccountDefinition; // autowired

	// List headers
	protected Listheader listheader_SIACode; // autowired
	protected Listheader listheader_SIAName; // autowired
	protected Listheader listheader_SIAShortName; // autowired
	protected Listheader listheader_SIAAcType; // autowired
	protected Listheader listheader_SIANumber; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition; // autowired
	protected Button button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog; // autowired
	protected Button button_SystemInternalAccountDefinitionList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SystemInternalAccountDefinition> searchObj;
	
	private transient SystemInternalAccountDefinitionService systemInternalAccountDefinitionService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public SystemInternalAccountDefinitionListCtrl() {
		super();
	}

	public void onCreate$window_SystemInternalAccountDefinitionList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SystemInternalAccountDefinition");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SystemInternalAccountDefinition");
			
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
		
		this.borderLayout_SystemInternalAccountDefinitionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSystemInternalAccountDefinitionList.setPageSize(getListRows());
		this.pagingSystemInternalAccountDefinitionList.setDetailed(true);

		this.listheader_SIACode.setSortAscending(new FieldComparator("sIACode", true));
		this.listheader_SIACode.setSortDescending(new FieldComparator("sIACode", false));
		this.listheader_SIAName.setSortAscending(new FieldComparator("sIAName", true));
		this.listheader_SIAName.setSortDescending(new FieldComparator("sIAName", false));
		this.listheader_SIAShortName.setSortAscending(new FieldComparator("sIAShortName", true));
		this.listheader_SIAShortName.setSortDescending(new FieldComparator("sIAShortName", false));
		this.listheader_SIAAcType.setSortAscending(new FieldComparator("sIAAcType", true));
		this.listheader_SIAAcType.setSortDescending(new FieldComparator("sIAAcType", false));
		this.listheader_SIANumber.setSortAscending(new FieldComparator("sIANumber", true));
		this.listheader_SIANumber.setSortDescending(new FieldComparator("sIANumber", false));
		
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
		this.searchObj = new JdbcSearchObject<SystemInternalAccountDefinition>(SystemInternalAccountDefinition.class,getListRows());
		this.searchObj.addSort("SIACode", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("SystemInternalAccountDef_View");
			if (isFirstTask()) {
				button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition.setVisible(true);
			} else {
				button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("SystemInternalAccountDef_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition.setVisible(false);
			this.button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog.setVisible(false);
			this.button_SystemInternalAccountDefinitionList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSystemInternalAccountDefinition,this.pagingSystemInternalAccountDefinitionList);
			// set the itemRenderer
			this.listBoxSystemInternalAccountDefinition.setItemRenderer(new SystemInternalAccountDefinitionListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SystemInternalAccountDefinitionList");
		
		this.button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition"));
		this.button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionFindDialog"));
		this.button_SystemInternalAccountDefinitionList_PrintList.setVisible(getUserWorkspace().isAllowed("button_SystemInternalAccountDefinitionList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.masters.systeminternalaccountdefinition.model.SystemInternalAccountDefinitionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onSystemInternalAccountDefinitionItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected SystemInternalAccountDefinition object
		final Listitem item = this.listBoxSystemInternalAccountDefinition.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SystemInternalAccountDefinition aSystemInternalAccountDefinition = (SystemInternalAccountDefinition) item.getAttribute("data");
			final SystemInternalAccountDefinition systemInternalAccountDefinition = getSystemInternalAccountDefinitionService().getSystemInternalAccountDefinitionById(aSystemInternalAccountDefinition.getId());
			
			if(systemInternalAccountDefinition==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aSystemInternalAccountDefinition.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_SIACode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND SIACode='"+ systemInternalAccountDefinition.getSIACode()+"' AND version=" + systemInternalAccountDefinition.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "SystemInternalAccountDefinition", whereCond, systemInternalAccountDefinition.getTaskId(), systemInternalAccountDefinition.getNextTaskId());
					if (userAcces){
						showDetailView(systemInternalAccountDefinition);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(systemInternalAccountDefinition);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the SystemInternalAccountDefinition dialog with a new empty entry. <br>
	 */
	public void onClick$button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new SystemInternalAccountDefinition object, We GET it from the backend.
		final SystemInternalAccountDefinition aSystemInternalAccountDefinition = getSystemInternalAccountDefinitionService().getNewSystemInternalAccountDefinition();
		showDetailView(aSystemInternalAccountDefinition);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param SystemInternalAccountDefinition (aSystemInternalAccountDefinition)
	 * @throws Exception
	 */
	private void showDetailView(SystemInternalAccountDefinition aSystemInternalAccountDefinition) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aSystemInternalAccountDefinition.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSystemInternalAccountDefinition.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("systemInternalAccountDefinition", aSystemInternalAccountDefinition);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the SystemInternalAccountDefinitionListbox from the
		 * dialog when we do a delete, edit or insert a SystemInternalAccountDefinition.
		 */
		map.put("systemInternalAccountDefinitionListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Account/SystemInternalAccountDefinition/SystemInternalAccountDefinitionDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SystemInternalAccountDefinitionList);
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
		this.pagingSystemInternalAccountDefinitionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SystemInternalAccountDefinitionList, event);
		this.window_SystemInternalAccountDefinitionList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the SystemInternalAccountDefinition dialog
	 */
	
	public void onClick$button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our SystemInternalAccountDefinitionDialog zul-file with parameters. So we can
		 * call them with a object of the selected SystemInternalAccountDefinition. For handed over
		 * these parameter only a Map is accepted. So we put the SystemInternalAccountDefinition object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("systemInternalAccountDefinitionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Account/SystemInternalAccountDefinition/SystemInternalAccountDefinitionSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the systemInternalAccountDefinition print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SystemInternalAccountDefinitionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("SystemInternalAccountDefinition", getSearchObj());
		logger.debug("Leaving");
	}

	public void setSystemInternalAccountDefinitionService(SystemInternalAccountDefinitionService systemInternalAccountDefinitionService) {
		this.systemInternalAccountDefinitionService = systemInternalAccountDefinitionService;
	}

	public SystemInternalAccountDefinitionService getSystemInternalAccountDefinitionService() {
		return this.systemInternalAccountDefinitionService;
	}

	public JdbcSearchObject<SystemInternalAccountDefinition> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<SystemInternalAccountDefinition> searchObj) {
		this.searchObj = searchObj;
	}
}