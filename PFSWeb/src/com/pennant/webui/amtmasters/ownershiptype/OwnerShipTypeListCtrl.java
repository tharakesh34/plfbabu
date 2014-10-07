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
 * FileName    		:  OwnerShipTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.ownershiptype;


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
import com.pennant.backend.model.amtmasters.OwnerShipType;
import com.pennant.backend.service.amtmasters.OwnerShipTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.ownershiptype.model.OwnerShipTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/OwnerShipType/OwnerShipTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class OwnerShipTypeListCtrl extends GFCBaseListCtrl<OwnerShipType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(OwnerShipTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_OwnerShipTypeList; // autowired
	protected Borderlayout borderLayout_OwnerShipTypeList; // autowired
	protected Paging pagingOwnerShipTypeList; // autowired
	protected Listbox listBoxOwnerShipType; // autowired

	// List headers
	protected Listheader listheader_OwnerShipTypeName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_OwnerShipTypeList_NewOwnerShipType; // autowired
	protected Button button_OwnerShipTypeList_OwnerShipTypeSearchDialog; // autowired
	protected Button button_OwnerShipTypeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<OwnerShipType> searchObj;
	
	private transient OwnerShipTypeService ownerShipTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public OwnerShipTypeListCtrl() {
		super();
	}

	public void onCreate$window_OwnerShipTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("OwnerShipType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("OwnerShipType");
			
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
		
		this.borderLayout_OwnerShipTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingOwnerShipTypeList.setPageSize(getListRows());
		this.pagingOwnerShipTypeList.setDetailed(true);

		this.listheader_OwnerShipTypeName.setSortAscending(new FieldComparator("ownerShipTypeName", true));
		this.listheader_OwnerShipTypeName.setSortDescending(new FieldComparator("ownerShipTypeName", false));
		
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
		this.searchObj = new JdbcSearchObject<OwnerShipType>(OwnerShipType.class,getListRows());
		this.searchObj.addSort("OwnerShipTypeId", false);

		this.searchObj.addTabelName("AMTOwnerShipType_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_OwnerShipTypeList_NewOwnerShipType.setVisible(true);
			} else {
				button_OwnerShipTypeList_NewOwnerShipType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_OwnerShipTypeList_NewOwnerShipType.setVisible(false);
			this.button_OwnerShipTypeList_OwnerShipTypeSearchDialog.setVisible(false);
			this.button_OwnerShipTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxOwnerShipType,this.pagingOwnerShipTypeList);
			// set the itemRenderer
			this.listBoxOwnerShipType.setItemRenderer(new OwnerShipTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("OwnerShipTypeList");
		
		this.button_OwnerShipTypeList_NewOwnerShipType.setVisible(getUserWorkspace().isAllowed("button_OwnerShipTypeList_NewOwnerShipType"));
		this.button_OwnerShipTypeList_OwnerShipTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_OwnerShipTypeList_OwnerShipTypeFindDialog"));
		this.button_OwnerShipTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_OwnerShipTypeList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.ownershiptype.model.OwnerShipTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onOwnerShipTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected OwnerShipType object
		final Listitem item = this.listBoxOwnerShipType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final OwnerShipType aOwnerShipType = (OwnerShipType) item.getAttribute("data");
			final OwnerShipType ownerShipType = getOwnerShipTypeService().getOwnerShipTypeById(aOwnerShipType.getId());
			
			if(ownerShipType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aOwnerShipType.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_OwnerShipTypeId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND OwnerShipTypeId="+ ownerShipType.getOwnerShipTypeId()+" AND version=" + ownerShipType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "OwnerShipType", whereCond, ownerShipType.getTaskId(), ownerShipType.getNextTaskId());
					if (userAcces){
						showDetailView(ownerShipType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(ownerShipType);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the OwnerShipType dialog with a new empty entry. <br>
	 */
	public void onClick$button_OwnerShipTypeList_NewOwnerShipType(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new OwnerShipType object, We GET it from the backend.
		final OwnerShipType aOwnerShipType = getOwnerShipTypeService().getNewOwnerShipType();
		showDetailView(aOwnerShipType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param OwnerShipType (aOwnerShipType)
	 * @throws Exception
	 */
	private void showDetailView(OwnerShipType aOwnerShipType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aOwnerShipType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aOwnerShipType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ownerShipType", aOwnerShipType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the OwnerShipTypeListbox from the
		 * dialog when we do a delete, edit or insert a OwnerShipType.
		 */
		map.put("ownerShipTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/OwnerShipType/OwnerShipTypeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_OwnerShipTypeList);
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
		this.pagingOwnerShipTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_OwnerShipTypeList, event);
		this.window_OwnerShipTypeList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the OwnerShipType dialog
	 */
	
	public void onClick$button_OwnerShipTypeList_OwnerShipTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our OwnerShipTypeDialog zul-file with parameters. So we can
		 * call them with a object of the selected OwnerShipType. For handed over
		 * these parameter only a Map is accepted. So we put the OwnerShipType object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ownerShipTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/OwnerShipType/OwnerShipTypeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the ownerShipType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_OwnerShipTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("OwnerShipType", getSearchObj(),this.pagingOwnerShipTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setOwnerShipTypeService(OwnerShipTypeService ownerShipTypeService) {
		this.ownerShipTypeService = ownerShipTypeService;
	}

	public OwnerShipTypeService getOwnerShipTypeService() {
		return this.ownerShipTypeService;
	}

	public JdbcSearchObject<OwnerShipType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<OwnerShipType> searchObj) {
		this.searchObj = searchObj;
	}
}