package com.pennant.webui.ocrmaster.ocrheader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class OCRDetailDialogCtrl extends GFCBaseCtrl<OCRDetail> {
	private static final Logger logger = LogManager.getLogger(OCRDetailDialogCtrl.class);

	private static final long serialVersionUID = -6708644161007723783L;

	protected Window window_OCRDetailDialog;
	protected Intbox stepSequence;
	protected Combobox contributor;
	protected Intbox customerContribution;
	protected Intbox financerContribution;
	protected Label label_OCRDetailDialog_Contributor;
	protected Space spaceCustContribution;
	protected Space spaceFinContribution;
	protected Label label_OCRDetailDialog_CustomerContribution;
	protected Label label_OCRDetailDialog_FinancerContribution;

	private OCRDetail ocrDetail;
	private OCRHeader ocrHeader;
	private List<OCRDetail> ocrDetailList;
	private boolean newRecord = false;
	private boolean newStep = false;
	private String userRole = "";
	private boolean isFromParent = false;

	// ServiceDAOs / Domain Classes
	private transient OCRHeaderService ocrHeaderService;
	private OCRHeaderDialogCtrl ocrHeaderDialogCtrl;

	private transient boolean validationOn;

	private final List<ValueLabel> ocrContributorList = PennantStaticListUtil.getOCRContributorList();

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OCRDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected OCRDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OCRDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_OCRDetailDialog);

		try {
			if (arguments.containsKey("ocrHeaderDialogCtrl")) {
				this.ocrHeaderDialogCtrl = (OCRHeaderDialogCtrl) arguments.get("ocrHeaderDialogCtrl");
				setFromParent(true);
			}
			if (arguments.containsKey("ocrHeader")) {
				this.ocrHeader = (OCRHeader) arguments.get("ocrHeader");
				setOcrHeader(this.ocrHeader);
			}
			if (arguments.containsKey("ocrDetail")) {
				this.ocrDetail = (OCRDetail) arguments.get("ocrDetail");
			}

			if (this.ocrDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			this.ocrDetail.setWorkflowId(0);
			setNewRecord(this.ocrDetail.isNew());
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "OCRDetailDialog");
			}

			// Store the before image.
			OCRDetail ocrDetail = new OCRDetail();
			BeanUtils.copyProperties(this.ocrDetail, ocrDetail);
			this.ocrDetail.setBefImage(ocrDetail);
			// Render the page and display the data.
			doLoadWorkFlow(this.ocrDetail.isWorkflow(), this.ocrDetail.getWorkflowId(), this.ocrDetail.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "OCRDetailDialog");
			}
			// set Field Properties
			doSetFieldProperties();
			/* set components visible dependent of the users rights */
			doCheckRights();

			doShowDialog(this.ocrDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_OCRDetailDialog.onClose();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.stepSequence.setMaxlength(2);
		this.contributor.setMaxlength(30);
		this.customerContribution.setMaxlength(2);
		this.financerContribution.setMaxlength(2);
		this.customerContribution.setReadonly(true);
		this.financerContribution.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_OCRDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_OCRDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_OCRDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OCRDetailDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doEdit();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_OCRDetailDialog);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doDelete();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCancel();
		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.ocrDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aOCRDetail
	 * 
	 */
	public void doWriteBeanToComponents(OCRDetail aOCRDetail) {
		logger.debug(Literal.ENTERING);

		this.stepSequence.setValue(getNextStepSequence(aOCRDetail));
		this.customerContribution.setValue(aOCRDetail.getCustomerContribution());
		this.financerContribution.setValue(aOCRDetail.getFinancerContribution());
		fillComboBox(this.contributor, aOCRDetail.getContributor(), ocrContributorList, "");
		doSetContributorProperties();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOCRDetail
	 */
	public void doWriteComponentsToBean(OCRDetail aOCRDetail) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		//Step Sequence
		try {
			aOCRDetail.setStepSequence(this.stepSequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Contributor
		try {
			String strContributor = null;
			if (this.contributor.getSelectedItem() != null) {
				strContributor = this.contributor.getSelectedItem().getValue().toString();
			}
			if (strContributor != null && !PennantConstants.List_Select.equals(strContributor)) {
				aOCRDetail.setContributor(strContributor);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Customer Contribution
		try {
			aOCRDetail.setCustomerContribution(this.customerContribution.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Financier Contribution
		try {
			aOCRDetail.setFinancerContribution(this.financerContribution.getValue());
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

		setOcrDetail(aOCRDetail);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aOCRDetail
	 * 
	 * @throws Exception
	 */
	public void doShowDialog(OCRDetail aOCRDetail) throws Exception {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (aOCRDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isFromParent()) {
				if (enqiryModule) {
					doReadOnly();
					this.btnCtrl.setBtnStatus_Enquiry();
					this.btnNotes.setVisible(false);
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(aOCRDetail);
		this.window_OCRDetailDialog.setHeight("50%");
		this.window_OCRDetailDialog.setWidth("65%");
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);

		if (!this.stepSequence.isReadonly()) {
			this.stepSequence.setConstraint(new PTStringValidator(
					Labels.getLabel("label_OCRDetailDialog_StepSequence.value"), null, true, true));
		}
		if (!this.customerContribution.isReadonly()) {
			this.customerContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_OCRDetailDialog_CustomerContribution.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		} else {
			this.customerContribution.setConstraint("");
		}
		if (!this.financerContribution.isReadonly()) {
			this.financerContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_OCRDetailDialog_FinancierContribution.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		} else {
			this.financerContribution.setConstraint("");
		}
		//Applicable On
		if (!this.contributor.isDisabled() && this.label_OCRDetailDialog_Contributor.isVisible()) {
			this.contributor.setConstraint(new StaticListValidator(ocrContributorList,
					Labels.getLabel("label_OCRDetailDialog_Contributor.value")));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.stepSequence.setConstraint("");
		this.customerContribution.setConstraint("");
		this.financerContribution.setConstraint("");
		this.contributor.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.stepSequence.setErrorMessage("");
		this.customerContribution.setErrorMessage("");
		this.financerContribution.setErrorMessage("");
		this.contributor.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	// CRUD operations

	/**
	 * Deletes a OCRDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final OCRDetail aOCRDetail = new OCRDetail();
		BeanUtils.copyProperties(getOcrDetail(), aOCRDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_OCRDetailDialog_StepSequence.value") + " : " + aOCRDetail.getStepSequence();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aOCRDetail.getRecordType())) {
				aOCRDetail.setVersion(aOCRDetail.getVersion() + 1);
				aOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (getOcrHeaderDialogCtrl() != null) {
					aOCRDetail.setNewRecord(true);
				}
				if (isWorkFlowEnabled()) {
					aOCRDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newOCRDetailProcess(aOCRDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_OCRDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getOcrHeaderDialogCtrl() != null) {
						getOcrHeaderDialogCtrl().doFillOCRDetails(this.ocrDetailList);
					}
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		this.stepSequence.setReadonly(true);
		readOnlyComponent(isReadOnly("OCRDetailDialog_Contributor"), this.contributor);
		readOnlyComponent(isReadOnly("OCRDetailDialog_CustomerContribution"), this.customerContribution);
		readOnlyComponent(isReadOnly("OCRDetailDialog_FinancierContribution"), this.financerContribution);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.stepSequence.setReadonly(true);
		this.customerContribution.setReadonly(true);
		this.financerContribution.setReadonly(true);
		this.contributor.setDisabled(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// remove validation, if there are a save before
		this.contributor.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final OCRDetail aOCRDetail = new OCRDetail();
		BeanUtils.copyProperties(this.ocrDetail, aOCRDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the OCRDetail object with the components data
		doWriteComponentsToBean(aOCRDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aOCRDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aOCRDetail.getRecordType())) {
				aOCRDetail.setVersion(aOCRDetail.getVersion() + 1);
				aOCRDetail.setNewRecord(true);
				if (isNew) {
					aOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOCRDetail.setNewRecord(true);
				}
			}
		} else {
			aOCRDetail.setVersion(aOCRDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aOCRDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aOCRDetail.getRecordType())) {
				aOCRDetail.setVersion(aOCRDetail.getVersion() + 1);
				aOCRDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if ((PennantConstants.RCD_ADD).equals(aOCRDetail.getRecordType()) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_NEW.equals(aOCRDetail.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newOCRDetailProcess(aOCRDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_OCRDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getOcrHeaderDialogCtrl().doFillOCRDetails(this.ocrDetailList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private AuditHeader newOCRDetailProcess(OCRDetail aOcrDetail, String tranType) {

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aOcrDetail, tranType);
		ocrDetailList = new ArrayList<OCRDetail>();

		if (CollectionUtils.isNotEmpty(getOcrHeaderDialogCtrl().getOcrDetailList())) {
			if (!PennantConstants.TRAN_DEL.equals(tranType)) {
				auditHeader = validateOCRSteps(getOcrHeaderDialogCtrl().getOcrDetailList(), aOcrDetail, auditHeader);
			}
			if (!CollectionUtils.isEmpty(auditHeader.getErrorMessage())) {
				return auditHeader;
			}
			for (OCRDetail ocrDetail : getOcrHeaderDialogCtrl().getOcrDetailList()) {
				// Both Current and Existing list Sequence is same
				if (ocrDetail.getStepSequence() == aOcrDetail.getStepSequence()) {

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aOcrDetail.getRecordType())) {
							aOcrDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							ocrDetailList.add(aOcrDetail);
						} else if (PennantConstants.RCD_ADD.equals(aOcrDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aOcrDetail.getRecordType())) {
							aOcrDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							ocrDetailList.add(aOcrDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aOcrDetail.getRecordType())) {
							recordAdded = true;
						}
					} else if (!PennantConstants.TRAN_UPD.equals(tranType)) {
						ocrDetailList.add(ocrDetail);
					}
				} else {
					ocrDetailList.add(ocrDetail);
				}
			}
		} else if (!PennantConstants.TRAN_DEL.equals(tranType)) {
			auditHeader = validateOCRSteps(null, aOcrDetail, auditHeader);
		}
		if (!recordAdded) {
			ocrDetailList.add(aOcrDetail);
		}
		return auditHeader;

	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aOCRDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(OCRDetail aOCRDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aOCRDetail.getBefImage(), aOCRDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aOCRDetail.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_OCRDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}
		logger.debug(Literal.LEAVING);
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
		doShowNotes(this.ocrDetail);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getOcrDetail().getStepSequence() + PennantConstants.KEY_SEPERATOR + getOcrDetail().getContributor();
	}

	/**
	 * On Select event for Contributor for setting Conditional Mandatory
	 */
	public void onSelect$contributor(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		doSetContributorProperties();
		doRemoveValidation();
		logger.debug(Literal.LEAVING);
	}

	private void doSetContributorProperties() {
		if (getComboboxValue(this.contributor).equals(PennantConstants.List_Select)) {
			this.customerContribution.setConstraint("");
			this.financerContribution.setConstraint("");
			this.customerContribution.setErrorMessage("");
			this.financerContribution.setErrorMessage("");
			this.spaceCustContribution.setSclass("");
			this.spaceFinContribution.setSclass("");
			this.customerContribution.setReadonly(true);
			this.financerContribution.setReadonly(true);
			this.customerContribution.setValue(0);
			this.financerContribution.setValue(0);
		} else if (getComboboxValue(this.contributor).equals(PennantConstants.CUSTOMER_CONTRIBUTION)) {
			this.financerContribution.setConstraint("");
			this.financerContribution.setErrorMessage("");
			this.financerContribution.clearErrorMessage();
			this.spaceFinContribution.setSclass("");
			this.spaceCustContribution.setSclass(PennantConstants.mandateSclass);
			this.financerContribution.setReadonly(true);
			this.customerContribution.setReadonly(isReadOnly("OCRDetailDialog_CustomerContribution"));
			this.financerContribution.setValue(0);
		} else {
			this.customerContribution.setConstraint("");
			this.customerContribution.setErrorMessage("");
			this.customerContribution.clearErrorMessage(true);
			this.spaceCustContribution.setSclass("");
			this.spaceFinContribution.setSclass(PennantConstants.mandateSclass);
			this.customerContribution.setReadonly(true);
			this.financerContribution.setReadonly(isReadOnly("OCRDetailDialog_FinancierContribution"));
			this.customerContribution.setValue(0);
		}
	}

	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		} else {
			return getUserWorkspace().isReadOnly(componentName);
		}
	}

	/**
	 * Generating Step Sequence
	 * 
	 * @param OCRDetail
	 * @return
	 */
	private int getNextStepSequence(OCRDetail ocrDetail) {
		int sequence = 0;
		if (ocrDetail.getStepSequence() > 0) {
			return ocrDetail.getStepSequence();
		}
		List<OCRDetail> list = getOcrHeaderDialogCtrl().getOcrDetailList();
		if (!CollectionUtils.isEmpty(list)) {
			for (OCRDetail ocrDetail2 : list) {
				if (ocrDetail2.getStepSequence() > 0) {
					sequence = ocrDetail2.getStepSequence();
				}
			}
		}

		return sequence + 1;
	}

	/**
	 * Validating OCR Step Details
	 * 
	 * @param ocrDetailList
	 * @param aOCRDetail
	 * @param auditHeader
	 * @return
	 */
	private AuditHeader validateOCRSteps(List<OCRDetail> ocrDetailList, OCRDetail aOCRDetail, AuditHeader auditHeader) {
		int customerPortion = 0;
		int financerPortion = 0;
		int totalExtCustomer = 0;
		int totalExtFinancer = 0;
		String message = "Total ";
		String[] valueParm = new String[2];

		// Header contribution splitting 
		if (ocrHeader != null) {
			//100 – value entered at header section against field "Customer Portion (%)".
			financerPortion = 100 - getOcrHeader().getCustomerPortion();
			customerPortion = getOcrHeader().getCustomerPortion();
		}
		if (!CollectionUtils.isEmpty(ocrDetailList)) {
			for (OCRDetail ocrDetail : ocrDetailList) {
				if (PennantConstants.RCD_DEL.equalsIgnoreCase(ocrDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrDetail.getRecordType())) {
					continue;
				}
				if (aOCRDetail.getStepSequence() != ocrDetail.getStepSequence()) {
					totalExtCustomer += ocrDetail.getCustomerContribution();
					totalExtFinancer += ocrDetail.getFinancerContribution();
				}
			}
		}

		totalExtCustomer += aOCRDetail.getCustomerContribution();
		totalExtFinancer += aOCRDetail.getFinancerContribution();

		if (totalExtCustomer > customerPortion
				&& PennantConstants.CUSTOMER_CONTRIBUTION.equals(aOCRDetail.getContributor())) {
			valueParm[0] = message.concat(Labels.getLabel("label_OCRDetailDialog_CustomerContribution.value"));
			valueParm[1] = String.valueOf(customerPortion);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			return auditHeader;
		} else if (totalExtFinancer > financerPortion
				&& PennantConstants.FINANCER_CONTRIBUTION.equals(aOCRDetail.getContributor())) {
			valueParm[0] = message.concat(Labels.getLabel("label_OCRDetailDialog_FinancierContribution.value"));
			valueParm[1] = String.valueOf(financerPortion);
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			return auditHeader;
		}
		return auditHeader;
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

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

	public OCRHeaderService getOcrHeaderService() {
		return this.ocrHeaderService;
	}

	public OCRDetail getOcrDetail() {
		return ocrDetail;
	}

	public void setOcrDetail(OCRDetail ocrDetail) {
		this.ocrDetail = ocrDetail;
	}

	public OCRHeaderDialogCtrl getOcrHeaderDialogCtrl() {
		return ocrHeaderDialogCtrl;
	}

	public void setOcrHeaderDialogCtrl(OCRHeaderDialogCtrl ocrHeaderDialogCtrl) {
		this.ocrHeaderDialogCtrl = ocrHeaderDialogCtrl;
	}

	public OCRHeader getOcrHeader() {
		return ocrHeader;
	}

	public void setOcrHeader(OCRHeader ocrHeader) {
		this.ocrHeader = ocrHeader;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewStep() {
		return newStep;
	}

	public void setNewStep(boolean newStep) {
		this.newStep = newStep;
	}

	public boolean isFromParent() {
		return isFromParent;
	}

	public void setFromParent(boolean isFromParent) {
		this.isFromParent = isFromParent;
	}

}