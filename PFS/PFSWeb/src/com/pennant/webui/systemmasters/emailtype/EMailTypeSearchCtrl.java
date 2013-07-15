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
 * FileName    		:  EMailTypeSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.emailtype;

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
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.systemmasters.EMailTypeService;
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
 * /WEB-INF/pages/SystemMaster/EMailType/EMailTypeSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EMailTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3089315155808746090L;

	private final static Logger logger = Logger.getLogger(EMailTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_EMailTypeSearch; 			// autoWired

	protected Textbox 	emailTypeCode; 						// autoWired
	protected Listbox 	sortOperator_emailTypeCode; 		// autoWired
	protected Textbox 	emailTypeDesc; 						// autoWired
	protected Listbox 	sortOperator_emailTypeDesc; 		// autoWired
	protected Intbox 	emailTypePriority; 					// autoWired
	protected Listbox 	sortOperator_emailTypePriority; 	// autoWired
	protected Checkbox 	emailTypeIsActive; 					// autoWired
	protected Listbox 	sortOperator_emailTypeIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	protected Label label_EMailTypeSearch_RecordStatus; 	// autoWired
	protected Label label_EMailTypeSearch_RecordType; 		// autoWired
	protected Label label_EMailTypeSearchResult; 			// autoWired

	// not autoWired variables
	private transient EMailTypeListCtrl eMailTypeCtrl; // over handed per parameter
	private transient EMailTypeService eMailTypeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EMailType");

	/**
	 * Default constructor
	 */
	public EMailTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected EMailType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EMailTypeSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("eMailTypeCtrl")) {
			this.eMailTypeCtrl = (EMailTypeListCtrl) args.get("eMailTypeCtrl");
		} else {
			this.eMailTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_emailTypeCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_emailTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypeDesc.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_emailTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypePriority.setModel(new ListModelList(new SearchOperators().getNumericOperators()));
		this.sortOperator_emailTypePriority.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypeIsActive.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_emailTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_EMailTypeSearch_RecordStatus.setVisible(false);
			this.label_EMailTypeSearch_RecordType.setVisible(false);
		}

		//Set Field Properties
		this.emailTypePriority.setText("");
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<EMailType> searchObj = (JdbcSearchObject<EMailType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("emailTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_emailTypeCode, filter);
					this.emailTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("emailTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_emailTypeDesc, filter);
					this.emailTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("emailTypePriority")) {
					SearchOperators.restoreNumericOperator(this.sortOperator_emailTypePriority, filter);
					this.emailTypePriority.setValue(Integer.parseInt(filter.getValue().toString()));
				} else if (filter.getProperty().equals("emailTypeIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_emailTypeIsActive, filter);
					//this.emailTypeIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.emailTypeIsActive.setChecked(true);
					}else{
						this.emailTypeIsActive.setChecked(false);
					}
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
		showEMailTypeSeekDialog();
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
		this.window_EMailTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showEMailTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_EMailTypeSearch.doModal();
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

		final JdbcSearchObject<EMailType> so = new JdbcSearchObject<EMailType>(EMailType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTEMailTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTEMailTypes_AView");
		}

		if (StringUtils.isNotEmpty(this.emailTypeCode.getValue())) {

			// get the search operator
			final Listitem item_EmailTypeCode = this.sortOperator_emailTypeCode.getSelectedItem();

			if (item_EmailTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_EmailTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("emailTypeCode", "%" + this.emailTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("emailTypeCode", this.emailTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.emailTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_EmailTypeDesc = this.sortOperator_emailTypeDesc.getSelectedItem();

			if (item_EmailTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_EmailTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("emailTypeDesc", "%" + this.emailTypeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("emailTypeDesc", this.emailTypeDesc.getValue(), searchOpId));
				}
			}
		}
		if (this.emailTypePriority.getValue() != null) {

			// get the search operator
			final Listitem item_EmailTypePriority = this.sortOperator_emailTypePriority.getSelectedItem();

			if (item_EmailTypePriority != null) {
				final int searchOpId = ((SearchOperators) item_EmailTypePriority.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("emailTypePriority", this.emailTypePriority.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_EmailTypeIsActive = this.sortOperator_emailTypeIsActive.getSelectedItem();

		if (item_EmailTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_EmailTypeIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.emailTypeIsActive.isChecked()) {
					so.addFilter(new Filter("emailTypeIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("emailTypeIsActive", 0, searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%",	searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
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
		so.addSort("EmailTypeCode", false);

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
		this.eMailTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.eMailTypeCtrl.listBoxEMailType;
		final Paging paging = this.eMailTypeCtrl.pagingEMailTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<EMailType>) listBox.getModel()).init(so, listBox, paging);
		this.eMailTypeCtrl.setSearchObj(so);

		this.label_EMailTypeSearchResult.setValue(Labels.getLabel(
		"label_EMailTypeSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEMailTypeService(EMailTypeService eMailTypeService) {
		this.eMailTypeService = eMailTypeService;
	}
	public EMailTypeService getEMailTypeService() {
		return this.eMailTypeService;
	}
}