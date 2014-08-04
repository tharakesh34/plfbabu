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
 * FileName    		:  GroupStatusCodeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.groupstatuscode;

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
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.service.systemmasters.GroupStatusCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
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
 * /WEB-INF/pages/SystemMaster/GroupStatusCode/GroupStatusCodeSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class GroupStatusCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3904995590581543438L;
	private final static Logger logger = Logger.getLogger(GroupStatusCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_GroupStatusCodeSearch; 		// autoWired

	protected Textbox 	grpStsCode; 						// autoWired
	protected Listbox 	sortOperator_grpStsCode; 			// autoWired
	protected Textbox 	grpStsDescription; 					// autoWired
	protected Listbox 	sortOperator_grpStsDescription; 	// autoWired
	protected Checkbox 	grpStsIsActive; 					// autoWired
	protected Listbox 	sortOperator_grpStsIsActive; 		// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_GroupStatusCodeSearch_RecordStatus; 	// autoWired
	protected Label label_GroupStatusCodeSearch_RecordType; 	// autoWired
	protected Label label_GroupStatusCodeSearchResult; 			// autoWired

	// not autoWired variables
	private transient GroupStatusCodeListCtrl groupStatusCodeCtrl; // over handed per parameter
	private transient GroupStatusCodeService groupStatusCodeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GroupStatusCode");

	/**
	 * constructor
	 */
	public GroupStatusCodeSearchCtrl() {
		super();
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected GroupStatusCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GroupStatusCodeSearch(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("groupStatusCodeCtrl")) {
			this.groupStatusCodeCtrl = (GroupStatusCodeListCtrl) args.get("groupStatusCodeCtrl");
		} else {
			this.groupStatusCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_grpStsCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpStsCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_grpStsDescription.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_grpStsDescription.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_grpStsIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_grpStsIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_GroupStatusCodeSearch_RecordStatus.setVisible(false);
			this.label_GroupStatusCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<GroupStatusCode> searchObj = (JdbcSearchObject<GroupStatusCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("grpStsCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpStsCode, filter);
					this.grpStsCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("grpStsDescription")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpStsDescription, filter);
					this.grpStsDescription.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("grpStsIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_grpStsIsActive, filter);
					//this.grpStsIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.grpStsIsActive.setChecked(true);
					}else{
						this.grpStsIsActive.setChecked(false);
					}
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showGroupStatusCodeSeekDialog();
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
		this.window_GroupStatusCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showGroupStatusCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_GroupStatusCodeSearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<GroupStatusCode> so = new JdbcSearchObject<GroupStatusCode>(GroupStatusCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTGrpStatusCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else {
			so.addTabelName("BMTGrpStatusCodes_AView");
		}
		so.addFilter(new Filter("GrpStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		if (StringUtils.isNotEmpty(this.grpStsCode.getValue())) {

			// get the search operator
			final Listitem item_GrpStsCode = this.sortOperator_grpStsCode.getSelectedItem();

			if (item_GrpStsCode != null) {
				final int searchOpId = ((SearchOperators) item_GrpStsCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("grpStsCode", "%"
							+ this.grpStsCode.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("grpStsCode", this.grpStsCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.grpStsDescription.getValue())) {

			// get the search operator
			final Listitem item_GrpStsDescription = this.sortOperator_grpStsDescription.getSelectedItem();

			if (item_GrpStsDescription != null) {
				final int searchOpId = ((SearchOperators) item_GrpStsDescription.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("grpStsDescription", "%"
							+ this.grpStsDescription.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("grpStsDescription",this.grpStsDescription.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_GrpStsIsActive = this.sortOperator_grpStsIsActive.getSelectedItem();

		if (item_GrpStsIsActive != null) {
			final int searchOpId = ((SearchOperators) item_GrpStsIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.grpStsIsActive.isChecked()) {
					so.addFilter(new Filter("grpStsIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("grpStsIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("GrpStsCode", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.groupStatusCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.groupStatusCodeCtrl.listBoxGroupStatusCode;
		final Paging paging = this.groupStatusCodeCtrl.pagingGroupStatusCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<GroupStatusCode>) listBox.getModel()).init(so,listBox, paging);
		this.groupStatusCodeCtrl.setSearchObj(so);

		this.label_GroupStatusCodeSearchResult.setValue(Labels.getLabel(
		"label_GroupStatusCodeSearchResult.value")+ " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGroupStatusCodeService(
			GroupStatusCodeService groupStatusCodeService) {
		this.groupStatusCodeService = groupStatusCodeService;
	}
	public GroupStatusCodeService getGroupStatusCodeService() {
		return this.groupStatusCodeService;
	}
}