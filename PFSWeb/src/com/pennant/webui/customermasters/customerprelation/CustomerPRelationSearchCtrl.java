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
 * FileName    		:  CustomerPRelationSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerprelation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerPRelation;
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
 * /WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationSearchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerPRelationSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7680716861548778724L;
	private final static Logger logger = Logger.getLogger(CustomerPRelationSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CustomerPRelationSearch; 	// autowired
	
	protected Textbox pRCustCIF; 						// autowired
	protected Listbox sortOperator_pRCustCIF; 			// autowired
	protected Intbox  pRCustPRSNo; 						// autowired
	protected Listbox sortOperator_pRCustPRSNo; 		// autowired
	protected Textbox pRRelationCode; 					// autowired
	protected Listbox sortOperator_pRRelationCode; 		// autowired
	protected Textbox pRRelationCustID; 				// autowired
	protected Listbox sortOperator_pRRelationCustID; 	// autowired
	protected Checkbox pRisGuardian; 					// autowired
	protected Listbox sortOperator_pRisGuardian; 		// autowired
	protected Textbox pRFName; 							// autowired
	protected Listbox sortOperator_pRFName; 			// autowired
	protected Textbox pRMName; 							// autowired
	protected Listbox sortOperator_pRMName; 			// autowired
	protected Textbox pRLName; 							// autowired
	protected Listbox sortOperator_pRLName; 			// autowired
	protected Textbox pRSName; 							// autowired
	protected Listbox sortOperator_pRSName; 			// autowired
	protected Textbox pRFNameLclLng; 					// autowired
	protected Listbox sortOperator_pRFNameLclLng; 		// autowired
	protected Textbox pRMNameLclLng; 					// autowired
	protected Listbox sortOperator_pRMNameLclLng; 		// autowired
	protected Textbox pRLNameLclLng; 					// autowired
	protected Listbox sortOperator_pRLNameLclLng; 		// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_CustomerPRelationSearch_RecordStatus; // autowired
	protected Label label_CustomerPRelationSearch_RecordType; 	// autowired
	protected Label label_CustomerPRelationSearchResult;	 	// autowired

	// not auto wired vars
	private transient CustomerPRelationListCtrl customerPRelationCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CustomerPRelation");
	
	/**
	 * constructor
	 */
	public CustomerPRelationSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPRelation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPRelationSearch(Event event) throws Exception {
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

		if (args.containsKey("customerPRelationCtrl")) {
			this.customerPRelationCtrl = (CustomerPRelationListCtrl) args.get("customerPRelationCtrl");
		} else {
			this.customerPRelationCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_pRCustCIF.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRCustCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRCustPRSNo.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_pRCustPRSNo.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRRelationCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRRelationCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRRelationCustID.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRRelationCustID.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRisGuardian.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getBooleanOperators()));
		this.sortOperator_pRisGuardian.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRFName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRFName.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRMName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRMName.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRLName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRLName.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRSName.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRSName.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRFNameLclLng.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRFNameLclLng.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRMNameLclLng.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRMNameLclLng.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_pRLNameLclLng.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_pRLNameLclLng.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(
					new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerPRelationSearch_RecordStatus.setVisible(false);
			this.label_CustomerPRelationSearch_RecordType.setVisible(false);
		}
		
		//Set Field Properties
		this.pRCustPRSNo.setText("");
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<CustomerPRelation> searchObj = (JdbcSearchObject<CustomerPRelation>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("pRCustCIF")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRCustCIF, filter);
					this.pRCustCIF.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRCustPRSNo")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_pRCustPRSNo, filter);
					this.pRCustPRSNo.setValue(Integer.parseInt(filter.getValue().toString()));
			    } else if (filter.getProperty().equals("pRRelationCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRRelationCode, filter);
					this.pRRelationCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRRelationCustID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRRelationCustID, filter);
					this.pRRelationCustID.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRisGuardian")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRisGuardian, filter);
					this.pRisGuardian.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRFName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRFName, filter);
					this.pRFName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRMName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRMName, filter);
					this.pRMName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRLName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRLName, filter);
					this.pRLName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRSName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRSName, filter);
					this.pRSName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRFNameLclLng")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRFNameLclLng, filter);
					this.pRFNameLclLng.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRMNameLclLng")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRMNameLclLng, filter);
					this.pRMNameLclLng.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("pRLNameLclLng")) {
					SearchOperators.restoreStringOperator(this.sortOperator_pRLNameLclLng, filter);
					this.pRLNameLclLng.setValue(filter.getValue().toString());
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
		showCustomerPRelationSeekDialog();
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
		this.window_CustomerPRelationSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerPRelationSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_CustomerPRelationSearch.doModal();
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
		final JdbcSearchObject<CustomerPRelation> so = new JdbcSearchObject<CustomerPRelation>(CustomerPRelation.class);
		so.addTabelName("CustomersPRelations_View");
		so.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		if (StringUtils.isNotEmpty(this.pRCustCIF.getValue())) {

			// get the search operator
			final Listitem itemPRCustCIF = this.sortOperator_pRCustCIF.getSelectedItem();
			if (itemPRCustCIF != null) {
				final int searchOpId = ((SearchOperators) itemPRCustCIF.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lovDescCustCIF", "%" + this.pRCustCIF.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lovDescCustCIF", this.pRCustCIF.getValue(), searchOpId));
				}
			}
		}
		if (this.pRCustPRSNo.getValue() != null) {

			// get the search operator
			final Listitem itemPRCustPRSNo = this.sortOperator_pRCustPRSNo.getSelectedItem();
			if (itemPRCustPRSNo != null) {
				final int searchOpId = ((SearchOperators) itemPRCustPRSNo.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRCustPRSNo", this.pRCustPRSNo.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRRelationCode.getValue())) {

			// get the search operator
			final Listitem itemPRRelationCode = this.sortOperator_pRRelationCode.getSelectedItem();
			if (itemPRRelationCode != null) {
				final int searchOpId = ((SearchOperators) itemPRRelationCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRRelationCode", "%" + this.pRRelationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
	   				so.addFilter(new Filter("pRRelationCode", this.pRRelationCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRRelationCustID.getValue())) {

			// get the search operator
			final Listitem itemPRRelationCustID = this.sortOperator_pRRelationCustID.getSelectedItem();
			if (itemPRRelationCustID != null) {
				final int searchOpId = ((SearchOperators) itemPRRelationCustID.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRRelationCustID", "%" + this.pRRelationCustID.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRRelationCustID", this.pRRelationCustID.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemPRisGuardian = this.sortOperator_pRisGuardian.getSelectedItem();
		if (itemPRisGuardian != null) {
			final int searchOpId = ((SearchOperators) itemPRisGuardian.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.pRisGuardian.isChecked()){
					so.addFilter(new Filter("pRisGuardian",1, searchOpId));
				}else{
					so.addFilter(new Filter("pRisGuardian",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRFName.getValue())) {

			// get the search operator
			final Listitem itemPRFName = this.sortOperator_pRFName.getSelectedItem();
			if (itemPRFName != null) {
				final int searchOpId = ((SearchOperators) itemPRFName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRFName", "%" + this.pRFName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRFName", this.pRFName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRMName.getValue())) {

			// get the search operator
			final Listitem itemPRMName = this.sortOperator_pRMName.getSelectedItem();
			if (itemPRMName != null) {
				final int searchOpId = ((SearchOperators) itemPRMName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRMName", "%" + this.pRMName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRMName", this.pRMName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRLName.getValue())) {

			// get the search operator
			final Listitem itemPRLName = this.sortOperator_pRLName.getSelectedItem();
			if (itemPRLName != null) {
				final int searchOpId = ((SearchOperators) itemPRLName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRLName", "%" + this.pRLName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRLName", this.pRLName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRSName.getValue())) {

			// get the search operator
			final Listitem itemPRSName = this.sortOperator_pRSName.getSelectedItem();
			if (itemPRSName != null) {
				final int searchOpId = ((SearchOperators) itemPRSName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRSName", "%" + this.pRSName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRSName", this.pRSName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRFNameLclLng.getValue())) {
			
			// get the search operator
			final Listitem itemPRFNameLclLng = this.sortOperator_pRFNameLclLng.getSelectedItem();
			if (itemPRFNameLclLng != null) {
				final int searchOpId = ((SearchOperators) itemPRFNameLclLng.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRFNameLclLng", "%" + this.pRFNameLclLng.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRFNameLclLng", this.pRFNameLclLng.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRMNameLclLng.getValue())) {

			// get the search operator
			final Listitem itemPRMNameLclLng = this.sortOperator_pRMNameLclLng.getSelectedItem();
			if (itemPRMNameLclLng != null) {
				final int searchOpId = ((SearchOperators) itemPRMNameLclLng.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRMNameLclLng", "%" + this.pRMNameLclLng.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRMNameLclLng", this.pRMNameLclLng.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.pRLNameLclLng.getValue())) {

			// get the search operator
			final Listitem itemPRLNameLclLng = this.sortOperator_pRLNameLclLng.getSelectedItem();
			if (itemPRLNameLclLng != null) {
				final int searchOpId = ((SearchOperators) itemPRLNameLclLng.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("pRLNameLclLng", "%" + this.pRLNameLclLng.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("pRLNameLclLng", this.pRLNameLclLng.getValue(), searchOpId));
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
		this.customerPRelationCtrl.setSearchObj(so);

		// set the model to the listBox with the initial resultSet get by the DAO method.
		 final Listbox listBox = this.customerPRelationCtrl.listBoxCustomerPRelation;
			final Paging paging = this.customerPRelationCtrl.pagingCustomerPRelationList;
			
			((PagedListWrapper<CustomerPRelation>) listBox.getModel()).init(so, listBox, paging);
			
		this.label_CustomerPRelationSearchResult.setValue(Labels.getLabel("label_CustomerPRelationSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

}