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
 * FileName    		:  EmpStsCodeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.empstscode;

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
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.service.systemmasters.EmpStsCodeService;
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
 * /WEB-INF/pages/SystemMaster/EmpStsCode/EmpStsCodeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EmpStsCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3863461998157704045L;
	private final static Logger logger = Logger.getLogger(EmpStsCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_EmpStsCodeSearch; 		// autoWired

	protected Textbox 	empStsCode; 					// autoWired
	protected Listbox 	sortOperator_empStsCode; 		// autoWired
	protected Textbox 	empStsDesc; 					// autoWired
	protected Listbox 	sortOperator_empStsDesc; 		// autoWired
	protected Checkbox 	empStsIsActive; 				// autoWired
	protected Listbox 	sortOperator_empStsIsActive; 	// autoWired
	protected Textbox 	recordStatus; 					// autoWired
	protected Listbox 	recordType; 					// autoWired
	protected Listbox 	sortOperator_recordStatus; 		// autoWired
	protected Listbox 	sortOperator_recordType; 		// autoWired

	protected Label label_EmpStsCodeSearch_RecordStatus;// autoWired
	protected Label label_EmpStsCodeSearch_RecordType; 	// autoWired
	protected Label label_EmpStsCodeSearchResult; 		// autoWired

	// not autoWired variables
	private transient EmpStsCodeListCtrl empStsCodeCtrl; // over handed per parameter
	private transient EmpStsCodeService empStsCodeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EmpStsCode");

	/**
	 * constructor
	 */
	public EmpStsCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected EmpStsCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmpStsCodeSearch(Event event) throws Exception {
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

		if (args.containsKey("empStsCodeCtrl")) {
			this.empStsCodeCtrl = (EmpStsCodeListCtrl) args.get("empStsCodeCtrl");
		} else {
			this.empStsCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_empStsCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_empStsCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_empStsDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_empStsDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_empStsIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_empStsIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_EmpStsCodeSearch_RecordStatus.setVisible(false);
			this.label_EmpStsCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<EmpStsCode> searchObj = (JdbcSearchObject<EmpStsCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("empStsCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_empStsCode, filter);
					this.empStsCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empStsDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_empStsDesc, filter);
					this.empStsDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empStsIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_empStsIsActive, filter);
					//this.empStsIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.empStsIsActive.setChecked(true);
					}else{
						this.empStsIsActive.setChecked(false);
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
		showEmpStsCodeSeekDialog();
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
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_EmpStsCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showEmpStsCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_EmpStsCodeSearch.doModal();
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

		final JdbcSearchObject<EmpStsCode> so = new JdbcSearchObject<EmpStsCode>(EmpStsCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTEmpStsCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTEmpStsCodes_AView");
		}
		if (StringUtils.isNotEmpty(this.empStsCode.getValue())) {

			// get the search operator
			final Listitem item_EmpStsCode = this.sortOperator_empStsCode.getSelectedItem();

			if (item_EmpStsCode != null) {
				final int searchOpId = ((SearchOperators) item_EmpStsCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("empStsCode", "%" + this.empStsCode.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("empStsCode", this.empStsCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.empStsDesc.getValue())) {

			// get the search operator
			final Listitem item_EmpStsDesc = this.sortOperator_empStsDesc.getSelectedItem();

			if (item_EmpStsDesc != null) {
				final int searchOpId = ((SearchOperators) item_EmpStsDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("empStsDesc", "%" + this.empStsDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("empStsDesc", this.empStsDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_EmpStsIsActive = this.sortOperator_empStsIsActive
		.getSelectedItem();

		if (item_EmpStsIsActive != null) {
			final int searchOpId = ((SearchOperators) item_EmpStsIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {
				if (this.empStsIsActive.isChecked()) {
					so.addFilter(new Filter("empStsIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("empStsIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("EmpStsCode", false);

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
		this.empStsCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.empStsCodeCtrl.listBoxEmpStsCode;
		final Paging paging = this.empStsCodeCtrl.pagingEmpStsCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<EmpStsCode>) listBox.getModel()).init(so, listBox, paging);
		this.empStsCodeCtrl.setSearchObj(so);

		this.label_EmpStsCodeSearchResult.setValue(Labels.getLabel(
		"label_EmpStsCodeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEmpStsCodeService(EmpStsCodeService empStsCodeService) {
		this.empStsCodeService = empStsCodeService;
	}
	public EmpStsCodeService getEmpStsCodeService() {
		return this.empStsCodeService;
	}
}