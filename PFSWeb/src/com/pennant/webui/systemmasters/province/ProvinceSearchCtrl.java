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
 * FileName    		:  ProvinceSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.province;

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
import com.pennant.backend.model.systemmasters.Province;
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
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Province/ProvinceSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ProvinceSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2671508928883232822L;
	private final static Logger logger = Logger.getLogger(ProvinceSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_ProvinceSearch; 		// autoWired
	protected Textbox cPCountry; 					// autoWired
	protected Listbox sortOperator_cPCountry; 		// autoWired
	protected Textbox cPProvince; 					// autoWired
	protected Listbox sortOperator_cPProvince; 		// autoWired
	protected Textbox cPProvinceName; 				// autoWired
	protected Listbox sortOperator_cPProvinceName; 	// autoWired
	protected Textbox recordStatus; 				// autoWired
	protected Listbox recordType;					// autoWired
	protected Listbox sortOperator_recordStatus; 	// autoWired
	protected Listbox sortOperator_recordType; 		// autoWired

	protected Label label_ProvinceSearch_RecordStatus; // autoWired
	protected Label label_ProvinceSearch_RecordType;   // autoWired
	protected Label label_ProvinceSearchResult;        // autoWired

	// not auto wired variables
	private transient ProvinceListCtrl 	provinceCtrl; // over handed per parameters
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("Province");

	/**
	 * constructor
	 */
	public ProvinceSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvinceSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("provinceCtrl")) {
			this.provinceCtrl = (ProvinceListCtrl) args.get("provinceCtrl");
		} else {
			this.provinceCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_cPCountry.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_cPCountry
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_cPProvince.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_cPProvince
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_cPProvinceName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_cPProvinceName
				.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType
					.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_ProvinceSearch_RecordStatus.setVisible(false);
			this.label_ProvinceSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<Province> searchObj = (JdbcSearchObject<Province>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("cPCountry")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_cPCountry, filter);
					this.cPCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("cPProvince")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_cPProvince, filter);
					this.cPProvince.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("cPProvinceName")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_cPProvinceName, filter);
					this.cPProvinceName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue()
								.equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}

				}
			}

		}
		showProvinceSeekDialog();
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
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_ProvinceSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showProvinceSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_ProvinceSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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

		final JdbcSearchObject<Province> so = new JdbcSearchObject<Province>(
				Province.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTCountryVsProvince_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTCountryVsProvince_AView");
		}

		if (StringUtils.isNotEmpty(this.cPCountry.getValue())) {

			// get the search operator
			final Listitem item_CPCountry = this.sortOperator_cPCountry
					.getSelectedItem();

			if (item_CPCountry != null) {
				final int searchOpId = ((SearchOperators) item_CPCountry
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("cPCountry", "%"
						+ this.cPCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("cPCountry", this.cPCountry
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.cPProvince.getValue())) {

			// get the search operator
			final Listitem item_CPProvince = this.sortOperator_cPProvince
					.getSelectedItem();

			if (item_CPProvince != null) {
				final int searchOpId = ((SearchOperators) item_CPProvince
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("cPProvince", "%"
						+ this.cPProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("cPProvince", this.cPProvince
							.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.cPProvinceName.getValue())) {

			// get the search operator
			final Listitem item_CPProvinceName = this.sortOperator_cPProvinceName
					.getSelectedItem();

			if (item_CPProvinceName != null) {
				final int searchOpId = ((SearchOperators) item_CPProvinceName
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("cPProvinceName", "%"
							+ this.cPProvinceName.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("cPProvinceName",
							this.cPProvinceName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus
					.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
					+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
			final Listitem item_RecordType = this.sortOperator_recordType
					.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType
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
		so.addSort("CPCountry", false);

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
		this.provinceCtrl.setSearchObj(so);

		final Listbox listBox = this.provinceCtrl.listBoxProvince;
		final Paging paging = this.provinceCtrl.pagingProvinceList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<Province>) listBox.getModel()).init(so, listBox,paging);
		this.provinceCtrl.setSearchObj(so);

		this.label_ProvinceSearchResult.setValue(Labels
				.getLabel("label_ProvinceSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}