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
 * FileName    		:  CustomerSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class CustomerSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CustomerSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerSearch; // autowired
	
	protected Textbox custID; // autowired
	protected Listbox sortOperator_custID; // autowired
	protected Textbox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
	protected Textbox custCoreBank; // autowired
	protected Listbox sortOperator_custCoreBank; // autowired
	protected Textbox custCtgCode; // autowired
	protected Listbox sortOperator_custCtgCode; // autowired
	protected Textbox custTypeCode; // autowired
	protected Listbox sortOperator_custTypeCode; // autowired
	protected Textbox custSalutationCode; // autowired
	protected Listbox sortOperator_custSalutationCode; // autowired
	protected Textbox custFName; // autowired
	protected Listbox sortOperator_custFName; // autowired
	protected Textbox custMName; // autowired
	protected Listbox sortOperator_custMName; // autowired
	protected Textbox custLName; // autowired
	protected Listbox sortOperator_custLName; // autowired
	protected Textbox custShrtName; // autowired
	protected Listbox sortOperator_custShrtName; // autowired
	protected Textbox custDftBranch; // autowired
	protected Listbox sortOperator_custDftBranch; // autowired
	/*protected Textbox custRO1; // autowired
	protected Listbox sortOperator_custRO1; // autowired
	protected Textbox custSts; // autowired
	protected Listbox sortOperator_custSts; // autowired
	protected Checkbox custIsBlocked; // autowired
	protected Listbox sortOperator_custIsBlocked; // autowired
	protected Checkbox custIsActive; // autowired
	protected Listbox sortOperator_custIsActive; // autowired
	protected Checkbox custIsClosed; // autowired
	protected Listbox sortOperator_custIsClosed; // autowired
	protected Textbox custInactiveReason; // autowired
	protected Listbox sortOperator_custInactiveReason; // autowired
	protected Checkbox custIsTradeFinCust; // autowired
	protected Listbox sortOperator_custIsTradeFinCust; // autowired
	protected Checkbox custIsStaff; // autowired
	protected Listbox sortOperator_custIsStaff; // autowired
	protected Textbox custStaffID; // autowired
	protected Listbox sortOperator_custStaffID; // autowired
	protected Textbox custIndustry; // autowired
	protected Listbox sortOperator_custIndustry; // autowired
	protected Textbox custSector; // autowired
	protected Listbox sortOperator_custSector; // autowired
	protected Textbox custSubSector; // autowired
	protected Listbox sortOperator_custSubSector; // autowired
	protected Textbox custProfession; // autowired
	protected Listbox sortOperator_custProfession; // autowired
	protected Textbox custTotalIncome; // autowired
	protected Listbox sortOperator_custTotalIncome; // autowired
	protected Textbox custMaritalSts; // autowired
	protected Listbox sortOperator_custMaritalSts; // autowired
	protected Textbox custEmpSts; // autowired
	protected Listbox sortOperator_custEmpSts; // autowired
	protected Textbox custSegment; // autowired
	protected Listbox sortOperator_custSegment; // autowired
	protected Textbox custSubSegment; // autowired
	protected Listbox sortOperator_custSubSegment; // autowired
	protected Checkbox custIsBlackListed; // autowired
	protected Listbox sortOperator_custIsBlackListed; // autowired
	protected Textbox custBLRsnCode; // autowired
	protected Listbox sortOperator_custBLRsnCode; // autowired
	protected Checkbox custIsRejected; // autowired
	protected Listbox sortOperator_custIsRejected; // autowired
	protected Textbox custRejectedRsn; // autowired
	protected Listbox sortOperator_custRejectedRsn; // autowired
	protected Textbox custBaseCcy; // autowired
	protected Listbox sortOperator_custBaseCcy; // autowired
	protected Textbox custLng; // autowired
	protected Listbox sortOperator_custLng; // autowired
	protected Textbox custParentCountry; // autowired
	protected Listbox sortOperator_custParentCountry; // autowired
	protected Textbox custResdCountry; // autowired
	protected Listbox sortOperator_custResdCountry; // autowired
	protected Textbox custRiskCountry; // autowired
	protected Listbox sortOperator_custRiskCountry; // autowired
	protected Textbox custNationality; // autowired
	protected Listbox sortOperator_custNationality; // autowired
	protected Textbox custClosedOn; // autowired
	protected Listbox sortOperator_custClosedOn; // autowired
	protected Textbox custStmtFrq; // autowired
	protected Listbox sortOperator_custStmtFrq; // autowired
	protected Textbox custStmtFrqDay; // autowired
	protected Listbox sortOperator_custStmtFrqDay; // autowired
	protected Checkbox custIsStmtCombined; // autowired
	protected Listbox sortOperator_custIsStmtCombined; // autowired
*/	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_CustomerSearch_RecordStatus; // autowired
	protected Label label_CustomerSearch_RecordType; // autowired
	protected Label label_CustomerSearchResult; // autowired

	// not auto wired vars
	private transient CustomerListCtrl customerCtrl; // overhanded per param
	private transient CustomerService customerService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Customer");
	
	/**
	 * constructor
	 */
	public CustomerSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerCtrl")) {
			this.customerCtrl = (CustomerListCtrl) args.get("customerCtrl");
		} else {
			this.customerCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_custID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custCIF.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custCoreBank.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCoreBank.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custCtgCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custCtgCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custTypeCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSalutationCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSalutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custFName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custFName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custMName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custMName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custLName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custLName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custShrtName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custShrtName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custDftBranch.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custDftBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		/*this.sortOperator_custRO1.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custRO1.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSts.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSts.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsBlocked.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsBlocked.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsClosed.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsClosed.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custInactiveReason.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custInactiveReason.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsTradeFinCust.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsTradeFinCust.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsStaff.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsStaff.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custStaffID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custStaffID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIndustry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custIndustry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSector.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSector.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSubSector.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSubSector.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custProfession.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custProfession.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custTotalIncome.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custTotalIncome.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custMaritalSts.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custMaritalSts.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpSts.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpSts.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSegment.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSegment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSubSegment.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custSubSegment.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsBlackListed.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsBlackListed.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custBLRsnCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custBLRsnCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsRejected.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsRejected.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custRejectedRsn.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custRejectedRsn.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custBaseCcy.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custBaseCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custLng.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custLng.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custParentCountry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custParentCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custResdCountry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custResdCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custRiskCountry.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custRiskCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custNationality.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custNationality.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custClosedOn.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custClosedOn.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custStmtFrq.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custStmtFrq.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custStmtFrqDay.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custStmtFrqDay.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custIsStmtCombined.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custIsStmtCombined.setItemRenderer(new SearchOperatorListModelItemRenderer());*/
		
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
			this.label_CustomerSearch_RecordStatus.setVisible(false);
			this.label_CustomerSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Customer> searchObj = (JdbcSearchObject<Customer>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("custID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custID, filter);
					this.custID.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCIF, filter);
					this.custCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custCoreBank")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCoreBank, filter);
					this.custCoreBank.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custCtgCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custCtgCode, filter);
					this.custCtgCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custTypeCode, filter);
					this.custTypeCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSalutationCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSalutationCode, filter);
					this.custSalutationCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custFName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custFName, filter);
					this.custFName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custMName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custMName, filter);
					this.custMName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custLName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custLName, filter);
					this.custLName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custShrtName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custShrtName, filter);
					this.custShrtName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custDftBranch")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custDftBranch, filter);
					this.custDftBranch.setValue(filter.getValue().toString());
			    } /*else if (filter.getProperty().equals("custRO1")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custRO1, filter);
					this.custRO1.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSts")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSts, filter);
					this.custSts.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsBlocked")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsBlocked, filter);
					this.custIsBlocked.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsActive, filter);
					this.custIsActive.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsClosed")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsClosed, filter);
					this.custIsClosed.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custInactiveReason")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custInactiveReason, filter);
					this.custInactiveReason.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsTradeFinCust")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsTradeFinCust, filter);
					this.custIsTradeFinCust.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsStaff")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsStaff, filter);
					this.custIsStaff.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custStaffID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custStaffID, filter);
					this.custStaffID.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIndustry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIndustry, filter);
					this.custIndustry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSector")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSector, filter);
					this.custSector.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSubSector")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSubSector, filter);
					this.custSubSector.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custProfession")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custProfession, filter);
					this.custProfession.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custTotalIncome")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custTotalIncome, filter);
					this.custTotalIncome.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custMaritalSts")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custMaritalSts, filter);
					this.custMaritalSts.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custEmpSts")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custEmpSts, filter);
					this.custEmpSts.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSegment")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSegment, filter);
					this.custSegment.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSubSegment")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSubSegment, filter);
					this.custSubSegment.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsBlackListed")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsBlackListed, filter);
					this.custIsBlackListed.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custBLRsnCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custBLRsnCode, filter);
					this.custBLRsnCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsRejected")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsRejected, filter);
					this.custIsRejected.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custRejectedRsn")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custRejectedRsn, filter);
					this.custRejectedRsn.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custBaseCcy")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custBaseCcy, filter);
					this.custBaseCcy.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custLng")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custLng, filter);
					this.custLng.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custParentCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custParentCountry, filter);
					this.custParentCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custResdCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custResdCountry, filter);
					this.custResdCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custRiskCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custRiskCountry, filter);
					this.custRiskCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custNationality")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custNationality, filter);
					this.custNationality.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custClosedOn")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custClosedOn, filter);
					this.custClosedOn.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custStmtFrq")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custStmtFrq, filter);
					this.custStmtFrq.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custStmtFrqDay")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custStmtFrqDay, filter);
					this.custStmtFrqDay.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custIsStmtCombined")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custIsStmtCombined, filter);
					this.custIsStmtCombined.setValue(filter.getValue().toString());
				}*/ else if (filter.getProperty().equals("recordStatus")) {
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
		showCustomerSeekDialog();
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
		this.window_CustomerSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_CustomerSearch.doModal();
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

		final JdbcSearchObject<Customer> so = new JdbcSearchObject<Customer>(Customer.class);
		so.addTabelName("Customers_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.custID.getValue())) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_custID.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custID", "%" + this.custID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custID", this.custID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custCIF.getValue())) {

			// get the search operator
			final Listitem item_CustCIF = this.sortOperator_custCIF.getSelectedItem();

			if (item_CustCIF != null) {
				final int searchOpId = ((SearchOperators) item_CustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCIF", "%" + this.custCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCIF", this.custCIF.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custCoreBank.getValue())) {

			// get the search operator
			final Listitem item_CustCoreBank = this.sortOperator_custCoreBank.getSelectedItem();

			if (item_CustCoreBank != null) {
				final int searchOpId = ((SearchOperators) item_CustCoreBank.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCoreBank", "%" + this.custCoreBank.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCoreBank", this.custCoreBank.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custCtgCode.getValue())) {

			// get the search operator
			final Listitem item_CustCtgCode = this.sortOperator_custCtgCode.getSelectedItem();

			if (item_CustCtgCode != null) {
				final int searchOpId = ((SearchOperators) item_CustCtgCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCtgCode", "%" + this.custCtgCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCtgCode", this.custCtgCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custTypeCode.getValue())) {

			// get the search operator
			final Listitem item_CustTypeCode = this.sortOperator_custTypeCode.getSelectedItem();

			if (item_CustTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_CustTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custTypeCode", "%" + this.custTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custTypeCode", this.custTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSalutationCode.getValue())) {

			// get the search operator
			final Listitem item_CustSalutationCode = this.sortOperator_custSalutationCode.getSelectedItem();

			if (item_CustSalutationCode != null) {
				final int searchOpId = ((SearchOperators) item_CustSalutationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSalutationCode", "%" + this.custSalutationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSalutationCode", this.custSalutationCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custFName.getValue())) {

			// get the search operator
			final Listitem item_CustFName = this.sortOperator_custFName.getSelectedItem();

			if (item_CustFName != null) {
				final int searchOpId = ((SearchOperators) item_CustFName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custFName", "%" + this.custFName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custFName", this.custFName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custMName.getValue())) {

			// get the search operator
			final Listitem item_CustMName = this.sortOperator_custMName.getSelectedItem();

			if (item_CustMName != null) {
				final int searchOpId = ((SearchOperators) item_CustMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custMName", "%" + this.custMName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custMName", this.custMName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custLName.getValue())) {

			// get the search operator
			final Listitem item_CustLName = this.sortOperator_custLName.getSelectedItem();

			if (item_CustLName != null) {
				final int searchOpId = ((SearchOperators) item_CustLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custLName", "%" + this.custLName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custLName", this.custLName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custShrtName.getValue())) {

			// get the search operator
			final Listitem item_CustShrtName = this.sortOperator_custShrtName.getSelectedItem();

			if (item_CustShrtName != null) {
				final int searchOpId = ((SearchOperators) item_CustShrtName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custShrtName", "%" + this.custShrtName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custShrtName", this.custShrtName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custDftBranch.getValue())) {

			// get the search operator
			final Listitem item_CustDftBranch = this.sortOperator_custDftBranch.getSelectedItem();

			if (item_CustDftBranch != null) {
				final int searchOpId = ((SearchOperators) item_CustDftBranch.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custDftBranch", "%" + this.custDftBranch.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custDftBranch", this.custDftBranch.getValue(), searchOpId));
				}
			}
		}
		/*if (StringUtils.isNotEmpty(this.custRO1.getValue())) {

			// get the search operator
			final Listitem item_CustRO1 = this.sortOperator_custRO1.getSelectedItem();

			if (item_CustRO1 != null) {
				final int searchOpId = ((SearchOperators) item_CustRO1.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custRO1", "%" + this.custRO1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custRO1", this.custRO1.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSts.getValue())) {

			// get the search operator
			final Listitem item_CustSts = this.sortOperator_custSts.getSelectedItem();

			if (item_CustSts != null) {
				final int searchOpId = ((SearchOperators) item_CustSts.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSts", "%" + this.custSts.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSts", this.custSts.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsBlocked = this.sortOperator_custIsBlocked.getSelectedItem();

		if (item_CustIsBlocked != null) {
			final int searchOpId = ((SearchOperators) item_CustIsBlocked.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsBlocked.isChecked()){
					so.addFilter(new Filter("custIsBlocked",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsBlocked",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsActive = this.sortOperator_custIsActive.getSelectedItem();

		if (item_CustIsActive != null) {
			final int searchOpId = ((SearchOperators) item_CustIsActive.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsActive.isChecked()){
					so.addFilter(new Filter("custIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsActive",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsClosed = this.sortOperator_custIsClosed.getSelectedItem();

		if (item_CustIsClosed != null) {
			final int searchOpId = ((SearchOperators) item_CustIsClosed.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsClosed.isChecked()){
					so.addFilter(new Filter("custIsClosed",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsClosed",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custInactiveReason.getValue())) {

			// get the search operator
			final Listitem item_CustInactiveReason = this.sortOperator_custInactiveReason.getSelectedItem();

			if (item_CustInactiveReason != null) {
				final int searchOpId = ((SearchOperators) item_CustInactiveReason.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custInactiveReason", "%" + this.custInactiveReason.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custInactiveReason", this.custInactiveReason.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsTradeFinCust = this.sortOperator_custIsTradeFinCust.getSelectedItem();

		if (item_CustIsTradeFinCust != null) {
			final int searchOpId = ((SearchOperators) item_CustIsTradeFinCust.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsTradeFinCust.isChecked()){
					so.addFilter(new Filter("custIsTradeFinCust",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsTradeFinCust",0, searchOpId));	
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsStaff = this.sortOperator_custIsStaff.getSelectedItem();

		if (item_CustIsStaff != null) {
			final int searchOpId = ((SearchOperators) item_CustIsStaff.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsStaff.isChecked()){
					so.addFilter(new Filter("custIsStaff",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsStaff",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custStaffID.getValue())) {

			// get the search operator
			final Listitem item_CustStaffID = this.sortOperator_custStaffID.getSelectedItem();

			if (item_CustStaffID != null) {
				final int searchOpId = ((SearchOperators) item_CustStaffID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custStaffID", "%" + this.custStaffID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custStaffID", this.custStaffID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custIndustry.getValue())) {

			// get the search operator
			final Listitem item_CustIndustry = this.sortOperator_custIndustry.getSelectedItem();

			if (item_CustIndustry != null) {
				final int searchOpId = ((SearchOperators) item_CustIndustry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custIndustry", "%" + this.custIndustry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custIndustry", this.custIndustry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSector.getValue())) {

			// get the search operator
			final Listitem item_CustSector = this.sortOperator_custSector.getSelectedItem();

			if (item_CustSector != null) {
				final int searchOpId = ((SearchOperators) item_CustSector.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSector", "%" + this.custSector.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSector", this.custSector.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSubSector.getValue())) {

			// get the search operator
			final Listitem item_CustSubSector = this.sortOperator_custSubSector.getSelectedItem();

			if (item_CustSubSector != null) {
				final int searchOpId = ((SearchOperators) item_CustSubSector.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSubSector", "%" + this.custSubSector.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSubSector", this.custSubSector.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custProfession.getValue())) {

			// get the search operator
			final Listitem item_CustProfession = this.sortOperator_custProfession.getSelectedItem();

			if (item_CustProfession != null) {
				final int searchOpId = ((SearchOperators) item_CustProfession.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custProfession", "%" + this.custProfession.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custProfession", this.custProfession.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custTotalIncome.getValue())) {

			// get the search operator
			final Listitem item_CustTotalIncome = this.sortOperator_custTotalIncome.getSelectedItem();

			if (item_CustTotalIncome != null) {
				final int searchOpId = ((SearchOperators) item_CustTotalIncome.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custTotalIncome", "%" + this.custTotalIncome.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custTotalIncome", this.custTotalIncome.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custMaritalSts.getValue())) {

			// get the search operator
			final Listitem item_CustMaritalSts = this.sortOperator_custMaritalSts.getSelectedItem();

			if (item_CustMaritalSts != null) {
				final int searchOpId = ((SearchOperators) item_CustMaritalSts.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custMaritalSts", "%" + this.custMaritalSts.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custMaritalSts", this.custMaritalSts.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custEmpSts.getValue())) {

			// get the search operator
			final Listitem item_CustEmpSts = this.sortOperator_custEmpSts.getSelectedItem();

			if (item_CustEmpSts != null) {
				final int searchOpId = ((SearchOperators) item_CustEmpSts.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custEmpSts", "%" + this.custEmpSts.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custEmpSts", this.custEmpSts.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSegment.getValue())) {

			// get the search operator
			final Listitem item_CustSegment = this.sortOperator_custSegment.getSelectedItem();

			if (item_CustSegment != null) {
				final int searchOpId = ((SearchOperators) item_CustSegment.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSegment", "%" + this.custSegment.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSegment", this.custSegment.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSubSegment.getValue())) {

			// get the search operator
			final Listitem item_CustSubSegment = this.sortOperator_custSubSegment.getSelectedItem();

			if (item_CustSubSegment != null) {
				final int searchOpId = ((SearchOperators) item_CustSubSegment.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSubSegment", "%" + this.custSubSegment.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSubSegment", this.custSubSegment.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsBlackListed = this.sortOperator_custIsBlackListed.getSelectedItem();

		if (item_CustIsBlackListed != null) {
			final int searchOpId = ((SearchOperators) item_CustIsBlackListed.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsBlackListed.isChecked()){
					so.addFilter(new Filter("custIsBlackListed",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsBlackListed",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custBLRsnCode.getValue())) {

			// get the search operator
			final Listitem item_CustBLRsnCode = this.sortOperator_custBLRsnCode.getSelectedItem();

			if (item_CustBLRsnCode != null) {
				final int searchOpId = ((SearchOperators) item_CustBLRsnCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custBLRsnCode", "%" + this.custBLRsnCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custBLRsnCode", this.custBLRsnCode.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsRejected = this.sortOperator_custIsRejected.getSelectedItem();

		if (item_CustIsRejected != null) {
			final int searchOpId = ((SearchOperators) item_CustIsRejected.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsRejected.isChecked()){
					so.addFilter(new Filter("custIsRejected",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsRejected",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custRejectedRsn.getValue())) {

			// get the search operator
			final Listitem item_CustRejectedRsn = this.sortOperator_custRejectedRsn.getSelectedItem();

			if (item_CustRejectedRsn != null) {
				final int searchOpId = ((SearchOperators) item_CustRejectedRsn.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custRejectedRsn", "%" + this.custRejectedRsn.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custRejectedRsn", this.custRejectedRsn.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custBaseCcy.getValue())) {

			// get the search operator
			final Listitem item_CustBaseCcy = this.sortOperator_custBaseCcy.getSelectedItem();

			if (item_CustBaseCcy != null) {
				final int searchOpId = ((SearchOperators) item_CustBaseCcy.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custBaseCcy", "%" + this.custBaseCcy.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custBaseCcy", this.custBaseCcy.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custLng.getValue())) {

			// get the search operator
			final Listitem item_CustLng = this.sortOperator_custLng.getSelectedItem();

			if (item_CustLng != null) {
				final int searchOpId = ((SearchOperators) item_CustLng.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custLng", "%" + this.custLng.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custLng", this.custLng.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custParentCountry.getValue())) {

			// get the search operator
			final Listitem item_CustParentCountry = this.sortOperator_custParentCountry.getSelectedItem();

			if (item_CustParentCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustParentCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custParentCountry", "%" + this.custParentCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custParentCountry", this.custParentCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custResdCountry.getValue())) {

			// get the search operator
			final Listitem item_CustResdCountry = this.sortOperator_custResdCountry.getSelectedItem();

			if (item_CustResdCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustResdCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custResdCountry", "%" + this.custResdCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custResdCountry", this.custResdCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custRiskCountry.getValue())) {

			// get the search operator
			final Listitem item_CustRiskCountry = this.sortOperator_custRiskCountry.getSelectedItem();

			if (item_CustRiskCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustRiskCountry.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custRiskCountry", "%" + this.custRiskCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custRiskCountry", this.custRiskCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custNationality.getValue())) {

			// get the search operator
			final Listitem item_CustNationality = this.sortOperator_custNationality.getSelectedItem();

			if (item_CustNationality != null) {
				final int searchOpId = ((SearchOperators) item_CustNationality.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custNationality", "%" + this.custNationality.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custNationality", this.custNationality.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custClosedOn.getValue())) {

			// get the search operator
			final Listitem item_CustClosedOn = this.sortOperator_custClosedOn.getSelectedItem();

			if (item_CustClosedOn != null) {
				final int searchOpId = ((SearchOperators) item_CustClosedOn.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custClosedOn", "%" + this.custClosedOn.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custClosedOn", this.custClosedOn.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custStmtFrq.getValue())) {

			// get the search operator
			final Listitem item_CustStmtFrq = this.sortOperator_custStmtFrq.getSelectedItem();

			if (item_CustStmtFrq != null) {
				final int searchOpId = ((SearchOperators) item_CustStmtFrq.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custStmtFrq", "%" + this.custStmtFrq.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custStmtFrq", this.custStmtFrq.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custStmtFrqDay.getValue())) {

			// get the search operator
			final Listitem item_CustStmtFrqDay = this.sortOperator_custStmtFrqDay.getSelectedItem();

			if (item_CustStmtFrqDay != null) {
				final int searchOpId = ((SearchOperators) item_CustStmtFrqDay.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custStmtFrqDay", "%" + this.custStmtFrqDay.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custStmtFrqDay", this.custStmtFrqDay.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_CustIsStmtCombined = this.sortOperator_custIsStmtCombined.getSelectedItem();

		if (item_CustIsStmtCombined != null) {
			final int searchOpId = ((SearchOperators) item_CustIsStmtCombined.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.custIsStmtCombined.isChecked()){
					so.addFilter(new Filter("custIsStmtCombined",1, searchOpId));
				}else{
					so.addFilter(new Filter("custIsStmtCombined",0, searchOpId));	
				}
			}
		}*/
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
		// Defualt Sort on the table
		so.addSort("CustID", false);

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
		this.customerCtrl.setSearchObj(so);

		final Listbox listBox = this.customerCtrl.listBoxCustomer;
		final Paging paging = this.customerCtrl.pagingCustomerList;

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<Customer>) listBox.getModel()).init(so, listBox, paging);
		this.customerCtrl.setSearchObj(so);

		this.label_CustomerSearchResult.setValue(Labels.getLabel("label_CustomerSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return this.customerService;
	}
}