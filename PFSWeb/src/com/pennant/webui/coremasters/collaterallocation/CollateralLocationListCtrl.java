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
 * FileName    		:  CollateralLocationListCtrl.java                                                   * 	  
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

package com.pennant.webui.coremasters.collaterallocation;


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
import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.service.coremasters.CollateralLocationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.coremasters.collaterallocation.model.CollateralLocationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/coremasters/CollateralLocation/CollateralLocationList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CollateralLocationListCtrl extends GFCBaseListCtrl<CollateralLocation> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralLocationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralLocationList; // autowired
	protected Borderlayout borderLayout_CollateralLocationList; // autowired
	protected Paging pagingCollateralLocationList; // autowired
	protected Listbox listBoxCollateralLocation; // autowired

	// List headers
	protected Listheader listheader_HZCLO; // autowired
	protected Listheader listheader_HZCLC; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CollateralLocationList_NewCollateralLocation; // autowired
	protected Button button_CollateralLocationList_CollateralLocationSearchDialog; // autowired
	protected Button button_CollateralLocationList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CollateralLocation> searchObj;
	
	private transient CollateralLocationService collateralLocationService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CollateralLocationListCtrl() {
		super();
	}

	public void onCreate$window_CollateralLocationList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CollateralLocation");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CollateralLocation");
			
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
		
		this.borderLayout_CollateralLocationList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCollateralLocationList.setPageSize(getListRows());
		this.pagingCollateralLocationList.setDetailed(true);

		this.listheader_HZCLO.setSortAscending(new FieldComparator("hZCLO", true));
		this.listheader_HZCLO.setSortDescending(new FieldComparator("hZCLO", false));
		this.listheader_HZCLC.setSortAscending(new FieldComparator("hZCLC", true));
		this.listheader_HZCLC.setSortDescending(new FieldComparator("hZCLC", false));
		
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
		this.searchObj = new JdbcSearchObject<CollateralLocation>(CollateralLocation.class,getListRows());
		this.searchObj.addSort("HZCLO", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("HZPF_View");
			if (isFirstTask()) {
				button_CollateralLocationList_NewCollateralLocation.setVisible(true);
			} else {
				button_CollateralLocationList_NewCollateralLocation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("HZPF_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CollateralLocationList_NewCollateralLocation.setVisible(false);
			this.button_CollateralLocationList_CollateralLocationSearchDialog.setVisible(false);
			this.button_CollateralLocationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCollateralLocation,this.pagingCollateralLocationList);
			// set the itemRenderer
			this.listBoxCollateralLocation.setItemRenderer(new CollateralLocationListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CollateralLocationList");
		
		this.button_CollateralLocationList_NewCollateralLocation.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationList_NewCollateralLocation"));
		this.button_CollateralLocationList_CollateralLocationSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationList_CollateralLocationFindDialog"));
		this.button_CollateralLocationList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.coremasters.collaterallocation.model.CollateralLocationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCollateralLocationItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CollateralLocation object
		final Listitem item = this.listBoxCollateralLocation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CollateralLocation aCollateralLocation = (CollateralLocation) item.getAttribute("data");
			final CollateralLocation collateralLocation = getCollateralLocationService().getCollateralLocationById(aCollateralLocation.getId());
			
			if(collateralLocation==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCollateralLocation.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_HZCLO")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND HZCLO='"+ collateralLocation.getHZCLO()+"' AND version=" + collateralLocation.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CollateralLocation", whereCond, collateralLocation.getTaskId(), collateralLocation.getNextTaskId());
					if (userAcces){
						showDetailView(collateralLocation);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(collateralLocation);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CollateralLocation dialog with a new empty entry. <br>
	 */
	public void onClick$button_CollateralLocationList_NewCollateralLocation(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new CollateralLocation object, We GET it from the backend.
		final CollateralLocation aCollateralLocation = getCollateralLocationService().getNewCollateralLocation();
		showDetailView(aCollateralLocation);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CollateralLocation (aCollateralLocation)
	 * @throws Exception
	 */
	private void showDetailView(CollateralLocation aCollateralLocation) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCollateralLocation.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCollateralLocation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralLocation", aCollateralLocation);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CollateralLocationListbox from the
		 * dialog when we do a delete, edit or insert a CollateralLocation.
		 */
		map.put("collateralLocationListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/coremasters/CollateralLocation/CollateralLocationDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CollateralLocationList);
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
		this.pagingCollateralLocationList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CollateralLocationList, event);
		this.window_CollateralLocationList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the CollateralLocation dialog
	 */
	
	public void onClick$button_CollateralLocationList_CollateralLocationSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our CollateralLocationDialog zul-file with parameters. So we can
		 * call them with a object of the selected CollateralLocation. For handed over
		 * these parameter only a Map is accepted. So we put the CollateralLocation object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralLocationCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/coremasters/CollateralLocation/CollateralLocationSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the collateralLocation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CollateralLocationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("CollateralLocation", getSearchObj(),this.pagingCollateralLocationList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setCollateralLocationService(CollateralLocationService collateralLocationService) {
		this.collateralLocationService = collateralLocationService;
	}

	public CollateralLocationService getCollateralLocationService() {
		return this.collateralLocationService;
	}

	public JdbcSearchObject<CollateralLocation> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CollateralLocation> searchObj) {
		this.searchObj = searchObj;
	}
}