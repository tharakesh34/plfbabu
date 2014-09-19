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
 * FileName    		:  LovFieldCodeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.lovfieldcode;

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
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class LovFieldCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3975025805822011949L;
	private final static Logger logger = Logger.getLogger(LovFieldCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_LovFieldCodeSearch; 	         // autoWired
	protected Textbox  fieldCode; 						     // autoWired
	protected Listbox  sortOperator_fieldCode; 			     // autoWired
	protected Textbox  fieldCodeDesc; 					     // autoWired
	protected Listbox  sortOperator_fieldCodeDesc; 		     // autoWired
	protected Textbox  fieldCodeType; 					     // autoWired
	protected Listbox  sortOperator_fieldCodeType; 		     // autoWired
	protected Checkbox isActive; 						     // autoWired
	protected Listbox  sortOperator_isActive; 			     // autoWired
	protected Textbox  recordStatus; 					     // autoWired
	protected Listbox  recordType;						     // autoWired
	protected Listbox  sortOperator_recordStatus; 		     // autoWired
	protected Listbox  sortOperator_recordType; 			 // autoWired
	protected Label    label_LovFieldCodeSearch_RecordStatus;// autoWired
	protected Label    label_LovFieldCodeSearch_RecordType;  // autoWired
	protected Label    label_LovFieldCodeSearchResult; 	     // autoWired

	// not auto wired variables
	private transient LovFieldCodeListCtrl lovFieldCodeCtrl; // overHanded per parameter
	private transient LovFieldCodeService lovFieldCodeService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("LovFieldCode");

	/**
	 * constructor
	 */
	public LovFieldCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LovFieldCodeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("lovFieldCodeCtrl")) {
			this.lovFieldCodeCtrl = (LovFieldCodeListCtrl) args.get("lovFieldCodeCtrl");
		} else {
			this.lovFieldCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_fieldCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_fieldCodeDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_fieldCodeType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_isActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_LovFieldCodeSearch_RecordStatus.setVisible(false);
			this.label_LovFieldCodeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<LovFieldCode> searchObj = (JdbcSearchObject<LovFieldCode>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("fieldCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldCode, filter);
					this.fieldCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("fieldCodeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldCodeDesc, filter);
					this.fieldCodeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("fieldCodeType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldCodeType, filter);
					this.fieldCodeType.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("isActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_isActive, filter);
					//this.isActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.isActive.setChecked(true);
					}else{
						this.isActive.setChecked(false);
					}
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
		showLovFieldCodeSeekDialog();
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
		this.window_LovFieldCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showLovFieldCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_LovFieldCodeSearch.doModal();
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
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<LovFieldCode> so = new JdbcSearchObject<LovFieldCode>(LovFieldCode.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTLovFieldCode_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTLovFieldCode_AView");
		}

		if (StringUtils.isNotEmpty(this.fieldCode.getValue())) {

			// get the search operator
			final Listitem itemFieldCode = this.sortOperator_fieldCode.getSelectedItem();
			if (itemFieldCode != null) {
				final int searchOpId = ((SearchOperators) itemFieldCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldCode", "%" + this.fieldCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldCode", this.fieldCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldCodeDesc.getValue())) {

			// get the search operator
			final Listitem itemFieldCodeDesc = this.sortOperator_fieldCodeDesc.getSelectedItem();
			if (itemFieldCodeDesc != null) {
				final int searchOpId = ((SearchOperators) itemFieldCodeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldCodeDesc", "%" + this.fieldCodeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldCodeDesc", this.fieldCodeDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.fieldCodeType.getValue())) {

			// get the search operator
			final Listitem itemFieldCodeType = this.sortOperator_fieldCodeType.getSelectedItem();
			if (itemFieldCodeType != null) {
				final int searchOpId = ((SearchOperators) itemFieldCodeType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldCodeType", "%" + this.fieldCodeType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldCodeType", this.fieldCodeType.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemIsActive = this.sortOperator_isActive.getSelectedItem();
		if (itemIsActive != null) {
			final int searchOpId = ((SearchOperators) itemIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if(this.isActive.isChecked()){
					so.addFilter(new Filter("isActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("isActive",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).getSearchOperatorId();

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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute("data")).getSearchOperatorId();

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
		so.addSort("FieldCode", false);

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
		this.lovFieldCodeCtrl.setSearchObj(so);
		final Listbox listBox = this.lovFieldCodeCtrl.listBoxLovFieldCode;
		final Paging paging = this.lovFieldCodeCtrl.pagingLovFieldCodeList;


		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<LovFieldCode>) listBox.getModel()).init(so, listBox, paging);
		this.lovFieldCodeCtrl.setSearchObj(so);
		this.label_LovFieldCodeSearchResult.setValue(Labels.getLabel(
		"label_LovFieldCodeSearchResult.value") + " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setLovFieldCodeService(LovFieldCodeService lovFieldCodeService) {
		this.lovFieldCodeService = lovFieldCodeService;
	}
	public LovFieldCodeService getLovFieldCodeService() {
		return this.lovFieldCodeService;
	}
}