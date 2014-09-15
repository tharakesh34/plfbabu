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
 * FileName    		:  QuestionSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2011    														*
 *                                                                  						*
 * Modified Date    :  21-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.question;

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
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.service.bmtmasters.QuestionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class QuestionSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(QuestionSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_QuestionSearch; // autowired
	
	protected Textbox questionId; // autowired
	protected Listbox sortOperator_questionId; // autowired
	protected Textbox questionDesc; // autowired
	protected Listbox sortOperator_questionDesc; // autowired
	protected Textbox answerA; // autowired
	protected Listbox sortOperator_answerA; // autowired
	protected Textbox answerB; // autowired
	protected Listbox sortOperator_answerB; // autowired
	protected Textbox answerC; // autowired
	protected Listbox sortOperator_answerC; // autowired
	protected Textbox answerD; // autowired
	protected Listbox sortOperator_answerD; // autowired
	protected Textbox correctAnswer; // autowired
	protected Listbox sortOperator_correctAnswer; // autowired
	protected Checkbox questionIsActive; // autowired
	protected Listbox sortOperator_questionIsActive; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_QuestionSearch_RecordStatus; // autowired
	protected Label label_QuestionSearch_RecordType; // autowired
	protected Label label_QuestionSearchResult; // autowired

	// not auto wired vars
	private transient QuestionListCtrl questionCtrl; // overhanded per param
	private transient QuestionService questionService;
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Question");
	
	/**
	 * constructor
	 */
	public QuestionSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_QuestionSearch(Event event) throws Exception {

		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("questionCtrl")) {
			this.questionCtrl = (QuestionListCtrl) args.get("questionCtrl");
		} else {
			this.questionCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_questionId.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_questionId.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_questionDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_questionDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_answerA.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_answerA.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_answerB.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_answerB.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_answerC.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_answerC.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_answerD.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_answerD.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_correctAnswer.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_correctAnswer.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_questionIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_questionIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_QuestionSearch_RecordStatus.setVisible(false);
			this.label_QuestionSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<Question> searchObj = (JdbcSearchObject<Question>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("questionId")) {
					SearchOperators.restoreStringOperator(this.sortOperator_questionId, filter);
					this.questionId.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("questionDesc")) {
					SearchOperators.restoreStringOperator(this.sortOperator_questionDesc, filter);
					this.questionDesc.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("answerA")) {
					SearchOperators.restoreStringOperator(this.sortOperator_answerA, filter);
					this.answerA.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("answerB")) {
					SearchOperators.restoreStringOperator(this.sortOperator_answerB, filter);
					this.answerB.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("answerC")) {
					SearchOperators.restoreStringOperator(this.sortOperator_answerC, filter);
					this.answerC.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("answerD")) {
					SearchOperators.restoreStringOperator(this.sortOperator_answerD, filter);
					this.answerD.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("correctAnswer")) {
					SearchOperators.restoreStringOperator(this.sortOperator_correctAnswer, filter);
					this.correctAnswer.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("questionIsActive")) {
					SearchOperators.restoreStringOperator(this.sortOperator_questionIsActive, filter);
					//this.questionIsActive.setValue(filter.getValue().toString());
					if(Integer.parseInt(filter.getValue().toString()) == 1){
						this.questionIsActive.setChecked(true);
					}else{
						this.questionIsActive.setChecked(false);
					}
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
		showQuestionSeekDialog();
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
		this.window_QuestionSearch.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showQuestionSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			this.window_QuestionSearch.doModal();
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

		final JdbcSearchObject<Question> so = new JdbcSearchObject<Question>(Question.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("BMTQuestion_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("BMTQuestion_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.questionId.getValue())) {

			// get the search operator
			final Listitem listItemQuestionId = this.sortOperator_questionId.getSelectedItem();

			if (listItemQuestionId != null) {
				final int searchOpId = ((SearchOperators) listItemQuestionId.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("questionId", "%" + this.questionId.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("questionId", this.questionId.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.questionDesc.getValue())) {

			// get the search operator
			final Listitem listItemQuestionDesc = this.sortOperator_questionDesc.getSelectedItem();

			if (listItemQuestionDesc != null) {
				final int searchOpId = ((SearchOperators) listItemQuestionDesc.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("questionDesc", "%" + this.questionDesc.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("questionDesc", this.questionDesc.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.answerA.getValue())) {

			// get the search operator
			final Listitem listItemAnswerA = this.sortOperator_answerA.getSelectedItem();

			if (listItemAnswerA != null) {
				final int searchOpId = ((SearchOperators) listItemAnswerA.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("answerA", "%" + this.answerA.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("answerA", this.answerA.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.answerB.getValue())) {

			// get the search operator
			final Listitem listItemAnswerB = this.sortOperator_answerB.getSelectedItem();

			if (listItemAnswerB != null) {
				final int searchOpId = ((SearchOperators) listItemAnswerB.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("answerB", "%" + this.answerB.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("answerB", this.answerB.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.answerC.getValue())) {

			// get the search operator
			final Listitem listItemAnswerC = this.sortOperator_answerC.getSelectedItem();

			if (listItemAnswerC != null) {
				final int searchOpId = ((SearchOperators) listItemAnswerC.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("answerC", "%" + this.answerC.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("answerC", this.answerC.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.answerD.getValue())) {

			// get the search operator
			final Listitem listItemAnswerD = this.sortOperator_answerD.getSelectedItem();

			if (listItemAnswerD != null) {
				final int searchOpId = ((SearchOperators) listItemAnswerD.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("answerD", "%" + this.answerD.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("answerD", this.answerD.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.correctAnswer.getValue())) {

			// get the search operator
			final Listitem listItemCorrectAnswer = this.sortOperator_correctAnswer.getSelectedItem();

			if (listItemCorrectAnswer != null) {
				final int searchOpId = ((SearchOperators) listItemCorrectAnswer.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("correctAnswer", "%" + this.correctAnswer.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("correctAnswer", this.correctAnswer.getValue(), searchOpId));
				}
			}
		}
		// get the search operatorxxx
		final Listitem listItemQuestionIsActive = this.sortOperator_questionIsActive.getSelectedItem();

		if (listItemQuestionIsActive != null) {
			final int searchOpId = ((SearchOperators) listItemQuestionIsActive.getAttribute("data")).getSearchOperatorId();
			
			if (searchOpId == -1) {
				// do nothing
			} else {
				
				if(this.questionIsActive.isChecked()){
					so.addFilter(new Filter("questionIsActive",1, searchOpId));
				}else{
					so.addFilter(new Filter("questionIsActive",0, searchOpId));	
				}
			}
		}
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem listItemRecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (listItemRecordStatus != null) {
				final int searchOpId = ((SearchOperators) listItemRecordStatus.getAttribute("data")).getSearchOperatorId();
	
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
			final Listitem listItemRecordType = this.sortOperator_recordType.getSelectedItem();
			if (listItemRecordType!= null) {
				final int searchOpId = ((SearchOperators) listItemRecordType.getAttribute("data")).getSearchOperatorId();
	
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
		so.addSort("QuestionId", false);

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
		this.questionCtrl.setSearchObj(so);

		final Listbox listBox = this.questionCtrl.listBoxQuestion;
		final Paging paging = this.questionCtrl.pagingQuestionList;
		

		// set the model to the listbox with the initial resultset get by the DAO method.
		((PagedListWrapper<Question>) listBox.getModel()).init(so, listBox, paging);
		this.questionCtrl.setSearchObj(so);

		this.label_QuestionSearchResult.setValue(Labels.getLabel("label_QuestionSearchResult.value") + " "
				+ String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}

	public QuestionService getQuestionService() {
		return this.questionService;
	}
}