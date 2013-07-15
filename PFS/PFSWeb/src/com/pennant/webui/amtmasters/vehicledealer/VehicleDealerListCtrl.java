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
 * FileName    		:  VehicleDealerListCtrl.java                                                   * 	  
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

package com.pennant.webui.amtmasters.vehicledealer;


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
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.vehicledealer.model.VehicleDealerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleDealer/VehicleDealerList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VehicleDealerListCtrl extends GFCBaseListCtrl<VehicleDealer> implements Serializable {

	private static final long serialVersionUID = 259921702952389829L;

	private final static Logger logger = Logger.getLogger(VehicleDealerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleDealerList; 				// autowired
	protected Borderlayout borderLayout_VehicleDealerList;  // autowired
	protected Paging pagingVehicleDealerList; 				// autowired
	protected Listbox listBoxVehicleDealer; 				// autowired

	// List headers
	protected Listheader listheader_DealerName; 	// autowired
	protected Listheader listheader_RecordStatus; 	// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_VehicleDealerList_NewVehicleDealer; 		 // autowired
	protected Button button_VehicleDealerList_VehicleDealerSearchDialog; // autowired
	protected Button button_VehicleDealerList_PrintList; 				 // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<VehicleDealer> searchObj;
	
	private transient VehicleDealerService vehicleDealerService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public VehicleDealerListCtrl() {
		super();
	}

	public void onCreate$window_VehicleDealerList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil
		.getModuleMap("VehicleDealer");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("VehicleDealer");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_VehicleDealerList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingVehicleDealerList.setPageSize(getListRows());
		this.pagingVehicleDealerList.setDetailed(true);

		this.listheader_DealerName.setSortAscending(new FieldComparator(
				"dealerName", true));
		this.listheader_DealerName.setSortDescending(new FieldComparator(
				"dealerName", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator(
					"recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator(
					"recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator(
					"recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator(
					"recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<VehicleDealer>(
				VehicleDealer.class, getListRows());
		this.searchObj.addSort("DealerId", false);

		this.searchObj.addTabelName("AMTVehicleDealer_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_VehicleDealerList_NewVehicleDealer.setVisible(true);
			} else {
				button_VehicleDealerList_NewVehicleDealer.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_VehicleDealerList_NewVehicleDealer.setVisible(false);
			this.button_VehicleDealerList_VehicleDealerSearchDialog
			.setVisible(false);
			this.button_VehicleDealerList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxVehicleDealer, this.pagingVehicleDealerList);
			// set the itemRenderer
			this.listBoxVehicleDealer
			.setItemRenderer(new VehicleDealerListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("VehicleDealerList");

		this.button_VehicleDealerList_NewVehicleDealer
		.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleDealerList_NewVehicleDealer"));
		this.button_VehicleDealerList_VehicleDealerSearchDialog
		.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleDealerList_VehicleDealerFindDialog"));
		this.button_VehicleDealerList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_VehicleDealerList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.vehicledealer.model.
	 * VehicleDealerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onVehicleDealerItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected VehicleDealer object
		final Listitem item = this.listBoxVehicleDealer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final VehicleDealer aVehicleDealer = (VehicleDealer) item
			.getAttribute("data");
			final VehicleDealer vehicleDealer = getVehicleDealerService()
			.getVehicleDealerById(aVehicleDealer.getId());

			if (vehicleDealer == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aVehicleDealer.getId());
				errParm[0] = PennantJavaUtil.getLabel("label_DealerId") + ":"
				+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace()
								.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND DealerId="
						+ vehicleDealer.getDealerId() + " AND version="
						+ vehicleDealer.getVersion() + " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(),
							"VehicleDealer", whereCond,
							vehicleDealer.getTaskId(),
							vehicleDealer.getNextTaskId());
					if (userAcces) {
						showDetailView(vehicleDealer);
					} else {
						PTMessageUtils.showErrorMessage(Labels
								.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(vehicleDealer);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the VehicleDealer dialog with a new empty entry. <br>
	 */
	public void onClick$button_VehicleDealerList_NewVehicleDealer(Event event)
	throws Exception {
		logger.debug(event.toString());
		// create a new VehicleDealer object, We GET it from the backend.
		final VehicleDealer aVehicleDealer = getVehicleDealerService()
		.getNewVehicleDealer();
		showDetailView(aVehicleDealer);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param VehicleDealer
	 *            (aVehicleDealer)
	 * @throws Exception
	 */
	private void showDetailView(VehicleDealer aVehicleDealer) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aVehicleDealer.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aVehicleDealer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleDealer", aVehicleDealer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the VehicleDealerListbox from the
		 * dialog when we do a delete, edit or insert a VehicleDealer.
		 */
		map.put("vehicleDealerListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/AMTMasters/VehicleDealer/VehicleDealerDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_VehicleDealerList);
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
		this.pagingVehicleDealerList.setActivePage(0);
		Events.postEvent("onCreate", this.window_VehicleDealerList, event);
		this.window_VehicleDealerList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the VehicleDealer dialog
	 */

	public void onClick$button_VehicleDealerList_VehicleDealerSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our VehicleDealerDialog zul-file with parameters. So we
		 * can call them with a object of the selected VehicleDealer. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * VehicleDealer object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vehicleDealerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/AMTMasters/VehicleDealer/VehicleDealerSearchDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the vehicleDealer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_VehicleDealerList_PrintList(Event event)
	throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("VehicleDealer", getSearchObj());
		logger.debug("Leaving");
	}

	public void setVehicleDealerService(
			VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public VehicleDealerService getVehicleDealerService() {
		return this.vehicleDealerService;
	}

	public JdbcSearchObject<VehicleDealer> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<VehicleDealer> searchObj) {
		this.searchObj = searchObj;
	}
}