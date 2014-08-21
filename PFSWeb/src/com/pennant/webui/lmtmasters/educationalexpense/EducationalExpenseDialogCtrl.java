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
 * FileName    		:  EducationalExpenseDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.educationalexpense;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.EducationalExpenseService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.webui.lmtmasters.educationalloan.EducationalLoanDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/EducationalExpense/educationalExpenseDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EducationalExpenseDialogCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = 5568767672970530001L;
	private final static Logger logger = Logger.getLogger(EducationalExpenseDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window          window_EducationalExpenseDialog;                    // autoWired
	protected ExtendedCombobox     eduExpDetail;                                       // autoWired
	protected CurrencyBox     eduExpAmount;                                       // autoWired
	protected Datebox         eduExpDate;                                         // autoWired
	protected Label           recordStatus;                                       // autoWired                                                   
	protected Radiogroup      userAction;                                         // autoWired    
	protected Groupbox        groupboxWf;                                         // autoWired    
	protected Row             statusRow;                                          // autoWired    
	protected Button          btnNew;                                             // autoWired
	protected Button          btnEdit;                                            // autoWired
	protected Button          btnDelete;                                          // autoWired
	protected Button          btnSave;                                            // autoWired
	protected Button          btnCancel;                                          // autoWired
	protected Button          btnClose;                                           // autoWired
	protected Button          btnHelp;                                            // autoWired
	protected Button          btnNotes;                                           // autoWired

	// not auto wired variables
	private EducationalExpense educationalExpense = null;                         // over handed per parameters
	private EducationalExpense prvEducationalExpense;                             // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.

	private transient long  		    oldVar_eduExpDetail;
	private transient BigDecimal  		oldVar_eduExpAmount;
	private transient Date  		    oldVar_eduExpDate;
	private transient String            oldVar_recordStatus;
	private transient boolean           validationOn;
	private boolean                     notes_Entered=false;
	// Button controller for the CRUD buttons
	private transient final String       btnCtroller_ClassPrefix = "button_EducationalExpenseDialog_";
	private transient ButtonStatusCtrl   btnCtrl;
	private transient String 		     oldVar_lovDescEduExpDetailName;
	// ServiceDAOs / Domain Classes
	private transient EducationalExpenseService      educationalExpenseService;
	private transient PagedListService               pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private EducationalLoanDialogCtrl                educationalLoanDialogCtrl = null;
	private EducationalLoan                          educationalLoan;
	@SuppressWarnings("unused")
	private boolean                                  isNewRecord=false;
	private List<EducationalExpense>                 educationalExpenseList;
	private transient int   ccyFormatter = 0;
	
	/**
	 * default constructor.<br>
	 */
	public EducationalExpenseDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected EducationalExpense object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EducationalExpenseDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("educationalExpense")) {
			this.educationalExpense = (EducationalExpense) args.get("educationalExpense");
			EducationalExpense befImage =new EducationalExpense();
			BeanUtils.copyProperties(this.educationalExpense, befImage);
			this.educationalExpense.setBefImage(befImage);

			setEducationalExpense(this.educationalExpense);
		} else {
			setEducationalExpense(null);
		}
		// READ OVERHANDED params !
		if (args.containsKey("educationalLoan")) {
			this.educationalLoan = (EducationalLoan) args.get("educationalLoan");
			setEducationalLoan(this.educationalLoan);
		}
		if(args.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)args.get("ccyFormatter");
		}

		if (args.containsKey("educationalLoanDialogCtrl")) {
			this.educationalLoanDialogCtrl = (EducationalLoanDialogCtrl) args.get("educationalLoanDialogCtrl");
			setEducationalLoanDialogCtrl(this.educationalLoanDialogCtrl);

			this.educationalExpense.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				setRole((String) args.get("roleCode"));
				getUserWorkspace().alocateRoleAuthorities(getRole(), "EducationalExpenseDialog");
			}
		} 

		doLoadWorkFlow(this.educationalExpense.isWorkflow(),this.educationalExpense.getWorkflowId(),this.educationalExpense.getNextTaskId());
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "EducationalExpenseDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(this.educationalExpense);
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_EducationalExpenseDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_EducationalExpenseDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * when "Notes" button clicked 
	 * @param event
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++  GUI Process ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEducationalExpense
	 * @throws InterruptedException
	 */
	public void doShowDialog(EducationalExpense aEducationalExpense) throws InterruptedException {
		logger.debug("Entering") ;

		// if aEducationalExpense == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aEducationalExpense == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aEducationalExpense = getEducationalExpenseService().getNewEducationalExpense();
			this.isNewRecord=true;
			setEducationalExpense(aEducationalExpense);
		} else {
			setEducationalExpense(aEducationalExpense);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aEducationalExpense.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.eduExpDetail.focus();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);

		}		try {
			// fill the components with the data
			doWriteBeanToComponents(aEducationalExpense);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_EducationalExpenseDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.eduExpDetail.setInputAllowed(false);
		this.eduExpDetail.setDisplayStyle(3);
        this.eduExpDetail.setMandatoryStyle(true);
		this.eduExpDetail.setModuleName("ExpenseType");
		this.eduExpDetail.setValueColumn("ExpenceTypeId");
		this.eduExpDetail.setDescColumn("ExpenceTypeName");
		this.eduExpDetail.setValidateColumns(new String[] { "ExpenceTypeId" });
		Filter[] filter = {new Filter("ExpenseFor", PennantConstants.EXPENSE_FOR_EDUCATION, Filter.OP_EQUAL)};
		this.eduExpDetail.setFilters(filter);
		this.eduExpAmount.setMaxlength(18);
		this.eduExpAmount.setMandatory(true);
		this.eduExpAmount.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		this.eduExpDate.setFormat(PennantConstants.dateFormat);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("EducationalExpenseDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}
	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEducationalExpense
	 *            EducationalExpense
	 */
	public void doWriteBeanToComponents(EducationalExpense aEducationalExpense) {
		logger.debug("Entering") ;
		this.eduExpDetail.setValue(String.valueOf(aEducationalExpense.getEduExpDetail()));
		this.eduExpAmount.setValue(PennantAppUtil.formateAmount(aEducationalExpense.getEduExpAmount(),
				this.ccyFormatter));
		this.eduExpDate.setValue(aEducationalExpense.getEduExpDate());
		
		if (aEducationalExpense.isNewRecord()){
			this.eduExpDetail.setDescription("");
		}else{
			this.eduExpDetail.setDescription(aEducationalExpense.getLovDescEduExpDetailName());
		}
		this.recordStatus.setValue(aEducationalExpense.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEducationalExpense
	 */
	public void doWriteComponentsToBean(EducationalExpense aEducationalExpense) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aEducationalExpense.setLovDescEduExpDetailName(this.eduExpDetail.getDescription());
			aEducationalExpense.setEduExpDetail(Long.valueOf(this.eduExpDetail.getValue()));	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.eduExpAmount.getValue()!=null){
				aEducationalExpense.setEduExpAmount(PennantAppUtil.unFormateAmount(this.eduExpAmount.getValue(),
						this.ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalExpense.setEduExpDate(this.eduExpDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			int i=DateUtility.compare(getEducationalLoan().getEduCommenceDate(),aEducationalExpense.getEduExpDate());
			if(i>0 ){
				throw new WrongValueException(
						this.eduExpDate,Labels.getLabel("DATE_RANGE",new String[]{
								Labels.getLabel("label_EducationalExpenseDialog_EduExpDate.value"),
								Labels.getLabel("label_EducationalLoanDialog_EduCommenceDate.value"),
								Labels.getLabel("label_EducationalLoanDialog_EduCompletionDate.value")}));
			}
			int j=DateUtility.compare(getEducationalLoan().getEduCompletionDate(),aEducationalExpense.getEduExpDate());
			if(j<0){
				throw new WrongValueException(
						this.eduExpDate,Labels.getLabel("DATE_RANGE",new String[]{
								Labels.getLabel("label_EducationalExpenseDialog_EduExpDate.value"),
								Labels.getLabel("label_EducationalLoanDialog_EduCommenceDate.value"),
								Labels.getLabel("label_EducationalLoanDialog_EduCompletionDate.value")}));
			}
		}

		catch (WrongValueException we ) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aEducationalExpense.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	/**
	 * Create a new EducationalExpense object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final EducationalExpense aEducationalExpense = getEducationalExpenseService().getNewEducationalExpense();
		setEducationalExpense(aEducationalExpense);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.eduExpDetail.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getEducationalExpense().isNewRecord()){
			this.btnCancel.setVisible(false);

		}else{
			this.btnCancel.setVisible(true);
		}

		this.eduExpDetail.setReadonly(isReadOnly("EducationalExpenseDialog_eduExpDetail"));
		this.eduExpAmount.setReadonly(isReadOnly("EducationalExpenseDialog_eduExpAmount"));
		this.eduExpDate.setDisabled(isReadOnly("EducationalExpenseDialog_eduExpDate"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.educationalExpense.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if (this.educationalExpense.isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EducationalExpense aEducationalExpense = new EducationalExpense();
		BeanUtils.copyProperties(getEducationalExpense(), aEducationalExpense);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerRating object with the components data
		doWriteComponentsToBean(aEducationalExpense);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aEducationalExpense.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aEducationalExpense.getRecordType()).equals("")){
				aEducationalExpense.setVersion(aEducationalExpense.getVersion()+1);
				if(isNew){
					aEducationalExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aEducationalExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEducationalExpense.setNewRecord(true);
				}
			}
		}else{
			/*set the tranType according to RecordType*/
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
				aEducationalExpense.setVersion(1);
				aEducationalExpense.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}

			if(StringUtils.trimToEmpty(aEducationalExpense.getRecordType()).equals("")){
				tranType =PennantConstants.TRAN_UPD;
				aEducationalExpense.setRecordType(PennantConstants.RCD_UPD);
			}
			if(aEducationalExpense.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(aEducationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				tranType =PennantConstants.TRAN_UPD;
			} 
		}
		try {
			AuditHeader auditHeader =  newEduExpenseDetailProcess(aEducationalExpense,tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_EducationalExpenseDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getEducationalLoanDialogCtrl().doFillExpenseDetailsList(this.educationalExpenseList);

				this.window_EducationalExpenseDialog.onClose();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Deletes a EducationalExpense object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");

		final EducationalExpense educationalExpense = new EducationalExpense();
		BeanUtils.copyProperties(getEducationalExpense(), educationalExpense);
		String tranType=PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + educationalExpense.getEduExpDetail();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(educationalExpense.getRecordType()).equals("")){
				educationalExpense.setVersion(educationalExpense.getVersion()+1);
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					educationalExpense.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(educationalExpense.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				educationalExpense.setVersion(educationalExpense.getVersion() + 1);
				educationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newEduExpenseDetailProcess(educationalExpense,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_EducationalExpenseDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getEducationalLoanDialogCtrl().doFillExpenseDetailsList(this.educationalExpenseList);

					this.window_EducationalExpenseDialog.onClose();
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		this.oldVar_eduExpDetail = Long.valueOf(this.eduExpDetail.getValue());
		this.oldVar_lovDescEduExpDetailName = this.eduExpDetail.getDescription();
		this.oldVar_eduExpAmount = this.eduExpAmount.getValue();
		this.oldVar_eduExpDate = this.eduExpDate.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.eduExpDetail.setValue(String.valueOf(this.oldVar_eduExpDetail));
		this.eduExpDetail.setDescription(this.oldVar_lovDescEduExpDetailName);
		this.eduExpAmount.setValue(this.oldVar_eduExpAmount);
		this.eduExpDate.setValue(this.oldVar_eduExpDate);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
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
		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_eduExpDetail != Long.valueOf(this.eduExpDetail.getValue())) {
			return true;
		}
		if (this.oldVar_eduExpAmount != this.eduExpAmount.getValue()) {
			return true;
		}
		String old_eduExpDate = "";
		String new_eduExpDate ="";
		if (this.oldVar_eduExpDate!=null){
			old_eduExpDate=DateUtility.formatDate(this.oldVar_eduExpDate,PennantConstants.dateFormat);
		}
		if (this.eduExpDate.getValue()!=null){
			new_eduExpDate=DateUtility.formatDate(this.eduExpDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_eduExpDate).equals(StringUtils.trimToEmpty(new_eduExpDate))) {
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

		if (!this.eduExpAmount.isReadonly()){
			this.eduExpAmount.setConstraint(new AmountValidator(18,0
					,Labels.getLabel("label_EducationalExpenseDialog_EduExpAmount.value"),false));
		}	
		if (!this.eduExpDate.isDisabled()){
			this.eduExpDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_EducationalExpenseDialog_EduExpDate.value")}));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.eduExpAmount.setConstraint("");
		this.eduExpDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.eduExpDetail.setReadonly(true);
		this.eduExpAmount.setReadonly(true);
		this.eduExpDate.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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

		this.eduExpDetail.setValue(String.valueOf(new Long(0)));
		this.eduExpDetail.setDescription("");
		this.eduExpAmount.setValue("");
		this.eduExpDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * closes the window
	 * @throws InterruptedException
	 */

	private void doClose() throws InterruptedException{
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
			.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("doClose isDataChanged : False");
		}

		if (close) {
			this.window_EducationalExpenseDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * This method added the EducationalExpense object into educationalExpenseList
	 *  by setting RecordType according to tranType
	 *  <p>eg: 	if(tranType==PennantConstants.TRAN_DEL){
	 *  	aEducationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
	 *  }</p>
	 * @param  aEducationalExpense (EducationalExpense)
	 * @param  tranType (String)
	 * @return auditHeader (AuditHeader)
	 */
	private AuditHeader newEduExpenseDetailProcess(EducationalExpense aEducationalExpense,String tranType){
		logger.debug("Entering ");
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aEducationalExpense, tranType);
		educationalExpenseList= new ArrayList<EducationalExpense>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aEducationalExpense.getLovDescEduExpDetailName();
		errParm[0] = PennantJavaUtil.getLabel("label_EducationalExpenseDialog_EduExpDetail.value") + ":"+valueParm[0];

		if(getEducationalLoanDialogCtrl().getEduExpenseDetailList()!=null 
				&& getEducationalLoanDialogCtrl().getEduExpenseDetailList().size()>0){
			for (int i = 0; i < getEducationalLoanDialogCtrl().getEduExpenseDetailList().size(); i++) {
				EducationalExpense educationalExpense = getEducationalLoanDialogCtrl().getEduExpenseDetailList().get(i);

				if( aEducationalExpense.getEduExpDetail()== educationalExpense.getEduExpDetail()){ 
					// Both Current and Existing list expense same
					/*if same educational expenses added twice set error detail*/
					if(getEducationalExpense().isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm)
								, getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aEducationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aEducationalExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							educationalExpenseList.add(aEducationalExpense);
						}
						else if(aEducationalExpense.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aEducationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aEducationalExpense.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							educationalExpenseList.add(aEducationalExpense);
						}else if(aEducationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getEducationalLoanDialogCtrl().getEduExpenseDetailList().size(); j++) {
								EducationalExpense eduExpense =  getEducationalLoanDialogCtrl().getEduExpenseDetailList().get(j);
								if( aEducationalExpense.getEduExpDetail()==aEducationalExpense.getEduExpDetail()){
									educationalExpenseList.add(eduExpense);
								}
							}
						}else if(aEducationalExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							aEducationalExpense.setNewRecord(true);
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD ){
							educationalExpenseList.add(educationalExpense);
						}
					}
				}else{
					educationalExpenseList.add(educationalExpense);
				}
			}
		}
		if(!recordAdded){
			educationalExpenseList.add(aEducationalExpense);
		}
		return auditHeader;
	} 
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_EducationalExpenseDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}
	/**
	 * This method returns new AuditHeader 
	 * @param aEducationalExpense
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(EducationalExpense aEducationalExpense, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEducationalExpense.getBefImage(), aEducationalExpense);   
		return new AuditHeader(String.valueOf(aEducationalExpense.getEduExpDetail())
				,null,null,null,auditDetail,aEducationalExpense.getUserDetails(),getOverideMap());
	}

	/**
	 * This method sets validation for LovFields
	 */
	private void doSetLOVValidation() {
		this.eduExpDetail.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_EducationalExpenseDialog_EduExpDetail.value")}));
	}
	/**
	 *  This method removes validation for LovFields
	 */
	private void doRemoveLOVValidation() {
		this.eduExpDetail.setConstraint("");
	}


	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.eduExpDetail.setErrorMessage("");
		this.eduExpAmount.setErrorMessage("");
		this.eduExpDate.setErrorMessage("");
		logger.debug("Leaving");
	}
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}
	// Get the notes entered for rejected reason
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("EducationalExpense");
		notes.setReference(String.valueOf(getEducationalExpense().getEduExpDetail()));
		notes.setVersion(getEducationalExpense().getVersion());
		return notes;
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

	public EducationalExpense getEducationalExpense() {
		return this.educationalExpense;
	}

	public void setEducationalExpense(EducationalExpense educationalExpense) {
		this.educationalExpense = educationalExpense;
	}

	public void setEducationalExpenseService(EducationalExpenseService educationalExpenseService) {
		this.educationalExpenseService = educationalExpenseService;
	}

	public EducationalExpenseService getEducationalExpenseService() {
		return this.educationalExpenseService;
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

	public EducationalExpense getPrvEducationalExpense() {
		return prvEducationalExpense;
	}

	public void setEducationalLoanDialogCtrl(EducationalLoanDialogCtrl educationalLoanDialogCtrl) {
		this.educationalLoanDialogCtrl = educationalLoanDialogCtrl;
	}

	public EducationalLoanDialogCtrl getEducationalLoanDialogCtrl() {
		return educationalLoanDialogCtrl;
	}

	public void setEducationalLoan(EducationalLoan educationalLoan) {
		this.educationalLoan = educationalLoan;
	}

	public EducationalLoan getEducationalLoan() {
		return educationalLoan;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
}
