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
 * FileName    		:  CollateralTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collateraltype;


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
import com.pennant.backend.model.coremasters.CollateralType;
import com.pennant.backend.service.coremasters.CollateralTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.coremasters.collateraltype.model.CollateralTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/coremasters/CollateralType/CollateralTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CollateralTypeListCtrl extends GFCBaseListCtrl<CollateralType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralTypeList; // autowired
	protected Borderlayout borderLayout_CollateralTypeList; // autowired
	protected Paging pagingCollateralTypeList; // autowired
	protected Listbox listBoxCollateralType; // autowired

	// List headers
	protected Listheader listheader_HWCLP; // autowired
	protected Listheader listheader_HWCPD; // autowired
	protected Listheader listheader_HWBVM; // autowired
	protected Listheader listheader_HWINS; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CollateralTypeList_NewCollateralType; // autowired
	protected Button button_CollateralTypeList_CollateralTypeSearchDialog; // autowired
	protected Button button_CollateralTypeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CollateralType> searchObj;
	
	private transient CollateralTypeService collateralTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CollateralTypeListCtrl() {
		super();
	}

	public void onCreate$window_CollateralTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CollateralType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CollateralType");
			
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
		//doCheckRights();
		
		this.borderLayout_CollateralTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCollateralTypeList.setPageSize(getListRows());
		this.pagingCollateralTypeList.setDetailed(true);

		this.listheader_HWCLP.setSortAscending(new FieldComparator("hWCLP", true));
		this.listheader_HWCLP.setSortDescending(new FieldComparator("hWCLP", false));
		this.listheader_HWCPD.setSortAscending(new FieldComparator("hWCPD", true));
		this.listheader_HWCPD.setSortDescending(new FieldComparator("hWCPD", false));
		this.listheader_HWBVM.setSortAscending(new FieldComparator("hWBVM", true));
		this.listheader_HWBVM.setSortDescending(new FieldComparator("hWBVM", false));
		this.listheader_HWINS.setSortAscending(new FieldComparator("hWINS", true));
		this.listheader_HWINS.setSortDescending(new FieldComparator("hWINS", false));
		
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
		this.searchObj = new JdbcSearchObject<CollateralType>(CollateralType.class,getListRows());
		this.searchObj.addSort("HWCLP", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("HWPF_View");
			if (isFirstTask()) {
				button_CollateralTypeList_NewCollateralType.setVisible(true);
			} else {
				button_CollateralTypeList_NewCollateralType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("HWPF_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CollateralTypeList_NewCollateralType.setVisible(false);
			this.button_CollateralTypeList_CollateralTypeSearchDialog.setVisible(false);
			this.button_CollateralTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCollateralType,this.pagingCollateralTypeList);
			// set the itemRenderer
			this.listBoxCollateralType.setItemRenderer(new CollateralTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CollateralTypeList");
		
		this.button_CollateralTypeList_NewCollateralType.setVisible(getUserWorkspace().isAllowed("button_CollateralTypeList_NewCollateralType"));
		this.button_CollateralTypeList_CollateralTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CollateralTypeList_CollateralTypeFindDialog"));
		this.button_CollateralTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CollateralTypeList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.coremasters.collateraltype.model.CollateralTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCollateralTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CollateralType object
		final Listitem item = this.listBoxCollateralType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CollateralType aCollateralType = (CollateralType) item.getAttribute("data");
			final CollateralType collateralType = getCollateralTypeService().getCollateralTypeById(aCollateralType.getId());
			
			if(collateralType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCollateralType.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_HWCLP")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND HWCLP='"+ collateralType.getHWCLP()+"' AND version=" + collateralType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CollateralType", whereCond, collateralType.getTaskId(), collateralType.getNextTaskId());
					if (userAcces){
						showDetailView(collateralType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(collateralType);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CollateralType dialog with a new empty entry. <br>
	 */
	public void onClick$button_CollateralTypeList_NewCollateralType(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new CollateralType object, We GET it from the backend.
		final CollateralType aCollateralType = getCollateralTypeService().getNewCollateralType();
		showDetailView(aCollateralType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CollateralType (aCollateralType)
	 * @throws Exception
	 */
	private void showDetailView(CollateralType aCollateralType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCollateralType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCollateralType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralType", aCollateralType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CollateralTypeListbox from the
		 * dialog when we do a delete, edit or insert a CollateralType.
		 */
		map.put("collateralTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/coremasters/CollateralType/CollateralTypeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CollateralTypeList);
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
		this.pagingCollateralTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CollateralTypeList, event);
		this.window_CollateralTypeList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the CollateralType dialog
	 */
	
	public void onClick$button_CollateralTypeList_CollateralTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our CollateralTypeDialog zul-file with parameters. So we can
		 * call them with a object of the selected CollateralType. For handed over
		 * these parameter only a Map is accepted. So we put the CollateralType object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/coremasters/CollateralType/CollateralTypeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the collateralType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CollateralTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("CollateralType", getSearchObj(),this.pagingCollateralTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setCollateralTypeService(CollateralTypeService collateralTypeService) {
		this.collateralTypeService = collateralTypeService;
	}

	public CollateralTypeService getCollateralTypeService() {
		return this.collateralTypeService;
	}

	public JdbcSearchObject<CollateralType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CollateralType> searchObj) {
		this.searchObj = searchObj;
	}
}