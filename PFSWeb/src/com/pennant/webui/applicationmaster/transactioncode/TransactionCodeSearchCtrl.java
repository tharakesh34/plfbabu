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
 * FileName    		:  TransactionCodeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.transactioncode;

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
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
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
 * /WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class TransactionCodeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3788218903451016825L;
	private final static Logger logger = Logger.getLogger(TransactionCodeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_TransactionCodeSearch; // autoWired
	
	protected Textbox tranCode; 					// autoWired
	protected Listbox sortOperator_tranCode; 		// autoWired
	protected Textbox tranDesc; 					// autoWired
	protected Listbox sortOperator_tranDesc; 		// autoWired
	protected Textbox tranType; 					// autoWired
	protected Listbox sortOperator_tranType; 		// autoWired
	protected Checkbox tranIsActive; 				// autoWired
	protected Listbox sortOperator_tranIsActive; 	// autoWired
	protected Textbox recordStatus; 				// autoWired
	protected Listbox recordType;					// autoWired
	protected Listbox sortOperator_recordStatus; 	// autoWired
	protected Listbox sortOperator_recordType; 		// autoWired
	
	protected Label label_TransactionCodeSearch_RecordStatus; 	// autoWired
	protected Label label_TransactionCodeSearch_RecordType; 	// autoWired
	protected Label label_TransactionCodeSearchResult; 			// autoWired

	// not auto wired variables
	private transient TransactionCodeListCtrl transactionCodeCtrl; // overHanded per parameter
	private transient TransactionCodeService transactionCodeService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("TransactionCode");
	
	/**
	 * constructor
	 */
	public TransactionCodeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected TransactionCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionCodeSearch(Event event) throws Exception {
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

		if (args.containsKey("transactionCodeCtrl")) {
			this.transactionCodeCtrl = (TransactionCodeListCtrl) args.get("transactionCodeCtrl");
		} else {
			this.transactionCodeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_tranCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_tranCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_tranDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_tranDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_tranType.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_tranType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_tranIsActive.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_tranIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_TransactionCodeSearch_RecordStatus.setVisible(false);
			this.label_TransactionCodeSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<TransactionCode> searchObj = (JdbcSearchObject<TransactionCode>) 
																	args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("tranCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_tranCode, filter);
					this.tranCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("tranDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_tranDesc, filter);
					this.tranDesc.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("tranType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_tranType, filter);
					this.tranType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("tranIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_tranIsActive, filter);
					//this.tranIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.tranIsActive.setChecked(true);
					}else{
						this.tranIsActive.setChecked(false);
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
		showTransactionCodeSeekDialog();
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
		logger.debug("Leaving" + event.toString());
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
		this.window_TransactionCodeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showTransactionCodeSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_TransactionCodeSearch.doModal();
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
		
		final JdbcSearchObject<TransactionCode> so = new JdbcSearchObject<TransactionCode>(
				TransactionCode.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTTransactionCode_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTTransactionCode_AView");
		}
		
		if (StringUtils.isNotEmpty(this.tranCode.getValue())) {

			// get the search operator
			final Listitem itemTranCode = this.sortOperator_tranCode.getSelectedItem();
			if (itemTranCode != null) {
				final int searchOpId = ((SearchOperators) itemTranCode.getAttribute("data")).
													getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("tranCode", "%" + this.tranCode.getValue().toUpperCase()
																		+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("tranCode", this.tranCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.tranDesc.getValue())) {

			// get the search operator
			final Listitem itemTranDesc = this.sortOperator_tranDesc.getSelectedItem();
			if (itemTranDesc != null) {
				final int searchOpId = ((SearchOperators) itemTranDesc.getAttribute("data")).
														getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("tranDesc", "%" + this.tranDesc.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("tranDesc", this.tranDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.tranType.getValue())) {

			// get the search operator
			final Listitem itemTranType = this.sortOperator_tranType.getSelectedItem();
			if (itemTranType != null) {
				final int searchOpId = ((SearchOperators) itemTranType.getAttribute("data")).
											getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("tranType", "%" + this.tranType.getValue().toUpperCase() 
								+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("tranType", this.tranType.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemTranIsActive = this.sortOperator_tranIsActive.getSelectedItem();
		if (itemTranIsActive != null) {
			final int searchOpId = ((SearchOperators) itemTranIsActive.getAttribute("data")).
														getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.tranIsActive.isChecked()){
					so.addFilter(new Filter("tranIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("tranIsActive",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute("data")).
																getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().
									toUpperCase() + "%", searchOpId));
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
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute("data")).
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
		so.addSort("TranCode", false);

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
		this.transactionCodeCtrl.setSearchObj(so);

		final Listbox listBox = this.transactionCodeCtrl.listBoxTransactionCode;
		final Paging paging = this.transactionCodeCtrl.pagingTransactionCodeList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<TransactionCode>) listBox.getModel()).init(so, listBox, paging);
		this.transactionCodeCtrl.setSearchObj(so);

		this.label_TransactionCodeSearchResult.setValue(Labels.getLabel("label_TransactionCodeSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}
	public TransactionCodeService getTransactionCodeService() {
		return this.transactionCodeService;
	}
}