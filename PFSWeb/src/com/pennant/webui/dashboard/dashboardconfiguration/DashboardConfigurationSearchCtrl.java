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
 * FileName    		:  DashboardConfigurationSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dashboard.dashboardconfiguration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/DashBoards/DashboardConfiguration/dashboardConfigurationSearch.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DashboardConfigurationSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3791083476253744767L;
	private final static Logger logger = Logger.getLogger(DashboardConfigurationSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DashboardConfigurationSearch; 					// autoWired

	protected Textbox  dashboardCode; 										// autoWired
	protected Listbox  sortOperator_dashboardCode; 							// autoWired
	protected Textbox  dashboardDesc; 										// autoWired
	protected Listbox  sortOperator_dashboardDesc; 							// autoWired
	protected Combobox dashboardType; 										// autoWired
	protected Listbox  sortOperator_dashboardType; 							// autoWired
	protected Textbox  caption; 										    // autoWired
	protected Listbox  sortOperator_caption; 							    // autoWired
	protected Textbox  subCaption; 										    // autoWired
	protected Listbox  sortOperator_subCaption; 							// autoWired
	protected Textbox  dimension; 										    // autoWired
	protected Listbox  sortOperator_dimension; 							    // autoWired


	protected Textbox recordStatus; 										// autoWired
	protected Listbox recordType;											// autoWired
	protected Listbox sortOperator_recordStatus; 							// autoWired
	protected Listbox sortOperator_recordType; 								// autoWired

	protected Label  label_DashboardConfigurationSearch_RecordStatus; 	     // autoWired
	protected Label  label_DashboardConfigurationSearch_RecordType; 		 // autoWired
	protected Label  label_DashboardConfigurationSearchResult; 				 // autoWired

	// not auto wired variables
	private transient DashboardConfigurationListCtrl dashboardConfigurationCtrl; // overHanded per parameter
	private transient DashboardConfigurationService dashboardConfigurationService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DashboardConfiguration");
	private static   List<ValueLabel>  listDashboardType = PennantAppUtil.getDashBoardType(); 	// autoWiredgetChartDimensions()

	/**
	 * constructor
	 */
	public DashboardConfigurationSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected AddressType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void onCreate$window_DashboardConfigurationSearch(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
		setListType(this.listDashboardType,this.dashboardType);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("dashboardConfigurationCtrl")) {
			this.dashboardConfigurationCtrl = (DashboardConfigurationListCtrl) args.get("dashboardConfigurationCtrl");
		} else {
			this.dashboardConfigurationCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_dashboardCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dashboardCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_dashboardDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dashboardDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_caption.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_caption.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_subCaption.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_subCaption.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_dashboardType.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_dashboardType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_dimension.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dimension.setItemRenderer(new SearchOperatorListModelItemRenderer());



		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_DashboardConfigurationSearch_RecordStatus.setVisible(false);
			this.label_DashboardConfigurationSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<DashboardConfiguration> searchObj = (JdbcSearchObject<DashboardConfiguration>) 
			args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("dashboardCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dashboardCode, filter);
					this.dashboardCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("dashboardDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dashboardDesc, filter);
					this.dashboardDesc.setValue(filter.getValue().toString());
				}
				else if (filter.getProperty().equals("caption")) {
					SearchOperators.restoreStringOperator(this.sortOperator_caption, filter);
					this.dashboardType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("subCaption")) {
					SearchOperators.restoreStringOperator(this.sortOperator_subCaption, filter);
					this.dashboardType.setValue(filter.getValue().toString());
				}else if (filter.getProperty().equals("dashboardType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dashboardType, filter);
					this.dashboardType.setValue(filter.getValue().toString());
				}else if (filter.getProperty().equals("dimension")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dimension, filter);
					this.dashboardType.setValue(filter.getValue().toString());
				}else if (filter.getProperty().equals("recordStatus")) {
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
		showDashboardDetailSeekDialog();
		logger.debug("Leaving"+event.toString());
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
		logger.debug("Entering"+event.toString());
		doSearch();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving"+event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		this.window_DashboardConfigurationSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDashboardDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_DashboardConfigurationSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");
		@SuppressWarnings("rawtypes")
		final JdbcSearchObject<DashboardConfiguration> so = new JdbcSearchObject(DashboardConfiguration.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("DashboardConfiguration_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		else{
			so.addTabelName("DashboardConfiguration_AView");
		}

		if (StringUtils.isNotEmpty(this.dashboardCode.getValue())) {

			// get the search operator
			final Listitem item_dashboardCode = this.sortOperator_dashboardCode.getSelectedItem();

			if (item_dashboardCode != null) {
				final int searchOpId = ((SearchOperators) item_dashboardCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dashboardCode", "%" + this.dashboardCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dashboardCode", this.dashboardCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dashboardDesc.getValue())) {

			// get the search operator
			final Listitem item_DashboardDesc = this.sortOperator_dashboardDesc.getSelectedItem();

			if (item_DashboardDesc != null) {
				final int searchOpId = ((SearchOperators) item_DashboardDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dashboardDesc", "%" + this.dashboardDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dashboardDesc", this.dashboardDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.caption.getValue())) {

			// get the search operator
			final Listitem item_DashboardDesc = this.sortOperator_caption.getSelectedItem();

			if (item_DashboardDesc != null) {
				final int searchOpId = ((SearchOperators) item_DashboardDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("caption", "%" + this.caption.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("caption", this.caption.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.subCaption.getValue())) {

			// get the search operator
			final Listitem item_DashboardType = this.sortOperator_subCaption.getSelectedItem();

			if (item_DashboardType != null) {
				final int searchOpId = ((SearchOperators) item_DashboardType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("subCaption", "%" + this.subCaption.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("subCaption", this.subCaption.getValue(), searchOpId));
				}
			}
		}
		if(this.dashboardType.getSelectedItem()!=null){
			String dashBoardType=PennantAppUtil.getlabelDesc(
					String.valueOf(this.dashboardType.getSelectedItem().getValue()),PennantAppUtil.getDashBoardType());
			// get the search operator
			final Listitem item_DashboardDesc = this.sortOperator_dashboardType.getSelectedItem();

			if (item_DashboardDesc != null) {
				final int searchOpId = ((SearchOperators) item_DashboardDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dashboardType", "%" + dashBoardType.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dashboardType", dashBoardType.toUpperCase(), searchOpId));
				}
			}
		}


		if (StringUtils.isNotEmpty(this.dimension.getValue())) {

			// get the search operator
			final Listitem item_DashboardDesc = this.sortOperator_dimension.getSelectedItem();

			if (item_DashboardDesc != null) {
				final int searchOpId = ((SearchOperators) item_DashboardDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dimension", "%" + this.dimension.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dimension", this.dimension.getValue(), searchOpId));
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
		// Default Sort on the table
		so.addSort("dashboardCode", false);

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
		this.dashboardConfigurationCtrl.setSearchObj(so);

		final Listbox listBox = this.dashboardConfigurationCtrl.listBoxDashboardConfiguration;
		final Paging paging = this.dashboardConfigurationCtrl.pagingDashboardConfigurationList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<DashboardConfiguration>) listBox.getModel()).init(so, listBox, paging);
		this.dashboardConfigurationCtrl.setSearchObj(so);

		this.label_DashboardConfigurationSearchResult.setValue(Labels.getLabel(
		"label_DashboardConfigurationSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}
	/**
	 * Set chart types
	 * @param listDashboardType
	 * @param comboBox
	 */
	private void setListType(List<ValueLabel> listDashboardType,Combobox comboBox) {
		logger.debug("Entering ");
		for (int i = 0; i < listDashboardType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listDashboardType.get(i).getLabel());
			comboitem.setValue(listDashboardType.get(i).getValue());
			comboBox.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}

	public void setDashboardConfigurationService(
			DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}
}