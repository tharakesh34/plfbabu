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
 * FileName    		:  VehicleModelListCtrl.java                                                   * 	  
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

package com.pennant.webui.amtmasters.vehiclemodel;


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
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.vehiclemodel.model.VehicleModelListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleModel/VehicleModelList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VehicleModelListCtrl extends GFCBaseListCtrl<VehicleModel> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(VehicleModelListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleModelList; // autowired
	protected Borderlayout borderLayout_VehicleModelList; // autowired
	protected Paging pagingVehicleModelList; // autowired
	protected Listbox listBoxVehicleModel; // autowired

	// List headers
	protected Listheader listheader_VehicleManufacterId;
	protected Listheader listheader_VehicleModelDesc; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_VehicleModelList_NewVehicleModel; // autowired
	protected Button button_VehicleModelList_VehicleModelSearchDialog; // autowired
	protected Button button_VehicleModelList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<VehicleModel> searchObj;
	
	private transient VehicleModelService vehicleModelService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public VehicleModelListCtrl() {
		super();
	}

	public void onCreate$window_VehicleModelList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("VehicleModel");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("VehicleModel");
			
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
		
		this.borderLayout_VehicleModelList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingVehicleModelList.setPageSize(getListRows());
		this.pagingVehicleModelList.setDetailed(true);
		
		this.listheader_VehicleManufacterId.setSortAscending(new FieldComparator("Manufacturer", true));
		this.listheader_VehicleManufacterId.setSortDescending(new FieldComparator("Manufacturer", false));
		this.listheader_VehicleModelDesc.setSortAscending(new FieldComparator("vehicleModelDesc", true));
		this.listheader_VehicleModelDesc.setSortDescending(new FieldComparator("vehicleModelDesc", false));
		
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
		this.searchObj = new JdbcSearchObject<VehicleModel>(VehicleModel.class,getListRows());
		this.searchObj.addSort("VehicleModelId", false);

		this.searchObj.addTabelName("AMTVehicleModel_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_VehicleModelList_NewVehicleModel.setVisible(true);
			} else {
				button_VehicleModelList_NewVehicleModel.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_VehicleModelList_NewVehicleModel.setVisible(false);
			this.button_VehicleModelList_VehicleModelSearchDialog.setVisible(false);
			this.button_VehicleModelList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxVehicleModel,this.pagingVehicleModelList);
			// set the itemRenderer
			this.listBoxVehicleModel.setItemRenderer(new VehicleModelListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("VehicleModelList");
		
		this.button_VehicleModelList_NewVehicleModel.setVisible(getUserWorkspace().isAllowed("button_VehicleModelList_NewVehicleModel"));
		this.button_VehicleModelList_VehicleModelSearchDialog.setVisible(getUserWorkspace().isAllowed("button_VehicleModelList_VehicleModelFindDialog"));
		this.button_VehicleModelList_PrintList.setVisible(getUserWorkspace().isAllowed("button_VehicleModelList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.vehiclemodel.model.VehicleModelListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onVehicleModelItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected VehicleModel object
		final Listitem item = this.listBoxVehicleModel.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final VehicleModel aVehicleModel = (VehicleModel) item.getAttribute("data");
			final VehicleModel vehicleModel = getVehicleModelService().getVehicleModelById(aVehicleModel.getId());
			
			if(vehicleModel==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aVehicleModel.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_VehicleModelId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND VehicleModelId="+ vehicleModel.getVehicleModelId()+" AND version=" + vehicleModel.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "VehicleModel", whereCond, vehicleModel.getTaskId(), vehicleModel.getNextTaskId());
					if (userAcces){
						showDetailView(vehicleModel);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(vehicleModel);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the VehicleModel dialog with a new empty entry. <br>
	 */
	public void onClick$button_VehicleModelList_NewVehicleModel(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new VehicleModel object, We GET it from the backend.
		final VehicleModel aVehicleModel = getVehicleModelService().getNewVehicleModel();
		showDetailView(aVehicleModel);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param VehicleModel (aVehicleModel)
	 * @throws Exception
	 */
	private void showDetailView(VehicleModel aVehicleModel) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aVehicleModel.getWorkflowId()==0 && isWorkFlowEnabled()){
			aVehicleModel.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleModel", aVehicleModel);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the VehicleModelListbox from the
		 * dialog when we do a delete, edit or insert a VehicleModel.
		 */
		map.put("vehicleModelListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleModel/VehicleModelDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_VehicleModelList);
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
		this.pagingVehicleModelList.setActivePage(0);
		Events.postEvent("onCreate", this.window_VehicleModelList, event);
		this.window_VehicleModelList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the VehicleModel dialog
	 */
	
	public void onClick$button_VehicleModelList_VehicleModelSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our VehicleModelDialog zul-file with parameters. So we can
		 * call them with a object of the selected VehicleModel. For handed over
		 * these parameter only a Map is accepted. So we put the VehicleModel object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleModelCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMaster/VehicleModel/VehicleModelSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the vehicleModel print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_VehicleModelList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("VehicleModel", getSearchObj());
		logger.debug("Leaving");
	}

	public void setVehicleModelService(VehicleModelService vehicleModelService) {
		this.vehicleModelService = vehicleModelService;
	}

	public VehicleModelService getVehicleModelService() {
		return this.vehicleModelService;
	}

	public JdbcSearchObject<VehicleModel> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<VehicleModel> searchObj) {
		this.searchObj = searchObj;
	}
}