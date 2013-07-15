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
 * FileName    		:  CheckListListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.checklist;


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
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.checklist.model.CheckListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CheckList/CheckListList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CheckListListCtrl extends GFCBaseListCtrl<CheckList> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CheckListListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CheckListList;                         // autoWired
	protected Borderlayout borderLayout_CheckListList;             // autoWired
	protected Paging pagingCheckListList;                          // autoWired
	protected Listbox listBoxCheckList;                            // autoWired

	// List headers
	protected Listheader listheader_CheckListDesc;                 // autoWired
	protected Listheader listheader_Active;                        // autoWired
	protected Listheader listheader_RecordStatus;                  // autoWired
	protected Listheader listheader_RecordType;                    // autoWired
	protected Listheader listheader_CheckListMaxCount;             // autoWired
	protected Listheader listheader_CheckListMinCount;             // autoWired

	// checkRights
	protected Button btnHelp;                                       // autoWired
	protected Button button_CheckListList_NewCheckList;             // autoWired
	protected Button button_CheckListList_CheckListSearchDialog;    // autoWired
	protected Button button_CheckListList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CheckList> searchObj;
	
	private transient CheckListService checkListService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CheckListListCtrl() {
		super();
	}

	public void onCreate$window_CheckListList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CheckList");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CheckList");
			
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
		
		this.borderLayout_CheckListList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCheckListList.setPageSize(getListRows());
		this.pagingCheckListList.setDetailed(true);

		this.listheader_CheckListDesc.setSortAscending(new FieldComparator("checkListDesc", true));
		this.listheader_CheckListDesc.setSortDescending(new FieldComparator("checkListDesc", false));
		this.listheader_Active.setSortAscending(new FieldComparator("active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("active", false));
		this.listheader_CheckListMinCount.setSortAscending(new FieldComparator("checkMinCount", true));
		this.listheader_CheckListMinCount.setSortDescending(new FieldComparator("checkMinCount", false));
		this.listheader_CheckListMaxCount.setSortAscending(new FieldComparator("checkMaxCount", true));
		this.listheader_CheckListMaxCount.setSortDescending(new FieldComparator("checkMaxCount", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<CheckList>(CheckList.class,getListRows());
		this.searchObj.addSort("CheckListId", false);
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCheckList_View");
			if (isFirstTask()) {
				button_CheckListList_NewCheckList.setVisible(true);
			} else {
				button_CheckListList_NewCheckList.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTCheckList_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CheckListList_NewCheckList.setVisible(false);
			this.button_CheckListList_CheckListSearchDialog.setVisible(false);
			this.button_CheckListList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCheckList,this.pagingCheckListList);
			// set the itemRenderer
			this.listBoxCheckList.setItemRenderer(new CheckListListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CheckListList");
		this.button_CheckListList_NewCheckList.setVisible(getUserWorkspace().isAllowed("button_CheckListList_NewCheckList"));
		this.button_CheckListList_CheckListSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CheckListList_CheckListFindDialog"));
		this.button_CheckListList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CheckListList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.checklist.model.CheckListListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCheckListItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CheckList object
		final Listitem item = this.listBoxCheckList.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CheckList aCheckList = (CheckList) item.getAttribute("data");
			final CheckList checkList = getCheckListService().getCheckListById(aCheckList.getId());
			
			if(checkList==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aCheckList.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_CheckListId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CheckListId="+ checkList.getCheckListId()+" AND version=" + checkList.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CheckList", whereCond, checkList.getTaskId(), checkList.getNextTaskId());
					if (userAcces){
						showDetailView(checkList);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(checkList);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CheckList dialog with a new empty entry. <br>
	 */
	public void onClick$button_CheckListList_NewCheckList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CheckList object, We GET it from the backEnd.
		final CheckList aCheckList = getCheckListService().getNewCheckList();
		showDetailView(aCheckList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CheckList (aCheckList)
	 * @throws Exception
	 */
	private void showDetailView(CheckList aCheckList) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCheckList.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCheckList.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("checkList", aCheckList);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CheckListListbox from the
		 * dialog when we do a delete, edit or insert a CheckList.
		 */
		map.put("checkListListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CheckListList);
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
		this.pagingCheckListList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CheckListList, event);
		this.window_CheckListList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the CheckList dialog
	 */
	
	public void onClick$button_CheckListList_CheckListSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CheckListDialog zul-file with parameters. So we can
		 * call them with a object of the selected CheckList. For handed over
		 * these parameter only a Map is accepted. So we put the CheckList object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("checkListCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the checkList print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CheckListList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CheckList", getSearchObj(),this.pagingCheckListList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setCheckListService(CheckListService checkListService) {
		this.checkListService = checkListService;
	}

	public CheckListService getCheckListService() {
		return this.checkListService;
	}

	public JdbcSearchObject<CheckList> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CheckList> searchObj) {
		this.searchObj = searchObj;
	}
}