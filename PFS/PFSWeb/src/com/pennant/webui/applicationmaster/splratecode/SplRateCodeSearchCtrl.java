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
 * FileName    		:  SplRateCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.splratecode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.SplRateCode;
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
 * /WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeSearchDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SplRateCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3870672701579874108L;
	private final static Logger logger = Logger.getLogger(SplRateCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_SplRateCodeSearch; 	// autowired
	protected Textbox  sRType; 						// autowired
	protected Listbox  sortOperator_sRType; 		// autowired
	protected Textbox  sRTypeDesc; 					// autowired
	protected Listbox  sortOperator_sRTypeDesc; 	// autowired
	protected Checkbox sRIsActive; 					// autowired
	protected Listbox  sortOperator_sRIsActive; 	// autowired
	protected Textbox  recordStatus; 				// autowired
	protected Listbox  recordType;					// autowired
	protected Listbox  sortOperator_recordStatus; 	// autowired
	protected Listbox  sortOperator_recordType; 	// autowired

	protected Label label_SplRateCodeSearch_RecordStatus; 	// autowired
	protected Label label_SplRateCodeSearch_RecordType; 	// autowired
	protected Label label_SplRateCodeSearchResult; 			// autowired

	// not auto wired vars
	private transient SplRateCodeListCtrl splRateCodeCtrl; // overhanded per param
	private transient WorkFlowDetails     workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("SplRateCode");

	/**
	 * constructor
	 */
	public SplRateCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected SplRateCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SplRateCodeSearch(Event event) throws Exception {
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

		if (args.containsKey("splRateCodeCtrl")) {
			this.splRateCodeCtrl = (SplRateCodeListCtrl) args
					.get("splRateCodeCtrl");
		} else {
			this.splRateCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_sRType.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_sRType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_sRTypeDesc.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_sRTypeDesc.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		this.sortOperator_sRIsActive.setModel(new ListModelList(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_sRIsActive.setItemRenderer(
				new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_SplRateCodeSearch_RecordStatus.setVisible(false);
			this.label_SplRateCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<SplRateCode> searchObj = (JdbcSearchObject<SplRateCode>) 
					args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("sRType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_sRType, filter);
					this.sRType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("sRTypeDesc")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_sRTypeDesc, filter);
					this.sRTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("sRIsActive")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_sRIsActive, filter);
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.sRIsActive.setChecked(true);
					}else{
						this.sRIsActive.setChecked(false);
					}
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
		showSplRateCodeSeekDialog();
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
		this.window_SplRateCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showSplRateCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SplRateCodeSearch.doModal();
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
		logger.debug("Entering");
		final JdbcSearchObject<SplRateCode> so = new JdbcSearchObject<SplRateCode>(
				SplRateCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("RMTSplRateCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			so.addTabelName("RMTSplRateCodes_AView");
		}
		if (StringUtils.isNotEmpty(this.sRType.getValue())) {

			// get the search operator
			final Listitem item_SRType = this.sortOperator_sRType
					.getSelectedItem();

			if (item_SRType != null) {
				final int searchOpId = ((SearchOperators) item_SRType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sRType", "%"
							+ this.sRType.getValue().toUpperCase() + "%",
							searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sRType", this.sRType.getValue(),
							searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.sRTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_SRTypeDesc = this.sortOperator_sRTypeDesc
					.getSelectedItem();

			if (item_SRTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_SRTypeDesc
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("sRTypeDesc", "%"
						+ this.sRTypeDesc.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("sRTypeDesc", this.sRTypeDesc
							.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_SRIsActive = this.sortOperator_sRIsActive
				.getSelectedItem();

		if (item_SRIsActive != null) {
			final int searchOpId = ((SearchOperators) item_SRIsActive
					.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.sRIsActive.isChecked()) {
					so.addFilter(new Filter("sRIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("sRIsActive", 0, searchOpId));
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
			selectedValue = this.recordType.getSelectedItem().getValue()
					.toString();
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
		so.addSort("SRType", false);

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
		this.splRateCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.splRateCodeCtrl.listBoxSplRateCode;
		final Paging paging = this.splRateCodeCtrl.pagingSplRateCodeList;

		// set the model to the listBox with the initial resultSet get by the
		// DAO method.
		((PagedListWrapper<SplRateCode>) listBox.getModel()).init(so, listBox,paging);
		this.splRateCodeCtrl.setSearchObj(so);

		this.label_SplRateCodeSearchResult.setValue(Labels
				.getLabel("label_SplRateCodeSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}