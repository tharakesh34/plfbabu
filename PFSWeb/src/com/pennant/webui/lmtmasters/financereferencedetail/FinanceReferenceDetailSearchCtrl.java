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
 * FileName    		:  FinanceReferenceDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financereferencedetail;

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
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class FinanceReferenceDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceReferenceDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceReferenceDetailSearch; // auto wired
	
	protected Textbox finRefDetailId; // auto wired
	protected Listbox sortOperator_finRefDetailId; // auto wired
	protected Textbox finType; // auto wired
	protected Listbox sortOperator_finType; // auto wired
	protected Textbox finRefType; // auto wired
	protected Listbox sortOperator_finRefType; // auto wired
	protected Textbox finRefId; // auto wired
	protected Listbox sortOperator_finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Listbox sortOperator_isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Listbox sortOperator_showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Listbox sortOperator_mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired
	protected Listbox sortOperator_allowInputInStage; // auto wired
	protected Textbox recordStatus; // auto wired
	protected Listbox recordType;	// auto wired
	protected Listbox sortOperator_recordStatus; // auto wired
	protected Listbox sortOperator_recordType; // auto wired
	
	protected Label label_FinanceReferenceDetailSearch_RecordStatus; // auto wired
	protected Label label_FinanceReferenceDetailSearch_RecordType; // auto wired
	protected Label label_FinanceReferenceDetailSearchResult; // auto wired

	// not auto wired variables
	private transient FinanceReferenceDetailListCtrl financeReferenceDetailCtrl; // over handed per parameter
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceReferenceDetail");
	
	/**
	 * constructor
	 */
	public FinanceReferenceDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceReferenceDetailSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("financeReferenceDetailCtrl")) {
			this.financeReferenceDetailCtrl = (FinanceReferenceDetailListCtrl) args.get("financeReferenceDetailCtrl");
		} else {
			this.financeReferenceDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finRefDetailId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefDetailId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finRefType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finRefId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_isActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_showInStage.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_showInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mandInputInStage.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_mandInputInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_allowInputInStage.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_allowInputInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_FinanceReferenceDetailSearch_RecordStatus.setVisible(false);
			this.label_FinanceReferenceDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FinanceReferenceDetail> searchObj = (JdbcSearchObject<FinanceReferenceDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("finRefDetailId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finRefDetailId, filter);
					this.finRefDetailId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("finType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finType, filter);
					this.finType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("finRefType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finRefType, filter);
					this.finRefType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("finRefId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finRefId, filter);
					this.finRefId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("isActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_isActive, filter);
					this.isActive.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("showInStage")) {
					SearchOperators.restoreStringOperator(this.sortOperator_showInStage, filter);
					this.showInStage.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("mandInputInStage")) {
					SearchOperators.restoreStringOperator(this.sortOperator_mandInputInStage, filter);
					this.mandInputInStage.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("allowInputInStage")) {
					SearchOperators.restoreStringOperator(this.sortOperator_allowInputInStage, filter);
					this.allowInputInStage.setValue(filter.getValue().toString());
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
		showFinanceReferenceDetailSeekDialog();
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
		this.window_FinanceReferenceDetailSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFinanceReferenceDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_FinanceReferenceDetailSearch.doModal();
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

		final JdbcSearchObject<FinanceReferenceDetail> so = new JdbcSearchObject<FinanceReferenceDetail>(FinanceReferenceDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("LMTFinRefDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("LMTFinRefDetail_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finRefDetailId.getValue())) {

			// get the search operator
			final Listitem itemFinRefDetailId = this.sortOperator_finRefDetailId.getSelectedItem();
			if (itemFinRefDetailId != null) {
				final int searchOpId = ((SearchOperators) itemFinRefDetailId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finRefDetailId", "%" + this.finRefDetailId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finRefDetailId", this.finRefDetailId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finType.getValue())) {

			// get the search operator
			final Listitem itemFinType = this.sortOperator_finType.getSelectedItem();
			if (itemFinType != null) {
				final int searchOpId = ((SearchOperators) itemFinType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finType", "%" + this.finType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finType", this.finType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finRefType.getValue())) {

			// get the search operator
			final Listitem itemFinRefType = this.sortOperator_finRefType.getSelectedItem();
			if (itemFinRefType != null) {
				final int searchOpId = ((SearchOperators) itemFinRefType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finRefType", "%" + this.finRefType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finRefType", this.finRefType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finRefId.getValue())) {

			// get the search operator
			final Listitem itemFinRefId = this.sortOperator_finRefId.getSelectedItem();
			if (itemFinRefId != null) {
				final int searchOpId = ((SearchOperators) itemFinRefId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finRefId", "%" + this.finRefId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finRefId", this.finRefId.getValue(), searchOpId));
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
		if (StringUtils.isNotEmpty(this.showInStage.getValue())) {

			// get the search operator
			final Listitem itemShowInStage = this.sortOperator_showInStage.getSelectedItem();
			if (itemShowInStage != null) {
				final int searchOpId = ((SearchOperators) itemShowInStage.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("showInStage", "%" + this.showInStage.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("showInStage", this.showInStage.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.mandInputInStage.getValue())) {

			// get the search operator
			final Listitem itemMandInputInStage = this.sortOperator_mandInputInStage.getSelectedItem();
			if (itemMandInputInStage != null) {
				final int searchOpId = ((SearchOperators) itemMandInputInStage.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("mandInputInStage", "%" + this.mandInputInStage.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("mandInputInStage", this.mandInputInStage.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.allowInputInStage.getValue())) {

			// get the search operator
			final Listitem itemAllowInputInStage = this.sortOperator_allowInputInStage.getSelectedItem();
			if (itemAllowInputInStage != null) {
				final int searchOpId = ((SearchOperators) itemAllowInputInStage.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("allowInputInStage", "%" + this.allowInputInStage.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("allowInputInStage", this.allowInputInStage.getValue(), searchOpId));
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
		so.addSort("FinRefDetailId", false);

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
		//this.financeReferenceDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.financeReferenceDetailCtrl.listBoxFinanceReferenceDetail;
		final Paging paging = this.financeReferenceDetailCtrl.pagingFinanceReferenceDetailList;
		

		// set the model to the list box with the initial result set get by the DAO method.
		((PagedListWrapper<FinanceReferenceDetail>) listBox.getModel()).init(so, listBox, paging);	
		//this.financeReferenceDetailCtrl.setSearchObj(so);

		this.label_FinanceReferenceDetailSearchResult.setValue(Labels.getLabel("label_FinanceReferenceDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return this.financeReferenceDetailService;
	}
}