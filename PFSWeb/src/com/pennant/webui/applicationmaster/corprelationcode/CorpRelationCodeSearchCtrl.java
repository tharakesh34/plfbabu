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
 * FileName    		:  CorpRelationCodeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.corprelationcode;

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
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.service.applicationmaster.CorpRelationCodeService;
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
 * /WEB-INF/pages/ApplicationMaster/CorpRelationCode/CorpRelationCodeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class CorpRelationCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6766173756969269842L;
	private final static Logger logger = Logger.getLogger(CorpRelationCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CorpRelationCodeSearch; 		// autoWired

	protected Textbox 	corpRelationCode; 					// autoWired
	protected Listbox 	sortOperator_corpRelationCode; 		// autoWired
	protected Textbox 	corpRelationDesc; 					// autoWired
	protected Listbox 	sortOperator_corpRelationDesc; 		// autoWired
	protected Checkbox 	corpRelationIsActive; 				// autoWired
	protected Listbox 	sortOperator_corpRelationIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_CorpRelationCodeSearch_RecordStatus; 	// autoWired
	protected Label label_CorpRelationCodeSearch_RecordType; 	// autoWired
	protected Label label_CorpRelationCodeSearchResult; 		// autoWired

	// not autoWired variables
	private transient CorpRelationCodeListCtrl corpRelationCodeCtrl; // overHanded per parameter
	private transient CorpRelationCodeService corpRelationCodeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CorpRelationCode");

	/**
	 * constructor
	 */
	public CorpRelationCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CorpRelationCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorpRelationCodeSearch(Event event)	throws Exception {
		logger.debug("Entering"+event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("corpRelationCodeCtrl")) {
			this.corpRelationCodeCtrl = (CorpRelationCodeListCtrl) args.get("corpRelationCodeCtrl");
		} else {
			this.corpRelationCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_corpRelationCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_corpRelationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_corpRelationDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_corpRelationDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_corpRelationIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_corpRelationIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CorpRelationCodeSearch_RecordStatus.setVisible(false);
			this.label_CorpRelationCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CorpRelationCode> searchObj = (JdbcSearchObject<CorpRelationCode>)args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("corpRelationCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_corpRelationCode, filter);
					this.corpRelationCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("corpRelationDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_corpRelationDesc, filter);
					this.corpRelationDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("corpRelationIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_corpRelationIsActive, filter);
					//this.corpRelationIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.corpRelationIsActive.setChecked(true);
					}else{
						this.corpRelationIsActive.setChecked(false);
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
		showCorpRelationCodeSeekDialog();
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
		logger.debug("Entering");
		this.window_CorpRelationCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCorpRelationCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_CorpRelationCodeSearch.doModal();
		} catch (final Exception e) {
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

		final JdbcSearchObject<CorpRelationCode> so = new JdbcSearchObject<CorpRelationCode>(CorpRelationCode.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTCorpRelationCodes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTCorpRelationCodes_AView");
		}

		if (StringUtils.isNotEmpty(this.corpRelationCode.getValue())) {

			// get the search operator
			final Listitem item_CorpRelationCode = this.sortOperator_corpRelationCode.getSelectedItem();

			if (item_CorpRelationCode != null) {
				final int searchOpId = ((SearchOperators) item_CorpRelationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("corpRelationCode", "%" + this.corpRelationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("corpRelationCode", this.corpRelationCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.corpRelationDesc.getValue())) {

			// get the search operator
			final Listitem item_CorpRelationDesc = this.sortOperator_corpRelationDesc.getSelectedItem();

			if (item_CorpRelationDesc != null) {
				final int searchOpId = ((SearchOperators) item_CorpRelationDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("corpRelationDesc", "%"	+ this.corpRelationDesc.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("corpRelationDesc",	this.corpRelationDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_CorpRelationIsActive = this.sortOperator_corpRelationIsActive.getSelectedItem();

		if (item_CorpRelationIsActive != null) {
			final int searchOpId = ((SearchOperators) item_CorpRelationIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.corpRelationIsActive.isChecked()) {
					so.addFilter(new Filter("corpRelationIsActive", 1,searchOpId));
				} else {
					so.addFilter(new Filter("corpRelationIsActive", 0,searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"	+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
		so.addSort("CorpRelationCode", false);

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
		this.corpRelationCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.corpRelationCodeCtrl.listBoxCorpRelationCode;
		final Paging paging = this.corpRelationCodeCtrl.pagingCorpRelationCodeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<CorpRelationCode>) listBox.getModel()).init(so, listBox, paging);
		this.corpRelationCodeCtrl.setSearchObj(so);

		this.label_CorpRelationCodeSearchResult.setValue(Labels.getLabel(
		"label_CorpRelationCodeSearchResult.value")+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCorpRelationCodeService(CorpRelationCodeService corpRelationCodeService) {
		this.corpRelationCodeService = corpRelationCodeService;
	}
	public CorpRelationCodeService getCorpRelationCodeService() {
		return this.corpRelationCodeService;
	}
}