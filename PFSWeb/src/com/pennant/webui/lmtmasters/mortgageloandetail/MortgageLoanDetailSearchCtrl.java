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
 * FileName    		:  MortgageLoanDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.mortgageloandetail;

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
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class MortgageLoanDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2002056626038365302L;
	private final static Logger logger = Logger.getLogger(MortgageLoanDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_MortgageLoanDetailSearch; 		// autowired
	
	protected Textbox mortgLoanId; 							// autowired
	protected Listbox sortOperator_mortgLoanId; 			// autowired
	protected Textbox loanRefNumber; 						// autowired
	protected Listbox sortOperator_loanRefNumber; 			// autowired
	protected Checkbox loanRefType; 						// autowired
	protected Listbox sortOperator_loanRefType; 			// autowired
	protected Textbox mortgProperty; 						// autowired
	protected Listbox sortOperator_mortgProperty; 			// autowired
	protected Textbox mortgCurrentValue; 					// autowired
	protected Listbox sortOperator_mortgCurrentValue; 		// autowired
	protected Textbox mortgPurposeOfLoan; 					// autowired
	protected Listbox sortOperator_mortgPurposeOfLoan; 		// autowired
	protected Textbox mortgPropertyRelation; 				// autowired
	protected Listbox sortOperator_mortgPropertyRelation; 	// autowired
	protected Textbox mortgOwnership; 						// autowired
	protected Listbox sortOperator_mortgOwnership; 			// autowired
	protected Textbox mortgAddrHNbr; 						// autowired
	protected Listbox sortOperator_mortgAddrHNbr; 			// autowired
	protected Textbox mortgAddrFlatNbr; 					// autowired
	protected Listbox sortOperator_mortgAddrFlatNbr; 		// autowired
	protected Textbox mortgAddrStreet; 						// autowired
	protected Listbox sortOperator_mortgAddrStreet; 		// autowired
	protected Textbox mortgAddrLane1; 						// autowired
	protected Listbox sortOperator_mortgAddrLane1; 			// autowired
	protected Textbox mortgAddrLane2; 						// autowired
	protected Listbox sortOperator_mortgAddrLane2;	 		// autowired
	protected Textbox mortgAddrPOBox; 						// autowired
	protected Listbox sortOperator_mortgAddrPOBox; 			// autowired
	protected Textbox mortgAddrCountry; 					// autowired
	protected Listbox sortOperator_mortgAddrCountry; 		// autowired
	protected Textbox mortgAddrProvince; 					// autowired
	protected Listbox sortOperator_mortgAddrProvince; 		// autowired
	protected Textbox mortgAddrCity; 						// autowired
	protected Listbox sortOperator_mortgAddrCity; 			// autowired
	protected Textbox mortgAddrZIP; 						// autowired
	protected Listbox sortOperator_mortgAddrZIP; 			// autowired
	protected Textbox mortgAddrPhone; 						// autowired
	protected Listbox sortOperator_mortgAddrPhone; 			// autowired
	protected Textbox recordStatus; 						// autowired
	protected Listbox recordType;							// autowired
	protected Listbox sortOperator_recordStatus; 			// autowired
	protected Listbox sortOperator_recordType; 				// autowired
	
	protected Label label_MortgageLoanDetailSearch_RecordStatus; 	// autowired
	protected Label label_MortgageLoanDetailSearch_RecordType; 		// autowired
	protected Label label_MortgageLoanDetailSearchResult; 			// autowired

	// not auto wired vars
	private transient MortgageLoanDetailListCtrl mortgageLoanDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("MortgageLoanDetail");
	
	/**
	 * constructor
	 */
	public MortgageLoanDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected MortgageLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MortgageLoanDetailSearch(Event event) throws Exception {
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

		if (args.containsKey("mortgageLoanDetailCtrl")) {
			this.mortgageLoanDetailCtrl = (MortgageLoanDetailListCtrl) args.get("mortgageLoanDetailCtrl");
		} else {
			this.mortgageLoanDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_mortgLoanId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgLoanId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_loanRefNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_loanRefNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_loanRefType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_loanRefType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgProperty.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgProperty.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgCurrentValue.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgCurrentValue.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgPurposeOfLoan.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgPurposeOfLoan.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgPropertyRelation.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgPropertyRelation.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgOwnership.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgOwnership.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrHNbr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrHNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrFlatNbr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrStreet.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrStreet.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrLane1.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrLane1.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrLane2.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrLane2.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrPOBox.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrCountry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrProvince.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrCity.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrCity.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrZIP.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrZIP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mortgAddrPhone.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mortgAddrPhone.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_MortgageLoanDetailSearch_RecordStatus.setVisible(false);
			this.label_MortgageLoanDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<MortgageLoanDetail> searchObj = (JdbcSearchObject<MortgageLoanDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("mortgLoanId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgLoanId, filter);
					this.mortgLoanId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("loanRefNumber")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefNumber, filter);
					this.loanRefNumber.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("loanRefType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_loanRefType, filter);
					this.loanRefType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgProperty")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgProperty, filter);
					this.mortgProperty.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgCurrentValue")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgCurrentValue, filter);
					this.mortgCurrentValue.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgPurposeOfLoan")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgPurposeOfLoan, filter);
					this.mortgPurposeOfLoan.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgPropertyRelation")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgPropertyRelation, filter);
					this.mortgPropertyRelation.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgOwnership")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgOwnership, filter);
					this.mortgOwnership.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrHNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrHNbr, filter);
					this.mortgAddrHNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrFlatNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrFlatNbr, filter);
					this.mortgAddrFlatNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrStreet")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrStreet, filter);
					this.mortgAddrStreet.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrLane1")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrLane1, filter);
					this.mortgAddrLane1.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrLane2")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrLane2, filter);
					this.mortgAddrLane2.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrPOBox")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrPOBox, filter);
					this.mortgAddrPOBox.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrCountry, filter);
					this.mortgAddrCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrProvince")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrProvince, filter);
					this.mortgAddrProvince.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrCity")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrCity, filter);
					this.mortgAddrCity.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrZIP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrZIP, filter);
					this.mortgAddrZIP.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mortgAddrPhone")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mortgAddrPhone, filter);
					this.mortgAddrPhone.setValue(filter.getValue().toString());
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
		showMortgageLoanDetailSeekDialog();
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
		this.window_MortgageLoanDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showMortgageLoanDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_MortgageLoanDetailSearch.doModal();
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
		
		final JdbcSearchObject<MortgageLoanDetail> so = new JdbcSearchObject<MortgageLoanDetail>(MortgageLoanDetail.class);
		
		if (isWorkFlowEnabled()){
			so.addTabelName("LMTMortgageLoanDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("LMTMortgageLoanDetail_AView");
		}		
		
		if (StringUtils.isNotEmpty(this.mortgLoanId.getValue())) {

			// get the search operator
			final Listitem itemMortgLoanId = this.sortOperator_mortgLoanId.getSelectedItem();
			if (itemMortgLoanId != null) {
				final int searchOpId = ((SearchOperators) itemMortgLoanId.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgLoanId", "%" + 
							this.mortgLoanId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgLoanId", this.mortgLoanId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.loanRefNumber.getValue())) {

			// get the search operator
			final Listitem itemLoanRefNumber = this.sortOperator_loanRefNumber.getSelectedItem();
			if (itemLoanRefNumber != null) {
				final int searchOpId = ((SearchOperators) itemLoanRefNumber.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("loanRefNumber", "%" +
							this.loanRefNumber.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("loanRefNumber", this.loanRefNumber.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem itemLoanRefType = this.sortOperator_loanRefType.getSelectedItem();
		if (itemLoanRefType != null) {
			final int searchOpId = ((SearchOperators) itemLoanRefType.getAttribute(
					"data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.loanRefType.isChecked()){
					so.addFilter(new Filter("loanRefType",1, searchOpId));
				}else{
					so.addFilter(new Filter("loanRefType",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgProperty.getValue())) {

			// get the search operator
			final Listitem itemMortgProperty = this.sortOperator_mortgProperty.getSelectedItem();
			if (itemMortgProperty != null) {
				final int searchOpId = ((SearchOperators) itemMortgProperty.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgProperty", "%" + 
							this.mortgProperty.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgProperty", this.mortgProperty.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgCurrentValue.getValue())) {

			// get the search operator
			final Listitem itemMortgCurrentValue = this.sortOperator_mortgCurrentValue.getSelectedItem();
			if (itemMortgCurrentValue != null) {
				final int searchOpId = ((SearchOperators) itemMortgCurrentValue.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgCurrentValue", "%" + 
							this.mortgCurrentValue.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgCurrentValue", 
							this.mortgCurrentValue.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgPurposeOfLoan.getValue())) {

			// get the search operator
			final Listitem itemMortgPurposeOfLoan = this.sortOperator_mortgPurposeOfLoan.getSelectedItem();
			if (itemMortgPurposeOfLoan != null) {
				final int searchOpId = ((SearchOperators) itemMortgPurposeOfLoan.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgPurposeOfLoan", "%" + 
							this.mortgPurposeOfLoan.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgPurposeOfLoan", 
							this.mortgPurposeOfLoan.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgPropertyRelation.getValue())) {

			// get the search operator
			final Listitem itemMortgPropertyRelation = this.sortOperator_mortgPropertyRelation.getSelectedItem();
			if (itemMortgPropertyRelation != null) {
				final int searchOpId = ((SearchOperators) itemMortgPropertyRelation.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgPropertyRelation", "%" +
							this.mortgPropertyRelation.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgPropertyRelation",
							this.mortgPropertyRelation.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgOwnership.getValue())) {

			// get the search operator
			final Listitem itemMortgOwnership = this.sortOperator_mortgOwnership.getSelectedItem();
			if (itemMortgOwnership != null) {
				final int searchOpId = ((SearchOperators) itemMortgOwnership.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgOwnership", "%" + 
							this.mortgOwnership.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgOwnership", this.mortgOwnership.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrHNbr.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrHNbr = this.sortOperator_mortgAddrHNbr.getSelectedItem();
			if (itemMortgAddrHNbr != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrHNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrHNbr", "%" + 
							this.mortgAddrHNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrHNbr", this.mortgAddrHNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrFlatNbr.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrFlatNbr = this.sortOperator_mortgAddrFlatNbr.getSelectedItem();
			if (itemMortgAddrFlatNbr != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrFlatNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrFlatNbr", "%" + 
							this.mortgAddrFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrFlatNbr",
							this.mortgAddrFlatNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrStreet.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrStreet = this.sortOperator_mortgAddrStreet.getSelectedItem();
			if (itemMortgAddrStreet != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrStreet.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrStreet", "%" +
							this.mortgAddrStreet.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrStreet", this.mortgAddrStreet.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrLane1.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrLane1 = this.sortOperator_mortgAddrLane1.getSelectedItem();
			if (itemMortgAddrLane1 != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrLane1.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrLane1", "%" + 
							this.mortgAddrLane1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrLane1", this.mortgAddrLane1.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrLane2.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrLane2 = this.sortOperator_mortgAddrLane2.getSelectedItem();
			if (itemMortgAddrLane2 != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrLane2.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrLane2", "%" +
							this.mortgAddrLane2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrLane2", this.mortgAddrLane2.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrPOBox.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrPOBox = this.sortOperator_mortgAddrPOBox.getSelectedItem();
			if (itemMortgAddrPOBox != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrPOBox.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrPOBox", "%" +
							this.mortgAddrPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrPOBox", this.mortgAddrPOBox.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrCountry.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrCountry = this.sortOperator_mortgAddrCountry.getSelectedItem();
			if (itemMortgAddrCountry != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrCountry.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrCountry", "%" +
							this.mortgAddrCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrCountry", this.mortgAddrCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrProvince.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrProvince = this.sortOperator_mortgAddrProvince.getSelectedItem();
			if (itemMortgAddrProvince != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrProvince.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrProvince", "%" + 
							this.mortgAddrProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrProvince", 
							this.mortgAddrProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrCity.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrCity = this.sortOperator_mortgAddrCity.getSelectedItem();
			if (itemMortgAddrCity != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrCity.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrCity", "%" +
							this.mortgAddrCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrCity", this.mortgAddrCity.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrZIP.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrZIP = this.sortOperator_mortgAddrZIP.getSelectedItem();
			if (itemMortgAddrZIP != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrZIP.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrZIP", "%" + 
							this.mortgAddrZIP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrZIP", this.mortgAddrZIP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mortgAddrPhone.getValue())) {

			// get the search operator
			final Listitem itemMortgAddrPhone = this.sortOperator_mortgAddrPhone.getSelectedItem();
			if (itemMortgAddrPhone != null) {
				final int searchOpId = ((SearchOperators) itemMortgAddrPhone.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mortgAddrPhone", "%" + 
							this.mortgAddrPhone.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mortgAddrPhone", this.mortgAddrPhone.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus.getAttribute(
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
			final Listitem itemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (itemRecordType!= null) {
				final int searchOpId = ((SearchOperators) itemRecordType.getAttribute(
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
		so.addSort("MortgLoanId", false);

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
		this.mortgageLoanDetailCtrl.setSearchObj(so);
		final Listbox listBox = this.mortgageLoanDetailCtrl.listBoxMortgageLoanDetail;
		final Paging paging = this.mortgageLoanDetailCtrl.pagingMortgageLoanDetailList;

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<MortgageLoanDetail>) listBox.getModel()).init(so, listBox, paging);
		this.label_MortgageLoanDetailSearchResult.setValue(Labels.getLabel("label_MortgageLoanDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}