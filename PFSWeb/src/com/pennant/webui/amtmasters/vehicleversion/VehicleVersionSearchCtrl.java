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
 * FileName    		:  VehicleVersionSearchCtrl.java                                                   * 	  
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class VehicleVersionSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5556718499408538286L;

	private final static Logger logger = Logger.getLogger(VehicleVersionSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleVersionSearch; 		// autowired
	
	protected Textbox vehicleVersionId; 				// autowired
	protected Listbox sortOperator_vehicleVersionId; 	// autowired
	protected Textbox vehicleModelId; 					// autowired
	protected Listbox sortOperator_vehicleModelId; 		// autowired
	protected Textbox vehicleVersionCode; 				// autowired
	protected Listbox sortOperator_vehicleVersionCode; 	// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_VehicleVersionSearch_RecordStatus; // autowired
	protected Label label_VehicleVersionSearch_RecordType; 	 // autowired
	protected Label label_VehicleVersionSearchResult; 		 // autowired

	// not auto wired vars
	private transient VehicleVersionListCtrl vehicleVersionCtrl; // overhanded per param
	private transient VehicleVersionService vehicleVersionService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("VehicleVersion");
	
	/**
	 * constructor
	 */
	public VehicleVersionSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_VehicleVersionSearch(Event event) throws Exception {
		
		logger.debug("Entering" + event.toString());
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("vehicleVersionCtrl")) {
			this.vehicleVersionCtrl = (VehicleVersionListCtrl) args.get("vehicleVersionCtrl");
		} else {
			this.vehicleVersionCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_vehicleVersionId.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_vehicleVersionId.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_vehicleModelId.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_vehicleModelId.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_vehicleVersionCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_vehicleVersionCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_VehicleVersionSearch_RecordStatus.setVisible(false);
			this.label_VehicleVersionSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<VehicleVersion> searchObj = (JdbcSearchObject<VehicleVersion>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("vehicleVersionId")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_vehicleVersionId, filter);
					this.vehicleVersionId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("vehicleModelId")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_vehicleModelId, filter);
					this.vehicleModelId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("vehicleVersionCode")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_vehicleVersionCode, filter);
					this.vehicleVersionCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(
								filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
			
		}
		showVehicleVersionSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_VehicleVersionSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showVehicleVersionSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_VehicleVersionSearch.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering ");
		final JdbcSearchObject<VehicleVersion> so = new JdbcSearchObject<VehicleVersion>(
				VehicleVersion.class);
		so.addTabelName("AMTVehicleVersion_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.vehicleVersionId.getValue())) {

			// get the search operator
			final Listitem itemVehicleVersionId = this.sortOperator_vehicleVersionId
					.getSelectedItem();

			if (itemVehicleVersionId != null) {
				final int searchOpId = ((SearchOperators) itemVehicleVersionId
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("vehicleVersionId", "%"
							+ this.vehicleVersionId.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("vehicleVersionId",
							this.vehicleVersionId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.vehicleModelId.getValue())) {

			// get the search operator
			final Listitem itemVehicleModelId = this.sortOperator_vehicleModelId
					.getSelectedItem();

			if (itemVehicleModelId != null) {
				final int searchOpId = ((SearchOperators) itemVehicleModelId
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("vehicleModelId", "%"
							+ this.vehicleModelId.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("vehicleModelId",
							this.vehicleModelId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.vehicleVersionCode.getValue())) {

			// get the search operator
			final Listitem itemVehicleVersionCode = this.sortOperator_vehicleVersionCode.getSelectedItem();

			if (itemVehicleVersionCode != null) {
				final int searchOpId = ((SearchOperators) itemVehicleVersionCode
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("vehicleVersionCode", "%"
							+ this.vehicleVersionCode.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("vehicleVersionCode",
							this.vehicleVersionCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus
							.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType != null) {
				final int searchOpId = ((SearchOperators) itemRecordType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"
							+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("VehicleVersionId", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.vehicleVersionCtrl.setSearchObj(so);

		final Listbox listBox = this.vehicleVersionCtrl.listBoxVehicleVersion;
		final Paging paging = this.vehicleVersionCtrl.pagingVehicleVersionList;

		// set the model to the listbox with the initial resultset get by the
		// DAO method.
		((PagedListWrapper<VehicleVersion>) listBox.getModel()).init(so,
				listBox, paging);
		this.vehicleVersionCtrl.setSearchObj(so);

		this.label_VehicleVersionSearchResult.setValue(Labels
				.getLabel("label_VehicleVersionSearchResult.value")
				+ " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setVehicleVersionService(VehicleVersionService vehicleVersionService) {
		this.vehicleVersionService = vehicleVersionService;
	}
	public VehicleVersionService getVehicleVersionService() {
		return this.vehicleVersionService;
	}
	
}