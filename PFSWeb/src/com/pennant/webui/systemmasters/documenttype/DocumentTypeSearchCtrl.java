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
 * FileName    		:  DocumentTypeSearchCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.documenttype;

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
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
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
 * /WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeSearch.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DocumentTypeSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 665938684136332420L;
	private final static Logger logger = Logger.getLogger(DocumentTypeSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_DocumentTypeSearch; 		// autoWired

	protected Textbox 	docTypeCode; 					// autoWired
	protected Listbox 	sortOperator_docTypeCode; 		// autoWired
	protected Textbox 	docTypeDesc; 					// autoWired
	protected Listbox 	sortOperator_docTypeDesc; 		// autoWired
	protected Checkbox 	docIsMandatory; 				// autoWired
	protected Listbox 	sortOperator_docIsMandatory; 	// autoWired
	protected Checkbox 	docTypeIsActive; 				// autoWired
	protected Listbox 	sortOperator_docTypeIsActive; 	// autoWired
	protected Textbox 	recordStatus; 					// autoWired
	protected Listbox 	recordType; 					// autoWired
	protected Listbox 	sortOperator_recordStatus; 		// autoWired
	protected Listbox 	sortOperator_recordType; 		// autoWired

	protected Label label_DocumentTypeSearch_RecordStatus;	// autoWired
	protected Label label_DocumentTypeSearch_RecordType; 	// autoWired
	protected Label label_DocumentTypeSearchResult; 		// autoWired

	// not autoWired variables
	private transient DocumentTypeListCtrl documentTypeCtrl; // over handed per parameter
	private transient DocumentTypeService documentTypeService;
	private transient WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DocumentType");

	/**
	 * constructor
	 */
	public DocumentTypeSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected DocumentType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DocumentTypeSearch(Event event) throws Exception {
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

		if (args.containsKey("documentTypeCtrl")) {
			this.documentTypeCtrl = (DocumentTypeListCtrl) args.get("documentTypeCtrl");
		} else {
			this.documentTypeCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_docTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_docTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_docTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_docTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_docIsMandatory.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_docIsMandatory.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_docTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_docTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_DocumentTypeSearch_RecordStatus.setVisible(false);
			this.label_DocumentTypeSearch_RecordType.setVisible(false);
		}

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			@SuppressWarnings("unchecked")
			final JdbcSearchObject<DocumentType> searchObj = (JdbcSearchObject<DocumentType>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("docTypeCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_docTypeCode, filter);
					this.docTypeCode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("docTypeDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_docTypeDesc, filter);
					this.docTypeDesc.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("docIsMandatory")) {
					SearchOperators.restoreStringOperator(this.sortOperator_docIsMandatory, filter);
					this.docIsMandatory.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("docTypeIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_docTypeIsActive, filter);
					//this.docTypeIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.docTypeIsActive.setChecked(true);
					}else{
						this.docTypeIsActive.setChecked(false);
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
		showDocumentTypeSeekDialog();
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
		this.window_DocumentTypeSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDocumentTypeSeekDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			// open the dialog in modal mode
			this.window_DocumentTypeSearch.doModal();
		} catch (final Exception e) {
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

		final JdbcSearchObject<DocumentType> so = new JdbcSearchObject<DocumentType>(DocumentType.class);

		if (isWorkFlowEnabled()) {
			so.addTabelName("BMTDocumentTypes_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			so.addTabelName("BMTDocumentTypes_AView");
		}
		if (StringUtils.isNotEmpty(this.docTypeCode.getValue())) {

			// get the search operator
			final Listitem item_DocTypeCode = this.sortOperator_docTypeCode.getSelectedItem();

			if (item_DocTypeCode != null) {
				final int searchOpId = ((SearchOperators) item_DocTypeCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("docTypeCode", "%" + this.docTypeCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("docTypeCode", this.docTypeCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.docTypeDesc.getValue())) {

			// get the search operator
			final Listitem item_DocTypeDesc = this.sortOperator_docTypeDesc.getSelectedItem();

			if (item_DocTypeDesc != null) {
				final int searchOpId = ((SearchOperators) item_DocTypeDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("docTypeDesc", "%" + this.docTypeDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("docTypeDesc", this.docTypeDesc.getValue(), searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_DocIsMandatory = this.sortOperator_docIsMandatory.getSelectedItem();

		if (item_DocIsMandatory != null) {
			final int searchOpId = ((SearchOperators) item_DocIsMandatory.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.docIsMandatory.isChecked()) {
					so.addFilter(new Filter("docIsMandatory", 1, searchOpId));
				} else {
					so.addFilter(new Filter("docIsMandatory", 0, searchOpId));
				}
			}
		}
		// get the search operator
		final Listitem item_DocTypeIsActive = this.sortOperator_docTypeIsActive.getSelectedItem();

		if (item_DocTypeIsActive != null) {
			final int searchOpId = ((SearchOperators) item_DocTypeIsActive.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.docTypeIsActive.isChecked()) {
					so.addFilter(new Filter("docTypeIsActive", 1, searchOpId));
				} else {
					so.addFilter(new Filter("docTypeIsActive", 0, searchOpId));
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
		so.addSort("DocTypeCode", false);

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
		this.documentTypeCtrl.setSearchObj(so);

		final Listbox listBox = this.documentTypeCtrl.listBoxDocumentType;
		final Paging paging = this.documentTypeCtrl.pagingDocumentTypeList;

		// set the model to the list box with the initial result set get by the
		// DAO method.
		((PagedListWrapper<DocumentType>) listBox.getModel()).init(so, listBox,	paging);
		this.documentTypeCtrl.setSearchObj(so);

		this.label_DocumentTypeSearchResult.setValue(Labels.getLabel(
		"label_DocumentTypeSearchResult.value")	+ " " + String.valueOf(paging.getTotalSize()));
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
	public DocumentTypeService getDocumentTypeService() {
		return this.documentTypeService;
	}
}