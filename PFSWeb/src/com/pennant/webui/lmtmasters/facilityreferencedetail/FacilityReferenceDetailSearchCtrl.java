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
 * FileName    		:  FacilityReferenceDetailSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.facilityreferencedetail;

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
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class FacilityReferenceDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FacilityReferenceDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FacilityReferenceDetailSearch; // auto wired
	
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
	
	protected Label label_FacilityReferenceDetailSearch_RecordStatus; // auto wired
	protected Label label_FacilityReferenceDetailSearch_RecordType; // auto wired
	protected Label label_FacilityReferenceDetailSearchResult; // auto wired

	// not auto wired variables
	private transient FacilityReferenceDetailListCtrl facilityReferenceDetailCtrl; // over handed per parameter
	private transient FacilityReferenceDetailService facilityReferenceDetailService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FacilityReferenceDetail");
	
	/**
	 * constructor
	 */
	public FacilityReferenceDetailSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FacilityReferenceDetailSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("facilityReferenceDetailCtrl")) {
			this.facilityReferenceDetailCtrl = (FacilityReferenceDetailListCtrl) args.get("facilityReferenceDetailCtrl");
		} else {
			this.facilityReferenceDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_finRefDetailId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefDetailId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finRefType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finRefId.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finRefId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_isActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_showInStage.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_showInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_mandInputInStage.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_mandInputInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_allowInputInStage.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_allowInputInStage.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_FacilityReferenceDetailSearch_RecordStatus.setVisible(false);
			this.label_FacilityReferenceDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<FacilityReferenceDetail> searchObj = (JdbcSearchObject<FacilityReferenceDetail>) args
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
		showFacilityReferenceDetailSeekDialog();
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
		this.window_FacilityReferenceDetailSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showFacilityReferenceDetailSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_FacilityReferenceDetailSearch.doModal();
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

		final JdbcSearchObject<FacilityReferenceDetail> so = new JdbcSearchObject<FacilityReferenceDetail>(FacilityReferenceDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("LMTFinRefDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("LMTFinRefDetail_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.finRefDetailId.getValue())) {

			// get the search operator
			final Listitem item_FinRefDetailId = this.sortOperator_finRefDetailId.getSelectedItem();

			if (item_FinRefDetailId != null) {
				final int searchOpId = ((SearchOperators) item_FinRefDetailId.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinType = this.sortOperator_finType.getSelectedItem();

			if (item_FinType != null) {
				final int searchOpId = ((SearchOperators) item_FinType.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinRefType = this.sortOperator_finRefType.getSelectedItem();

			if (item_FinRefType != null) {
				final int searchOpId = ((SearchOperators) item_FinRefType.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_FinRefId = this.sortOperator_finRefId.getSelectedItem();

			if (item_FinRefId != null) {
				final int searchOpId = ((SearchOperators) item_FinRefId.getAttribute("data")).getSearchOperatorId();

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
		final Listitem item_IsActive = this.sortOperator_isActive.getSelectedItem();

		if (item_IsActive != null) {
			final int searchOpId = ((SearchOperators) item_IsActive.getAttribute("data")).getSearchOperatorId();
			
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
			final Listitem item_ShowInStage = this.sortOperator_showInStage.getSelectedItem();

			if (item_ShowInStage != null) {
				final int searchOpId = ((SearchOperators) item_ShowInStage.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_MandInputInStage = this.sortOperator_mandInputInStage.getSelectedItem();

			if (item_MandInputInStage != null) {
				final int searchOpId = ((SearchOperators) item_MandInputInStage.getAttribute("data")).getSearchOperatorId();

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
			final Listitem item_AllowInputInStage = this.sortOperator_allowInputInStage.getSelectedItem();

			if (item_AllowInputInStage != null) {
				final int searchOpId = ((SearchOperators) item_AllowInputInStage.getAttribute("data")).getSearchOperatorId();

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
		//this.facilityReferenceDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.facilityReferenceDetailCtrl.listBoxFacilityReferenceDetail;
		final Paging paging = this.facilityReferenceDetailCtrl.pagingFacilityReferenceDetailList;
		

		// set the model to the list box with the initial result set get by the DAO method.
		((PagedListWrapper<FacilityReferenceDetail>) listBox.getModel()).init(so, listBox, paging);	
		//this.facilityReferenceDetailCtrl.setSearchObj(so);

		this.label_FacilityReferenceDetailSearchResult.setValue(Labels.getLabel("label_FacilityReferenceDetailSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFacilityReferenceDetailService(FacilityReferenceDetailService facilityReferenceDetailService) {
		this.facilityReferenceDetailService = facilityReferenceDetailService;
	}

	public FacilityReferenceDetailService getFacilityReferenceDetailService() {
		return this.facilityReferenceDetailService;
	}
}