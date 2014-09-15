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
 * FileName    		:  EmployerDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.employerdetail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
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
 * /WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class EmployerDetailSearchCtrl extends GFCBaseCtrl implements
		Serializable {

	private static final long serialVersionUID = -4124921474560332813L;
	private final static Logger logger = Logger
			.getLogger(EmployerDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_EmployerDetailSearch; 		// auto wired
	
	protected Textbox empIndustry; // autowired
	protected Listbox sortOperator_EmpIndustry; // autowired

	protected Textbox empName; // autowired
	protected Listbox sortOperator_EmpName; // autowired

	protected Textbox empFlatNbr; // autowired
	protected Listbox sortOperator_EmpFlatNbr; // autowired

	protected Textbox empPOBox; // autowired
	protected Listbox sortOperator_EmpPOBox; // autowired

	protected Textbox empCountry; // autowired
	protected Listbox sortOperator_EmpCountry; // autowired

	protected Textbox empProvince; // autowired
	protected Listbox sortOperator_EmpProvince; // autowired

	protected Textbox empCity; // autowired
	protected Listbox sortOperator_EmpCity; // autowired

    protected Combobox empAlocationType; // autowired
	protected Listbox sortOperator_EmpAlocationType; // autowired

	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired

	protected Label label_EmployerDetailList_RecordStatus; 	// auto wired
	protected Label label_EmployerDetailList_RecordType; 		// auto wired
	protected Label label_EmployerDetailListSearchResult; 			// auto wired

	// not auto wired Var's
	private transient EmployerDetailListCtrl EmployerDetailListCtrl; // overHanded per parameter
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("EmployerDetail");

	/**
	 * Default constructor
	 */
	public EmployerDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected EmployerDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_EmployerDetailSearch(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(
					workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("EmployerDetailListCtrl")) {
			this.EmployerDetailListCtrl = (EmployerDetailListCtrl) args
					.get("EmployerDetailListCtrl");
		} else {
			this.EmployerDetailListCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		
		this.sortOperator_EmpIndustry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpIndustry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpFlatNbr.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpPOBox.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpCountry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpProvince.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_EmpCity.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_EmpCity.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
      	this.sortOperator_EmpAlocationType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
      	fillComboBox(this.empAlocationType,"",PennantStaticListUtil.getEmpAlocList(),"");
		this.sortOperator_EmpAlocationType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_EmployerDetailList_RecordStatus.setVisible(false);
			this.label_EmployerDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<EmployerDetail> searchObj = (JdbcSearchObject<EmployerDetail>)
					args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("empIndustry")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpIndustry, filter);
					this.empIndustry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empName")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpName, filter);
					this.empName.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empFlatNbr")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpFlatNbr, filter);
					this.empFlatNbr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empPOBox")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpPOBox, filter);
					this.empPOBox.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empCountry")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpCountry, filter);
					this.empCountry.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empProvince")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpProvince, filter);
					this.empProvince.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("empCity")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_EmpCity, filter);
					this.empCity.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_RecordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(
							this.sortOperator_RecordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue()
								.equals(filter.getValue().toString())) {
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showEmployerDetailSeekDialog();
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
		this.window_EmployerDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showEmployerDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_EmployerDetailSearch.doModal();
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
	 * 1. Checks for each text box if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {
		logger.debug("Entering");

		final JdbcSearchObject<EmployerDetail> so = new JdbcSearchObject<EmployerDetail>(
				EmployerDetail.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("EmployerDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		} else {
			so.addTabelName("EmployerDetail_AView");
		}

		if (StringUtils.isNotEmpty(this.empIndustry.getValue())) {
			
			// get the search operator
			final Listitem itemEmpIndustry = this.sortOperator_EmpIndustry.getSelectedItem();
			
			if (itemEmpIndustry != null) {
				final int searchOpId = ((SearchOperators) itemEmpIndustry
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpIndustry", "%"
							+ this.empIndustry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpIndustry", this.empIndustry.getValue(),
							searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empName.getValue())) {

			// get the search operator
			final Listitem itemEmpName = this.sortOperator_EmpName.getSelectedItem();

			if (itemEmpName != null) {
				final int searchOpId = ((SearchOperators) itemEmpName
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpName", "%"
							+ this.empName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpName", this.empName.getValue(),
							searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empFlatNbr.getValue())) {

			// get the search operator
			final Listitem itemEmpFlatNbr = this.sortOperator_EmpFlatNbr.getSelectedItem();

			if (itemEmpFlatNbr != null) {
				final int searchOpId = ((SearchOperators) itemEmpFlatNbr
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpFlatNbr", "%"
						+ this.empFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpFlatNbr", this.empFlatNbr
							.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empPOBox.getValue())) {
			
			// get the search operator
			final Listitem itemEmpPOBox = this.sortOperator_EmpPOBox.getSelectedItem();
			
			if (itemEmpPOBox != null) {
				final int searchOpId = ((SearchOperators) itemEmpPOBox
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpPOBox", "%"
							+ this.empPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpPOBox", this.empPOBox
							.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empCountry.getValue())) {
			
			// get the search operator
			final Listitem itemEmpCountry = this.sortOperator_EmpCountry.getSelectedItem();
			
			if (itemEmpCountry != null) {
				final int searchOpId = ((SearchOperators) itemEmpCountry
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpCountry", "%"
							+ this.empCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpCountry", this.empCountry
							.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empProvince.getValue())) {
			
			// get the search operator
			final Listitem itemEmpProvince = this.sortOperator_EmpProvince.getSelectedItem();
			
			if (itemEmpProvince != null) {
				final int searchOpId = ((SearchOperators) itemEmpProvince
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpProvince", "%"
							+ this.empProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpProvince", this.empProvince
							.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.empCity.getValue())) {
			
			// get the search operator
			final Listitem itemEmpCity = this.sortOperator_EmpCity.getSelectedItem();
			
			if (itemEmpCity != null) {
				final int searchOpId = ((SearchOperators) itemEmpCity
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpCity", "%"
							+ this.empCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpCity", this.empCity
							.getValue(), searchOpId));
				}
			}
		}
		if (!StringUtils.trimToEmpty(this.empAlocationType.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
			
			// get the search operator
			final Listitem itemEmpAlocationType = this.sortOperator_EmpAlocationType.getSelectedItem();
			
			if (itemEmpAlocationType != null) {
				final int searchOpId = ((SearchOperators) itemEmpAlocationType
						.getAttribute("data")).getSearchOperatorId();
				
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("EmpAlocationType", "%"
							+ this.empAlocationType.getSelectedItem().getValue().toString().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("EmpAlocationType", this.empAlocationType
							.getValue(), searchOpId));
				}
			}
		}
		
		
		
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem itemRecordStatus = this.sortOperator_RecordStatus.getSelectedItem();
			if (itemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) itemRecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%"
						+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus
							.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem itemRecordType = this.sortOperator_RecordType.getSelectedItem();
			if (itemRecordType != null) {
				final int searchOpId = ((SearchOperators) itemRecordType
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%"
							+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("EmployerId", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.EmployerDetailListCtrl.setSearchObj(so);

		final Listbox listBox = this.EmployerDetailListCtrl.listBoxEmployerDetail;
		final Paging paging = this.EmployerDetailListCtrl.pagingEmployerDetailList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<EmployerDetail>) listBox.getModel()).init(so,
				listBox, paging);
		this.EmployerDetailListCtrl.setSearchObj(so);

		this.label_EmployerDetailListSearchResult.setValue(Labels
				.getLabel("label_EmployerDetailListSearchResult.value")
				+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

}
