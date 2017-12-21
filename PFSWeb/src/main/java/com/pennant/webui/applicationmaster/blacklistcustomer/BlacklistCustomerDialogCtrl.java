package com.pennant.webui.applicationmaster.blacklistcustomer;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class BlacklistCustomerDialogCtrl extends GFCBaseCtrl<BlackListCustomers> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BlacklistCustomerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */          
	protected Window window_BlacklistCustomerDialog; // autoWired

	protected Textbox 				custCIF; // autoWired
	protected Datebox 				custDOB; // autoWired
	protected Textbox 				custFName; // autoWired
	protected Textbox 				custLName; // autoWired
	protected Textbox 				custEID; // autoWired
	protected Textbox 				custPassport; // autoWired
	//protected Textbox 				custMobileNum; // autoWired
	//protected Textbox				phoneCountryCode; // autoWired						
	//protected Textbox				phoneAreaCode;	// autoWired									
	protected Textbox				custMobileNum;		// autoWired								
	protected ExtendedCombobox		employer; // autoWired
	protected ExtendedCombobox 		custNationality;
	protected Checkbox              custIsActive; 
	
	private transient boolean validationOn;
	
	// not auto wired Var's
	private BlackListCustomers blacklistCustomer; // overHanded per
	private transient BlacklistCustomerListCtrl blacklistCustomerListCtrl; // overHanded

	// ServiceDAOs / Domain Classes
	private transient BlacklistCustomerService blacklistCustomerService;

	/**
	 * default constructor.<br>
	 */
	public BlacklistCustomerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BlacklistCustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Currency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlacklistCustomerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BlacklistCustomerDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("blackListCustomer")) {
				this.blacklistCustomer = (BlackListCustomers) arguments
						.get("blackListCustomer");
				BlackListCustomers befImage = new BlackListCustomers();
				BeanUtils.copyProperties(this.blacklistCustomer, befImage);
				this.blacklistCustomer.setBefImage(befImage);

				setBlacklistCustomer(this.blacklistCustomer);
			} else {
				setBlacklistCustomer(null);
			}

			doLoadWorkFlow(this.blacklistCustomer.isWorkflow(),
					this.blacklistCustomer.getWorkflowId(),
					this.blacklistCustomer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BlacklistCustomerDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}	

			// READ OVERHANDED parameters !
			// we get the currencyListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete currency here.
			if (arguments.containsKey("blacklistCustomerListCtrl")) {
				setBlacklistCustomerListCtrl((BlacklistCustomerListCtrl) arguments
						.get("blacklistCustomerListCtrl"));
			} else {
				setBlacklistCustomerListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBlacklistCustomer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BlacklistCustomerDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.custCIF.setMaxlength(6);
		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custFName.setMaxlength(50);
		this.custLName.setMaxlength(50);
		this.custEID.setMaxlength(20);
		this.custPassport.setMaxlength(20);
		this.custMobileNum.setMaxlength(10);
		
		// Employer ExtendedCombobox
		this.employer.setInputAllowed(true);
		this.employer.setMandatoryStyle(true);
		this.employer.setModuleName("EmployerDetail");
		this.employer.setValueColumn("EmployerId");
		this.employer.setDescColumn("EmpName");
		this.employer.setValidateColumns(new String[] { "EmployerId" });
		this.employer.setMaxlength(5);
		
		// custNationality ExtendedCombobox
		this.custNationality.setInputAllowed(true);
		this.custNationality.setMaxlength(2);
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_blacklistCustomerList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_blacklistCustomerList_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_blacklistCustomerList_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_blacklistCustomerList_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
		MessageUtil.showHelpWindow(event, window_BlacklistCustomerDialog);
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.blacklistCustomer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCurrency
	 *            Currency
	 */
	public void doWriteBeanToComponents(BlackListCustomers aBlackListCustomers) {
		logger.debug("Entering ");
		
		this.custCIF.setValue(aBlackListCustomers.getCustCIF());
		this.custDOB.setValue(aBlackListCustomers.getCustDOB());
		this.custFName.setValue(aBlackListCustomers.getCustFName());
		this.custLName.setValue(aBlackListCustomers.getCustLName());
		this.custEID.setValue(PennantApplicationUtil.formatEIDNumber(aBlackListCustomers.getCustCRCPR()));
		this.custPassport.setValue(aBlackListCustomers.getCustPassportNo());
		this.custMobileNum.setValue(aBlackListCustomers.getMobileNumber());
		this.custNationality.setValue(aBlackListCustomers.getCustNationality());
		this.custNationality.setDescription(aBlackListCustomers.getLovDescNationalityDesc());
		this.employer.setValue(aBlackListCustomers.getEmployer());
		this.employer.setDescription(aBlackListCustomers.getLovDescEmpName());
		this.recordStatus.setValue(aBlackListCustomers.getRecordStatus());
		this.custIsActive.setChecked(aBlackListCustomers.isCustIsActive());

		if (aBlackListCustomers.isNew()
				|| (aBlackListCustomers.getRecordType() != null ? aBlackListCustomers
						.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
				this.custIsActive.setChecked(true);
				this.custIsActive.setDisabled(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCurrency
	 */
	public void doWriteComponentsToBean(BlackListCustomers finBlacklistCust) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			finBlacklistCust.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustDOB(this.custDOB.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustFName(this.custFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustLName(this.custLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.custEID.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustPassportNo(this.custPassport.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setMobileNumber(this.custMobileNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustNationality(this.custNationality
					.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setLovDescNationalityDesc(this.custNationality
					.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setEmployer(this.employer.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setLovDescEmpName(this.employer
					.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustIsActive(this.custIsActive.isChecked());
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

		finBlacklistCust.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	public void onChange$custEID(Event event){
		logger.debug("Entering");
		this.custEID.setValue(PennantApplicationUtil.formatEIDNumber(this.custEID.getValue()));
		logger.debug("Leaving");
	}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCurrency
	 * @throws Exception
	 */
	public void doShowDialog(BlackListCustomers aFinBlacklistCust)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinBlacklistCust.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFinBlacklistCust.getRecordType())) {
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
			doWriteBeanToComponents(aFinBlacklistCust);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BlacklistCustomerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		Date appStartDate = DateUtility.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		setValidationOn(true);

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_CustCIF.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.custDOB.isReadonly()) {
			this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustDOB.value"), true, startDate, appStartDate, false));
		}
		if (!this.custFName.isReadonly()) {
			this.custFName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_CustFName.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.custLName.isReadonly()) {
			this.custLName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_CustLName.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.custEID.isReadonly()) {
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.custEID.setConstraint(new PTStringValidator(Labels
						.getLabel("label_BlacklistCustomerDialog_CustEID.value"),
						PennantRegularExpressions.REGEX_PASSPORT, true));
			}else{
				this.custEID.setConstraint(new PTStringValidator(Labels
						.getLabel("label_BlacklistCustomerDialog_CustEID.value"),
						PennantRegularExpressions.REGEX_PANNUMBER, true));
			}
			
		}

		if (!this.custPassport.isReadonly()) {
			this.custPassport.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_CustPassport.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}
		if (!this.custMobileNum.isReadonly()) {
			this.custMobileNum.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"), true));
		}
		if (!this.custNationality.isReadonly()) {
			this.custNationality.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_CustNationality.value"),
					PennantRegularExpressions.REGEX_ALPHA, true));
		}
		if (!this.employer.isReadonly()) {
			this.employer.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BlacklistCustomerDialog_Employer.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custDOB.setConstraint("");
		this.custFName.setConstraint("");
		this.custLName.setConstraint("");
		this.custEID.setConstraint("");
		this.custPassport.setConstraint("");
		this.custMobileNum.setConstraint("");
		this.custNationality.setConstraint("");
		this.employer.setConstraint("");
		
		logger.debug("Leaving ");
	}
	
	/**
	 * Removes the Validation by setting the accordingly constraints to the
	 * LOVfields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custNationality.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		
		this.custCIF.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custFName.setErrorMessage("");
		this.custLName.setErrorMessage("");
		this.custEID.setErrorMessage("");
		this.custPassport.setErrorMessage("");
		this.custMobileNum.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.employer.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getBlacklistCustomerListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a Currency object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final BlackListCustomers aBlackListCustomers = new BlackListCustomers();
		BeanUtils.copyProperties(getBlacklistCustomer(), aBlackListCustomers);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_BlacklistCustomerDialog_CustCIF.value")+" : "+aBlackListCustomers.getCustCIF();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBlackListCustomers.getRecordType())) {
				aBlackListCustomers.setVersion(aBlackListCustomers.getVersion() + 1);
				aBlackListCustomers.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBlackListCustomers.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBlackListCustomers, tranType)) {
					refreshList();
					closeDialog();
				}
			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		
		if (getBlacklistCustomer().isNewRecord()) {
			this.custCIF.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custCIF.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custDOB.setDisabled(isReadOnly("BlacklistCustomerDialog_CustDOB"));
		this.custFName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustFName"));
		this.custLName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustLName"));
		this.custEID.setReadonly(isReadOnly("BlacklistCustomerDialog_CustEID"));
		this.custPassport.setReadonly(isReadOnly("BlacklistCustomerDialog_CustPassport"));
		this.custMobileNum.setReadonly(isReadOnly("BlacklistCustomerDialog_CustMobileNum"));
		this.custNationality.setReadonly(isReadOnly("BlacklistCustomerDialog_CustNationality"));
		this.employer.setReadonly(isReadOnly("BlacklistCustomerDialog_Employer"));
		this.custIsActive.setDisabled(isReadOnly("BlacklistCustomerDialog_CustIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.blacklistCustomer.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		
		this.custCIF.setReadonly(true);
		this.custDOB.setDisabled(true);
		this.custFName.setReadonly(true);
		this.custLName.setReadonly(true);
		this.custEID.setReadonly(true);
		this.custPassport.setReadonly(true);
		this.custMobileNum.setReadonly(true);
		this.custNationality.setReadonly(true);
		this.employer.setReadonly(true);
		this.custIsActive.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before

		this.custCIF.setValue("");
		this.custDOB.setText("");
		this.custFName.setValue("");
		this.custLName.setValue("");
		this.custEID.setText("");
		this.custPassport.setValue("");
		this.custMobileNum.setValue("");
		this.custNationality.setValue("");
		this.employer.setValue("");
		this.custIsActive.setChecked(false);
		
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final BlackListCustomers aFinBlklistCust = new BlackListCustomers();
		BeanUtils.copyProperties(getBlacklistCustomer(), aFinBlklistCust);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		// fill the Currency object with the components data
		doWriteComponentsToBean(aFinBlklistCust);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinBlklistCust.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinBlklistCust.getRecordType())) {
				aFinBlklistCust.setVersion(aFinBlklistCust.getVersion() + 1);
				if (isNew) {
					aFinBlklistCust.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinBlklistCust.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinBlklistCust.setNewRecord(true);
				}
			}
		} else {
			aFinBlklistCust.setVersion(aFinBlklistCust.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinBlklistCust, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCurrency
	 *            (Currency)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BlackListCustomers aFinBlacklistCust, String tranType) {
		logger.debug("Entering ");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinBlacklistCust.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinBlacklistCust.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinBlacklistCust.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinBlacklistCust.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinBlacklistCust.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinBlacklistCust);
				}

				if (isNotesMandatory(taskId, aFinBlacklistCust)) {
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

			aFinBlacklistCust.setTaskId(taskId);
			aFinBlacklistCust.setNextTaskId(nextTaskId);
			aFinBlacklistCust.setRoleCode(getRole());
			aFinBlacklistCust.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinBlacklistCust, tranType);

			String operationRefs = getServiceOperations(taskId, aFinBlacklistCust);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinBlacklistCust,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinBlacklistCust, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BlackListCustomers aFinBlklistCust = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getBlacklistCustomerService().delete(auditHeader);

						deleteNotes = true;
					} else {
						auditHeader = getBlacklistCustomerService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getBlacklistCustomerService().doApprove(auditHeader);

						if (aFinBlklistCust.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBlacklistCustomerService().doReject(auditHeader);
						if (aFinBlklistCust.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(this.window_BlacklistCustomerDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_BlacklistCustomerDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.blacklistCustomer), true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBlackListCustomers
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BlackListCustomers aBlackListCustomers, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aBlackListCustomers.getBefImage(), aBlackListCustomers);
		return new AuditHeader(String.valueOf(aBlackListCustomers.getId()), null, null,
				null, auditDetail, aBlackListCustomers.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BlacklistCustomerDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.blacklistCustomer);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.blacklistCustomer.getCustCIF());
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}


	public BlackListCustomers getBlacklistCustomer() {
		return blacklistCustomer;
	}

	public void setBlacklistCustomer(BlackListCustomers blacklistCustomer) {
		this.blacklistCustomer = blacklistCustomer;
	}

	public BlacklistCustomerListCtrl getBlacklistCustomerListCtrl() {
		return blacklistCustomerListCtrl;
	}

	public void setBlacklistCustomerListCtrl(
			BlacklistCustomerListCtrl blacklistCustomerListCtrl) {
		this.blacklistCustomerListCtrl = blacklistCustomerListCtrl;
	}

	public BlacklistCustomerService getBlacklistCustomerService() {
		return blacklistCustomerService;
	}

	public void setBlacklistCustomerService(
			BlacklistCustomerService blacklistCustomerService) {
		this.blacklistCustomerService = blacklistCustomerService;
	}

}
