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
 * FileName    		:  WIFFinanceDisbursementSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancedisbursement;

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
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.finance.WIFFinanceDisbursementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class WIFFinanceDisbursementSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceDisbursementSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceDisbursementSearch; // autowired
	
	protected Textbox finReference; // autowired
	protected Listbox sortOperator_finReference; // autowired
	protected Textbox disbDate; // autowired
	protected Listbox sortOperator_disbDate; // autowired
	protected Textbox disbSeq; // autowired
	protected Listbox sortOperator_disbSeq; // autowired
	protected Textbox disbDesc; // autowired
	protected Listbox sortOperator_disbDesc; // autowired
	protected Textbox disbAmount; // autowired
	protected Listbox sortOperator_disbAmount; // autowired
	protected Textbox disbActDate; // autowired
	protected Listbox sortOperator_disbActDate; // autowired
	protected Checkbox disbDisbursed; // autowired
	protected Listbox sortOperator_disbDisbursed; // autowired
	protected Checkbox disbIsActive; // autowired
	protected Listbox sortOperator_disbIsActive; // autowired
	protected Textbox disbRemarks; // autowired
	protected Listbox sortOperator_disbRemarks; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_WIFFinanceDisbursementSearch_RecordStatus; // autowired
	protected Label label_WIFFinanceDisbursementSearch_RecordType; // autowired
	protected Label label_WIFFinanceDisbursementSearchResult; // autowired

	// not auto wired vars
	private transient WIFFinanceDisbursementListCtrl wIFFinanceDisbursementCtrl; // overhanded per param
	private transient WIFFinanceDisbursementService wIFFinanceDisbursementService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("WIFFinanceDisbursement");
	
	/**
	 * constructor
	 */
	public WIFFinanceDisbursementSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_WIFFinanceDisbursementSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("wIFFinanceDisbursementCtrl")) {
			this.wIFFinanceDisbursementCtrl = (WIFFinanceDisbursementListCtrl) args.get("wIFFinanceDisbursementCtrl");
		} else {
			this.wIFFinanceDisbursementCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finReference.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbSeq.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbSeq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbAmount.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbActDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbActDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbDisbursed.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_disbDisbursed.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_disbIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_disbRemarks.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_disbRemarks.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_WIFFinanceDisbursementSearch_RecordStatus.setVisible(false);
			this.label_WIFFinanceDisbursementSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FinanceDisbursement> searchObj = (JdbcSearchObject<FinanceDisbursement>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("finReference")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finReference, filter);
					this.finReference.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbDate, filter);
					this.disbDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbSeq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbSeq, filter);
					this.disbSeq.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbDesc, filter);
					this.disbDesc.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbAmount")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbAmount, filter);
					this.disbAmount.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbActDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbActDate, filter);
					this.disbActDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbDisbursed")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbDisbursed, filter);
					this.disbDisbursed.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbIsActive, filter);
					this.disbIsActive.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("disbRemarks")) {
					SearchOperators.restoreStringOperator(this.sortOperator_disbRemarks, filter);
					this.disbRemarks.setValue(filter.getValue().toString());
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
		showWIFFinanceDisbursementSeekDialog();
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
		this.window_WIFFinanceDisbursementSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showWIFFinanceDisbursementSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_WIFFinanceDisbursementSearch.doModal();
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

		final JdbcSearchObject<FinanceDisbursement> so = new JdbcSearchObject<FinanceDisbursement>(FinanceDisbursement.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("WIFFinDisbursementDetails_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("WIFFinDisbursementDetails_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {

			// get the search operator
			final Listitem itemFinReference = this.sortOperator_finReference.getSelectedItem();

			if (itemFinReference != null) {
				final int searchOpId = ((SearchOperators) itemFinReference.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finReference", "%" + this.finReference.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finReference", this.finReference.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbDate.getValue())) {

			// get the search operator
			final Listitem itemDisbDate = this.sortOperator_disbDate.getSelectedItem();

			if (itemDisbDate != null) {
				final int searchOpId = ((SearchOperators) itemDisbDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbDate", "%" + this.disbDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbDate", this.disbDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbSeq.getValue())) {

			// get the search operator
			final Listitem itemDisbSeq = this.sortOperator_disbSeq.getSelectedItem();

			if (itemDisbSeq != null) {
				final int searchOpId = ((SearchOperators) itemDisbSeq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbSeq", "%" + this.disbSeq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbSeq", this.disbSeq.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbDesc.getValue())) {

			// get the search operator
			final Listitem itemDisbDesc = this.sortOperator_disbDesc.getSelectedItem();

			if (itemDisbDesc != null) {
				final int searchOpId = ((SearchOperators) itemDisbDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbDesc", "%" + this.disbDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbDesc", this.disbDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbAmount.getValue())) {

			// get the search operator
			final Listitem itemDisbAmount = this.sortOperator_disbAmount.getSelectedItem();

			if (itemDisbAmount != null) {
				final int searchOpId = ((SearchOperators) itemDisbAmount.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbAmount", "%" + this.disbAmount.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbAmount", this.disbAmount.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbActDate.getValue())) {

			// get the search operator
			final Listitem itemDisbActDate = this.sortOperator_disbActDate.getSelectedItem();

			if (itemDisbActDate != null) {
				final int searchOpId = ((SearchOperators) itemDisbActDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbActDate", "%" + this.disbActDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbActDate", this.disbActDate.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemDisbDisbursed = this.sortOperator_disbDisbursed.getSelectedItem();

		if (itemDisbDisbursed != null) {
			final int searchOpId = ((SearchOperators) itemDisbDisbursed.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.disbDisbursed.isChecked()){
					so.addFilter(new Filter("disbDisbursed",1, searchOpId));
				}else{
					so.addFilter(new Filter("disbDisbursed",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem itemDisbIsActive = this.sortOperator_disbIsActive.getSelectedItem();

		if (itemDisbIsActive != null) {
			final int searchOpId = ((SearchOperators) itemDisbIsActive.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.disbIsActive.isChecked()){
					so.addFilter(new Filter("disbIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("disbIsActive",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.disbRemarks.getValue())) {

			// get the search operator
			final Listitem itemDisbRemarks = this.sortOperator_disbRemarks.getSelectedItem();

			if (itemDisbRemarks != null) {
				final int searchOpId = ((SearchOperators) itemDisbRemarks.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("disbRemarks", "%" + this.disbRemarks.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("disbRemarks", this.disbRemarks.getValue(), searchOpId));
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
		// Defualt Sort on the table
		so.addSort("FinReference", false);

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
		this.wIFFinanceDisbursementCtrl.setSearchObj(so);

		final Listbox listBox = this.wIFFinanceDisbursementCtrl.listBoxWIFFinanceDisbursement;
		final Paging paging = this.wIFFinanceDisbursementCtrl.pagingWIFFinanceDisbursementList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<FinanceDisbursement>) listBox.getModel()).init(so, listBox, paging);
		this.wIFFinanceDisbursementCtrl.setSearchObj(so);

		this.label_WIFFinanceDisbursementSearchResult.setValue(Labels.getLabel("label_WIFFinanceDisbursementSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setWIFFinanceDisbursementService(WIFFinanceDisbursementService wIFFinanceDisbursementService) {
		this.wIFFinanceDisbursementService = wIFFinanceDisbursementService;
	}

	public WIFFinanceDisbursementService getWIFFinanceDisbursementService() {
		return this.wIFFinanceDisbursementService;
	}
}