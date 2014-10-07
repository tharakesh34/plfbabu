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
 * FileName    		:  VehicleManufacturerListCtrl.java                                                   * 	  
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

package com.pennant.webui.amtmasters.vehiclemanufacturer;


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
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.service.amtmasters.VehicleManufacturerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.vehiclemanufacturer.model.VehicleManufacturerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleManufacturer/VehicleManufacturerList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VehicleManufacturerListCtrl extends GFCBaseListCtrl<VehicleManufacturer> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(VehicleManufacturerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleManufacturerList; // autowired
	protected Borderlayout borderLayout_VehicleManufacturerList; // autowired
	protected Paging pagingVehicleManufacturerList; // autowired
	protected Listbox listBoxVehicleManufacturer; // autowired

	// List headers
	protected Listheader listheader_ManufacturerName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_VehicleManufacturerList_NewVehicleManufacturer; // autowired
	protected Button button_VehicleManufacturerList_VehicleManufacturerSearchDialog; // autowired
	protected Button button_VehicleManufacturerList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<VehicleManufacturer> searchObj;
	
	private transient VehicleManufacturerService vehicleManufacturerService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public VehicleManufacturerListCtrl() {
		super();
	}

	public void onCreate$window_VehicleManufacturerList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("VehicleManufacturer");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("VehicleManufacturer");
			
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
		
		this.borderLayout_VehicleManufacturerList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingVehicleManufacturerList.setPageSize(getListRows());
		this.pagingVehicleManufacturerList.setDetailed(true);

		this.listheader_ManufacturerName.setSortAscending(new FieldComparator("manufacturerName", true));
		this.listheader_ManufacturerName.setSortDescending(new FieldComparator("manufacturerName", false));
		
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
		this.searchObj = new JdbcSearchObject<VehicleManufacturer>(VehicleManufacturer.class,getListRows());
		this.searchObj.addSort("ManufacturerId", false);

		this.searchObj.addTabelName("AMTVehicleManufacturer_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_VehicleManufacturerList_NewVehicleManufacturer.setVisible(true);
			} else {
				button_VehicleManufacturerList_NewVehicleManufacturer.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_VehicleManufacturerList_NewVehicleManufacturer.setVisible(false);
			this.button_VehicleManufacturerList_VehicleManufacturerSearchDialog.setVisible(false);
			this.button_VehicleManufacturerList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxVehicleManufacturer,this.pagingVehicleManufacturerList);
			// set the itemRenderer
			this.listBoxVehicleManufacturer.setItemRenderer(new VehicleManufacturerListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("VehicleManufacturerList");
		
		this.button_VehicleManufacturerList_NewVehicleManufacturer.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerList_NewVehicleManufacturer"));
		this.button_VehicleManufacturerList_VehicleManufacturerSearchDialog.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerList_VehicleManufacturerFindDialog"));
		this.button_VehicleManufacturerList_PrintList.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.vehiclemanufacturer.model.VehicleManufacturerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onVehicleManufacturerItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected VehicleManufacturer object
		final Listitem item = this.listBoxVehicleManufacturer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final VehicleManufacturer aVehicleManufacturer = (VehicleManufacturer) item.getAttribute("data");
			final VehicleManufacturer vehicleManufacturer = getVehicleManufacturerService().getVehicleManufacturerById(aVehicleManufacturer.getId());
			
			if(vehicleManufacturer==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aVehicleManufacturer.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_ManufacturerId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ManufacturerId="+ vehicleManufacturer.getManufacturerId()+" AND version=" + vehicleManufacturer.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "VehicleManufacturer", whereCond, vehicleManufacturer.getTaskId(), vehicleManufacturer.getNextTaskId());
					if (userAcces){
						showDetailView(vehicleManufacturer);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(vehicleManufacturer);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the VehicleManufacturer dialog with a new empty entry. <br>
	 */
	public void onClick$button_VehicleManufacturerList_NewVehicleManufacturer(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new VehicleManufacturer object, We GET it from the backend.
		final VehicleManufacturer aVehicleManufacturer = getVehicleManufacturerService().getNewVehicleManufacturer();
		showDetailView(aVehicleManufacturer);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param VehicleManufacturer (aVehicleManufacturer)
	 * @throws Exception
	 */
	private void showDetailView(VehicleManufacturer aVehicleManufacturer) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aVehicleManufacturer.getWorkflowId()==0 && isWorkFlowEnabled()){
			aVehicleManufacturer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleManufacturer", aVehicleManufacturer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the VehicleManufacturerListbox from the
		 * dialog when we do a delete, edit or insert a VehicleManufacturer.
		 */
		map.put("vehicleManufacturerListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleManufacturer/VehicleManufacturerDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_VehicleManufacturerList);
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
		this.pagingVehicleManufacturerList.setActivePage(0);
		Events.postEvent("onCreate", this.window_VehicleManufacturerList, event);
		this.window_VehicleManufacturerList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the VehicleManufacturer dialog
	 */
	
	public void onClick$button_VehicleManufacturerList_VehicleManufacturerSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our VehicleManufacturerDialog zul-file with parameters. So we can
		 * call them with a object of the selected VehicleManufacturer. For handed over
		 * these parameter only a Map is accepted. So we put the VehicleManufacturer object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleManufacturerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleManufacturer/VehicleManufacturerSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the vehicleManufacturer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_VehicleManufacturerList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("VehicleManufacturer", getSearchObj(),this.pagingVehicleManufacturerList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setVehicleManufacturerService(VehicleManufacturerService vehicleManufacturerService) {
		this.vehicleManufacturerService = vehicleManufacturerService;
	}

	public VehicleManufacturerService getVehicleManufacturerService() {
		return this.vehicleManufacturerService;
	}

	public JdbcSearchObject<VehicleManufacturer> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<VehicleManufacturer> searchObj) {
		this.searchObj = searchObj;
	}
}