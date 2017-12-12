package com.pennant.webui.applicationmaster.policecase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.PoliceCaseService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class PoliceCaseDialogCtrl extends GFCBaseCtrl<PoliceCaseDetail> {
	private static final long serialVersionUID = 5058430665774376406L;
	private static final Logger logger = Logger.getLogger(PoliceCaseDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 				window_PoliceCaseDialog; 		

	protected  Textbox 				policeCaseCustCIF; 	
	protected  Datebox				policeCaseCustDOB;
	protected  Textbox				policeCaseCustFName;
	protected  Textbox				policeCaseCustLName;
	protected  Uppercasebox			policeCaseCustEIDNumber;
	protected  Textbox  			policeCaseCustPassport;
	protected Textbox 				phoneCountryCode; 						
	protected Textbox 				phoneAreaCode; 	
	protected  Textbox				policeCaseCustMobileNumber;
	protected ExtendedCombobox 		policeCaseCustNationality; 			

	// not autoWired Var's
	private PoliceCaseDetail policeCaseDetail; 
	private transient PoliceCaseListCtrl policeCaseListCtrl; 

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PoliceCaseService policeCaseService;

	/**
	 * default constructor.<br>
	 */
	public PoliceCaseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PoliceCaseCustomersDialog";
	}


	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected PoliceCase object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PoliceCaseDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PoliceCaseDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("policeCaseDetail")) {
				this.policeCaseDetail = (PoliceCaseDetail) arguments
						.get("policeCaseDetail");
				PoliceCaseDetail befImage = new PoliceCaseDetail();
				BeanUtils.copyProperties(this.policeCaseDetail, befImage);
				this.policeCaseDetail.setBefImage(befImage);

				setPoliceCaseDetail(this.policeCaseDetail);
			} else {
				setPoliceCaseDetail(null);
			}

			doLoadWorkFlow(this.policeCaseDetail.isWorkflow(),
					this.policeCaseDetail.getWorkflowId(),
					this.policeCaseDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"PoliceCaseCustomersDialog");
			}

			// READ OVERHANDED parameters !
			// we get the PoliceCaseListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete PoliceCaseCustomers here.
			if (arguments.containsKey("policeCaseListCtrl")) {
				setPoliceCaseListCtrl((PoliceCaseListCtrl) arguments
						.get("policeCaseListCtrl"));
			} else {
				setPoliceCaseListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPoliceCaseDetail());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PoliceCaseDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
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
		getUserWorkspace().allocateAuthorities("PoliceCaseCustomersDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PoliceCaseCustomersDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PoliceCaseCustomersDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PoliceCaseCustomersDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PoliceCaseCustomersDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.policeCaseCustCIF.setMaxlength(6);
		this.policeCaseCustFName.setMaxlength(50);
		this.policeCaseCustLName.setMaxlength(50);
		this.policeCaseCustDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.policeCaseCustEIDNumber.setMaxlength(LengthConstants.LEN_EID);
		this.policeCaseCustPassport.setMaxlength(20);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneAreaCode.setMaxlength(3);
		this.policeCaseCustMobileNumber.setMaxlength(8);
		this.policeCaseCustNationality.setMaxlength(2);
		this.policeCaseCustNationality.setTextBoxWidth(121);
		this.policeCaseCustNationality.setMandatoryStyle(true);
		this.policeCaseCustNationality.setModuleName("NationalityCode");
		this.policeCaseCustNationality.setValueColumn("NationalityCode");
		this.policeCaseCustNationality.setDescColumn("NationalityDesc");
		this.policeCaseCustNationality.setValidateColumns(new String[] { "NationalityCode" });
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
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
		MessageUtil.showHelpWindow(event, window_PoliceCaseDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
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
		doShowNotes(this.policeCaseDetail);
	}

	public void onChange$policeCaseCustEIDNumber(Event event){
			logger.debug("Entering"+event.toString());
			this.policeCaseCustEIDNumber.setValue(PennantApplicationUtil.formatEIDNumber(this.policeCaseCustEIDNumber.getValue()));
			logger.debug("Leaving"+event.toString());
		}
		
		
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPoliceCaseDetail
	 * @throws Exception
	 */
	public void doShowDialog(PoliceCaseDetail aPoliceCaseDetail) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aPoliceCaseDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.policeCaseCustCIF.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.policeCaseCustFName.focus();
				if (StringUtils.isNotBlank(aPoliceCaseDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aPoliceCaseDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PoliceCaseDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getPoliceCaseDetail().isNewRecord()) {
			this.policeCaseCustCIF.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.policeCaseCustCIF.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.policeCaseCustDOB.setDisabled(isReadOnly("PoliceCaseCustomersDialog_custDOB"));
		this.policeCaseCustEIDNumber.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custEIDNumber"));
		this.policeCaseCustFName.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custFName"));
		this.policeCaseCustLName.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custLName"));
		this.policeCaseCustMobileNumber.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custMobileNumber"));
		this.phoneAreaCode.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custMobileNumber"));
		this.phoneCountryCode.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custMobileNumber"));
		this.policeCaseCustPassport.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custPassportNo"));
		this.policeCaseCustNationality.setReadonly(isReadOnly("PoliceCaseCustomersDialog_custNationality"));
		

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.policeCaseDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}	


	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.policeCaseCustCIF.setReadonly(true);
		this.policeCaseCustLName.setReadonly(true);
		this.policeCaseCustFName.setReadonly(true);
		this.policeCaseCustEIDNumber.setReadonly(true);
		this.policeCaseCustMobileNumber.setReadonly(true);
		this.policeCaseCustPassport.setReadonly(true);
		this.policeCaseCustNationality.setReadonly(true);
		this.policeCaseCustDOB.setDisabled(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);

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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		Date appStartDate = DateUtility.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		setValidationOn(true);

		if (!this.policeCaseCustCIF.isReadonly()) {
			this.policeCaseCustCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_PoliceCaseDialog_CustCIF.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.policeCaseCustDOB.isReadonly()) {
			this.policeCaseCustDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_PoliceCaseDialog_CustDOB.value"),true,startDate,appStartDate,false));
		}
		if (!this.policeCaseCustFName.isReadonly()) {
			this.policeCaseCustFName.setConstraint(new PTStringValidator(Labels.getLabel("label_PoliceCaseDialog_CustFName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.policeCaseCustLName.isReadonly()) {
			this.policeCaseCustLName.setConstraint(new PTStringValidator(Labels.getLabel("label_PoliceCaseDialog_CustLName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if(!this.phoneCountryCode.isReadonly()){
			this.phoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_PoliceCaseDialog_mobileCountryCode.value"),true,1));
		}
		if(!this.phoneAreaCode.isReadonly()){
			this.phoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_PoliceCaseDialog_mobileAreaCode.value"),true,2));
		}
		if (!this.policeCaseCustMobileNumber.isReadonly()){
			this.policeCaseCustMobileNumber.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_PoliceCaseDialog_CustMobileNumber.value"),true,3));
		}
		if (!this.policeCaseCustPassport.isReadonly()) {
			this.policeCaseCustPassport.setConstraint(new PTStringValidator(Labels.getLabel("label_PoliceCaseDialog_CustPassport.value"),null,true));
		}
		
		if (!this.policeCaseCustEIDNumber.isReadonly()) {
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.policeCaseCustEIDNumber.setConstraint(new PTStringValidator(Labels
						.getLabel("label_PoliceCaseDialog_CustEIDNumber.value"),
						PennantRegularExpressions.REGEX_EIDNUMBER, true));
			}else{
				this.policeCaseCustEIDNumber.setConstraint(new PTStringValidator(Labels
						.getLabel("label_PoliceCaseDialog_CustEIDNumber.value"),
						PennantRegularExpressions.REGEX_PANNUMBER, true));
			}
		}
		logger.debug("Leaving"); 
		
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.policeCaseCustCIF.setConstraint("");
		this.policeCaseCustDOB.setConstraint("");
		this.policeCaseCustEIDNumber.setConstraint("");
		this.policeCaseCustFName.setConstraint("");
		this.policeCaseCustLName.setConstraint("");
		this.policeCaseCustMobileNumber.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.policeCaseCustNationality.setConstraint("");
		this.policeCaseCustPassport.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Sets the Validation by setting the accordingly constraints to the
	 * LOVfields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if(!this.policeCaseCustNationality.isReadonly()){
			this.policeCaseCustNationality.setConstraint(new PTStringValidator(Labels.getLabel("label_PoliceCaseDialog_CustNationality.value"), null, true,true));
		}
	}
	/**
	 * Removes the Validation by setting the accordingly constraints to the
	 * LOVfields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.policeCaseCustNationality.setConstraint("");
	}
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.policeCaseCustCIF.setErrorMessage("");
		this.policeCaseCustDOB.setErrorMessage("");
		this.policeCaseCustEIDNumber.setErrorMessage("");
		this.policeCaseCustFName.setErrorMessage("");
		this.policeCaseCustLName.setErrorMessage("");
		this.policeCaseCustMobileNumber.setErrorMessage("");
		this.policeCaseCustPassport.setErrorMessage("");
		this.policeCaseCustNationality.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.policeCaseCustCIF.setValue("");
		this.policeCaseCustDOB.setText("");
		this.policeCaseCustEIDNumber.setValue("");
		this.policeCaseCustFName.setValue("");
		this.policeCaseCustLName.setValue("");
		this.policeCaseCustMobileNumber.setValue("");
		this.policeCaseCustPassport.setValue("");
		this.policeCaseCustNationality.setValue("");
		this.phoneAreaCode.setValue("");
		this.phoneCountryCode.setValue("");
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
		doWriteBeanToComponents(this.policeCaseDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPoliceCaseDetail
	 *            PoliceCaseDetail
	 */
	public void doWriteBeanToComponents(PoliceCaseDetail aPoliceCaseDetail) {
		logger.debug("Entering");
		this.policeCaseCustCIF.setValue(aPoliceCaseDetail.getCustCIF());
		this.policeCaseCustDOB.setValue(aPoliceCaseDetail.getCustDOB());
		this.policeCaseCustFName.setValue(aPoliceCaseDetail.getCustFName());
		this.policeCaseCustLName.setValue(aPoliceCaseDetail.getCustLName());
		this.policeCaseCustEIDNumber.setValue( PennantApplicationUtil.formatEIDNumber(aPoliceCaseDetail.getCustCRCPR()));
		this.policeCaseCustPassport.setValue(aPoliceCaseDetail.getCustPassportNo());
		String[]phone = PennantApplicationUtil.unFormatPhoneNumber(aPoliceCaseDetail.getMobileNumber());
		this.phoneCountryCode.setValue(phone[0]);
		this.phoneAreaCode.setValue(phone[1]);
		this.policeCaseCustMobileNumber.setValue(phone[2]);
		this.policeCaseCustNationality.setValue(aPoliceCaseDetail.getCustNationality());
		this.policeCaseCustNationality.setDescription(StringUtils.isBlank(aPoliceCaseDetail.getLovDescNationalityDesc())? "" : aPoliceCaseDetail.getLovDescNationalityDesc());
		this.recordStatus.setValue(aPoliceCaseDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPoliceCaseDetail
	 */
	public void doWriteComponentsToBean(PoliceCaseDetail aPoliceCaseDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPoliceCaseDetail.setCustCIF(this.policeCaseCustCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustDOB(this.policeCaseCustDOB.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustFName(this.policeCaseCustFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustLName(this.policeCaseCustLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.policeCaseCustEIDNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustPassportNo(this.policeCaseCustPassport.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setMobileNumber(PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(),this.policeCaseCustMobileNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPoliceCaseDetail.setCustNationality(this.policeCaseCustNationality.getValidatedValue());
			aPoliceCaseDetail.setLovDescNationalityDesc(this.policeCaseCustNationality.getDescription());
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

		aPoliceCaseDetail.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPoliceCaseDetail
	 *            (PoliceCaseDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PoliceCaseDetail aPoliceCaseDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPoliceCaseDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPoliceCaseDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPoliceCaseDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPoliceCaseDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPoliceCaseDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPoliceCaseDetail);
				}

				if (isNotesMandatory(taskId, aPoliceCaseDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aPoliceCaseDetail.setTaskId(taskId);
			aPoliceCaseDetail.setNextTaskId(nextTaskId);
			aPoliceCaseDetail.setRoleCode(getRole());
			aPoliceCaseDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPoliceCaseDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aPoliceCaseDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPoliceCaseDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPoliceCaseDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final PoliceCaseDetail aPoliceCaseDetail = new PoliceCaseDetail();
		BeanUtils.copyProperties(getPoliceCaseDetail(), aPoliceCaseDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PoliceCaseDetail object with the components data
		doWriteComponentsToBean(aPoliceCaseDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPoliceCaseDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPoliceCaseDetail.getRecordType())) {
				aPoliceCaseDetail.setVersion(aPoliceCaseDetail.getVersion() + 1);
				if (isNew) {
					aPoliceCaseDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPoliceCaseDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPoliceCaseDetail.setNewRecord(true);
				}
			}
		} else {
			aPoliceCaseDetail.setVersion(aPoliceCaseDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aPoliceCaseDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}




	/**
	 * Get Audit Header Details
	 * 
	 * @param aPoliceCaseDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(PoliceCaseDetail aPoliceCaseDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPoliceCaseDetail.getBefImage(), aPoliceCaseDetail);
		return new AuditHeader(String.valueOf(getPoliceCaseDetail().getId()), null, null,
				null, auditDetail, aPoliceCaseDetail.getUserDetails(), getOverideMap());
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
		PoliceCaseDetail aPoliceCaseDetail = (PoliceCaseDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPoliceCaseService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPoliceCaseService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPoliceCaseService().doApprove(auditHeader);

						if (aPoliceCaseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPoliceCaseService().doReject(auditHeader);

						if (aPoliceCaseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PoliceCaseDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PoliceCaseDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.policeCaseDetail), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getPoliceCaseListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.policeCaseDetail.getId());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_PoliceCaseDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}
	
	
	
	/**
	 * Deletes a PoliceCaseDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final PoliceCaseDetail aPoliceCaseDetail = new PoliceCaseDetail();
		BeanUtils.copyProperties(getPoliceCaseDetail(), aPoliceCaseDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_PoliceCaseDialog_CustCIF.value")+" : "+aPoliceCaseDetail.getCustCIF();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aPoliceCaseDetail.getRecordType())) {
				aPoliceCaseDetail.setVersion(aPoliceCaseDetail.getVersion() + 1);
				aPoliceCaseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPoliceCaseDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPoliceCaseDetail, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public PoliceCaseService getPoliceCaseService() {
		return policeCaseService;
	}

	public void setPoliceCaseService(PoliceCaseService policeCaseService) {
		this.policeCaseService = policeCaseService;
	}

	public PoliceCaseListCtrl getPoliceCaseListCtrl() {
		return policeCaseListCtrl;
	}

	public void setPoliceCaseListCtrl(PoliceCaseListCtrl policeCaseListCtrl) {
		this.policeCaseListCtrl = policeCaseListCtrl;
	}

	public PoliceCaseDetail getPoliceCaseDetail() {
		return policeCaseDetail;
	}

	public void setPoliceCaseDetail(PoliceCaseDetail policeCaseDetail) {
		this.policeCaseDetail = policeCaseDetail;
	}
}
