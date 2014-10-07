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
 * FileName    		:  VehicleVersionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.vehicleversion;


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
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.vehicleversion.model.VehicleVersionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/VehicleVersion/VehicleVersionList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VehicleVersionListCtrl extends GFCBaseListCtrl<VehicleVersion> implements Serializable {

	private static final long serialVersionUID = 8007121399442577547L;

	private final static Logger logger = Logger.getLogger(VehicleVersionListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleVersionList; 			// autowired
	protected Borderlayout borderLayout_VehicleVersionList; // autowired
	protected Paging pagingVehicleVersionList; 				// autowired
	protected Listbox listBoxVehicleVersion; 				// autowired

	// List headers
	protected Listheader listheader_VehicleModelId; 		// autowired
	protected Listheader listheader_VehicleVersionCode; 	// autowired
	protected Listheader listheader_RecordStatus; 			// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autowired
	protected Button button_VehicleVersionList_NewVehicleVersion; 			// autowired
	protected Button button_VehicleVersionList_VehicleVersionSearchDialog; 	// autowired
	protected Button button_VehicleVersionList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<VehicleVersion> searchObj;

	private transient VehicleVersionService vehicleVersionService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public VehicleVersionListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected VehicleVersion object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleVersionList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("VehicleVersion");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("VehicleVersion");

			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_VehicleVersionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingVehicleVersionList.setPageSize(getListRows());
		this.pagingVehicleVersionList.setDetailed(true);

		this.listheader_VehicleModelId.setSortAscending(new FieldComparator(
				"vehicleModelId", true));
		this.listheader_VehicleModelId.setSortDescending(new FieldComparator(
				"vehicleModelId", false));
		this.listheader_VehicleVersionCode.setSortAscending(new FieldComparator(
				"vehicleVersionCode", true));
		this.listheader_VehicleVersionCode.setSortDescending(new FieldComparator(
				"vehicleVersionCode", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator(
					"recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator(
					"recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator(
					"recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator(
					"recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<VehicleVersion>(
				VehicleVersion.class, getListRows());
		this.searchObj.addSort("VehicleVersionId", false);

		this.searchObj.addTabelName("AMTVehicleVersion_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_VehicleVersionList_NewVehicleVersion.setVisible(true);
			} else {
				button_VehicleVersionList_NewVehicleVersion.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_VehicleVersionList_NewVehicleVersion.setVisible(false);
			this.button_VehicleVersionList_VehicleVersionSearchDialog
			.setVisible(false);
			this.button_VehicleVersionList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxVehicleVersion, this.pagingVehicleVersionList);
			// set the itemRenderer
			this.listBoxVehicleVersion
			.setItemRenderer(new VehicleVersionListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("VehicleVersionList");

		this.button_VehicleVersionList_NewVehicleVersion.setVisible(
				getUserWorkspace().isAllowed(
				"button_VehicleVersionList_NewVehicleVersion"));
		this.button_VehicleVersionList_VehicleVersionSearchDialog.setVisible(
				getUserWorkspace().isAllowed(
				"button_VehicleVersionList_VehicleVersionFindDialog"));
		this.button_VehicleVersionList_PrintList.setVisible(
				getUserWorkspace().isAllowed(
				"button_VehicleVersionList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmasters.vehicleversion.model.
	 * VehicleVersionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onVehicleVersionItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected VehicleVersion object
		final Listitem item = this.listBoxVehicleVersion.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final VehicleVersion aVehicleVersion = (VehicleVersion) item.getAttribute("data");
			final VehicleVersion vehicleVersion = getVehicleVersionService()
					.getVehicleVersionById(aVehicleVersion.getId());

			if(vehicleVersion==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aVehicleVersion.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_VehicleVersionId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond = " AND VehicleVersionId="
							+ vehicleVersion.getVehicleVersionId()
							+ " AND version=" + vehicleVersion.getVersion()+ " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(),
							"VehicleVersion", whereCond,vehicleVersion.getTaskId(),
							vehicleVersion.getNextTaskId());
					if (userAcces){
						showDetailView(vehicleVersion);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(vehicleVersion);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the VehicleVersion dialog with a new empty entry. <br>
	 */
	public void onClick$button_VehicleVersionList_NewVehicleVersion(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new VehicleVersion object, We GET it from the backend.
		final VehicleVersion aVehicleVersion = getVehicleVersionService().getNewVehicleVersion();
		showDetailView(aVehicleVersion);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param VehicleVersion (aVehicleVersion)
	 * @throws Exception
	 */
	private void showDetailView(VehicleVersion aVehicleVersion) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aVehicleVersion.getWorkflowId()==0 && isWorkFlowEnabled()){
			aVehicleVersion.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleVersion", aVehicleVersion);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the VehicleVersionListbox from the
		 * dialog when we do a delete, edit or insert a VehicleVersion.
		 */
		map.put("vehicleVersionListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
						"/WEB-INF/pages/AMTMasters/VehicleVersion/VehicleVersionDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_VehicleVersionList);
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
		this.pagingVehicleVersionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_VehicleVersionList, event);
		this.window_VehicleVersionList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the VehicleVersion dialog
	 */
	public void onClick$button_VehicleVersionList_VehicleVersionSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our VehicleVersionDialog zul-file with parameters. So we can
		 * call them with a object of the selected VehicleVersion. For handed over
		 * these parameter only a Map is accepted. So we put the VehicleVersion object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleVersionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
						"/WEB-INF/pages/AMTMasters/VehicleVersion/VehicleVersionSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the vehicleVersion print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_VehicleVersionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("VehicleVersion", getSearchObj(),this.pagingVehicleVersionList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setVehicleVersionService(VehicleVersionService vehicleVersionService) {
		this.vehicleVersionService = vehicleVersionService;
	}
	public VehicleVersionService getVehicleVersionService() {
		return this.vehicleVersionService;
	}

	public JdbcSearchObject<VehicleVersion> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<VehicleVersion> searchObj) {
		this.searchObj = searchObj;
	}
	
}