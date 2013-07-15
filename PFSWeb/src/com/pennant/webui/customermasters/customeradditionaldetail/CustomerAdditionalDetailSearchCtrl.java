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
 * FileName    		:  CustomerAdditionalDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeradditionaldetail;

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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.service.customermasters.CustomerAdditionalDetailService;
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
 * /WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail
 * /CustomerAdditionalDetailSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class CustomerAdditionalDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8216248572576607741L;

	private final static Logger logger = Logger.getLogger(CustomerAdditionalDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerAdditionalDetailSearch; // autoWired
	
	protected Textbox custCIF; 							// autoWired
	protected Listbox sortOperator_custCIF; 				// autoWired
	protected Textbox custAcademicLevel; 				// autoWired
	protected Listbox sortOperator_custAcademicLevel; 	// autoWired
	protected Textbox academicDecipline; 				// autoWired
	protected Listbox sortOperator_academicDecipline; 	// autoWired
	protected Longbox custRefCustID; 					// autoWired
	protected Listbox sortOperator_custRefCustID; 		// autoWired
	protected Textbox custRefStaffID; 					// autoWired
	protected Listbox sortOperator_custRefStaffID; 		// autoWired
	protected Textbox recordStatus; 					// autoWired
	protected Listbox recordType;						// autoWired
	protected Listbox sortOperator_recordStatus; 		// autoWired
	protected Listbox sortOperator_recordType; 			// autoWired
	
	protected Label label_CustomerAdditionalDetailSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerAdditionalDetailSearch_RecordType; 	// autoWired
	protected Label label_CustomerAdditionalDetailSearchResult; 		// autoWired

	// not auto wired variables
	private transient CustomerAdditionalDetailListCtrl customerAdditionalDetailCtrl; // overHanded per parameter
	private transient CustomerAdditionalDetailService customerAdditionalDetailService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("CustomerAdditionalDetail");
	
	/**
	 * constructor
	 */
	public CustomerAdditionalDetailSearchCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerAdditional object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAdditionalDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("customerAdditionalDetailCtrl")) {
			this.customerAdditionalDetailCtrl = (CustomerAdditionalDetailListCtrl) args
					.get("customerAdditionalDetailCtrl");
		} else {
			this.customerAdditionalDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_custCIF.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAcademicLevel.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custAcademicLevel.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_academicDecipline.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_academicDecipline.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custRefCustID.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_custRefCustID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custRefStaffID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custRefStaffID.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CustomerAdditionalDetailSearch_RecordStatus.setVisible(false);
			this.label_CustomerAdditionalDetailSearch_RecordType.setVisible(false);
		}
		
		//Set Field Properties
		this.custRefCustID.setText("");
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerAdditionalDetail> searchObj = 
				(JdbcSearchObject<CustomerAdditionalDetail>)args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAcademicLevel")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAcademicLevel, filter);
					this.custAcademicLevel.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("academicDecipline")) {
					SearchOperators.restoreStringOperator(this.sortOperator_academicDecipline, filter);
					this.academicDecipline.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custRefCustID")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_custRefCustID, filter);
					this.custRefCustID.setValue(Long.parseLong(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("custRefStaffID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custRefStaffID, filter);
					this.custRefStaffID.setValue(filter.getValue().toString());
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
		showCustomerAdditionalDetailSeekDialog();
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
		logger.debug("Entering ");
		this.window_CustomerAdditionalDetailSearch.onClose();
		logger.debug("Leaving ");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerAdditionalDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering ");
		
		try {
			// open the dialog in modal mode
			this.window_CustomerAdditionalDetailSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		final JdbcSearchObject<CustomerAdditionalDetail> so = new JdbcSearchObject<CustomerAdditionalDetail>(
				CustomerAdditionalDetail.class);
		
		if (isWorkFlowEnabled()){
			so.addTabelName("CustAdditionalDetails_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		} else {
			so.addTabelName("CustAdditionalDetails_AView");
		}
		
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_custCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_custCIF != null) {
				final int searchOpId = ((SearchOperators) item_custCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%"+ this.custCIF.getValue().toUpperCase() + "%",searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAcademicLevel.getValue())) {

			// get the search operator
			final Listitem item_CustAcademicLevel = this.sortOperator_custAcademicLevel.getSelectedItem();

			if (item_CustAcademicLevel != null) {
				final int searchOpId = ((SearchOperators) item_CustAcademicLevel.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAcademicLevel", "%"+ this.custAcademicLevel.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAcademicLevel",this.custAcademicLevel.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.academicDecipline.getValue())) {

			// get the search operator
			final Listitem item_AcademicDecipline = this.sortOperator_academicDecipline.getSelectedItem();

			if (item_AcademicDecipline != null) {
				final int searchOpId = ((SearchOperators) item_AcademicDecipline.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("academicDecipline", "%"+ this.academicDecipline.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("academicDecipline",this.academicDecipline.getValue(), searchOpId));
				}
			}
		}
		if (this.custRefCustID.getValue() != null) {

			// get the search operator
			final Listitem item_CustRefCustID = this.sortOperator_custRefCustID.getSelectedItem();

			if (item_CustRefCustID != null) {
				final int searchOpId = ((SearchOperators) item_CustRefCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custRefCustID", this.custRefCustID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custRefStaffID.getValue())) {

			// get the search operator
			final Listitem item_CustRefStaffID = this.sortOperator_custRefStaffID.getSelectedItem();

			if (item_CustRefStaffID != null) {
				final int searchOpId = ((SearchOperators) item_CustRefStaffID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custRefStaffID", "%"+ this.custRefStaffID.getValue().toUpperCase()+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custRefStaffID",this.custRefStaffID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"+ this.recordStatus.getValue().toUpperCase() + "%",searchOpId));
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
					so.addFilter(new Filter("recordType", "%"+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("lovDescCustCIF", false);

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
		this.customerAdditionalDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.customerAdditionalDetailCtrl.listBoxCustomerAdditionalDetail;
		final Paging paging = this.customerAdditionalDetailCtrl.pagingCustomerAdditionalDetailList;
		this.customerAdditionalDetailCtrl.pagingCustomerAdditionalDetailList.getPageSize();

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CustomerAdditionalDetail>) listBox.getModel()).init(so, listBox, paging);
		this.customerAdditionalDetailCtrl.setSearchObj(so);

		this.label_CustomerAdditionalDetailSearchResult.setValue(Labels.getLabel(
				"label_CustomerAdditionalDetailSearchResult.value")+ " "+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerAdditionalDetailService(
			CustomerAdditionalDetailService customerAdditionalDetailService) {
		this.customerAdditionalDetailService = customerAdditionalDetailService;
	}
	public CustomerAdditionalDetailService getCustomerAdditionalDetailService() {
		return this.customerAdditionalDetailService;
	}
	
}