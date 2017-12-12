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
 * FileName    		:  QuestionListCtrl.java                                                   * 	  
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.service.bmtmasters.QuestionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.bmtmasters.question.model.QuestionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the /WEB-INF/pages/BMTMasters/Question/QuestionList.zul
 * file.
 */
public class QuestionListCtrl extends GFCBaseListCtrl<Question> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QuestionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QuestionList; // autowired
	protected Borderlayout borderLayout_QuestionList; // autowired
	protected Paging pagingQuestionList; // autowired
	protected Listbox listBoxQuestion; // autowired

	// List headers
	protected Listheader listheader_QuestionDesc; // autowired
	protected Listheader listheader_AnswerA; // autowired
	protected Listheader listheader_AnswerB; // autowired
	protected Listheader listheader_AnswerC; // autowired
	protected Listheader listheader_AnswerD; // autowired
	protected Listheader listheader_QuestionIsActive; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_QuestionList_NewQuestion; // autowired
	protected Button button_QuestionList_QuestionSearchDialog; // autowired
	protected Button button_QuestionList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Question> searchObj;
	
	private transient QuestionService questionService;

	/**
	 * default constructor.<br>
	 */
	public QuestionListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		moduleCode = "Question";
	}

	public void onCreate$window_QuestionList(Event event) throws Exception {
		logger.debug("Entering");
				
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_QuestionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingQuestionList.setPageSize(getListRows());
		this.pagingQuestionList.setDetailed(true);

		this.listheader_QuestionDesc.setSortAscending(new FieldComparator("questionDesc", true));
		this.listheader_QuestionDesc.setSortDescending(new FieldComparator("questionDesc", false));
		this.listheader_AnswerA.setSortAscending(new FieldComparator("answerA", true));
		this.listheader_AnswerA.setSortDescending(new FieldComparator("answerA", false));
		this.listheader_AnswerB.setSortAscending(new FieldComparator("answerB", true));
		this.listheader_AnswerB.setSortDescending(new FieldComparator("answerB", false));
		this.listheader_AnswerC.setSortAscending(new FieldComparator("answerC", true));
		this.listheader_AnswerC.setSortDescending(new FieldComparator("answerC", false));
		this.listheader_AnswerD.setSortAscending(new FieldComparator("answerD", true));
		this.listheader_AnswerD.setSortDescending(new FieldComparator("answerD", false));
		this.listheader_QuestionIsActive.setSortAscending(new FieldComparator("questionIsActive", true));
		this.listheader_QuestionIsActive.setSortDescending(new FieldComparator("questionIsActive", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<Question>(Question.class,getListRows());
		this.searchObj.addSort("QuestionId", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTQuestion_View");
			if (isFirstTask()) {
				button_QuestionList_NewQuestion.setVisible(true);
			} else {
				button_QuestionList_NewQuestion.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTQuestion_AView");
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxQuestion,this.pagingQuestionList);
		// set the itemRenderer
		this.listBoxQuestion.setItemRenderer(new QuestionListModelItemRenderer());
					
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("QuestionList");
		
		this.button_QuestionList_NewQuestion.setVisible(getUserWorkspace().isAllowed("button_QuestionList_NewQuestion"));
		this.button_QuestionList_QuestionSearchDialog.setVisible(getUserWorkspace().isAllowed("button_QuestionList_QuestionFindDialog"));
		this.button_QuestionList_PrintList.setVisible(getUserWorkspace().isAllowed("button_QuestionList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.question.model.QuestionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onQuestionItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Question object
		final Listitem item = this.listBoxQuestion.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Question aQuestion = (Question) item.getAttribute("data");
			final Question question = getQuestionService().getQuestionById(aQuestion.getId());
			
			if(question==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aQuestion.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_QuestionId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND QuestionId="+ question.getQuestionId()+" AND version=" + question.getVersion()+" ";

					boolean userAcces =  validateUserAccess(question.getWorkflowId(),getUserWorkspace().getLoggedInUser().getLoginUsrID(), "Question", whereCond, question.getTaskId(), question.getNextTaskId());
					if (userAcces){
						showDetailView(question);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(question);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Question dialog with a new empty entry. <br>
	 */
	public void onClick$button_QuestionList_NewQuestion(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Question object, We GET it from the backend.
		final Question aQuestion = getQuestionService().getNewQuestion();
		showDetailView(aQuestion);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Question (aQuestion)
	 * @throws Exception
	 */
	private void showDetailView(Question aQuestion) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if (aQuestion.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aQuestion.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("question", aQuestion);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the QuestionListbox from the
		 * dialog when we do a delete, edit or insert a Question.
		 */
		map.put("questionListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/BMTMasters/Question/QuestionDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_QuestionList);
		logger.debug("Leaving");
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug(event.toString());
		this.pagingQuestionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_QuestionList, event);
		this.window_QuestionList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the Question dialog
	 */
	
	public void onClick$button_QuestionList_QuestionSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our QuestionDialog zul-file with parameters. So we can
		 * call them with a object of the selected Question. For handed over
		 * these parameter only a Map is accepted. So we put the Question object
		 * in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("questionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/BMTMasters/Question/QuestionSearchDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * When the question print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_QuestionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("Question", getSearchObj(),this.pagingQuestionList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}

	public QuestionService getQuestionService() {
		return this.questionService;
	}

	public JdbcSearchObject<Question> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Question> searchObj) {
		this.searchObj = searchObj;
	}
}