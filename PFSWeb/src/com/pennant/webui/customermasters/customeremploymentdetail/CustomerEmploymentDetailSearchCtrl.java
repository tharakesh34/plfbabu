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
 * FileName    		:  CustomerEmploymentDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeremploymentdetail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
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

public class CustomerEmploymentDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 670235824332034515L;
	private final static Logger logger = Logger.getLogger(CustomerEmploymentDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CustomerEmploymentDetailSearch; 	// autowired
	
	protected Textbox custCIF; 									// autowired
	protected Listbox sortOperator_custCIF; 					// autowired
	protected Textbox custEmpName; 								// autowired
	protected Listbox sortOperator_custEmpName; 				// autowired
	protected Datebox custEmpFrom; 								// autowired
	protected Listbox sortOperator_custEmpFrom; 				// autowired
	protected Textbox custEmpDesg; 								// autowired
	protected Listbox sortOperator_custEmpDesg; 				// autowired
	protected Textbox custEmpDept; 								// autowired
	protected Listbox sortOperator_custEmpDept; 				// autowired
	protected Textbox custEmpID; 								// autowired
	protected Listbox sortOperator_custEmpID; 					// autowired
	protected Textbox custEmpType; 								// autowired
	protected Listbox sortOperator_custEmpType; 				// autowired
	protected Textbox custEmpHNbr; 								// autowired
	protected Listbox sortOperator_custEmpHNbr; 				// autowired
	protected Textbox custEMpFlatNbr; 							// autowired
	protected Listbox sortOperator_custEMpFlatNbr; 				// autowired
	protected Textbox custEmpAddrStreet; 						// autowired
	protected Listbox sortOperator_custEmpAddrStreet; 			// autowired
	protected Textbox custEmpPOBox; 							// autowired
	protected Listbox sortOperator_custEmpPOBox; 				// autowired
	protected Textbox custEmpAddrCity; 							// autowired
	protected Listbox sortOperator_custEmpAddrCity; 			// autowired
	protected Textbox custEmpAddrProvince; 						// autowired
	protected Listbox sortOperator_custEmpAddrProvince; 		// autowired
	protected Textbox custEmpAddrCountry; 						// autowired
	protected Listbox sortOperator_custEmpAddrCountry; 			// autowired
	protected Textbox custEmpAddrZIP; 							// autowired
	protected Listbox sortOperator_custEmpAddrZIP; 				// autowired
	protected Textbox custEmpAddrPhone; 						// autowired
	protected Listbox sortOperator_custEmpAddrPhone; 			// autowired
	protected Textbox recordStatus; 							// autowired
	protected Listbox recordType;								// autowired
	protected Listbox sortOperator_recordStatus; 				// autowired
	protected Listbox sortOperator_recordType; 					// autowired
	
	protected Label label_CustomerEmploymentDetailSearch_RecordStatus; 	// autowired
	protected Label label_CustomerEmploymentDetailSearch_RecordType; 	// autowired
	protected Label label_CustomerEmploymentDetailSearchResult; 		// autowired

	// not auto wired vars
	private transient CustomerEmploymentDetailListCtrl customerEmploymentDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerEmploymentDetail");
	
	/**
	 * constructor
	 */
	public CustomerEmploymentDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEmploymentDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("customerEmploymentDetailCtrl")) {
			this.customerEmploymentDetailCtrl = (CustomerEmploymentDetailListCtrl) args.get(
					"customerEmploymentDetailCtrl");
		} else {
			this.customerEmploymentDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_custCIF.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpFrom.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_custEmpFrom.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpDesg.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpDesg.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpDept.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpDept.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpHNbr.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpHNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEMpFlatNbr.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEMpFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrStreet.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrStreet.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpPOBox.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrCity.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrCity.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrProvince.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrCountry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrZIP.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrZIP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpAddrPhone.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpAddrPhone.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CustomerEmploymentDetailSearch_RecordStatus.setVisible(false);
			this.label_CustomerEmploymentDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerEmploymentDetail> searchObj = (JdbcSearchObject<CustomerEmploymentDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpName, filter);
					this.custEmpName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpFrom")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_custEmpFrom, filter);
					this.custEmpFrom.setValue(DateUtility.getUtilDate(filter.getValue().toString(),PennantConstants.DBDateFormat));
			    } else if (filter.getProperty().equals("custEmpDesg")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpDesg, filter);
					this.custEmpDesg.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpDept")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpDept, filter);
					this.custEmpDept.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpID, filter);
					this.custEmpID.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpType, filter);
					this.custEmpType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpHNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpHNbr, filter);
					this.custEmpHNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEMpFlatNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEMpFlatNbr, filter);
					this.custEMpFlatNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrStreet")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrStreet, filter);
					this.custEmpAddrStreet.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpPOBox")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpPOBox, filter);
					this.custEmpPOBox.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrCity")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrCity, filter);
					this.custEmpAddrCity.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrProvince")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrProvince, filter);
					this.custEmpAddrProvince.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrCountry, filter);
					this.custEmpAddrCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrZIP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrZIP, filter);
					this.custEmpAddrZIP.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpAddrPhone")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpAddrPhone, filter);
					this.custEmpAddrPhone.setValue(filter.getValue().toString());
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
		showCustomerEmploymentDetailSeekDialog();
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
		this.window_CustomerEmploymentDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerEmploymentDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerEmploymentDetailSearch.doModal();
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
		
		final JdbcSearchObject<CustomerEmploymentDetail> so = new JdbcSearchObject<CustomerEmploymentDetail>(
				CustomerEmploymentDetail.class);
		so.addTabelName("CustomerEmpDetails_View");
		so.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_CustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustCIF != null) {
				final int searchOpId = ((SearchOperators) item_CustCIF.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + 
							this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpName.getValue())) {

			// get the search operator
			final Listitem item_CustEmpName = this.sortOperator_custEmpName.getSelectedItem();

			if (item_CustEmpName != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpName", "%" + 
							this.custEmpName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpName", this.custEmpName.getValue(), searchOpId));
				}
			}
		}
		if (this.custEmpFrom.getValue() != null) {

			// get the search operator
			final Listitem item_CustEmpFrom = this.sortOperator_custEmpFrom.getSelectedItem();

			if (item_CustEmpFrom != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpFrom.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpFrom", DateUtility.formatUtilDate(
							this.custEmpFrom.getValue(),PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpDesg.getValue())) {

			// get the search operator
			final Listitem item_CustEmpDesg = this.sortOperator_custEmpDesg.getSelectedItem();

			if (item_CustEmpDesg != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpDesg.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpDesg", "%" + 
							this.custEmpDesg.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpDesg", this.custEmpDesg.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpDept.getValue())) {

			// get the search operator
			final Listitem item_CustEmpDept = this.sortOperator_custEmpDept.getSelectedItem();

			if (item_CustEmpDept != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpDept.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpDept", "%" + 
							this.custEmpDept.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpDept", this.custEmpDept.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpID.getValue())) {

			// get the search operator
			final Listitem item_CustEmpID = this.sortOperator_custEmpID.getSelectedItem();

			if (item_CustEmpID != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpID.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpID", "%" + 
							this.custEmpID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpID", this.custEmpID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpType.getValue())) {

			// get the search operator
			final Listitem item_CustEmpType = this.sortOperator_custEmpType.getSelectedItem();

			if (item_CustEmpType != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpType.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpType", "%" +
							this.custEmpType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpType", this.custEmpType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpHNbr.getValue())) {

			// get the search operator
			final Listitem item_CustEmpHNbr = this.sortOperator_custEmpHNbr.getSelectedItem();

			if (item_CustEmpHNbr != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpHNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpHNbr", "%" + 
							this.custEmpHNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpHNbr", this.custEmpHNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEMpFlatNbr.getValue())) {

			// get the search operator
			final Listitem item_CustEMpFlatNbr = this.sortOperator_custEMpFlatNbr.getSelectedItem();

			if (item_CustEMpFlatNbr != null) {
				final int searchOpId = ((SearchOperators) item_CustEMpFlatNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEMpFlatNbr", "%" + 
							this.custEMpFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEMpFlatNbr", this.custEMpFlatNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrStreet.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrStreet = this.sortOperator_custEmpAddrStreet.getSelectedItem();

			if (item_CustEmpAddrStreet != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrStreet.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrStreet", "%" + 
							this.custEmpAddrStreet.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrStreet", this.custEmpAddrStreet.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpPOBox.getValue())) {

			// get the search operator
			final Listitem item_CustEmpPOBox = this.sortOperator_custEmpPOBox.getSelectedItem();

			if (item_CustEmpPOBox != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpPOBox.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpPOBox", "%" + 
							this.custEmpPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpPOBox", this.custEmpPOBox.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrCity.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrCity = this.sortOperator_custEmpAddrCity.getSelectedItem();

			if (item_CustEmpAddrCity != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrCity.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrCity", "%" + 
							this.custEmpAddrCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrCity", this.custEmpAddrCity.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrProvince.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrProvince = this.sortOperator_custEmpAddrProvince.getSelectedItem();

			if (item_CustEmpAddrProvince != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrProvince.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrProvince", "%" + 
							this.custEmpAddrProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrProvince", this.custEmpAddrProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrCountry.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrCountry = this.sortOperator_custEmpAddrCountry.getSelectedItem();

			if (item_CustEmpAddrCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrCountry.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrCountry", "%" + 
							this.custEmpAddrCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrCountry", this.custEmpAddrCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrZIP.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrZIP = this.sortOperator_custEmpAddrZIP.getSelectedItem();

			if (item_CustEmpAddrZIP != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrZIP.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrZIP", "%" +
							this.custEmpAddrZIP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrZIP", this.custEmpAddrZIP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpAddrPhone.getValue())) {

			// get the search operator
			final Listitem item_CustEmpAddrPhone = this.sortOperator_custEmpAddrPhone.getSelectedItem();

			if (item_CustEmpAddrPhone != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpAddrPhone.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpAddrPhone", "%" +
							this.custEmpAddrPhone.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpAddrPhone", this.custEmpAddrPhone.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute(
						"data")).getSearchOperatorId();
	
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
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute(
						"data")).getSearchOperatorId();
	
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
		this.customerEmploymentDetailCtrl.setSearchObj(so);
		final Listbox listBox = this.customerEmploymentDetailCtrl.listBoxCustomerEmploymentDetail;
		final Paging paging = this.customerEmploymentDetailCtrl.pagingCustomerEmploymentDetailList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<CustomerEmploymentDetail>) listBox.getModel()).init(so, listBox, paging);

		this.label_CustomerEmploymentDetailSearchResult.setValue(Labels.getLabel(
				"label_CustomerEmploymentDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

}