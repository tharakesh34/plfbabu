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
 * FileName    		:  CommodityBrokerDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.finance.commodity.commoditybrokerdetail;

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
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
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
 * /WEB-INF/pages/Finance.Commodity/CommodityBrokerDetail/commodityBrokerDetailSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CommodityBrokerDetailSearchCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = -1942740756934254488L;
	private final static Logger logger = Logger.getLogger(CommodityBrokerDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CommodityBrokerDetailSearch;          // autoWired

	protected Textbox brokerCode;                                 // autoWired
	protected Listbox sortOperator_brokerCode;                    // autoWired
	protected Textbox brokerCustID;                               // autoWired
	protected Listbox sortOperator_brokerCustID;                  // autoWired
	protected Textbox brokerCIF;                                  // autoWired
	protected Listbox sortOperator_brokerCIF;                     // autoWired
	protected Datebox brokerFrom;                                 // autoWired
	protected Listbox sortOperator_brokerFrom;                    // autoWired
	protected Textbox brokerAddrHNbr;                             // autoWired
	protected Listbox sortOperator_brokerAddrHNbr;                // autoWired
	protected Textbox brokerAddrFlatNbr;                          // autoWired
	protected Listbox sortOperator_brokerAddrFlatNbr;             // autoWired
	protected Textbox brokerAddrStreet;                           // autoWired
	protected Listbox sortOperator_brokerAddrStreet;              // autoWired
	protected Textbox brokerAddrLane1;                            // autoWired
	protected Listbox sortOperator_brokerAddrLane1;               // autoWired
	protected Textbox brokerAddrLane2;                            // autoWired
	protected Listbox sortOperator_brokerAddrLane2;               // autoWired
	protected Textbox brokerAddrPOBox;                            // autoWired
	protected Listbox sortOperator_brokerAddrPOBox;               // autoWired
	protected Textbox brokerAddrCountry;                          // autoWired
	protected Listbox sortOperator_brokerAddrCountry;             // autoWired
	protected Textbox brokerAddrProvince;                         // autoWired
	protected Listbox sortOperator_brokerAddrProvince;            // autoWired
	protected Textbox brokerAddrCity;                             // autoWired
	protected Listbox sortOperator_brokerAddrCity;                // autoWired
	protected Textbox brokerAddrZIP;                              // autoWired
	protected Listbox sortOperator_brokerAddrZIP;                 // autoWired
	protected Textbox brokerAddrPhone;                            // autoWired
	protected Listbox sortOperator_brokerAddrPhone;               // autoWired
	protected Textbox brokerAddrFax;                              // autoWired
	protected Listbox sortOperator_brokerAddrFax;                 // autoWired
	protected Textbox brokerEmail;                                // autoWired
	protected Listbox sortOperator_brokerEmail;                   // autoWired
	protected Textbox agreementRef;                               // autoWired
	protected Listbox sortOperator_agreementRef;                  // autoWired
	protected Textbox recordStatus;                               // autoWired
	protected Listbox recordType;	                              // autoWired
	protected Listbox sortOperator_recordStatus;                  // autoWired
	protected Listbox sortOperator_recordType;                    // autoWired

	protected Label label_CommodityBrokerDetailSearch_RecordStatus;// autoWired
	protected Label label_CommodityBrokerDetailSearch_RecordType;  // autoWired
	protected Label label_CommodityBrokerDetailSearchResult;       // autoWired

	// not auto wired variables
	private transient CommodityBrokerDetailListCtrl commodityBrokerDetailCtrl; // over handed per parameters
	private transient CommodityBrokerDetailService commodityBrokerDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CommodityBrokerDetail");

	/**
	 * constructor
	 */
	public CommodityBrokerDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CommodityBrokerDetailSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("commodityBrokerDetailCtrl")) {
			this.commodityBrokerDetailCtrl = (CommodityBrokerDetailListCtrl) args.get("commodityBrokerDetailCtrl");
		} else {
			this.commodityBrokerDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_brokerCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerCustID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerCustID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerCIF.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.brokerFrom.setFormat(PennantConstants.dateFormat);
		this.sortOperator_brokerFrom.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_brokerFrom.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrHNbr.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrHNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrFlatNbr.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrStreet.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrStreet.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrLane1.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrLane1.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrLane2.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrLane2.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrPOBox.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrCountry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrProvince.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrCity.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrCity.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrZIP.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrZIP.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrPhone.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrPhone.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerAddrFax.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerAddrFax.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_brokerEmail.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_brokerEmail.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_agreementRef.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_agreementRef.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CommodityBrokerDetailSearch_RecordStatus.setVisible(false);
			this.label_CommodityBrokerDetailSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<CommodityBrokerDetail> searchObj = (JdbcSearchObject<CommodityBrokerDetail>) args
			.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("brokerCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerCode, filter);
					this.brokerCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerCustID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerCustID, filter);
					this.brokerCustID.setValue(filter.getValue().toString());
				}else if(filter.getProperty().equals("brokerCIF")){
					SearchOperators.restoreStringOperator(this.sortOperator_brokerCIF, filter);
					this.brokerCustID.setValue(filter.getValue().toString());
				} 
				else if (filter.getProperty().equals("brokerFrom")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerFrom, filter);
					this.brokerFrom.setText(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrHNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrHNbr, filter);
					this.brokerAddrHNbr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrFlatNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrFlatNbr, filter);
					this.brokerAddrFlatNbr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrStreet")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrStreet, filter);
					this.brokerAddrStreet.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrLane1")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrLane1, filter);
					this.brokerAddrLane1.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrLane2")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrLane2, filter);
					this.brokerAddrLane2.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrPOBox")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrPOBox, filter);
					this.brokerAddrPOBox.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrCountry, filter);
					this.brokerAddrCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrProvince")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrProvince, filter);
					this.brokerAddrProvince.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrCity")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrCity, filter);
					this.brokerAddrCity.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrZIP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrZIP, filter);
					this.brokerAddrZIP.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrPhone")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrPhone, filter);
					this.brokerAddrPhone.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerAddrFax")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerAddrFax, filter);
					this.brokerAddrFax.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("brokerEmail")) {
					SearchOperators.restoreStringOperator(this.sortOperator_brokerEmail, filter);
					this.brokerEmail.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("agreementRef")) {
					SearchOperators.restoreStringOperator(this.sortOperator_agreementRef, filter);
					this.agreementRef.setValue(filter.getValue().toString());
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
		showCommodityBrokerDetailSeekDialog();
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
		this.window_CommodityBrokerDetailSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCommodityBrokerDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CommodityBrokerDetailSearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<CommodityBrokerDetail> so = new JdbcSearchObject<CommodityBrokerDetail>(CommodityBrokerDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("FCMTBrokerDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("FCMTBrokerDetail_AView");
		}


		if (StringUtils.isNotEmpty(this.brokerCode.getValue())) {

			// get the search operator
			final Listitem item_BrokerCode = this.sortOperator_brokerCode.getSelectedItem();

			if (item_BrokerCode != null) {
				final int searchOpId = ((SearchOperators) item_BrokerCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerCode", "%" + this.brokerCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerCode", this.brokerCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerCustID.getValue())) {

			// get the search operator
			final Listitem item_BrokerCustID = this.sortOperator_brokerCustID.getSelectedItem();

			if (item_BrokerCustID != null) {
				final int searchOpId = ((SearchOperators) item_BrokerCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerCustID", "%" + this.brokerCustID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerCustID", this.brokerCustID.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.brokerCIF.getValue())) {

			// get the search operator
			final Listitem item_BrokerCIF = this.sortOperator_brokerCIF.getSelectedItem();

			if (item_BrokerCIF != null) {
				final int searchOpId = ((SearchOperators) item_BrokerCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescBrokerCIF", "%" + this.brokerCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescBrokerCIF", this.brokerCIF.getValue(), searchOpId));
				}
			}
		}
		if (this.brokerFrom.getValue()!=null) {

			// get the search operator
			final Listitem item_BrokerFrom = this.sortOperator_brokerFrom.getSelectedItem();

			if (item_BrokerFrom != null) {
				final int searchOpId = ((SearchOperators) item_BrokerFrom.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerFrom", "'"+ DateUtility.getDate(
							DateUtility.formatUtilDate(this.brokerFrom.getValue(),
									PennantConstants.dateFormat)) +"'" , searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrHNbr.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrHNbr = this.sortOperator_brokerAddrHNbr.getSelectedItem();

			if (item_BrokerAddrHNbr != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrHNbr.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrHNbr", "%" + this.brokerAddrHNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrHNbr", this.brokerAddrHNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrFlatNbr.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrFlatNbr = this.sortOperator_brokerAddrFlatNbr.getSelectedItem();

			if (item_BrokerAddrFlatNbr != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrFlatNbr.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrFlatNbr", "%" + this.brokerAddrFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrFlatNbr", this.brokerAddrFlatNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrStreet.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrStreet = this.sortOperator_brokerAddrStreet.getSelectedItem();

			if (item_BrokerAddrStreet != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrStreet.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrStreet", "%" + this.brokerAddrStreet.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrStreet", this.brokerAddrStreet.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrLane1.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrLane1 = this.sortOperator_brokerAddrLane1.getSelectedItem();

			if (item_BrokerAddrLane1 != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrLane1.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrLane1", "%" + this.brokerAddrLane1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrLane1", this.brokerAddrLane1.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrLane2.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrLane2 = this.sortOperator_brokerAddrLane2.getSelectedItem();

			if (item_BrokerAddrLane2 != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrLane2.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrLane2", "%" + this.brokerAddrLane2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrLane2", this.brokerAddrLane2.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrPOBox.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrPOBox = this.sortOperator_brokerAddrPOBox.getSelectedItem();

			if (item_BrokerAddrPOBox != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrPOBox.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrPOBox", "%" + this.brokerAddrPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrPOBox", this.brokerAddrPOBox.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrCountry.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrCountry = this.sortOperator_brokerAddrCountry.getSelectedItem();

			if (item_BrokerAddrCountry != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrCountry", "%" + this.brokerAddrCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrCountry", this.brokerAddrCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrProvince.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrProvince = this.sortOperator_brokerAddrProvince.getSelectedItem();

			if (item_BrokerAddrProvince != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrProvince.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrProvince", "%" + this.brokerAddrProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrProvince", this.brokerAddrProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrCity.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrCity = this.sortOperator_brokerAddrCity.getSelectedItem();

			if (item_BrokerAddrCity != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrCity.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrCity", "%" + this.brokerAddrCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrCity", this.brokerAddrCity.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrZIP.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrZIP = this.sortOperator_brokerAddrZIP.getSelectedItem();

			if (item_BrokerAddrZIP != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrZIP.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrZIP", "%" + this.brokerAddrZIP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrZIP", this.brokerAddrZIP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrPhone.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrPhone = this.sortOperator_brokerAddrPhone.getSelectedItem();

			if (item_BrokerAddrPhone != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrPhone.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrPhone", "%" + this.brokerAddrPhone.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrPhone", this.brokerAddrPhone.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerAddrFax.getValue())) {

			// get the search operator
			final Listitem item_BrokerAddrFax = this.sortOperator_brokerAddrFax.getSelectedItem();

			if (item_BrokerAddrFax != null) {
				final int searchOpId = ((SearchOperators) item_BrokerAddrFax.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerAddrFax", "%" + this.brokerAddrFax.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerAddrFax", this.brokerAddrFax.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.brokerEmail.getValue())) {

			// get the search operator
			final Listitem item_BrokerEmail = this.sortOperator_brokerEmail.getSelectedItem();

			if (item_BrokerEmail != null) {
				final int searchOpId = ((SearchOperators) item_BrokerEmail.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("brokerEmail", "%" + this.brokerEmail.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("brokerEmail", this.brokerEmail.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.agreementRef.getValue())) {

			// get the search operator
			final Listitem item_AgreementRef = this.sortOperator_agreementRef.getSelectedItem();

			if (item_AgreementRef != null) {
				final int searchOpId = ((SearchOperators) item_AgreementRef.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("agreementRef", "%" + this.agreementRef.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("agreementRef", this.agreementRef.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
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
		so.addSort("BrokerCode", false);

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
		this.commodityBrokerDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.commodityBrokerDetailCtrl.listBoxCommodityBrokerDetail;
		final Paging paging = this.commodityBrokerDetailCtrl.pagingCommodityBrokerDetailList;


		// set the model to the list box with the initial result set get by the DAO method.
		((PagedListWrapper<CommodityBrokerDetail>) listBox.getModel()).init(so, listBox, paging);
		this.commodityBrokerDetailCtrl.setSearchObj(so);

		this.label_CommodityBrokerDetailSearchResult.setValue(Labels.getLabel("label_CommodityBrokerDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCommodityBrokerDetailService(CommodityBrokerDetailService commodityBrokerDetailService) {
		this.commodityBrokerDetailService = commodityBrokerDetailService;
	}

	public CommodityBrokerDetailService getCommodityBrokerDetailService() {
		return this.commodityBrokerDetailService;
	}
}