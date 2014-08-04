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
 * FileName    		:  LovFieldDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.lovfielddetail;

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
import com.pennant.backend.model.systemmasters.LovFieldDetail;
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
 * /PFSWeb/WebContent/WEB-INF/pages/SystemMaster/LovFieldDetail/LovFieldDetailSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class LovFieldDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4084189417990512880L;
	private final static Logger logger = Logger.getLogger(LovFieldDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  	window_LovFieldDetailSearch; 	// autoWired
	
	protected Textbox 	fieldCodeId; 					// autoWired
	protected Listbox 	sortOperator_fieldCodeId; 		// autoWired
	protected Textbox 	fieldCodeValue; 				// autoWired
	protected Listbox 	sortOperator_fieldCodeValue; 	// autoWired
	protected Checkbox 	isActive; 						// autoWired
	protected Listbox 	sortOperator_isActive; 			// autoWired
	protected Textbox 	recordStatus; 					// autoWired
	protected Listbox 	recordType;						// autoWired
	protected Listbox 	sortOperator_recordStatus; 		// autoWired
	protected Listbox 	sortOperator_recordType; 		// autoWired
	
	protected Label label_LovFieldDetailSearch_RecordStatus; 	// autoWired
	protected Label label_LovFieldDetailSearch_RecordType; 		// autoWired
	protected Label label_LovFieldDetailSearchResult; 			// autoWired

	// not auto wired variables
	private transient LovFieldDetailListCtrl lovFieldDetailCtrl; // over handed per parameters
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("LovFieldDetail");
	
	/**
	 * constructor
	 */
	public LovFieldDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("lovFieldDetailCtrl")) {
			this.lovFieldDetailCtrl = (LovFieldDetailListCtrl) args.get("lovFieldDetailCtrl");
		} else {
			this.lovFieldDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_fieldCodeId.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_fieldCodeValue.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_fieldCodeValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_isActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_LovFieldDetailSearch_RecordStatus.setVisible(false);
			this.label_LovFieldDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<LovFieldDetail> searchObj = (JdbcSearchObject<LovFieldDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("fieldCodeId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldCodeId, filter);
					this.fieldCodeId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("fieldCodeValue")) {
					SearchOperators.restoreStringOperator(this.sortOperator_fieldCodeValue, filter);
					this.fieldCodeValue.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("isActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_isActive, filter);
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
						if (this.recordType.getItemAtIndex(i).getValue().equals(
								filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
	
				}
			}
		}
		showLovFieldDetailSeekDialog();
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
		this.window_LovFieldDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showLovFieldDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_LovFieldDetailSearch.doModal();
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
		
		final JdbcSearchObject<LovFieldDetail> so = new JdbcSearchObject<LovFieldDetail>(
				LovFieldDetail.class);
		
		if (isWorkFlowEnabled()){
			so.addTabelName("RMTLovFieldDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("RMTLovFieldDetail_AView");
		}
		
		if (StringUtils.isNotEmpty(this.fieldCodeId.getValue())) {

			// get the search operator
			final Listitem item_FieldCodeId = this.sortOperator_fieldCodeId.getSelectedItem();

			if (item_FieldCodeId != null) {
				final int searchOpId = ((SearchOperators) item_FieldCodeId.getAttribute("data")).
								getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldCode", "%" + 
							this.fieldCodeId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldCode", this.fieldCodeId.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.fieldCodeValue.getValue())) {

			// get the search operator
			final Listitem item_FieldCodeValue = this.sortOperator_fieldCodeValue.getSelectedItem();

			if (item_FieldCodeValue != null) {
				final int searchOpId = ((SearchOperators) item_FieldCodeValue.getAttribute("data")).
										getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("fieldCodeValue", "%" + 
								this.fieldCodeValue.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("fieldCodeValue", this.fieldCodeValue.getValue(), 
								searchOpId));
				}
			}
		}
		
		// get the search operator
		final Listitem item_isActive = this.sortOperator_isActive.getSelectedItem();

		if (item_isActive != null) {
			final int searchOpId = ((SearchOperators) item_isActive.getAttribute("data")).
							getSearchOperatorId();
			
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
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).
								getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + 
								this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
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
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).
								getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", 
								searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		
		// Default Sort on the table
		so.addSort("FieldCodeId", false);

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
		this.lovFieldDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.lovFieldDetailCtrl.listBoxLovFieldDetail;
		final Paging paging = this.lovFieldDetailCtrl.pagingLovFieldDetailList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<LovFieldDetail>) listBox.getModel()).init(so, listBox, paging);
		this.lovFieldDetailCtrl.setSearchObj(so);

		this.label_LovFieldDetailSearchResult.setValue(
					Labels.getLabel("label_LovFieldDetailSearchResult.value") + " "
						+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}