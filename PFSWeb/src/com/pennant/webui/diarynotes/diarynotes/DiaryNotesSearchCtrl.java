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
 * FileName    		:  DiaryNotesSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.diarynotes.diarynotes;

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
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.service.diarynotes.DiaryNotesService;
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
 * /WEB-INF/pages/SolutionFactory/DiaryNotes/diaryNotesSearch.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DiaryNotesSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DiaryNotesSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DiaryNotesSearch; // autowWired
	
	protected Textbox seqNo; // autowWired
	protected Listbox sortOperator_seqNo; // autowWired
	protected Textbox dnType; // autowWired
	protected Listbox sortOperator_dnType; // autowWired
	protected Textbox dnCreatedNo; // autowWired
	protected Listbox sortOperator_dnCreatedNo; // autowWired
	protected Textbox dnCreatedName; // autowWired
	protected Listbox sortOperator_dnCreatedName; // autowWired
	protected Textbox frqCode; // autowWired
	protected Listbox sortOperator_frqCode; // autowWired
	protected Textbox firstActionDate; // autowWired
	protected Listbox sortOperator_firstActionDate; // autowWired
	protected Textbox nextActionDate; // autowWired
	protected Listbox sortOperator_nextActionDate; // autowWired
	protected Textbox lastActionDate; // autowWired
	protected Listbox sortOperator_lastActionDate; // autowWired
	protected Textbox finalActionDate; // autowWired
	protected Listbox sortOperator_finalActionDate; // autowWired
	protected Checkbox suspend; // autowWired
	protected Listbox sortOperator_suspend; // autowWired
	protected Textbox suspendStartDate; // autowWired
	protected Listbox sortOperator_suspendStartDate; // autowWired
	protected Textbox suspendEndDate; // autowWired
	protected Listbox sortOperator_suspendEndDate; // autowWired
	protected Checkbox recordDeleted; // autowWired
	protected Listbox sortOperator_recordDeleted; // autowWired
	protected Textbox narration; // autowWired
	protected Listbox sortOperator_narration; // autowWired
	protected Textbox recordStatus; // autowWired
	protected Listbox recordType;	// autowWired
	protected Listbox sortOperator_recordStatus; // autowWired
	protected Listbox sortOperator_recordType; // autowWired
	
	protected Label label_DiaryNotesSearch_RecordStatus; // autowWired
	protected Label label_DiaryNotesSearch_RecordType; // autowWired
	protected Label label_DiaryNotesSearchResult; // autowWired

	// not auto wired vars
	private transient DiaryNotesListCtrl diaryNotesCtrl; // overhanded per param
	private transient DiaryNotesService diaryNotesService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DiaryNotes");
	
	/**
	 * constructor
	 */
	public DiaryNotesSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DiaryNotesSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("diaryNotesCtrl")) {
			this.diaryNotesCtrl = (DiaryNotesListCtrl) args.get("diaryNotesCtrl");
		} else {
			this.diaryNotesCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_seqNo.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_seqNo.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_dnType.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dnType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_dnCreatedNo.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dnCreatedNo.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_dnCreatedName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_dnCreatedName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_frqCode.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_frqCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_firstActionDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_firstActionDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_nextActionDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_nextActionDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_lastActionDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_lastActionDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_finalActionDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_finalActionDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_suspend.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_suspend.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_suspendStartDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_suspendStartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_suspendEndDate.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_suspendEndDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_recordDeleted.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_recordDeleted.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_narration.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_narration.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_DiaryNotesSearch_RecordStatus.setVisible(false);
			this.label_DiaryNotesSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<DiaryNotes> searchObj = (JdbcSearchObject<DiaryNotes>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("seqNo")) {
					SearchOperators.restoreStringOperator(this.sortOperator_seqNo, filter);
					this.seqNo.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("dnType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dnType, filter);
					this.dnType.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("dnCreatedNo")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dnCreatedNo, filter);
					this.dnCreatedNo.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("dnCreatedName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_dnCreatedName, filter);
					this.dnCreatedName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("frqCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_frqCode, filter);
					this.frqCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("firstActionDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_firstActionDate, filter);
					this.firstActionDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("nextActionDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_nextActionDate, filter);
					this.nextActionDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("lastActionDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_lastActionDate, filter);
					this.lastActionDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("finalActionDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_finalActionDate, filter);
					this.finalActionDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("suspend")) {
					SearchOperators.restoreStringOperator(this.sortOperator_suspend, filter);
					this.suspend.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("suspendStartDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_suspendStartDate, filter);
					this.suspendStartDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("suspendEndDate")) {
					SearchOperators.restoreStringOperator(this.sortOperator_suspendEndDate, filter);
					this.suspendEndDate.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("recordDeleted")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordDeleted, filter);
					this.recordDeleted.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("narration")) {
					SearchOperators.restoreStringOperator(this.sortOperator_narration, filter);
					this.narration.setValue(filter.getValue().toString());
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
		showDiaryNotesSeekDialog();
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
		this.window_DiaryNotesSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDiaryNotesSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_DiaryNotesSearch.doModal();
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

		final JdbcSearchObject<DiaryNotes> so = new JdbcSearchObject<DiaryNotes>(DiaryNotes.class);
		so.addTabelName("DiaryNotes_View");
		
		if (isWorkFlowEnabled()){
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}
		
		
		if (StringUtils.isNotEmpty(this.seqNo.getValue())) {

			// get the search operator
			final Listitem item_SeqNo = this.sortOperator_seqNo.getSelectedItem();

			if (item_SeqNo != null) {
				final int searchOpId = ((SearchOperators) item_SeqNo.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("seqNo", "%" + this.seqNo.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("seqNo", this.seqNo.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dnType.getValue())) {

			// get the search operator
			final Listitem item_DnType = this.sortOperator_dnType.getSelectedItem();

			if (item_DnType != null) {
				final int searchOpId = ((SearchOperators) item_DnType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dnType", "%" + this.dnType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dnType", this.dnType.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dnCreatedNo.getValue())) {

			// get the search operator
			final Listitem item_DnCreatedNo = this.sortOperator_dnCreatedNo.getSelectedItem();

			if (item_DnCreatedNo != null) {
				final int searchOpId = ((SearchOperators) item_DnCreatedNo.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dnCreatedNo", "%" + this.dnCreatedNo.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dnCreatedNo", this.dnCreatedNo.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.dnCreatedName.getValue())) {

			// get the search operator
			final Listitem item_DnCreatedName = this.sortOperator_dnCreatedName.getSelectedItem();

			if (item_DnCreatedName != null) {
				final int searchOpId = ((SearchOperators) item_DnCreatedName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("dnCreatedName", "%" + this.dnCreatedName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("dnCreatedName", this.dnCreatedName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.frqCode.getValue())) {

			// get the search operator
			final Listitem item_FrqCode = this.sortOperator_frqCode.getSelectedItem();

			if (item_FrqCode != null) {
				final int searchOpId = ((SearchOperators) item_FrqCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("frqCode", "%" + this.frqCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("frqCode", this.frqCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.firstActionDate.getValue())) {

			// get the search operator
			final Listitem item_FirstActionDate = this.sortOperator_firstActionDate.getSelectedItem();

			if (item_FirstActionDate != null) {
				final int searchOpId = ((SearchOperators) item_FirstActionDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("firstActionDate", "%" + this.firstActionDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("firstActionDate", this.firstActionDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.nextActionDate.getValue())) {

			// get the search operator
			final Listitem item_NextActionDate = this.sortOperator_nextActionDate.getSelectedItem();

			if (item_NextActionDate != null) {
				final int searchOpId = ((SearchOperators) item_NextActionDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("nextActionDate", "%" + this.nextActionDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("nextActionDate", this.nextActionDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.lastActionDate.getValue())) {

			// get the search operator
			final Listitem item_LastActionDate = this.sortOperator_lastActionDate.getSelectedItem();

			if (item_LastActionDate != null) {
				final int searchOpId = ((SearchOperators) item_LastActionDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lastActionDate", "%" + this.lastActionDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lastActionDate", this.lastActionDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.finalActionDate.getValue())) {

			// get the search operator
			final Listitem item_FinalActionDate = this.sortOperator_finalActionDate.getSelectedItem();

			if (item_FinalActionDate != null) {
				final int searchOpId = ((SearchOperators) item_FinalActionDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("finalActionDate", "%" + this.finalActionDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("finalActionDate", this.finalActionDate.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_Suspend = this.sortOperator_suspend.getSelectedItem();

		if (item_Suspend != null) {
			final int searchOpId = ((SearchOperators) item_Suspend.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.suspend.isChecked()){
					so.addFilter(new Filter("suspend",1, searchOpId));
				}else{
					so.addFilter(new Filter("suspend",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.suspendStartDate.getValue())) {

			// get the search operator
			final Listitem item_SuspendStartDate = this.sortOperator_suspendStartDate.getSelectedItem();

			if (item_SuspendStartDate != null) {
				final int searchOpId = ((SearchOperators) item_SuspendStartDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("suspendStartDate", "%" + this.suspendStartDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("suspendStartDate", this.suspendStartDate.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.suspendEndDate.getValue())) {

			// get the search operator
			final Listitem item_SuspendEndDate = this.sortOperator_suspendEndDate.getSelectedItem();

			if (item_SuspendEndDate != null) {
				final int searchOpId = ((SearchOperators) item_SuspendEndDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("suspendEndDate", "%" + this.suspendEndDate.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("suspendEndDate", this.suspendEndDate.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem item_RecordDeleted = this.sortOperator_recordDeleted.getSelectedItem();

		if (item_RecordDeleted != null) {
			final int searchOpId = ((SearchOperators) item_RecordDeleted.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.recordDeleted.isChecked()){
					so.addFilter(new Filter("recordDeleted",1, searchOpId));
				}else{
					so.addFilter(new Filter("recordDeleted",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.narration.getValue())) {

			// get the search operator
			final Listitem item_Narration = this.sortOperator_narration.getSelectedItem();

			if (item_Narration != null) {
				final int searchOpId = ((SearchOperators) item_Narration.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("narration", "%" + this.narration.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("narration", this.narration.getValue(), searchOpId));
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
		// Defualt Sort on the table
		so.addSort("SeqNo", false);

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
		this.diaryNotesCtrl.setSearchObj(so);

		final Listbox listBox = this.diaryNotesCtrl.listBoxDiaryNotes;
		final Paging paging = this.diaryNotesCtrl.pagingDiaryNotesList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<DiaryNotes>) listBox.getModel()).init(so, listBox, paging);
		this.diaryNotesCtrl.setSearchObj(so);

		this.label_DiaryNotesSearchResult.setValue(Labels.getLabel("label_DiaryNotesSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDiaryNotesService(DiaryNotesService diaryNotesService) {
		this.diaryNotesService = diaryNotesService;
	}

	public DiaryNotesService getDiaryNotesService() {
		return this.diaryNotesService;
	}
}