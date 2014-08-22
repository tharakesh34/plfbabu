package com.pennant.webui.finance.financemain.stepfinance;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class FinStepPolicyDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4626382073313654611L;
	private final static Logger logger = Logger.getLogger(FinStepPolicyDetailDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_FinStepPolicyDialog; 	            // autoWired

   	protected Decimalbox   stepFinAmount; 					    // autoWired
	protected Intbox       stepNumber; 					        // autoWired
  	protected Decimalbox   tenorSplitPerc; 						// autoWired
  	protected Intbox       installments; 						// autoWired
  	protected Decimalbox   rateMargin; 						    // autoWired
	protected Decimalbox   eMIStepPerc; 						// autoWired
	protected Decimalbox   steppedEMI; 							// autoWired
	
	protected Label 		recordStatus; 						// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South			south;

	// not auto wired variables
	private FinanceStepPolicyDetail financeStepPolicyDetail; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient int  		    oldVar_stepNumber;
	private transient BigDecimal  	oldVar_tenorSplitPerc;
	private transient int  		    oldVar_installmensts;
	private transient BigDecimal  	oldVar_rateMargin;
	private transient BigDecimal  	oldVar_eMIStepPerc;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinStepPolicyDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
		
	private boolean newRecord=false;
	private boolean newFinStep=false;
	private StepDetailDialogCtrl stepDetailDialogCtrl;
	private List<FinanceStepPolicyDetail> finStepPolicyDetails;
	private String moduleType="";
	private String userRole="";
	private int ccyFormatter = 0;
	private double totTenorPerc = 0.00;
	private boolean alwDeletion = true;
	
	/**
	 * default constructor.<br>
	 */
	public FinStepPolicyDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinStepPolicyDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true,
				this.btnNew,this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("financeStepPolicyDetail")) {
			this.financeStepPolicyDetail = (FinanceStepPolicyDetail) args.get("financeStepPolicyDetail");
			FinanceStepPolicyDetail befImage =new FinanceStepPolicyDetail();
			BeanUtils.copyProperties(this.financeStepPolicyDetail, befImage);
			this.financeStepPolicyDetail.setBefImage(befImage);
			setFinanceStepPolicyDetail(this.financeStepPolicyDetail);
		} else {
			setFinanceStepPolicyDetail(null);
		}
	
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		
		if (args.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer)args.get("ccyFormatter");
		}
		
		if(args.containsKey("totTenorPerc")){
			this.totTenorPerc = (Double) args.get("totTenorPerc");
		}
		
		if(args.containsKey("alwDeletion")){
			this.alwDeletion = (Boolean) args.get("alwDeletion");
		}

		if(args.containsKey("stepDetailDialogCtrl")){

			setStepDetailDialogCtrl((StepDetailDialogCtrl) args.get("stepDetailDialogCtrl"));
			setNewFinStep(true);

			if(args.containsKey("newRecord")){
				setNewRecord((Boolean)arg.get("newRecord"));
			}else{
				setNewRecord(getFinanceStepPolicyDetail().isNewRecord());
			}
			this.financeStepPolicyDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "FinStepPolicyDetailDialog");
			}
		}
		
		doLoadWorkFlow(this.financeStepPolicyDetail.isWorkflow(),
				this.financeStepPolicyDetail.getWorkflowId(),this.financeStepPolicyDetail.getNextTaskId());
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinStepPolicyDetailDialog");
		}
	
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceStepPolicyDetail());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		//Empty sent any required attributes
		this.stepNumber.setMaxlength(2);
		this.tenorSplitPerc.setMaxlength(6);
		this.tenorSplitPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.installments.setMaxlength(3);
		this.eMIStepPerc.setMaxlength(6);
		this.eMIStepPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.rateMargin.setMaxlength(14);
		this.rateMargin.setFormat(PennantApplicationUtil.getRateFormate(9));
		this.steppedEMI.setMaxlength(18);
		this.steppedEMI.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.stepFinAmount.setMaxlength(18);
		this.stepFinAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().alocateAuthorities("FinStepPolicyDetailDialog",userRole);
		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		
		if(alwDeletion){
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinStepPolicyDetailDialog_btnDelete"));
		}else{
			this.btnDelete.setVisible(false);
		}
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinStepPolicyDetailDialog_btnSave"));
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
	public void onClose$window_CustomerEmploymentDetailDialog(Event event) throws Exception {
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_FinStepPolicyDialog);
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
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" +event.toString());
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
		if(close){
			closeWindow();
		}
		logger.debug("Leaving");
	}
	
	public void closeWindow() throws InterruptedException{
		closePopUpWindow(this.window_FinStepPolicyDialog, "FinStepPolicyDetailDialog");
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
	 * @param aFinStepPolicy
	 *            CustomerEmploymentDetail
	 */
	public void doWriteBeanToComponents(FinanceStepPolicyDetail aFinStepPolicy) {
		logger.debug("Entering");
		this.stepFinAmount.setValue(getStepDetailDialogCtrl().getFinScheduleData().getFinanceMain().getFinAmount());	
		this.stepNumber.setValue(aFinStepPolicy.getStepNo());
		this.tenorSplitPerc.setValue(aFinStepPolicy.getTenorSplitPerc());
		this.installments.setValue(aFinStepPolicy.getInstallments());
		this.eMIStepPerc.setValue(aFinStepPolicy.getEmiSplitPerc());
		this.steppedEMI.setValue(aFinStepPolicy.getSteppedEMI());
		this.rateMargin.setValue(aFinStepPolicy.getRateMargin());
		this.recordStatus.setValue(aFinStepPolicy.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinStepPolicy
	 */
	public void doWriteComponentsToBean(FinanceStepPolicyDetail aFinStepPolicy) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aFinStepPolicy.setStepNo(this.stepNumber.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}		
		try {
			aFinStepPolicy.setTenorSplitPerc(this.tenorSplitPerc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinStepPolicy.setInstallments(this.installments.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}		
		try {
	 		aFinStepPolicy.setEmiSplitPerc(this.eMIStepPerc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aFinStepPolicy.setSteppedEMI(PennantApplicationUtil.unFormateAmount(this.steppedEMI.getValue(), ccyFormatter));
		}catch (WrongValueException we ) {
			wve.add(we);
		}		
		try {
			aFinStepPolicy.setRateMargin(this.rateMargin.getValue());	
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

		aFinStepPolicy.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinStepPolicy
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceStepPolicyDetail aFinStepPolicy) throws InterruptedException {
		logger.debug("Entering");
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			this.stepNumber.focus();
			if (isNewFinStep()){
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
			doWriteBeanToComponents(aFinStepPolicy);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			doCheckEnquiry();
			
			this.window_FinStepPolicyDialog.setHeight("35%");
			this.window_FinStepPolicyDialog.setWidth("80%");
			this.groupboxWf.setVisible(false);
			this.window_FinStepPolicyDialog.doModal() ;
			
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.tenorSplitPerc.setDisabled(true);
			this.installments.setReadonly(true);
			this.eMIStepPerc.setDisabled(true);
			this.steppedEMI.setDisabled(true);
			this.rateMargin.setDisabled(true);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_stepNumber = this.stepNumber.intValue();
		this.oldVar_tenorSplitPerc = this.tenorSplitPerc.getValue();	
		this.oldVar_installmensts = this.installments.intValue();	
		this.oldVar_rateMargin = this.rateMargin.getValue();
 		this.oldVar_eMIStepPerc = this.eMIStepPerc.getValue();
 		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.stepNumber.setValue(this.oldVar_stepNumber);
		this.tenorSplitPerc.setValue(this.oldVar_tenorSplitPerc);
		this.installments.setValue(this.oldVar_installmensts);
		this.rateMargin.setValue(this.oldVar_rateMargin);
 		this.eMIStepPerc.setValue(this.oldVar_eMIStepPerc);
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
		if (this.oldVar_stepNumber != this.stepNumber.intValue()) {
			return true;
		}
		if (this.oldVar_tenorSplitPerc != this.tenorSplitPerc.getValue()) {
			return true;
		}
		if (this.oldVar_installmensts != this.installments.intValue()) {
			return true;
		}
		if (this.oldVar_rateMargin!= this.rateMargin.getValue()) {
			return true;
		}
		if (this.oldVar_eMIStepPerc != this.eMIStepPerc.getValue()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearMessage();
		setValidationOn(true);
		
		if(!this.stepNumber.isReadonly()){
			this.stepNumber.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinStepPolicyDialog_StepNumber.value"), true, false, 1, 100));
		}
		if(!this.tenorSplitPerc.isDisabled()){
			this.tenorSplitPerc.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinStepPolicyDialog_EMIStepPerc.value"),2, true, false, this.totTenorPerc));
		}
		if(!this.installments.isReadonly()){
			this.installments.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinStepPolicyDialog_Installments.value"), true, false));
		}
		if (!this.rateMargin.isDisabled()) {
			this.rateMargin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinStepPolicyDialog_RateMargin.value"),9,false,true,-9999,9999));
		}
		if(!this.eMIStepPerc.isDisabled()){
			this.eMIStepPerc.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinStepPolicyDialog_EMIStepPerc.value"),2, true, false, 999));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.stepNumber.setConstraint("");
		this.tenorSplitPerc.setConstraint("");
		this.installments.setConstraint("");
		this.rateMargin.setConstraint("");
		this.eMIStepPerc.setConstraint("");
		logger.debug("Leaving");
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
		this.stepNumber.setErrorMessage("");
		this.tenorSplitPerc.setErrorMessage("");
		this.installments.setErrorMessage("");
		this.rateMargin.setErrorMessage("");
		this.eMIStepPerc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerEmploymentDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final FinanceStepPolicyDetail aFinStepPolicy = new FinanceStepPolicyDetail();
		BeanUtils.copyProperties(getFinanceStepPolicyDetail(), aFinStepPolicy);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
									"\n\n --> " + aFinStepPolicy.getFinReference()+" with "+aFinStepPolicy.getStepNo();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinStepPolicy.getRecordType()).equals("")){
				aFinStepPolicy.setVersion(aFinStepPolicy.getVersion()+1);
				aFinStepPolicy.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getStepDetailDialogCtrl() != null &&  getStepDetailDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().isWorkflow()){
					aFinStepPolicy.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()){
					aFinStepPolicy.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewFinStep()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newFinStepPolicyProcess(aFinStepPolicy,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinStepPolicyDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getStepDetailDialogCtrl().doFillStepDetais(this.finStepPolicyDetails);
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
	 * Create a new CustomerEmploymentDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		this.stepFinAmount.setDisabled(true);
		if(isNewRecord()){
			this.stepNumber.setReadonly(false);
			this.tenorSplitPerc.setDisabled(isReadOnly("FinStepPolicyDetailDialog_TenorSplitPerc"));
			this.tenorSplitPerc.setDisabled(true);
		}else{
			this.stepNumber.setReadonly(true);
			this.tenorSplitPerc.setDisabled(true);
		}
		
		this.installments.setDisabled(isReadOnly("FinStepPolicyDetailDialog_Installmensts"));
		this.rateMargin.setDisabled(isReadOnly("FinStepPolicyDetailDialog_RateMargin"));
		this.eMIStepPerc.setReadonly(isReadOnly("FinStepPolicyDetailDialog_EMIStepPerc"));
		this.steppedEMI.setDisabled(true);
		logger.debug("Leaving ");
	}
	
	public boolean isReadOnly(String componentName){
		boolean isFinStepWorkflow = false;
		if(getStepDetailDialogCtrl() != null){
			isFinStepWorkflow = getStepDetailDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().isWorkflow();
		}
		if (isWorkFlowEnabled() || isFinStepWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.stepFinAmount.setReadonly(true);
		this.stepNumber.setReadonly(true);
		this.tenorSplitPerc.setDisabled(true);
		this.installments.setDisabled(true);
		this.rateMargin.setDisabled(true);
		this.eMIStepPerc.setReadonly(true);
		this.steppedEMI.setReadonly(true);
		
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
		this.tenorSplitPerc.setText("");
		this.installments.setText("");
	  	this.eMIStepPerc.setText("");
		this.eMIStepPerc.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceStepPolicyDetail aFinanceStepPolicyDetail = new FinanceStepPolicyDetail();
		BeanUtils.copyProperties(getFinanceStepPolicyDetail(), aFinanceStepPolicyDetail);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerEmploymentDetail object with the components data
		doWriteComponentsToBean(aFinanceStepPolicyDetail);
		
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aFinanceStepPolicyDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceStepPolicyDetail.getRecordType()).equals("")){
				aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion()+1);
				if(isNew){
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceStepPolicyDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewFinStep()){
				if(isNewRecord()){
					aFinanceStepPolicyDetail.setVersion(1);
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aFinanceStepPolicyDetail.getRecordType()).equals("")){
					aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion()+1);
					aFinanceStepPolicyDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aFinanceStepPolicyDetail.setVersion(aFinanceStepPolicyDetail.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}
		
		// save it to database
		try {
			if(isNewFinStep()){
				AuditHeader auditHeader =  newFinStepPolicyProcess(aFinanceStepPolicyDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinStepPolicyDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getStepDetailDialogCtrl().doFillStepDetais(this.finStepPolicyDetails);
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	
	private AuditHeader newFinStepPolicyProcess(FinanceStepPolicyDetail aFinanceStepPolicyDetail,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aFinanceStepPolicyDetail, tranType);
		finStepPolicyDetails = new ArrayList<FinanceStepPolicyDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aFinanceStepPolicyDetail.getFinReference());
		valueParm[1] = String.valueOf(aFinanceStepPolicyDetail.getStepNo());

		errParm[0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_StepNumber") + ":"+valueParm[1];

		if(getStepDetailDialogCtrl().getFinStepPoliciesList() != null && !getStepDetailDialogCtrl().getFinStepPoliciesList().isEmpty()){
			for (int i = 0; i < getStepDetailDialogCtrl().getFinStepPoliciesList().size(); i++) {
				FinanceStepPolicyDetail financeStepPolicyDetail = getStepDetailDialogCtrl().getFinStepPoliciesList().get(i);

				if(financeStepPolicyDetail.getStepNo() == aFinanceStepPolicyDetail.getStepNo()){ // Both Current and Existing list Steps same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						/*if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							finStepPolicyDetails.add(aFinanceStepPolicyDetail);
						}else if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinanceStepPolicyDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							finStepPolicyDetails.add(aFinanceStepPolicyDetail);
						}else if(aFinanceStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getStepDetailDialogCtrl().getFinScheduleData().getStepPolicyDetails().size(); j++) {
								FinanceStepPolicyDetail policyDetail =  getStepDetailDialogCtrl().getFinScheduleData().getStepPolicyDetails().get(j);
								if(policyDetail.getFinReference().equals(aFinanceStepPolicyDetail.getFinReference()) && 
										policyDetail.getStepNo() == aFinanceStepPolicyDetail.getStepNo()){
									finStepPolicyDetails.add(policyDetail);
								}
							}
						}*/
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							finStepPolicyDetails.add(financeStepPolicyDetail);
						}
					}
				}else{
					finStepPolicyDetails.add(financeStepPolicyDetail);
				}
			}
		}
		if(!recordAdded){
			finStepPolicyDetails.add(aFinanceStepPolicyDetail);
		}
		return auditHeader;
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
	private AuditHeader getAuditHeader(FinanceStepPolicyDetail aFinanceStepPolicyDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinanceStepPolicyDetail.getBefImage(), aFinanceStepPolicyDetail);

		return new AuditHeader(String.valueOf(aFinanceStepPolicyDetail.getFinReference())
				, String.valueOf(aFinanceStepPolicyDetail.getStepNo()), null,
				null, auditDetail, aFinanceStepPolicyDetail.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_FinStepPolicyDialog, auditHeader);
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
		Notes notes = new Notes();
		notes.setModuleName("FinanceStepPolicy");
		notes.setReference(String.valueOf(getFinanceStepPolicyDetail().getFinReference()));
		notes.setVersion(getFinanceStepPolicyDetail().getVersion());
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}
	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}

	public void setNewFinStep(boolean newFinStep) {
		this.newFinStep = newFinStep;
	}
	public boolean isNewFinStep() {
		return newFinStep;
	}
	
	public FinanceStepPolicyDetail getFinanceStepPolicyDetail() {
		return financeStepPolicyDetail;
	}
	public void setFinanceStepPolicyDetail(FinanceStepPolicyDetail financeStepPolicyDetail) {
		this.financeStepPolicyDetail = financeStepPolicyDetail;
	}

}
