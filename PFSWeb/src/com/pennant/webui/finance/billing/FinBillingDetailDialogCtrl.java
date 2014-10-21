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
 * FileName    		:  FinBillingDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Billing/FinBillingDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class FinBillingDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6959194080451993569L;
	private final static Logger logger = Logger.getLogger(FinBillingDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinBillingDetailDialog;// autowired

	protected Textbox 		finReference; 				// autowired
	protected Datebox 		progClaimDate; 				// autowired
	protected Decimalbox	progClaimAmount; 			// autowired
	protected Decimalbox	totalPercWork; 				// autowired

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired vars
	private FinBillingDetail finBillingDetail; 	// overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String 		oldVar_finReference;
	private transient Date 			oldVar_progClaimDate;
	private transient BigDecimal 	oldVar_progClaimAmount;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinBillingDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autowire
	protected Button btnEdit; 			// autowire
	protected Button btnDelete; 		// autowire
	protected Button btnSave; 			// autowire
	protected Button btnCancel; 		// autowire
	protected Button btnClose; 			// autowire
	protected Button btnHelp; 			// autowire
	protected Button btnNotes; 			// autowire

	private boolean newRecord=false;
	private boolean newBilling=false;
	
	private List<FinBillingDetail> billingDetails;
	private FinanceMainDialogCtrl  financeMainDialogCtrl;
	private String moduleType="";
	private BigDecimal balBillingAmount= BigDecimal.ZERO;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private  int formatter = 0;

	/**
	 * default constructor.<br>
	 */
	public FinBillingDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinBillingDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */	
	public void onCreate$window_FinBillingDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("finBillingDetail")) {
			this.finBillingDetail = (FinBillingDetail) args.get("finBillingDetail");
			FinBillingDetail befImage =new FinBillingDetail();
			BeanUtils.copyProperties(this.finBillingDetail, befImage);
			this.finBillingDetail.setBefImage(befImage);
			setFinBillingDetail(this.finBillingDetail);
		} else {
			setFinBillingDetail(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		
		if (args.containsKey("finAmount")) {
			this.finAmount = (BigDecimal) args.get("finAmount");
		}
		
		if (args.containsKey("balBillingAmount")) {
			this.balBillingAmount = (BigDecimal) args.get("balBillingAmount");
		}

		if(getFinBillingDetail().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("financeMainDialogCtrl")){

			setFinanceMainDialogCtrl((FinanceMainDialogCtrl) args.get("financeMainDialogCtrl"));
			setNewBilling(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			
			this.finBillingDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "FinBillingDetailDialog");
			}
			
			formatter = this.financeMainDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		}

		doLoadWorkFlow(this.finBillingDetail.isWorkflow(),this.finBillingDetail.getWorkflowId(),
				this.finBillingDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinBillingDetailDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinBillingDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		
		this.progClaimDate.setFormat(PennantConstants.dateFormat);
		this.progClaimAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totalPercWork.setFormat(PennantConstants.percentageFormate2);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("FinBillingDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinBillingDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinBillingDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinBillingDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinBillingDetailDialog_btnSave"));
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
	public void onClose$window_FinBillingDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
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
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinBillingDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


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
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeWindow();
		}
		logger.debug("Leaving");		
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewBilling()){
			window_FinBillingDetailDialog.onClose();	
		}else{
			closeDialog(this.window_FinBillingDetailDialog, "FinBillingDetail");
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
	 * @param aFinBillingDetail
	 *            FinBillingDetail
	 */
	public void doWriteBeanToComponents(FinBillingDetail aFinBillingDetail) {
		logger.debug("Entering");
		
		this.finReference.setValue(aFinBillingDetail.getFinReference());
		this.progClaimDate.setValue(aFinBillingDetail.getProgClaimDate());
		this.progClaimAmount.setValue(PennantAppUtil.formateAmount(
				aFinBillingDetail.getProgClaimAmount(),formatter));
		if(aFinBillingDetail.getProgClaimAmount() != null){
		this.totalPercWork.setValue((aFinBillingDetail.getProgClaimAmount().divide(finAmount,2,RoundingMode.HALF_DOWN))
				.multiply(new BigDecimal(100)));
		}else{
			this.totalPercWork.setValue(BigDecimal.ZERO);
		}

		this.recordStatus.setValue(aFinBillingDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinBillingDetail
	 */
	public void doWriteComponentsToBean(FinBillingDetail aFinBillingDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		int formatter = this.financeMainDialogCtrl.getFinanceDetail().getFinScheduleData()
								.getFinanceMain().getLovDescFinFormatter();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinBillingDetail.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinBillingDetail.setProgClaimDate(this.progClaimDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			BigDecimal claimAmt = PennantAppUtil.unFormateAmount(
					this.progClaimAmount.getValue(), formatter);
			
			if(claimAmt.compareTo(balBillingAmount) > 0){
				throw new WrongValueException(this.progClaimAmount, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
						new String[] { Labels.getLabel("label_FinBillingDetailDialog_ProgClaimAmount.value"),
						PennantAppUtil.amountFormate(balBillingAmount, formatter)}));
			}	
			
			aFinBillingDetail.setProgClaimAmount(claimAmt);
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinBillingDetail.setRecordStatus(this.recordStatus.getValue());
		setFinBillingDetail(aFinBillingDetail);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinBillingDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinBillingDetail aFinBillingDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.progClaimDate.focus();
		} else {
			this.progClaimAmount.focus();
			if (isNewBilling()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinBillingDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			if(isNewBilling()){
				this.window_FinBillingDetailDialog.setHeight("280px");
				this.window_FinBillingDetailDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_FinBillingDetailDialog.doModal() ;
			}else{
				this.window_FinBillingDetailDialog.setWidth("100%");
				this.window_FinBillingDetailDialog.setHeight("100%");
				setDialog(this.window_FinBillingDetailDialog);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
			this.window_FinBillingDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_progClaimDate = this.progClaimDate.getValue();
		this.oldVar_progClaimAmount = this.progClaimAmount.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setValue(this.oldVar_finReference);
		this.progClaimDate.setValue(this.oldVar_progClaimDate);
		this.progClaimAmount.setValue(this.oldVar_progClaimAmount);
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

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_progClaimDate != this.progClaimDate.getValue()) {
			return true;
		}
		if (this.oldVar_progClaimAmount != this.progClaimAmount.getValue()) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.progClaimAmount.isDisabled()) {
			this.progClaimAmount.setConstraint(new AmountValidator(18, formatter,
					Labels.getLabel("label_FinanceMainDialog_PreContrOrDeffCost.value"), false));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.progClaimDate.setConstraint("");
		this.progClaimAmount.setConstraint("");
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
		this.progClaimDate.setErrorMessage("");
		this.progClaimAmount.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinBillingDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinBillingDetail aFinBillingDetail = new FinBillingDetail();
		BeanUtils.copyProperties(getFinBillingDetail(), aFinBillingDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aFinBillingDetail.getProgClaimDate();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinBillingDetail.getRecordType()).equals("")){
				aFinBillingDetail.setVersion(aFinBillingDetail.getVersion()+1);
				aFinBillingDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinBillingDetail.setNewRecord(true);

				if (isWorkFlowEnabled()){
					aFinBillingDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isNewBilling()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newBillingProcess(aFinBillingDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinBillingDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getFinanceMainDialogCtrl().doFillFinBillingDetails(this.billingDetails);
						// send the data back to customer
						closeWindow();
					}	

				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinBillingDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		
		// setFocus
		this.progClaimDate.focus();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new FinBillingDetail() in the frontEnd.
		// we get it from the backEnd.
		final FinBillingDetail aFinBillingDetail = new FinBillingDetail();
		aFinBillingDetail.setNewRecord(true);
		setFinBillingDetail(aFinBillingDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (isNewRecord()){
			if(isNewBilling()){
				this.btnCancel.setVisible(false);	
			}
			this.progClaimDate.setDisabled(isReadOnly("FinBillingDetailDialog_progClaimDate"));
		}else{
			this.btnCancel.setVisible(true);
			this.progClaimDate.setDisabled(true);
		}
		
		this.finReference.setReadonly(true);
		this.progClaimAmount.setDisabled(isReadOnly("FinBillingDetailDialog_progClaimAmount"));
		this.totalPercWork.setDisabled(true);
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finBillingDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{

			if(newBilling){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newBilling);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewBilling()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.progClaimDate.setDisabled(true);
		this.progClaimAmount.setDisabled(true);
		this.totalPercWork.setDisabled(true);

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
		this.finReference.setValue("");
		this.progClaimDate.setText("");
		this.progClaimAmount.setText("");
		this.totalPercWork.setText("");
		logger.debug("Leaving");		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinBillingDetail aFinBillingDetail = new FinBillingDetail();
		BeanUtils.copyProperties(getFinBillingDetail(), aFinBillingDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinBillingDetail object with the components data
		doWriteComponentsToBean(aFinBillingDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinBillingDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinBillingDetail.getRecordType()).equals("")){
				aFinBillingDetail.setVersion(aFinBillingDetail.getVersion()+1);
				if(isNew){
					aFinBillingDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinBillingDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinBillingDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewBilling()){
				if(isNewRecord()){
					aFinBillingDetail.setVersion(1);
					aFinBillingDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aFinBillingDetail.getRecordType()).equals("")){
					aFinBillingDetail.setVersion(aFinBillingDetail.getVersion()+1);
					aFinBillingDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aFinBillingDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aFinBillingDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aFinBillingDetail.setVersion(aFinBillingDetail.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewBilling()){
				AuditHeader auditHeader =  newBillingProcess(aFinBillingDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinBillingDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceMainDialogCtrl().doFillFinBillingDetails(this.billingDetails);
					//true;
					// send the data back to customer
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newBillingProcess(FinBillingDetail aFinBillingDetail,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aFinBillingDetail, tranType);
		billingDetails = new ArrayList<FinBillingDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aFinBillingDetail.getFinReference();
		valueParm[1] = DateUtility.formateDate(aFinBillingDetail.getProgClaimDate(), PennantConstants.dateFormate);

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ProgClaimDate") + ":"+valueParm[1];

		if(getFinanceMainDialogCtrl().getBillingList() != null && getFinanceMainDialogCtrl().getBillingList().size()>0){
			for (int i = 0; i < getFinanceMainDialogCtrl().getBillingList().size(); i++) {
				FinBillingDetail finBillingDetail = getFinanceMainDialogCtrl().getBillingList().get(i);

				if(DateUtility.compare(finBillingDetail.getProgClaimDate(),aFinBillingDetail.getProgClaimDate()) == 0){ // Both Current and Existing list Date same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aFinBillingDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aFinBillingDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							billingDetails.add(aFinBillingDetail);
						}else if(aFinBillingDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aFinBillingDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinBillingDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							billingDetails.add(aFinBillingDetail);
						}else if(aFinBillingDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinanceMainDialogCtrl().getFinanceDetail().getFinBillingHeader().getBillingDetailList().size(); j++) {
								FinBillingDetail detail =  getFinanceMainDialogCtrl().getFinanceDetail().getFinBillingHeader().getBillingDetailList().get(j);
								if(DateUtility.compare(detail.getProgClaimDate(),aFinBillingDetail.getProgClaimDate()) == 0){
									billingDetails.add(detail);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							billingDetails.add(finBillingDetail);
						}
					}
				}else{
					billingDetails.add(finBillingDetail);
				}
			}
		}
		if(!recordAdded){
			billingDetails.add(aFinBillingDetail);
		}
		return auditHeader;
	} 
	
	public void onChange$progClaimAmount(Event event){
		logger.debug("Entering" + event.toString());
		if(this.progClaimAmount.getValue() != null || 
				this.progClaimAmount.getValue().compareTo(BigDecimal.ZERO) > 0){
			
			BigDecimal progClaimAmt = PennantAppUtil.unFormateAmount(this.progClaimAmount.getValue(), formatter);
			this.totalPercWork.setValue((progClaimAmt.divide(finAmount,2,RoundingMode.HALF_DOWN))
					.multiply(new BigDecimal(100)));
		}else{
			this.totalPercWork.setValue(BigDecimal.ZERO);
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinBillingDetail aFinBillingDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinBillingDetail.getBefImage(), aFinBillingDetail);

		return new AuditHeader(getReference(),String.valueOf(aFinBillingDetail.getFinReference()+"-"+aFinBillingDetail.getProgClaimDate()), null,
				null, auditDetail, aFinBillingDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinBillingDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("FinBillingDetail");
		notes.setReference(getReference());
		notes.setVersion(getFinBillingDetail().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	/** 
	 * Get the Reference value
	 */
	private String getReference(){
		return getFinBillingDetail().getFinReference()+PennantConstants.KEY_SEPERATOR +
					getFinBillingDetail().getProgClaimDate();
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

	public FinBillingDetail getFinBillingDetail() {
		return this.finBillingDetail;
	}
	public void setFinBillingDetail(FinBillingDetail customerRating) {
		this.finBillingDetail = customerRating;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewBilling() {
		return newBilling;
	}
	public void setNewBilling(boolean newBilling) {
		this.newBilling = newBilling;
	}

	public FinanceMainDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(FinanceMainDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

}
