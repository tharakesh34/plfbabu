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
 * FileName    		:  QuestionDialogCtrl.java                                                   * 	  
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.QuestionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/Question/questionDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class QuestionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4852924198816837755L;
	private final static Logger logger = Logger.getLogger(QuestionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_QuestionDialog;	// autoWired
	protected Textbox 	questionDesc; 			// autoWired
	protected Textbox 	answerA; 				// autoWired
	protected Textbox 	answerB; 				// autoWired
	protected Textbox 	answerC; 				// autoWired
	protected Textbox 	answerD; 				// autoWired
	protected Combobox 	correctAnswer; 			// autoWired
	protected Checkbox 	questionIsActive; 		// autoWired

	protected Label recordStatus; // autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired variables
	private Question question; 								// overHanded per parameter
	private Question prvQuestion; 							// overHanded per parameter
	private transient QuestionListCtrl questionListCtrl; 	// overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_questionDesc;
	private transient String 	oldVar_answerA;
	private transient String 	oldVar_answerB;
	private transient String 	oldVar_answerC;
	private transient String 	oldVar_answerD;
	private transient String 	oldVar_correctAnswer;
	private transient boolean 	oldVar_questionIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_QuestionDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient QuestionService questionService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private List<ValueLabel> listCorrectAnswer = null;

	/**
	 * default constructor.<br>
	 */
	public QuestionDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Question object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_QuestionDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("question")) {
			this.question = (Question) args.get("question");
			Question befImage = new Question();
			BeanUtils.copyProperties(this.question, befImage);
			this.question.setBefImage(befImage);
			setQuestion(this.question);
		} else {
			setQuestion(null);
		}

		doLoadWorkFlow(this.question.isWorkflow(), this.question.getWorkflowId(), this.question.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "QuestionDialog");
		}

		// READ OVERHANDED parameters !
		// we get the questionListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete question here.
		if (args.containsKey("questionListCtrl")) {
			setQuestionListCtrl((QuestionListCtrl) args.get("questionListCtrl"));
		} else {
			setQuestionListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getQuestion());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("QuestionDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_QuestionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_QuestionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_QuestionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_QuestionDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_QuestionDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_QuestionDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" +event.toString());
		doNew();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" +event.toString());
	}

	// GUI Process

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_QuestionDialog, "Question");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aQuestion
	 *            Question
	 */
	public void doWriteBeanToComponents(Question aQuestion) {
		logger.debug("Entering");
		this.questionDesc.setValue(aQuestion.getQuestionDesc());
		this.answerA.setValue(aQuestion.getAnswerA());
		this.answerB.setValue(aQuestion.getAnswerB());
		this.answerC.setValue(aQuestion.getAnswerC());
		this.answerD.setValue(aQuestion.getAnswerD());
		setListCorrectAnswer(this.correctAnswer, aQuestion.getCorrectAnswer());
		if(aQuestion.isNewRecord() || aQuestion.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.questionIsActive.setChecked(true);
			this.questionIsActive.setDisabled(true);
		}else{
			this.questionIsActive.setChecked(aQuestion.isQuestionIsActive());
		}
		this.recordStatus.setValue(aQuestion.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aQuestion
	 */
	public void doWriteComponentsToBean(Question aQuestion) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aQuestion.setQuestionDesc(this.questionDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuestion.setAnswerA(this.answerA.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuestion.setAnswerB(this.answerB.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuestion.setAnswerC(this.answerC.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuestion.setAnswerD(this.answerD.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.correctAnswer.getSelectedItem().getValue() != null) {
				if (this.correctAnswer
						.getSelectedItem().getLabel().toString().equals(PennantJavaUtil.getLabel("label_QuestionDialog_AnswerC.value"))) {
					if (this.answerC.getValue() == null
							|| this.answerC.getValue().equals("")) {
						throw new WrongValueException(
								this.answerC,Labels.getLabel("FIELD_NO_EMPTY", new String[] { 
										Labels.getLabel("label_QuestionDialog_AnswerC.value") }));
					}

				} else if (this.correctAnswer
						.getSelectedItem().getLabel().toString().equals(PennantJavaUtil.getLabel("label_QuestionDialog_AnswerD.value"))) {
					if (this.answerD.getValue() == null || this.answerD.getValue().equals("")) {
						throw new WrongValueException(
								this.answerD,Labels.getLabel("FIELD_NO_EMPTY",new String[] { 
										Labels.getLabel("label_QuestionDialog_AnswerD.value") }));
					}
				}
			}

			aQuestion.setCorrectAnswer(this.correctAnswer.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuestion.setQuestionIsActive(this.questionIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aQuestion.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aQuestion
	 * @throws InterruptedException
	 */
	public void doShowDialog(Question aQuestion) throws InterruptedException {
		logger.debug("Entering");

		// if aQuestion == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aQuestion == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aQuestion = getQuestionService().getNewQuestion();

			setQuestion(aQuestion);
		} else {
			setQuestion(aQuestion);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aQuestion.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.questionDesc.focus();
		} else {
			this.questionDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aQuestion);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_QuestionDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_questionDesc = this.questionDesc.getValue();
		this.oldVar_answerA = this.answerA.getValue();
		this.oldVar_answerB = this.answerB.getValue();
		this.oldVar_answerC = this.answerC.getValue();
		this.oldVar_answerD = this.answerD.getValue();
		this.oldVar_correctAnswer = this.correctAnswer.getValue();
		this.oldVar_questionIsActive = this.questionIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.questionDesc.setValue(this.oldVar_questionDesc);
		this.answerA.setValue(this.oldVar_answerA);
		this.answerB.setValue(this.oldVar_answerB);
		this.answerC.setValue(this.oldVar_answerC);
		this.answerD.setValue(this.oldVar_answerD);
		this.correctAnswer.setValue(this.oldVar_correctAnswer);
		this.questionIsActive.setChecked(this.oldVar_questionIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();
		if (this.oldVar_questionDesc != this.questionDesc.getValue()) {
			return true;
		}
		if (this.oldVar_answerA != this.answerA.getValue()) {
			return true;
		}
		if (this.oldVar_answerB != this.answerB.getValue()) {
			return true;
		}
		if (this.oldVar_answerC != this.answerC.getValue()) {
			return true;
		}
		if (this.oldVar_answerD != this.answerD.getValue()) {
			return true;
		}
		if (this.oldVar_correctAnswer != this.correctAnswer.getValue()) {
			return true;
		}
		if (this.oldVar_questionIsActive != this.questionIsActive.isChecked()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.questionDesc.isReadonly()) {
			this.questionDesc.setConstraint("NO EMPTY:"+ Labels.getLabel(
					"FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_QuestionDialog_QuestionDesc.value") }));
		}
		if (!this.answerA.isReadonly()) {
			this.answerA.setConstraint("NO EMPTY:"+ Labels.getLabel(
					"FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_QuestionDialog_AnswerA.value") }));
		}
		if (!this.answerB.isReadonly()) {
			this.answerB.setConstraint("NO EMPTY:"+ Labels.getLabel(
					"FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_QuestionDialog_AnswerB.value") }));
		}
		/*
		 * if (!this.answerC.isReadonly()){
		 * this.answerC.setConstraint("NO EMPTY:" +
		 * Labels.getLabel("FIELD_NO_EMPTY",new
		 * String[]{Labels.getLabel("label_QuestionDialog_AnswerC.value")})); }
		 * if (!this.answerD.isReadonly()){
		 * this.answerD.setConstraint("NO EMPTY:" +
		 * Labels.getLabel("FIELD_NO_EMPTY",new
		 * String[]{Labels.getLabel("label_QuestionDialog_AnswerD.value")})); }
		 */
		if (!this.correctAnswer.isDisabled()) {
			this.correctAnswer.setConstraint(new StaticListValidator(listCorrectAnswer,
							Labels.getLabel("label_QuestionDialog_CorrectAnswer.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.questionDesc.setConstraint("");
		this.answerA.setConstraint("");
		this.answerB.setConstraint("");
		this.answerC.setConstraint("");
		this.answerD.setConstraint("");
		this.correctAnswer.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.questionDesc.setErrorMessage("");
		this.answerA.setErrorMessage("");
		this.answerB.setErrorMessage("");
		this.answerC.setErrorMessage("");
		this.answerD.setErrorMessage("");
		this.correctAnswer.setErrorMessage("");
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Question object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Question aQuestion = new Question();
		BeanUtils.copyProperties(getQuestion(), aQuestion);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aQuestion.getQuestionId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aQuestion.getRecordType()).equals("")) {
				aQuestion.setVersion(aQuestion.getVersion() + 1);
				aQuestion.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aQuestion.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aQuestion, tranType)) {
					refreshList();
					closeDialog(this.window_QuestionDialog, "Question");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Question object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final Question aQuestion = getQuestionService().getNewQuestion();
		setQuestion(aQuestion);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.questionDesc.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getQuestion().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.questionIsActive.setDisabled(true);
		} else {
			this.btnCancel.setVisible(true);
			this.questionIsActive.setDisabled(isReadOnly("QuestionDialog_questionIsActive"));
		}

		this.questionDesc.setReadonly(isReadOnly("QuestionDialog_questionDesc"));
		this.answerA.setReadonly(isReadOnly("QuestionDialog_answerA"));
		this.answerB.setReadonly(isReadOnly("QuestionDialog_answerB"));
		this.answerC.setReadonly(isReadOnly("QuestionDialog_answerC"));
		this.answerD.setReadonly(isReadOnly("QuestionDialog_answerD"));
		this.correctAnswer.setDisabled(isReadOnly("QuestionDialog_correctAnswer"));
		/*this.questionIsActive
				.setDisabled(isReadOnly("QuestionDialog_questionIsActive"));*/

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.question.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.questionDesc.setReadonly(true);
		this.answerA.setReadonly(true);
		this.answerB.setReadonly(true);
		this.answerC.setReadonly(true);
		this.answerD.setReadonly(true);
		this.correctAnswer.setDisabled(true);
		this.questionIsActive.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.questionDesc.setValue("");
		this.answerA.setValue("");
		this.answerB.setValue("");
		this.answerC.setValue("");
		this.answerD.setValue("");
		this.correctAnswer.setValue("");
		this.questionIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Question aQuestion = new Question();
		BeanUtils.copyProperties(getQuestion(), aQuestion);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Question object with the components data
		doWriteComponentsToBean(aQuestion);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aQuestion.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aQuestion.getRecordType()).equals("")) {
				aQuestion.setVersion(aQuestion.getVersion() + 1);
				if (isNew) {
					aQuestion.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aQuestion.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aQuestion.setNewRecord(true);
				}
			}
		} else {
			aQuestion.setVersion(aQuestion.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aQuestion, tranType)) {
				doWriteBeanToComponents(aQuestion);
				refreshList();
				closeDialog(this.window_QuestionDialog, "Question");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aQuestion
	 *            (Question)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Question aQuestion, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aQuestion.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aQuestion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aQuestion.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aQuestion.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aQuestion.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aQuestion);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aQuestion))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aQuestion.setTaskId(taskId);
			aQuestion.setNextTaskId(nextTaskId);
			aQuestion.setRoleCode(getRole());
			aQuestion.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aQuestion, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aQuestion);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aQuestion,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aQuestion, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Question aQuestion = (Question) auditHeader.getAuditDetail()
				.getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getQuestionService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getQuestionService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getQuestionService().doApprove(auditHeader);

						if (aQuestion.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getQuestionService().doReject(auditHeader);
						if (aQuestion.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_QuestionDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_QuestionDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for setting the selected answer in ComboBox. <br>
	 * 
	 * 
	 */
	private void setListCorrectAnswer(Combobox codeCombobox, String value) {
		logger.debug("Entering");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < listCorrectAnswer.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(listCorrectAnswer.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(listCorrectAnswer.get(i).getLabel()));
			codeCombobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(listCorrectAnswer.get(i).getValue()))) {
				codeCombobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aQuestion
	 *            (Question)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Question aQuestion, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aQuestion.getBefImage(), aQuestion);
		return new AuditHeader(String.valueOf(aQuestion.getQuestionId()), null,
				null, null, auditDetail, aQuestion.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_QuestionDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("Question");
		notes.setReference(String.valueOf(getQuestion().getQuestionId()));
		notes.setVersion(getQuestion().getVersion());
		return notes;
	}

	// Method for refreshing the list after successful update
	private void refreshList() {
		final JdbcSearchObject<Question> soQuestion = getQuestionListCtrl().getSearchObj();
		getQuestionListCtrl().pagingQuestionList.setActivePage(0);
		getQuestionListCtrl().getPagedListWrapper().setSearchObject(soQuestion);
		if (getQuestionListCtrl().listBoxQuestion != null) {
			getQuestionListCtrl().listBoxQuestion.getListModel();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Question getQuestion() {
		return this.question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}
	public QuestionService getQuestionService() {
		return this.questionService;
	}

	public void setQuestionListCtrl(QuestionListCtrl questionListCtrl) {
		this.questionListCtrl = questionListCtrl;
	}
	public QuestionListCtrl getQuestionListCtrl() {
		return this.questionListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Question getPrvQuestion() {
		return prvQuestion;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}
}
