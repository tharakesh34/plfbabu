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
 * FileName    		:  VehicleModelSearchCtrl.java                                                   * 	  
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
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class VehicleModelSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(VehicleModelSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleModelSearch; // autowired
	
	protected Textbox vehicleModelId; // autowired
	protected Listbox sortOperator_vehicleModelId; // autowired
	protected Textbox vehicleModelDesc; // autowired
	protected Listbox sortOperator_vehicleModelDesc; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_VehicleModelSearch_RecordStatus; // autowired
	protected Label label_VehicleModelSearch_RecordType; // autowired
	protected Label label_VehicleModelSearchResult; // autowired

	// not auto wired vars
	private transient VehicleModelListCtrl vehicleModelCtrl; // overhanded per param
	private transient VehicleModelService vehicleModelService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("VehicleModel");
	
	/**
	 * constructor
	 */
	public VehicleModelSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_VehicleModelSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("vehicleModelCtrl")) {
			this.vehicleModelCtrl = (VehicleModelListCtrl) args.get("vehicleModelCtrl");
		} else {
			this.vehicleModelCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_vehicleModelId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_vehicleModelId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_vehicleModelDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_vehicleModelDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_VehicleModelSearch_RecordStatus.setVisible(false);
			this.label_VehicleModelSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<VehicleModel> searchObj = (JdbcSearchObject<VehicleModel>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("vehicleModelId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_vehicleModelId, filter);
					this.vehicleModelId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("vehicleModelDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_vehicleModelDesc, filter);
					this.vehicleModelDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
	
				}
			}
			
		}
		showVehicleModelSeekDialog();
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
		logger.debug(event.toString());
		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_VehicleModelSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showVehicleModelSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_VehicleModelSearch.doModal();
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

		final JdbcSearchObject<VehicleModel> so = new JdbcSearchObject<VehicleModel>(VehicleModel.class);
		so.addTabelName("AMTVehicleModel_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.vehicleModelId.getValue())) {

			// get the search operator
			final Listitem item_VehicleModelId = this.sortOperator_vehicleModelId.getSelectedItem();

			if (item_VehicleModelId != null) {
				final int searchOpId = ((SearchOperators) item_VehicleModelId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("vehicleModelId", "%" + this.vehicleModelId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("vehicleModelId", this.vehicleModelId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.vehicleModelDesc.getValue())) {

			// get the search operator
			final Listitem item_VehicleModelDesc = this.sortOperator_vehicleModelDesc.getSelectedItem();

			if (item_VehicleModelDesc != null) {
				final int searchOpId = ((SearchOperators) item_VehicleModelDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("vehicleModelDesc", "%" + this.vehicleModelDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("vehicleModelDesc", this.vehicleModelDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}
		
		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Defualt Sort on the table
		so.addSort("VehicleModelId", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.vehicleModelCtrl.setSearchObj(so);

		final Listbox listBox = this.vehicleModelCtrl.listBoxVehicleModel;
		final Paging paging = this.vehicleModelCtrl.pagingVehicleModelList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<VehicleModel>) listBox.getModel()).init(so, listBox, paging);
		this.vehicleModelCtrl.setSearchObj(so);

		this.label_VehicleModelSearchResult.setValue(Labels.getLabel("label_VehicleModelSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setVehicleModelService(VehicleModelService vehicleModelService) {
		this.vehicleModelService = vehicleModelService;
	}

	public VehicleModelService getVehicleModelService() {
		return this.vehicleModelService;
	}
}