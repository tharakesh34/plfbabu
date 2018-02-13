package com.pennant.webui.applicationmaster.returnedcheque;

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

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.service.applicationmaster.ReturnedChequeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/ReturnedCheque/returnedChequeDialog.zul
 * file.
 */
public class ReturnedChequeDialogCtrl extends GFCBaseCtrl<ReturnedChequeDetails> {
	private static final long serialVersionUID = -210929672381582779L;
	private static Logger logger = Logger
			.getLogger(ReturnedChequeDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReturnedChequeDialog; // autoWired

	protected ExtendedCombobox custCIF; // autoWired
	protected ExtendedCombobox currency; // autoWired
	protected CurrencyBox amount; // autoWired
	protected Textbox returnReason; // autoWired
	protected Datebox returnDate;
	protected Uppercasebox chequeNo;

	// not autoWired Var's
	private ReturnedChequeDetails returnedCheque; // overHanded per parameter
	private transient ReturnedChequeListCtrl returnedChequeListCtrl; // overHanded per parameter

	private int ccyFormatter = 0;
	
	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ReturnedChequeService returnedChequeService;

	/**
	 * default constructor.<br>
	 */
	public ReturnedChequeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReturnedChequeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReturnedCheque object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReturnedChequeDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReturnedChequeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("returnedCheque")) {
				this.returnedCheque = (ReturnedChequeDetails) arguments
						.get("returnedCheque");
				ReturnedChequeDetails befImage = new ReturnedChequeDetails();
				BeanUtils.copyProperties(this.returnedCheque, befImage);
				this.returnedCheque.setBefImage(befImage);

				setReturnedCheque(this.returnedCheque);
			} else {
				setReturnedCheque(null);
			}

			doLoadWorkFlow(this.returnedCheque.isWorkflow(),
					this.returnedCheque.getWorkflowId(),
					this.returnedCheque.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"ReturnedChequeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the returnedChequeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete returnedCheque here.
			if (arguments.containsKey("returnedChequeListCtrl")) {
				setReturnedChequeListCtrl((ReturnedChequeListCtrl) arguments
						.get("returnedChequeListCtrl"));
			} else {
				setReturnedChequeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getReturnedCheque());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReturnedChequeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.custCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.chequeNo.setMaxlength(50);
		this.returnDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custCIF.setMandatoryStyle(true);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		Filter coreCustFilter[] = new Filter[1];
		coreCustFilter[0] = new Filter("CustCoreBank"," ",Filter.OP_NOT_EQUAL);
		this.custCIF.setFilters(coreCustFilter);
		this.currency.setMaxlength(3);
		this.currency.setMandatoryStyle(true);
		this.currency.setModuleName("Currency");
		this.currency.setValueColumn("CcyCode");
		this.currency.setDescColumn("CcyDesc");
		this.currency.setValidateColumns(new String[] { "CcyCode" });
		this.currency.setTextBoxWidth(145);
		this.amount.setMandatory(true);
		 this.amount.setFormat(PennantApplicationUtil.getAmountFormate(
				  ccyFormatter)); 
		  this.amount.setScale(ccyFormatter);
		 this.custCIF.setTextBoxWidth(145);
		this.amount.setTextBoxWidth(150);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		this.returnReason.setMaxlength(100);
		logger.debug("Leaving ");
	}

	public void onFulfill$currency(Event event) {
		logger.debug("Entering");

		this.currency.setConstraint("");
		Object dataObject = currency.getObject();

		if (dataObject instanceof String) {
			this.currency.setValue(dataObject.toString());
			this.currency.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.currency.setValue(details.getCcyCode());
				this.currency.setDescription(details.getCcyDesc());
				ccyFormatter = details.getCcyEditField();
				doSetFieldProperties();
			}
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
		logger.debug("Entering ");

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_ReturnedChequeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_ReturnedChequeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_ReturnedChequeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_ReturnedChequeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_ReturnedChequeDialog);
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
		doWriteBeanToComponents(this.returnedCheque.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	private void doWriteBeanToComponents(ReturnedChequeDetails aReturnedCheque) {
		logger.debug("Entering");
		
		this.returnDate.setValue(aReturnedCheque.getReturnDate());
		this.returnReason.setValue(aReturnedCheque.getReturnReason());
		this.custCIF.setValue(aReturnedCheque.getCustCIF());
		this.chequeNo.setValue(aReturnedCheque.getChequeNo());
		this.currency.setValue(aReturnedCheque.getCurrency());
		if(aReturnedCheque.isNewRecord()){
			ccyFormatter = 2;
			this.currency.setDescription("");
			this.custCIF.setDescription("");
			this.amount.setValue(PennantAppUtil.formateAmount(aReturnedCheque.getAmount(),ccyFormatter));
		}else{
			this.custCIF.setDescription(aReturnedCheque.getCustShrtName());
			this.currency.setDescription(aReturnedCheque.getCcyDesc());
			ccyFormatter = aReturnedCheque.getCcyEditField();
			this.amount.setValue(PennantAppUtil.formateAmount(aReturnedCheque.getAmount(),ccyFormatter));
			/*this.amount.setFormat(PennantApplicationUtil.getAmountFormate(
					  aReturnedCheque.getCcyEditField())); 
			  this.amount.setScale(aReturnedCheque.getCcyEditField())*/;
			/*this.amount.setValue(PennantAppUtil.formateAmount(aReturnedCheque.getAmount(),aReturnedCheque.getCcyEditField()));*/
		}
		this.amount.setFormat(PennantApplicationUtil.getAmountFormate(
				  ccyFormatter)); 
		  this.amount.setScale(ccyFormatter);
		this.recordStatus.setValue(aReturnedCheque.getRecordStatus());
		logger.debug("Leaving");

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReturendCheque
	 */
	public void doWriteComponentsToBean(ReturnedChequeDetails aReturnedCheque) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReturnedCheque.setCurrency(this.currency.getValidatedValue());
			aReturnedCheque.setCcyDesc(this.currency.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aReturnedCheque.setCustCIF(this.custCIF.getValidatedValue());
			aReturnedCheque.setCustShrtName(this.custCIF.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aReturnedCheque.setAmount(PennantApplicationUtil.unFormateAmount(this.amount.getValidateValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReturnedCheque.setChequeNo(this.chequeNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReturnedCheque.setReturnDate(this.returnDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReturnedCheque.setReturnReason(this.returnReason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aReturnedCheque.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aReturnedCheque
	 * 
	 * @throws Exception
	 */
	public void doShowDialog(ReturnedChequeDetails aReturnedCheque)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aReturnedCheque.isNew()) {
			this.custCIF.setVisible(true);
			this.chequeNo.setVisible(true);
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.setReadonly(true);
			this.chequeNo.setReadonly(true);
			this.returnDate.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aReturnedCheque.getRecordType())) {
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
			doWriteBeanToComponents(aReturnedCheque);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_ReturnedChequeDialog.onClose();
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
		setValidationOn(true);
		Date appStartDate = DateUtility.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		if (!this.returnReason.isReadonly()) {
			this.returnReason.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ReturnedChequeDialog_ReturnReason.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.chequeNo.isReadonly()) {
				this.chequeNo.setConstraint(new PTStringValidator(Labels
						.getLabel("label_ReturnedChequeDialog_ChequeNo.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true,6,50));
			
		}
		if (!this.returnDate.isDisabled()) {
			this.returnDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_ReturnedChequeDialog_ReturnDate.value"),
					true,startDate,appStartDate,false));

		}
		if (!this.amount.isDisabled()) {
			this.amount.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_ReturnedChequeDialog_Amount.value"), 0, true, true));
		}
		if (!this.currency.isReadonly()) {
			this.currency.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ReturnedChequeDialog_Currency.value"),
					null, true, true));
		}
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ReturnedChequeDialog_CustCIF.value"),
					null, true, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.chequeNo.setConstraint("");
		this.amount.setConstraint("");
		this.custCIF.setConstraint("");
		this.returnDate.setConstraint("");
		this.returnReason.setConstraint("");
		this.currency.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");

		this.amount.setErrorMessage("");
		this.chequeNo.setErrorMessage("");
		this.currency.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.returnDate.setErrorMessage("");
		this.returnReason.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getReturnedChequeListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a ReturnedCheque object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final ReturnedChequeDetails aReturnedCheque = new ReturnedChequeDetails();
		BeanUtils.copyProperties(getReturnedCheque(), aReturnedCheque);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_ReturnedChequeDialog_CustCIF.value")+" : "+aReturnedCheque.getCustCIF()+","+
				Labels.getLabel("label_ReturnedChequeDialog_ChequeNo.value")+" : "+aReturnedCheque.getChequeNo();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aReturnedCheque.getRecordType())) {
				aReturnedCheque.setVersion(aReturnedCheque.getVersion() + 1);
				aReturnedCheque.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aReturnedCheque.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aReturnedCheque, tranType)) {
					refreshList();
					closeDialog();
				}

			}  catch (Exception e) {
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

		if (getReturnedCheque().isNewRecord()) {
			this.custCIF.setReadonly(false);
			this.chequeNo.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custCIF.setReadonly(true);
			this.chequeNo.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.currency.setReadonly(isReadOnly("ReturnedChequeDialog_currency"));
		this.returnDate.setDisabled(isReadOnly("ReturnedChequeDialog_returnDate"));
		this.returnReason.setReadonly(isReadOnly("ReturnedChequeDialog_returnReason"));
		this.amount.setReadonly(isReadOnly("ReturnedChequeDialog_amount"));

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.returnedCheque.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	private void doReadOnly() {
		logger.debug("Entering");

		this.custCIF.setReadonly(true);
		this.amount.setReadonly(true);
		this.chequeNo.setReadonly(true);
		this.currency.setReadonly(true);
		this.returnDate.setDisabled(true);
		this.returnReason.setReadonly(true);

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
		logger.debug("Entering ");
		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.chequeNo.setValue("");
		this.amount.setValue("");
		this.returnReason.setValue("");
		this.returnDate.setText("");
		this.currency.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final ReturnedChequeDetails aReturnedCheque = new ReturnedChequeDetails();
		BeanUtils.copyProperties(getReturnedCheque(), aReturnedCheque);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ReturnedCheque object with the components data
		doWriteComponentsToBean(aReturnedCheque);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aReturnedCheque.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReturnedCheque.getRecordType())) {
				aReturnedCheque.setVersion(aReturnedCheque.getVersion() + 1);
				if (isNew) {
					aReturnedCheque
					.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReturnedCheque
					.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReturnedCheque.setNewRecord(true);
				}
			}
		} else {
			aReturnedCheque.setVersion(aReturnedCheque.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aReturnedCheque, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		}  catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReturnedcheque
	 *            (Returnedcheque)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ReturnedChequeDetails aReturnedCheque,
			String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReturnedCheque.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aReturnedCheque.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReturnedCheque
		.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReturnedCheque.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReturnedCheque
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReturnedCheque);
				}

				if (isNotesMandatory(taskId, aReturnedCheque)) {
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

			aReturnedCheque.setTaskId(taskId);
			aReturnedCheque.setNextTaskId(nextTaskId);
			aReturnedCheque.setRoleCode(getRole());
			aReturnedCheque.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aReturnedCheque, tranType);

			String operationRefs = getServiceOperations(taskId, aReturnedCheque);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReturnedCheque,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aReturnedCheque, tranType);
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReturnedChequeDetails aRetunedCheque = (ReturnedChequeDetails) auditHeader
				.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getReturnedChequeService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getReturnedChequeService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getReturnedChequeService().doApprove(
								auditHeader);

						if (aRetunedCheque.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReturnedChequeService().doReject(
								auditHeader);

						if (aRetunedCheque.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_ReturnedChequeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_ReturnedChequeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.returnedCheque), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aReturnedcheque
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReturnedChequeDetails aReturnedCheque,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aReturnedCheque.getBefImage(), aReturnedCheque);
		return new AuditHeader(getReference(), null, null, null, auditDetail,
				aReturnedCheque.getUserDetails(), getOverideMap());
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
		doShowNotes(this.returnedCheque);
	}

	

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getReturnedCheque().getCustCIF()
				+ PennantConstants.KEY_SEPERATOR
				+ getReturnedCheque().getChequeNo();
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReturnedChequeDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReturnedChequeService getReturnedChequeService() {
		return returnedChequeService;
	}

	public void setReturnedChequeService(
			ReturnedChequeService returnedChequeService) {
		this.returnedChequeService = returnedChequeService;
	}

	public ReturnedChequeDetails getReturnedCheque() {
		return returnedCheque;
	}

	public void setReturnedCheque(ReturnedChequeDetails returnedCheque) {
		this.returnedCheque = returnedCheque;
	}

	public ReturnedChequeListCtrl getReturnedChequeListCtrl() {
		return returnedChequeListCtrl;
	}

	public void setReturnedChequeListCtrl(
			ReturnedChequeListCtrl returnedChequeListCtrl) {
		this.returnedChequeListCtrl = returnedChequeListCtrl;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
}
